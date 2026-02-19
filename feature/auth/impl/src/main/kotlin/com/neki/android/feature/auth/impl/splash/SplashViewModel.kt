package com.neki.android.feature.auth.impl.splash

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.neki.android.core.dataapi.repository.AuthRepository
import com.neki.android.core.dataapi.repository.TokenRepository
import com.neki.android.core.ui.MviIntentStore
import com.neki.android.core.ui.mviIntentStore
import com.neki.android.feature.auth.impl.splash.model.UpdateType
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class SplashViewModel @Inject constructor(
    private val tokenRepository: TokenRepository,
    private val authRepository: AuthRepository,
) : ViewModel() {

    val store: MviIntentStore<SplashState, SplashIntent, SplashSideEffect> =
        mviIntentStore(
            initialState = SplashState(),
            onIntent = ::onIntent,
        )

    private fun onIntent(
        intent: SplashIntent,
        state: SplashState,
        reduce: (SplashState.() -> SplashState) -> Unit,
        postSideEffect: (SplashSideEffect) -> Unit,
    ) {
        when (intent) {
            is SplashIntent.EnterSplashScreen -> checkVersionAndAuth(intent.appVersion, reduce, postSideEffect)
            SplashIntent.ClickDismissUpdateDialog -> handleDismissUpdate(state, reduce, postSideEffect)
            SplashIntent.ClickUpdateButton -> postSideEffect(SplashSideEffect.NavigatePlayStore)
        }
    }

    private fun checkVersionAndAuth(
        currentAppVersion: String,
        reduce: (SplashState.() -> SplashState) -> Unit,
        postSideEffect: (SplashSideEffect) -> Unit,
    ) {
        viewModelScope.launch {
            authRepository.getAppVersion()
                .onSuccess { appVersion ->
                    val updateType = getUpdateType(
                        appVersion = currentAppVersion,
                        minVersion = appVersion.minVersion,
                        currentVersion = appVersion.currentVersion,
                    )

                    when (updateType) {
                        UpdateType.None -> {
                            fetchAuthState(postSideEffect)
                        }

                        UpdateType.Required -> {
                            reduce {
                                copy(
                                    updateType = UpdateType.Required,
                                    minVersion = appVersion.minVersion,
                                )
                            }
                        }

                        UpdateType.Optional -> {
                            val dismissedVersion = authRepository.dismissedVersion.first()
                            if (dismissedVersion != appVersion.currentVersion) {
                                reduce {
                                    copy(
                                        updateType = UpdateType.Optional,
                                        minVersion = appVersion.currentVersion,
                                    )
                                }
                            } else {
                                fetchAuthState(postSideEffect)
                            }
                        }
                    }
                }
                .onFailure { exception ->
                    Timber.e(exception, "Failed to fetch app version")
                    fetchAuthState(postSideEffect)
                }
        }
    }

    private fun handleDismissUpdate(
        state: SplashState,
        reduce: (SplashState.() -> SplashState) -> Unit,
        postSideEffect: (SplashSideEffect) -> Unit,
    ) {
        viewModelScope.launch {
            authRepository.setDismissedVersion(state.minVersion)
            reduce { copy(updateType = UpdateType.None) }
            fetchAuthState(postSideEffect)
        }
    }

    private fun fetchAuthState(postSideEffect: (SplashSideEffect) -> Unit) {
        viewModelScope.launch {
            delay(1000)
            if (!authRepository.hasCompletedOnboarding().first()) {
                postSideEffect(SplashSideEffect.NavigateToOnboarding)
                return@launch
            }

            if (tokenRepository.hasTokens().first()) {
                authRepository.updateAccessToken(
                    refreshToken = tokenRepository.getRefreshToken().first(),
                ).onSuccess {
                    tokenRepository.saveTokens(it.accessToken, it.refreshToken)
                    postSideEffect(SplashSideEffect.NavigateToMain)
                }.onFailure { exception ->
                    Timber.e(exception)
                    postSideEffect(SplashSideEffect.NavigateToLogin)
                }
            } else {
                postSideEffect(SplashSideEffect.NavigateToLogin)
            }
        }
    }

    /**
     * 버전 체크 로직:
     * - appVersion >= currentVersion: 최신 버전 (None)
     * - minVersion <= appVersion < currentVersion: 선택 업데이트 (Optional)
     * - appVersion < minVersion: 필수 업데이트 (Required)
     */
    private fun getUpdateType(
        appVersion: String,
        minVersion: String,
        currentVersion: String,
    ): UpdateType {
        return when {
            compareVersions(appVersion, currentVersion) >= 0 -> UpdateType.None
            compareVersions(appVersion, minVersion) >= 0 -> UpdateType.Optional
            else -> UpdateType.Required
        }
    }

    /**
     * 버전 문자열을 각 자릿수를 비교하여 차이를 반환
     * @return 양수: appVersion이 더 높음, 0: 같음, 음수: appVersion이 더 낮음
     */
    private fun compareVersions(appVersion: String, compareVersion: String): Int {
        val parts1 = appVersion.split(".").map { it.toIntOrNull() ?: 0 }
        val parts2 = compareVersion.split(".").map { it.toIntOrNull() ?: 0 }
        val maxLength = maxOf(parts1.size, parts2.size)

        for (i in 0 until maxLength) {
            val v1 = parts1.getOrElse(i) { 0 }
            val v2 = parts2.getOrElse(i) { 0 }
            if (v1 != v2) return v1 - v2
        }
        return 0
    }
}
