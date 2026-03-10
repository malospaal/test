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
    "Habits" to v("Gewohnheiten", "Habitudes", "Habitos", "Abitudini", "Привычки"),
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
    "Menu" to v("Menu", "Menu", "Menu", "Menu", "Меню"),
    "Add" to v("Hinzufugen", "Ajouter", "Anadir", "Aggiungi", "Добавить"),
    "Upgrade" to v("Upgrade", "Mettre a niveau", "Mejorar", "Aggiorna", "Улучшить"),
    "PRO yearly activated (debug)" to v(
        "PRO-Jahresplan aktiviert (Debug)",
        "PRO annuel active (debug)",
        "PRO anual activado (debug)",
        "PRO annuale attivato (debug)",
        "PRO на год активирован (debug)"
    ),
    "PRO monthly activated (debug)" to v(
        "PRO-Monatsplan aktiviert (Debug)",
        "PRO mensuel active (debug)",
        "PRO mensual activado (debug)",
        "PRO mensile attivato (debug)",
        "PRO на месяц активирован (debug)"
    ),
    "Purchases restored (debug)" to v(
        "Kaufe wiederhergestellt (Debug)",
        "Achats restaures (debug)",
        "Compras restauradas (debug)",
        "Acquisti ripristinati (debug)",
        "Покупки восстановлены (debug)"
    ),
    "Micro-habit" to v("Micro-Habit", "Micro-habitude", "Micro-habito", "Micro-abitudine", "Микро-привычка"),
    "Plan: PRO" to v("Plan: PRO", "Forfait : PRO", "Plan: PRO", "Piano: PRO", "Тариф: PRO"),
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
    "Upgrade to PRO" to v("Auf PRO upgraden", "Passer a PRO", "Mejorar a PRO", "Passa a PRO", "Обновиться до PRO"),
    "Active habits" to v("Aktive Gewohnheiten", "Habitudes actives", "Habitos activos", "Abitudini attive", "Активные привычки"),
    "Archived habits" to v("Archivierte Gewohnheiten", "Habitudes archivees", "Habitos archivados", "Abitudini archiviate", "Архивные привычки"),
    "Create and select a habit to view analytics." to v(
        "Erstelle und wahle eine Gewohnheit, um Analysen zu sehen.",
        "Creez et selectionnez une habitude pour voir les analyses.",
        "Crea y selecciona un habito para ver analitica.",
        "Crea e seleziona un'abitudine per vedere le analisi.",
        "Создайте и выберите привычку, чтобы увидеть аналитику."
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
    "Completion details" to v("Details zur Erfullung", "Details de realisation", "Detalles de cumplimiento", "Dettagli completamento", "Детали выполнения"),
    "Completed" to v("Abgeschlossen", "Termine", "Completado", "Completato", "Выполнено"),
    "Missed" to v("Verpasst", "Manque", "Perdido", "Saltato", "Пропущено"),
    "Future" to v("Zukunft", "Futur", "Futuro", "Futuro", "Будущее"),
    "You are on PRO. Manage options in Premium." to v(
        "Du hast PRO. Verwalte Optionen im Premium-Bereich.",
        "Vous etes sur PRO. Gere vos options dans Premium.",
        "Estas en PRO. Gestiona opciones en Premium.",
        "Sei su PRO. Gestisci le opzioni in Premium.",
        "У вас PRO. Управляйте параметрами в разделе Премиум."
    ),
    "You are on Free. Upgrade to unlock unlimited habits." to v(
        "Du hast Free. Upgrade fur unbegrenzte Gewohnheiten.",
        "Vous etes sur Free. Passez a niveau pour des habitudes illimitees.",
        "Estas en Free. Mejora para desbloquear habitos ilimitados.",
        "Sei su Free. Passa a PRO per abitudini illimitate.",
        "У вас Free. Обновитесь, чтобы открыть безлимитные привычки."
    ),
    "Open Premium" to v("Premium offnen", "Ouvrir Premium", "Abrir Premium", "Apri Premium", "Открыть Премиум"),
    "Upgrade to Premium" to v("Auf Premium upgraden", "Passer a Premium", "Mejorar a Premium", "Passa a Premium", "Обновиться до Премиум"),
    "Free" to v("Kostenlos", "Gratuit", "Gratis", "Gratis", "Бесплатный"),
    "1 habit" to v("1 Gewohnheit", "1 habitude", "1 habito", "1 abitudine", "1 привычка"),
    "Current" to v("Aktuell", "Actuel", "Actual", "Attuale", "Текущий"),
    "Choose" to v("Wahlen", "Choisir", "Elegir", "Scegli", "Выбрать"),
    "Unlimited habits" to v("Unbegrenzte Gewohnheiten", "Habitudes illimitees", "Habitos ilimitados", "Abitudini illimitate", "Безлимитные привычки"),
    "Choose PRO" to v("PRO wahlen", "Choisir PRO", "Elegir PRO", "Scegli PRO", "Выбрать PRO"),
    "Profile and app usage overview." to v(
        "Profil und Nutzungsubersicht.",
        "Profil et apercu d'utilisation.",
        "Perfil y resumen de uso de la app.",
        "Profilo e panoramica utilizzo app.",
        "Профиль и обзор использования приложения."
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
    "Daily reminders and nudges." to v("Tagliche Erinnerungen.", "Rappels quotidiens.", "Recordatorios diarios.", "Promemoria giornalieri.", "Ежедневные напоминания."),
    "Reminders" to v("Erinnerungen", "Rappels", "Recordatorios", "Promemoria", "Напоминания"),
    "Enable habit reminder notifications" to v("Erinnerungen fur Gewohnheiten aktivieren", "Activer les rappels d'habitude", "Activar recordatorios de habitos", "Abilita promemoria abitudini", "Включить уведомления-напоминания"),
    "Reminder time" to v("Erinnerungszeit", "Heure de rappel", "Hora del recordatorio", "Orario promemoria", "Время напоминания"),
    "Daily notification time" to v("Tagliche Benachrichtigungszeit", "Heure quotidienne de notification", "Hora diaria de notificacion", "Orario notifica giornaliera", "Ежедневное время уведомления"),
    "Subscription" to v("Abonnement", "Abonnement", "Suscripcion", "Abbonamento", "Подписка"),
    "Manage Free and PRO plans." to v("Free- und PRO-Plane verwalten.", "Gerer les offres Free et PRO.", "Gestiona planes Free y PRO.", "Gestisci i piani Free e PRO.", "Управляйте тарифами Free и PRO."),
    "Manage subscription" to v("Abo verwalten", "Gerer l'abonnement", "Gestionar suscripcion", "Gestisci abbonamento", "Управление подпиской"),
    "PRO active: unlimited habits" to v("PRO aktiv: unbegrenzte Gewohnheiten", "PRO actif : habitudes illimitees", "PRO activo: habitos ilimitados", "PRO attivo: abitudini illimitate", "PRO активен: безлимитные привычки"),
    "Free plan: one active habit" to v("Free-Plan: eine aktive Gewohnheit", "Plan Free : une habitude active", "Plan Free: un habito activo", "Piano Free: una abitudine attiva", "План Free: одна активная привычка"),
    "Data & Privacy" to v("Daten & Datenschutz", "Donnees et confidentialite", "Datos y privacidad", "Dati e privacy", "Данные и приватность"),
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
    "Delete account?" to v("Konto loschen?", "Supprimer le compte ?", "Eliminar cuenta?", "Eliminare account?", "Удалить аккаунт?"),
    "Delete habit?" to v("Gewohnheit loschen?", "Supprimer l'habitude ?", "Eliminar habito?", "Eliminare abitudine?", "Удалить привычку?"),
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
    "Advanced analytics" to v("Erweiterte Analysen", "Analyses avancees", "Analitica avanzada", "Analisi avanzate", "Расширенная аналитика"),
    "Priority support" to v("Priorisierter Support", "Support prioritaire", "Soporte prioritario", "Supporto prioritario", "Приоритетная поддержка"),
    "Monthly" to v("Monatlich", "Mensuel", "Mensual", "Mensile", "Ежемесячно"),
    "Flexible monthly billing" to v("Flexible monatliche Abrechnung", "Facturation mensuelle flexible", "Facturacion mensual flexible", "Fatturazione mensile flessibile", "Гибкая ежемесячная оплата"),
    "Yearly" to v("Jahrlich", "Annuel", "Anual", "Annuale", "Ежегодно"),
    "Equivalent to \$3.33 / month" to v("Entspricht \$3.33 / Monat", "Equivalent a \$3.33 / mois", "Equivale a \$3.33 / mes", "Equivalente a \$3.33 / mese", "Эквивалент \$3.33 / месяц"),
    "Recommended" to v("Empfohlen", "Recommande", "Recomendado", "Consigliato", "Рекомендуется"),
    "Premium active" to v("Premium aktiv", "Premium actif", "Premium activo", "Premium attivo", "Премиум активен"),
    "Continue with %s" to v("Fortfahren mit %s", "Continuer avec %s", "Continuar con %s", "Continua con %s", "Продолжить с %s"),
    "Restore purchase" to v("Kauf wiederherstellen", "Restaurer l'achat", "Restaurar compra", "Ripristina acquisto", "Восстановить покупку"),
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
    "Mark as done" to v("Als erledigt markieren", "Marquer comme fait", "Marcar como hecho", "Segna come completata", "Отметить как выполнено"),
    "Great job, your streak is safe." to v("Super, deine Serie ist gesichert.", "Bravo, votre serie est preservee.", "Buen trabajo, tu racha esta a salvo.", "Ottimo lavoro, la tua serie e salva.", "Отлично, ваша серия сохранена."),
    "7 day chart" to v("7-Tage-Chart", "Graphique sur 7 jours", "Grafico de 7 dias", "Grafico 7 giorni", "График за 7 дней"),
    "W" to v("KW ", "S", "S", "S", "Н"),
    "Tracking type" to v("Tracking-Typ", "Type de suivi", "Tipo de seguimiento", "Tipo di monitoraggio", "Тип отслеживания"),
    "Frequency" to v("Frequenz", "Frequence", "Frecuencia", "Frequenza", "Частота"),
    "Basic setup" to v("Basiseinstellungen", "Configuration de base", "Configuracion basica", "Impostazioni base", "Базовые настройки"),
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
    "Start date: %s" to v("Startdatum: %s", "Date de debut : %s", "Fecha de inicio: %s", "Data di inizio: %s", "Дата начала: %s"),
    "Fill required fields to continue." to v("Fulle die erforderlichen Felder aus.", "Remplissez les champs obligatoires.", "Completa los campos requeridos.", "Compila i campi richiesti.", "Заполните обязательные поля."),
    "Save habit" to v("Gewohnheit speichern", "Enregistrer l'habitude", "Guardar habito", "Salva abitudine", "Сохранить привычку"),
    "Save changes" to v("Anderungen speichern", "Enregistrer les modifications", "Guardar cambios", "Salva modifiche", "Сохранить изменения"),
    "Create Habit" to v("Gewohnheit erstellen", "Creer une habitude", "Crear habito", "Crea abitudine", "Создать привычку"),
    "Edit Habit" to v("Gewohnheit bearbeiten", "Modifier l'habitude", "Editar habito", "Modifica abitudine", "Редактировать привычку"),
    "Yes / No" to v("Ja / Nein", "Oui / Non", "Si / No", "Si / No", "Да / Нет"),
    "Count" to v("Anzahl", "Compteur", "Cantidad", "Conteggio", "Количество"),
    "Duration" to v("Dauer", "Duree", "Duracion", "Durata", "Длительность"),
    "Archived" to v("Archiviert", "Archive", "Archivado", "Archiviata", "В архиве"),
    "Active" to v("Aktiv", "Active", "Activo", "Attiva", "Активна"),
    "Streak" to v("Serie", "Serie", "Racha", "Serie", "Серия"),
    "Completion" to v("Erfullung", "Realisation", "Cumplimiento", "Completamento", "Выполнение"),
    "Unarchive" to v("Wiederherstellen", "Restaurer", "Desarchivar", "Ripristina", "Разархивировать"),
    "Archive" to v("Archivieren", "Archiver", "Archivar", "Archivia", "В архив"),
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
    if (language == AppLanguage.EN) return source
    if (language == AppLanguage.UK) return ukTranslations[source] ?: source
    return translations[source]?.get(language) ?: source
}

fun formatTranslate(language: AppLanguage, source: String, vararg args: Any): String {
    return String.format(localeForLanguage(language), translate(language, source), *args)
}

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
