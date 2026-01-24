package com.neki.android.feature.mypage.impl.main

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.neki.android.core.designsystem.ComponentPreview
import com.neki.android.core.designsystem.ui.theme.NekiTheme
import com.neki.android.core.ui.compose.collectWithLifecycle
import com.neki.android.feature.mypage.impl.component.SectionArrowItem
import com.neki.android.feature.mypage.impl.component.SectionTitleText
import com.neki.android.feature.mypage.impl.component.SectionVersionItem
import com.neki.android.feature.mypage.impl.main.component.MainTopBar
import com.neki.android.feature.mypage.impl.main.component.ProfileCard

@Composable
internal fun MyPageRoute(
    viewModel: MyPageViewModel = hiltViewModel(),
    navigateToPermission: () -> Unit,
    navigateToProfile: () -> Unit,
) {
    val uiState by viewModel.store.uiState.collectAsStateWithLifecycle()

    viewModel.store.sideEffects.collectWithLifecycle { effect ->
        when (effect) {
            MyPageEffect.NavigateToNotification -> {}
            MyPageEffect.NavigateToProfile -> navigateToProfile()
            MyPageEffect.NavigateToPermission -> navigateToPermission()
            MyPageEffect.NavigateToInquiry -> {}
            MyPageEffect.NavigateToTermsOfService -> {}
            MyPageEffect.NavigateToPrivacyPolicy -> {}
            MyPageEffect.NavigateToOpenSourceLicense -> {}
            MyPageEffect.NavigateBack -> {}
            MyPageEffect.NavigateToLogin -> {}
            is MyPageEffect.MoveAppSettings -> {}
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
        modifier = Modifier
            .fillMaxSize(),
    ) {
        MainTopBar(
            modifier = Modifier.padding(start = 20.dp, end = 8.dp),
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
            SectionTitleText(text = "권한 설정")
            SectionArrowItem(
                text = "기기 권한",
                onClick = { onIntent(MyPageIntent.ClickPermission) },
            )
        }
        Column {
            SectionTitleText(text = "서비스 정보 및 지원")
            SectionArrowItem(
                text = "Neki에 문의하기",
                onClick = { onIntent(MyPageIntent.ClickInquiry) },
            )
            SectionArrowItem(
                text = "이용약관",
                onClick = { onIntent(MyPageIntent.ClickTermsOfService) },
            )
            SectionArrowItem(
                text = "개인정보 처리방침",
                onClick = { onIntent(MyPageIntent.ClickPrivacyPolicy) },
            )
            SectionArrowItem(
                text = "오픈소스 라이선스",
                onClick = { onIntent(MyPageIntent.ClickOpenSourceLicense) },
            )
            SectionVersionItem(uiState.appVersion)
        }
    }
}

@ComponentPreview
@Composable
private fun MyPageScreenPreview() {
    NekiTheme {
        MyPageScreen(
            uiState = MyPageState(),
            onIntent = {},
        )
    }
}
