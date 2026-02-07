package com.neki.android.feature.auth.impl.onboarding.component

import androidx.annotation.DrawableRes
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.neki.android.core.designsystem.ComponentPreview
import com.neki.android.core.designsystem.R
import com.neki.android.core.designsystem.ui.theme.NekiTheme

@Composable
internal fun TextSection(
    page: OnboardingPage,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Box(
            modifier = Modifier
                .background(
                    shape = CircleShape,
                    color = NekiTheme.colorScheme.primary25
                )
                .padding(horizontal = 12.dp, vertical = 4.dp)
        ) {
            Text(
                text = page.title,
                style = NekiTheme.typography.body16SemiBold,
                color = NekiTheme.colorScheme.primary400,
            )
        }
        Text(
            text = page.description,
            style = NekiTheme.typography.title28Bold,
            color = NekiTheme.colorScheme.gray900,
        )
    }
}

enum class OnboardingPage(
    @DrawableRes val imageRes: Int,
    val title: String,
    val description: String,
) {
    BOOTH_SEARCH(
        imageRes = R.drawable.icon_onboarding_01,
        title = "빠른 네컷 부스 탐색",
        description = "네컷 부스 정보를\n빠르게 쉽게 찾아요",
    ),
    POSE_RECOMMEND(
        imageRes = R.drawable.icon_onboarding_02,
        title = "포즈 걱정 없는 촬영 경험",
        description = "인원수에 맞는\n포즈를 추천받아요",
    ),
    PHOTO_ARCHIVE(
        imageRes = R.drawable.icon_onboarding_03,
        title = "네컷 사진 아카이빙",
        description = "흩어지기 쉬운 사진을\n한곳에 모아요",
    ),
}

@ComponentPreview
@Composable
private fun OnboardingTextSectionPreview() {
    NekiTheme {
        TextSection(
            page = OnboardingPage.BOOTH_SEARCH,
            modifier = Modifier.padding(16.dp),
        )
    }
}
