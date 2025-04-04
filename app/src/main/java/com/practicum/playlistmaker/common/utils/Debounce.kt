package com.practicum.playlistmaker.common.utils

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

fun <T> debounce(
    delayMillis: Long,
    coroutineScope: CoroutineScope,
    useLastParam: Boolean,
    action: (T) -> Unit,
): (T) -> Unit {
    var debounceJob: Job? = null

    return { param: T ->
        if (useLastParam) {
            debounceJob?.cancel() // Отменяем предыдущую задачу, если используем последний параметр.
        }

        // Если задача не активна или используем последний параметр, запускаем новую задачу.
        if (debounceJob?.isActive != true || useLastParam) {
            debounceJob = coroutineScope.launch {
                try {
                    delay(delayMillis)
                    action(param)
                } finally {
                    debounceJob = null // Очищаем ссылку на завершенную задачу.
                }
            }
        }
    }
}
