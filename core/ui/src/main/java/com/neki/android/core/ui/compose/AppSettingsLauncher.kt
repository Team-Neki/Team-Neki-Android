package com.neki.android.core.ui.compose

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.provider.Settings
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberUpdatedState
import com.neki.android.core.common.permission.NekiPermission

/**
 * 앱 설정 화면은 의미 있는 resultCode를 반환하지 않으므로, 콜백이 호출된 시점 자체를
 * "설정 화면에서 돌아옴" 신호로 사용한다.
 */
@Composable
fun rememberAppSettingsLauncher(
    onResult: () -> Unit,
): ActivityResultLauncher<Intent> {
    val currentOnResult by rememberUpdatedState(onResult)
    return rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult(),
    ) {
        currentOnResult()
    }
}

/**
 * NOTIFICATION은 알림 설정만 바로 보여주는 전용 화면(ACTION_APP_NOTIFICATION_SETTINGS)으로 이동한다.
 * 그 외 권한은 개별 권한만 여는 공식 API가 없어 앱 상세 설정 화면으로 이동한다.
 */
fun ActivityResultLauncher<Intent>.launchAppSettings(context: Context, permission: NekiPermission) {
    val intent = if (permission == NekiPermission.NOTIFICATION) {
        Intent(Settings.ACTION_APP_NOTIFICATION_SETTINGS).apply {
            putExtra(Settings.EXTRA_APP_PACKAGE, context.packageName)
        }
    } else {
        Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
            data = Uri.fromParts("package", context.packageName, null)
        }
    }
    launch(intent)
}
