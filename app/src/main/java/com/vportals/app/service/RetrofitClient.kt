package com.vportals.app.service

import android.content.Context
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.converter.scalars.ScalarsConverterFactory


object RetrofitClient {
    private var retrofitClient: Retrofit? = null

    fun getClient(url: String, context: Context): Retrofit? {
        if (retrofitClient == null) {
            retrofitClient = Retrofit.Builder()
                .baseUrl(url)
                .addConverterFactory(ScalarsConverterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
                .client(provideOkHttpClient(context))
                .build()
        }
        return retrofitClient
    }

    private fun provideCustomInterceptor(
        token: String?,
        headers: Map<String, String>?
    ): Interceptor {
        return Interceptor { chain: Interceptor.Chain ->
            val original = chain.request()

            // Request customization: add request headers
            val requestBuilder = original.newBuilder()
            requestBuilder.addHeader("Accept", "application/json")

            if (token != null) {
                requestBuilder.addHeader("Authorization", String.format("Token %s", token))
            }

            if (headers != null && headers.isNotEmpty()) {
                for ((key, value) in headers.entries) {
                    requestBuilder.addHeader(key, value)
                }
            }

            val request = requestBuilder.build()
            chain.proceed(request)
        }
    }

    private fun provideOkHttpClient(context: Context): OkHttpClient {
        return OkHttpClient.Builder()
            .addInterceptor(provideCustomInterceptor(null, null))
            .build()
    }
}
