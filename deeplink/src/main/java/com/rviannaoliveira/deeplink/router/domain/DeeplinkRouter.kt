package com.rviannaoliveira.deeplink.router.domain

import android.app.Activity
import android.content.Intent
import android.util.Log
import com.rviannaoliveira.deeplink.router.DeeplinkLib
import com.rviannaoliveira.deeplink.router.DeeplinkRouteProcessor
import com.rviannaoliveira.deeplink.router.domain.mapper.DeeplinkUriMapper
import com.rviannaoliveira.deeplink.router.parseLinkUri
import com.rviannaoliveira.deeplink.router.putLinkUri

interface DeeplinkRouter {
    fun buildRoute(
        currentActivity: Activity,
        deeplink: DeeplinkUriMapper,
        vararg flags: Int
    ): Intent?

    fun buildRouteWithStack(
        currentActivity: Activity,
        deeplink: DeeplinkUriMapper,
        vararg flags: Int
    ): List<Intent>

    fun launch(
        currentActivity: Activity,
        deeplink: DeeplinkLib,
        resultRequestCode: Int? = null,
        vararg flags: Int
    )

    fun launch(
        currentActivity: Activity,
        deeplink: String,
        resultRequestCode: Int? = null,
        vararg flags: Int
    )

    fun launch(
        currentActivity: Activity,
        deeplink: DeeplinkUriMapper,
        resultRequestCode: Int? = null,
        vararg flags: Int
    )

    fun launchWithStack(
        currentActivity: Activity,
        deeplink: DeeplinkLib,
        resultRequestCode: Int? = null,
        vararg flags: Int
    )

    fun launchWithStack(
        currentActivity: Activity,
        deeplink: String,
        resultRequestCode: Int? = null,
        vararg flags: Int
    )

    fun launchWithStack(
        currentActivity: Activity,
        deeplink: DeeplinkUriMapper?,
        resultRequestCode: Int? = null,
        vararg flags: Int
    )

    fun isKnownDeepLink(deepLinkAddress: String?): Boolean
}

class DeeplinkRouterImpl(
    private val deeplinks: List<DeeplinkLib>,
    private val internScheme: String,
    private val externalScheme: String,
    private val routeProcessors: List<DeeplinkRouteProcessor>,
    private val authorities: List<DeeplinkAuthority>
) : DeeplinkRouter {

    /**
     * @param deepLinkAddress: scheme://authority
     * Verify if deeplink is mapped with scheme and authority
     * @return
     */
    override fun isKnownDeepLink(deepLinkAddress: String?): Boolean {
        if (deepLinkAddress == null) return false
        val uri = deepLinkAddress.parseLinkUri()
        return (uri.scheme == internScheme && authorities.firstOrNull { enum ->
            enum.authority.equals(
                uri.authority,
                true
            )
        } != null) ||
                (uri.scheme == externalScheme && authorities.firstOrNull { enum ->
                    enum.authority.equals(
                        uri.path,
                        true
                    )
                } != null)

    }

    /**
     * build a route without stack it will take always the last stack
     * @param currentActivity: activity will call the start
     * @param deeplink: is a Deeplink that was convert to URI
     * @param flags: support intent flag
     * @return Intent with URI inside in the Bundle
     */
    override fun buildRoute(
        currentActivity: Activity,
        deeplink: DeeplinkUriMapper,
        vararg flags: Int
    ): Intent? {
        return buildRouteWithStack(currentActivity, deeplink, *flags).lastOrNull()
    }

    /**
     * build a route complete with stack
     * @param currentActivity: activity will call the start
     * @param deeplink: is a Deeplink that was convert to URI
     * @param flags: support intent flag
     * @return List<Intent> : list of intent with each step of the stack
     */
    override fun buildRouteWithStack(
        currentActivity: Activity,
        deeplink: DeeplinkUriMapper,
        vararg flags: Int
    ): List<Intent> {
        if (!isKnownDeepLink((deeplink.toString()))) {
            return emptyList()
        }

        return getDeeplink(deeplink)?.let {
            val currentScreen = getActivity(it) ?: return emptyList()
            val stackComplete = buildStack(currentActivity, it) + currentScreen
            createIntentsForStack(currentActivity, stackComplete, it, *flags)
        } ?: emptyList()
    }

    /**
     * Initialize the currentActiviy with a Intent already builder
     * @param currentActivity: activity will call the start
     * @param deeplink: is a Deeplink that was convert to URI
     * @param resultRequestCode: accepted one requestCode to use to startActivityResult
     * @param flags: support intent flag
     */
    override fun launch(
        currentActivity: Activity,
        deeplink: DeeplinkLib,
        resultRequestCode: Int?,
        vararg flags: Int
    ) {
        startDeeplink(currentActivity, deeplink.toUri(), resultRequestCode, *flags)
    }

    /**
     * Initialize the currentActiviy with a Intent already builder
     * @param currentActivity: activity will call the start
     * @param deeplink: is a Deeplink that was convert to URI
     * @param resultRequestCode: accepted one requestCode to use to startActivityResult
     * @param flags: support intent flag
     */
    override fun launch(
        currentActivity: Activity,
        deeplink: String,
        resultRequestCode: Int?,
        vararg flags: Int
    ) {
        startDeeplink(currentActivity, deeplink.parseLinkUri(), resultRequestCode, *flags)
    }

    /**
     * Initialize the currentActiviy with a Intent already builder
     * @param currentActivity: activity will call the start
     * @param deeplink: is a Deeplink that was convert to URI
     * @param resultRequestCode: accepted one requestCode to use to startActivityResult
     * @param flags: support intent flag
     */
    override fun launch(
        currentActivity: Activity,
        deeplink: DeeplinkUriMapper,
        resultRequestCode: Int?,
        vararg flags: Int
    ) {
        startDeeplink(currentActivity, deeplink, resultRequestCode, *flags)
    }

    /**
     * Initialize the currentActiviy with a list intent already builder
     * @param currentActivity: activity will call the start
     * @param deeplink: is a Deeplink that was convert to URI
     * @param resultRequestCode: accepted one requestCode to use to startActivityResult
     * @param flags: support intent flag
     */
    override fun launchWithStack(
        currentActivity: Activity,
        deeplink: DeeplinkLib,
        resultRequestCode: Int?,
        vararg flags: Int
    ) {
        startDeeplinkWithStack(
            currentActivity,
            deeplink.toUri(),
            resultRequestCode,
            *flags
        )
    }

    /**
     * Initialize the currentActiviy with a list intent already builder
     * @param currentActivity: activity will call the start
     * @param deeplink: is a Deeplink that was convert to URI
     * @param resultRequestCode: accepted one requestCode to use to startActivityResult
     * @param flags: support intent flag
     */
    override fun launchWithStack(
        currentActivity: Activity,
        deeplink: String,
        resultRequestCode: Int?,
        vararg flags: Int
    ) {
        startDeeplinkWithStack(currentActivity, deeplink.parseLinkUri(), resultRequestCode, *flags)
    }

    /**
     * Initialize the currentActiviy with a list intent already builder
     * @param currentActivity: activity will call the start
     * @param deeplink: is a Deeplink that was convert to URI
     * @param resultRequestCode: accepted one requestCode to use to startActivityResult
     * @param flags: support intent flag
     */
    override fun launchWithStack(
        currentActivity: Activity,
        deeplink: DeeplinkUriMapper?,
        resultRequestCode: Int?,
        vararg flags: Int
    ) {
        deeplink?.let {
            startDeeplinkWithStack(currentActivity, deeplink, resultRequestCode, *flags)
        } ?: Log.d(TAG, ERROR, DeeplinkEmptyException())
    }

    /**
     * @param currentActivity: activity will call the start
     * @param deeplink: is a Deeplink that was convert to URI if nullable nothing happens
     * @param flags: support intent flag
     * @param resultRequestCode: accepted one requestCode to use to startActivityResult
     */
    private fun startDeeplink(
        currentActivity: Activity,
        deeplink: DeeplinkUriMapper,
        resultRequestCode: Int?,
        vararg flags: Int
    ) {
        val intent = buildRoute(currentActivity, deeplink, *flags)
        intent?.let {
            startIntent(listOf(intent), currentActivity, resultRequestCode)
        } ?: Log.d(TAG, ERROR, DeeplinkEmptyException())
    }

    /**
     * @param currentActivity: activity will call the start
     * @param deeplink: is a Deeplink that was convert to URI if nullable nothing happens
     * @param flags: support intent flag
     * @param resultRequestCode: accepted one requestCode to use to startActivityResult
     */
    private fun startDeeplinkWithStack(
        currentActivity: Activity,
        deeplink: DeeplinkUriMapper,
        resultRequestCode: Int?,
        vararg flags: Int
    ) {
        val intents = buildRouteWithStack(currentActivity, deeplink, *flags)
        startIntent(intents, currentActivity, resultRequestCode)
    }

    /**
     * It will take a Deeplink that was mapped in the project
     * @return Deeplink object valid
     * @param uri: is a Deeplink that was convert to URI
     */
    private fun getDeeplink(uri: DeeplinkUriMapper): DeeplinkLib? {
        return deeplinks.firstOrNull {
            it.authority == authorities.firstOrNull { enum ->
                enum.authority.equals(uri.authority, true) ||
                        enum.authority.equals(uri.path, true)
            }?.authority
        }?.apply {
            val list = mutableListOf<Pair<String, String>>()
            uri.queryParameterNames().forEach {
                list.add(Pair(it, uri.getQueryParameter(it).orEmpty()))
            }
            if (list.isNotEmpty()) {
                params = MapParams(*list.toTypedArray())
            }
        }
    }

    /**
     * Receive a intent's list and run with or without stack
     * @param intents list of intent from current deeplink
     * @param currentActivity: Activity that called the method
     * @param resultRequestCode: accepted one requestCode to use to startActivityResult
     */
    private fun startIntent(
        intents: List<Intent>,
        currentActivity: Activity,
        resultRequestCode: Int?
    ) {
        if (intents.isEmpty()) {
            return
        }

        if (intents.size == 1) {
            resultRequestCode?.let {
                currentActivity.startActivityForResult(intents.first(), resultRequestCode)
            } ?: currentActivity.startActivity(intents.first())
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
    private fun buildStack(
        currentActivity: Activity,
        deeplink: DeeplinkLib
    ): List<Class<out Activity>> {
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
     * @param Deeplink: is a Deeplink that was convert to URI
     * @return Class<out Activity activity was mapped with routeProcessors
     */
    private fun getActivity(Deeplink: DeeplinkLib): Class<out Activity>? =
        routeProcessors.firstOrNull { route ->
            route.hasRouteProcessor(Deeplink)
        }?.route(Deeplink)

    /**
     * After build a list activity::class will be build the intents
     * @param currentActivity: activity will call the start
     * @param stack: all of activities
     * @param deeplink: is a Deeplink that was convert to URI
     * @param flags: support intent flag
     * @return List<Intent>
     */
    private fun createIntentsForStack(
        currentActivity: Activity,
        stack: List<Class<out Activity>>,
        deeplink: DeeplinkLib,
        vararg flags: Int
    ): List<Intent> =
        stack.map { activityClazz ->
            createIntentForScreen(currentActivity, activityClazz, deeplink, *flags)
        }

    /**
     * After build a list activity::class will be build the intents
     * @param activity: activity will call the start
     * @param screen: all of activities
     * @param deeplink: is a Deeplink that was convert to URI
     * @param flags: support intent flag
     * @return List<Intent>
     */
    private fun createIntentForScreen(
        activity: Activity,
        screen: Class<out Activity>,
        deeplink: DeeplinkLib,
        vararg flags: Int
    ): Intent {
        val intent = Intent(activity, screen)
            .apply {
                putLinkUri(deeplink.toUri())
            }

        flags.forEach {
            intent.addFlags(it)
        }
        return intent
    }

    companion object {
        internal const val TAG = "DeeplinkRouterImpl"
        internal const val ERROR = "Error"
    }
}
