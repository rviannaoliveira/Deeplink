package com.rviannaoliveira.deeplink.router.domain.mapper

import android.net.Uri
import android.os.Parcelable
import kotlinx.parcelize.IgnoredOnParcel
import kotlinx.parcelize.Parcelize

@Parcelize
class DeeplinkUriMapper(private val uri: Uri) : Parcelable {
    @IgnoredOnParcel
    val scheme: String?
        get() = uri.scheme

    @IgnoredOnParcel
    val authority: String?
        get() = uri.authority

    @IgnoredOnParcel
    val path: String?
        get() = uri.path?.replace("/app/","")

    override fun toString(): String =
        uri.toString()

    fun getQueryParameter(key: String): String? = uri.getQueryParameter(key)

    fun queryParameterNames() : Set<String> = uri.queryParameterNames ?: emptySet()

    fun requireParamString(key: String): String? {
        return getQueryParameter(key)
    }

    fun requireParamBoolean(key: String): Boolean {
        return getQueryParameter(key).toBoolean()
    }

    fun requireParamInt(key: String): Int? {
        return getQueryParameter(key)?.toInt()
    }

    class Builder {
        private var scheme: String? = null
        private var authority: String? = null
        private val queryParams = mutableMapOf<String, String>()

        fun scheme(scheme: String): Builder = apply {
            this.scheme = scheme
        }

        fun authority(authority: String?): Builder = apply {
            this.authority = authority
        }

        fun setQueryParameters(map: Map<String, String>): Builder = apply {
            this.queryParams.putAll(map)
        }

        fun build(): DeeplinkUriMapper {
            val uri = Uri.Builder()
                .scheme(scheme)
                .authority(authority).apply {
                    queryParams.forEach { (key, value) ->
                        appendQueryParameter(key, value)
                    }
                }.build()
            return DeeplinkUriMapper(uri)
        }
    }

    companion object {
        @JvmStatic
        fun parse(uri: String?): DeeplinkUriMapper? {
            if (uri == null) return null
            return DeeplinkUriMapper(Uri.parse(uri))
        }
    }
}