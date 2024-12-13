package com.bangkit.replaste.ui.location

import android.content.ActivityNotFoundException
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.bangkit.replaste.databinding.ActivityRecyclingLocationBinding

class RecyclingLocationsActivity : AppCompatActivity() {
    private lateinit var binding: ActivityRecyclingLocationBinding
    private lateinit var locationsAdapter: RecyclingLocationsAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRecyclingLocationBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupRecyclerView()
        fetchRecyclingLocations()

        binding.toolbar.setNavigationOnClickListener {
            onBackPressed()
        }
    }

    private fun setupRecyclerView() {
        locationsAdapter = RecyclingLocationsAdapter(
            onMapClickListener = { location ->
                openGoogleMaps(location.gmapsLink)
            }
        )
        binding.rvRecyclingLocations.apply {
            layoutManager = LinearLayoutManager(this@RecyclingLocationsActivity)
            adapter = locationsAdapter
        }
    }

    private fun fetchRecyclingLocations() {
        binding.progressBar.visibility = View.VISIBLE

        val locations = listOf(
            RecyclingLocation(
                name = "Waste4Change",
                address = "Kantor Waste4Change Vida Bumipala, Jl. Alun Alun Utara",
                phoneNumber = "082111100170",
                gmapsLink = "https://maps.app.goo.gl/Ajmk4LkRpWb2X4kJA"
            )
        )
        locationsAdapter.submitList(locations)
        binding.progressBar.visibility = View.GONE
    }

    fun openGoogleMaps(location: String) {
        try {

            val gmmIntentUri = Uri.parse(location)
            val mapIntent = Intent(Intent.ACTION_VIEW, gmmIntentUri)
            mapIntent.setPackage("com.google.android.apps.maps")


            if (mapIntent.resolveActivity(packageManager) != null) {
                startActivity(mapIntent)
            } else {

                openInBrowser(location)
            }
        } catch (e: ActivityNotFoundException) {

            openInBrowser(location)
        }
    }

    private fun openInBrowser(url: String) {
        try {
            val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
            startActivity(browserIntent)
        } catch (e: Exception) {

            Toast.makeText(
                this,
                "Tidak dapat membuka lokasi. Pastikan perangkat memiliki aplikasi peta atau browser.",
                Toast.LENGTH_SHORT
            ).show()
        }
    }
}