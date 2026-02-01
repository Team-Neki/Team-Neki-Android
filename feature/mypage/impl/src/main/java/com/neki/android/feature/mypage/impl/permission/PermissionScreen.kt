package com.neki.android.feature.mypage.impl.permission

import android.os.Build
import androidx.activity.compose.LocalActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.LifecycleResumeEffect
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.neki.android.core.common.permission.CameraPermissionManager
import com.neki.android.core.common.permission.LocationPermissionManager
import com.neki.android.core.common.permission.NotificationPermissionManager
import com.neki.android.core.common.permission.navigateToAppSettings
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

@Composable
internal fun PermissionRoute(
    viewModel: MyPageViewModel = hiltViewModel(),
    navigateBack: () -> Unit = {},
) {
    val uiState by viewModel.store.uiState.collectAsStateWithLifecycle()
    val activity = LocalActivity.current!!
    val context = LocalContext.current

    fun checkPermissions() {
        NekiPermission.entries.forEach { permission ->
            viewModel.store.onIntent(
                MyPageIntent.UpdatePermissionState(
                    permission = permission,
                    isGranted = when (permission) {
                        NekiPermission.CAMERA -> CameraPermissionManager.isGrantedCameraPermission(context)
                        NekiPermission.LOCATION -> LocationPermissionManager.isGrantedLocationPermission(context)
                        NekiPermission.NOTIFICATION -> NotificationPermissionManager.isGrantedNotificationPermission(context)
                    }
                )
            )
        }
    }

    LaunchedEffect(Unit) {
        checkPermissions()
    }

    LifecycleResumeEffect(Unit) {
        checkPermissions()
        onPauseOrDispose {}
    }

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions(),
    ) { permissions ->
        val permission = when {
            permissions.containsKey(CameraPermissionManager.CAMERA_PERMISSION) -> NekiPermission.CAMERA
            LocationPermissionManager.LOCATION_PERMISSIONS.any { permissions.containsKey(it) } -> NekiPermission.LOCATION
            permissions.containsKey(NotificationPermissionManager.NOTIFICATION_PERMISSION) -> NekiPermission.NOTIFICATION
            else -> return@rememberLauncherForActivityResult
        }
        val isGranted = permissions.values.any { it }
        viewModel.store.onIntent(MyPageIntent.UpdatePermissionState(permission, isGranted))

        if (!isGranted) {
            val shouldShowRationale = when (permission) {
                NekiPermission.CAMERA -> CameraPermissionManager.shouldShowCameraRationale(activity)
                NekiPermission.LOCATION -> LocationPermissionManager.shouldShowLocationRationale(activity)
                NekiPermission.NOTIFICATION -> NotificationPermissionManager.shouldShowNotificationRationale(activity)
            }
            if (!shouldShowRationale) {
                viewModel.store.onIntent(MyPageIntent.ShowPermissionDeniedDialog(permission))
            }
        }
    }

    viewModel.store.sideEffects.collectWithLifecycle { sideEffect ->
        when (sideEffect) {
            MyPageEffect.NavigateBack -> navigateBack()
            is MyPageEffect.RequestPermission -> {
                when (sideEffect.permission) {
                    NekiPermission.CAMERA -> permissionLauncher.launch(arrayOf(CameraPermissionManager.CAMERA_PERMISSION))
                    NekiPermission.LOCATION -> permissionLauncher.launch(LocationPermissionManager.LOCATION_PERMISSIONS)
                    NekiPermission.NOTIFICATION -> {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                            permissionLauncher.launch(arrayOf(NotificationPermissionManager.NOTIFICATION_PERMISSION))
                        } else {
                            if (!NotificationPermissionManager.isGrantedNotificationPermission(context)) {
                                viewModel.store.onIntent(MyPageIntent.ShowPermissionDeniedDialog(NekiPermission.NOTIFICATION))
                            }
                        }
                    }
                }
            }
            is MyPageEffect.MoveAppSettings -> navigateToAppSettings(context)
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
