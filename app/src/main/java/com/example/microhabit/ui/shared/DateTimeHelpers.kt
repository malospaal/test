package com.example.microhabit.ui.shared

import android.app.Activity
import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Context
import android.content.ContextWrapper
import android.content.Intent
import android.net.Uri
import android.provider.Settings
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.fragment.app.FragmentActivity
import com.example.microhabit.i18n.t
import com.example.microhabit.ui.theme.AppTheme
import com.google.android.material.datepicker.CalendarConstraints
import com.google.android.material.datepicker.DateValidatorPointForward
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.android.material.timepicker.MaterialTimePicker
import com.google.android.material.timepicker.TimeFormat
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.time.ZoneOffset

@Composable
internal fun CalendarHeaderRow(
    monthLabel: String,
    isTodaySelected: Boolean,
    onPrev: () -> Unit,
    onToday: () -> Unit,
    onNext: () -> Unit,
    todayButtonBorderColor: Color? = null,
    todayButtonTextColor: Color? = null,
    todayButtonHeight: Dp = 30.dp,
    todayButtonBorderWidth: Dp? = null
) {
    val spacing = AppTheme.spacing
    val radius = AppTheme.radius
    val stroke = AppTheme.stroke
    val colors = AppTheme.colors
    val todayBtnBorderColor = todayButtonBorderColor ?: colors.borderSubtle.copy(alpha = 0.65f)
    val todayBtnTextColor = todayButtonTextColor ?: colors.textSecondary
    val resolvedTodayBorderWidth = todayButtonBorderWidth ?: stroke.thin

    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = monthLabel,
            modifier = Modifier.weight(1f),
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.SemiBold,
            color = colors.textPrimary
        )
        Row(
            horizontalArrangement = Arrangement.spacedBy(spacing.x0_5),
            verticalAlignment = Alignment.CenterVertically
        ) {
            TextButton(
                onClick = onPrev,
                contentPadding = PaddingValues(horizontal = spacing.x0_5, vertical = spacing.x0),
                modifier = Modifier.height(30.dp)
            ) {
                Text("<", color = colors.textSecondary)
            }
            OutlinedButton(
                onClick = onToday,
                enabled = !isTodaySelected,
                modifier = Modifier
                    .height(todayButtonHeight)
                    .graphicsLayer(alpha = if (isTodaySelected) 0.55f else 1f),
                shape = RoundedCornerShape(radius.full),
                contentPadding = PaddingValues(horizontal = spacing.x1, vertical = spacing.x0),
                border = BorderStroke(resolvedTodayBorderWidth, todayBtnBorderColor),
                colors = ButtonDefaults.outlinedButtonColors(
                    containerColor = Color.Transparent,
                    contentColor = todayBtnTextColor,
                    disabledContainerColor = Color.Transparent,
                    disabledContentColor = todayBtnTextColor
                )
            ) {
                Text(t("Today"), style = MaterialTheme.typography.labelMedium)
            }
            TextButton(
                onClick = onNext,
                contentPadding = PaddingValues(horizontal = spacing.x0_5, vertical = spacing.x0),
                modifier = Modifier.height(30.dp)
            ) {
                Text(">", color = colors.textSecondary)
            }
        }
    }
}

internal fun showThemedTimePicker(
    context: Context,
    themeResId: Int,
    initialHour: Int,
    initialMinute: Int,
    is24HourView: Boolean,
    actionColorArgb: Int,
    onTimeSet: (hour: Int, minute: Int) -> Unit
) {
    val hostActivity = context.findActivity()
    val fragmentActivity = hostActivity as? FragmentActivity

    if (fragmentActivity != null && !fragmentActivity.isFinishing) {
        val picker = MaterialTimePicker.Builder()
            .setTimeFormat(if (is24HourView) TimeFormat.CLOCK_24H else TimeFormat.CLOCK_12H)
            .setHour(initialHour.coerceIn(0, 23))
            .setMinute(initialMinute.coerceIn(0, 59))
            .setTheme(themeResId)
            .build()

        picker.addOnPositiveButtonClickListener {
            onTimeSet(picker.hour, picker.minute)
        }

        picker.show(fragmentActivity.supportFragmentManager, "microhabit_time_picker")
        return
    }

    val dialog = TimePickerDialog(
        context,
        themeResId,
        { _, hour, minute -> onTimeSet(hour, minute) },
        initialHour,
        initialMinute,
        is24HourView
    )
    dialog.setOnShowListener {
        dialog.getButton(TimePickerDialog.BUTTON_POSITIVE)?.setTextColor(actionColorArgb)
        dialog.getButton(TimePickerDialog.BUTTON_NEGATIVE)?.setTextColor(actionColorArgb)
    }
    dialog.show()
}

internal fun showThemedDatePicker(
    context: Context,
    themeResId: Int,
    initialDate: LocalDate,
    minDate: LocalDate? = null,
    actionColorArgb: Int,
    onDateSet: (year: Int, month: Int, day: Int) -> Unit
) {
    val hostActivity = context.findActivity()
    val fragmentActivity = hostActivity as? FragmentActivity

    if (fragmentActivity != null && !fragmentActivity.isFinishing) {
        val minUtcMillis = minDate?.atStartOfDay(ZoneOffset.UTC)?.toInstant()?.toEpochMilli()
        val resolvedInitialDate = if (minDate != null && initialDate.isBefore(minDate)) minDate else initialDate
        val initialUtcMillis = resolvedInitialDate
            .atStartOfDay(ZoneOffset.UTC)
            .toInstant()
            .toEpochMilli()

        val constraintsBuilder = CalendarConstraints.Builder()
            .setOpenAt(initialUtcMillis)
        minUtcMillis?.let { constraintsBuilder.setValidator(DateValidatorPointForward.from(it)) }

        val picker = MaterialDatePicker.Builder.datePicker()
            .setTheme(themeResId)
            .setSelection(initialUtcMillis)
            .setCalendarConstraints(constraintsBuilder.build())
            .build()

        picker.addOnPositiveButtonClickListener { selectedMillis ->
            val selectedDate = Instant.ofEpochMilli(selectedMillis)
                .atZone(ZoneOffset.UTC)
                .toLocalDate()
            onDateSet(selectedDate.year, selectedDate.monthValue - 1, selectedDate.dayOfMonth)
        }

        picker.show(fragmentActivity.supportFragmentManager, "microhabit_date_picker")
        return
    }

    val dialog = DatePickerDialog(
        context,
        themeResId,
        { _, year, month, day -> onDateSet(year, month, day) },
        initialDate.year,
        initialDate.monthValue - 1,
        initialDate.dayOfMonth
    )
    dialog.setOnShowListener {
        dialog.window?.let { window ->
            window.setLayout(
                android.view.ViewGroup.LayoutParams.WRAP_CONTENT,
                android.view.ViewGroup.LayoutParams.WRAP_CONTENT
            )
            window.setGravity(android.view.Gravity.CENTER)
        }
        dialog.getButton(DatePickerDialog.BUTTON_POSITIVE)?.setTextColor(actionColorArgb)
        dialog.getButton(DatePickerDialog.BUTTON_NEGATIVE)?.setTextColor(actionColorArgb)
    }
    minDate?.let {
        val minMillis = it.atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli()
        dialog.datePicker.minDate = minMillis
    }
    dialog.show()
}

internal fun openNotificationOrAppSettings(context: Context): Boolean {
    val packageName = context.packageName
    val activity = context.findActivity()

    val notificationSettingsIntent = Intent(Settings.ACTION_APP_NOTIFICATION_SETTINGS).apply {
        putExtra(Settings.EXTRA_APP_PACKAGE, packageName)
        putExtra("android.provider.extra.APP_PACKAGE", packageName)
        if (activity == null) addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
    }
    runCatching { context.startActivity(notificationSettingsIntent) }.onSuccess { return true }

    val appDetailsIntent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
        data = Uri.fromParts("package", packageName, null)
        if (activity == null) addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
    }
    runCatching { context.startActivity(appDetailsIntent) }.onSuccess { return true }

    return false
}

internal tailrec fun Context.findActivity(): Activity? = when (this) {
    is Activity -> this
    is ContextWrapper -> baseContext.findActivity()
    else -> null
}

internal fun formatTimeForDevice(context: Context, hour: Int, minute: Int): String {
    val calendar = java.util.Calendar.getInstance().apply {
        set(java.util.Calendar.HOUR_OF_DAY, hour.coerceIn(0, 23))
        set(java.util.Calendar.MINUTE, minute.coerceIn(0, 59))
        set(java.util.Calendar.SECOND, 0)
        set(java.util.Calendar.MILLISECOND, 0)
    }
    return android.text.format.DateFormat.getTimeFormat(context).format(calendar.time)
}
