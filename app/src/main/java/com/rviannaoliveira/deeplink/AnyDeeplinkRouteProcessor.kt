package com.rviannaoliveira.deeplink

import android.app.Activity
import com.rviannaoliveira.deeplink.router.DeeplinkLib
import com.rviannaoliveira.deeplink.router.DeeplinkRouteProcessor

class AnyDeeplinkRouteProcessor : DeeplinkRouteProcessor {
    override fun route(deeplink: DeeplinkLib): Class<out Activity>? =
        when (deeplink) {
            is Deeplink.AnyA -> AnyAActivity::class.java
            is Deeplink.AnyB -> AnyBActivity::class.java
            else -> null
        }

    override fun hasRouteProcessor(deeplink: DeeplinkLib): Boolean = true
}
