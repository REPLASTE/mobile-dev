package com.bangkit.replaste.api.auth


import com.bangkit.replaste.api.ApiConstants
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

interface AuthApiService {

    @POST(ApiConstants.LOGIN)
    fun login(@Body body: LoginRequest): Call<AuthResponse>

    @POST(ApiConstants.REGISTER)
    fun register(@Body body: RegisterRequest): Call<AuthResponse>

    @POST(ApiConstants.REQUEST_RESET)
    fun requestPasswordReset(@Body request: ResetPasswordRequest): Call<MessageResponse>

    @GET(ApiConstants.PROFILE_USER)
    fun getProfileUser(@Path("userId") userId: String): Call<UserProfileResponse>

    @PUT(ApiConstants.CHANGE_PASSWORD_USER)
    fun changePassword(
        @Path("userId") userId: String,
        @Body request: ChangePasswordRequest
    ): Call<MessageResponse>

    @PUT(ApiConstants.UPDATE_PROFILE_USER)
    fun changeProfile(
        @Path("userId") userId: String,
        @Body request: UpdateProfileRequest
    ): Call<MessageResponse>


}