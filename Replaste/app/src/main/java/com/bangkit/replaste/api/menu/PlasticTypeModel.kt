package com.bangkit.replaste.api.menu

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
data class PlasticTypeModel(
    @SerializedName("nama") val nama: String,
    @SerializedName("kode") val kode: String,
    @SerializedName("deskripsi") val deskripsi: String,
    @SerializedName("recycling_time") val recyclingTime: String,
    @SerializedName("produk_penggunaan") val produkPenggunaan: String,
    @SerializedName("environmental_impact") val environmentalImpact: String,
    @SerializedName("image_url") val imageUrl: String
) : Parcelable
