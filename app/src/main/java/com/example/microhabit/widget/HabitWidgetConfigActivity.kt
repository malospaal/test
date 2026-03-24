package com.example.microhabit.widget

import android.appwidget.AppWidgetManager
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.microhabit.MainActivity
import com.example.microhabit.data.HabitTask
import com.example.microhabit.ui.theme.AppTheme
import com.example.microhabit.ui.theme.MicroHabitTheme

class HabitWidgetConfigActivity : ComponentActivity() {
    private var appWidgetId: Int = AppWidgetManager.INVALID_APPWIDGET_ID

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        appWidgetId = intent?.extras?.getInt(
            AppWidgetManager.EXTRA_APPWIDGET_ID,
            AppWidgetManager.INVALID_APPWIDGET_ID
        ) ?: AppWidgetManager.INVALID_APPWIDGET_ID
        if (appWidgetId == AppWidgetManager.INVALID_APPWIDGET_ID) {
            finish()
            return
        }
        setResult(RESULT_CANCELED)

        val dataProvider = WidgetDataProvider(this)
        val isProUser = dataProvider.isProUser()
        val activeHabits = dataProvider.getActiveHabits()
        WidgetDebugLog.d(
            "ConfigActivity onCreate appWidgetId=$appWidgetId isPro=$isProUser activeHabits=${activeHabits.size}"
        )

        if (!isProUser) {
            setContent {
                MicroHabitTheme {
                    WidgetUpsellScreen(
                        onOpenPro = {
                            startActivity(Intent(this, MainActivity::class.java))
                            finish()
                        },
                        onCancel = { finish() }
                    )
                }
            }
            return
        }

        if (activeHabits.isEmpty()) {
            finish()
            return
        }

        if (activeHabits.size == 1) {
            bindHabitAndFinish(activeHabits.first().id)
            return
        }

        setContent {
            MicroHabitTheme {
                HabitSelectionScreen(
                    habits = activeHabits,
                    onHabitSelected = ::bindHabitAndFinish,
                    onCancel = { finish() }
                )
            }
        }
    }

    private fun bindHabitAndFinish(habitId: String) {
        WidgetDebugLog.d("ConfigActivity bindHabit appWidgetId=$appWidgetId habitId=$habitId")
        WidgetBindingStore.setHabitId(this, appWidgetId, habitId)
        WidgetUpdateTrigger.triggerUpdate(this)
        HabitWidgetUpdateScheduler.scheduleWidgetUpdates(this)
        val result = Intent().putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId)
        setResult(RESULT_OK, result)
        finish()
    }
}

@Composable
private fun WidgetUpsellScreen(
    onOpenPro: () -> Unit,
    onCancel: () -> Unit
) {
    val colors = AppTheme.colors
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(20.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Widgets are available in PRO",
            style = MaterialTheme.typography.titleMedium,
            color = colors.textPrimary,
            fontWeight = FontWeight.SemiBold
        )
        Spacer(Modifier.height(8.dp))
        Text(
            text = "Open app to upgrade and add this widget.",
            style = MaterialTheme.typography.bodyMedium,
            color = colors.textSecondary
        )
        Spacer(Modifier.height(16.dp))
        Button(onClick = onOpenPro, modifier = Modifier.fillMaxWidth()) {
            Text("Open PRO")
        }
        Spacer(Modifier.height(8.dp))
        Text(
            text = "Cancel",
            modifier = Modifier.clickable(onClick = onCancel),
            style = MaterialTheme.typography.labelLarge,
            color = colors.textSecondary
        )
    }
}

@Composable
private fun HabitSelectionScreen(
    habits: List<HabitTask>,
    onHabitSelected: (String) -> Unit,
    onCancel: () -> Unit
) {
    val colors = AppTheme.colors
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(
            text = "Choose a habit for widget",
            style = MaterialTheme.typography.titleMedium,
            color = colors.textPrimary,
            fontWeight = FontWeight.SemiBold
        )
        Spacer(Modifier.height(12.dp))
        LazyColumn(
            modifier = Modifier
                .fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(habits, key = { it.id }) { habit ->
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onHabitSelected(habit.id) },
                    shape = RoundedCornerShape(12.dp),
                    color = colors.backgroundSurface,
                    tonalElevation = 0.dp
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 12.dp, vertical = 10.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = habit.emoji,
                            fontSize = 18.sp,
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(Modifier.width(10.dp))
                        Text(
                            text = habit.title,
                            style = MaterialTheme.typography.bodyLarge,
                            color = colors.textPrimary,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }
            }
        }
        Spacer(Modifier.height(8.dp))
        Text(
            text = "Cancel",
            modifier = Modifier.clickable(onClick = onCancel),
            style = MaterialTheme.typography.labelLarge,
            color = colors.textSecondary
        )
    }
}
