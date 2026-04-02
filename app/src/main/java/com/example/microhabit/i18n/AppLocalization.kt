package com.example.microhabit.i18n

import androidx.compose.runtime.Composable
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.runtime.staticCompositionLocalOf
import com.example.microhabit.data.AppLanguage
import java.time.DayOfWeek
import java.time.format.TextStyle
import java.util.Locale

val LocalAppLanguage = staticCompositionLocalOf { AppLanguage.EN }

private fun v(de: String, fr: String, es: String, it: String, ru: String): Map<AppLanguage, String> =
    mapOf(
        AppLanguage.DE to de,
        AppLanguage.FR to fr,
        AppLanguage.ES to es,
        AppLanguage.IT to it,
        AppLanguage.RU to ru
    )

private val translations: Map<String, Map<AppLanguage, String>> = mapOf(
    "Tracker" to v("Tracker", "Suivi", "Seguimiento", "Monitoraggio", "Трекер"),
    "Habit details" to v("Gewohnheitsdetails", "Details de l'habitude", "Detalles del habito", "Dettagli abitudine", "Детали привычки"),
    "Habits" to v("Gewohnheiten", "Habitudes", "Habitos", "Abitudini", "Привычки"),
    "My habits" to v("Meine Gewohnheiten", "Mes habitudes", "Mis habitos", "Le mie abitudini", "Мои привычки"),
    "No habits" to (
        v("Keine Gewohnheiten", "Aucune habitude", "No hay habitos", "Nessuna abitudine", "Нет привычек") +
            mapOf(AppLanguage.CS to "Žádné návyky")
    ),
    "%d habit" to (
        v("%d Gewohnheit", "%d habitude", "%d habito", "%d abitudine", "%d привычка") +
            mapOf(AppLanguage.CS to "%d návyk")
    ),
    "%d habits few" to (
        v("%d Gewohnheiten", "%d habitudes", "%d habitos", "%d abitudini", "%d привычки") +
            mapOf(AppLanguage.CS to "%d návyky")
    ),
    "%d habits" to (
        v("%d Gewohnheiten", "%d habitudes", "%d habitos", "%d abitudini", "%d привычек") +
            mapOf(AppLanguage.CS to "%d návyků")
    ),
    "← → swipe to switch habits" to (
        v(
            "← → Gewohnheiten wischen",
            "← → balayez pour changer d'habitude",
            "← → desliza para cambiar habitos",
            "← → scorri per cambiare abitudini",
            "← → листать привычки"
        ) + mapOf(AppLanguage.CS to "← → přejet pro návyky")
    ),
    "Analytics" to v("Analysen", "Analyses", "Analitica", "Analisi", "Аналитика"),
    "Calendar" to v("Kalender", "Calendrier", "Calendario", "Calendario", "Календарь"),
    "Premium" to v("Premium", "Premium", "Premium", "Premium", "Премиум"),
    "Calm focus for consistent habit building." to v(
        "Ruhiger Fokus fur konstante Gewohnheiten.",
        "Un focus calme pour des habitudes regulieres.",
        "Enfoque tranquilo para crear habitos constantes.",
        "Focus calmo per costruire abitudini costanti.",
        "Спокойный фокус для стабильного формирования привычек."
    ),
    "Unlock unlimited habits and deeper insights." to v(
        "Schalte unbegrenzte Gewohnheiten und tiefere Einblicke frei.",
        "Debloquez des habitudes illimitees et des analyses approfondies.",
        "Desbloquea habitos ilimitados y analitica mas profunda.",
        "Sblocca abitudini illimitate e analisi piu approfondite.",
        "Откройте безлимитные привычки и более глубокую аналитику."
    ),
    "You already have Premium access." to v(
        "Du hast bereits Premium-Zugang.",
        "Vous avez deja l'acces Premium.",
        "Ya tienes acceso Premium.",
        "Hai gia accesso Premium.",
        "У вас уже есть доступ к Премиум."
    ),
    "Account" to v("Konto", "Compte", "Cuenta", "Account", "Аккаунт"),
    "Settings" to v("Einstellungen", "Parametres", "Ajustes", "Impostazioni", "Настройки"),
    "App settings" to v("App-Einstellungen", "Parametres de l'app", "Ajustes de la app", "Impostazioni app", "Настройки приложения"),
    "Account controls" to v("Kontosteuerung", "Controles du compte", "Controles de la cuenta", "Controlli account", "Управление аккаунтом"),
    "Premium and app preferences." to v(
        "Premium und App-Einstellungen.",
        "Premium et preferences de l'application.",
        "Premium y preferencias de la aplicacion.",
        "Premium e preferenze dell'app.",
        "Премиум и настройки приложения."
    ),
    "Open advanced app options" to v(
        "Erweiterte App-Optionen offnen",
        "Ouvrir les options avancees de l'application",
        "Abrir opciones avanzadas de la aplicacion",
        "Apri opzioni avanzate dell'app",
        "Открыть расширенные параметры приложения"
    ),
    "Reminders enabled" to v("Erinnerungen aktiv", "Rappels actives", "Recordatorios activos", "Promemoria attivi", "Напоминания включены"),
    "Menu" to v("Menu", "Menu", "Menu", "Menu", "Меню"),
    "Add" to v("Hinzufugen", "Ajouter", "Anadir", "Aggiungi", "Добавить"),
    "Upgrade" to v("Upgrade", "Mettre a niveau", "Mejorar", "Aggiorna", "Улучшить"),
    "Premium yearly activated (debug)" to v(
        "Premium-Jahresplan aktiviert (Debug)",
        "Premium annuel active (debug)",
        "Premium anual activado (debug)",
        "Premium annuale attivato (debug)",
        "Premium на год активирован (debug)"
    ),
    "Premium monthly activated (debug)" to v(
        "Premium-Monatsplan aktiviert (Debug)",
        "Premium mensuel active (debug)",
        "Premium mensual activado (debug)",
        "Premium mensile attivato (debug)",
        "Premium на месяц активирован (debug)"
    ),
    "Premium lifetime activated (debug)" to v(
        "Premium-Lifetime aktiviert (Debug)",
        "Premium a vie active (debug)",
        "Premium de por vida activado (debug)",
        "Premium a vita attivato (debug)",
        "Premium навсегда активирован (debug)"
    ),
    "Purchases restored (debug)" to v(
        "Kaufe wiederhergestellt (Debug)",
        "Achats restaures (debug)",
        "Compras restauradas (debug)",
        "Acquisti ripristinati (debug)",
        "Покупки восстановлены (debug)"
    ),
    "Micro-habit" to v("Micro-Habit", "Micro-habitude", "Micro-habito", "Micro-abitudine", "Микро-привычка"),
    "Plan: Premium" to v("Plan: Premium", "Forfait : Premium", "Plan: Premium", "Piano: Premium", "Тариф: Premium"),
    "Plan: Free" to v("Plan: Kostenlos", "Forfait : Gratuit", "Plan: Gratis", "Piano: Gratuito", "Тариф: Free"),
    "No habits yet" to v("Noch keine Gewohnheiten", "Aucune habitude pour le moment", "Aun no hay habitos", "Nessuna abitudine ancora", "Пока нет привычек"),
    "Create your first habit to start building momentum." to v(
        "Erstelle deine erste Gewohnheit, um Schwung aufzubauen.",
        "Creez votre premiere habitude pour lancer l'elan.",
        "Crea tu primer habito para empezar a avanzar.",
        "Crea la tua prima abitudine per iniziare con slancio.",
        "Создайте первую привычку, чтобы начать движение."
    ),
    "Create habit" to v("Gewohnheit erstellen", "Creer une habitude", "Crear habito", "Crea abitudine", "Создать привычку"),
    "Get Premium" to v("Premium holen", "Obtenir Premium", "Obtener Premium", "Ottieni Premium", "Получить Premium"),
    "Active habits" to v("Aktive Gewohnheiten", "Habitudes actives", "Habitos activos", "Abitudini attive", "Активные привычки"),
    "Completed habits" to v("Abgeschlossene Gewohnheiten", "Habitudes terminees", "Habitos completados", "Abitudini completate", "Завершенные привычки"),
    "Archived habits" to v("Archivierte Gewohnheiten", "Habitudes archivees", "Habitos archivados", "Abitudini archiviate", "Архивные привычки"),
    "Create and select a habit to view analytics." to v(
        "Erstelle und wahle eine Gewohnheit, um Analysen zu sehen.",
        "Creez et selectionnez une habitude pour voir les analyses.",
        "Crea y selecciona un habito para ver analitica.",
        "Crea e seleziona un'abitudine per vedere le analisi.",
        "Создайте и выберите привычку, чтобы увидеть аналитику."
    ),
    "Stability score" to (
        v("Stabilitatswert", "Score de stabilite", "Puntuacion de estabilidad", "Punteggio stabilita", "Рейтинг стабильности") +
            mapOf(AppLanguage.CS to "Skore stability")
    ),
    "out of 100" to (
        v("von 100", "sur 100", "de 100", "su 100", "из 100") +
            mapOf(AppLanguage.CS to "ze 100")
    ),
    "pts this week" to (
        v("pkt diese Woche", "pts cette semaine", "pts esta semana", "pt questa settimana", "очков за неделю") +
            mapOf(AppLanguage.CS to "bodů tento týden")
    ),
    "Best: %dd" to (
        v("Rekord: %dT", "Record : %dj", "Mejor: %dd", "Record: %dg", "Рекорд: %dд") +
            mapOf(AppLanguage.CS to "Rekord: %dd")
    ),
    "30-day rate" to (
        v("30-Tage-Quote", "Taux sur 30 jours", "Tasa de 30 dias", "Tasso 30 giorni", "За 30 дней") +
            mapOf(AppLanguage.CS to "Za 30 dní")
    ),
    "7-day rate" to (
        v("7-Tage-Quote", "Taux sur 7 jours", "Tasa de 7 dias", "Tasso 7 giorni", "За 7 дней") +
            mapOf(AppLanguage.CS to "Za 7 dní")
    ),
    "↑ %d this week" to (
        v("↑ %d diese Woche", "↑ %d cette semaine", "↑ %d esta semana", "↑ %d questa settimana", "↑ %d эта неделя") +
            mapOf(AppLanguage.CS to "↑ %d tento týden")
    ),
    "%d more days — new record" to (
        v("Noch %d Tage — neuer Rekord", "Encore %d jours — nouveau record", "%d dias mas — nuevo record", "Ancora %d giorni — nuovo record", "Ещё %d дн. — новый рекорд") +
            mapOf(AppLanguage.CS to "Ještě %d dnů — nový rekord")
    ),
    "Best is %dd. At this pace you'll beat it on %s." to (
        v("Bestwert ist %dT. In diesem Tempo schaffst du es am %s.", "Le record est de %dj. A ce rythme, vous le battrez le %s.", "El mejor es %dd. A este ritmo lo superarás el %s.", "Il record e %dg. A questo ritmo lo supererai il %s.", "Рекорд %dд. При текущем темпе побьёшь %s.") +
            mapOf(AppLanguage.CS to "Rekord je %dd. Tímto tempem ho překonáš %s.")
    ),
    "Best time of day" to (
        v("Beste Tageszeit", "Meilleur moment de la journee", "Mejor momento del dia", "Momento migliore della giornata", "Лучшее время дня") +
            mapOf(AppLanguage.CS to "Nejlepší čas dne")
    ),
    "You usually complete habits at %s. Set a reminder." to (
        v("Du erledigst Gewohnheiten meist um %s. Stell eine Erinnerung ein.", "Vous terminez souvent vos habitudes a %s. Definissez un rappel.", "Sueles completar habitos a las %s. Configura un recordatorio.", "Di solito completi le abitudini alle %s. Imposta un promemoria.", "Чаще всего выполняешь в %s. Настрой напоминание.") +
            mapOf(AppLanguage.CS to "Návyky nejčastěji plníš v %s. Nastav si připomínku.")
    ),
    "Week over week" to (
        v("Woche zu Woche", "Semaine apres semaine", "Semana a semana", "Settimana su settimana", "Неделя к неделе") +
            mapOf(AppLanguage.CS to "Týden po týdnu")
    ),
    "Unlock Premium" to (
        v("Premium freischalten", "Debloquer Premium", "Desbloquear Premium", "Sblocca Premium", "Открыть Premium") +
            mapOf(AppLanguage.CS to "Odemknout Premium")
    ),
    "Best time of day\navailable in Premium" to (
        v("Beste Tageszeit\nnur in Premium", "Meilleur moment de la journee\ndisponible en Premium", "Mejor momento del dia\ndisponible en Premium", "Momento migliore della giornata\ndisponibile in Premium", "Лучшее время дня\nдоступно в Premium") +
            mapOf(AppLanguage.CS to "Nejlepší čas dne\nk dispozici v Premium")
    ),
    "Weekday consistency\navailable in Premium" to (
        v("Wochentags-Konsistenz\nnur in Premium", "Regularite par jour\ndisponible en Premium", "Consistencia por dias\ndisponible en Premium", "Costanza per giorni\ndisponibile in Premium", "Стабильность по дням\nдоступна в Premium") +
            mapOf(AppLanguage.CS to "Stabilita podle dnů\nk dispozici v Premium")
    ),
    "Week comparison\navailable in Premium" to (
        v("Wochenvergleich\nnur in Premium", "Comparaison des semaines\ndisponible en Premium", "Comparacion semanal\ndisponible en Premium", "Confronto settimane\ndisponibile in Premium", "Сравнение недель\nдоступно в Premium") +
            mapOf(AppLanguage.CS to "Porovnání týdnů\nk dispozici v Premium")
    ),
    "−2 wk" to (
        v("−2 Wo.", "−2 sem.", "−2 sem.", "−2 sett.", "−2 нед.") +
            mapOf(AppLanguage.CS to "−2 týd.")
    ),
    "Last wk" to (
        v("Letzte Wo.", "Semaine dern.", "Semana ant.", "Sett. scorsa", "Пр. нед.") +
            mapOf(AppLanguage.CS to "Min. týden")
    ),
    "This wk" to (
        v("Diese Wo.", "Cette sem.", "Esta sem.", "Questa sett.", "Эта нед.") +
            mapOf(AppLanguage.CS to "Tento týden")
    ),
    "Current streak" to v("Aktuelle Serie", "Serie actuelle", "Racha actual", "Serie attuale", "Текущая серия"),
    "Best streak" to v("Beste Serie", "Meilleure serie", "Mejor racha", "Serie migliore", "Лучшая серия"),
    "7 day completion" to v("Erfullung in 7 Tagen", "Realisation sur 7 jours", "Cumplimiento de 7 dias", "Completamento 7 giorni", "Выполнение за 7 дней"),
    "30 day completion" to v("Erfullung in 30 Tagen", "Realisation sur 30 jours", "Cumplimiento de 30 dias", "Completamento 30 giorni", "Выполнение за 30 дней"),
    "Total completions" to v("Gesamtabschlusse", "Total des validations", "Total de completados", "Completamenti totali", "Всего выполнений"),
    "Weekly completion chart" to v("Wochendiagramm", "Graphique hebdomadaire", "Grafico semanal", "Grafico settimanale", "Недельный график выполнения"),
    "Monthly progress chart" to v("Monatlicher Fortschritt", "Graphique mensuel", "Grafico mensual", "Grafico mensile", "Месячный график прогресса"),
    "Weekday consistency" to v("Wochentags-Konsistenz", "Regularite par jour", "Consistencia por dias", "Costanza nei giorni", "Стабильность по дням недели"),
    "Select or create a habit to view completion history." to v(
        "Wahle oder erstelle eine Gewohnheit, um den Verlauf zu sehen.",
        "Selectionnez ou creez une habitude pour voir l'historique.",
        "Selecciona o crea un habito para ver el historial.",
        "Seleziona o crea un'abitudine per vedere la cronologia.",
        "Выберите или создайте привычку, чтобы посмотреть историю."
    ),
    "Today" to v("Heute", "Aujourd'hui", "Hoy", "Oggi", "Сегодня"),
    "Yesterday" to (
        v("Gestern", "Hier", "Ayer", "Ieri", "Вчера") + mapOf(AppLanguage.CS to "Včera")
    ),
    "Today short" to (
        v("Heute", "Auj.", "Hoy", "Oggi", "Сег.") + mapOf(AppLanguage.CS to "Dnes")
    ),
    "Completion details" to v("Details zur Erfullung", "Details de realisation", "Detalles de cumplimiento", "Dettagli completamento", "Детали выполнения"),
    "Completed" to v("Abgeschlossen", "Termine", "Completado", "Completato", "Выполнено"),
    "Partial" to v("Teilweise", "Partiel", "Parcial", "Parziale", "Частично"),
    "Missed" to v("Verpasst", "Manque", "Perdido", "Saltato", "Пропущено"),
    "Not scheduled" to v("Nicht geplant", "Non planifie", "No programado", "Non pianificato", "Не запланировано"),
    "Future" to v("Zukunft", "Futur", "Futuro", "Futuro", "Будущее"),
    "Upcoming" to v("Bevorstehend", "A venir", "Proximo", "In arrivo", "Впереди"),
    "You are on Premium. Manage options in Premium." to v(
        "Du hast Premium. Verwalte Optionen im Premium-Bereich.",
        "Vous etes sur Premium. Gere vos options dans Premium.",
        "Estas en Premium. Gestiona opciones en Premium.",
        "Sei su Premium. Gestisci le opzioni in Premium.",
        "У вас Premium. Управляйте параметрами в разделе Премиум."
    ),
    "You are on Free. Upgrade to unlock unlimited habits." to v(
        "Du hast Free. Upgrade fur unbegrenzte Gewohnheiten.",
        "Vous etes sur Free. Passez a niveau pour des habitudes illimitees.",
        "Estas en Free. Mejora para desbloquear habitos ilimitados.",
        "Sei su Free. Passa a Premium per abitudini illimitate.",
        "У вас Free. Обновитесь, чтобы открыть безлимитные привычки."
    ),
    "Open Premium" to v("Premium offnen", "Ouvrir Premium", "Abrir Premium", "Apri Premium", "Открыть Премиум"),
    "Upgrade to Premium" to v("Auf Premium upgraden", "Passer a Premium", "Mejorar a Premium", "Passa a Premium", "Обновиться до Премиум"),
    "Free" to v("Kostenlos", "Gratuit", "Gratis", "Gratis", "Бесплатный"),
    "1 habit" to v("1 Gewohnheit", "1 habitude", "1 habito", "1 abitudine", "1 привычка"),
    "Current" to v("Aktuell", "Actuel", "Actual", "Attuale", "Текущий"),
    "Choose" to v("Wahlen", "Choisir", "Elegir", "Scegli", "Выбрать"),
    "Unlimited habits" to v("Unbegrenzte Gewohnheiten", "Habitudes illimitees", "Habitos ilimitados", "Abitudini illimitate", "Безлимитные привычки"),
    "Choose Premium" to v("Premium wahlen", "Choisir Premium", "Elegir Premium", "Scegli Premium", "Выбрать Premium"),
    "Profile and app usage overview." to v(
        "Profil und Nutzungsubersicht.",
        "Profil et apercu d'utilisation.",
        "Perfil y resumen de uso de la app.",
        "Profilo e panoramica utilizzo app.",
        "Профиль и обзор использования приложения."
    ),
    "plan_free_badge" to (
        v("Basis", "Basique", "Básico", "Base", "Базовый") +
            mapOf(AppLanguage.EN to "Basic", AppLanguage.CS to "Základní")
    ),
    "plan_free_title" to (
        v("Free", "Free", "Free", "Free", "Free") +
            mapOf(AppLanguage.EN to "Free", AppLanguage.CS to "Free")
    ),
    "plan_habits_usage" to (
        v("Aktive Gewohnheiten", "Habitudes actives", "Hábitos activos", "Abitudini attive", "Активные привычки") +
            mapOf(AppLanguage.EN to "Active habits", AppLanguage.CS to "Aktivní návyky")
    ),
    "plan_limit_reached" to (
        v("Limit erreicht", "Limite atteint", "Límite alcanzado", "Limite raggiunto", "Лимит достигнут") +
            mapOf(AppLanguage.EN to "Limit reached", AppLanguage.CS to "Limit dosažen")
    ),
    "plan_slots_free" to (
        v("{n} Plätze frei", "{n} places libres", "{n} espacios libres", "{n} slot liberi", "{n} слота свободно") +
            mapOf(AppLanguage.EN to "{n} slots free", AppLanguage.CS to "{n} místa volná")
    ),
    "manage_subscription" to (
        v("Abo verwalten →", "Gérer l'abonnement →", "Gestionar suscripción →", "Gestisci abbonamento →", "Управление подпиской →") +
            mapOf(AppLanguage.EN to "Manage subscription →", AppLanguage.CS to "Spravovat předplatné →")
    ),
    "Current plan" to v("Aktueller Plan", "Forfait actuel", "Plan actual", "Piano corrente", "Текущий тариф"),
    "One active habit" to v("Eine aktive Gewohnheit", "Une habitude active", "Un habito activo", "Una abitudine attiva", "Одна активная привычка"),
    "Non-archived habits" to v("Nicht archivierte Gewohnheiten", "Habitudes non archivees", "Habitos no archivados", "Abitudini non archiviate", "Неархивные привычки"),
    "Current habit" to v("Aktuelle Gewohnheit", "Habitude actuelle", "Habito actual", "Abitudine attuale", "Текущая привычка"),
    "Selected for tracking today" to v("Heute zum Tracking ausgewahlt", "Selectionnee pour aujourd'hui", "Seleccionado para seguimiento hoy", "Selezionata per oggi", "Выбрана для отслеживания сегодня"),
    "Not selected" to v("Nicht ausgewahlt", "Non selectionne", "No seleccionado", "Non selezionata", "Не выбрано"),
    "Appearance" to v("Darstellung", "Apparence", "Apariencia", "Aspetto", "Внешний вид"),
    "Visual style of the app." to v("Visueller Stil der App.", "Style visuel de l'application.", "Estilo visual de la app.", "Stile visivo dell'app.", "Визуальный стиль приложения."),
    "Theme" to v("Thema", "Theme", "Tema", "Tema", "Тема"),
    "System, light or dark mode" to v("System-, Hell- oder Dunkelmodus", "Mode systeme, clair ou sombre", "Modo sistema, claro u oscuro", "Sistema, chiaro o scuro", "Системный, светлый или темный режим"),
    "Language" to v("Sprache", "Langue", "Idioma", "Lingua", "Язык"),
    "App interface language." to v("Sprache der App-Oberflache.", "Langue de l'interface.", "Idioma de la interfaz.", "Lingua dell'interfaccia.", "Язык интерфейса приложения."),
    "Choose your preferred locale" to v("Bevorzugte Sprache wahlen", "Choisissez votre langue preferee", "Elige tu idioma preferido", "Scegli la lingua preferita", "Выберите предпочитаемый язык"),
    "Notifications" to v("Benachrichtigungen", "Notifications", "Notificaciones", "Notifiche", "Уведомления"),
    "Enable reminders" to v("Erinnerungen aktivieren", "Activer les rappels", "Activar recordatorios", "Abilita promemoria", "Включить напоминания"),
    "Daily reminders and nudges." to v("Tagliche Erinnerungen.", "Rappels quotidiens.", "Recordatorios diarios.", "Promemoria giornalieri.", "Ежедневные напоминания."),
    "Reminders" to v("Erinnerungen", "Rappels", "Recordatorios", "Promemoria", "Напоминания"),
    "Enable habit reminder notifications" to v("Erinnerungen fur Gewohnheiten aktivieren", "Activer les rappels d'habitude", "Activar recordatorios de habitos", "Abilita promemoria abitudini", "Включить уведомления-напоминания"),
    "Reminder time" to v("Erinnerungszeit", "Heure de rappel", "Hora del recordatorio", "Orario promemoria", "Время напоминания"),
    "Enable" to v("Aktivieren", "Activer", "Activar", "Abilita", "Включить"),
    "Daily notification time" to v("Tagliche Benachrichtigungszeit", "Heure quotidienne de notification", "Hora diaria de notificacion", "Orario notifica giornaliera", "Ежедневное время уведомления"),
    "Reminder off" to v("Erinnerung aus", "Rappel desactive", "Recordatorio desactivado", "Promemoria disattivato", "Напоминание выключено"),
    "Notifications are disabled" to v(
        "Benachrichtigungen sind deaktiviert",
        "Les notifications sont desactivees",
        "Las notificaciones estan desactivadas",
        "Le notifiche sono disattivate",
        "Уведомления отключены"
    ),
    "Enable notifications in system settings to receive habit reminders." to v(
        "Aktiviere Benachrichtigungen in den Systemeinstellungen, um Erinnerungen zu erhalten.",
        "Activez les notifications dans les parametres systeme pour recevoir des rappels.",
        "Activa las notificaciones en la configuracion del sistema para recibir recordatorios.",
        "Attiva le notifiche nelle impostazioni di sistema per ricevere i promemoria.",
        "Включите уведомления в системных настройках, чтобы получать напоминания о привычках."
    ),
    "Open Settings" to v("Einstellungen", "Ouvrir les parametres", "Abrir ajustes", "Apri impostazioni", "Открыть настройки"),
    "Unable to open app settings." to v(
        "App-Einstellungen konnen nicht geoffnet werden.",
        "Impossible d'ouvrir les parametres de l'application.",
        "No se pudieron abrir los ajustes de la aplicacion.",
        "Impossibile aprire le impostazioni dell'app.",
        "Не удалось открыть настройки приложения."
    ),
    "Habit reminder" to v("Gewohnheits-Erinnerung", "Rappel d'habitude", "Recordatorio de habito", "Promemoria abitudine", "Напоминание о привычке"),
    "Time to complete: %s" to v("Zeit fur: %s", "Il est temps de faire : %s", "Hora de completar: %s", "E ora di completare: %s", "Пора выполнить: %s"),
    "Habit reminders channel" to v(
        "Gewohnheits-Erinnerungen",
        "Rappels d'habitude",
        "Recordatorios de habitos",
        "Promemoria abitudini",
        "Напоминания о привычках"
    ),
    "Habit reminders" to (
        v(
            "Gewohnheits-Erinnerungen",
            "Rappels d'habitude",
            "Recordatorios de habitos",
            "Promemoria abitudini",
            "Напоминания о привычках"
        ) + mapOf(AppLanguage.CS to "Připomínky návyků")
    ),
    "Daily reminders channel description" to v(
        "Tagliche Erinnerungen fur Micro Habit Aufgaben",
        "Rappels quotidiens pour les taches Micro Habit",
        "Recordatorios diarios para tareas de Micro Habit",
        "Promemoria giornalieri per le attivita Micro Habit",
        "Ежедневные напоминания для задач Micro Habit"
    ),
    "Daily habit reminders" to (
        v(
            "Tagliche Erinnerungen fur Gewohnheiten",
            "Rappels quotidiens d'habitude",
            "Recordatorios diarios de habitos",
            "Promemoria quotidiani delle abitudini",
            "Ежедневные напоминания о привычках"
        ) + mapOf(AppLanguage.CS to "Denní připomínky návyků")
    ),
    "Notification permission denied. Reminders are disabled." to v(
        "Benachrichtigungsberechtigung verweigert. Erinnerungen sind deaktiviert.",
        "Autorisation de notification refusee. Les rappels sont desactives.",
        "Permiso de notificaciones denegado. Los recordatorios estan desactivados.",
        "Permesso notifiche negato. I promemoria sono disattivati.",
        "Разрешение на уведомления отклонено. Напоминания отключены."
    ),
    "Time to complete your habit!" to (
        v(
            "Zeit, deine Gewohnheit zu erledigen!",
            "Il est temps de terminer votre habitude !",
            "Hora de completar tu habito!",
            "E il momento di completare la tua abitudine!",
            "Время выполнить привычку!"
        ) + mapOf(AppLanguage.CS to "Čas splnit svůj návyk!")
    ),
    "Subscription" to v("Abonnement", "Abonnement", "Suscripcion", "Abbonamento", "Подписка"),
    "Manage Free and Premium plans." to v("Free- und Premium-Plane verwalten.", "Gerer les offres Free et Premium.", "Gestiona planes Free y Premium.", "Gestisci i piani Free e Premium.", "Управляйте тарифами Free и Premium."),
    "Manage subscription" to v("Abo verwalten", "Gerer l'abonnement", "Gestionar suscripcion", "Gestisci abbonamento", "Управление подпиской"),
    "Home screen widgets" to (
        v("Widgets", "Widgets de l'écran d'accueil", "Widgets de pantalla de inicio", "Widget schermata home", "Виджеты для рабочего стола") +
            mapOf(AppLanguage.EN to "Home screen widgets", AppLanguage.CS to "Widgety")
    ),
    "Premium active: unlimited habits" to v("Premium aktiv: unbegrenzte Gewohnheiten", "Premium actif : habitudes illimitees", "Premium activo: habitos ilimitados", "Premium attivo: abitudini illimitate", "Premium активен: безлимитные привычки"),
    "3 habits" to (
        v("3 Gewohnheiten", "3 habitudes", "3 hábitos", "3 abitudini", "3 привычки") +
            mapOf(AppLanguage.EN to "3 habits", AppLanguage.CS to "3 návyky")
    ),
    "Next billing" to (
        v("Nächste Zahlung", "Prochaine facturation", "Próximo cobro", "Prossimo addebito", "Следующее списание") +
            mapOf(AppLanguage.EN to "Next billing", AppLanguage.CS to "Příští platba")
    ),
    "Subscription cancelled" to (
        v("Abo gekündigt", "Abonnement annulé", "Suscripción cancelada", "Abbonamento annullato", "Подписка отменена") +
            mapOf(AppLanguage.EN to "Subscription cancelled", AppLanguage.CS to "Předplatné zrušeno")
    ),
    "Until expiry" to (
        v("Bis Ablauf", "Jusqu'à expiration", "Hasta el vencimiento", "Fino alla scadenza", "До истечения") +
            mapOf(AppLanguage.EN to "Until expiry", AppLanguage.CS to "Do vypršení")
    ),
    "No charge will happen." to (
        v("Es erfolgt keine Abbuchung.", "Aucun débit n'aura lieu.", "No se realizará ningún cobro.", "Non verrà effettuato alcun addebito.", "Списание не произойдёт.") +
            mapOf(AppLanguage.EN to "No charge will happen.", AppLanguage.CS to "Žádná platba neproběhne.")
    ),
    "Premium active until" to (
        v("Premium aktiv bis", "Premium actif jusqu'au", "Premium activo hasta", "Premium attivo fino al", "Premium активен до") +
            mapOf(AppLanguage.EN to "Premium active until", AppLanguage.CS to "Premium aktivní do")
    ),
    "Active plan" to (
        v("Aktiver Plan", "Plan actif", "Plan activo", "Piano attivo", "Активный план") +
            mapOf(AppLanguage.EN to "Active plan", AppLanguage.CS to "Aktivní plán")
    ),
    "Included in Premium" to (
        v("In Premium enthalten", "Inclus dans Premium", "Incluido en Premium", "Incluso in Premium", "Включено в Premium") +
            mapOf(AppLanguage.EN to "Included in Premium", AppLanguage.CS to "Součást Premium")
    ),
    "Changed your mind?" to (
        v("Anders überlegt?", "Vous avez changé d'avis ?", "¿Cambiaste de opinión?", "Hai cambiato idea?", "Передумали?") +
            mapOf(AppLanguage.EN to "Changed your mind?", AppLanguage.CS to "Rozmyslel sis to?")
    ),
    "Renew before %s and nothing changes." to (
        v("Erneuere vor %s und alles bleibt gleich.", "Renouvelez avant le %s et rien ne change.", "Renueva antes de %s y nada cambiará.", "Rinnova entro %s e non cambia nulla.", "Возобновите до %s и ничего не изменится.") +
            mapOf(AppLanguage.EN to "Renew before %s and nothing changes.", AppLanguage.CS to "Obnov do %s a nic se nezmění.")
    ),
    "Renew subscription" to (
        v("Abo erneuern", "Renouveler l'abonnement", "Renovar suscripción", "Rinnova abbonamento", "Возобновить подписку") +
            mapOf(AppLanguage.EN to "Renew subscription", AppLanguage.CS to "Obnovit předplatné")
    ),
    "After cancellation Premium stays active until %s. No charge will happen. Then your account switches to Free." to (
        v("Nach der Kündigung bleibt Premium bis %s aktiv. Es erfolgt keine Abbuchung. Danach wechselt dein Konto auf Free.", "Après annulation Premium reste actif jusqu'au %s. Aucun débit n'aura lieu. Ensuite le compte passe au plan Free.", "Tras cancelar, Premium sigue activo hasta %s. No habrá cobro. Después tu cuenta pasará a Free.", "Dopo l'annullamento Premium resta attivo fino al %s. Nessun addebito. Poi l'account passerà al piano Free.", "После отмены Premium остаётся активным до %s. Списание не произойдёт. Затем аккаунт перейдёт на Free.") +
            mapOf(AppLanguage.EN to "After cancellation Premium stays active until %s. No charge will happen. Then your account switches to Free.", AppLanguage.CS to "Po zrušení zůstane Premium aktivní do %s. Platba neproběhne. Poté se účet přepne na Free.")
    ),
    "Debug" to (
        v("Debug", "Debug", "Debug", "Debug", "Debug") +
            mapOf(AppLanguage.EN to "Debug", AppLanguage.CS to "Debug")
    ),
    "Switch to Free plan" to (
        v("Auf Free wechseln", "Passer à Free", "Cambiar a Free", "Passa a Free", "Переключить на Free план") +
            mapOf(AppLanguage.EN to "Switch to Free plan", AppLanguage.CS to "Přepnout na Free")
    ),
    "Cancel subscription?" to (
        v("Abo kündigen?", "Annuler l'abonnement ?", "¿Cancelar suscripción?", "Annullare l'abbonamento?", "Отменить подписку?") +
            mapOf(AppLanguage.EN to "Cancel subscription?", AppLanguage.CS to "Zrušit předplatné?")
    ),
    "Cancel subscription" to (
        v("Abo kündigen", "Annuler l'abonnement", "Cancelar suscripción", "Annulla abbonamento", "Отменить подписку") +
            mapOf(AppLanguage.EN to "Cancel subscription", AppLanguage.CS to "Zrušit předplatné")
    ),
    "Active until" to (
        v("Aktiv bis", "Actif jusqu'au", "Activo hasta", "Attivo fino al", "Активен до") +
            mapOf(AppLanguage.EN to "Active until", AppLanguage.CS to "Aktivní do")
    ),
    "Charge" to (
        v("Abbuchung", "Débit", "Cobro", "Addebito", "Списание") +
            mapOf(AppLanguage.EN to "Charge", AppLanguage.CS to "Platba")
    ),
    "No charge ✓" to (
        v("Keine Abbuchung ✓", "Aucun débit ✓", "Sin cobro ✓", "Nessun addebito ✓", "Списание не произойдёт ✓") +
            mapOf(AppLanguage.EN to "No charge ✓", AppLanguage.CS to "Žádná platba ✓")
    ),
    "After %s you lose:" to (
        v("Nach %s verlierst du:", "Après le %s vous perdrez :", "Después de %s perderás:", "Dopo %s perderai:", "После %s вы потеряете:") +
            mapOf(AppLanguage.EN to "After %s you lose:", AppLanguage.CS to "Po %s ztratíš:")
    ),
    "More than 3 active habits" to (
        v("Mehr als 3 aktive Gewohnheiten", "Plus de 3 habitudes actives", "Más de 3 hábitos activos", "Più di 3 abitudini attive", "Более 3 активных привычек") +
            mapOf(AppLanguage.EN to "More than 3 active habits", AppLanguage.CS to "Více než 3 aktivní návyky")
    ),
    "Keep Premium" to (
        v("Premium behalten", "Garder Premium", "Mantener Premium", "Mantieni Premium", "Оставить Premium") +
            mapOf(AppLanguage.EN to "Keep Premium", AppLanguage.CS to "Ponechat Premium")
    ),
    "Confirm cancellation" to (
        v("Kündigung bestätigen", "Confirmer l'annulation", "Confirmar cancelación", "Conferma annullamento", "Подтвердить отмену") +
            mapOf(AppLanguage.EN to "Confirm cancellation", AppLanguage.CS to "Potvrdit zrušení")
    ),
    "Unlock Premium" to (
        v("Premium freischalten", "Débloquer Premium", "Desbloquear Premium", "Sblocca Premium", "Разблокируй Premium") +
            mapOf(AppLanguage.EN to "Unlock Premium", AppLanguage.CS to "Odemkni Premium")
    ),
    "You reached the limit of 3 habits." to (
        v("Du hast das Limit von 3 Gewohnheiten erreicht.", "Vous avez atteint la limite de 3 habitudes.", "Has alcanzado el límite de 3 hábitos.", "Hai raggiunto il limite di 3 abitudini.", "Вы достигли лимита в 3 привычки.") +
            mapOf(AppLanguage.EN to "You reached the limit of 3 habits.", AppLanguage.CS to "Dosáhl jsi limitu 3 návyků.")
    ),
    "Unlock advanced analytics." to (
        v("Schalte erweiterte Analysen frei.", "Débloquez les analyses avancées.", "Desbloquea analíticas avanzadas.", "Sblocca analisi avanzate.", "Откройте расширенную аналитику.") +
            mapOf(AppLanguage.EN to "Unlock advanced analytics.", AppLanguage.CS to "Odemkni pokročilou analytiku.")
    ),
    "Add widgets on your home screen." to (
        v("Füge Widgets zum Startbildschirm hinzu.", "Ajoutez des widgets à votre écran d'accueil.", "Añade widgets a tu pantalla de inicio.", "Aggiungi widget alla schermata iniziale.", "Добавьте виджеты на рабочий стол.") +
            mapOf(AppLanguage.EN to "Add widgets on your home screen.", AppLanguage.CS to "Přidej widgety na plochu.")
    ),
    "Get access to all features." to (
        v("Erhalte Zugriff auf alle Funktionen.", "Accédez à toutes les fonctionnalités.", "Obtén acceso a todas las funciones.", "Ottieni accesso a tutte le funzionalità.", "Получите доступ ко всем возможностям.") +
            mapOf(AppLanguage.EN to "Get access to all features.", AppLanguage.CS to "Získej přístup ke všem funkcím.")
    ),
    "Upgrade to Premium to unlock all features." to (
        v("Upgrade auf Premium, um alle Funktionen freizuschalten.", "Passez à Premium pour débloquer toutes les fonctionnalités.", "Mejora a Premium para desbloquear todas las funciones.", "Passa a Premium per sbloccare tutte le funzionalità.", "Перейдите на Premium, чтобы открыть все возможности.") +
            mapOf(AppLanguage.EN to "Upgrade to Premium to unlock all features.", AppLanguage.CS to "Přejděte na Premium a odemkněte všechny funkce.")
    ),
    "year" to (
        v("Jahr", "an", "año", "anno", "год") +
            mapOf(AppLanguage.EN to "year", AppLanguage.CS to "rok")
    ),
    "month" to (
        v("Monat", "mois", "mes", "mese", "месяц") +
            mapOf(AppLanguage.EN to "month", AppLanguage.CS to "měsíc")
    ),
    "Track as many goals as you want" to (
        v("Verfolge so viele Ziele wie du willst", "Suivez autant d'objectifs que vous voulez", "Sigue tantas metas como quieras", "Tieni traccia di tutti gli obiettivi che vuoi", "Отслеживай сколько угодно целей") +
            mapOf(AppLanguage.EN to "Track as many goals as you want", AppLanguage.CS to "Sleduj tolik cílů, kolik chceš")
    ),
    "Progress right on your home screen" to (
        v("Fortschritt direkt auf dem Startbildschirm", "Progression directement sur l'écran d'accueil", "Progreso directo en la pantalla de inicio", "Progressi direttamente nella schermata iniziale", "Прогресс прямо на рабочем столе") +
            mapOf(AppLanguage.EN to "Progress right on your home screen", AppLanguage.CS to "Pokrok přímo na ploše")
    ),
    "Patterns, consistency, and best time insights" to (
        v("Muster, Konstanz und beste Zeiten", "Tendances, régularité et meilleur moment", "Patrones, constancia y mejor hora", "Pattern, costanza e orario migliore", "Паттерны, стабильность и лучшее время") +
            mapOf(AppLanguage.EN to "Patterns, consistency, and best time insights", AppLanguage.CS to "Vzorce, stabilita a nejlepší čas")
    ),
    "Patterns, consistency, best time insights" to (
        v("Muster, Konstanz, beste Zeiten", "Tendances, régularité, meilleur moment", "Patrones, constancia, mejor hora", "Pattern, costanza, orario migliore", "Паттерны, стабильность, лучшее время") +
            mapOf(AppLanguage.EN to "Patterns, consistency, best time insights", AppLanguage.CS to "Vzorce, stabilita, nejlepší čas")
    ),
    "Response within 24 hours" to (
        v("Antwort innerhalb von 24 Stunden", "Réponse sous 24 heures", "Respuesta en 24 horas", "Risposta entro 24 ore", "Ответ в течение 24 часов") +
            mapOf(AppLanguage.EN to "Response within 24 hours", AppLanguage.CS to "Odpověď do 24 hodin")
    ),
    "Best value" to (
        v("Bestes Angebot", "Meilleur choix", "Mejor opción", "Miglior scelta", "Лучший выбор") +
            mapOf(AppLanguage.EN to "Best value", AppLanguage.CS to "Nejlepší volba")
    ),
    "Forever, no recurring payments" to (
        v("Für immer, ohne weitere Zahlungen", "À vie, sans paiements récurrents", "Para siempre, sin pagos recurrentes", "Per sempre, senza pagamenti ricorrenti", "Навсегда, без платежей") +
            mapOf(AppLanguage.EN to "Forever, no recurring payments", AppLanguage.CS to "Navždy, bez opakovaných plateb")
    ),
    "plan_lifetime_subtitle" to (
        v("einmalig, für immer", "paiement unique, pour toujours", "un solo pago, para siempre", "una volta, per sempre", "разово, навсегда") +
            mapOf(AppLanguage.EN to "one-time, forever", AppLanguage.CS to "jednorázově, navždy")
    ),
    "paywall_savings_badge" to (
        v("48% sparen", "Économisez 48%", "Ahorra 48%", "Risparmia il 48%", "Экономия 48%") +
            mapOf(AppLanguage.EN to "Save 48%", AppLanguage.CS to "Úspora 48%")
    ),
    "Auto-renewal. Cancel anytime." to (
        v("Automatische Verlängerung. Jederzeit kündbar.", "Renouvellement automatique. Annulation à tout moment.", "Renovación automática. Cancela cuando quieras.", "Rinnovo automatico. Annulla quando vuoi.", "Автопродление. Отмена в любое время.") +
            mapOf(AppLanguage.EN to "Auto-renewal. Cancel anytime.", AppLanguage.CS to "Automatické obnovení. Zrušení kdykoli.")
    ),
    "Terms · Privacy" to (
        v("Bedingungen · Datenschutz", "Conditions · Confidentialité", "Términos · Privacidad", "Termini · Privacy", "Условия · Конфиденциальность") +
            mapOf(AppLanguage.EN to "Terms · Privacy", AppLanguage.CS to "Podmínky · Ochrana soukromí")
    ),
    "Auto-renewal. Cancel anytime. Terms · Privacy" to (
        v("Automatische Verlängerung. Jederzeit kündbar. Bedingungen · Datenschutz", "Renouvellement automatique. Annulation à tout moment. Conditions · Confidentialité", "Renovación automática. Cancela cuando quieras. Términos · Privacidad", "Rinnovo automatico. Annulla quando vuoi. Termini · Privacy", "Автопродление. Отмена в любое время. Terms · Privacy") +
            mapOf(AppLanguage.EN to "Auto-renewal. Cancel anytime. Terms · Privacy", AppLanguage.CS to "Automatické obnovení. Zrušení kdykoli. Podmínky · Ochrana soukromí")
    ),
    "Free plan: one active habit" to v("Free-Plan: eine aktive Gewohnheit", "Plan Free : une habitude active", "Plan Free: un habito activo", "Piano Free: una abitudine attiva", "План Free: одна активная привычка"),
    "Free plan: 3 active habits" to (
        v("Free-Plan: 3 aktive Gewohnheiten", "Plan Free : 3 habitudes actives", "Plan Free: 3 habitos activos", "Piano Free: 3 abitudini attive", "План Free: 3 активные привычки") +
            mapOf(AppLanguage.CS to "Free plán: 3 aktivní návyky")
    ),
    "Data & Privacy" to v("Daten & Datenschutz", "Donnees et confidentialite", "Datos y privacidad", "Dati e privacy", "Данные и приватность"),
    "Data" to v("Daten", "Donnees", "Datos", "Dati", "Данные"),
    "Control your data and account lifecycle." to v(
        "Kontrolliere deine Daten und dein Konto.",
        "Controlez vos donnees et le cycle du compte.",
        "Controla tus datos y ciclo de cuenta.",
        "Controlla i dati e il ciclo del tuo account.",
        "Управляйте данными и жизненным циклом аккаунта."
    ),
    "Export data" to v("Daten exportieren", "Exporter les donnees", "Exportar datos", "Esporta dati", "Экспорт данных"),
    "Save tasks and progress as JSON" to v(
        "Aufgaben und Fortschritt als JSON speichern",
        "Sauvegarder taches et progression en JSON",
        "Guardar tareas y progreso en JSON",
        "Salva attivita e progresso in JSON",
        "Сохранить задачи и прогресс в JSON"
    ),
    "Data exported: %s" to v("Daten exportiert: %s", "Donnees exportees : %s", "Datos exportados: %s", "Dati esportati: %s", "Данные экспортированы: %s"),
    "Export failed: %s" to v("Export fehlgeschlagen: %s", "Echec de l'export : %s", "Error de exportacion: %s", "Esportazione fallita: %s", "Ошибка экспорта: %s"),
    "Unknown error" to v("Unbekannter Fehler", "Erreur inconnue", "Error desconocido", "Errore sconosciuto", "Неизвестная ошибка"),
    "Reset progress" to v("Fortschritt zurucksetzen", "Reinitialiser la progression", "Restablecer progreso", "Reimposta progressi", "Сбросить прогресс"),
    "Clear completion history, keep habits" to v(
        "Abschlussverlauf loschen, Gewohnheiten behalten",
        "Effacer l'historique, conserver les habitudes",
        "Borrar historial de completado, mantener habitos",
        "Cancella cronologia completamenti, mantieni abitudini",
        "Очистить историю выполнения, оставить привычки"
    ),
    "Delete account" to v("Konto loschen", "Supprimer le compte", "Eliminar cuenta", "Elimina account", "Удалить аккаунт"),
    "Danger zone" to v("Gefahrenzone", "Zone de danger", "Zona de riesgo", "Zona pericolosa", "Опасная зона"),
    "Remove all habits and settings" to v("Alle Gewohnheiten und Einstellungen entfernen", "Supprimer toutes les habitudes et parametres", "Eliminar todos los habitos y ajustes", "Rimuovi tutte le abitudini e impostazioni", "Удалить все привычки и настройки"),
    "Support" to v("Support", "Support", "Soporte", "Supporto", "Поддержка"),
    "Get help and send feedback." to v("Hilfe erhalten und Feedback senden.", "Obtenir de l'aide et envoyer des retours.", "Obten ayuda y envia comentarios.", "Ottieni aiuto e invia feedback.", "Получить помощь и отправить отзыв."),
    "Help center" to v("Hilfezentrum", "Centre d'aide", "Centro de ayuda", "Centro assistenza", "Центр помощи"),
    "Quick guidance for app features" to v("Kurze Hilfe zu Funktionen", "Guide rapide des fonctions", "Guia rapida de funciones", "Guida rapida alle funzioni", "Краткая справка по функциям"),
    "Help center is not available in debug build." to v(
        "Hilfezentrum ist im Debug-Build nicht verfugbar.",
        "Le centre d'aide n'est pas disponible en build debug.",
        "El centro de ayuda no esta disponible en build debug.",
        "Il centro assistenza non e disponibile nella build debug.",
        "Центр помощи недоступен в debug-сборке."
    ),
    "Contact support" to v("Support kontaktieren", "Contacter le support", "Contactar soporte", "Contatta il supporto", "Связаться с поддержкой"),
    "Send us your feedback" to v("Sende uns dein Feedback", "Envoyez-nous vos retours", "Envianos tus comentarios", "Inviaci il tuo feedback", "Отправьте нам отзыв"),
    "Support contact will be connected in the next build." to v(
        "Kontakt zum Support wird in der nachsten Version verfugbar sein.",
        "Le contact support sera ajoute dans la prochaine version.",
        "El contacto de soporte se conectara en la proxima build.",
        "Il contatto supporto sara attivato nella prossima build.",
        "Контакт с поддержкой будет подключен в следующей сборке."
    ),
    "Select theme" to v("Thema auswahlen", "Choisir le theme", "Seleccionar tema", "Seleziona tema", "Выбрать тему"),
    "System" to v("System", "Systeme", "Sistema", "Sistema", "Система"),
    "Light" to v("Hell", "Clair", "Claro", "Chiaro", "Светлая"),
    "Dark" to v("Dunkel", "Sombre", "Oscuro", "Scuro", "Темная"),
    "Select language" to v("Sprache auswahlen", "Choisir la langue", "Seleccionar idioma", "Seleziona lingua", "Выбрать язык"),
    "Reset progress?" to v("Fortschritt zurucksetzen?", "Reinitialiser la progression ?", "Restablecer progreso?", "Reimpostare i progressi?", "Сбросить прогресс?"),
    "This will remove all completion history and keep your habits." to v(
        "Dadurch wird der Verlauf geloscht, Gewohnheiten bleiben.",
        "Cela supprimera l'historique en conservant les habitudes.",
        "Esto eliminara el historial y mantendra los habitos.",
        "Questo rimuovera la cronologia mantenendo le abitudini.",
        "Это удалит всю историю выполнения и сохранит привычки."
    ),
    "Progress reset." to v("Fortschritt zuruckgesetzt.", "Progression reinitialisee.", "Progreso restablecido.", "Progressi reimpostati.", "Прогресс сброшен."),
    "Reset" to v("Zurucksetzen", "Reinitialiser", "Restablecer", "Reimposta", "Сброс"),
    "Cancel" to v("Abbrechen", "Annuler", "Cancelar", "Annulla", "Отмена"),
    "Save" to (
        v("Speichern", "Enregistrer", "Guardar", "Salva", "Сохранить") +
            mapOf(AppLanguage.CS to "Uložit")
    ),
    "edit" to (
        v("andern", "modifier", "editar", "modifica", "изменить") +
            mapOf(AppLanguage.CS to "upravit")
    ),
    "Backspace" to (
        v("Loschen", "Supprimer", "Borrar", "Elimina", "Удалить") +
            mapOf(AppLanguage.CS to "Smazat")
    ),
    "Delete account?" to v("Konto loschen?", "Supprimer le compte ?", "Eliminar cuenta?", "Eliminare account?", "Удалить аккаунт?"),
    "Delete habit?" to v("Gewohnheit loschen?", "Supprimer l'habitude ?", "Eliminar habito?", "Eliminare abitudine?", "Удалить привычку?"),
    "Congratulations! Habit completed." to v(
        "Gluckwunsch! Gewohnheit abgeschlossen.",
        "Felicitations ! L'habitude est terminee.",
        "Felicidades! Habito completado.",
        "Complimenti! Abitudine completata.",
        "Поздравляем! Привычка завершена."
    ),
    "Continue habit" to v("Gewohnheit fortsetzen", "Continuer l'habitude", "Continuar habito", "Continua abitudine", "Продолжить привычку"),
    "Choose how to continue this habit." to v(
        "Wahle, wie du diese Gewohnheit fortsetzen mochtest.",
        "Choisissez comment continuer cette habitude.",
        "Elige como continuar este habito.",
        "Scegli come continuare questa abitudine.",
        "Выберите, как продолжить эту привычку."
    ),
    "Choose a new end date" to v(
        "Neues Enddatum wahlen",
        "Choisir une nouvelle date de fin",
        "Elegir una nueva fecha de finalizacion",
        "Scegli una nuova data di fine",
        "Выбрать новую дату завершения"
    ),
    "Make habit indefinite" to v(
        "Gewohnheit unbegrenzt machen",
        "Rendre l'habitude illimitee",
        "Hacer el habito indefinido",
        "Rendi l'abitudine senza scadenza",
        "Сделать привычку бессрочной"
    ),
    "This will permanently delete this habit and its completion history." to v(
        "Diese Gewohnheit und ihr Verlauf werden dauerhaft geloscht.",
        "Cette habitude et son historique seront supprimes definitivement.",
        "Este habito y su historial se eliminaran de forma permanente.",
        "Questa abitudine e la sua cronologia saranno eliminate definitivamente.",
        "Эта привычка и история ее выполнения будут удалены навсегда."
    ),
    "This action removes all habits, progress and settings." to v(
        "Diese Aktion entfernt alle Gewohnheiten, Fortschritte und Einstellungen.",
        "Cette action supprime toutes les habitudes, progres et parametres.",
        "Esta accion elimina habitos, progreso y ajustes.",
        "Questa azione rimuove abitudini, progressi e impostazioni.",
        "Это действие удалит все привычки, прогресс и настройки."
    ),
    "Account data deleted." to v("Kontodaten geloscht.", "Donnees du compte supprimees.", "Datos de la cuenta eliminados.", "Dati account eliminati.", "Данные аккаунта удалены."),
    "Delete" to v("Loschen", "Supprimer", "Eliminar", "Elimina", "Удалить"),
    "Included with Premium" to v("In Premium enthalten", "Inclus avec Premium", "Incluido con Premium", "Incluso con Premium", "Включено в Премиум"),
    "Unlimited active habits" to v("Unbegrenzte aktive Gewohnheiten", "Habitudes actives illimitees", "Habitos activos ilimitados", "Abitudini attive illimitate", "Безлимит активных привычек"),
    "Advanced analytics and consistency views" to v("Erweiterte Analysen und Konsistenzansichten", "Analyses avancees et vues de regularite", "Analitica avanzada y vistas de consistencia", "Analisi avanzate e viste di costanza", "Расширенная аналитика и показатели стабильности"),
    "Priority support and early access updates" to v("Priorisierter Support und fruher Zugang", "Support prioritaire et acces anticipe", "Soporte prioritario y acceso anticipado", "Supporto prioritario e accesso anticipato", "Приоритетная поддержка и ранний доступ к обновлениям"),
    "Future cross-device sync support" to v("Zukunftige gerateubergreifende Synchronisierung", "Synchronisation multi-appareils a venir", "Sincronizacion entre dispositivos en el futuro", "Supporto sync multi-dispositivo in futuro", "Будущая синхронизация между устройствами"),
    "Plan comparison" to v("Tarifvergleich", "Comparaison des offres", "Comparacion de planes", "Confronto piani", "Сравнение планов"),
    "Feature" to v("Funktion", "Fonctionnalite", "Funcion", "Funzionalita", "Функция"),
    "More than one habit" to v("Mehr als eine Gewohnheit", "Plus d'une habitude", "Mas de un habito", "Piu di un'abitudine", "Больше одной привычки"),
    "More than three habits" to (
        v("Mehr als drei Gewohnheiten", "Plus de trois habitudes", "Mas de tres habitos", "Piu di tre abitudini", "Больше трех привычек") +
            mapOf(AppLanguage.CS to "Více než tři návyky")
    ),
    "Advanced analytics" to v("Erweiterte Analysen", "Analyses avancees", "Analitica avanzada", "Analisi avanzate", "Расширенная аналитика"),
    "Priority support" to v("Priorisierter Support", "Support prioritaire", "Soporte prioritario", "Supporto prioritario", "Приоритетная поддержка"),
    "Lifetime" to (
        v("Lebenslang", "A vie", "De por vida", "A vita", "Навсегда") +
            mapOf(AppLanguage.CS to "Doživotně")
    ),
    "Monthly" to v("Monatlich", "Mensuel", "Mensual", "Mensile", "Ежемесячно"),
    "Flexible monthly billing" to v("Flexible monatliche Abrechnung", "Facturation mensuelle flexible", "Facturacion mensual flexible", "Fatturazione mensile flessibile", "Гибкая ежемесячная оплата"),
    "Yearly" to v("Jahrlich", "Annuel", "Anual", "Annuale", "Ежегодно"),
    "Pay once — keep it forever." to (
        v("Einmal zahlen, fur immer nutzen.", "Payez une fois, gardez-le pour toujours.", "Paga una vez y usalo para siempre.", "Paga una volta e usalo per sempre.", "Платишь один раз — пользуешься всегда.") +
            mapOf(AppLanguage.CS to "Zaplať jednou a používej navždy.")
    ),
    "Equivalent to \$2.08 / month" to (
        v("Entspricht \$2.08 / Monat", "Equivalent a \$2.08 / mois", "Equivale a \$2.08 / mes", "Equivalente a \$2.08 / mese", "Эквивалент \$2.08 / месяц") +
            mapOf(AppLanguage.CS to "Ekvivalent \$2.08 / měsíc")
    ),
    "Equivalent to \$3.33 / month" to v("Entspricht \$3.33 / Monat", "Equivalent a \$3.33 / mois", "Equivale a \$3.33 / mes", "Equivalente a \$3.33 / mese", "Эквивалент \$3.33 / месяц"),
    "One-time forever · \$2.08/mo equivalent over 2 years" to (
        v(
            "Einmalig fur immer · \$2.08/Monat uber 2 Jahre",
            "Paiement unique a vie · \$2.08/mois sur 2 ans",
            "Pago unico para siempre · \$2.08/mes en 2 anos",
            "Una volta per sempre · \$2.08/mese su 2 anni",
            "Один раз навсегда · \$2.08/мес в эквиваленте за 2 года"
        ) + mapOf(AppLanguage.CS to "Jednorázově navždy · \$2.08/měs v přepočtu za 2 roky")
    ),
    "Recommended" to v("Empfohlen", "Recommande", "Recomendado", "Consigliato", "Рекомендуется"),
    "Premium active" to v("Premium aktiv", "Premium actif", "Premium activo", "Premium attivo", "Премиум активен"),
    "Continue with %s" to v("Fortfahren mit %s", "Continuer avec %s", "Continuar con %s", "Continua con %s", "Продолжить с %s"),
    "Restore purchase" to v("Kauf wiederherstellen", "Restaurer l'achat", "Restaurar compra", "Ripristina acquisto", "Восстановить покупку"),
    "plan_switcher_title" to (
        v("Plan ändern", "Changer de forfait", "Cambiar plan", "Cambia piano", "Сменить план") +
            mapOf(AppLanguage.EN to "Change plan", AppLanguage.CS to "Změnit plán")
    ),
    "plan_switcher_credit" to (
        v("Aktives Abo wird angerechnet", "L'abonnement actuel sera pris en compte", "La suscripción actual se acreditará", "L'abbonamento attuale sarà accreditato", "Текущая подписка зачтётся") +
            mapOf(AppLanguage.EN to "Current subscription will be credited", AppLanguage.CS to "Aktuální předplatné bude zohledněno")
    ),
    "plan_switcher_unavailable" to (
        v("Gesperrt", "Verrouillé", "Bloqueado", "Bloccato", "Недоступно") +
            mapOf(AppLanguage.EN to "Locked", AppLanguage.CS to "Zamčeno")
    ),
    "plan_switcher_select_hint" to (
        v("Wählen Sie einen Plan, um fortzufahren", "Choisissez un forfait pour continuer", "Elige un plan para continuar", "Scegli un piano per continuare", "Выберите план для продолжения") +
            mapOf(AppLanguage.EN to "Select a plan to continue", AppLanguage.CS to "Vyberte plán pro pokračování")
    ),
    "plan_monthly" to (
        v("Monatlich", "Mensuel", "Mensual", "Mensile", "Monthly") +
            mapOf(AppLanguage.EN to "Monthly", AppLanguage.CS to "Měsíčně")
    ),
    "plan_yearly" to (
        v("Jährlich", "Annuel", "Anual", "Annuale", "Yearly") +
            mapOf(AppLanguage.EN to "Yearly", AppLanguage.CS to "Ročně")
    ),
    "plan_lifetime" to (
        v("Lifetime", "À vie", "De por vida", "A vita", "Lifetime") +
            mapOf(AppLanguage.EN to "Lifetime", AppLanguage.CS to "Doživotně")
    ),
    "plan_lifetime_yours" to (
        v("Ihr Plan", "Votre forfait", "Tu plan", "Il tuo piano", "Ваш план") +
            mapOf(AppLanguage.EN to "Your plan", AppLanguage.CS to "Váš plán")
    ),
    "plan_lifetime_forever_badge" to (
        v("Für immer", "Pour toujours", "Para siempre", "Per sempre", "Навсегда") +
            mapOf(AppLanguage.EN to "Forever", AppLanguage.CS to "Navždy")
    ),
    "plan_lifetime_locked" to (
        v("Sie haben Premium für immer. Planwechsel nicht verfügbar.", "Vous avez Premium pour toujours. Le changement de forfait n'est pas disponible.", "Tienes Premium para siempre. Cambiar de plan no está disponible.", "Hai Premium per sempre. Il cambio piano non è disponibile.", "У вас Premium навсегда. Переход на другой план недоступен.") +
            mapOf(AppLanguage.EN to "You have Premium forever. Switching plans is not available.", AppLanguage.CS to "Máte Premium navždy. Změna plánu není dostupná.")
    ),
    "billing_never" to (
        v("Nie ✓", "Jamais ✓", "Nunca ✓", "Mai ✓", "Никогда ✓") +
            mapOf(AppLanguage.EN to "Never ✓", AppLanguage.CS to "Nikdy ✓")
    ),
    "billing_no_recurring" to (
        v("Wiederkehrende Zahlung", "Paiement récurrent", "Cobro recurrente", "Addebito ricorrente", "Повторное списание") +
            mapOf(AppLanguage.EN to "No recurring billing", AppLanguage.CS to "Opakovaná platba")
    ),
    "cancel_sheet_title" to (
        v("Abo kündigen?", "Annuler l'abonnement ?", "¿Cancelar suscripción?", "Annullare l'abbonamento?", "Отменить подписку?") +
            mapOf(AppLanguage.EN to "Cancel subscription?", AppLanguage.CS to "Zrušit předplatné?")
    ),
    "cancel_loses_title" to (
        v("Ab {date} verlieren Sie:", "À partir du {date}, vous perdez :", "Desde {date} perderás:", "Dal {date} perderai:", "С {date} станет недоступно:") +
            mapOf(AppLanguage.EN to "From {date} you lose:", AppLanguage.CS to "Od {date} ztratíte:")
    ),
    "cancel_confirm_btn" to (
        v("Kündigung bestätigen", "Confirmer l'annulation", "Confirmar cancelación", "Conferma annullamento", "Подтвердить отмену") +
            mapOf(AppLanguage.EN to "Confirm cancellation", AppLanguage.CS to "Potvrdit zrušení")
    ),
    "switch_to_plan" to (
        v("Zu {plan} wechseln", "Passer à {plan}", "Cambiar a {plan}", "Passa a {plan}", "Перейти на {plan}") +
            mapOf(AppLanguage.EN to "Switch to {plan}", AppLanguage.CS to "Přejít na {plan}")
    ),
    "hint_yearly_upgrade" to (
        v("Angerechnet · Nächste Zahlung {date}", "Crédité · Prochain paiement {date}", "Acreditado · Próximo cobro {date}", "Accreditato · Prossimo addebito {date}", "Зачтётся · Новое списание {date}") +
            mapOf(AppLanguage.EN to "Credited · Next billing {date}", AppLanguage.CS to "Zohledněno · Příští platba {date}")
    ),
    "hint_lifetime_upgrade" to (
        v("Jetzt \$59.99 zahlen · Keine weiteren Abbuchungen", "Payez \$59.99 maintenant · Plus de facturation", "Paga \$59.99 ahora · No habrá más cobros", "Paga \$59.99 ora · Nessun altro addebito", "Оплата \$59.99 сейчас · Списаний больше не будет") +
            mapOf(AppLanguage.EN to "Pay \$59.99 now · No more billing", AppLanguage.CS to "Nyní zaplatit \$59.99 · Žádné další platby")
    ),
    "hint_monthly_downgrade" to (
        v("Wechsel zu Monatlich ab {date}", "Passage à Mensuel à partir du {date}", "Cambio a Monthly desde {date}", "Passaggio a Monthly dal {date}", "Переход на Monthly с {date}") +
            mapOf(AppLanguage.EN to "Switch to Monthly from {date}", AppLanguage.CS to "Přechod na Měsíčně od {date}")
    ),
    "cancelled_renew_subtitle" to (
        v("Erneuern Sie bis {date} — nichts ändert sich.", "Renouvelez avant {date} — rien ne change.", "Renueva antes del {date} y no cambiará nada.", "Rinnova entro il {date} e non cambia nulla.", "Возобновите до {date} — ничего не изменится.") +
            mapOf(AppLanguage.EN to "Renew before {date} and nothing changes.", AppLanguage.CS to "Obnovte do {date} — nic se nezmění.")
    ),
    "cancelled_plan_title" to (
        v("Premium bis {date}", "Premium jusqu'au {date}", "Premium hasta {date}", "Premium fino al {date}", "Premium до {date}") +
            mapOf(AppLanguage.EN to "Premium until {date}", AppLanguage.CS to "Premium do {date}")
    ),
    "cancelled_plan_subtitle" to (
        v("Es erfolgt keine Abbuchung. Nach {date} — Free-Plan.", "Aucun débit n'aura lieu. Après {date} — forfait Free.", "No habrá cobro. Después de {date} — plan Free.", "Nessun addebito. Dopo {date} — piano Free.", "Списание не произойдёт. После {date} — Free план.") +
            mapOf(AppLanguage.EN to "No charge will happen. After {date} — Free plan.", AppLanguage.CS to "Platba neproběhne. Po {date} — Free plán.")
    ),
    "Billing integration is shown in debug mode for now." to v(
        "Zahlungsintegration wird derzeit im Debug-Modus gezeigt.",
        "L'integration de paiement est actuellement en mode debug.",
        "La integracion de pagos se muestra en modo debug por ahora.",
        "L'integrazione pagamenti e mostrata in modalita debug per ora.",
        "Интеграция оплаты пока показана в режиме debug."
    ),
    "Selected" to v("Ausgewahlt", "Selectionne", "Seleccionado", "Selezionato", "Выбрано"),
    "Close" to v("Schliessen", "Fermer", "Cerrar", "Chiudi", "Закрыть"),
    "Create your first habit" to v("Erstelle deine erste Gewohnheit", "Creez votre premiere habitude", "Crea tu primer habito", "Crea la tua prima abitudine", "Создайте первую привычку"),
    "Set up the basics first and add advanced options if needed." to v(
        "Richte zuerst die Grundlagen ein und aktiviere bei Bedarf Erweiterungen.",
        "Configurez d'abord l'essentiel puis les options avancees si besoin.",
        "Configura lo basico primero y agrega opciones avanzadas si es necesario.",
        "Imposta prima le basi e aggiungi opzioni avanzate se serve.",
        "Сначала настройте базовые параметры, затем при необходимости расширенные."
    ),
    "Build powerful habits one day at a time" to v(
        "Baue kraftvolle Gewohnheiten Tag fur Tag auf",
        "Construisez des habitudes puissantes jour apres jour",
        "Construye habitos poderosos dia a dia",
        "Costruisci abitudini solide giorno dopo giorno",
        "Формируйте сильные привычки день за днем"
    ),
    "Start" to v("Start", "Commencer", "Empezar", "Inizia", "Начать"),
    "Skip" to v("Uberspringen", "Passer", "Saltar", "Salta", "Пропустить"),
    "What do you want to improve?" to v(
        "Was mochtest du verbessern?",
        "Que voulez-vous ameliorer ?",
        "Que quieres mejorar?",
        "Cosa vuoi migliorare?",
        "Что вы хотите улучшить?"
    ),
    "Health" to v("Gesundheit", "Sante", "Salud", "Salute", "Здоровье"),
    "Learning" to v("Lernen", "Apprentissage", "Aprendizaje", "Apprendimento", "Обучение"),
    "Mindfulness" to v("Achtsamkeit", "Pleine conscience", "Atencion plena", "Mindfulness", "Осознанность"),
    "Productivity" to v("Produktivitat", "Productivite", "Productividad", "Produttivita", "Продуктивность"),
    "Personal Growth" to v("Personliche Entwicklung", "Developpement personnel", "Crecimiento personal", "Crescita personale", "Личностный рост"),
    "Custom habit" to v("Eigene Gewohnheit", "Habitude personnalisee", "Habito personalizado", "Abitudine personalizzata", "Своя привычка"),
    "Back" to v("Zuruck", "Retour", "Atras", "Indietro", "Назад"),
    "Next" to v("Weiter", "Suivant", "Siguiente", "Avanti", "Далее"),
    "Pick a template" to v(
        "Wahle eine Vorlage",
        "Choisissez un modele",
        "Elige una plantilla",
        "Scegli un modello",
        "Выберите шаблон"
    ),
    "Set up your habit" to v(
        "Richte deine Gewohnheit ein",
        "Configurez votre habitude",
        "Configura tu habito",
        "Configura la tua abitudine",
        "Настройте привычку"
    ),
    "Your habit is ready" to v(
        "Deine Gewohnheit ist bereit",
        "Votre habitude est prete",
        "Tu habito esta listo",
        "La tua abitudine e pronta",
        "Ваша привычка готова"
    ),
    "We highlighted the completion button so you can log your first win." to v(
        "Wir haben die Erledigen-Taste hervorgehoben, damit du den ersten Erfolg festhalten kannst.",
        "Nous avons mis en avant le bouton de validation pour enregistrer votre premier succes.",
        "Resaltamos el boton de completar para que registres tu primera victoria.",
        "Abbiamo evidenziato il pulsante di completamento per registrare il tuo primo successo.",
        "Мы выделили кнопку выполнения, чтобы вы зафиксировали первую победу."
    ),
    "Go to tracker" to v("Zum Tracker", "Aller au suivi", "Ir al tracker", "Vai al tracker", "Перейти в трекер"),
    "Drink water" to v("Wasser trinken", "Boire de l'eau", "Beber agua", "Bere acqua", "Пить воду"),
    "Workout" to v("Training", "Entrainement", "Entrenamiento", "Allenamento", "Тренировка"),
    "Stretch" to v("Dehnen", "Etirements", "Estiramiento", "Stretching", "Растяжка"),
    "Walk 10 minutes" to v("10 Minuten gehen", "Marcher 10 minutes", "Caminar 10 minutos", "Cammina 10 minuti", "Прогулка 10 минут"),
    "Take vitamins" to v("Vitamine nehmen", "Prendre des vitamines", "Tomar vitaminas", "Prendi vitamine", "Принять витамины"),
    "Read 10 minutes" to v("10 Minuten lesen", "Lire 10 minutes", "Leer 10 minutos", "Leggi 10 minuti", "Читать 10 минут"),
    "Practice language" to v("Sprache uben", "Pratiquer une langue", "Practicar idioma", "Pratica lingua", "Практиковать язык"),
    "Watch educational video" to v("Lernvideo ansehen", "Regarder une video educative", "Ver video educativo", "Guarda video educativo", "Смотреть обучающее видео"),
    "Meditate" to v("Meditieren", "Mediter", "Meditar", "Meditare", "Медитация"),
    "Journal" to v("Tagebuch", "Journal", "Diario", "Diario", "Дневник"),
    "Gratitude" to v("Dankbarkeit", "Gratitude", "Gratitud", "Gratitudine", "Благодарность"),
    "Plan the day" to v("Tag planen", "Planifier la journee", "Planificar el dia", "Pianifica la giornata", "Планировать день"),
    "Deep work" to v("Tiefenarbeit", "Travail profond", "Trabajo profundo", "Lavoro profondo", "Глубокая работа"),
    "Clean workspace" to v("Arbeitsplatz aufraumen", "Ranger l'espace de travail", "Limpiar espacio de trabajo", "Pulisci la postazione", "Убрать рабочее место"),
    "Write ideas" to v("Ideen notieren", "Ecrire des idees", "Escribir ideas", "Scrivi idee", "Записывать идеи"),
    "Review goals" to v("Ziele uberprufen", "Revoir les objectifs", "Revisar objetivos", "Rivedi obiettivi", "Пересмотреть цели"),
    "Reflect on day" to v("Tag reflektieren", "Reflechir a la journee", "Reflexionar sobre el dia", "Rifletti sulla giornata", "Рефлексия дня"),
    "No active habit" to v("Keine aktive Gewohnheit", "Aucune habitude active", "Ningun habito activo", "Nessuna abitudine attiva", "Нет активной привычки"),
    "TODAY" to v("HEUTE", "AUJOURD'HUI", "HOY", "OGGI", "СЕГОДНЯ"),
    "FREE" to v("KOSTENLOS", "GRATUIT", "GRATIS", "GRATIS", "FREE"),
    "New" to v("Neu", "Nouveau", "Nuevo", "Nuovo", "Новая"),
    "Edit" to v("Bearbeiten", "Modifier", "Editar", "Modifica", "Изменить"),
    "Did you complete it on this date?" to v(
        "Hast du es an diesem Tag abgeschlossen?",
        "L'avez-vous terminee a cette date ?",
        "Lo completaste en esta fecha?",
        "L'hai completata in questa data?",
        "Вы выполнили это в эту дату?"
    ),
    "Completed ✓" to v("Erledigt ✓", "Termine ✓", "Completado ✓", "Completato ✓", "Выполнено ✓"),
    "Future dates cannot be marked" to v("Zukunftige Daten konnen nicht markiert werden", "Les dates futures ne peuvent pas etre cochees", "No se pueden marcar fechas futuras", "Le date future non possono essere segnate", "Нельзя отмечать будущие даты"),
    "Not scheduled for this date" to v("Nicht fur dieses Datum geplant", "Non prevu pour cette date", "No programado para esta fecha", "Non previsto per questa data", "Не запланировано на эту дату"),
    "Rest day" to v("Ruhetag", "Jour de repos", "Dia de descanso", "Giorno di riposo", "День отдыха"),
    "This habit is not scheduled for this date." to v(
        "Diese Gewohnheit ist fur dieses Datum nicht geplant.",
        "Cette habitude n'est pas prevue a cette date.",
        "Este habito no esta programado para esta fecha.",
        "Questa abitudine non e prevista per questa data.",
        "Эта привычка не запланирована на эту дату."
    ),
    "Next scheduled date: %s" to v(
        "Nächstes geplantes Datum: %s",
        "Prochaine date prevue : %s",
        "Proxima fecha programada: %s",
        "Prossima data pianificata: %s",
        "Следующая запланированная дата: %s"
    ),
    "Manual log saved for this date." to v(
        "Manueller Eintrag fur dieses Datum gespeichert.",
        "Saisie manuelle enregistree pour cette date.",
        "Registro manual guardado para esta fecha.",
        "Registrazione manuale salvata per questa data.",
        "Ручная отметка сохранена для этой даты."
    ),
    "Mark anyway" to v("Trotzdem markieren", "Marquer quand meme", "Marcar de todos modos", "Segna comunque", "Отметить всё равно"),
    "Mark as done" to v("Als erledigt markieren", "Marquer comme fait", "Marcar como hecho", "Segna come completata", "Отметить как выполнено"),
    "More details →" to (
        v("Details →", "Details →", "Detalles →", "Dettagli →", "Подробнее →") +
            mapOf(AppLanguage.CS to "Podrobnosti →")
    ),
    "Great job, your streak is safe." to v("Super, deine Serie ist gesichert.", "Bravo, votre serie est preservee.", "Buen trabajo, tu racha esta a salvo.", "Ottimo lavoro, la tua serie e salva.", "Отлично, ваша серия сохранена."),
    "Goal reached! 🎉" to (
        v("Ziel erreicht! 🎉", "Objectif atteint ! 🎉", "Objetivo alcanzado! 🎉", "Obiettivo raggiunto! 🎉", "Цель достигнута! 🎉") +
            mapOf(AppLanguage.CS to "Cíl splněn! 🎉")
    ),
    "%d %s to go" to (
        v("noch %d %s bis zum Ziel", "encore %d %s", "quedan %d %s", "mancano %d %s", "ещё %d %s до цели") +
            mapOf(AppLanguage.CS to "ještě %d %s do cíle")
    ),
    "+%d %s beyond goal" to (
        v("+%d %s über dem Ziel", "+%d %s au-dela de l'objectif", "+%d %s por encima de la meta", "+%d %s oltre l'obiettivo", "+%d %s сверх цели") +
            mapOf(AppLanguage.CS to "+%d %s nad cílem")
    ),
    "Start today 🌱" to (
        v("Heute starten 🌱", "Commencez aujourd'hui 🌱", "Empieza hoy 🌱", "Inizia oggi 🌱", "Начни сегодня 🌱") +
            mapOf(AppLanguage.CS to "Začni dnes 🌱")
    ),
    "🔥 %dd streak · ⭐ %dd best · %d%% week" to (
        v(
            "🔥 %dT Serie · ⭐ %dT Rekord · %d%% Woche",
            "🔥 Serie %dj · ⭐ Record %dj · %d%% semaine",
            "🔥 Racha %dd · ⭐ Record %dd · %d%% semana",
            "🔥 Serie %dg · ⭐ Record %dg · %d%% settimana",
            "🔥 %dд серия · ⭐ %dд рекорд · %d%% неделя"
        ) + mapOf(AppLanguage.CS to "🔥 %dd série · ⭐ %dd rekord · %d%% týden")
    ),
    "🔥 Streak %dd · Best %dd · %d%% this month" to (
        v(
            "🔥 Serie %dT · Beste %dT · %d%% diesen Monat",
            "🔥 Serie %dj · Meilleure %dj · %d%% ce mois-ci",
            "🔥 Racha %dd · Mejor %dd · %d%% este mes",
            "🔥 Serie %dg · Migliore %dg · %d%% questo mese",
            "🔥 Серия %dд · Лучшая %dд · %d%% за месяц"
        ) + mapOf(AppLanguage.CS to "🔥 Série %dd · Nejlepší %dd · %d%% tento měsíc")
    ),
    "Great job" to v("Sehr gut", "Bravo", "Gran trabajo", "Ottimo lavoro", "Отлично"),
    "Keep it going" to v("Bleib dran", "Continuez", "Sigue asi", "Continua cosi", "Так держать"),
    "Streak milestone" to v("Serien-Meilenstein", "Palier de serie", "Hito de racha", "Traguardo serie", "Рубеж серии"),
    "Streak Saver" to v("Serienretter", "Sauve-serie", "Salva racha", "Salva serie", "Сохранение серии"),
    "You missed yesterday.\nSave your streak?" to v(
        "Du hast gestern verpasst.\nSerie retten?",
        "Vous avez manque hier.\nSauver votre serie ?",
        "Ayer fallaste.\nGuardar tu racha?",
        "Hai saltato ieri.\nSalvare la tua serie?",
        "Вы пропустили вчера.\nСохранить серию?"
    ),
    "Use saver" to v("Retter nutzen", "Utiliser un sauveur", "Usar salvavidas", "Usa salva-serie", "Использовать сохранение"),
    "Streak savers: %d" to v("Serienretter: %d", "Sauve-series : %d", "Salvavidas de racha: %d", "Salva-serie: %d", "Сохранений серии: %d"),
    "Amazing consistency" to v("Beeindruckende Konstanz", "Constante impressionnante", "Consistencia increible", "Costanza incredibile", "Впечатляющая стабильность"),
    "You're building momentum" to v("Du baust Momentum auf", "Vous prenez de l'elan", "Estas creando impulso", "Stai costruendo slancio", "Вы набираете темп"),
    "Continue" to v("Weiter", "Continuer", "Continuar", "Continua", "Продолжить"),
    "%d day streak" to v("%d Tage Serie", "Serie de %d jours", "Racha de %d dias", "Serie di %d giorni", "Серия %d дней"),
    "Milestone reached!" to v("Meilenstein erreicht!", "Palier atteint !", "Hito alcanzado!", "Traguardo raggiunto!", "Достигнут рубеж!"),
    "Streak updated" to v("Serie aktualisiert", "Serie mise a jour", "Racha actualizada", "Serie aggiornata", "Серия обновлена"),
    "7 day chart" to v("7-Tage-Chart", "Graphique sur 7 jours", "Grafico de 7 dias", "Grafico 7 giorni", "График за 7 дней"),
    "W" to v("KW ", "S", "S", "S", "Н"),
    "Tracking type" to v("Tracking-Typ", "Type de suivi", "Tipo de seguimiento", "Tipo di monitoraggio", "Тип отслеживания"),
    "Frequency" to v("Frequenz", "Frequence", "Frecuencia", "Frequenza", "Частота"),
    "Basic setup" to v("Name der Gewohnheit", "Nom de l'habitude", "Nombre del habito", "Nome abitudine", "Название привычки"),
    "Habit name" to v("Name der Gewohnheit", "Nom de l'habitude", "Nombre del habito", "Nome abitudine", "Название привычки"),
    "Morning meditation" to v("Morgenmeditation", "Meditation du matin", "Meditacion matutina", "Meditazione mattutina", "Утренняя медитация"),
    "Icon / emoji" to v("Symbol / Emoji", "Icone / emoji", "Icono / emoji", "Icona / emoji", "Иконка / эмодзи"),
    "Color" to v("Farbe", "Couleur", "Color", "Colore", "Цвет"),
    "Every day" to v("Jeden Tag", "Tous les jours", "Cada dia", "Ogni giorno", "Каждый день"),
    "Selected weekdays" to v("Ausgewahlte Wochentage", "Jours selectionnes", "Dias de semana seleccionados", "Giorni selezionati", "Выбранные дни недели"),
    "X / week" to v("X / Woche", "X / semaine", "X / semana", "X / settimana", "X / неделя"),
    "Select at least one weekday." to v("Wahle mindestens einen Wochentag.", "Selectionnez au moins un jour.", "Selecciona al menos un dia.", "Seleziona almeno un giorno.", "Выберите хотя бы один день недели."),
    "Times per week" to v("Male pro Woche", "Fois par semaine", "Veces por semana", "Volte a settimana", "Раз в неделю"),
    "Hide advanced settings" to v("Erweiterte Einstellungen ausblenden", "Masquer les options avancees", "Ocultar ajustes avanzados", "Nascondi impostazioni avanzate", "Скрыть расширенные настройки"),
    "Show advanced settings" to v("Erweiterte Einstellungen anzeigen", "Afficher les options avancees", "Mostrar ajustes avanzados", "Mostra impostazioni avanzate", "Показать расширенные настройки"),
    "Advanced settings" to v("Erweiterte Einstellungen", "Parametres avances", "Ajustes avanzados", "Impostazioni avanzate", "Расширенные настройки"),
    "Reminder: %s" to v("Erinnerung: %s", "Rappel : %s", "Recordatorio: %s", "Promemoria: %s", "Напоминание: %s"),
    "Start date" to (
        v("Startdatum", "Date de debut", "Fecha de inicio", "Data di inizio", "Дата начала") +
            mapOf(AppLanguage.CS to "Datum zahájení")
        ),
    "Start date: %s" to (
        v("Startdatum: %s", "Date de debut : %s", "Fecha de inicio: %s", "Data di inizio: %s", "Дата начала: %s") +
            mapOf(AppLanguage.CS to "Datum zahájení: %s")
        ),
    "End date" to v("Enddatum", "Date de fin", "Fecha de finalizacion", "Data di fine", "Дата завершения"),
    "End date: %s" to v("Enddatum: %s", "Date de fin : %s", "Fecha de finalizacion: %s", "Data di fine: %s", "Дата завершения: %s"),
    "Optional challenge finish date" to v(
        "Optionales Enddatum der Challenge",
        "Date de fin optionnelle du defi",
        "Fecha opcional de finalizacion del reto",
        "Data di fine opzionale della sfida",
        "Необязательная дата завершения челленджа"
    ),
    "Fill required fields to continue." to v("Fulle die erforderlichen Felder aus.", "Remplissez les champs obligatoires.", "Completa los campos requeridos.", "Compila i campi richiesti.", "Заполните обязательные поля."),
    "Save habit" to v("Gewohnheit speichern", "Enregistrer l'habitude", "Guardar habito", "Salva abitudine", "Сохранить привычку"),
    "Save changes" to v("Anderungen speichern", "Enregistrer les modifications", "Guardar cambios", "Salva modifiche", "Сохранить изменения"),
    "Create Habit" to v("Gewohnheit erstellen", "Creer une habitude", "Crear habito", "Crea abitudine", "Создать привычку"),
    "Edit Habit" to v("Gewohnheit bearbeiten", "Modifier l'habitude", "Editar habito", "Modifica abitudine", "Редактировать привычку"),
    "Do once" to v("Einmal tun", "Faire une fois", "Hacer una vez", "Fallo una volta", "Сделать один раз"),
    "Do N times" to v("N-mal tun", "Faire N fois", "Hacer N veces", "Fallo N volte", "Сделать N раз"),
    "Do N minutes" to v("N Minuten tun", "Faire N minutes", "Hacer N minutos", "Fallo N minuti", "Делать N минут"),
    "Just mark whether you did it today" to v(
        "Markiere nur, ob du es heute gemacht hast",
        "Indiquez simplement si vous l'avez fait aujourd'hui",
        "Solo marca si lo hiciste hoy",
        "Segna solo se l'hai fatto oggi",
        "Просто отметь, сделал ли ты это сегодня"
    ),
    "Set a daily quantity target" to v(
        "Setze ein Tagesziel nach Anzahl",
        "Definissez un objectif quotidien en quantite",
        "Establece un objetivo diario por cantidad",
        "Imposta un obiettivo giornaliero di quantita",
        "Поставь цель по количеству на день"
    ),
    "Set a daily time target" to v(
        "Setze ein Tagesziel nach Zeit",
        "Definissez un objectif quotidien de temps",
        "Establece un objetivo diario de tiempo",
        "Imposta un obiettivo giornaliero di tempo",
        "Поставь цель по времени на день"
    ),
    "Count target" to v("Zahlziel", "Objectif de quantite", "Objetivo de cantidad", "Obiettivo quantita", "Цель по количеству"),
    "Daily target" to v("Tagesziel", "Objectif quotidien", "Objetivo diario", "Obiettivo giornaliero", "Ежедневная цель"),
    "Unit label" to v("Einheit", "Unite", "Unidad", "Unita", "Единица измерения"),
    "e.g. glasses, pages, reps" to v(
        "z.B. Glaser, Seiten, Wiederholungen",
        "ex. verres, pages, repetitions",
        "p. ej. vasos, paginas, repeticiones",
        "es. bicchieri, pagine, ripetizioni",
        "напр. стаканов, страниц, повторений"
    ),
    "Duration target" to v("Zeitziel", "Objectif de duree", "Objetivo de duracion", "Obiettivo durata", "Цель по времени"),
    "Daily minute goal" to v("Tagesziel in Minuten", "Objectif quotidien en minutes", "Meta diaria en minutos", "Obiettivo minuti al giorno", "Ежедневная цель в минутах"),
    "Tracking" to v("Tracking", "Suivi", "Seguimiento", "Monitoraggio", "Отслеживание"),
    "How much progress counts as completed." to v(
        "Wie viel Fortschritt als erledigt gilt.",
        "Quel progres compte comme accompli.",
        "Cuanto progreso cuenta como completado.",
        "Quanto progresso conta come completato.",
        "Сколько прогресса считается выполнением."
    ),
    "Minimum completion percent" to v(
        "Minimaler Erfullungsprozentsatz",
        "Pourcentage minimal de completion",
        "Porcentaje minimo de cumplimiento",
        "Percentuale minima di completamento",
        "Минимальный процент выполнения"
    ),
    "Used for count and duration habits" to v(
        "Gilt fur Gewohnheiten mit Anzahl und Dauer",
        "Utilise pour les habitudes de quantite et duree",
        "Se usa para habitos de cantidad y duracion",
        "Usato per abitudini di conteggio e durata",
        "Используется для привычек по количеству и времени"
    ),
    "Applies to count and duration habits" to v(
        "Gilt fur Gewohnheiten mit Anzahl und Dauer",
        "S'applique aux habitudes de quantite et de duree",
        "Se aplica a habitos de cantidad y duracion",
        "Si applica alle abitudini di conteggio e durata",
        "Применяется к привычкам по количеству и времени"
    ),
    "Completion threshold" to v("Erfullungsschwelle", "Seuil de completion", "Umbral de cumplimiento", "Soglia di completamento", "Порог выполнения"),
    "For example: 100%" to v("Zum Beispiel: 100%", "Par exemple : 100%", "Por ejemplo: 100%", "Per esempio: 100%", "Например: 100%"),
    "Value must be between 1 and 100" to v(
        "Der Wert muss zwischen 1 und 100 liegen",
        "La valeur doit etre comprise entre 1 et 100",
        "El valor debe estar entre 1 y 100",
        "Il valore deve essere compreso tra 1 e 100",
        "Значение должно быть от 1 до 100"
    ),
    "In progress" to v("In Arbeit", "En cours", "En progreso", "In corso", "В процессе"),
    "%d%% of %d%% threshold" to v(
        "%d%% von %d%% Schwelle",
        "%d%% sur seuil %d%%",
        "%d%% de umbral %d%%",
        "%d%% della soglia %d%%",
        "%d%% из %d%% порога"
    ),
    "times" to v("Mal", "fois", "veces", "volte", "раз"),
    "min" to v("Min", "min", "min", "min", "мин"),
    "Manual minutes" to v("Minuten manuell", "Minutes manuelles", "Minutos manuales", "Minuti manuali", "Минуты вручную"),
    "Edit minutes" to v("Minuten bearbeiten", "Modifier les minutes", "Editar minutos", "Modifica minuti", "Изменить минуты"),
    "Enter minutes manually" to v("Minuten manuell eingeben", "Saisir les minutes manuellement", "Ingresar minutos manualmente", "Inserisci minuti manualmente", "Ввести минуты вручную"),
    "Enter manually" to v("Manuell eingeben", "Saisir manuellement", "Introducir manualmente", "Inserisci manualmente", "Ввести вручную"),
    "Timer" to v("Timer", "Minuteur", "Temporizador", "Timer", "Таймер"),
    "Pause" to v("Pause", "Pause", "Pausa", "Pausa", "Пауза"),
    "Resume" to v("Fortsetzen", "Reprendre", "Reanudar", "Riprendi", "Продолжить"),
    "Stop" to v("Stopp", "Arreter", "Detener", "Ferma", "Стоп"),
    "Add %d minutes?" to v(
        "%d Minuten hinzufugen?",
        "Ajouter %d minutes ?",
        "Agregar %d minutos?",
        "Aggiungere %d minuti?",
        "Добавить %d минут?"
    ),
    "Apply manual value" to v("Manuellen Wert anwenden", "Appliquer la valeur manuelle", "Aplicar valor manual", "Applica valore manuale", "Применить ручное значение"),
    "Confirm" to v("Bestatigen", "Confirmer", "Confirmar", "Conferma", "Подтвердить"),
    "Added %d min" to v("%d Min hinzugefugt", "%d min ajoutees", "%d min agregados", "%d min aggiunti", "Добавлено %d мин"),
    "Stop timer" to v("Timer stoppen", "Arreter le minuteur", "Detener temporizador", "Ferma timer", "Остановить таймер"),
    "Start timer" to v("Timer starten", "Demarrer le minuteur", "Iniciar temporizador", "Avvia timer", "Запустить таймер"),
    "Timer (Premium)" to v("Timer (Premium)", "Minuteur (Premium)", "Temporizador (Premium)", "Timer (Premium)", "Таймер (Премиум)"),
    "Total value" to v("Gesamtwert", "Valeur totale", "Valor total", "Valore totale", "Общее значение"),
    "Average per day" to v("Durchschnitt pro Tag", "Moyenne par jour", "Promedio por dia", "Media al giorno", "Среднее в день"),
    "Yes / No" to v("Ja / Nein", "Oui / Non", "Si / No", "Si / No", "Да / Нет"),
    "Count" to v("Anzahl", "Compteur", "Cantidad", "Conteggio", "Количество"),
    "Duration" to v("Dauer", "Duree", "Duracion", "Durata", "Длительность"),
    "Archived" to v("Archiviert", "Archive", "Archivado", "Archiviata", "В архиве"),
    "Active" to v("Aktiv", "Active", "Activo", "Attiva", "Активна"),
    "All habits" to v("Alle Gewohnheiten", "Toutes les habitudes", "Todos los habitos", "Tutte le abitudini", "Все привычки"),
    "Global overview" to v("Globaler Uberblick", "Vue d'ensemble globale", "Resumen global", "Panoramica globale", "Глобальный обзор"),
    "No active or completed habits yet." to v(
        "Noch keine aktiven oder abgeschlossenen Gewohnheiten.",
        "Aucune habitude active ou terminee pour le moment.",
        "Aun no hay habitos activos o completados.",
        "Nessuna abitudine attiva o completata al momento.",
        "Пока нет активных или завершённых привычек."
    ),
    "Day breakdown" to v("Tagesaufschlusselung", "Detail du jour", "Desglose del dia", "Dettaglio del giorno", "Разбор дня"),
    "Completed %d of %d scheduled" to v(
        "Erledigt %d von %d geplant",
        "Terminees %d sur %d prevues",
        "Completadas %d de %d programadas",
        "Completate %d su %d pianificate",
        "Выполнено %d из %d запланированных"
    ),
    "Done %d of %d" to (
        v("%d von %d erledigt", "%d sur %d terminees", "%d de %d completadas", "%d su %d completate", "Выполнено %d из %d") +
            mapOf(AppLanguage.CS to "Splněno %d z %d")
    ),
    "Today pending" to v("Heute ausstehend", "Aujourd'hui en attente", "Pendiente de hoy", "In attesa oggi", "Ожидается сегодня"),
    "Value %d / %d" to v("Wert %d / %d", "Valeur %d / %d", "Valor %d / %d", "Valore %d / %d", "Значение %d / %d"),
    "Value %d / %d %s" to v("Wert %d / %d %s", "Valeur %d / %d %s", "Valor %d / %d %s", "Valore %d / %d %s", "Значение %d / %d %s"),
    "Streak" to v("Serie", "Serie", "Racha", "Serie", "Серия"),
    "Completion" to v("Erfullung", "Realisation", "Cumplimiento", "Completamento", "Выполнение"),
    "Unarchive" to v("Wiederherstellen", "Restaurer", "Desarchivar", "Ripristina", "Разархивировать"),
    "Archive" to v("Archivieren", "Archiver", "Archivar", "Archivia", "В архив"),
    "Weekly completion: %d%%" to v(
        "Wochenerfullung: %d%%",
        "Progression hebdomadaire : %d%%",
        "Cumplimiento semanal: %d%%",
        "Completamento settimanale: %d%%",
        "Выполнение за неделю: %d%%"
    ),
    "Completed today" to v("Heute erledigt", "Termine aujourd'hui", "Completado hoy", "Completato oggi", "Выполнено сегодня"),
    "Complete today" to v("Heute abschliessen", "Valider aujourd'hui", "Completar hoy", "Completa oggi", "Выполнить сегодня"),
    "Not scheduled today" to v("Heute nicht geplant", "Non prevu aujourd'hui", "No programado hoy", "Non pianificato oggi", "Не запланировано на сегодня"),
    "Level progress" to v("Level-Fortschritt", "Progression de niveau", "Progreso de nivel", "Progresso livello", "Прогресс уровня"),
    "Level %d" to v("Level %d", "Niveau %d", "Nivel %d", "Livello %d", "Уровень %d"),
    "Best streak: %d" to v("Beste Serie: %d", "Meilleure serie : %d", "Mejor racha: %d", "Serie migliore: %d", "Лучшая серия: %d"),
    "%d days to level %d" to v("%d Tage bis Level %d", "%d jours jusqu'au niveau %d", "%d dias hasta nivel %d", "%d giorni al livello %d", "%d дней до уровня %d"),
    "Level %d unlocked" to v("Level %d freigeschaltet", "Niveau %d debloque", "Nivel %d desbloqueado", "Livello %d sbloccato", "Уровень %d открыт"),
    "Stats" to v("Statistiken", "Statistiques", "Estadisticas", "Statistiche", "Статистика"),
    "Streak history" to v("Serienhistorie", "Historique des series", "Historial de rachas", "Storico serie", "История серий"),
    "days" to v("Tage", "jours", "dias", "giorni", "дней"),
    "Previous streaks" to v("Vorherige Serien", "Series precedentes", "Rachas anteriores", "Serie precedenti", "Предыдущие серии"),
    "No streak history yet" to v("Noch keine Serienhistorie", "Pas encore d'historique de serie", "Aun no hay historial de rachas", "Nessuno storico serie", "Пока нет истории серий"),
    "No data" to v("Keine Daten", "Aucune donnee", "Sin datos", "Nessun dato", "Нет данных"),
    "Insights" to v("Einblicke", "Insights", "Insights", "Insight", "Инсайты"),
    "Most consistent day" to v("Stabilster Tag", "Jour le plus stable", "Dia mas constante", "Giorno piu stabile", "Самый стабильный день"),
    "Hardest day" to v("Schwierigster Tag", "Jour le plus difficile", "Dia mas dificil", "Giorno piu difficile", "Самый сложный день"),
    "Completion consistency" to v("Erfullungs-Konstanz", "Regularite d'execution", "Consistencia de cumplimiento", "Costanza di completamento", "Стабильность выполнения"),
    "Habit notes" to v("Notizen zur Gewohnheit", "Notes d'habitude", "Notas de habito", "Note abitudine", "Заметки о привычке"),
    "Why did you miss today?" to v("Warum hast du heute ausgesetzt?", "Pourquoi avez-vous manque aujourd'hui ?", "Por que fallaste hoy?", "Perche hai saltato oggi?", "Почему вы пропустили сегодня?"),
    "notes_placeholder_done" to (v("Wie lief es heute?", "Comment cela s'est-il passé aujourd'hui ?", "Como te fue hoy?", "Com'è andata oggi?", "Как прошло сегодня?") + mapOf(AppLanguage.EN to "How did it go today?", AppLanguage.CS to "Jak to dnes šlo?")),
    "notes_placeholder_missed" to (v("Warum hast du heute ausgesetzt?", "Pourquoi avez-vous manque aujourd'hui ?", "Por que fallaste hoy?", "Perche hai saltato oggi?", "Почему пропустил сегодня?") + mapOf(AppLanguage.EN to "Why did you miss today?", AppLanguage.CS to "Proč jsi dnes vynechal?")),
    "notes_placeholder_default" to (v("Notizen zur Gewohnheit", "Notes d'habitude", "Notas del habito", "Note sull'abitudine", "Заметки о привычке") + mapOf(AppLanguage.EN to "Habit notes", AppLanguage.CS to "Poznámky ke zvyku")),
    "Widgets are Premium" to (
        v("Widgets sind Premium", "Les widgets sont Premium", "Los widgets son Premium", "I widget sono Premium", "Виджеты доступны в Premium") +
            mapOf(AppLanguage.EN to "Widgets are Premium", AppLanguage.CS to "Widgety jsou v Premium")
    ),
    "widget_streak_label" to (
        v("Serie", "serie", "racha", "serie", "серия") +
            mapOf(AppLanguage.EN to "streak", AppLanguage.CS to "série")
    ),
    "widget_mark_short" to (
        v("Markieren", "Marquer", "Marcar", "Segna", "Отметить") +
            mapOf(AppLanguage.EN to "Mark", AppLanguage.CS to "Označit")
    ),
    "widget_day_short" to (
        v("T", "j", "d", "g", "дн") +
            mapOf(AppLanguage.EN to "d", AppLanguage.CS to "d")
    ),
    "widget_today_completed" to (
        v("Heute: erledigt", "Aujourd'hui : fait", "Hoy: completado", "Oggi: completato", "Сегодня: выполнено") +
            mapOf(AppLanguage.EN to "Today: completed", AppLanguage.CS to "Dnes: splněno")
    ),
    "widget_today_in_progress" to (
        v("Heute: in Arbeit", "Aujourd'hui : en cours", "Hoy: en progreso", "Oggi: in corso", "Сегодня: в процессе") +
            mapOf(AppLanguage.EN to "Today: in progress", AppLanguage.CS to "Dnes: v procesu")
    ),
    "widget_stat_streak" to (
        v("SERIE", "SÉRIE", "RACHA", "SERIE", "СЕРИЯ") +
            mapOf(AppLanguage.EN to "STREAK", AppLanguage.CS to "SÉRIE")
    ),
    "widget_stat_record" to (
        v("REKORD", "RECORD", "RÉCORD", "RECORD", "РЕКОРД") +
            mapOf(AppLanguage.EN to "RECORD", AppLanguage.CS to "REKORD")
    ),
    "widget_stat_7_days" to (
        v("7 TAGE", "7 JOURS", "7 DÍAS", "7 GIORNI", "7 ДНЕЙ") +
            mapOf(AppLanguage.EN to "7 DAYS", AppLanguage.CS to "7 DNÍ")
    ),
    "widget_week_progress_short" to (
        v("%d / 7 %s", "%d / 7 %s", "%d / 7 %s", "%d / 7 %s", "%d / 7 %s") +
            mapOf(AppLanguage.EN to "%d / 7 %s", AppLanguage.CS to "%d / 7 %s")
    ),
    "widget_current_record_days" to (
        v("Aktueller Rekord: %d Tage", "Record actuel : %d jours", "Récord actual: %d días", "Record attuale: %d giorni", "Текущий рекорд: %d дней") +
            mapOf(AppLanguage.EN to "Current record: %d days", AppLanguage.CS to "Aktuální rekord: %d dní")
    ),
    "widget_insight_keep_streak" to (
        v("Heute markiert. Halte die Serie.", "Marqué aujourd'hui. Garde la série.", "Hoy marcado. Mantén la racha.", "Segnato oggi. Mantieni la serie.", "Сегодня отмечено. Продолжай серию.") +
            mapOf(AppLanguage.EN to "Today is marked. Keep the streak going.", AppLanguage.CS to "Dnes splněno. Udrž sérii.")
    ),
    "widget_insight_mark_today" to (
        v("Markiere heute und halte die Serie.", "Marque aujourd'hui et garde la série.", "Marca hoy y mantén la racha.", "Segna oggi e mantieni la serie.", "Отметь сегодня и сохрани серию.") +
            mapOf(AppLanguage.EN to "Mark today and keep the streak.", AppLanguage.CS to "Označ dnes a udrž sérii.")
    ),
    "widget_done" to (
        v("Erledigt", "Terminé", "Completado", "Completato", "Выполнено") +
            mapOf(AppLanguage.EN to "Completed", AppLanguage.CS to "Splněno")
    ),
    "widget_mark_done" to (
        v("Markieren ✓", "Marquer ✓", "Marcar ✓", "Segna ✓", "Отметить ✓") +
            mapOf(AppLanguage.EN to "Mark ✓", AppLanguage.CS to "Označit ✓")
    ),
    "widget_cta_not_started" to (
        v("%d %s noch", "%d %s restants", "%d %s para la meta", "%d %s al traguardo", "%d %s до цели") +
            mapOf(AppLanguage.EN to "%d %s to go", AppLanguage.CS to "zbývá %d %s")
    ),
    "widget_cta_not_started_no_unit" to (
        v("noch %d", "%d restants", "faltan %d", "ancora %d", "%d до цели") +
            mapOf(AppLanguage.EN to "%d to go", AppLanguage.CS to "zbývá %d")
    ),
    "widget_cta_in_progress" to (
        v("noch %d %s", "encore %d %s", "faltan %d %s", "ancora %d %s", "ещё %d %s") +
            mapOf(AppLanguage.EN to "%d %s left", AppLanguage.CS to "ještě %d %s")
    ),
    "widget_cta_in_progress_no_unit" to (
        v("noch %d", "encore %d", "faltan %d", "ancora %d", "ещё %d") +
            mapOf(AppLanguage.EN to "%d left", AppLanguage.CS to "ještě %d")
    ),
    "widget_cta_done" to (
        v("Erledigt ✓", "Terminé ✓", "Completado ✓", "Completato ✓", "Выполнено ✓") +
            mapOf(AppLanguage.EN to "Completed ✓", AppLanguage.CS to "Splněno ✓")
    ),
    "widget_goal_reached" to (
        v("Ziel erreicht", "Objectif atteint", "Objetivo alcanzado", "Obiettivo raggiunto", "Цель достигнута") +
            mapOf(AppLanguage.EN to "Goal reached", AppLanguage.CS to "Cíl splněn")
    ),
    "widget_inc_btn_min" to (
        v("+%d %s", "+%d %s", "+%d %s", "+%d %s", "+%d %s") +
            mapOf(AppLanguage.EN to "+%d %s", AppLanguage.CS to "+%d %s")
    ),
    "widget_upsell_title" to (
        v("Widgets sind in Premium verfügbar", "Les widgets sont disponibles en Premium", "Los widgets están disponibles en Premium", "I widget sono disponibili in Premium", "Виджеты доступны в Premium") +
            mapOf(AppLanguage.EN to "Widgets are available in Premium", AppLanguage.CS to "Widgety jsou dostupné v Premium")
    ),
    "widget_upsell_subtitle" to (
        v("Öffne die App zum Upgrade und Hinzufügen dieses Widgets.", "Ouvrez l'application pour passer à Premium et ajouter ce widget.", "Abre la app para mejorar a Premium y añadir este widget.", "Apri l'app per passare a Premium e aggiungere questo widget.", "Открой приложение, чтобы перейти на Premium и добавить этот виджет.") +
            mapOf(AppLanguage.EN to "Open app to upgrade and add this widget.", AppLanguage.CS to "Otevři aplikaci, upgraduj a přidej tento widget.")
    ),
    "widget_open_pro" to (
        v("Premium öffnen", "Ouvrir Premium", "Abrir Premium", "Apri Premium", "Открыть Premium") +
            mapOf(AppLanguage.EN to "Open Premium", AppLanguage.CS to "Otevřít Premium")
    ),
    "widget_choose_habit" to (
        v("Wähle eine Gewohnheit für das Widget", "Choisissez une habitude pour le widget", "Elige un hábito para el widget", "Scegli un'abitudine per il widget", "Выбери привычку для виджета") +
            mapOf(AppLanguage.EN to "Choose a habit for widget", AppLanguage.CS to "Vyber návyk pro widget")
    ),
    "Save note" to v("Notiz speichern", "Enregistrer la note", "Guardar nota", "Salva nota", "Сохранить заметку"),
    "New habit" to v("Neue Gewohnheit", "Nouvelle habitude", "Nuevo habito", "Nuova abitudine", "Новая привычка"),
    "Choose emoji" to v("Emoji wahlen", "Choisir un emoji", "Elegir emoji", "Scegli emoji", "Выбрать эмодзи"),
    "Done" to v("Fertig", "Termine", "Listo", "Fatto", "Готово"),
    "More" to v("Mehr", "Plus", "Más", "Altro", "Ещё"),
    "action_done" to (v("Fertig", "Termine", "Listo", "Fatto", "Готово") + mapOf(AppLanguage.EN to "Done", AppLanguage.CS to "Hotovo")),
    "action_edit" to (v("Bearbeiten", "Modifier", "Editar", "Modifica", "Изменить") + mapOf(AppLanguage.EN to "Edit", AppLanguage.CS to "Upravit")),
    "action_enable" to (v("Aktivieren", "Activer", "Activar", "Attiva", "Включить") + mapOf(AppLanguage.EN to "Enable", AppLanguage.CS to "Povolit")),
    "edit_label" to (v("Bearbeiten", "Modifier", "Editar", "Modifica", "Изменить") + mapOf(AppLanguage.EN to "Edit", AppLanguage.CS to "Upravit")),
    "enable_label" to (v("Aktivieren", "Activer", "Activar", "Attiva", "Включить") + mapOf(AppLanguage.EN to "Enable", AppLanguage.CS to "Povolit")),
    "action_save" to (v("Speichern", "Enregistrer", "Guardar", "Salva", "Сохранить") + mapOf(AppLanguage.EN to "Save", AppLanguage.CS to "Uložit")),
    "action_cancel" to (v("Abbrechen", "Annuler", "Cancelar", "Annulla", "Отмена") + mapOf(AppLanguage.EN to "Cancel", AppLanguage.CS to "Zrušit")),
    "btn_create_habit" to (v("Gewohnheit erstellen", "Creer l'habitude", "Crear habito", "Crea abitudine", "Создать привычку") + mapOf(AppLanguage.EN to "Create habit", AppLanguage.CS to "Vytvořit návyk")),
    "btn_configure_more" to (v("Anpassen", "Configurer", "Personalizar", "Configura", "Настроить подробнее") + mapOf(AppLanguage.EN to "Customize", AppLanguage.CS to "Přizpůsobit")),
    "btn_create_custom" to (v("+ Eigene Gewohnheit", "+ Habitude personnalisee", "+ Habito personalizado", "+ Abitudine personalizzata", "+ Создать свою привычку") + mapOf(AppLanguage.EN to "+ Custom habit", AppLanguage.CS to "+ Vlastní návyk")),
    "custom_habit_btn" to (v("+ Eigene Gewohnheit", "+ Habitude personnalisee", "+ Habito personalizado", "+ Abitudine personalizzata", "+ Своя привычка") + mapOf(AppLanguage.EN to "+ Custom habit", AppLanguage.CS to "+ Vlastní návyk")),
    "customize_more" to (v("Anpassen →", "Personnaliser →", "Personalizar →", "Personalizza →", "Настроить подробнее →") + mapOf(AppLanguage.EN to "Customize →", AppLanguage.CS to "Přizpůsobit →")),
    "screen_new_habit" to (v("Neue Gewohnheit", "Nouvelle habitude", "Nuevo habito", "Nuova abitudine", "Новая привычка") + mapOf(AppLanguage.EN to "New habit", AppLanguage.CS to "Nový návyk")),
    "screen_create_habit" to (v("Gewohnheit erstellen", "Creer une habitude", "Crear habito", "Crea abitudine", "Создать привычку") + mapOf(AppLanguage.EN to "Create habit", AppLanguage.CS to "Vytvořit návyk")),
    "label_what_to_improve" to (v("Was möchtest du verbessern?", "Que voulez-vous améliorer ?", "¿Qué quieres mejorar?", "Cosa vuoi migliorare?", "Что хочешь улучшить?") + mapOf(AppLanguage.EN to "What do you want to improve?", AppLanguage.CS to "Co chceš zlepšit?")),
    "label_choose_category" to (v("Kategorie wählen", "Choisissez une catégorie", "Elige una categoría", "Scegli una categoria", "Выбери категорию") + mapOf(AppLanguage.EN to "Choose a category", AppLanguage.CS to "Vyberte kategorii")),
    "label_frequency" to (v("Häufigkeit", "Frequence", "Frecuencia", "Frequenza", "Частота") + mapOf(AppLanguage.EN to "Frequency", AppLanguage.CS to "Frekvence")),
    "label_start_date" to (v("Startdatum", "Date de debut", "Fecha de inicio", "Data di inizio", "Дата начала") + mapOf(AppLanguage.EN to "Start date", AppLanguage.CS to "Datum zahájení")),
    "label_reminder" to (v("Erinnerung", "Rappel", "Recordatorio", "Promemoria", "Напоминание") + mapOf(AppLanguage.EN to "Reminder", AppLanguage.CS to "Připomínka")),
    "label_skip" to (v("Überspringen", "Passer", "Saltar", "Salta", "Пропустить") + mapOf(AppLanguage.EN to "Skip", AppLanguage.CS to "Přeskočit")),
    "label_reminder_off" to (v("Aus", "Desactive", "Desactivado", "Disattivato", "Выключено") + mapOf(AppLanguage.EN to "Off", AppLanguage.CS to "Vypnuto")),
    "editor_advanced_show" to (v("Mehr Optionen", "Plus d'options", "Más opciones", "Più opzioni", "Больше опций") + mapOf(AppLanguage.EN to "More options", AppLanguage.CS to "Více možností")),
    "editor_advanced_hide" to (v("Weniger Optionen", "Moins d'options", "Menos opciones", "Meno opzioni", "Меньше опций") + mapOf(AppLanguage.EN to "Fewer options", AppLanguage.CS to "Méně možností")),
    "editor_unit_hint_small" to (v("z.B. Gläser, Sätze, Wiederholungen", "ex. verres, séries, répétitions", "p. ej. vasos, series, repeticiones", "es. bicchieri, serie, ripetizioni", "например, стаканы, подходы, повторы") + mapOf(AppLanguage.EN to "e.g. glasses, sets, reps", AppLanguage.CS to "např. sklenice, série, opakování")),
    "editor_unit_hint_medium" to (v("z.B. Seiten, Schritte", "ex. pages, pas", "p. ej. páginas, pasos", "es. pagine, passi", "например, страницы, шаги") + mapOf(AppLanguage.EN to "e.g. pages, steps", AppLanguage.CS to "např. stránky, kroky")),
    "editor_unit_hint_large" to (v("z.B. Schritte, Wörter", "ex. pas, mots", "p. ej. pasos, palabras", "es. passi, parole", "например, шаги, слова") + mapOf(AppLanguage.EN to "e.g. steps, words", AppLanguage.CS to "např. kroky, slova")),
    "editor_freq_daily_title" to (v("Täglich", "Quotidien", "Diario", "Quotidiano", "Каждый день") + mapOf(AppLanguage.EN to "Daily", AppLanguage.CS to "Denně")),
    "editor_freq_daily_subtitle" to (v("Jeden Tag", "Chaque jour", "Cada día", "Ogni giorno", "Каждый день") + mapOf(AppLanguage.EN to "Every day", AppLanguage.CS to "Každý den")),
    "editor_freq_set_days_title" to (v("Feste Tage", "Jours fixes", "Días fijos", "Giorni fissi", "Выбранные дни") + mapOf(AppLanguage.EN to "Set days", AppLanguage.CS to "Vybrané dny")),
    "editor_freq_set_days_subtitle" to (v("Wochentage wählen", "Choisir des jours", "Elegir días", "Scegli i giorni", "Выбрать дни недели") + mapOf(AppLanguage.EN to "Pick weekdays", AppLanguage.CS to "Vyber dny v týdnu")),
    "editor_freq_n_times_title" to (v("N× / Woche", "N× / semaine", "N× / semana", "N× / settimana", "N× / неделя") + mapOf(AppLanguage.EN to "N× / week", AppLanguage.CS to "N× / týden")),
    "editor_freq_n_times_subtitle" to (v("Flexible Tage", "Jours flexibles", "Días flexibles", "Giorni flessibili", "Гибкие дни") + mapOf(AppLanguage.EN to "Flexible days", AppLanguage.CS to "Flexibilní dny")),
    "editor_remind_me_at" to (v("Erinnere mich um", "Rappelle-moi à", "Recuérdame a las", "Ricordamelo alle", "Напомнить в") + mapOf(AppLanguage.EN to "Remind me at", AppLanguage.CS to "Připomeň mi v")),
    "label_finish_on" to (v("Abschließen am", "Se termine le", "Finaliza el", "Termina il", "Завершить") + mapOf(AppLanguage.EN to "Finish on", AppLanguage.CS to "Dokončit")),
    "editor_how_many_per_day" to (v("Wie oft pro Tag?", "Combien par jour ?", "¿Cuántas veces al día?", "Quante volte al giorno?", "Сколько раз в день?") + mapOf(AppLanguage.EN to "How many per day?", AppLanguage.CS to "Kolikrát denně?")),
    "editor_times_per_week" to (v("Mal pro Woche", "Fois par semaine", "Veces por semana", "Volte a settimana", "Раз в неделю") + mapOf(AppLanguage.EN to "Times per week", AppLanguage.CS to "Krát týdně")),
    "show_advanced" to v("Enddatum und Erinnerungen ▾", "Date de fin et rappels ▾", "Fecha final y recordatorios ▾", "Data di fine e promemoria ▾", "Дата окончания и напоминания ▾"),
    "hide_advanced" to v("Ausblenden ▴", "Masquer ▴", "Ocultar ▴", "Nascondi ▴", "Скрыть ▴"),
    "tracking_type_yesno" to (v("Erledigen", "Faire", "Hacer", "Fallo", "Сделать") + mapOf(AppLanguage.EN to "Do it", AppLanguage.CS to "Splnit")),
    "tracking_type_do" to (v("Machen", "Faire", "Hacer", "Fare", "Сделать") + mapOf(AppLanguage.EN to "Do it", AppLanguage.CS to "Udělat")),
    "tracking_type_do_it" to (v("Machen", "Faire", "Hacer", "Fare", "Сделать") + mapOf(AppLanguage.EN to "Do it", AppLanguage.CS to "Udělat")),
    "tracking_type_do_it_sub" to (v("Einfach abhaken, wenn erledigt", "Cochez simplement si c'est fait", "Solo marca si lo hiciste", "Segna solo se l'hai fatto", "Просто отметь, если сделал") + mapOf(AppLanguage.EN to "Just mark if you did it", AppLanguage.CS to "Jen označ, že je hotovo")),
    "tracking_type_yesno_desc" to (v("Einfach abhaken", "Simplement cocher", "Solo marcar si lo hiciste", "Basta segnare se l'hai fatto", "Просто отметить — сделал или нет") + mapOf(AppLanguage.EN to "Just mark if you did it", AppLanguage.CS to "Jen označit")),
    "tracking_type_time" to (v("Zeit", "Temps", "Tiempo", "Tempo", "Время") + mapOf(AppLanguage.EN to "Time it", AppLanguage.CS to "Čas")),
    "tracking_type_time_it" to (v("Zeit messen", "Chronométrer", "Medir tiempo", "Misura tempo", "Засечь время") + mapOf(AppLanguage.EN to "Time it", AppLanguage.CS to "Měřit čas")),
    "tracking_type_time_it_sub" to (v("Minuten Laufen, Meditation...", "Minutes de course, méditation...", "Minutos de correr, meditar...", "Minuti di corsa, meditazione...", "Минуты бега, медитации...") + mapOf(AppLanguage.EN to "Minutes of running, meditation...", AppLanguage.CS to "Minuty běhu, meditace...")),
    "tracking_type_count" to (v("Zählen", "Compter", "Contar", "Conta", "Счёт") + mapOf(AppLanguage.EN to "Count", AppLanguage.CS to "Počet")),
    "tracking_type_count_sub" to (v("Gläser, Seiten, Sätze...", "Verres, pages, séries...", "Vasos, páginas, series...", "Bicchieri, pagine, serie...", "Стаканы, страницы, подходы...") + mapOf(AppLanguage.EN to "Glasses, pages, sets...", AppLanguage.CS to "Sklenice, stránky, série...")),
    "tracking_type_count_desc" to (v("Gläser, Seiten, Sätze...", "Verres, pages, séries...", "Vasos, páginas, series...", "Bicchieri, pagine, serie...", "Стаканы, страницы, подходы...") + mapOf(AppLanguage.EN to "Glasses, pages, sets...", AppLanguage.CS to "Sklenice, stránky, série...")),
    "tracking_type_duration" to (v("Zeiterfassung", "Chronometrer", "Cronometrar", "Cronometra", "Засечь время") + mapOf(AppLanguage.EN to "Time it", AppLanguage.CS to "Měřit čas")),
    "tracking_type_duration_desc" to (v("Minuten Laufen, Meditation...", "Minutes de course, méditation...", "Minutos de correr, meditar...", "Minuti di corsa, meditazione...", "Минуты бега, медитации, учёбы...") + mapOf(AppLanguage.EN to "Minutes of running, meditation...", AppLanguage.CS to "Minuty běhu, meditace...")),
    "freq_every_day" to (v("Jeden Tag", "Chaque jour", "Cada dia", "Ogni giorno", "Каждый день") + mapOf(AppLanguage.EN to "Every day", AppLanguage.CS to "Každý den")),
    "freq_daily" to (v("Jeden Tag", "Chaque jour", "Cada día", "Ogni giorno", "Каждый день") + mapOf(AppLanguage.EN to "Every day", AppLanguage.CS to "Každý den")),
    "freq_daily_desc" to (v("Die Gewohnheit wiederholt sich täglich. Ein verpasster geplanter Tag unterbricht die Serie.", "L'habitude se répète chaque jour. Manquer un jour planifié casse la série.", "El hábito se repite cada día. Perder un día programado rompe la racha.", "L'abitudine si ripete ogni giorno. Saltare un giorno pianificato interrompe la serie.", "Привычка повторяется каждый день. Пропуск запланированного дня прерывает серию.") + mapOf(AppLanguage.EN to "Habit repeats every day. Missing a scheduled day breaks your streak.", AppLanguage.CS to "Návyk se opakuje každý den. Vynechání plánovaného dne přeruší sérii.")),
    "freq_selected_days" to (v("Bestimmte Tage", "Jours spécifiques", "Días específicos", "Giorni specifici", "Выбрать дни") + mapOf(AppLanguage.EN to "Specific days", AppLanguage.CS to "Vybrané dny")),
    "freq_selected_days_desc" to (v("Wähle Wochentage für die Wiederholung. Nur diese Tage zählen zur Serie.", "Choisissez les jours de répétition. Seuls ces jours comptent pour la série.", "Elige los días de repetición. Solo esos días cuentan para la racha.", "Scegli i giorni della settimana. Solo quei giorni contano per la serie.", "Выбери дни недели. Только эти дни идут в серию.") + mapOf(AppLanguage.EN to "Choose which weekdays to repeat. Only those days count toward your streak.", AppLanguage.CS to "Vyber dny v týdnu. Jen tyto dny se započítají do série.")),
    "freq_times_per_week" to (v("Mal pro Woche", "Fois par semaine", "Veces por semana", "Volte a settimana", "Раз в неделю") + mapOf(AppLanguage.EN to "Times per week", AppLanguage.CS to "Krát týdně")),
    "freq_times_per_week_desc" to (v("Erreiche dein Ziel an beliebigen Tagen. Die Serie zählt abgeschlossene Wochen.", "Atteignez votre objectif les jours de votre choix. La série compte les semaines réussies.", "Cumple tu objetivo en cualquier día. La racha cuenta semanas completas.", "Raggiungi l'obiettivo nei giorni che vuoi. La serie conta le settimane completate.", "Выполняй цель в любые дни. Серия считает завершённые недели.") + mapOf(AppLanguage.EN to "Hit your target any days you choose. Streak counts completed weeks, not specific days.", AppLanguage.CS to "Splň cíl v libovolné dny. Série počítá dokončené týdny, ne konkrétní dny.")),
    "freq_times_short" to (v("×/Wo", "×/sem", "×/sem", "×/sett", "раз/нед") + mapOf(AppLanguage.EN to "×/week", AppLanguage.CS to "×/týd")),
    "unit_label_hint_small" to v("Glaser, Tabletten...", "verres, comprimes...", "vasos, tabletas...", "bicchieri, compresse...", "стаканы, таблетки..."),
    "unit_label_hint_large" to v("Schritte, Seiten...", "pas, pages...", "pasos, paginas...", "passi, pagine...", "шагов, страниц..."),
    "template_count_one" to (v("%d Gewohnheit", "%d habitude", "%d hábito", "%d abitudine", "%d привычка") + mapOf(AppLanguage.EN to "%d habit", AppLanguage.CS to "%d návyk")),
    "template_count_few" to (v("%d Gewohnheiten", "%d habitudes", "%d hábitos", "%d abitudini", "%d привычки") + mapOf(AppLanguage.EN to "%d habits", AppLanguage.CS to "%d návyky")),
    "template_count_many" to (v("%d Gewohnheiten", "%d habitudes", "%d hábitos", "%d abitudini", "%d привычек") + mapOf(AppLanguage.EN to "%d habits", AppLanguage.CS to "%d návyků")),
    "cat_all" to v("Alle", "Tous", "Todos", "Tutte", "Все"),
    "cat_health" to (v("Gesundheit", "Sante", "Salud", "Salute", "Здоровье") + mapOf(AppLanguage.EN to "Health", AppLanguage.CS to "Zdraví")),
    "cat_sport" to (v("Sport", "Sport", "Deporte", "Sport", "Спорт") + mapOf(AppLanguage.EN to "Sport", AppLanguage.CS to "Sport")),
    "cat_mental" to (v("Mental", "Mental", "Mental", "Mentale", "Ментальное") + mapOf(AppLanguage.EN to "Mental", AppLanguage.CS to "Mentální")),
    "cat_productivity" to (v("Produktivität", "Productivite", "Productividad", "Produttivita", "Продуктивность") + mapOf(AppLanguage.EN to "Productivity", AppLanguage.CS to "Produktivita")),
    "category_nutrition" to (v("Ernährung", "Nutrition", "Nutricion", "Nutrizione", "Питание") + mapOf(AppLanguage.EN to "Nutrition", AppLanguage.CS to "Výživa")),
    "category_finance" to (v("Finanzen", "Finance", "Finanzas", "Finanza", "Финансы") + mapOf(AppLanguage.EN to "Finance", AppLanguage.CS to "Finance")),
    "category_creativity" to (v("Kreativität", "Creativite", "Creatividad", "Creativita", "Творчество") + mapOf(AppLanguage.EN to "Creativity", AppLanguage.CS to "Kreativita")),
    "category_relationships" to (v("Beziehungen", "Relations", "Relaciones", "Relazioni", "Отношения") + mapOf(AppLanguage.EN to "Relationships", AppLanguage.CS to "Vztahy")),
    "tmpl_drink_water" to (v("Wasser trinken", "Boire de l'eau", "Beber agua", "Bere acqua", "Пить воду") + mapOf(AppLanguage.EN to "Drink water", AppLanguage.CS to "Pít vodu")),
    "tmpl_vitamins" to (v("Vitamine", "Vitamines", "Vitaminas", "Vitamine", "Витамины") + mapOf(AppLanguage.EN to "Vitamins", AppLanguage.CS to "Vitamíny")),
    "tmpl_healthy_food" to (v("Gesunde Ernährung", "Alimentation saine", "Comida saludable", "Alimentazione sana", "Здоровое питание") + mapOf(AppLanguage.EN to "Healthy eating", AppLanguage.CS to "Zdravé stravování")),
    "tmpl_no_sugar" to (v("Kein Zucker", "Sans sucre", "Sin azucar", "Senza zucchero", "Без сахара") + mapOf(AppLanguage.EN to "No sugar", AppLanguage.CS to "Bez cukru")),
    "tmpl_sleep_early" to (v("Früh schlafen", "Sommeil tôt", "Dormir temprano", "Dormire presto", "Сон до 23:00") + mapOf(AppLanguage.EN to "Early sleep", AppLanguage.CS to "Spát včas")),
    "tmpl_running" to (v("Laufen", "Course", "Correr", "Corsa", "Пробежка") + mapOf(AppLanguage.EN to "Running", AppLanguage.CS to "Běh")),
    "tmpl_workout" to (v("Training", "Entrainement", "Entrenamiento", "Allenamento", "Тренировка") + mapOf(AppLanguage.EN to "Workout", AppLanguage.CS to "Cvičení")),
    "tmpl_cycling" to (v("Radfahren", "Cyclisme", "Ciclismo", "Ciclismo", "Велосипед") + mapOf(AppLanguage.EN to "Cycling", AppLanguage.CS to "Jízda na kole")),
    "tmpl_stretching" to (v("Dehnen", "Etirements", "Estiramiento", "Stretching", "Растяжка") + mapOf(AppLanguage.EN to "Stretching", AppLanguage.CS to "Strečink")),
    "tmpl_steps" to (v("Schritte", "Pas", "Pasos", "Passi", "Шаги") + mapOf(AppLanguage.EN to "Steps", AppLanguage.CS to "Kroky")),
    "tmpl_meditation" to (v("Meditation", "Meditation", "Meditacion", "Meditazione", "Медитация") + mapOf(AppLanguage.EN to "Meditation", AppLanguage.CS to "Meditace")),
    "tmpl_no_phone" to (v("Kein Handy", "Sans téléphone", "Sin telefono", "Niente telefono", "Без телефона") + mapOf(AppLanguage.EN to "No phone", AppLanguage.CS to "Bez telefonu")),
    "tmpl_gratitude" to (v("Dankbarkeit", "Gratitude", "Gratitud", "Gratitudine", "Благодарность") + mapOf(AppLanguage.EN to "Gratitude", AppLanguage.CS to "Vděčnost")),
    "tmpl_reading" to (v("Lesen", "Lecture", "Lectura", "Lettura", "Чтение") + mapOf(AppLanguage.EN to "Reading", AppLanguage.CS to "Čtení")),
    "tmpl_journal" to (v("Tagebuch", "Journal", "Diario", "Diario", "Дневник") + mapOf(AppLanguage.EN to "Journal", AppLanguage.CS to "Deník")),
    "tmpl_learning" to (v("Lernen", "Apprentissage", "Aprendizaje", "Apprendimento", "Обучение") + mapOf(AppLanguage.EN to "Learning", AppLanguage.CS to "Učení")),
    "tmpl_no_alcohol" to (v("Kein Alkohol", "Sans alcool", "Sin alcohol", "Niente alcol", "Без алкоголя") + mapOf(AppLanguage.EN to "No alcohol", AppLanguage.CS to "Bez alkoholu")),
    "tmpl_no_junk_food" to (v("Kein Junkfood", "Sans malbouffe", "Sin comida basura", "Niente junk food", "Без фастфуда") + mapOf(AppLanguage.EN to "No junk food", AppLanguage.CS to "Bez nezdraveho jidla")),
    "tmpl_fruits_veggies" to (v("Obst & Gemüse", "Fruits & legumes", "Frutas y verduras", "Frutta e verdura", "Фрукты и овощи") + mapOf(AppLanguage.EN to "Fruits & veggies", AppLanguage.CS to "Ovoce a zelenina")),
    "tmpl_no_caffeine" to (v("Kein Koffein nach 14 Uhr", "Pas de cafeine apres 14h", "Sin cafeina despues de las 14:00", "Niente caffeina dopo le 14:00", "Без кофеина после 14:00") + mapOf(AppLanguage.EN to "No caffeine after 2pm", AppLanguage.CS to "Bez kofeinu po 14:00")),
    "tmpl_intermittent_fasting" to (v("Intervallfasten", "Jeune intermittent", "Ayuno intermitente", "Digiuno intermittente", "Интервальное голодание") + mapOf(AppLanguage.EN to "Intermittent fasting", AppLanguage.CS to "Prerusovany pust")),
    "tmpl_no_snacking" to (v("Kein Snacken abends", "Pas de grignotage le soir", "Sin picoteo nocturno", "Niente snack serali", "Без перекусов вечером") + mapOf(AppLanguage.EN to "No late snacking", AppLanguage.CS to "Zadne vecerni svaciny")),
    "tmpl_save_money" to (v("Geld sparen", "Economiser de l'argent", "Ahorrar dinero", "Risparmiare denaro", "Откладывать деньги") + mapOf(AppLanguage.EN to "Save money", AppLanguage.CS to "Setrit penize")),
    "tmpl_no_impulse_buy" to (v("Keine Impulskäufe", "Pas d'achats impulsifs", "Sin compras impulsivas", "Niente acquisti impulsivi", "Без импульсивных покупок") + mapOf(AppLanguage.EN to "No impulse purchases", AppLanguage.CS to "Zadne impulzivni nakupy")),
    "tmpl_track_expenses" to (v("Ausgaben verfolgen", "Suivre les depenses", "Registrar gastos", "Monitorare spese", "Учёт расходов") + mapOf(AppLanguage.EN to "Track expenses", AppLanguage.CS to "Sledovat vydaje")),
    "tmpl_invest" to (v("Täglich investieren", "Investir chaque jour", "Invertir cada dia", "Investire ogni giorno", "Инвестировать") + mapOf(AppLanguage.EN to "Invest daily", AppLanguage.CS to "Denne investovat")),
    "tmpl_budget_review" to (v("Budget überprüfen", "Reviser le budget", "Revisar presupuesto", "Rivedere il budget", "Проверка бюджета") + mapOf(AppLanguage.EN to "Review budget", AppLanguage.CS to "Kontrola rozpoctu")),
    "tmpl_draw" to (v("Zeichnen", "Dessin", "Dibujo", "Disegno", "Рисование") + mapOf(AppLanguage.EN to "Drawing", AppLanguage.CS to "Kresleni")),
    "tmpl_music" to (v("Instrument üben", "Pratiquer un instrument", "Practicar instrumento", "Praticare strumento", "Игра на инструменте") + mapOf(AppLanguage.EN to "Practice instrument", AppLanguage.CS to "Hra na nastroj")),
    "tmpl_creative_writing" to (v("Kreatives Schreiben", "Ecriture creative", "Escritura creativa", "Scrittura creativa", "Творческое письмо") + mapOf(AppLanguage.EN to "Creative writing", AppLanguage.CS to "Tvorci psani")),
    "tmpl_photography" to (v("Fotografie", "Photographie", "Fotografia", "Fotografia", "Фотография") + mapOf(AppLanguage.EN to "Photography", AppLanguage.CS to "Fotografie")),
    "tmpl_diy_craft" to (v("Basteln", "DIY / bricolage", "Manualidades", "Fai da te", "Рукоделие") + mapOf(AppLanguage.EN to "DIY / craft", AppLanguage.CS to "Tvoreni")),
    "tmpl_call_family" to (v("Familie anrufen", "Appeler la famille", "Llamar a la familia", "Chiamare la famiglia", "Позвонить близким") + mapOf(AppLanguage.EN to "Call family", AppLanguage.CS to "Zavolat rodine")),
    "tmpl_quality_time" to (v("Zeit mit Familie", "Temps de qualite", "Tiempo de calidad", "Tempo di qualita", "Время с близкими") + mapOf(AppLanguage.EN to "Quality time", AppLanguage.CS to "Cas s blizkymi")),
    "tmpl_no_social_media" to (v("Keine sozialen Medien", "Sans reseaux sociaux", "Sin redes sociales", "Niente social media", "Без соцсетей") + mapOf(AppLanguage.EN to "No social media", AppLanguage.CS to "Bez socialnich siti")),
    "tmpl_compliment" to (v("Kompliment machen", "Faire un compliment", "Dar un cumplido", "Fare un complimento", "Сделать комплимент") + mapOf(AppLanguage.EN to "Give a compliment", AppLanguage.CS to "Dat kompliment")),
    "tmpl_acts_of_kindness" to (v("Gute Tat", "Acte de gentillesse", "Acto de bondad", "Atto di gentilezza", "Доброе дело") + mapOf(AppLanguage.EN to "Act of kindness", AppLanguage.CS to "Dobry skutek")),
    "tmpl_floss" to (v("Zähne seidenfaden", "Fil dentaire", "Hilo dental", "Filo interdentale", "Зубная нить") + mapOf(AppLanguage.EN to "Floss teeth", AppLanguage.CS to "Mezizubni nit")),
    "tmpl_sunscreen" to (v("Sonnencreme", "Creme solaire", "Protector solar", "Crema solare", "Солнцезащитный крем") + mapOf(AppLanguage.EN to "Sunscreen", AppLanguage.CS to "Opalovaci krem")),
    "tmpl_posture" to (v("Haltungskontrolle", "Posture", "Control de postura", "Controllo postura", "Контроль осанки") + mapOf(AppLanguage.EN to "Posture check", AppLanguage.CS to "Kontrola drzeni tela")),
    "tmpl_cold_shower" to (v("Kaltdusche", "Douche froide", "Ducha fria", "Doccia fredda", "Холодный душ") + mapOf(AppLanguage.EN to "Cold shower", AppLanguage.CS to "Studena sprcha")),
    "tmpl_walk" to (v("Spaziergang", "Marche quotidienne", "Paseo diario", "Passeggiata", "Прогулка") + mapOf(AppLanguage.EN to "Daily walk", AppLanguage.CS to "Prochazka")),
    "tmpl_log_weight" to (v("Gewicht notieren", "Noter le poids", "Registrar peso", "Registrare peso", "Записать вес") + mapOf(AppLanguage.EN to "Log weight", AppLanguage.CS to "Zaznamenat vahu")),
    "tmpl_yoga" to (v("Yoga", "Yoga", "Yoga", "Yoga", "Йога") + mapOf(AppLanguage.EN to "Yoga", AppLanguage.CS to "Joga")),
    "tmpl_swimming" to (v("Schwimmen", "Natation", "Natacion", "Nuoto", "Плавание") + mapOf(AppLanguage.EN to "Swimming", AppLanguage.CS to "Plavani")),
    "tmpl_pushups" to (v("Liegestütze", "Pompes", "Flexiones", "Piegamenti", "Отжимания") + mapOf(AppLanguage.EN to "Push-ups", AppLanguage.CS to "Kliky")),
    "tmpl_pullups" to (v("Klimmzüge", "Tractions", "Dominadas", "Trazioni", "Подтягивания") + mapOf(AppLanguage.EN to "Pull-ups", AppLanguage.CS to "Shyby")),
    "tmpl_affirmations" to (v("Affirmationen", "Affirmations", "Afirmaciones", "Affermazioni", "Аффирмации") + mapOf(AppLanguage.EN to "Affirmations", AppLanguage.CS to "Afirmace")),
    "tmpl_breathwork" to (v("Atemübungen", "Respiration", "Respiracion", "Respirazione", "Дыхательные упражнения") + mapOf(AppLanguage.EN to "Breathwork", AppLanguage.CS to "Dechova cviceni")),
    "tmpl_mood_log" to (v("Stimmung aufzeichnen", "Suivi de l'humeur", "Registrar estado de animo", "Registro umore", "Отслеживать настроение") + mapOf(AppLanguage.EN to "Log mood", AppLanguage.CS to "Sledovat naladu")),
    "tmpl_digital_detox" to (v("Digitale Auszeit", "Detox numerique", "Desintoxicacion digital", "Detox digitale", "Час без гаджетов") + mapOf(AppLanguage.EN to "Digital detox hour", AppLanguage.CS to "Hodina bez technologii")),
    "tmpl_deep_work" to (v("Deep Work", "Deep work", "Trabajo profundo", "Deep work", "Глубокая работа") + mapOf(AppLanguage.EN to "Deep work", AppLanguage.CS to "Hluboka prace")),
    "tmpl_language" to (v("Sprachlernen", "Apprentissage des langues", "Aprendizaje de idiomas", "Studio della lingua", "Изучение языка") + mapOf(AppLanguage.EN to "Language learning", AppLanguage.CS to "Uceni jazyka")),
    "tmpl_plan_day" to (v("Tag planen", "Planifier la journee", "Planificar el dia", "Pianificare la giornata", "Планировать день") + mapOf(AppLanguage.EN to "Plan the day", AppLanguage.CS to "Planovat den")),
    "tmpl_inbox_zero" to (v("Posteingang leeren", "Boite zero", "Bandeja cero", "Inbox zero", "Очистить почту") + mapOf(AppLanguage.EN to "Inbox zero", AppLanguage.CS to "Vycistit schranku")),
    "tmpl_eat_frog" to (v("Schwierigste Aufgabe zuerst", "Commencer par le plus dur", "Empezar por lo mas dificil", "Inizia dal compito piu difficile", "Начать с трудного") + mapOf(AppLanguage.EN to "Start with the hardest task", AppLanguage.CS to "Zacit nejtizsim ukolem")),
    "tmpl_online_course" to (v("Online-Kurs", "Cours en ligne", "Curso en linea", "Corso online", "Онлайн-курс") + mapOf(AppLanguage.EN to "Online course", AppLanguage.CS to "Online kurz")),
    "unit_cup" to (v("Glas", "tasse", "taza", "tazza", "стакан") + mapOf(AppLanguage.EN to "cup", AppLanguage.CS to "sklenice")),
    "unit_min" to (v("Min", "min", "min", "min", "мин") + mapOf(AppLanguage.EN to "min", AppLanguage.CS to "min")),
    "unit_steps" to (v("Schritte", "pas", "pasos", "passi", "шагов") + mapOf(AppLanguage.EN to "steps", AppLanguage.CS to "kroků")),
    "milestone_next_goal" to (
        v("Next milestone", "Next milestone", "Next milestone", "Next milestone", "До следующей цели") +
            mapOf(AppLanguage.EN to "Next milestone", AppLanguage.CS to "Next milestone")
    ),
    "milestone_top_percent" to (
        v("You're in the top 0.01%", "You're in the top 0.01%", "You're in the top 0.01%", "You're in the top 0.01%", "Ты в 0.01% людей на планете") +
            mapOf(AppLanguage.EN to "You're in the top 0.01%", AppLanguage.CS to "You're in the top 0.01%")
    ),
    "milestone_badge_1" to (
        v("First step 🎉", "First step 🎉", "First step 🎉", "First step 🎉", "Первый шаг 🎉") +
            mapOf(AppLanguage.EN to "First step 🎉", AppLanguage.CS to "First step 🎉")
    ),
    "milestone_headline_1" to (
        v("Great start!", "Great start!", "Great start!", "Great start!", "Отличное начало!") +
            mapOf(AppLanguage.EN to "Great start!", AppLanguage.CS to "Great start!")
    ),
    "milestone_message_1" to (
        v("Every long journey begins with a single step. Come back tomorrow!", "Every long journey begins with a single step. Come back tomorrow!", "Every long journey begins with a single step. Come back tomorrow!", "Every long journey begins with a single step. Come back tomorrow!", "Самый длинный путь начинается с первого шага. Вернись завтра!") +
            mapOf(AppLanguage.EN to "Every long journey begins with a single step. Come back tomorrow!", AppLanguage.CS to "Every long journey begins with a single step. Come back tomorrow!")
    ),
    "milestone_cta_1" to (
        v("See you tomorrow 💪", "See you tomorrow 💪", "See you tomorrow 💪", "See you tomorrow 💪", "Буду завтра 💪") +
            mapOf(AppLanguage.EN to "See you tomorrow 💪", AppLanguage.CS to "See you tomorrow 💪")
    ),
    "milestone_badge_3" to (
        v("3 days done", "3 days done", "3 days done", "3 days done", "3 дня позади") +
            mapOf(AppLanguage.EN to "3 days done", AppLanguage.CS to "3 days done")
    ),
    "milestone_headline_3" to (
        v("Past the hard part!", "Past the hard part!", "Past the hard part!", "Past the hard part!", "Первые 3 — позади!") +
            mapOf(AppLanguage.EN to "Past the hard part!", AppLanguage.CS to "Past the hard part!")
    ),
    "milestone_message_3" to (
        v("Most people quit here. You didn't. Keep going!", "Most people quit here. You didn't. Keep going!", "Most people quit here. You didn't. Keep going!", "Most people quit here. You didn't. Keep going!", "Именно здесь большинство сдаётся. Ты — нет. Продолжай!") +
            mapOf(AppLanguage.EN to "Most people quit here. You didn't. Keep going!", AppLanguage.CS to "Most people quit here. You didn't. Keep going!")
    ),
    "milestone_cta_3" to (
        v("Keeping it up! 🔥", "Keeping it up! 🔥", "Keeping it up! 🔥", "Keeping it up! 🔥", "Продолжаю! 🔥") +
            mapOf(AppLanguage.EN to "Keeping it up! 🔥", AppLanguage.CS to "Keeping it up! 🔥")
    ),
    "milestone_badge_7" to (
        v("One week! 🔥", "One week! 🔥", "One week! 🔥", "One week! 🔥", "Неделя! 🔥") +
            mapOf(AppLanguage.EN to "One week! 🔥", AppLanguage.CS to "One week! 🔥")
    ),
    "milestone_headline_7" to (
        v("A whole week!", "A whole week!", "A whole week!", "A whole week!", "Целая неделя!") +
            mapOf(AppLanguage.EN to "A whole week!", AppLanguage.CS to "A whole week!")
    ),
    "milestone_message_7" to (
        v("You've proven you can. 21 days and it becomes automatic.", "You've proven you can. 21 days and it becomes automatic.", "You've proven you can. 21 days and it becomes automatic.", "You've proven you can. 21 days and it becomes automatic.", "Ты доказал себе что можешь. 21 день — и привычка станет автоматической.") +
            mapOf(AppLanguage.EN to "You've proven you can. 21 days and it becomes automatic.", AppLanguage.CS to "You've proven you can. 21 days and it becomes automatic.")
    ),
    "milestone_cta_7" to (
        v("Week one done 🔥", "Week one done 🔥", "Week one done 🔥", "Week one done 🔥", "Неделя пройдена 🔥") +
            mapOf(AppLanguage.EN to "Week one done 🔥", AppLanguage.CS to "Week one done 🔥")
    ),
    "milestone_badge_14" to (
        v("Two weeks", "Two weeks", "Two weeks", "Two weeks", "2 недели") +
            mapOf(AppLanguage.EN to "Two weeks", AppLanguage.CS to "Two weeks")
    ),
    "milestone_headline_14" to (
        v("Two weeks strong!", "Two weeks strong!", "Two weeks strong!", "Two weeks strong!", "Две недели силы!") +
            mapOf(AppLanguage.EN to "Two weeks strong!", AppLanguage.CS to "Two weeks strong!")
    ),
    "milestone_message_14" to (
        v("Your brain is rewiring. Neural pathways strengthen every day.", "Your brain is rewiring. Neural pathways strengthen every day.", "Your brain is rewiring. Neural pathways strengthen every day.", "Your brain is rewiring. Neural pathways strengthen every day.", "Твой мозг уже перестраивается. Нейронные связи крепнут с каждым днём.") +
            mapOf(AppLanguage.EN to "Your brain is rewiring. Neural pathways strengthen every day.", AppLanguage.CS to "Your brain is rewiring. Neural pathways strengthen every day.")
    ),
    "milestone_cta_14" to (
        v("On fire! 🔥", "On fire! 🔥", "On fire! 🔥", "On fire! 🔥", "Горю! 🔥") +
            mapOf(AppLanguage.EN to "On fire! 🔥", AppLanguage.CS to "On fire! 🔥")
    ),
    "milestone_badge_21" to (
        v("Magic number ⭐", "Magic number ⭐", "Magic number ⭐", "Magic number ⭐", "Магическое число ⭐") +
            mapOf(AppLanguage.EN to "Magic number ⭐", AppLanguage.CS to "Magic number ⭐")
    ),
    "milestone_headline_21" to (
        v("Habit forming!", "Habit forming!", "Habit forming!", "Habit forming!", "Привычка формируется!") +
            mapOf(AppLanguage.EN to "Habit forming!", AppLanguage.CS to "Habit forming!")
    ),
    "milestone_message_21" to (
        v("21 days — the number everyone knows. You're here. This is you now.", "21 days — the number everyone knows. You're here. This is you now.", "21 days — the number everyone knows. You're here. This is you now.", "21 days — the number everyone knows. You're here. This is you now.", "21 день — число которое все знают. Ты здесь. Это уже часть тебя.") +
            mapOf(AppLanguage.EN to "21 days — the number everyone knows. You're here. This is you now.", AppLanguage.CS to "21 days — the number everyone knows. You're here. This is you now.")
    ),
    "milestone_cta_21" to (
        v("21! Keeping going ⭐", "21! Keeping going ⭐", "21! Keeping going ⭐", "21! Keeping going ⭐", "21! Продолжаю ⭐") +
            mapOf(AppLanguage.EN to "21! Keeping going ⭐", AppLanguage.CS to "21! Keeping going ⭐")
    ),
    "milestone_badge_30" to (
        v("One month! 🏅", "One month! 🏅", "One month! 🏅", "One month! 🏅", "Месяц! 🏅") +
            mapOf(AppLanguage.EN to "One month! 🏅", AppLanguage.CS to "One month! 🏅")
    ),
    "milestone_headline_30" to (
        v("A full month!", "A full month!", "A full month!", "A full month!", "Целый месяц!") +
            mapOf(AppLanguage.EN to "A full month!", AppLanguage.CS to "A full month!")
    ),
    "milestone_message_30" to (
        v("This isn't luck anymore — it's you. A real achievement to be proud of.", "This isn't luck anymore — it's you. A real achievement to be proud of.", "This isn't luck anymore — it's you. A real achievement to be proud of.", "This isn't luck anymore — it's you. A real achievement to be proud of.", "Это уже не случайность — это ты. Серьёзное достижение которым стоит гордиться.") +
            mapOf(AppLanguage.EN to "This isn't luck anymore — it's you. A real achievement to be proud of.", AppLanguage.CS to "This isn't luck anymore — it's you. A real achievement to be proud of.")
    ),
    "milestone_cta_30" to (
        v("30 days — I'm good! ⭐", "30 days — I'm good! ⭐", "30 days — I'm good! ⭐", "30 days — I'm good! ⭐", "30 дней — я крут! ⭐") +
            mapOf(AppLanguage.EN to "30 days — I'm good! ⭐", AppLanguage.CS to "30 days — I'm good! ⭐")
    ),
    "milestone_badge_50" to (
        v("50 days 💪", "50 days 💪", "50 days 💪", "50 days 💪", "50 дней 💪") +
            mapOf(AppLanguage.EN to "50 days 💪", AppLanguage.CS to "50 days 💪")
    ),
    "milestone_headline_50" to (
        v("Halfway to legendary!", "Halfway to legendary!", "Halfway to legendary!", "Halfway to legendary!", "Полпути к легенде!") +
            mapOf(AppLanguage.EN to "Halfway to legendary!", AppLanguage.CS to "Halfway to legendary!")
    ),
    "milestone_message_50" to (
        v("50 days of pure discipline. Most people never get here.", "50 days of pure discipline. Most people never get here.", "50 days of pure discipline. Most people never get here.", "50 days of pure discipline. Most people never get here.", "50 дней чистой дисциплины. Большинство людей до этого не доходят.") +
            mapOf(AppLanguage.EN to "50 days of pure discipline. Most people never get here.", AppLanguage.CS to "50 days of pure discipline. Most people never get here.")
    ),
    "milestone_cta_50" to (
        v("On to 66! ⚡", "On to 66! ⚡", "On to 66! ⚡", "On to 66! ⚡", "Продолжаю к 66 ⚡") +
            mapOf(AppLanguage.EN to "On to 66! ⚡", AppLanguage.CS to "On to 66! ⚡")
    ),
    "milestone_badge_66" to (
        v("Science confirmed ⚡", "Science confirmed ⚡", "Science confirmed ⚡", "Science confirmed ⚡", "Наука доказала ⚡") +
            mapOf(AppLanguage.EN to "Science confirmed ⚡", AppLanguage.CS to "Science confirmed ⚡")
    ),
    "milestone_headline_66" to (
        v("Autopilot on!", "Autopilot on!", "Autopilot on!", "Autopilot on!", "Автопилот включён!") +
            mapOf(AppLanguage.EN to "Autopilot on!", AppLanguage.CS to "Autopilot on!")
    ),
    "milestone_message_66" to (
        v("UCL researchers found 66 days is the real habit threshold. You're there.", "UCL researchers found 66 days is the real habit threshold. You're there.", "UCL researchers found 66 days is the real habit threshold. You're there.", "UCL researchers found 66 days is the real habit threshold. You're there.", "Учёные UCL выяснили: именно 66 дней нужно для автоматизма. Ты там.") +
            mapOf(AppLanguage.EN to "UCL researchers found 66 days is the real habit threshold. You're there.", AppLanguage.CS to "UCL researchers found 66 days is the real habit threshold. You're there.")
    ),
    "milestone_cta_66" to (
        v("Running on autopilot! ⚡", "Running on autopilot! ⚡", "Running on autopilot! ⚡", "Running on autopilot! ⚡", "Это уже автоматически! ⚡") +
            mapOf(AppLanguage.EN to "Running on autopilot! ⚡", AppLanguage.CS to "Running on autopilot! ⚡")
    ),
    "milestone_badge_100" to (
        v("100! 💎", "100! 💎", "100! 💎", "100! 💎", "100! 💎") +
            mapOf(AppLanguage.EN to "100! 💎", AppLanguage.CS to "100! 💎")
    ),
    "milestone_headline_100" to (
        v("Triple digits.", "Triple digits.", "Triple digits.", "Triple digits.", "Три значки. Сотня.") +
            mapOf(AppLanguage.EN to "Triple digits.", AppLanguage.CS to "Triple digits.")
    ),
    "milestone_message_100" to (
        v("100 days isn't just a habit. It's a new version of you.", "100 days isn't just a habit. It's a new version of you.", "100 days isn't just a habit. It's a new version of you.", "100 days isn't just a habit. It's a new version of you.", "100 дней — это не просто привычка. Это новая версия тебя.") +
            mapOf(AppLanguage.EN to "100 days isn't just a habit. It's a new version of you.", AppLanguage.CS to "100 days isn't just a habit. It's a new version of you.")
    ),
    "milestone_cta_100" to (
        v("100 days — legendary! 💎", "100 days — legendary! 💎", "100 days — legendary! 💎", "100 days — legendary! 💎", "100 дней — легенда! 💎") +
            mapOf(AppLanguage.EN to "100 days — legendary! 💎", AppLanguage.CS to "100 days — legendary! 💎")
    ),
    "milestone_badge_180" to (
        v("Half a year 🌟", "Half a year 🌟", "Half a year 🌟", "Half a year 🌟", "Полгода 🌟") +
            mapOf(AppLanguage.EN to "Half a year 🌟", AppLanguage.CS to "Half a year 🌟")
    ),
    "milestone_headline_180" to (
        v("Six months straight!", "Six months straight!", "Six months straight!", "Six months straight!", "Полгода без остановок!") +
            mapOf(AppLanguage.EN to "Six months straight!", AppLanguage.CS to "Six months straight!")
    ),
    "milestone_message_180" to (
        v("180 days. You've changed yourself at the cellular level.", "180 days. You've changed yourself at the cellular level.", "180 days. You've changed yourself at the cellular level.", "180 days. You've changed yourself at the cellular level.", "180 дней — это серьёзно. Ты изменил себя на клеточном уровне.") +
            mapOf(AppLanguage.EN to "180 days. You've changed yourself at the cellular level.", AppLanguage.CS to "180 days. You've changed yourself at the cellular level.")
    ),
    "milestone_cta_180" to (
        v("Half a year! 🌟", "Half a year! 🌟", "Half a year! 🌟", "Half a year! 🌟", "Полгода! Продолжаю 🌟") +
            mapOf(AppLanguage.EN to "Half a year! 🌟", AppLanguage.CS to "Half a year! 🌟")
    ),
    "milestone_badge_365" to (
        v("One year! 👑", "One year! 👑", "One year! 👑", "One year! 👑", "Год! 👑") +
            mapOf(AppLanguage.EN to "One year! 👑", AppLanguage.CS to "One year! 👑")
    ),
    "milestone_headline_365" to (
        v("A full year. No breaks.", "A full year. No breaks.", "A full year. No breaks.", "A full year. No breaks.", "Целый год. Без остановок.") +
            mapOf(AppLanguage.EN to "A full year. No breaks.", AppLanguage.CS to "A full year. No breaks.")
    ),
    "milestone_message_365" to (
        v("Only 1% of people get here. You changed your life.", "Only 1% of people get here. You changed your life.", "Only 1% of people get here. You changed your life.", "Only 1% of people get here. You changed your life.", "Только 1% людей достигают этого. Ты изменил свою жизнь.") +
            mapOf(AppLanguage.EN to "Only 1% of people get here. You changed your life.", AppLanguage.CS to "Only 1% of people get here. You changed your life.")
    ),
    "milestone_cta_365" to (
        v("I'm a legend 👑", "I'm a legend 👑", "I'm a legend 👑", "I'm a legend 👑", "Я — легенда 👑") +
            mapOf(AppLanguage.EN to "I'm a legend 👑", AppLanguage.CS to "I'm a legend 👑")
    ),
    "milestone_badge_500" to (
        v("500 days 🔮", "500 days 🔮", "500 days 🔮", "500 days 🔮", "500 дней 🔮") +
            mapOf(AppLanguage.EN to "500 days 🔮", AppLanguage.CS to "500 days 🔮")
    ),
    "milestone_headline_500" to (
        v("Five hundred.", "Five hundred.", "Five hundred.", "Five hundred.", "Пятьсот.") +
            mapOf(AppLanguage.EN to "Five hundred.", AppLanguage.CS to "Five hundred.")
    ),
    "milestone_message_500" to (
        v("500 decisions. Every single day — your choice.", "500 decisions. Every single day — your choice.", "500 decisions. Every single day — your choice.", "500 decisions. Every single day — your choice.", "За этой цифрой стоят 500 решений. Каждый день — твой выбор.") +
            mapOf(AppLanguage.EN to "500 decisions. Every single day — your choice.", AppLanguage.CS to "500 decisions. Every single day — your choice.")
    ),
    "milestone_cta_500" to (
        v("500. And beyond 🔮", "500. And beyond 🔮", "500. And beyond 🔮", "500. And beyond 🔮", "500. И дальше 🔮") +
            mapOf(AppLanguage.EN to "500. And beyond 🔮", AppLanguage.CS to "500. And beyond 🔮")
    ),
    "milestone_badge_1000" to (
        v("One thousand ∞", "One thousand ∞", "One thousand ∞", "One thousand ∞", "Тысяча ∞") +
            mapOf(AppLanguage.EN to "One thousand ∞", AppLanguage.CS to "One thousand ∞")
    ),
    "milestone_headline_1000" to (
        v("This is who you are.", "This is who you are.", "This is who you are.", "This is who you are.", "Это больше не привычка.") +
            mapOf(AppLanguage.EN to "This is who you are.", AppLanguage.CS to "This is who you are.")
    ),
    "milestone_message_1000" to (
        v("It's part of you at a cellular level. Nearly 3 years. No words.", "It's part of you at a cellular level. Nearly 3 years. No words.", "It's part of you at a cellular level. Nearly 3 years. No words.", "It's part of you at a cellular level. Nearly 3 years. No words.", "Это часть тебя на клеточном уровне. Почти 3 года. Слов нет.") +
            mapOf(AppLanguage.EN to "It's part of you at a cellular level. Nearly 3 years. No words.", AppLanguage.CS to "It's part of you at a cellular level. Nearly 3 years. No words.")
    ),
    "milestone_cta_1000" to (
        v("1000. Unbelievable ∞", "1000. Unbelievable ∞", "1000. Unbelievable ∞", "1000. Unbelievable ∞", "1000. Это невероятно ∞") +
            mapOf(AppLanguage.EN to "1000. Unbelievable ∞", AppLanguage.CS to "1000. Unbelievable ∞")
    ),
    "dd MMM yyyy" to v("dd MMM yyyy", "dd MMM yyyy", "dd MMM yyyy", "dd MMM yyyy", "dd MMM yyyy"),
    "LLLL yyyy" to v("LLLL yyyy", "LLLL yyyy", "LLLL yyyy", "LLLL yyyy", "LLLL yyyy")
)

fun localeForLanguage(language: AppLanguage): Locale = when (language) {
    AppLanguage.EN -> Locale.ENGLISH
    AppLanguage.CS -> Locale.forLanguageTag("cs")
    AppLanguage.DE -> Locale.GERMAN
    AppLanguage.FR -> Locale.FRENCH
    AppLanguage.ES -> Locale.forLanguageTag("es")
    AppLanguage.IT -> Locale.ITALIAN
    AppLanguage.RU -> Locale.forLanguageTag("ru")
    AppLanguage.UK -> Locale.forLanguageTag("uk")
}

fun languageNativeLabel(language: AppLanguage): String = when (language) {
    AppLanguage.EN -> "English"
    AppLanguage.CS -> "Čeština"
    AppLanguage.DE -> "Deutsch"
    AppLanguage.FR -> "Francais"
    AppLanguage.ES -> "Espanol"
    AppLanguage.IT -> "Italiano"
    AppLanguage.RU -> "Русский"
    AppLanguage.UK -> "Українська"
}

fun translate(language: AppLanguage, source: String): String {
    val localized = when (language) {
        AppLanguage.UK -> ukTranslations[source]
        else -> translations[source]?.get(language)
    }
    if (!localized.isNullOrBlank()) return localized
    return translations[source]?.get(AppLanguage.EN) ?: source
}

fun formatTranslate(language: AppLanguage, source: String, vararg args: Any): String {
    return String.format(localeForLanguage(language), translate(language, source), *args)
}

fun widgetCtaNotStarted(language: AppLanguage, n: Int, unit: String): String {
    return if (unit.isBlank()) {
        formatTranslate(language, "widget_cta_not_started_no_unit", n)
    } else {
        formatTranslate(language, "widget_cta_not_started", n, unit)
    }
}

fun widgetCtaInProgress(language: AppLanguage, n: Int, unit: String): String {
    return if (unit.isBlank()) {
        formatTranslate(language, "widget_cta_in_progress_no_unit", n)
    } else {
        formatTranslate(language, "widget_cta_in_progress", n, unit)
    }
}

fun widgetGoalReached(language: AppLanguage): String {
    return translate(language, "widget_goal_reached")
}

fun widgetCtaDone(language: AppLanguage): String {
    return translate(language, "widget_cta_done")
}

fun widgetIncBtn(language: AppLanguage, n: Int, unit: String): String {
    return formatTranslate(language, "widget_inc_btn_min", n, unit)
}

fun streakDaysUnit(language: AppLanguage, n: Int): String {
    val value = n.coerceAtLeast(0)
    val mod10 = value % 10
    val mod100 = value % 100
    return when (language) {
        AppLanguage.RU -> when {
            mod10 == 1 && mod100 != 11 -> "день"
            mod10 in 2..4 && mod100 !in 12..14 -> "дня"
            else -> "дней"
        }
        AppLanguage.UK -> when {
            mod10 == 1 && mod100 != 11 -> "день"
            mod10 in 2..4 && mod100 !in 12..14 -> "дні"
            else -> "днів"
        }
        AppLanguage.CS -> when {
            value == 1 -> "den"
            mod10 in 2..4 && mod100 !in 12..14 -> "dny"
            else -> "dní"
        }
        AppLanguage.DE -> if (value == 1) "Tag" else "Tage"
        AppLanguage.FR -> if (value <= 1) "jour" else "jours"
        AppLanguage.ES -> if (value == 1) "día" else "días"
        AppLanguage.IT -> if (value == 1) "giorno" else "giorni"
        AppLanguage.EN -> if (value == 1) "day" else "days"
    }
}

@Composable
@ReadOnlyComposable
fun streakDaysUnit(n: Int): String = streakDaysUnit(LocalAppLanguage.current, n)

fun weekdayLabels(language: AppLanguage): List<String> {
    val locale = localeForLanguage(language)
    return (1..7).map { day ->
        DayOfWeek.of(day).getDisplayName(TextStyle.SHORT, locale)
    }
}

@Composable
@ReadOnlyComposable
fun t(source: String): String = translate(LocalAppLanguage.current, source)

@Composable
@ReadOnlyComposable
fun tf(source: String, vararg args: Any): String {
    return formatTranslate(LocalAppLanguage.current, source, *args)
}

@Composable
@ReadOnlyComposable
fun appLocale(): Locale = localeForLanguage(LocalAppLanguage.current)




