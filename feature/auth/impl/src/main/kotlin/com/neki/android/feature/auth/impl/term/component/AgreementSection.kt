package com.neki.android.feature.auth.impl.term.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
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
import com.neki.android.core.designsystem.modifier.noRippleClickableSingle
import com.neki.android.core.designsystem.ui.theme.NekiTheme
import com.neki.android.core.model.Term

@Composable
internal fun AgreementSection(
    term: Term,
    onClickAgree: () -> Unit = {},
    onClickNavigateUrl: () -> Unit = {},
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(10.dp),
    ) {
        Row(
            modifier = Modifier
                .weight(1f)
                .noRippleClickable(onClick = onClickAgree)
                .padding(start = 10.dp, top = 10.dp, bottom = 10.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Icon(
                modifier = Modifier
                    .padding(end = 10.dp)
                    .size(24.dp),
                imageVector = ImageVector.vectorResource(R.drawable.icon_check),
                contentDescription = null,
                tint = if (term.isChecked) NekiTheme.colorScheme.primary500 else NekiTheme.colorScheme.gray200,
            )
            Text(
                modifier = Modifier.padding(end = 2.dp),
                text = if (term.isRequired) "(필수)" else "(선택)",
                style = NekiTheme.typography.body14Medium,
                color = NekiTheme.colorScheme.gray500,
            )
            Text(
                text = term.title,
                style = NekiTheme.typography.body16Medium,
                color = NekiTheme.colorScheme.gray900,
            )
        }
        Icon(
            modifier = Modifier
                .size(24.dp)
                .noRippleClickableSingle(onClick = onClickNavigateUrl),
            imageVector = ImageVector.vectorResource(R.drawable.icon_arrow_right),
            contentDescription = null,
            tint = NekiTheme.colorScheme.gray300,
        )
    }
}

@ComponentPreview
@Composable
private fun TermSectionPreview() {
    NekiTheme {
        AgreementSection(term = Term(title = "서비스 이용 약관", isRequired = true))
    }
}
