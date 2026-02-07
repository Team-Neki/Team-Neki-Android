package com.neki.android.feature.auth.impl.onboarding

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.neki.android.core.designsystem.ComponentPreview
import com.neki.android.core.designsystem.button.CTAButtonPrimary
import com.neki.android.core.designsystem.ui.theme.NekiTheme
import com.neki.android.feature.auth.impl.onboarding.component.OnboardingPage
import com.neki.android.feature.auth.impl.onboarding.component.OnboardingPageContent
import com.neki.android.feature.auth.impl.onboarding.component.TextSection
import com.neki.android.feature.auth.impl.onboarding.component.PagerIndicator

@Composable
internal fun OnboardingRoute(
    navigateToLogin: () -> Unit,
) {
    OnboardingScreen(
        onClickLoginButton = navigateToLogin,
    )
}

@Composable
internal fun OnboardingScreen(
    onClickLoginButton: () -> Unit = {},
) {
    val pages = OnboardingPage.entries
    val pagerState = rememberPagerState { pages.size }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(color = NekiTheme.colorScheme.white)
    ) {
        HorizontalPager(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .padding(bottom = 38.dp),
            state = pagerState,
        ) { page ->
            Column {
                TextSection(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 104.dp, start = 32.dp, bottom = 28.dp),
                    page = pages[page],
                )
                OnboardingPageContent(imageRes = pages[page].imageRes)
            }
        }

        PagerIndicator(
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
                .padding(bottom = 38.dp),
            pageCount = pages.size,
            currentPage = pagerState.currentPage,
        )

        CTAButtonPrimary(
            modifier = Modifier
                .padding(horizontal = 20.dp, vertical = 28.dp)
                .fillMaxWidth(),
            text = "회원가입 및 로그인",
            onClick = onClickLoginButton
        )
    }
}

@ComponentPreview
@Composable
private fun OnboardingScreenPreview() {
    NekiTheme {
        OnboardingScreen()
    }
}
