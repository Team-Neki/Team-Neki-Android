package com.neki.android.feature.mypage.impl.permission

import android.Manifest
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.provider.Settings
import androidx.activity.compose.LocalActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.LifecycleResumeEffect
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.window.DialogProperties
import androidx.core.app.NotificationManagerCompat
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.neki.android.core.common.permission.CameraPermissionManager
import com.neki.android.core.common.permission.LocationPermissionManager
import com.neki.android.core.designsystem.ComponentPreview
import com.neki.android.core.designsystem.dialog.DoubleButtonAlertDialog
import com.neki.android.core.designsystem.topbar.BackTitleTopBar
import com.neki.android.core.designsystem.ui.theme.NekiTheme
import com.neki.android.core.ui.compose.collectWithLifecycle
import com.neki.android.feature.mypage.impl.component.PermissionSectionItem
import com.neki.android.feature.mypage.impl.component.SectionTitleText
import com.neki.android.feature.mypage.impl.main.MyPageEffect
import com.neki.android.feature.mypage.impl.main.MyPageIntent
import com.neki.android.feature.mypage.impl.main.MyPageState
import com.neki.android.feature.mypage.impl.main.MyPageViewModel
import com.neki.android.feature.mypage.impl.permission.const.NekiPermission
import timber.log.Timber

@Composable
internal fun PermissionRoute(
    viewModel: MyPageViewModel = hiltViewModel(),
    navigateBack: () -> Unit = {},
) {
    val uiState by viewModel.store.uiState.collectAsStateWithLifecycle()
    val activity = LocalActivity.current!!
    val context = LocalContext.current

    LifecycleResumeEffect(Unit) {
        val isCameraGranted = CameraPermissionManager.isGrantedCameraPermission(context)
        val isLocationGranted = LocationPermissionManager.isGrantedLocationPermission(context)
        val isNotificationGranted = NotificationManagerCompat.from(context).areNotificationsEnabled()

        if (isCameraGranted) {
            viewModel.store.onIntent(MyPageIntent.GrantedPermission(NekiPermission.CAMERA))
        }
        if (isLocationGranted) {
            viewModel.store.onIntent(MyPageIntent.GrantedPermission(NekiPermission.LOCATION))
        }
        if (isNotificationGranted) {
            viewModel.store.onIntent(MyPageIntent.GrantedPermission(NekiPermission.NOTIFICATION))
        }

        onPauseOrDispose { }
    }

    val cameraPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
    ) { isGranted ->
        if (isGranted) {
            viewModel.store.onIntent(MyPageIntent.GrantedPermission(NekiPermission.CAMERA))
        } else if (!CameraPermissionManager.shouldShowCameraRationale(activity)) {
            viewModel.store.onIntent(MyPageIntent.DeniedPermission(NekiPermission.CAMERA))
        }
    }

    val locationPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions(),
    ) { permissions ->
        val isGranted = permissions.values.any { it }
        if (isGranted) {
            viewModel.store.onIntent(MyPageIntent.GrantedPermission(NekiPermission.LOCATION))
        } else if (!LocationPermissionManager.shouldShowLocationRationale(activity)) {
            viewModel.store.onIntent(MyPageIntent.DeniedPermission(NekiPermission.LOCATION))
        }
    }

    val notificationPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
    ) { isGranted ->
        if (isGranted) {
            viewModel.store.onIntent(MyPageIntent.GrantedPermission(NekiPermission.NOTIFICATION))
        } else if (!activity.shouldShowRequestPermissionRationale(Manifest.permission.POST_NOTIFICATIONS)) {
            viewModel.store.onIntent(MyPageIntent.DeniedPermission(NekiPermission.LOCATION))
        }
    }

    viewModel.store.sideEffects.collectWithLifecycle { sideEffect ->
        when (sideEffect) {
            MyPageEffect.NavigateBack -> navigateBack()
            is MyPageEffect.RequestPermission -> {
                when (sideEffect.permission) {
                    NekiPermission.CAMERA -> cameraPermissionLauncher.launch(CameraPermissionManager.CAMERA_PERMISSION)
                    NekiPermission.LOCATION -> locationPermissionLauncher.launch(LocationPermissionManager.LOCATION_PERMISSIONS)
                    NekiPermission.NOTIFICATION -> {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                            notificationPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                        } else {
                            if (!NotificationManagerCompat.from(context).areNotificationsEnabled()) {
                                viewModel.store.onIntent(MyPageIntent.DeniedPermission(NekiPermission.NOTIFICATION))
                            }
                        }
                    }
                }
            }
            is MyPageEffect.MoveAppSettings -> {
                val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
                    data = Uri.fromParts("package", context.packageName, null)
                }
                context.startActivity(intent)
            }
            else -> {}
        }
    }

    PermissionScreen(
        uiState = uiState,
        onIntent = viewModel.store::onIntent,
    )
}

@Composable
fun PermissionScreen(
    uiState: MyPageState = MyPageState(),
    onIntent: (MyPageIntent) -> Unit = {},
) {
    Column(
        modifier = Modifier.fillMaxSize(),
    ) {
        BackTitleTopBar(
            title = "기기 권한",
            onBack = { onIntent(MyPageIntent.ClickBackIcon) },
        )
        SectionTitleText(text = "권한 설정")
        NekiPermission.entries.forEach { permission ->
            PermissionSectionItem(
                title = permission.title,
                subTitle = permission.subTitle,
                isGranted = when (permission) {
                    NekiPermission.CAMERA -> uiState.isGrantedCamera
                    NekiPermission.LOCATION -> uiState.isGrantedLocation
                    NekiPermission.NOTIFICATION -> uiState.isGrantedNotification
                },
                onClick = { onIntent(MyPageIntent.ClickPermissionItem(permission)) },
            )
        }
    }

    if (uiState.isShowPermissionDialog && uiState.clickedPermission != null) {
        DoubleButtonAlertDialog(
            title = uiState.clickedPermission.title,
            content = uiState.clickedPermission.dialogContent,
            grayButtonText = "취소",
            primaryButtonText = "허용",
            onDismissRequest = { onIntent(MyPageIntent.DismissPermissionDialog) },
            onClickGrayButton = { onIntent(MyPageIntent.DismissPermissionDialog) },
            onClickPrimaryButton = { onIntent(MyPageIntent.ConfirmPermissionDialog) },
        )
    }
}

@ComponentPreview
@Composable
private fun PermissionScreenPreview() {
    NekiTheme {
        PermissionScreen()
    }
}
