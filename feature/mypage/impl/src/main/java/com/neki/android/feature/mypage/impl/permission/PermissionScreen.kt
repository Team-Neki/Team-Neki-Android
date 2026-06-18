package com.neki.android.feature.mypage.impl.permission

import android.os.Build
import androidx.activity.compose.LocalActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.LifecycleResumeEffect
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.neki.android.core.common.permission.CameraPermissionManager
import com.neki.android.core.common.permission.LocationPermissionManager
import com.neki.android.core.common.permission.NotificationPermissionManager
import com.neki.android.core.common.permission.NekiPermission
import com.neki.android.core.common.permission.navigateToAppSettings
import com.neki.android.core.designsystem.ComponentPreview
import com.neki.android.core.designsystem.R
import com.neki.android.core.designsystem.dialog.DoubleButtonAlertDialog
import com.neki.android.core.designsystem.topbar.BackTitleTopBar
import com.neki.android.core.designsystem.toggle.NekiToggle
import com.neki.android.core.designsystem.ui.theme.NekiTheme
import com.neki.android.core.ui.compose.collectWithLifecycle
import com.neki.android.core.ui.compose.VerticalSpacer
import com.neki.android.core.ui.component.LoadingDialog
import com.neki.android.core.ui.toast.NekiToast
import com.neki.android.feature.mypage.impl.component.PermissionSectionItem
import com.neki.android.feature.mypage.impl.component.SectionTitleText

@Composable
internal fun PermissionRoute(
    viewModel: PermissionViewModel = hiltViewModel(),
    navigateBack: () -> Unit = {},
) {
    val uiState by viewModel.store.uiState.collectAsStateWithLifecycle()
    val activity = LocalActivity.current!!
    val context = LocalContext.current
    val nekiToast = remember { NekiToast(context) }

    fun checkPermissions() {
        NekiPermission.entries.forEach { permission ->
            viewModel.store.onIntent(
                PermissionIntent.UpdatePermissionState(
                    permission = permission,
                    isGranted = when (permission) {
                        NekiPermission.CAMERA -> CameraPermissionManager.isGrantedCameraPermission(context)
                        NekiPermission.LOCATION -> LocationPermissionManager.isGrantedLocationPermission(context)
                        NekiPermission.NOTIFICATION -> NotificationPermissionManager.isGrantedNotificationPermission(context)
                    },
                ),
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
        viewModel.store.onIntent(PermissionIntent.UpdatePermissionState(permission, isGranted))

        if (!isGranted) {
            val shouldShowRationale = when (permission) {
                NekiPermission.CAMERA -> CameraPermissionManager.shouldShowCameraRationale(activity)
                NekiPermission.LOCATION -> LocationPermissionManager.shouldShowLocationRationale(activity)
                NekiPermission.NOTIFICATION -> NotificationPermissionManager.shouldShowNotificationRationale(activity)
            }
            if (!shouldShowRationale) {
                viewModel.store.onIntent(PermissionIntent.ShowPermissionDeniedDialog(permission))
            }
        }
    }

    viewModel.store.sideEffects.collectWithLifecycle { sideEffect ->
        when (sideEffect) {
            PermissionEffect.NavigateBack -> navigateBack()
            is PermissionEffect.RequestPermission -> {
                when (sideEffect.permission) {
                    NekiPermission.CAMERA -> permissionLauncher.launch(arrayOf(CameraPermissionManager.CAMERA_PERMISSION))
                    NekiPermission.LOCATION -> permissionLauncher.launch(LocationPermissionManager.LOCATION_PERMISSIONS)
                    NekiPermission.NOTIFICATION -> {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                            permissionLauncher.launch(arrayOf(NotificationPermissionManager.NOTIFICATION_PERMISSION))
                        } else {
                            if (!NotificationPermissionManager.isGrantedNotificationPermission(context)) {
                                viewModel.store.onIntent(PermissionIntent.ShowPermissionDeniedDialog(NekiPermission.NOTIFICATION))
                            }
                        }
                    }
                }
            }
            is PermissionEffect.MoveAppSettings -> navigateToAppSettings(context)
            is PermissionEffect.ShowToast -> nekiToast.showToast(text = sideEffect.message)
        }
    }

    PermissionScreen(
        uiState = uiState,
        onIntent = viewModel.store::onIntent,
    )
}

@Composable
fun PermissionScreen(
    uiState: PermissionState = PermissionState(),
    onIntent: (PermissionIntent) -> Unit = {},
) {
    Column(
        modifier = Modifier.fillMaxSize(),
    ) {
        BackTitleTopBar(
            title = "기기 권한 및 알림",
            onBack = { onIntent(PermissionIntent.ClickBackIcon) },
        )
        SectionTitleText(text = "권한 설정")
        NekiPermission.entries.forEach { permission ->
            val isGranted = when (permission) {
                NekiPermission.CAMERA -> uiState.isGrantedCamera
                NekiPermission.LOCATION -> uiState.isGrantedLocation
                NekiPermission.NOTIFICATION -> uiState.isGrantedNotification
            }
            PermissionSectionItem(
                type = permission.type,
                subTitle = permission.subTitle,
                onClick = { onIntent(PermissionIntent.ClickPermissionItem(permission)) },
                trailingContent = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = if (isGranted) "허용됨" else "허용안됨",
                            color = NekiTheme.colorScheme.gray500,
                            style = NekiTheme.typography.body14Medium,
                        )
                        Icon(
                            modifier = Modifier.size(16.dp),
                            imageVector = ImageVector.vectorResource(R.drawable.icon_arrow_right),
                            tint = NekiTheme.colorScheme.gray300,
                            contentDescription = null,
                        )
                    }
                },
            )
        }
        VerticalSpacer(12.dp)
        SectionTitleText(text = "알람 설정")
        PermissionSectionItem(
            type = "혜택·소식 알림",
            subTitle = "이벤트, 혜택, 신규 기능 소식 등을 알려드려요.",
            trailingContent = {
                NekiToggle(
                    checked = uiState.isMarketingTermAgreed,
                    onCheckedChange = { onIntent(PermissionIntent.ToggleMarketingNotification) },
                )
            },
        )
    }

    if (uiState.isLoading) {
        LoadingDialog()
    }

    if (uiState.isShowPermissionDialog && uiState.clickedPermission != null) {
        DoubleButtonAlertDialog(
            title = uiState.clickedPermission.dialogTitle,
            content = uiState.clickedPermission.dialogContent,
            grayButtonText = "취소",
            primaryButtonText = "허용",
            onDismissRequest = { onIntent(PermissionIntent.DismissPermissionDialog) },
            onClickGrayButton = { onIntent(PermissionIntent.DismissPermissionDialog) },
            onClickPrimaryButton = { onIntent(PermissionIntent.ConfirmPermissionDialog) },
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
