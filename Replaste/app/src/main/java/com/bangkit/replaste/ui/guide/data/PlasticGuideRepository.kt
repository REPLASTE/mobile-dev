package com.bangkit.replaste.ui.guide.data

import android.content.Context
import com.bangkit.replaste.R
import com.google.gson.Gson

object PlasticGuideRepository {
    private var plasticGuideTypes: List<PlasticGuideType> = listOf()

    fun loadPlasticGuides(context: Context) {
        val jsonString =
            context.resources.openRawResource(R.raw.guide).bufferedReader().use { it.readText() }
        val gson = Gson()
        val plasticGuideResponse = gson.fromJson(jsonString, PlasticGuideResponse::class.java)
        plasticGuideTypes = plasticGuideResponse.data
    }

    fun getPlasticGuideById(id: Int): PlasticGuideModel? {
        return plasticGuideTypes.flatMap { it.plastics }.find { it.id_recycling_step == id }
    }

    fun getPlasticGuideByType(type: String): PlasticGuideType? {
        return plasticGuideTypes.find { it.type.equals(type, ignoreCase = true) }
    }

    fun getPlasticGuideByFullName(plasticFullName: String): PlasticGuideType? {
        return plasticGuideTypes.find {
            it.plastic_full_name.equals(
                plasticFullName,
                ignoreCase = true
            )
        }
    }


    fun getAllPlasticGuideTypes(): List<PlasticGuideType> {
        return plasticGuideTypes
    }
}