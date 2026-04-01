package com.example.microhabit.ui.calendar

import com.example.microhabit.CalendarBreakdownStatus

enum class BreakdownRowStatus {
    COMPLETED,
    PARTIAL,
    MISSED,
    TODAY_PENDING,
    FUTURE,
    NOT_SCHEDULED
}

internal fun CalendarBreakdownStatus.toBreakdownRowStatus(): BreakdownRowStatus = when (this) {
    CalendarBreakdownStatus.COMPLETED -> BreakdownRowStatus.COMPLETED
    CalendarBreakdownStatus.PARTIAL -> BreakdownRowStatus.PARTIAL
    CalendarBreakdownStatus.MISSED -> BreakdownRowStatus.MISSED
    CalendarBreakdownStatus.TODAY_PENDING -> BreakdownRowStatus.TODAY_PENDING
    CalendarBreakdownStatus.FUTURE -> BreakdownRowStatus.FUTURE
    CalendarBreakdownStatus.NOT_SCHEDULED -> BreakdownRowStatus.NOT_SCHEDULED
}

