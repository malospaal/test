package com.example.microhabit.data

import java.time.LocalDate

sealed class SubscriptionState {
    data object Free : SubscriptionState()

    data class PremiumActive(
        val plan: PremiumPlan,
        val nextBillingDate: LocalDate?,
        val nextBillingAmount: String?
    ) : SubscriptionState()

    data class PremiumCancelled(
        val plan: PremiumPlan,
        val expiresOn: LocalDate
    ) : SubscriptionState()
}

enum class PremiumPlan {
    MONTHLY,
    YEARLY,
    LIFETIME
}
