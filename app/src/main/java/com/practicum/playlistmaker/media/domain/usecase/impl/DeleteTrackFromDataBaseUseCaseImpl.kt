package com.practicum.playlistmaker.media.domain.usecase.impl

import com.practicum.playlistmaker.media.domain.repository.TracksDataBaseRepository
import com.practicum.playlistmaker.media.domain.usecase.DeleteTrackFromDataBaseUseCase

/**
 * Реализация UseCase для полного удаления трека из базы данных.
 *
 * Описание:
 * - Выполняет окончательное удаление трека из системы
 * - Используется, когда трек больше не содержится ни в избранном, ни в каких-либо плейлистах
 * - Предоставляет точку входа для каскадного удаления связанных данных
 *
 * Зависимости:
 * - [repository] - Репозиторий для работы с треками в базе данных
 */
class DeleteTrackFromDataBaseUseCaseImpl(
    private val repository: TracksDataBaseRepository,
) : DeleteTrackFromDataBaseUseCase {

    /**
     * Основная операция UseCase - удаление трека из базы данных.
     *
     * Логика работы:
     * 1. Получает команду на удаление трека по его ID
     * 2. Делегирует операцию удаления репозиторию [TracksDataBaseRepository]
     * 3. Не возвращает результат, так как операция считается выполненной при отсутствии исключений
     *
     * Особенности:
     * - Является терминальной операцией (после удаления восстановление невозможно)
     * - Должен вызываться только после проверки, что трек нигде не используется
     * - Использует suspend-функцию для асинхронного выполнения
     *
     * @param trackId Уникальный идентификатор трека для удаления
     */
    override suspend operator fun invoke(trackId: Int) {
        repository.deleteFromDataBase(trackId)
    }
}
