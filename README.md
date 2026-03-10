# MicroHabit (Android)

Простой трекер микро-привычек:
- онбординг: при первом запуске создание первой задачи;
- задачи можно создавать, редактировать, удалять;
- периодичность: `каждый день`, `будни`, `кастомные дни`;
- главный экран с большой кнопкой `Сегодня сделал? [ДА]`;
- `streak`, график за 7 дней и прогресс за 30 дней;
- календарь с выбором даты и кнопкой `Сегодня`;
- homescreen-виджет с задачей, streak и прогрессом.

## Стек
- Kotlin
- Android Jetpack Compose (Material 3)
- SharedPreferences (хранение задач и отметок)
- AppWidget (RemoteViews)

## Запуск
1. Открыть папку проекта в Android Studio (`e:/Projects/Application`).
2. Дождаться Gradle Sync.
3. Запустить `app` на эмуляторе или устройстве Android (minSdk 26).

## Структура
- `app/src/main/java/com/example/microhabit/MainActivity.kt` - UI экрана.
- `app/src/main/java/com/example/microhabit/MainViewModel.kt` - состояние экрана и действия.
- `app/src/main/java/com/example/microhabit/data/HabitRepository.kt` - хранение отметок и расчет streak.
- `app/src/main/java/com/example/microhabit/widget/HabitWidgetProvider.kt` - логика виджета.
