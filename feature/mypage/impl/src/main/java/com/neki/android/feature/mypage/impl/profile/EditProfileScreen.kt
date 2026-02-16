package com.neki.android.feature.mypage.impl.profile

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.input.InputTransformation
import androidx.compose.foundation.text.input.maxLength
import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.neki.android.core.designsystem.ComponentPreview
import com.neki.android.core.designsystem.NekiTextField
import com.neki.android.core.designsystem.ui.theme.NekiTheme
import com.neki.android.core.ui.component.LoadingDialog
import com.neki.android.core.ui.compose.collectWithLifecycle
import coil3.imageLoader
import coil3.request.ImageRequest
import com.neki.android.core.designsystem.R
import com.neki.android.feature.mypage.impl.main.MyPageEffect
import com.neki.android.feature.mypage.impl.main.MyPageIntent
import com.neki.android.feature.mypage.impl.main.MyPageState
import com.neki.android.feature.mypage.impl.main.MyPageViewModel
import com.neki.android.feature.mypage.impl.profile.model.EditProfileImageType
import com.neki.android.feature.mypage.impl.profile.component.EditProfileImage
import com.neki.android.feature.mypage.impl.profile.component.ProfileEditTopBar
import com.neki.android.feature.mypage.impl.profile.component.SelectProfileImageDialog
import com.neki.android.feature.mypage.impl.profile.component.ProfileImageOption
import com.neki.android.feature.mypage.impl.const.MyPageConst
import timber.log.Timber

@Composable
internal fun EditProfileRoute(
    viewModel: MyPageViewModel = hiltViewModel(),
    navigateBack: () -> Unit,
) {
    val context = LocalContext.current
    val uiState by viewModel.store.uiState.collectAsStateWithLifecycle()

    viewModel.store.sideEffects.collectWithLifecycle { sideEffect ->
        when (sideEffect) {
            MyPageEffect.NavigateBack -> navigateBack()
            is MyPageEffect.PreloadImageAndNavigateBack -> {
                val request = ImageRequest.Builder(context)
                    .data(sideEffect.url)
                    .build()
                context.imageLoader.execute(request)
                navigateBack()
            }
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
    var displayProfileImage by remember {
        mutableStateOf<Any?>(uiState.userInfo.profileImageUrl)
    }

    LaunchedEffect(uiState.profileImageState) {
        when (uiState.profileImageState) {
            is EditProfileImageType.OriginalImageUrl -> {}
            is EditProfileImageType.ImageUri -> displayProfileImage = uiState.profileImageState.uri
            EditProfileImageType.Default -> displayProfileImage = R.drawable.image_profile_empty
        }
    }

    val textFieldState = rememberTextFieldState(uiState.userInfo.nickname)

    val photoPicker = rememberLauncherForActivityResult(contract = ActivityResultContracts.PickVisualMedia()) { uri ->
        if (uri != null) {
            onIntent(MyPageIntent.SelectProfileImage(EditProfileImageType.ImageUri(uri)))
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
                onIntent(MyPageIntent.ClickEditComplete(nickname = textFieldState.text.toString()))
            },
        )
        EditProfileImage(
            profileImage = displayProfileImage,
            onClickCameraIcon = { onIntent(MyPageIntent.ClickCameraIcon) },
        )
        NekiTextField(
            textFieldState = textFieldState,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp),
            titleLabel = "닉네임",
            placeholder = "닉네임을 입력해주세요.",
            maxLength = MyPageConst.NICKNAME_MAX_LENGTH,
            inputTransformation = InputTransformation.maxLength(MyPageConst.NICKNAME_MAX_LENGTH),
        )
    }

    if (uiState.isShowImageSelectDialog) {
        SelectProfileImageDialog(
            onDismissRequest = { onIntent(MyPageIntent.DismissImageSelectDialog) },
            onSelect = { option ->
                when (option) {
                    ProfileImageOption.DEFAULT_PROFILE -> {
                        onIntent(MyPageIntent.SelectProfileImage(EditProfileImageType.Default))
                    }
                    ProfileImageOption.SELECT_PHOTO -> {
                        onIntent(MyPageIntent.DismissImageSelectDialog)
                        photoPicker.launch(
                            PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly),
                        )
                    }
                }
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
