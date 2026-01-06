package com.neki.android.feature.photo_upload.impl.qrscan.component

import androidx.camera.compose.CameraXViewfinder
import androidx.camera.core.Camera
import androidx.camera.core.CameraSelector
import androidx.camera.core.FocusMeteringAction
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.Preview
import androidx.camera.core.SurfaceOrientedMeteringPointFactory
import androidx.camera.core.SurfaceRequest
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.lifecycle.awaitInstance
import androidx.camera.viewfinder.compose.MutableCoordinateTransformer
import androidx.camera.viewfinder.core.ImplementationMode
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.gestures.detectTransformGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.neki.android.core.designsystem.ui.theme.NekiTheme
import com.neki.android.feature.photo_upload.impl.qrscan.util.QRImageAnalyzer
import kotlinx.coroutines.flow.MutableStateFlow
import java.util.concurrent.TimeUnit

@Composable
internal fun QRScanner(
    modifier: Modifier = Modifier,
    onQRCodeScanned: (String) -> Unit = {},
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current

    var camera by remember { mutableStateOf<Camera?>(null) }

    val surfaceRequests = remember { MutableStateFlow<SurfaceRequest?>(null) }
    val surfaceRequest by surfaceRequests.collectAsState(initial = null)

    val coordinateTransformer = remember { MutableCoordinateTransformer() }

    LaunchedEffect(Unit) {
        val provider = ProcessCameraProvider.awaitInstance(context)
        val preview = Preview.Builder().build().apply {
            setSurfaceProvider { req -> surfaceRequests.value = req }
        }
        val imageAnalyzer = ImageAnalysis.Builder()
            .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
            .build().apply {
                setAnalyzer(
                    ContextCompat.getMainExecutor(context),
                    QRImageAnalyzer { scannedUrl ->
                        if (scannedUrl.isNotEmpty()) {
                            onQRCodeScanned(scannedUrl)
                        }
                    },
                )
            }

        provider.unbindAll()
        camera = provider.bindToLifecycle(
            lifecycleOwner = lifecycleOwner,
            cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA,
            preview,
            imageAnalyzer,
        )
    }

    Box(modifier = modifier) {
        surfaceRequest?.let { req ->
            CameraXViewfinder(
                surfaceRequest = req,
                implementationMode = ImplementationMode.EXTERNAL,
                coordinateTransformer = coordinateTransformer,
                modifier = Modifier
                    .fillMaxSize()
                    .pointerInput(camera) {
                        // Tap-to-focus
                        detectTapGestures { offset ->
                            val cam = camera ?: return@detectTapGestures

                            // Transform Compose coordinates to camera surface
                            val surfacePoint = with(coordinateTransformer) {
                                offset.transform()
                            }

                            val meteringFactory = SurfaceOrientedMeteringPointFactory(
                                req.resolution.width.toFloat(),
                                req.resolution.height.toFloat(),
                            )

                            val focusPoint = meteringFactory.createPoint(
                                surfacePoint.x,
                                surfacePoint.y,
                            )

                            val action = FocusMeteringAction.Builder(
                                focusPoint,
                                FocusMeteringAction.FLAG_AF or FocusMeteringAction.FLAG_AE,
                            ).setAutoCancelDuration(3, TimeUnit.SECONDS).build()

                            cam.cameraControl.startFocusAndMetering(action)
                        }
                    }
                    .pointerInput(camera) {
                        // Pinch-to-zoom
                        detectTransformGestures { _, _, zoom, _ ->
                            val cam = camera ?: return@detectTransformGestures
                            val zoomState = cam.cameraInfo.zoomState.value ?: return@detectTransformGestures

                            val newRatio = (zoomState.zoomRatio * zoom).coerceIn(
                                zoomState.minZoomRatio,
                                zoomState.maxZoomRatio,
                            )

                            cam.cameraControl.setZoomRatio(newRatio)
                        }
                    },
            )
        }
    }
}

@androidx.compose.ui.tooling.preview.Preview
@Composable
private fun QRScannerPreview() {
    NekiTheme {
        QRScanner()
    }
}
