package com.neki.android.feature.mypage.impl

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.neki.android.core.designsystem.ComponentPreview
import com.neki.android.core.designsystem.ui.theme.NekiTheme
import com.neki.android.feature.mypage.impl.component.MainTopBar
import com.neki.android.feature.mypage.impl.component.ProfileCard
import com.neki.android.feature.mypage.impl.component.SectionItem
import com.neki.android.feature.mypage.impl.component.SectionTitleText

@Composable
fun MyPageRoute(
    modifier: Modifier = Modifier,
) {
    MyPageScreen(modifier = modifier)
}

@Composable
fun MyPageScreen(
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxSize()
    ) {
        MainTopBar()
        ProfileCard(
            name = "오종석"
        )
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(11.dp)
                .background(color = NekiTheme.colorScheme.gray25)
        )
        Column {
            SectionTitleText(
                text = "권한 설정"
            )
            SectionItem(
                text = "기기 권한"
            )
        }
        Column {
            SectionTitleText(
                text = "서비스 정보 및 지원"
            )
            SectionItem(
                text = "Neki에 문의하기"
            )
            SectionItem(
                text = "이용약관"
            )
            SectionItem(
                text = "개인정보 처리방침"
            )
            SectionItem(
                text = "오픈소스 라이선스"
            )
            SectionItem(
                text = "앱 버전 정보",
                trailingContent = {
                    Text(
                        text = "v1.3.1",
                        color = NekiTheme.colorScheme.gray500,
                        style = NekiTheme.typography.body14Medium,
                    )
                },
            )
        }
    }
}

@ComponentPreview
@Composable
private fun MyPageScreenPreview() {
    NekiTheme {
        MyPageScreen()
    }
}
