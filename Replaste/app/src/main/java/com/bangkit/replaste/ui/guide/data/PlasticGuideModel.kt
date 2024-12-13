package com.bangkit.replaste.ui.guide.data

data class PlasticGuideResponse(
    val data: List<PlasticGuideType>
)

data class PlasticGuideType(
    val plastic_full_name: String,
    val type: String,
    val introduction: String,
    val plastics: List<PlasticGuideModel>
)

data class PlasticGuideModel(
    val id_recycling_step: Int,
    val recycling_steps: List<RecyclingStep>,
    val video: String
)

data class RecyclingStep(
    val step_title: String,
    val importance: String,
    val description: String
)