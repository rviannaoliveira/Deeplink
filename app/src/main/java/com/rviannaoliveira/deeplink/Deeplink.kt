package com.rviannaoliveira.deeplink

import com.rviannaoliveira.deeplink.router.DeeplinkLib
import com.rviannaoliveira.deeplink.router.domain.MapParams

sealed class Deeplink(
    override val authority: String,
    override var params: MapParams = MapParams(),
    override val deepLinkStack: List<Deeplink> = emptyList(),
    override val scheme: String = "meuapp"
) : DeeplinkLib(
    authority = authority,
    params = params,
    deepLinkStack = deepLinkStack,
    scheme = scheme
) {

    data class AnyA(val name : String = "") : Deeplink(
        authority = AnyDeeplinkAuthority.AnyA.authority,
        params = MapParams(
            "name" to name
        )
    )

    object AnyB : Deeplink(
        authority = AnyDeeplinkAuthority.AnyB.authority,
        deepLinkStack = listOf(AnyA())
    )
}

