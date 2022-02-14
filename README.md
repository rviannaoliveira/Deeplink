# DeeplinkRouter

this library helps you to make navigation flows within the application

If you want to know more details about this library see this about [The Relentless Mage - DeeplinkRouter](https://medium.com/@rodrigo.vianna.oliveira/3f5d3ee22ed1) a explation about the use.

If you want to improve the use from the DeeplinkRouter see about [D&D - Perfect Party to your Application Android](https://medium.com/@rodrigo.vianna.oliveira/6fa4b94d8618)



## Setup

https://jitpack.io/#jitpack/android-example

Add it to your settings.gradle with:
```gradle
dependencyResolutionManagement {
    repositories {
        ...
        maven { url 'https://jitpack.io' }
    }
}
```
and:

```kotlin
dependencies {
    implementation 'implementation 'com.github.rviannaoliveira:Deeplink:{latest version}'
}
```

## How to use

### 1. Create your authority for example:
```kotlin 
sealed class AnyDeeplinkAuthority(override val authority: String) : DeeplinkAuthority(
    authority = authority
) {
    object AnyA : AnyDeeplinkAuthority("anyA")
    object AnyB : AnyDeeplinkAuthority("anyb")
}
```

### 2. Create your owner Deeplink class for example:
```kotlin
sealed class Deeplink(
    override val authority: String,
    override var params: MapParams = MapParams(),
    override val deepLinkStack: List<Deeplink> = emptyList(),
    override val scheme: String = "meuapp"
) : DeeplinkLib(
    authority = authority,
    params = params,
    deepLinkStack = deepLinkStack,
    scheme = scheme
) {

    data class AnyA(val name : String = "") : Deeplink(
        authority = AnyDeeplinkAuthority.AnyA.authority,
        params = MapParams(
            "name" to name
        )
    )

    object AnyB : Deeplink(
        authority = AnyDeeplinkAuthority.AnyB.authority,
        deepLinkStack = listOf(AnyA())
    )
}
```


### 3. Create your routes for example
```kotlin
class AnyDeeplinkRouteProcessor : DeeplinkRouteProcessor {
    override fun route(deeplink: DeeplinkLib): Class<out Activity>? =
        when (deeplink) {
            is Deeplink.AnyA -> AnyAActivity::class.java
            is Deeplink.AnyB -> AnyBActivity::class.java
            else -> null
        }

    override fun hasRouteProcessor(deeplink: DeeplinkLib): Boolean =
        when (deeplink.authority) {
            AnyDeeplinkAuthority.AnyA.authority,
            AnyDeeplinkAuthority.AnyB.authority -> true
            else -> false
        }
}
```

### 4. Now you can pass a deeplink or Uri or String

## Utilização

After creating the deeplink there are some methods that can help you use it.

// return intent that you can handle
```kotlin
fun buildRoute(currentActivity: Activity, deeplink: DeeplinkUriMapper): Intent?
```

// return a stack of intent that you can handle
```kotlin
fun buildRouteWithStack(currentActivity: Activity, deeplink: DeeplinkUriMapper): List<Intent>
```

// run some route
```kotlin
fun launch(currentActivity: Activity, deeplink: Deeplink) 
```

// run some route with stack
```kotlin
fun launchWithStack(currentActivity: Activity, deeplink: Deeplink)
```


## **License**

```
Copyright 2021 Rodrigo Vianna

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

   http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
