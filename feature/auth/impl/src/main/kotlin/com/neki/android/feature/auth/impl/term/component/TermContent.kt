package com.neki.android.feature.auth.impl.term.component

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.unit.dp
import com.neki.android.core.designsystem.ComponentPreview
import com.neki.android.core.designsystem.R
import com.neki.android.core.designsystem.modifier.noRippleClickable
import com.neki.android.core.designsystem.ui.theme.NekiTheme
import com.neki.android.core.ui.compose.VerticalSpacer
import com.neki.android.feature.auth.impl.term.model.TermAgreement
import kotlinx.collections.immutable.ImmutableSet
import kotlinx.collections.immutable.persistentSetOf

@Composable
fun TermContent(
    modifier: Modifier = Modifier,
    agreedTerms: ImmutableSet<TermAgreement> = persistentSetOf(),
    isAllRequiredAgreed: Boolean = false,
    onClickAgreeAll: () -> Unit = {},
    onClickAgreeTerm: (TermAgreement) -> Unit = {},
    onClickTermDetail: (TermAgreement) -> Unit = {},
) {
    Column(
        modifier = modifier
    ) {
        Image(
            modifier = Modifier.padding(bottom = 12.dp),
            imageVector = ImageVector.vectorResource(R.drawable.icon_agreement),
            contentDescription = null
        )
        Text(
            modifier = Modifier.padding(bottom = 24.dp),
            text = "편리한 네키 이용을 위한\n필수 약관에 동의해주세요.",
            style = NekiTheme.typography.title24SemiBold,
            color = NekiTheme.colorScheme.gray900
        )
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .noRippleClickable(onClick = onClickAgreeAll)
                .border(
                    width = 1.dp,
                    color = if (isAllRequiredAgreed) NekiTheme.colorScheme.primary500 else NekiTheme.colorScheme.gray100,
                    shape = RoundedCornerShape(12.dp)
                )
                .background(
                    shape = RoundedCornerShape(12.dp),
                    color = NekiTheme.colorScheme.white
                )
                .padding(vertical = 18.dp, horizontal = 16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Icon(
                modifier = Modifier.size(24.dp),
                imageVector = ImageVector.vectorResource(R.drawable.icon_check),
                contentDescription = null,
                tint = if (isAllRequiredAgreed) NekiTheme.colorScheme.primary500 else NekiTheme.colorScheme.gray200
            )
            Text(
                text = "약관 전체 동의",
                style = NekiTheme.typography.title18SemiBold,
                color = NekiTheme.colorScheme.gray900
            )
        }
        VerticalSpacer(12.dp)
        TermAgreement.entries.forEach { agreement ->
            AgreementSection(
                agreement = agreement,
                isAgreed = agreement in agreedTerms,
                onClickAgree = { onClickAgreeTerm(agreement) },
                onClickNavigateUrl = { onClickTermDetail(agreement) },
            )
        }
    }
}

@ComponentPreview
@Composable
private fun TermContentPreview() {
    NekiTheme {
        TermContent()
    }
}
