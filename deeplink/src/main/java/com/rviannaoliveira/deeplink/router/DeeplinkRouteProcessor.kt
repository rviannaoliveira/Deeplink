package com.rviannaoliveira.deeplink.router

import android.app.Activity

interface DeeplinkRouteProcessor {
    fun route(deeplink: DeeplinkLib): Class<out Activity>?
    fun hasRouteProcessor(deeplink: DeeplinkLib): Boolean
}