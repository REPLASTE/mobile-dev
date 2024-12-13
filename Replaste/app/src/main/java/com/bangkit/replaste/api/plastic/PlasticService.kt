package com.bangkit.replaste.api.plastic

import com.bangkit.replaste.api.ApiConstants
import com.bangkit.replaste.api.auth.PredictResponse
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part

interface PlasticService {

    @Multipart
    @POST(ApiConstants.PREDICT_PLASTIC)
    fun predictPlastic(
        @Part("user_id") userId: RequestBody,
        @Part file: MultipartBody.Part
    ): Call<PredictResponse>
}