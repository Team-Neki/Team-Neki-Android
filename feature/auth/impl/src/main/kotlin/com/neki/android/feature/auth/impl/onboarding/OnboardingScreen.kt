package com.neki.android.feature.auth.impl.onboarding

import androidx.annotation.DrawableRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.neki.android.core.designsystem.R
import com.neki.android.core.designsystem.button.CTAButtonPrimary
import com.neki.android.core.designsystem.ui.theme.NekiTheme
import com.neki.android.core.ui.compose.VerticalSpacer

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
    val pagerState = rememberPagerState { onboardingPages.size }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(color = NekiTheme.colorScheme.white)
    ) {
        OnboardingTextSection(
            pagerState = pagerState,
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 104.dp, start = 32.dp)
        )
        VerticalSpacer(28.dp)
        HorizontalPager(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .padding(bottom = 38.dp),
            state = pagerState,
        ) { page ->
            OnboardingPage(imageRes = onboardingPages[page].imageRes)
        }

        PagerIndicator(
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
                .padding(bottom = 38.dp),
            pagerState = pagerState,
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

private data class OnboardingPageData(
    @DrawableRes val imageRes: Int,
    val title: String,
    val description: String,
)

private val onboardingPages = listOf(
    OnboardingPageData(
        imageRes = R.drawable.icon_onboarding_01,
        title = "빠른 네컷 부스 탐색",
        description = "네컷 부스 정보를\n빠르게 쉽게 찾아요",
    ),
    OnboardingPageData(
        imageRes = R.drawable.icon_onboarding_02,
        title = "포즈 걱정 없는 촬영 경험",
        description = "인원수에 맞는\n포즈를 추천받아요",
    ),
    OnboardingPageData(
        imageRes = R.drawable.icon_onboarding_03,
        title = "네컷 사진 아카이빙",
        description = "흩어지기 쉬운 사진을\n한곳에 모아요",
    ),
)

@Composable
private fun OnboardingTextSection(
    pagerState: PagerState,
    modifier: Modifier = Modifier,
) {
    val pageData = onboardingPages[pagerState.currentPage]

    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Box(
            modifier = Modifier
                .background(
                    shape = CircleShape,
                    color = NekiTheme.colorScheme.gray25
                )
                .padding(horizontal = 12.dp, vertical = 4.dp)
        ) {
            Text(
                text = pageData.title,
                style = NekiTheme.typography.body16SemiBold,
                color = NekiTheme.colorScheme.primary400,
            )
        }
        Text(
            text = pageData.description,
            style = NekiTheme.typography.title24SemiBold,
            fontSize = 28.sp,
            color = NekiTheme.colorScheme.gray900,
        )
    }
}

@Composable
private fun OnboardingPage(
    @DrawableRes imageRes: Int,
) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center,
    ) {
        Image(
            painter = painterResource(id = imageRes),
            contentDescription = null,
        )
    }
}

@Composable
private fun PagerIndicator(
    pagerState: PagerState,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(11.dp),
    ) {
        repeat(pagerState.pageCount) { index ->
            val isSelected = pagerState.currentPage == index
            Box(
                modifier = Modifier
                    .size(10.dp)
                    .clip(CircleShape)
                    .background(
                        if (isSelected) {
                            NekiTheme.colorScheme.primary400
                        } else {
                            NekiTheme.colorScheme.gray50
                        }
                    ),
            )
        }
    }
}
