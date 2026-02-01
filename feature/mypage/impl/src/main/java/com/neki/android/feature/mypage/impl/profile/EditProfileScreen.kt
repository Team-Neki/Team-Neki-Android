package com.neki.android.feature.mypage.impl.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.input.InputTransformation
import androidx.compose.foundation.text.input.TextFieldLineLimits
import androidx.compose.foundation.text.input.maxLength
import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.material3.Text
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.neki.android.core.common.util.toByteArray
import com.neki.android.core.designsystem.ComponentPreview
import com.neki.android.core.designsystem.ui.theme.NekiTheme
import com.neki.android.core.ui.component.LoadingDialog
import com.neki.android.core.ui.compose.collectWithLifecycle
import com.neki.android.feature.mypage.impl.main.MyPageEffect
import com.neki.android.feature.mypage.impl.main.MyPageIntent
import com.neki.android.feature.mypage.impl.main.MyPageState
import com.neki.android.feature.mypage.impl.main.MyPageViewModel
import com.neki.android.feature.mypage.impl.main.SelectedProfileImage
import com.neki.android.feature.mypage.impl.profile.component.EditProfileImage
import com.neki.android.feature.mypage.impl.profile.component.ProfileEditTopBar
import com.neki.android.feature.mypage.impl.profile.component.ProfileImageChooseDialog
import timber.log.Timber

@Composable
internal fun EditProfileRoute(
    viewModel: MyPageViewModel = hiltViewModel(),
    navigateBack: () -> Unit,
) {
    val uiState by viewModel.store.uiState.collectAsStateWithLifecycle()

    viewModel.store.sideEffects.collectWithLifecycle { sideEffect ->
        when (sideEffect) {
            MyPageEffect.NavigateBack -> navigateBack()
            else -> {}
        }
    }

    EditProfileScreen(
        uiState = uiState,
        onIntent = viewModel.store::onIntent,
    )
}

@Composable
fun EditProfileScreen(
    uiState: MyPageState = MyPageState(),
    onIntent: (MyPageIntent) -> Unit = {},
) {
    val context = LocalContext.current

    val displayProfileImage: Any? = when (uiState.selectedProfileImage) {
        SelectedProfileImage.Default -> uiState.userInfo.profileImageUrl
        is SelectedProfileImage.Selected -> uiState.selectedProfileImage.uri
    }

    val textFieldState = rememberTextFieldState(uiState.userInfo.nickname)

    val photoPicker = rememberLauncherForActivityResult(contract = ActivityResultContracts.PickVisualMedia()) { uri ->
        if (uri != null) {
            onIntent(MyPageIntent.SelectProfileImage(SelectedProfileImage.Selected(uri)))
        } else {
            Timber.d("No media selected")
        }
    }

    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        ProfileEditTopBar(
            enabled = textFieldState.text.isNotEmpty(),
            onBack = { onIntent(MyPageIntent.ClickBackIcon) },
            onClickComplete = {
                val imageBytes = (uiState.selectedProfileImage as? SelectedProfileImage.Selected)?.uri?.toByteArray(context)
                onIntent(MyPageIntent.ClickEditComplete(textFieldState.text.toString(), imageBytes))
            },
        )
        EditProfileImage(
            profileImage = displayProfileImage,
            onClickCameraIcon = { onIntent(MyPageIntent.ClickCameraIcon) },
        )
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

    if (uiState.isShowImageChooseDialog) {
        ProfileImageChooseDialog(
            onDismissRequest = { onIntent(MyPageIntent.DismissImageChooseDialog) },
            onClickDefaultProfile = { onIntent(MyPageIntent.SelectProfileImage(SelectedProfileImage.Selected(null))) },
            onClickSelectPhoto = {
                onIntent(MyPageIntent.DismissImageChooseDialog)
                photoPicker.launch(
                    PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly),
                )
            },
        )
    }

    if (uiState.isLoading) {
        LoadingDialog()
    }
}

@ComponentPreview
@Composable
private fun EditProfileScreenPreview() {
    NekiTheme {
        EditProfileScreen()
    }
}
