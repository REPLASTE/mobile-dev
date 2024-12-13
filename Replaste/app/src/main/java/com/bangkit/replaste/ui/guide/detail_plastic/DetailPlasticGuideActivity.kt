package com.bangkit.replaste.ui.guide.detail_plastic

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.ViewTreeObserver.OnGlobalLayoutListener
import androidx.appcompat.app.AppCompatActivity
import com.bangkit.replaste.databinding.ActivityDetailPlasticGuideBinding
import com.bangkit.replaste.ui.guide.data.PlasticGuideModel
import com.bangkit.replaste.ui.guide.data.PlasticGuideRepository

class DetailPlasticGuideActivity : AppCompatActivity() {
    private lateinit var binding: ActivityDetailPlasticGuideBinding
    private lateinit var plasticTitle: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailPlasticGuideBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val plasticID = intent?.getIntExtra(PLASTIC_ID, -1) ?: -1
        val plasticIntroduction = intent?.getStringExtra(PLASTIC_INTRODUCTION)
        plasticTitle = intent?.getStringExtra(PLASTIC_TITLE) ?: ""

        binding.topAppBar.setNavigationOnClickListener {
            onBackPressed()
        }

        Log.d(TAG, "Received plasticID: $plasticID, plasticIntroduction: $plasticIntroduction")

        if (plasticID == -1) {
            Log.e(TAG, "Plastic ID is null or invalid")
            finish()
            return
        }

        val plasticGuide = PlasticGuideRepository.getPlasticGuideById(plasticID)

        if (plasticGuide == null) {
            Log.e(TAG, "Plastic Guide not found for ID: $plasticID")
            finish()
            return
        }

        binding.root.viewTreeObserver.addOnGlobalLayoutListener(object : OnGlobalLayoutListener {
            override fun onGlobalLayout() {
                binding.root.viewTreeObserver.removeOnGlobalLayoutListener(this)
                displayPlasticGuide(plasticGuide, plasticIntroduction)
            }
        })
    }

    private fun displayPlasticGuide(plasticGuide: PlasticGuideModel?, introduction: String?) {
        binding.tvIntroduction.text = introduction
        binding.topAppBar.title =
            "Daur Ulang ${plasticTitle} Tahap ${plasticGuide?.id_recycling_step}"


        if (plasticGuide != null) {
            val steps = plasticGuide.recycling_steps


            for (i in steps.indices) {
                when (i) {
                    0 -> {
                        binding.tvStepTitleA.text = "🧼 ${steps[i].step_title}"
                        binding.tvStepImportanceA.text = steps[i].importance
                        binding.tvStepDescriptionA.text = steps[i].description
                    }

                    1 -> {
                        binding.tvStepTitleB.text = "✂️ ${steps[i].step_title}"
                        binding.tvStepImportanceB.text = steps[i].importance
                        binding.tvStepDescriptionB.text = steps[i].description
                    }

                    2 -> {
                        binding.tvStepTitleC.text = "🔬 ${steps[i].step_title}"
                        binding.tvStepImportanceC.text = steps[i].importance
                        binding.tvStepDescriptionC.text = steps[i].description
                    }
                }
            }
        }


        binding.btnPlayVideo.setOnClickListener {

            val videoUrl = plasticGuide?.video

            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(videoUrl))
            startActivity(intent)
        }
    }

    companion object {
        private const val TAG = "DetailPlasticGuideActivity"
        const val PLASTIC_ID = "PLASTIC_ID"
        const val PLASTIC_INTRODUCTION = "PLASTIC_INTRODUCTION"
        const val PLASTIC_TITLE = "PLASTIC_TITLE"
    }
}