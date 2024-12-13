package com.bangkit.replaste.ui

import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.Window
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.bangkit.replaste.R
import com.bangkit.replaste.api.auth.PredictResponse
import com.bangkit.replaste.databinding.ActivityPredictBinding
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.request.target.Target
import com.github.chrisbanes.photoview.PhotoView
import com.google.gson.Gson
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class PredictActivity : AppCompatActivity() {
    private lateinit var binding: ActivityPredictBinding
    private var imageUrl: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPredictBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.toolbar.setNavigationOnClickListener { onBackPressed() }

        val predictResponseJson = intent.getStringExtra("PREDICT_RESPONSE")
        predictResponseJson?.let { json ->
            val predictResponse = Gson().fromJson(json, PredictResponse::class.java)

            
            imageUrl = predictResponse.imageUrl

            Log.d(this@PredictActivity.javaClass.name, "image url : ${imageUrl}")

            
            loadImageWithProgress(predictResponse.imageUrl)

            binding.tvImagePlasticType.text = predictResponse.predictedClass
            binding.tvPlasticType.text = predictResponse.plasticInfo.namaPlastik
            binding.tvPlasticDescription.text = predictResponse.plasticInfo.deskripsi
            binding.tvEnvironmentalImpact.text = predictResponse.plasticInfo.environmentalImpact
            binding.tvConfidence.text = predictResponse.confidence

            val currentDate = SimpleDateFormat("dd MMM yyyy, HH:mm", Locale("id", "ID"))
                .format(Date())
            binding.tvPredictionDate.text = currentDate

            
            binding.btnZoomImage.setOnClickListener {
                showZoomDialog()
            }
        }
    }

    private fun loadImageWithProgress(imageUrl: String?) {
        
        binding.progressBarImage.visibility = View.VISIBLE

        imageUrl?.let { url ->
            Glide.with(this)
                .load(url)
                .apply(
                    RequestOptions()
                        .placeholder(R.drawable.placeholder_plastic)
                        .error(R.drawable.placeholder_plastic)
                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                )
                .listener(object : RequestListener<Drawable> {
                    override fun onLoadFailed(
                        e: GlideException?,
                        model: Any?,
                        target: Target<Drawable>,
                        isFirstResource: Boolean
                    ): Boolean {
                        
                        binding.progressBarImage.visibility = View.GONE

                        
                        Log.e("GlideError", "Image Load Failed: ${e?.message}")
                        e?.printStackTrace()

                        
                        Toast.makeText(
                            this@PredictActivity,
                            "Gagal memuat gambar",
                            Toast.LENGTH_SHORT
                        ).show()

                        
                        binding.ivPredictedPlastic.setImageResource(R.drawable.placeholder_plastic)

                        return false
                    }

                    override fun onResourceReady(
                        resource: Drawable,
                        model: Any,
                        target: Target<Drawable>?,
                        dataSource: DataSource,
                        isFirstResource: Boolean
                    ): Boolean {
                        
                        binding.progressBarImage.visibility = View.GONE
                        return false
                    }
                })
                .into(binding.ivPredictedPlastic)
        } ?: run {
            
            binding.progressBarImage.visibility = View.GONE
            binding.ivPredictedPlastic.setImageResource(R.drawable.placeholder_plastic)

            
            Toast.makeText(
                this,
                "URL gambar tidak tersedia",
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    private fun showZoomDialog() {
        imageUrl?.let { url ->
            
            val dialog = Dialog(this, android.R.style.Theme_Black_NoTitleBar_Fullscreen)
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
            dialog.setContentView(R.layout.dialog_zoom_image)
            dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

            
            val photoView = dialog.findViewById<PhotoView>(R.id.photoView)
            val progressBar = dialog.findViewById<View>(R.id.progressBarZoom)

            
            progressBar.visibility = View.VISIBLE

            
            Glide.with(this)
                .load(url)
                .apply(
                    RequestOptions()
                        .placeholder(R.drawable.placeholder_plastic)
                        .error(R.drawable.placeholder_plastic)
                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                )
                .listener(object : RequestListener<Drawable> {
                    override fun onLoadFailed(
                        e: GlideException?,
                        model: Any?,
                        target: Target<Drawable>,
                        isFirstResource: Boolean
                    ): Boolean {
                        
                        progressBar.visibility = View.GONE

                        
                        Log.e("GlideZoomError", "Zoom Image Load Failed: ${e?.message}")
                        e?.printStackTrace()

                        
                        Toast.makeText(
                            this@PredictActivity,
                            "Gagal memuat gambar",
                            Toast.LENGTH_SHORT
                        ).show()

                        return false
                    }

                    override fun onResourceReady(
                        resource: Drawable,
                        model: Any,
                        target: Target<Drawable>?,
                        dataSource: DataSource,
                        isFirstResource: Boolean
                    ): Boolean {
                        
                        progressBar.visibility = View.GONE
                        return false
                    }
                })
                .into(photoView)

            
            photoView.setOnClickListener {
                dialog.dismiss()
            }

            dialog.show()
        }
    }
}