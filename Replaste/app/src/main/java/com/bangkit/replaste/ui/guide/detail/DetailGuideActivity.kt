package com.bangkit.replaste.ui.guide.detail

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.bangkit.replaste.databinding.ActivityDetailGuideBinding
import com.bangkit.replaste.ui.guide.data.PlasticGuideRepository
import com.bangkit.replaste.ui.guide.detail_plastic.DetailPlasticGuideActivity

class DetailGuideActivity : AppCompatActivity() {
    private lateinit var binding: ActivityDetailGuideBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailGuideBinding.inflate(layoutInflater)
        setContentView(binding.root)


        val plasticGuideType = intent.getStringExtra("plastic_guide_id") ?: return


        val plasticGuides = PlasticGuideRepository.getPlasticGuideByType(plasticGuideType)


        val guidesList = plasticGuides?.plastics ?: emptyList()


        if (plasticGuides != null && guidesList.isNotEmpty()) {
            Log.d(TAG, plasticGuides.introduction)
        } else {
            Log.d(TAG, "No guides found for type: $plasticGuideType")
        }

        binding.toolbar.setNavigationOnClickListener {
            onBackPressed()
        }


        binding.recyclerView.layoutManager = LinearLayoutManager(this)
        binding.recyclerView.adapter = DetailGuideAdapter(guidesList) { plastic ->

            val plasticId = plastic.id_recycling_step
            val introduction = plasticGuides?.introduction

            Log.d(TAG, "Attempting to send plasticId: $plasticId, introduction: $introduction")


            if (plasticId >= 0 && introduction != null) {
                val intent = Intent(this@DetailGuideActivity, DetailPlasticGuideActivity::class.java)
                intent.putExtra(DetailPlasticGuideActivity.PLASTIC_ID, plasticId)
                intent.putExtra(DetailPlasticGuideActivity.PLASTIC_INTRODUCTION, introduction)
                intent.putExtra(DetailPlasticGuideActivity.PLASTIC_TITLE, plasticGuideType)
                startActivity(intent)
            } else {
                Log.e(TAG, "Cannot start DetailPlasticGuideActivity: plasticId must be greater than 0 and introduction cannot be null")
            }
        }


        binding.toolbar.title = "Panduan Daur Ulang $plasticGuideType"
    }

    companion object {
        private const val TAG = "DetailGuideActivity"
    }
}