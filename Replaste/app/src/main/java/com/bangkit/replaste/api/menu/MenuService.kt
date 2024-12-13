package com.bangkit.replaste.api.menu

import com.bangkit.replaste.api.ApiConstants
import retrofit2.Call
import retrofit2.http.GET

interface MenuService {
    @GET(ApiConstants.LIST_TYPE_PLASTIC)
    fun getListPlastic(): Call<List<PlasticTypeModel>>
}