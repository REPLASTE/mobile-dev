package com.bangkit.replaste.ui.history

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.airbnb.lottie.LottieDrawable
import com.bangkit.replaste.R
import com.bangkit.replaste.api.ApiClient
import com.bangkit.replaste.api.auth.HistoryResponse
import com.bangkit.replaste.databinding.ActivityHistoryBinding
import com.bangkit.replaste.helper.SecurePreferences
import com.bangkit.replaste.ui.CameraActivity
import com.bangkit.replaste.ui.MainActivity
import com.google.android.material.bottomnavigation.BottomNavigationView
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class HistoryActivity : AppCompatActivity() {
    private lateinit var binding: ActivityHistoryBinding
    private lateinit var historyAdapter: HistoryAdapter
    private val historyList = mutableListOf<HistoryItem>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHistoryBinding.inflate(layoutInflater)
        setContentView(binding.root)

        
        binding.toolbar.setNavigationOnClickListener { onBackPressed() }

        
        setupLottieLoading()

        
        binding.swipeRefreshLayout.setOnRefreshListener {
            loadHistoryFromApi()
        }

        setupBottomNavigation()
        loadHistoryFromApi()
    }

    private fun setupLottieLoading() {
        binding.lottieLoading.apply {
            
            setAnimation(R.raw.loading_animation)

            
            addAnimatorListener(object : AnimatorListenerAdapter() {
                override fun onAnimationStart(animation: Animator) {
                    Log.d("LottieAnimation", "Animation Started")
                }

                override fun onAnimationEnd(animation: Animator) {
                    Log.d("LottieAnimation", "Animation Ended")
                }
            })

            
            repeatCount = LottieDrawable.INFINITE
            speed = 1.0f

            
            scaleType = ImageView.ScaleType.CENTER_INSIDE

            
            playAnimation()
        }
    }
    private fun showLoading() {
        binding.apply {
            loadingContainer.visibility = View.VISIBLE
            rvHistory.visibility = View.GONE
            emptyStateLayout.visibility = View.GONE

            
            lottieLoading.playAnimation()
        }
    }

    private fun hideLoading() {
        binding.apply {
            loadingContainer.visibility = View.GONE
            swipeRefreshLayout.isRefreshing = false

            
            lottieLoading.cancelAnimation()
        }
    }

    private fun loadHistoryFromApi() {
        
        showLoading()

        val securePreferences = SecurePreferences(this)
        val userId = securePreferences.getJwtToken() ?: return

        ApiClient.historyService.getHistoryPredictions(userId)
            .enqueue(object : Callback<HistoryResponse> {
                override fun onResponse(
                    call: Call<HistoryResponse>,
                    response: Response<HistoryResponse>
                ) {
                    
                    hideLoading()

                    if (response.isSuccessful) {
                        response.body()?.let { historyResponse ->
                            processHistoryResponse(historyResponse)
                        } ?: run {
                            showEmptyState()
                        }
                    } else {
                        Log.e("HistoryActivity", "Error: ${response.code()} ${response.message()}")
                        showEmptyState()
                    }
                }

                override fun onFailure(call: Call<HistoryResponse>, t: Throwable) {
                    
                    hideLoading()

                    Log.e("HistoryActivity", "Network Error: ${t.message}")
                    showEmptyState()
                }
            })
    }

    private fun processHistoryResponse(historyResponse: HistoryResponse) {
        historyList.clear()
        val dateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.US)

        historyResponse.data.forEach { historyData ->
            val date = dateFormat.parse(historyData.date) ?: Date()

            historyList.add(
                HistoryItem(
                    imageFile = historyData.imageFile,
                    predictedClass = historyData.predictedClass,
                    confidence = historyData.confidence.toFloat() / 100f,
                    date = date
                )
            )
        }

        
        historyList.sortByDescending { it.date }

        setupRecyclerView()
    }


    private fun setupRecyclerView() {
        val groupedHistory = groupHistoryByDate()
        historyAdapter = HistoryAdapter(groupedHistory)
        binding.rvHistory.apply {
            layoutManager = LinearLayoutManager(this@HistoryActivity)
            adapter = historyAdapter
        }

        updateEmptyState(groupedHistory)
    }

    private fun updateEmptyState(groupedHistory: Map<String, List<HistoryItem>>) {
        if (groupedHistory.isEmpty()) {
            showEmptyState()
        } else {
            binding.rvHistory.visibility = View.VISIBLE
            binding.emptyStateLayout.visibility = View.GONE
        }
    }

    private fun showEmptyState() {
        binding.rvHistory.visibility = View.GONE
        binding.emptyStateLayout.visibility = View.VISIBLE
    }

    private fun groupHistoryByDate(): Map<String, List<HistoryItem>> {
        val dateFormatter = SimpleDateFormat("dd MMM yyyy", Locale.getDefault())
        return historyList.groupBy { dateFormatter.format(it.date) }
    }

    private fun setupBottomNavigation() {
        val bottomNav = findViewById<BottomNavigationView>(R.id.bottomNav_history)
        bottomNav.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_home -> {
                    startActivity(Intent(this, MainActivity::class.java))
                    true
                }
                R.id.nav_camera -> {
                    startActivity(Intent(this, CameraActivity::class.java))
                    true
                }
                R.id.nav_history -> {
                    true
                }
                else -> false
            }
        }
        bottomNav.selectedItemId = R.id.nav_history
    }
}
