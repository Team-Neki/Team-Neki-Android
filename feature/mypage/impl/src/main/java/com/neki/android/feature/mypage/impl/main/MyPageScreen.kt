package com.neki.android.feature.mypage.impl.main

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.google.android.gms.oss.licenses.OssLicensesMenuActivity
import com.neki.android.core.designsystem.ComponentPreview
import com.neki.android.core.designsystem.ui.theme.NekiTheme
import com.neki.android.core.ui.compose.collectWithLifecycle
import com.neki.android.feature.mypage.impl.component.SectionArrowItem
import com.neki.android.feature.mypage.impl.component.SectionTitleText
import com.neki.android.feature.mypage.impl.component.SectionVersionItem
import com.neki.android.feature.mypage.impl.main.const.ServiceInfoMenu
import com.neki.android.feature.mypage.impl.main.component.MainTopBar
import com.neki.android.feature.mypage.impl.main.component.ProfileCard

@Composable
internal fun MyPageRoute(
    viewModel: MyPageViewModel = hiltViewModel(),
    navigateToPermission: () -> Unit,
    navigateToProfile: () -> Unit,
) {
    val context = LocalContext.current
    val uiState by viewModel.store.uiState.collectAsStateWithLifecycle()

    val appVersion = remember {
        "${context.packageManager.getPackageInfo(context.packageName, 0).versionName}"
    }

    viewModel.store.sideEffects.collectWithLifecycle { effect ->
        when (effect) {
            MyPageEffect.NavigateBack -> {}
            MyPageEffect.NavigateToLogin -> {}
            MyPageEffect.NavigateToNotification -> {}
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
        appVersion = appVersion,
        onIntent = viewModel.store::onIntent,
    )
}

@Composable
fun MyPageScreen(
    uiState: MyPageState,
    appVersion: String= "",
    onIntent: (MyPageIntent) -> Unit,
) {
    Column(
        modifier = Modifier.fillMaxSize(),
    ) {
        MainTopBar(
            onClickIcon = { onIntent(MyPageIntent.ClickNotificationIcon) },
        )
        ProfileCard(
            name = "오종석",
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
            SectionVersionItem(appVersion)
        }
    }
}

@ComponentPreview
@Composable
private fun MyPageScreenPreview() {
    NekiTheme {
        MyPageScreen(
            uiState = MyPageState(),
            appVersion = "1.1.0",
            onIntent = {},
        )
    }
}
