package com.neki.android.core.data.util

import kotlin.contracts.ExperimentalContracts
import kotlin.contracts.InvocationKind
import kotlin.contracts.contract
import kotlin.coroutines.cancellation.CancellationException

@OptIn(ExperimentalContracts::class)
inline fun <T> runSuspendCatching(block: () -> T): Result<T> {
    // Kotlin 의 contract(계약) 시스템을 이용해 block 이 정확히 한번만 호출 되어야 함을 나타냄
    contract { callsInPlace(block, InvocationKind.EXACTLY_ONCE) }

    return runCatching(block).also { result ->
        // 만약 람다에서 예외가 발생하면, Result 객체는 실패를 나타내고 해당 예외를 포함, 추가적인 작업을 실행
        val maybeException = result.exceptionOrNull()
        // 만약 예외가 CancellationException 이면 예외를 던져 코루틴 계층 구조에 따라 상위 코루틴까지 취소 신호를 전파
        // 이를 통해, 상위 코루틴에서 적절한 예외 처리 루틴을 수행할 수 있음
        if (maybeException is CancellationException) throw maybeException
    }
}
