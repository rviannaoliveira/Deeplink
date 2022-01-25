package com.rviannaoliveira.deeplink

import android.app.Activity
import android.content.Intent
import android.os.Build
import androidx.core.net.toUri
import com.google.common.truth.Truth.assertThat
import com.rviannaoliveira.deeplink.router.DeeplinkLib
import com.rviannaoliveira.deeplink.router.domain.DeeplinkAuthority
import com.rviannaoliveira.deeplink.router.domain.DeeplinkRouter
import com.rviannaoliveira.deeplink.router.domain.DeeplinkRouterImpl
import com.rviannaoliveira.deeplink.router.domain.mapper.DeeplinkUriMapper
import io.mockk.mockk
import io.mockk.verify
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.Robolectric
import org.robolectric.RobolectricTestRunner
import org.robolectric.android.controller.ActivityController
import org.robolectric.annotation.Config

@Config(sdk = [Build.VERSION_CODES.O_MR1])
@RunWith(RobolectricTestRunner::class)
class DeeplinkRouterTest {
    private lateinit var deeplinkRouter: DeeplinkRouter
    private lateinit var activityController: ActivityController<Activity>
    private lateinit var activity: Activity
    private val deeplinkUriMapperA = DeeplinkUriMapper("meuapp://a".toUri())
    private val deeplinkUriMapperNoOne = DeeplinkUriMapper("meuapp://bc".toUri())

    @Before
    fun setUp() {
        deeplinkRouter = DeeplinkRouterImpl(
            routeProcessors = routeProcessors,
            internScheme = internScheme,
            externalScheme = externalScheme,
            deeplinks = DeeplinkLib.values<DeeplinkTest>(),
            authorities = DeeplinkAuthority.values<DeeplinkAuthorityTest>()
         )
        activityController = Robolectric.buildActivity(Activity::class.java).setup()
        activity = activityController.get()
    }


    @Test
    fun `should be a deeplink valid`() {
        assertThat(deeplinkRouter.isKnownDeepLink(deeplinkUriMapperA.toString())).isTrue()
    }

    @Test
    fun `should be a deeplink invalid`() {
        assertThat(deeplinkRouter.isKnownDeepLink(deeplinkUriMapperNoOne.toString())).isFalse()
    }

    @Test
    fun `should not build intents when there arent deeplinks`() {
        val intents = deeplinkRouter.buildRouteWithStack(activity, deeplinkUriMapperNoOne)
        assertThat(intents).isEmpty()
    }

    @Test
    fun `should not build intent when there isnt deeplink`() {
        val intents = deeplinkRouter.buildRoute(activity, deeplinkUriMapperNoOne)
        assertThat(intents).isNull()
    }

    @Test
    fun `should build intents when there are deeplink`() {
        val intents = deeplinkRouter.buildRouteWithStack(activity, deeplinkUriMapperA)
        assertThat(intents).isNotEmpty()
    }

    @Test
    fun `should build intent when there is deeplink`() {
        val intents = deeplinkRouter.buildRoute(activity, deeplinkUriMapperA)
        assertThat(intents).isNotNull()
        assertThat(intents?.flags).isEqualTo(0)
    }

    @Test
    fun `should build intent when there is deeplink with flag`() {
        val intents = deeplinkRouter.buildRoute(activity, deeplinkUriMapperA,Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)

        assertThat(intents).isNotNull()
        assertThat(intents?.flags).isEqualTo(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
    }

    @Test
    fun `should run just one intent`() {
        val activityMocked = mockk<Activity>(relaxed = true)

        deeplinkRouter.launch(activityMocked, deeplinkUriMapperA)

        verify {
            activityMocked.startActivity(any())
        }
    }


    @Test
    fun `should run just one intent with result`() {
        val activityMocked = mockk<Activity>(relaxed = true)

        deeplinkRouter.launch(activityMocked, deeplinkUriMapperA, resultRequestCode = requestCode)

        verify {
            activityMocked.startActivityForResult(any(), requestCode )
        }
    }

    @Test
    fun `should run just all intent`() {
        val activityMocked = mockk<Activity>(relaxed = true)

        deeplinkRouter.launchWithStack(activityMocked, deeplinkUriMapperA)

        verify {
            activityMocked.startActivity(any())
        }
    }

    @Test
    fun `shouldnt run anything`() {
        val activityMocked = mockk<Activity>(relaxed = true)

        deeplinkRouter.launchWithStack(activityMocked, deeplinkUriMapperNoOne)

        verify(exactly = 0) {
            activityMocked.startActivities(any())
            activityMocked.startActivity(any())
        }
    }
}
