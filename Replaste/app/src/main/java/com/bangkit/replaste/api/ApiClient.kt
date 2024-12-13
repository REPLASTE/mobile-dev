package com.bangkit.replaste.api

import com.bangkit.replaste.api.auth.AuthApiService
import com.bangkit.replaste.api.history.HistoryService
import com.bangkit.replaste.api.menu.MenuService
import com.bangkit.replaste.api.plastic.PlasticService
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

object ApiClient {
    private val retrofit: Retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(ApiConstants.BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }
    private val plasticRetrofit: Retrofit by lazy {
        val okHttpClient = OkHttpClient.Builder()
            .connectTimeout(60, TimeUnit.SECONDS)
            .readTimeout(60, TimeUnit.SECONDS)
            .writeTimeout(60, TimeUnit.SECONDS)
            .build()

        Retrofit.Builder()
            .baseUrl(ApiConstants.PLASTIC_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    val authService: AuthApiService by lazy {
        retrofit.create(AuthApiService::class.java)
    }

    val menuService: MenuService by lazy {
        retrofit.create(MenuService::class.java)
    }


    val historyService: HistoryService by lazy {
        retrofit.create(HistoryService::class.java)
    }

    val plasticService: PlasticService by lazy {
        plasticRetrofit.create(PlasticService::class.java)
    }
}