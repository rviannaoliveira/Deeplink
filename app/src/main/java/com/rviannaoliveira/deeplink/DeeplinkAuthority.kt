package com.rviannaoliveira.deeplink

import com.rviannaoliveira.deeplink.router.domain.DeeplinkAuthority


@Suppress("ClassName")
sealed class AnyDeeplinkAuthority(override val authority: String) : DeeplinkAuthority(
    authority = authority
) {
    object AnyA : AnyDeeplinkAuthority("anyA")
    object AnyB : AnyDeeplinkAuthority("anyb")
}