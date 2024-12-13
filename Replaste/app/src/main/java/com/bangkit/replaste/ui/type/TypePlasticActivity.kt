package com.bangkit.replaste.ui.type

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.bangkit.replaste.api.ApiClient
import com.bangkit.replaste.api.menu.PlasticTypeModel
import com.bangkit.replaste.databinding.ActivityTypePlasticBinding
import com.bangkit.replaste.ui.type.detail.TypePlasticDetailActivity
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class TypePlasticActivity : AppCompatActivity() {
    private lateinit var binding: ActivityTypePlasticBinding
    private lateinit var plasticTypeAdapter: PlasticTypeAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTypePlasticBinding.inflate(layoutInflater)
        setContentView(binding.root)

        
        setupToolbar()

        
        setupRecyclerView()

        
        setupSwipeRefresh()

        
        fetchPlasticTypes()
    }

    private fun setupToolbar() {
        binding.topAppBar.setNavigationOnClickListener {
            onBackPressed()
        }
    }

    private fun setupRecyclerView() {
        plasticTypeAdapter = PlasticTypeAdapter { plasticType ->
            
            val intent = Intent(this, TypePlasticDetailActivity::class.java).apply {
                putExtra("PLASTIC_TYPE", plasticType)
            }
            startActivity(intent)
        }

        binding.recyclerViewPlasticTypes.apply {
            layoutManager = LinearLayoutManager(this@TypePlasticActivity)
            adapter = plasticTypeAdapter
            setHasFixedSize(true)
        }
    }

    private fun setupSwipeRefresh() {
        binding.swipeRefreshLayout.setOnRefreshListener {
            fetchPlasticTypes()
        }
    }

    private fun fetchPlasticTypes() {
        
        binding.swipeRefreshLayout.isRefreshing = true

        

        ApiClient.menuService.getListPlastic().enqueue(object : Callback<List<PlasticTypeModel>> {
            override fun onResponse(
                call: Call<List<PlasticTypeModel>>,
                response: Response<List<PlasticTypeModel>>
            ) {
                
                binding.swipeRefreshLayout.isRefreshing = false

                if (response.isSuccessful) {
                    
                    val plasticTypes = response.body() ?: emptyList()

                    
                    plasticTypeAdapter.submitList(plasticTypes)

                    
                    if (plasticTypes.isEmpty()) {
                        binding.tvEmptyState.visibility = View.VISIBLE
                        binding.recyclerViewPlasticTypes.visibility = View.GONE
                        binding.tvEmptyState.text = "Tidak ada data plastik"
                    } else {
                        binding.tvEmptyState.visibility = View.GONE
                        binding.recyclerViewPlasticTypes.visibility = View.VISIBLE
                    }
                } else {
                    
                    handleErrorResponse(response)
                }
            }

            override fun onFailure(call: Call<List<PlasticTypeModel>>, t: Throwable) {
                
                binding.swipeRefreshLayout.isRefreshing = false

                
                handleNetworkError(t)
            }
        })
    }

    private fun handleErrorResponse(response: Response<List<PlasticTypeModel>>) {
        val errorMessage = when (response.code()) {
            404 -> "Data tidak ditemukan"
            500 -> "Kesalahan server"
            403 -> "Akses ditolak"
            else -> "Gagal memuat data: ${response.code()}"
        }

        binding.tvEmptyState.text = errorMessage
        binding.tvEmptyState.visibility = View.VISIBLE
        binding.recyclerViewPlasticTypes.visibility = View.GONE

        Toast.makeText(this, errorMessage, Toast.LENGTH_SHORT).show()
    }

    private fun handleNetworkError(throwable: Throwable) {
        val errorMessage = when (throwable) {
            is java.net.UnknownHostException -> "Tidak ada koneksi internet"
            is java.net.SocketTimeoutException -> "Koneksi timeout"
            else -> "Kesalahan jaringan: ${throwable.message}"
        }

        binding.tvEmptyState.text = errorMessage
        binding.tvEmptyState.visibility = View.VISIBLE
                binding.recyclerViewPlasticTypes.visibility = View.GONE

        Toast.makeText(this, errorMessage, Toast.LENGTH_SHORT).show()
    }
}