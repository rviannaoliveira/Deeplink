package com.rviannaoliveira.deeplink.router.domain

object DeeplinkData {
    enum class DeepLinkAuthorityEnum(val authority: String) {
        NAME("");

        companion object {
            fun from(authority: String?): DeepLinkAuthorityEnum? {
                return values().firstOrNull {
                    it.authority.equals(authority, true)
                }
            }
        }
    }

    enum class DeepLinkSchemeEnum(val scheme: String) {
        INTERN_ROUTE("");

        companion object {
            @JvmStatic
            fun from(scheme: String?): DeepLinkSchemeEnum? {
                return values().firstOrNull { it.name.equals(scheme, true) }
            }
        }
    }

    class MapParams(private vararg val pairs: Pair<String, String>) {
        val entries: Map<String, String>
            get() = pairs.toMap()
    }

    object Params {
        const val NAME = "name"
    }
}
