package com.bangkit.replaste.api.history

import com.bangkit.replaste.api.ApiConstants
import com.bangkit.replaste.api.auth.HistoryResponse
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path

interface HistoryService {
    @GET(ApiConstants.HISTORY)
    fun getHistoryPredictions(@Path("userId") userId: String): Call<HistoryResponse>
}