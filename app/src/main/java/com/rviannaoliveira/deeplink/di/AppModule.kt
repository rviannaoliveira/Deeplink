package com.rviannaoliveira.deeplink.di

import com.rviannaoliveira.deeplink.AnyDeeplinkRouteProcessor
import com.rviannaoliveira.deeplink.Deeplink
import com.rviannaoliveira.deeplink.AnyDeeplinkAuthority
import com.rviannaoliveira.deeplink.router.DeeplinkLib
import com.rviannaoliveira.deeplink.router.domain.DeeplinkAuthority
import com.rviannaoliveira.deeplink.router.domain.DeeplinkRouter
import com.rviannaoliveira.deeplink.router.domain.DeeplinkRouterImpl
import org.koin.dsl.module

object AppModule {
    val instance = module {
        single<DeeplinkRouter> {
            DeeplinkRouterImpl(
                routeProcessors = get(),
                deeplinks = DeeplinkLib.values<Deeplink>(),
                internScheme = "meuapp",
                externalScheme = "https://",
                authorities = DeeplinkAuthority.values<AnyDeeplinkAuthority>()
            )
        }

        factory {
            listOf(
                AnyDeeplinkRouteProcessor()
            )
        }
    }
}