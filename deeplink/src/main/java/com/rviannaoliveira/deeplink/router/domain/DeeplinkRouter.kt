package com.rviannaoliveira.deeplink.router.domain

import android.app.Activity
import android.content.Intent
import com.rviannaoliveira.deeplink.router.Deeplink
import com.rviannaoliveira.deeplink.router.DeeplinkRouteProcessor
import com.rviannaoliveira.deeplink.router.domain.DeeplinkData.DeepLinkSchemeEnum.INTERN_ROUTE
import com.rviannaoliveira.deeplink.router.domain.mapper.DeeplinkUriMapper
import com.rviannaoliveira.deeplink.router.parseLinkUri
import com.rviannaoliveira.deeplink.router.putLinkUri

interface DeeplinkRouter {
    //Note: It was created some overloads because there is some java classes that are use, after this.. We can remove the overloads and work just with nullables
    fun buildRoute(currentActivity: Activity, deeplink: DeeplinkUriMapper): Intent?
    fun buildRouteWithStack(currentActivity: Activity, deeplink: DeeplinkUriMapper): List<Intent>
    fun launch(currentActivity: Activity, deeplink: Deeplink)
    fun launch(currentActivity: Activity, deeplink: String)
    fun launch(currentActivity: Activity, deeplink: DeeplinkUriMapper)
    fun launchWithStack(currentActivity: Activity, deeplink: Deeplink)
    fun launchWithStack(currentActivity: Activity, deeplink: String)
    fun launchWithStack(currentActivity: Activity, deeplink: DeeplinkUriMapper?)
    fun isKnownDeepLink(deepLinkAddress: String?): Boolean
}

class DeeplinkRouterImpl(
    private val routeProcessors: List<DeeplinkRouteProcessor>
) : DeeplinkRouter {
    private val deeplinks = Deeplink.values()

    /**
     * @param deepLinkAddress: scheme://authority
     * Verify if deeplink is mapped with scheme and authority
     * @return
     */
    override fun isKnownDeepLink(deepLinkAddress: String?): Boolean {
        if (deepLinkAddress == null) return false
        val uri = deepLinkAddress.parseLinkUri()
        return uri.scheme == INTERN_ROUTE.scheme &&
                DeeplinkData.DeepLinkAuthorityEnum.from(uri.authority) != null
    }

    /**
     * build a route without stack it will take always the last stack
     * @param currentActivity: activity will call the start
     * @param deeplink: is a Deeplink that was convert to URI
     * @return Intent with URI inside in the Bundle
     */
    override fun buildRoute(currentActivity: Activity, deeplink: DeeplinkUriMapper): Intent? {
        return buildRouteWithStack(currentActivity, deeplink).ifEmpty {
            null
        }?.last()
    }

    /**
     * build a route complete with stack
     * @param currentActivity: activity will call the start
     * @param deeplink: is a Deeplink that was convert to URI
     * @return List<Intent> : list of intent with each step of the stack
     */
    override fun buildRouteWithStack(currentActivity: Activity, deeplink: DeeplinkUriMapper): List<Intent> {
        if (!isKnownDeepLink((deeplink.toString()))) {
            return emptyList()
        }

        return getDeeplink(deeplink)?.let { deepLink ->
            val currentScreen = getActivity(deepLink) ?: return emptyList()
            val stackComplete = buildStack(currentActivity, deepLink) + currentScreen
            createIntentsForStack(currentActivity, stackComplete, deepLink)
        } ?: emptyList()
    }

    /**
     * Initialize the currentActiviy with a Intent already builder
     * @param currentActivity: activity will call the start
     * @param deeplink: is a Deeplink that was convert to URI
     */
    override fun launch(currentActivity: Activity, deeplink: Deeplink) {
        startDeeplink(currentActivity, deeplink.toUri())
    }

    /**
     * Initialize the currentActiviy with a Intent already builder
     * @param currentActivity: activity will call the start
     * @param deeplink: is a Deeplink that was convert to URI
     */
    override fun launch(currentActivity: Activity, deeplink: String) {
        startDeeplink(currentActivity, deeplink.parseLinkUri())
    }

    /**
     * Initialize the currentActiviy with a Intent already builder
     * @param currentActivity: activity will call the start
     * @param deeplink: is a Deeplink that was convert to URI
     */
    override fun launch(currentActivity: Activity, deeplink: DeeplinkUriMapper) {
        startDeeplink(currentActivity, deeplink)
    }

    /**
     * Initialize the currentActiviy with a list intent already builder
     * @param currentActivity: activity will call the start
     * @param deeplink: is a Deeplink that was convert to URI
     */
    override fun launchWithStack(currentActivity: Activity, deeplink: Deeplink) {
        startDeeplinkWithStack(currentActivity, deeplink.toUri())
    }

    /**
     * Initialize the currentActiviy with a list intent already builder
     * @param currentActivity: activity will call the start
     * @param deeplink: is a Deeplink that was convert to URI
     */
    override fun launchWithStack(currentActivity: Activity, deeplink: String) {
        startDeeplinkWithStack(currentActivity, deeplink.parseLinkUri())
    }

    /**
     * Initialize the currentActiviy with a list intent already builder
     * @param currentActivity: activity will call the start
     * @param deeplink: is a Deeplink that was convert to URI
     */
    override fun launchWithStack(currentActivity: Activity, deeplink: DeeplinkUriMapper?) {
        deeplink?.let {
            startDeeplinkWithStack(currentActivity, deeplink)
        }
    }

    /**
     * @param currentActivity: activity will call the start
     * @param deeplink: is a Deeplink that was convert to URI if nullable nothing happens
     */
    private fun startDeeplink(currentActivity: Activity, deeplink: DeeplinkUriMapper) {
        val intent = buildRoute(currentActivity, deeplink)
        intent?.let {
            startIntent(listOf(intent), currentActivity)
        }
    }

    /**
     * @param currentActivity: activity will call the start
     * @param deeplink: is a Deeplink that was convert to URI if nullable nothing happens
     */
    private fun startDeeplinkWithStack(currentActivity: Activity, deeplink: DeeplinkUriMapper) {
        val intents = buildRouteWithStack(currentActivity, deeplink)
        startIntent(intents, currentActivity)
    }

    /**
     * It will take a Deeplink that was mapped in the project
     * @return Deeplink object valid
     * @param uri: is a Deeplink that was convert to URI
     */
    private fun getDeeplink(uri: DeeplinkUriMapper): Deeplink? {
        return deeplinks.firstOrNull {
            it.authority == DeeplinkData.DeepLinkAuthorityEnum.from(uri.authority)?.authority
        }
    }

    /**
     * Receive a intent's list and run with or without stack
     * @param intents list of intent from current deeplink
     * @param currentActivity: Activity that called the method
     */
    private fun startIntent(intents: List<Intent>, currentActivity: Activity) {
        if (intents.isEmpty()) {
            return
        }

        if (intents.size == 1) {
            currentActivity.startActivity(intents.first())
            return
        }

        currentActivity.startActivities(intents.toTypedArray())
    }

    /**
     * method that buildRoute and buildRouteWithStack use to build a route
     * @param currentActivity: activity will call the start
     * @param deeplink: is a Deeplink that was convert to URI
     * @return List<Class<out Activity> activity was mapped with routeProcessors
     */
    private fun buildStack(currentActivity: Activity, deeplink: Deeplink): List<Class<out Activity>> {
        val activities = deeplink.deepLinkStack
                .mapNotNull { deeplinkStack ->
                    getActivity(deeplinkStack)
                }

        val activitiesName: List<String> = activities.map { it.name }

        return if (activitiesName.contains(currentActivity.localClassName)) {
            activities
                    .dropWhile {
                        it.name == currentActivity.localClassName
                    }
        } else {
            activities
        }.filterNot {
            it.name == currentActivity.localClassName
        }
    }

    /**
     * get Activity that was put in DI from some classes that implemented DeeplinkRouteProcessors
     * @param deeplink: is a Deeplink that was convert to URI
     * @return Class<out Activity activity was mapped with routeProcessors
     */
    private fun getActivity(deeplink: Deeplink): Class<out Activity>? =
            routeProcessors.firstOrNull { route ->
                route.hasRouteProcessor(deeplink)
            }?.route(deeplink)

    /**
     * After build a list activity::class will be build the intents
     * @param currentActivity: activity will call the start
     * @param stack: all of activities
     * @param deeplink: is a Deeplink that was convert to URI
     * @return List<Intent>
     */
    private fun createIntentsForStack(currentActivity: Activity, stack: List<Class<out Activity>>, deeplink: Deeplink): List<Intent> =
            stack.map { activityClazz ->
                createIntentForScreen(currentActivity, activityClazz, deeplink)
            }

    /**
     * After build a list activity::class will be build the intents
     * @param activity: activity will call the start
     * @param screen: all of activities
     * @param deeplink: is a Deeplink that was convert to URI
     * @return List<Intent>
     */
    private fun createIntentForScreen(activity: Activity, screen: Class<out Activity>, deeplink: Deeplink): Intent {
        return Intent(activity, screen)
                .apply {
                    putLinkUri(deeplink.toUri())
                }
    }

    companion object {
        internal const val TAG = "DeeplinkRouterImpl"
    }
}
