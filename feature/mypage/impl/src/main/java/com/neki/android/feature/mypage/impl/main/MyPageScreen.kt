package com.neki.android.feature.mypage.impl.main

import android.content.Intent
import android.net.Uri
import android.os.Build
import androidx.activity.compose.LocalActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.google.android.gms.oss.licenses.OssLicensesMenuActivity
import com.neki.android.core.common.permission.NekiPermission
import com.neki.android.core.common.permission.NotificationPermissionManager
import com.neki.android.core.designsystem.ComponentPreview
import com.neki.android.core.designsystem.dialog.DoubleButtonAlertDialog
import com.neki.android.core.designsystem.ui.theme.NekiTheme
import com.neki.android.core.ui.component.LoadingDialog
import com.neki.android.core.ui.compose.collectWithLifecycle
import com.neki.android.core.ui.compose.launchAppSettings
import com.neki.android.core.ui.compose.rememberAppSettingsLauncher
import com.neki.android.feature.mypage.impl.component.SectionArrowItem
import com.neki.android.feature.mypage.impl.component.SectionTitleText
import com.neki.android.feature.mypage.impl.component.SectionVersionItem
import com.neki.android.feature.mypage.impl.main.component.MyPageMainTopBar
import com.neki.android.feature.mypage.impl.main.component.ProfileCard
import com.neki.android.feature.mypage.impl.main.const.ServiceInfoMenu

@Composable
internal fun MyPageRoute(
    viewModel: MyPageViewModel = hiltViewModel(),
    navigateToNotification: () -> Unit = {},
    navigateToPermission: () -> Unit,
    navigateToProfile: () -> Unit,
) {
    val context = LocalContext.current
    val activity = LocalActivity.current!!
    val uiState by viewModel.store.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        val appVersion = context.packageManager.getPackageInfo(context.packageName, 0).versionName ?: ""
        viewModel.store.onIntent(MyPageIntent.SetAppVersion(appVersion))
    }

    val notificationPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
    ) { isGranted ->
        if (isGranted) {
            viewModel.store.onIntent(MyPageIntent.GrantNotificationPermission)
        } else if (!NotificationPermissionManager.shouldShowNotificationRationale(activity)) {
            viewModel.store.onIntent(MyPageIntent.DenyNotificationPermissionPermanent)
        }
    }

    val notificationAppSettingsLauncher = rememberAppSettingsLauncher {
        if (NotificationPermissionManager.isGrantedNotificationPermission(context)) {
            navigateToNotification()
        }
    }

    viewModel.store.sideEffects.collectWithLifecycle { effect ->
        when (effect) {
            MyPageEffect.NavigateToNotification -> navigateToNotification()
            MyPageEffect.RequestNotificationPermission -> {
                if (NotificationPermissionManager.isGrantedNotificationPermission(context)) {
                    navigateToNotification()
                } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    notificationPermissionLauncher.launch(NotificationPermissionManager.NOTIFICATION_PERMISSION)
                } else {
                    viewModel.store.onIntent(MyPageIntent.DenyNotificationPermissionPermanent)
                }
            }
            MyPageEffect.MoveAppSettingsForNotification ->
                notificationAppSettingsLauncher.launchAppSettings(context, NekiPermission.NOTIFICATION)
            MyPageEffect.NavigateToProfile -> navigateToProfile()
            MyPageEffect.NavigateToPermission -> navigateToPermission()
            is MyPageEffect.OpenExternalLink -> context.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(effect.url)))
            MyPageEffect.OpenOssLicenses -> {
                OssLicensesMenuActivity.setActivityTitle("오픈소스 라이선스 목록")
                context.startActivity(Intent(context, OssLicensesMenuActivity::class.java))
            }

            else -> {}
        }
    }

    MyPageScreen(
        uiState = uiState,
        onIntent = viewModel.store::onIntent,
    )
}

@Composable
fun MyPageScreen(
    uiState: MyPageState,
    onIntent: (MyPageIntent) -> Unit,
) {
    Column(
        modifier = Modifier.fillMaxSize(),
    ) {
        MyPageMainTopBar(
            onClickNotificationIcon = { onIntent(MyPageIntent.ClickNotificationIcon) },
        )
        ProfileCard(
            profileImageUrl = uiState.userInfo.profileImageUrl,
            name = uiState.userInfo.nickname,
            loginType = uiState.userInfo.loginType,
            onClickCard = { onIntent(MyPageIntent.ClickProfileCard) },
        )
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(11.dp)
                .background(color = NekiTheme.colorScheme.gray25),
        )
        Column {
            SectionTitleText(text = "권한")
            SectionArrowItem(
                text = "권한 설정하기",
                onClick = { onIntent(MyPageIntent.ClickPermission) },
            )
        }
        Column {
            SectionTitleText(text = "서비스 정보 및 지원")
            ServiceInfoMenu.entries.forEach { menu ->
                SectionArrowItem(
                    text = menu.text,
                    onClick = { onIntent(MyPageIntent.ClickServiceInfoMenu(menu)) },
                )
            }
            SectionArrowItem(
                text = "오픈소스 라이선스",
                onClick = { onIntent(MyPageIntent.ClickOpenSourceLicense) },
            )
            SectionVersionItem(uiState.appVersion)
        }
    }

    if (uiState.isLoading) {
        LoadingDialog()
    }

    if (uiState.showNotificationPermissionDeniedDialog) {
        DoubleButtonAlertDialog(
            title = NekiPermission.NOTIFICATION_PERMANENT_DENIED_DIALOG_TITLE,
            content = NekiPermission.NOTIFICATION_PERMANENT_DENIED_DIALOG_CONTENT,
            grayButtonText = "나중에",
            primaryButtonText = "설정으로 이동",
            onDismissRequest = { onIntent(MyPageIntent.DismissNotificationPermissionDialog) },
            onClickGrayButton = { onIntent(MyPageIntent.DismissNotificationPermissionDialog) },
            onClickPrimaryButton = { onIntent(MyPageIntent.ClickMoveToNotificationAppSettings) },
        )
    }
}

@ComponentPreview
@Composable
private fun MyPageScreenPreview() {
    NekiTheme {
        MyPageScreen(
            uiState = MyPageState(appVersion = "1.1.0"),
            onIntent = {},
        )
    }
}
