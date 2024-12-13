package com.bangkit.replaste.ui.type.detail

import android.content.Intent
import android.os.Bundle
import android.view.animation.AnimationUtils
import androidx.appcompat.app.AppCompatActivity
import com.bangkit.replaste.R
import com.bangkit.replaste.api.menu.PlasticTypeModel
import com.bangkit.replaste.databinding.ActivityTypePlasticDetailBinding
import com.bangkit.replaste.ui.guide.data.PlasticGuideModel
import com.bangkit.replaste.ui.guide.data.PlasticGuideRepository
import com.bangkit.replaste.ui.guide.detail_plastic.DetailPlasticGuideActivity
import com.bumptech.glide.Glide
import com.google.android.material.snackbar.Snackbar

class TypePlasticDetailActivity : AppCompatActivity() {
    private lateinit var binding: ActivityTypePlasticDetailBinding
    private var title = ""
    private var introduction = ""

    companion object {
        const val EXTRA_PLASTIC_TYPE = "PLASTIC_TYPE"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTypePlasticDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        PlasticGuideRepository.loadPlasticGuides(this)

        setupToolbar()

        val plasticType = intent.getParcelableExtra<PlasticTypeModel>(EXTRA_PLASTIC_TYPE)

        plasticType?.let {
            displayPlasticTypeDetails(it)
            setupRecyclingGuideButtons(it)
        }
    }

    private fun setupToolbar() {
        binding.toolbar.setNavigationOnClickListener {
            onBackPressed()
        }
    }

    private fun displayPlasticTypeDetails(plasticType: PlasticTypeModel) {
        binding.toolbar.title = plasticType.nama

        Glide.with(this).load(plasticType.imageUrl).placeholder(R.drawable.placeholder_plastic)
            .error(R.drawable.placeholder_plastic).into(binding.ivPlasticDetailImage)

        binding.tvPlasticName.text = plasticType.nama
        binding.tvPlasticDescription.text = plasticType.deskripsi
        binding.tvRecyclingTime.text = "Waktu Daur Ulang: ${plasticType.recyclingTime}"
        binding.tvProdukPenggunaan.text = "Produk Penggunaan: ${plasticType.produkPenggunaan}"
        binding.tvEnvironmentalImpact.text = "Dampak Lingkungan: ${plasticType.environmentalImpact}"
    }

    private fun setupRecyclingGuideButtons(plasticType: PlasticTypeModel) {
        val plasticGuideType = PlasticGuideRepository.getPlasticGuideByFullName(plasticType.nama)
        title = plasticGuideType?.type ?: ""
        introduction = plasticGuideType?.introduction ?: ""

        plasticGuideType?.let { guideType ->
            binding.btnRecyclingGuide1.setOnClickListener {
                navigateToRecyclingGuide(guideType.plastics, 1)
            }

            binding.btnRecyclingGuide2.setOnClickListener {
                navigateToRecyclingGuide(guideType.plastics, 2)
            }
        } ?: run {
            showSnackbar("Panduan daur ulang tidak ditemukan.", {
                setupRecyclingGuideButtons(plasticType)
            })
        }
    }

    private fun navigateToRecyclingGuide(plasticGuides: List<PlasticGuideModel>, stage: Int) {
        val guide = plasticGuides.find { it.id_recycling_step == stage }
        guide?.let {
            val intent = Intent(this, DetailPlasticGuideActivity::class.java).apply {
                putExtra(DetailPlasticGuideActivity.PLASTIC_ID, it.id_recycling_step)
                putExtra(DetailPlasticGuideActivity.PLASTIC_INTRODUCTION, introduction)
                putExtra(DetailPlasticGuideActivity.PLASTIC_TITLE, title)
            }
            startActivity(intent)
        } ?: run {
            showSnackbar("Panduan daur ulang untuk tahap $stage tidak ditemukan.", {
                navigateToRecyclingGuide(plasticGuides, stage)
            })
        }
    }

    private fun showSnackbar(message: String, retryAction: () -> Unit) {
        val actionText = "Coba Lagi"
        val snackbar = Snackbar.make(binding.root, message, Snackbar.LENGTH_INDEFINITE)
            .setAction(actionText) {
                retryAction()
            }.setActionTextColor(resources.getColor(R.color.replaste_accent))
            .setBackgroundTint(resources.getColor(R.color.replaste_primary))
            .setTextColor(resources.getColor(R.color.white)).setDuration(3000)

        snackbar.view.animation = AnimationUtils.loadAnimation(this, R.anim.snackbar_in)

        snackbar.show()
    }
}
