# PlaylistMaker - музыкальный плейлист-мейкер

<img src=".github/logo.png" alt="PlaylistMaker Icon" width="100" align="right">

Приложение для создания плейлистов и прослушивания музыки с использованием Apple Music API.

[![Kotlin Version](https://img.shields.io/badge/kotlin-2.1.20-blue.svg)](https://kotlinlang.org/)
[![License](https://img.shields.io/badge/license-MIT-green)](https://opensource.org/licenses/MIT)

## 🔍 Основной функционал

- **Поиск музыки** - интеграция с Apple Music API для поиска треков
- **Прослушивание превью** - воспроизведение 30-секундных сэмплов треков
- **Управление избранным**:
    - Добавление/удаление треков в избранное
    - Просмотр списка избранных треков
- **Работа с плейлистами**:
    - Создание и редактирование плейлистов
    - Добавление треков в плейлисты
    - Умное удаление неиспользуемых треков

## 🛠 Технологический стек

### Языки и инструменты
- Kotlin 2.1.20
- Android Gradle Plugin 8.1.0
- KSP 2.1.20-2.0.0

### Основные библиотеки
| Категория       | Библиотеки                          |
|----------------|-----------------------------------|
| UI             | Material Components, ViewPager2   |
| Навигация      | Navigation Component              |
| Локальное хранилище | Room Database                   |
| Сеть           | Retrofit + Gson                   |
| Медиа          | ExoPlayer                         |
| DI             | Koin                              |
| Загрузка изображений | Glide                        |
| Логирование    | Timber                            |

### Архитектура
- Clean Architecture
- MVVM
- Single Activity
- Реактивное программирование (Flow)

## 🚀 Быстрый старт

Клонируйте репозиторий:
```bash
git clone https://github.com/RD2W/PlaylistMaker.git
```