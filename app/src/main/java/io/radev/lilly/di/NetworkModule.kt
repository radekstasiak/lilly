package io.radev.lilly.di

import com.example.featherlyspy.lilly.OpenAIService
import com.haroldadmin.cnradapter.NetworkResponseAdapterFactory
import com.squareup.moshi.Moshi
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import io.radev.lilly.network.OpenAiApiKeyInterceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory

@Module
@InstallIn(SingletonComponent::class)
class NetworkModule {

    @Provides
    fun getAuthorizationInterceptor(): OpenAiApiKeyInterceptor =
        OpenAiApiKeyInterceptor()

    @Provides
    fun getLoggingInterceptor() = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BODY
    }

    @Provides
    fun getOkHttpClient(
        openAiApiKeyInterceptor: OpenAiApiKeyInterceptor,
        httpLoggingInterceptor: HttpLoggingInterceptor
    ) = OkHttpClient.Builder()
        .addInterceptor(openAiApiKeyInterceptor)  // Add the API key interceptor
        .addInterceptor(httpLoggingInterceptor) // Add logging interceptor (optional)
        .build()

    @Provides
    fun getMoshi(): Moshi = Moshi.Builder().build()

    @Provides
    fun getRetrofit(
        okHttpClient: OkHttpClient,
        moshi: Moshi
    ) = Retrofit.Builder()
        .baseUrl("https://api.openai.com/v1/")  // Base URL for OpenAI API
        .client(okHttpClient)
        .addConverterFactory(MoshiConverterFactory.create(moshi))  // Moshi Converter
        .addCallAdapterFactory(NetworkResponseAdapterFactory())    // NetworkResponseAdapter
        .build()
        .create(OpenAIService::class.java)

}
