package com.bangkit.replaste.api.auth

import com.google.gson.annotations.SerializedName


data class LoginRequest(
    val email: String,
    val password: String
)

data class RegisterRequest(
    val email: String,
    val password: String,
    val full_name: String
)

data class ResetPasswordRequest(
    val email: String
)

data class PredictResponse(
    val confidence: String,
    @SerializedName("image_url")
    val imageUrl: String,

    @SerializedName("plastic_info")
    val plasticInfo: PlasticInfo,

    @SerializedName("predicted_class")
    val predictedClass: String,

    @SerializedName("prediction_id")
    val predictionId: Int
)

data class PlasticInfo(
    @SerializedName("Deskripsi")
    val deskripsi: String,

    @SerializedName("Environmental impact")
    val environmentalImpact: String,

    @SerializedName("Nama plastik")
    val namaPlastik: String,

    @SerializedName("Produk penggunaan")
    val produkPenggunaan: List<String>,

    @SerializedName("Recycling time")
    val recyclingTime: String,

    @SerializedName("Simbol Kode")
    val simbolKode: String
)

data class HistoryResponse(
    val data: List<HistoryData>
)

data class HistoryData(
    val imageFile: String,
    val predictedClass: String,
    val confidence: Double,
    val date: String
)

data class UserProfileResponse(
    @SerializedName("full_name")
    val fullName: String,
    val email: String,
    @SerializedName("profile_image_url")
    val profileImageUrl: String? = null
)

data class UpdateProfileRequest(
    @SerializedName("full_name")
    val fullName: String,
)


data class ChangePasswordRequest(
    val oldPassword: String,
    val newPassword: String,
    val retypePassword: String
)

data class AuthResponse(
    val token: String,
    val message: String
)

data class MessageResponse(
    @SerializedName("message") val message: String? = null
)
