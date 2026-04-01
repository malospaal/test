package com.example.microhabit.domain.subscription

import com.example.microhabit.BillingCycle
import com.example.microhabit.PRODUCT_ID_LIFETIME
import com.example.microhabit.PRODUCT_ID_MONTHLY
import com.example.microhabit.PRODUCT_ID_YEARLY
import com.example.microhabit.data.ProAccessSource

internal fun billingProductIdFor(cycle: BillingCycle): String {
    return when (cycle) {
        BillingCycle.MONTHLY -> PRODUCT_ID_MONTHLY
        BillingCycle.YEARLY -> PRODUCT_ID_YEARLY
        BillingCycle.LIFETIME -> PRODUCT_ID_LIFETIME
    }
}

internal fun proAccessSourceFor(cycle: BillingCycle): ProAccessSource {
    return when (cycle) {
        BillingCycle.MONTHLY -> ProAccessSource.MONTHLY
        BillingCycle.YEARLY -> ProAccessSource.YEARLY
        BillingCycle.LIFETIME -> ProAccessSource.LIFETIME
    }
}
