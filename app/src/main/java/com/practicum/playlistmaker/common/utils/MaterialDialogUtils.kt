package com.practicum.playlistmaker.common.utils

import android.content.Context
import androidx.annotation.StringRes
import androidx.annotation.StyleRes
import androidx.fragment.app.Fragment
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.practicum.playlistmaker.R

/**
 * Создает и настраивает Material Design диалоговое окно с заданными параметрами.
 *
 * Позволяет быстро создать стандартный диалог с заголовком, сообщением,
 * кнопками подтверждения/отмены и кастомным действием при подтверждении.
 *
 * @param context Контекст для создания диалога (по умолчанию - контекст фрагмента)
 * @param title Текст заголовка диалога (обычная строка)
 * @param message Текст сообщения диалога (обычная строка)
 * @param positiveButtonRes ID строкового ресурса для текста кнопки подтверждения (по умолчанию R.string.yes)
 * @param negativeButtonRes ID строкового ресурса для текста кнопки отмены (по умолчанию R.string.no)
 * @param themeResId ID стиля диалога (по умолчанию кастомный стиль)
 * @param isCancelable Флаг, определяющий можно ли закрыть диалог кнопкой "Назад" или тапом вне диалога
 * @param positiveAction Лямбда-функция, выполняемая при нажатии кнопки подтверждения
 *
 * @return Настроенный билдер диалога типа [MaterialAlertDialogBuilder]
 *
 * Пример использования:
 * ```
 * buildMaterialDialog(
 *     title = "Удаление трека",
 *     message = "Вы действительно хотите удалить трек '${track.name}'?",
 *     positiveButtonRes = R.string.delete,
 *     isCancelable = false,
 *     positiveAction = { viewModel.deleteTrack(track.id) }
 * ).show()
 * ```
 *
 * @see MaterialAlertDialogBuilder
 */
fun Fragment.buildMaterialDialog(
    context: Context = requireContext(),
    title: String,
    message: String,
    @StringRes positiveButtonRes: Int = R.string.yes,
    @StringRes negativeButtonRes: Int = R.string.no,
    @StyleRes themeResId: Int = R.style.ThemeOverlay_PlaylistMaker_MaterialAlertDialog,
    isCancelable: Boolean = true,
    positiveAction: () -> Unit,
): MaterialAlertDialogBuilder {
    return MaterialAlertDialogBuilder(context, themeResId)
        .setTitle(title)
        .setMessage(message)
        .setNegativeButton(getString(negativeButtonRes)) { dialog, _ -> dialog.dismiss() }
        .setPositiveButton(getString(positiveButtonRes)) { _, _ -> positiveAction() }
        .setCancelable(isCancelable)
}
