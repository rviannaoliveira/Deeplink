package com.rviannaoliveira.deeplink.router

import android.app.Activity
import com.rviannaoliveira.deeplink.router.Deeplink

interface DeeplinkRouteProcessor {
    fun route(deeplink: Deeplink): Class<out Activity>?
    fun hasRouteProcessor(deeplink: Deeplink): Boolean
}