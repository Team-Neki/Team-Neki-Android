package com.neki.android.feature.auth.impl.onboarding.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.neki.android.core.designsystem.ComponentPreview
import com.neki.android.core.designsystem.ui.theme.NekiTheme
import com.neki.android.feature.auth.impl.onboarding.model.OnboardingPage

@Composable
internal fun TextSection(
    page: OnboardingPage,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        Box(
            modifier = Modifier
                .background(
                    shape = CircleShape,
                    color = NekiTheme.colorScheme.primary25,
                )
                .padding(horizontal = 12.dp, vertical = 4.dp),
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
