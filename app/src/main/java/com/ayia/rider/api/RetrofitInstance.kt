package com.ayia.rider.api

import com.ayia.rider.BASE_URL
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

class RetrofitInstance {

    private val gsonConverterFactory = GsonConverterFactory.create()

    private var retrofit: Retrofit? = null

    private val okHttpClient = OkHttpClient.Builder()
        .connectTimeout(10, TimeUnit.SECONDS)
        .readTimeout(5, TimeUnit.SECONDS)
        .build()


    fun getRetrofitService(): ApiService? {
        if (retrofit == null) {
            retrofit = Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(gsonConverterFactory)
                .client(okHttpClient)
                .build()
        }
        return retrofit?.create(ApiService::class.java)
    }
}