package com.rviannaoliveira.deeplink

import android.app.Activity
import android.content.Intent
import com.rviannaoliveira.deeplink.router.DeeplinkLib
import com.rviannaoliveira.deeplink.router.DeeplinkRouteProcessor
import com.rviannaoliveira.deeplink.router.domain.DeeplinkAuthority
import com.rviannaoliveira.deeplink.router.domain.MapParams
import java.util.*

val intent = Intent()
val customerID = "123"
val requestCode = 12
val internScheme = "meuapp"
val externalScheme = "https://"


class DeeplinkRouteProcessorTest : DeeplinkRouteProcessor {
    override fun route(deeplink: DeeplinkLib): Class<out Activity> =
        when(deeplink){
            is DeeplinkTest.A -> Activity::class.java
            is DeeplinkTest.B -> Activity::class.java
            else -> throw Exception()
        }

    override fun hasRouteProcessor(deeplink: DeeplinkLib): Boolean =
        true
}

sealed class DeeplinkTest(
    override val authority: String,
    override var params: MapParams = MapParams(),
    override val deepLinkStack: List<DeeplinkTest> = Collections.emptyList(),
    override val scheme: String = "meuapp://"
) : DeeplinkLib(
    authority = authority,
    params = params,
    deepLinkStack = deepLinkStack,
    scheme = scheme
){
    /**
     * meuapp://b
     */
    object B : DeeplinkTest(authority = DeeplinkAuthorityTest.A.authority)

    /**
     * meuapp://a
     */
    object A : DeeplinkTest(authority = DeeplinkAuthorityTest.B.authority, deepLinkStack = listOf(
        B
    ))

}

sealed class DeeplinkAuthorityTest(override val authority: String) : DeeplinkAuthority(
    authority = authority
) {
    object A : DeeplinkAuthorityTest("a")
    object B : DeeplinkAuthorityTest("b")
}


val routeProcessors = listOf(DeeplinkRouteProcessorTest())