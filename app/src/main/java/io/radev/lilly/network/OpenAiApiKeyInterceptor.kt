package io.radev.lilly.network

import io.radev.lilly.BuildConfig
import okhttp3.Interceptor
import okhttp3.Response

class OpenAiApiKeyInterceptor : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val original = chain.request()
        val request = original.newBuilder()
            .header("Authorization", "Bearer ${BuildConfig.OPEN_AI_KEY}")
            .method(original.method, original.body)
            .build()
        return chain.proceed(request)
    }
}
