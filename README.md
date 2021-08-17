# Approov Quickstart: Android Kotlin OkHttp

This quickstart is written specifically for native Android apps that are written in Kotlin and use [`OkHttp`](https://square.github.io/okhttp/) for making the API calls that you wish to protect with Approov. If this is not your situation then check if there is a more relevant quickstart guide available.

This quickstart provides the basic steps for integrating Approov into your app. A more detailed step-by-step guide using a [Shapes App Example](https://github.com/approov/quickstart-android-kotlin-okhttp/blob/master/SHAPES-EXAMPLE.md) is also available.

To follow this guide you should have received an onboarding email for a trial or paid Approov account.

## ADDING APPROOVSERVICE DEPENDENCY
The Approov integration is available via [`jitpack`](https://jitpack.io). This allows inclusion into the project by simply specifying a dependency in the `gradle` files for the app.

Firstly, `jitpack` needs to be added to the end the `repositories` section in the `build.gradle` file at the top root level of the project:

```
allprojects {
    repositories {
        ...
        maven { url 'https://jitpack.io' }
    }
}
```

Secondly, add the dependency in your app's `build.gradle`:

```
dependencies {
	 implementation 'com.github.approov:approov-service-okhttp:2.7.0'
}
```
Make sure you do a Gradle sync (by selecting `Sync Now` in the banner at the top of the modified `.gradle` file) after making these changes.

This package is actually an open source wrapper layer that allows you to easily use Approov with `OkHttp`. This has a further dependency to the closed source [Approov SDK](https://github.com/approov/approov-android-sdk).

## MANIFEST CHANGES
The following app permissions need to be available in the manifest to use Approov:

```xml
<uses-permission android:name="android.permission.ACCESS_NETWORK_STATE" />
<uses-permission android:name="android.permission.INTERNET" />
```

Note that the minimum SDK version you can use with the Approov package is 21 (Android 5.0). 

Please [read this](https://approov.io/docs/latest/approov-usage-documentation/#targetting-android-11-and-above) section of the reference documentation if targetting Android 11 (API level 30) or above.

## INITIALIZING APPROOVSERVICE
In order to use the `ApproovService` you must initialize it when your app is created, usually in the `onCreate` method:

```kotlin
import io.approov.service.okhttp.ApproovService

class YourApp: Application() {
    override fun onCreate() {
        super.onCreate()
        approovService = ApproovService(applicationContext, "<enter-your-config-string-here>")
    }

    companion object {
        lateinit var approovService: ApproovService
    }
}
```

The `<enter-your-config-string-here>` is a custom string that configures your Approov account access. This will have been provided in your Approov onboarding email.

This initializes Approov when the app is first created. A companion object allows other parts of the app to access the singleton Approov instance. All calls to `ApproovService` and the SDK itself are thread safe.

## USING APPROOVSERVICE
You can then make Approov enabled `OkHttp` API calls by using the `OkHttpClient` available from the `ApproovService`:

```kotlin
val client = YourApp.approovService.getOkHttpClient()
```

This obtains a cached client to be used for calls that includes an interceptor that is able to add an `Approov-Token` header and pins the connections. You should thus use this client for all API calls you may wish to protect.

You must always call this method whenever you want to make a request to ensure that you are using the most up to date client. Failure to do this will mean that the app is not able to dynamically change its pins.

## CUSTOM OKHTTP BUILDER
By default, the method gets a default `OkHttpClient` client. However, your existing code may use a customized client with, for instance, different timeouts or other interceptors. For example, if you have existing code:

```kotlin
val client = OkHttpClient.Builder().connectTimeout(5, TimeUnit.SECONDS).build()
```
Pass the modified builder to the `ApproovService` framework as follows:

```kotlin
YourApp.approovService.setOkHttpClientBuilder(OkHttpClient.Builder().connectTimeout(5, TimeUnit.SECONDS))
```

This call only needs to be made once. Subsequent calls to `YourApp.approovService.getOkHttpClient()` will then always a `OkHttpClient` with the builder values included.

## CHECKING IT WORKS
Initially you won't have set which API domains to protect, so the interceptor will not add anything. It will have called Approov though and made contact with the Approov cloud service. You will see logging from Approov saying `UNKNOWN_URL`.

Your Approov onboarding email should contain a link allowing you to access [Live Metrics Graphs](https://approov.io/docs/latest/approov-usage-documentation/#metrics-graphs). After you've run your app with Approov integration you should be able to see the results in the live metrics within a minute or so. At this stage you could even release your app to get details of your app population and the attributes of the devices they are running upon.

However, to actually protect your APIs there are some further steps you can learn about in [Next Steps](https://github.com/approov/quickstart-android-kotlin-okhttp/blob/master/NEXT-STEPS.md).

