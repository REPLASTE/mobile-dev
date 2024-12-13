package com.bangkit.replaste.ui.guide.detail


data class GuideModelPlastic(
    val id: String,
    val nama: String,
    val kode: String,
    val deskripsi: String,


    val tipsDaaurUlang: List<String>,
    val produkUmum: List<String>,
    val dampakLingkungan: String,
    val pengolahanTerbaik: String
)