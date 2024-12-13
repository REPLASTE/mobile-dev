package com.bangkit.replaste.ui.guide

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bangkit.replaste.R
import com.bangkit.replaste.ui.guide.data.PlasticGuideRepository
import com.bangkit.replaste.ui.guide.detail.DetailGuideActivity

class RecyclingGuideActivity : AppCompatActivity() {
    private lateinit var recyclerViewGuide: RecyclerView
    private lateinit var guideAdapter: RecyclingGuideAdapter
    private lateinit var toolbar: Toolbar

    private val plasticTypeEmojis = mapOf(
        "PET" to "🍼",
        "HDPE" to "🧴",
        "PVC" to "🚰",
        "LDPE" to "🛍️",
        "PP" to "🍽️",
        "PS" to "☕"
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_recycling_guide)


        PlasticGuideRepository.loadPlasticGuides(this)

        toolbar = findViewById(R.id.topAppBar)

        setupViews()
        setupRecyclerView()

        toolbar.setNavigationOnClickListener {
            onBackPressed()
        }

    }

    private fun setupViews() {
        recyclerViewGuide = findViewById(R.id.recyclerViewPlasticGuide)
    }

    private fun setupRecyclerView() {
        val guideList = getGuideList()
        guideAdapter = RecyclingGuideAdapter(guideList) { guide ->
            navigateToGuideDetail(guide)
        }

        recyclerViewGuide.apply {
            adapter = guideAdapter
            layoutManager = LinearLayoutManager(this@RecyclingGuideActivity)
            setHasFixedSize(true)
        }
    }

    private fun getGuideList(): List<GuideModel> {
        val guideList = mutableListOf<GuideModel>()


        val addedTypes = mutableSetOf<String>()

        PlasticGuideRepository.getAllPlasticGuideTypes().forEach { plasticGuideType ->
            if (addedTypes.add(plasticGuideType.type)) {
                guideList.add(
                    GuideModel(
                        title = "Daur Ulang Plastik ${plasticGuideType.type}",
                        description = plasticGuideType.introduction,
                        emoji = plasticTypeEmojis[plasticGuideType.type] ?: "♻️",
                        category = plasticGuideType.type
                    )
                )
            }
        }

        return guideList
    }

    private fun navigateToGuideDetail(guide: GuideModel) {
        val intent = Intent(this@RecyclingGuideActivity, DetailGuideActivity::class.java).apply {
            putExtra("plastic_guide_id", guide.category)
        }
        startActivity(intent)
    }
}