package com.neki.android.core.common.kakao

import android.content.Context
import com.kakao.sdk.user.UserApiClient

object KakaoAuthHelper {
    fun login(
        context: Context,
        onSuccess: (String) -> Unit,
        onFailure: (String) -> Unit,
    ) {
        if (UserApiClient.instance.isKakaoTalkLoginAvailable(context)) {
            UserApiClient.instance.loginWithKakaoTalk(context) { token, error ->
                if (error != null) {
                    onFailure(error.message ?: "카카오 로그인에 실패했습니다.")
                } else if (token != null) {
                    onSuccess(token.idToken!!)
                }
            }
        } else {
            UserApiClient.instance.loginWithKakaoAccount(context) { token, error ->
                if (error != null) {
                    onFailure(error.message ?: "카카오 로그인에 실패했습니다.")
                } else if (token != null) {
                    onSuccess(token.idToken!!)
                }
            }
        }
    }

    fun logout(
        onSuccess: () -> Unit,
        onFailure: (String) -> Unit,
    ) {
        UserApiClient.instance.logout { error ->
            if (error != null) {
                onFailure(error.message ?: "카카오 로그아웃에 실패했습니다.")
            } else {
                onSuccess()
            }
        }
    }

    fun unlink(
        onSuccess: () -> Unit,
        onFailure: (String) -> Unit,
    ) {
        UserApiClient.instance.unlink { error ->
            if (error != null) {
                onFailure(error.message ?: "카카오 연결 해제에 실패했습니다.")
            } else {
                onSuccess()
            }
        }
    }
}
