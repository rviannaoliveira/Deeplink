package com.rviannaoliveira.deeplink.router.domain

import kotlin.reflect.full.createInstance

abstract class DeeplinkAuthority(open val authority: String){
    companion object {
        inline fun <reified T : DeeplinkAuthority> values(): List<T> =
            T::class.sealedSubclasses.filterNot { it.isData }.map { it.objectInstance as T } +
                    T::class.sealedSubclasses.filter { it.isData }.map { it.createInstance() }
    }
}

class MapParams(private vararg val pairs: Pair<String, String>) {
    val entries: Map<String, String>
        get() = pairs.toMap()
}
