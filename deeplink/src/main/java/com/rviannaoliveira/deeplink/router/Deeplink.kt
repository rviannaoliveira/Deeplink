package com.rviannaoliveira.deeplink.router

import com.rviannaoliveira.deeplink.router.domain.DeeplinkData.MapParams
import com.rviannaoliveira.deeplink.router.domain.DeeplinkData.DeepLinkSchemeEnum
import com.rviannaoliveira.deeplink.router.domain.mapper.DeeplinkUriMapper
import java.util.*
import kotlin.reflect.full.createInstance

sealed class Deeplink(
        val authority: String,
        val params: MapParams = MapParams(),
        val deepLinkStack: List<Deeplink> = emptyList()
) {
    private val scheme: String
        get() = DeepLinkSchemeEnum.INTERN_ROUTE.scheme.toLowerCase(Locale.getDefault())

    fun toUri(): DeeplinkUriMapper = DeeplinkUriMapper.Builder()
            .scheme(scheme)
            .authority(authority)
            .setQueryParameters(params.entries)
            .build()

    companion object {
        fun values(): List<Deeplink> =
                Deeplink::class.sealedSubclasses.filterNot { it.isData }.map { it.objectInstance as Deeplink } +
                        Deeplink::class.sealedSubclasses.filter { it.isData }.map { it.createInstance() }
    }
}
