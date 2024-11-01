package com.example.featherlyspy.lilly

import com.haroldadmin.cnradapter.NetworkResponseAdapterFactory
import com.squareup.moshi.Moshi
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import okhttp3.Interceptor

object OpenAIClient {

    private const val API_KEY = ""

    private var retrofit: Retrofit? = null

    fun getClient(): Retrofit {
        if (retrofit == null) {

            // Create an interceptor to add API key to the headers
            val apiKeyInterceptor = Interceptor { chain ->
                val original = chain.request()
                val request = original.newBuilder()
                    .header("Authorization", "Bearer $API_KEY")
                    .method(original.method, original.body)
                    .build()
                chain.proceed(request)
            }

            val loggingInterceptor = HttpLoggingInterceptor().apply {
                level = HttpLoggingInterceptor.Level.BODY
            }

            // Build OkHttpClient with the interceptor
            val client = OkHttpClient.Builder()
                .addInterceptor(apiKeyInterceptor)  // Add the API key interceptor
                .addInterceptor(loggingInterceptor) // Add logging interceptor (optional)
                .build()

            val moshi = Moshi.Builder().build()

            retrofit = Retrofit.Builder()
                .baseUrl("https://api.openai.com/v1/")  // Base URL for OpenAI API
                .client(client)
                .addConverterFactory(MoshiConverterFactory.create(moshi))  // Moshi Converter
                .addCallAdapterFactory(NetworkResponseAdapterFactory())    // NetworkResponseAdapter
                .build()
        }
        return retrofit!!
    }
}
