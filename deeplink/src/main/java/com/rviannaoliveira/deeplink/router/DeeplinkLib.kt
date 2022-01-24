package com.rviannaoliveira.deeplink.router

import com.rviannaoliveira.deeplink.router.domain.MapParams
import com.rviannaoliveira.deeplink.router.domain.mapper.DeeplinkUriMapper
import kotlin.reflect.full.createInstance

abstract class DeeplinkLib(
    open val authority: String,
    open var params: MapParams = MapParams(),
    open val deepLinkStack: List<DeeplinkLib> = emptyList(),
    open val scheme: String
){
    fun toUri(): DeeplinkUriMapper = DeeplinkUriMapper.Builder()
        .scheme(scheme)
        .authority(authority)
        .setQueryParameters(params.entries)
        .build()

    companion object {
        inline fun <reified T : DeeplinkLib> values(): List<T> {
            return T::class.sealedSubclasses.filterNot { it.isData }.map { it.objectInstance as T } +
                    T::class.sealedSubclasses.filter { it.isData }.map { it.createInstance() }
        }
    }
}