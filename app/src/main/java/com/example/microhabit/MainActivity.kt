package com.example.microhabit

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.microhabit.data.HabitRepository
import com.example.microhabit.notifications.HabitReminderScheduler
import com.example.microhabit.ui.app.HabitApp
import com.example.microhabit.ui.theme.MicroHabitTheme
import com.example.microhabit.widget.HabitWidgetUpdateScheduler
import com.example.microhabit.widget.WidgetDebugLog
import com.example.microhabit.widget.WidgetUpdateTrigger

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val repository = HabitRepository(applicationContext)
        val reminderScheduler = HabitReminderScheduler(applicationContext, repository)
        reminderScheduler.ensureNotificationChannel()
        reminderScheduler.syncAllReminders()
        HabitWidgetUpdateScheduler.scheduleWidgetUpdates(applicationContext)

        setContent {
            val vm: MainViewModel = viewModel(
                factory = MainViewModel.Factory(repository, reminderScheduler)
            )
            val state by vm.state.collectAsState()

            MicroHabitTheme(themeMode = state.themeMode) {
                Surface(modifier = Modifier.fillMaxSize()) {
                    HabitApp(state = state, vm = vm)
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        WidgetDebugLog.d("MainActivity.onResume trigger widget refresh")
        WidgetUpdateTrigger.triggerUpdate(this)
    }
}
