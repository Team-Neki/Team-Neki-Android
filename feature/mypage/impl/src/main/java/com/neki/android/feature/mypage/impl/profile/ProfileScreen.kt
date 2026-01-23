package com.neki.android.feature.mypage.impl.profile

import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.input.InputTransformation
import androidx.compose.foundation.text.input.TextFieldLineLimits
import androidx.compose.foundation.text.input.maxLength
import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.DialogProperties
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil3.compose.AsyncImage
import com.neki.android.core.designsystem.ComponentPreview
import com.neki.android.core.designsystem.R
import com.neki.android.core.designsystem.dialog.DoubleButtonAlertDialog
import com.neki.android.core.designsystem.extension.noRippleClickableSingle
import com.neki.android.core.designsystem.ui.theme.NekiTheme
import com.neki.android.core.ui.compose.VerticalSpacer
import com.neki.android.core.ui.compose.collectWithLifecycle
import com.neki.android.feature.mypage.impl.component.SectionItem
import com.neki.android.feature.mypage.impl.component.SectionTitleText
import com.neki.android.feature.mypage.impl.main.MyPageEffect
import com.neki.android.feature.mypage.impl.main.MyPageIntent
import com.neki.android.feature.mypage.impl.main.MyPageState
import com.neki.android.feature.mypage.impl.main.MyPageViewModel
import com.neki.android.feature.mypage.impl.main.ProfileMode
import com.neki.android.feature.mypage.impl.profile.component.ProfileEditTopBar
import com.neki.android.feature.mypage.impl.profile.component.ProfileSettingTopBar
import timber.log.Timber

@Composable
internal fun ProfileRoute(
    viewModel: MyPageViewModel = hiltViewModel(),
    navigateBack: () -> Unit,
) {
    val uiState by viewModel.store.uiState.collectAsStateWithLifecycle()

    viewModel.store.sideEffects.collectWithLifecycle { sideEffect ->
        when (sideEffect) {
            MyPageEffect.NavigateBack -> navigateBack()
            MyPageEffect.NavigateToLogin -> {
                // TODO: 로그인 화면으로 이동
            }

            else -> {}
        }
    }

    ProfileScreen(
        uiState = uiState,
        onIntent = viewModel.store::onIntent,
    )
}

@Composable
fun ProfileScreen(
    uiState: MyPageState = MyPageState(),
    onIntent: (MyPageIntent) -> Unit = {},
) {
    Column(
        modifier = Modifier.fillMaxSize(),
    ) {
        when (uiState.profileMode) {
            ProfileMode.SETTING -> {
                ProfileSettingContent(
                    userName = uiState.userName,
                    onBack = { onIntent(MyPageIntent.ClickBackIcon) },
                    onClickEdit = { onIntent(MyPageIntent.ClickEditIcon) },
                    onClickLogout = { onIntent(MyPageIntent.ClickLogout) },
                    onClickWithdraw = { onIntent(MyPageIntent.CancelLogout) },
                )
            }

            ProfileMode.EDIT -> {
                ProfileEditContent(
                    initialNickname = uiState.userName,
                    profileImageUri = uiState.profileImageUri,
                    onBack = { onIntent(MyPageIntent.ClickBackIcon) },
                    onSelectImage = { uri -> onIntent(MyPageIntent.SelectProfileImage(uri)) },
                    onComplete = { nickname -> onIntent(MyPageIntent.ClickEditComplete(nickname)) },
                )
            }
        }
    }

    if (uiState.isShowLogoutDialog) {
        DoubleButtonAlertDialog(
            title = "로그아웃을 하시겠습니까?",
            content = "다시 로그인해야 서비스를 이용할 수 있어요.",
            grayButtonText = "취소",
            primaryButtonText = "확인",
            properties = DialogProperties(usePlatformDefaultWidth = false),
            onDismissRequest = { onIntent(MyPageIntent.DismissLogoutDialog) },
            onGrayButtonClick = { onIntent(MyPageIntent.DismissLogoutDialog) },
            onPrimaryButtonClick = { onIntent(MyPageIntent.ConfirmLogout) },
        )
    }

    if (uiState.isShowWithdrawDialog) {
        DoubleButtonAlertDialog(
            title = "정말 탈퇴하시겠어요?",
            content = "계정을 탈퇴하면 사진과 정보가 모두 삭제되며,\n삭제된 데이터는 복구할 수 없어요.",
            grayButtonText = "취소",
            primaryButtonText = "탈퇴 확정",
            properties = DialogProperties(usePlatformDefaultWidth = false),
            onDismissRequest = { onIntent(MyPageIntent.DismissSignOutDialog) },
            onGrayButtonClick = { onIntent(MyPageIntent.DismissSignOutDialog) },
            onPrimaryButtonClick = { onIntent(MyPageIntent.ConfirmSignOut) },
        )
    }
}

@Composable
private fun ProfileSettingContent(
    userName: String,
    onBack: () -> Unit,
    onClickEdit: () -> Unit,
    onClickLogout: () -> Unit,
    onClickWithdraw: () -> Unit,
) {
    ProfileSettingTopBar(
        onBack = onBack,
    )
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 20.dp, bottom = 16.dp),
        contentAlignment = Alignment.Center,
    ) {
        AsyncImage(
            modifier = Modifier
                .size(142.dp)
                .clip(CircleShape),
            model = R.drawable.icon_life_four_cut,
            contentDescription = null,
        )
    }
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.CenterHorizontally),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = userName,
            style = NekiTheme.typography.title20Medium,
            color = NekiTheme.colorScheme.gray900,
        )
        Icon(
            modifier = Modifier.noRippleClickableSingle { onClickEdit() },
            imageVector = ImageVector.vectorResource(R.drawable.icon_edit),
            contentDescription = null,
            tint = Color.Unspecified,
        )
    }
    VerticalSpacer(27.dp)
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(color = NekiTheme.colorScheme.gray25),
    )
    SectionTitleText(text = "서비스 정보 및 지원")
    SectionItem(
        text = "로그아웃",
        onClick = onClickLogout,
    )
    SectionItem(
        text = "탈퇴하기",
        onClick = onClickWithdraw,
    )
}

@Composable
private fun ProfileEditContent(
    initialNickname: String,
    profileImageUri: Uri?,
    onBack: () -> Unit,
    onSelectImage: (Uri) -> Unit,
    onComplete: (String) -> Unit,
) {
    val textFieldState = rememberTextFieldState(initialNickname)

    val photoPicker = rememberLauncherForActivityResult(contract = ActivityResultContracts.PickVisualMedia()) { uri ->
        if (uri != null) {
            onSelectImage(uri)
        } else {
            Timber.d("No media selected")
        }
    }

    ProfileEditTopBar(
        enabled = textFieldState.text.isNotEmpty(),
        onBack = onBack,
        onClickComplete = { onComplete(textFieldState.text.toString()) },
    )
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 20.dp, bottom = 16.dp),
        contentAlignment = Alignment.Center,
    ) {
        AsyncImage(
            modifier = Modifier
                .size(142.dp)
                .clip(CircleShape),
            model = profileImageUri ?: R.drawable.icon_life_four_cut,
            contentDescription = null,
        )
        Box(
            modifier = Modifier
                .padding(start = 102.dp, top = 102.dp)
                .border(
                    width = 1.dp,
                    shape = CircleShape,
                    color = NekiTheme.colorScheme.primary400,
                )
                .background(
                    color = NekiTheme.colorScheme.white,
                    shape = CircleShape,
                )
                .padding(8.dp)
                .noRippleClickableSingle {
                    photoPicker.launch(
                        PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly),
                    )
                },
            contentAlignment = Alignment.Center,
        ) {
            Icon(
                imageVector = ImageVector.vectorResource(R.drawable.icon_camera),
                contentDescription = null,
                tint = NekiTheme.colorScheme.primary400,
            )
        }
    }
    VerticalSpacer(28.dp)
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp),
        verticalArrangement = Arrangement.spacedBy(6.dp),
    ) {
        Text(
            text = "닉네임",
            style = NekiTheme.typography.body14Medium,
            color = NekiTheme.colorScheme.gray700,
        )
        BasicTextField(
            state = textFieldState,
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    color = NekiTheme.colorScheme.white,
                    shape = RoundedCornerShape(8.dp),
                )
                .border(
                    width = 1.dp,
                    color = if (textFieldState.text.isEmpty()) NekiTheme.colorScheme.gray75 else NekiTheme.colorScheme.gray700,
                    shape = RoundedCornerShape(8.dp),
                )
                .padding(horizontal = 16.dp, vertical = 13.dp),
            textStyle = NekiTheme.typography.body16Medium.copy(
                color = NekiTheme.colorScheme.gray900,
            ),
            inputTransformation = InputTransformation.maxLength(10),
            cursorBrush = SolidColor(NekiTheme.colorScheme.gray800),
            lineLimits = TextFieldLineLimits.SingleLine,
            decorator = { innerTextField ->
                Box {
                    if (textFieldState.text.isEmpty()) {
                        Text(
                            text = "닉네임을 입력해주세요.",
                            style = NekiTheme.typography.body16Regular,
                            color = NekiTheme.colorScheme.gray300,
                        )
                    }
                    innerTextField()
                }
            },
        )
    }
}

@ComponentPreview
@Composable
private fun ProfileScreenSettingPreview() {
    NekiTheme {
        ProfileScreen(
            uiState = MyPageState(profileMode = ProfileMode.SETTING),
        )
    }
}

@ComponentPreview
@Composable
private fun ProfileScreenEditPreview() {
    NekiTheme {
        ProfileScreen(
            uiState = MyPageState(profileMode = ProfileMode.EDIT),
        )
    }
}
