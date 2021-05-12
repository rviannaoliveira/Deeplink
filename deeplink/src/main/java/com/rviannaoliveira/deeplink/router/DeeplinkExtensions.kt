package com.rviannaoliveira.deeplink.router

import android.app.Activity
import android.content.Intent
import android.net.Uri
import com.rviannaoliveira.deeplink.router.domain.mapper.DeeplinkUriMapper

internal const val DEEPLINK_URL = "DEEPLINK_URL"

fun Activity.requireLinkUri(): DeeplinkUriMapper? {
    return getDeeplinkMapperUri()
}

internal fun String.parseLinkUri(): DeeplinkUriMapper = DeeplinkUriMapper(Uri.parse(this))

fun Intent.putLinkUri(uriMapper: DeeplinkUriMapper) {
    putExtra(DEEPLINK_URL, uriMapper)
}

fun Activity.getDeeplinkMapperUri(): DeeplinkUriMapper? {
    return if(intent.data != null){
        DeeplinkUriMapper(intent.data!!)
    }else{
        intent?.getParcelableExtra(DEEPLINK_URL)
    }
}
