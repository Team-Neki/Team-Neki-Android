package com.neki.android.feature.auth.impl.util

import android.content.Context
import com.kakao.sdk.user.UserApiClient

class KakaoLoginHelper(
    private val context: Context,
) {
    fun loginWithKakao(
        onSuccess: (String) -> Unit,
        onFailure: (String) -> Unit,
    ) {
        // 카카오톡 설치 여부 확인
        if (UserApiClient.Companion.instance.isKakaoTalkLoginAvailable(context)) {
            // 카카오톡으로 로그인
            UserApiClient.Companion.instance.loginWithKakaoTalk(context) { token, error ->
                if (error != null) {
                    onFailure(error.message ?: "카카오 로그인에 실패했습니다.")
                } else if (token != null) {
                    onSuccess(token.idToken!!)
                }
            }
        } else {
            // 카카오 계정으로 로그인
            UserApiClient.Companion.instance.loginWithKakaoAccount(context) { token, error ->
                if (error != null) {
                    onFailure(error.message ?: "카카오 로그인에 실패했습니다.")
                } else if (token != null) {
                    onSuccess(token.idToken!!)
                }
            }
        }
    }
}
