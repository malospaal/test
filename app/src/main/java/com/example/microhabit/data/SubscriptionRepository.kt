package com.example.microhabit.data

import android.content.Context
import android.content.SharedPreferences
import java.time.LocalDate
import java.time.format.DateTimeFormatter

class SubscriptionRepository(context: Context) {
    private val prefs: SharedPreferences = context.getSharedPreferences("habit_prefs", Context.MODE_PRIVATE)
    private val formatter: DateTimeFormatter = DateTimeFormatter.ISO_LOCAL_DATE

    fun getSubscriptionState(): SubscriptionState {
        migrateLegacyIfNeeded()
        if (isDebugForceFreeEnabled()) return SubscriptionState.Free

        val rawState = prefs.getString(KEY_SUB_STATE, STATE_FREE).orEmpty()
        return when (rawState) {
            STATE_ACTIVE -> {
                val plan = readPlan() ?: return SubscriptionState.Free
                SubscriptionState.PremiumActive(
                    plan = plan,
                    nextBillingDate = readDate(KEY_SUB_NEXT_DATE),
                    nextBillingAmount = readString(KEY_SUB_NEXT_AMOUNT)
                )
            }

            STATE_CANCELLED -> {
                val plan = readPlan() ?: return SubscriptionState.Free
                val expiresOn = readDate(KEY_SUB_EXPIRES_ON) ?: return SubscriptionState.Free
                SubscriptionState.PremiumCancelled(
                    plan = plan,
                    expiresOn = expiresOn
                )
            }

            else -> SubscriptionState.Free
        }
    }

    fun hasPremiumAccess(today: LocalDate = LocalDate.now()): Boolean {
        return when (val state = getSubscriptionState()) {
            SubscriptionState.Free -> false
            is SubscriptionState.PremiumActive -> true
            is SubscriptionState.PremiumCancelled -> !today.isAfter(state.expiresOn)
        }
    }

    fun activatePremium(
        plan: PremiumPlan,
        nextBillingDate: LocalDate?,
        nextBillingAmount: String?
    ) {
        prefs.edit()
            .putString(KEY_SUB_STATE, STATE_ACTIVE)
            .putString(KEY_SUB_PLAN, plan.name)
            .putString(KEY_SUB_NEXT_DATE, nextBillingDate?.format(formatter).orEmpty())
            .putString(KEY_SUB_NEXT_AMOUNT, nextBillingAmount.orEmpty())
            .putString(KEY_SUB_EXPIRES_ON, "")
            .putString(KEY_SUB_DEBUG_FORCE_FREE, "false")
            .apply()
    }

    fun setFree(debugForced: Boolean = false) {
        prefs.edit()
            .putString(KEY_SUB_STATE, STATE_FREE)
            .putString(KEY_SUB_PLAN, "")
            .putString(KEY_SUB_NEXT_DATE, "")
            .putString(KEY_SUB_NEXT_AMOUNT, "")
            .putString(KEY_SUB_EXPIRES_ON, "")
            .putString(KEY_SUB_DEBUG_FORCE_FREE, if (debugForced) "true" else "false")
            .apply()
    }

    fun cancelSubscription() {
        val current = getSubscriptionState()
        if (current !is SubscriptionState.PremiumActive || current.plan == PremiumPlan.LIFETIME) return
        val expiresOn = current.nextBillingDate ?: LocalDate.now()
        prefs.edit()
            .putString(KEY_SUB_STATE, STATE_CANCELLED)
            .putString(KEY_SUB_PLAN, current.plan.name)
            .putString(KEY_SUB_EXPIRES_ON, expiresOn.format(formatter))
            .apply()
    }

    fun renewSubscription() {
        val current = getSubscriptionState()
        val cancelled = current as? SubscriptionState.PremiumCancelled ?: return
        prefs.edit()
            .putString(KEY_SUB_STATE, STATE_ACTIVE)
            .putString(KEY_SUB_PLAN, cancelled.plan.name)
            .putString(KEY_SUB_NEXT_DATE, cancelled.expiresOn.format(formatter))
            .putString(KEY_SUB_EXPIRES_ON, "")
            .putString(KEY_SUB_DEBUG_FORCE_FREE, "false")
            .apply()
    }

    fun debugForceFreePlan() {
        setFree(debugForced = true)
    }

    fun isDebugForceFreeEnabled(): Boolean {
        return prefs.getString(KEY_SUB_DEBUG_FORCE_FREE, "false") == "true"
    }

    private fun migrateLegacyIfNeeded() {
        if (prefs.contains(KEY_SUB_STATE)) return
        val legacySource = prefs.getString(KEY_LEGACY_PRO_ACCESS_SOURCE, ProAccessSource.NONE.name)
            ?: ProAccessSource.NONE.name
        val source = runCatching { ProAccessSource.valueOf(legacySource) }.getOrDefault(ProAccessSource.NONE)
        when (source) {
            ProAccessSource.MONTHLY -> {
                activatePremium(
                    plan = PremiumPlan.MONTHLY,
                    nextBillingDate = LocalDate.now().plusMonths(1),
                    nextBillingAmount = "$3.99"
                )
            }

            ProAccessSource.YEARLY -> {
                activatePremium(
                    plan = PremiumPlan.YEARLY,
                    nextBillingDate = LocalDate.now().plusYears(1),
                    nextBillingAmount = "$24.99"
                )
            }

            ProAccessSource.LIFETIME -> {
                activatePremium(
                    plan = PremiumPlan.LIFETIME,
                    nextBillingDate = null,
                    nextBillingAmount = null
                )
            }

            ProAccessSource.NONE -> {
                setFree(debugForced = false)
            }
        }
    }

    private fun readPlan(): PremiumPlan? {
        val raw = readString(KEY_SUB_PLAN) ?: return null
        return runCatching { PremiumPlan.valueOf(raw) }.getOrNull()
    }

    private fun readDate(key: String): LocalDate? {
        val raw = readString(key) ?: return null
        return runCatching { LocalDate.parse(raw, formatter) }.getOrNull()
    }

    private fun readString(key: String): String? {
        val value = prefs.getString(key, "") ?: ""
        return value.takeIf { it.isNotBlank() }
    }

    companion object {
        private const val KEY_SUB_STATE = "sub_state"
        private const val KEY_SUB_PLAN = "sub_plan"
        private const val KEY_SUB_NEXT_DATE = "sub_next_date"
        private const val KEY_SUB_NEXT_AMOUNT = "sub_next_amount"
        private const val KEY_SUB_EXPIRES_ON = "sub_expires_on"
        private const val KEY_SUB_DEBUG_FORCE_FREE = "sub_debug_force_free"
        private const val KEY_LEGACY_PRO_ACCESS_SOURCE = "pro_access_source"

        private const val STATE_FREE = "FREE"
        private const val STATE_ACTIVE = "ACTIVE"
        private const val STATE_CANCELLED = "CANCELLED"
    }
}
