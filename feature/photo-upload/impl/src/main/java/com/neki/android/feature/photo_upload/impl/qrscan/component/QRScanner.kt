package com.neki.android.feature.photo_upload.impl.qrscan.component

import androidx.camera.compose.CameraXViewfinder
import androidx.camera.core.Camera
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.Preview
import androidx.camera.core.SurfaceRequest
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.lifecycle.awaitInstance
import androidx.camera.viewfinder.compose.MutableCoordinateTransformer
import androidx.camera.viewfinder.core.ImplementationMode
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
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.neki.android.feature.photo_upload.impl.qrscan.QRImageAnalyzer
import kotlinx.coroutines.flow.MutableStateFlow

@Composable
internal fun QRScanner(
    modifier: Modifier = Modifier,
    onQRCodeScanned: (String) -> Unit = {},
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    
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
        provider.bindToLifecycle(
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
                modifier = Modifier.fillMaxSize(),
            )
        }
    }
}
