package com.practicum.playlistmaker.media.domain.usecase

/**
 * UseCase для удаления трека из базы данных.
 *
 * Описание:
 * - Предоставляет suspend-функцию `invoke`, которая асинхронно удаляет трек по его `trackId`.
 * - Реализация этого UseCase должна взаимодействовать с репозиторием или DAO для выполнения операции удаления.
 * - Использует `suspend`, поэтому должен вызываться из корутины или другого suspend-контекста.
 *
 * Пример использования:
 * ```kotlin
 * val deleteTrackUseCase: DeleteTrackFromDataBaseUseCase = ...
 * viewModelScope.launch {
 *     deleteTrackUseCase(trackId = 123)
 * }
 * ```
 *
 * Примечания:
 * - Не выбрасывает исключений, если трек с указанным `trackId` не найден.
 * - Реализация может возвращать количество удаленных записей, но интерфейс абстрагируется от этого.
 */
interface DeleteTrackFromDataBaseUseCase {
    /**
     * Удаляет трек из базы данных по его ID.
     *
     * @param trackId Уникальный идентификатор трека, который нужно удалить.
     *
     * Логика работы:
     * - Если трек с `trackId` существует, он будет удален.
     * - Если трека нет, операция завершается без ошибок.
     * - Выполняется асинхронно (suspend-функция).
     */
    suspend operator fun invoke(trackId: Int)
}
