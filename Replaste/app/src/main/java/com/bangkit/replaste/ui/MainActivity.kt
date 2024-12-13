package com.bangkit.replaste.ui

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.bangkit.replaste.R
import com.bangkit.replaste.api.ApiClient
import com.bangkit.replaste.api.auth.UserProfileResponse
import com.bangkit.replaste.databinding.ActivityMainBinding
import com.bangkit.replaste.helper.SecurePreferences
import com.bangkit.replaste.ui.guide.RecyclingGuideActivity
import com.bangkit.replaste.ui.history.HistoryActivity
import com.bangkit.replaste.ui.location.RecyclingLocationsActivity
import com.bangkit.replaste.ui.type.TypePlasticActivity
import com.google.android.material.snackbar.Snackbar
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.Calendar

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        
        setupUserProfileCard()

        setupUI()
        setupBottomNavigation()
        setupCameraButton()


        binding.menuLocation.setOnClickListener {
            startActivity(Intent(this@MainActivity, RecyclingLocationsActivity::class.java))
        }

        binding.menuGuide.setOnClickListener {
            startActivity(Intent(this@MainActivity, RecyclingGuideActivity::class.java))
        }

        binding.menuPlasticType.setOnClickListener {
            startActivity(Intent(this@MainActivity, TypePlasticActivity::class.java))
        }
    }

    private fun setupUI() {
        val greeting = when (Calendar.getInstance().get(Calendar.HOUR_OF_DAY)) {
            in 0..11 -> "Selamat Pagi"
            in 12..14 -> "Selamat Siang"
            in 15..18 -> "Selamat Sore"
            else -> " Selamat Malam"
        }
        binding.greetingText.text = "$greeting, Plastik Warrior!"
    }

    private fun setupBottomNavigation() {
        binding.bottomNav.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_home -> {
                    true
                }
                R.id.nav_history -> {
                    startActivity(Intent(this, HistoryActivity::class.java))
                    true
                }
                R.id.nav_camera -> {
                    if (checkCameraPermission()) {
                        openCamera()
                    }
                    true
                }
                else -> false
            }
        }
        binding.bottomNav.selectedItemId = R.id.nav_home
    }

    private fun setupCameraButton() {
        binding.cameraButton.setOnClickListener {
            if (checkCameraPermission()) {
                openCamera()
            }
        }
    }

    private fun setupUserProfileCard() {
        val securePreferences = SecurePreferences(this)
        val token = securePreferences.getJwtToken()

        ApiClient.authService.getProfileUser(token.toString()).enqueue(object : Callback<UserProfileResponse> {
            override fun onResponse(
                call: Call<UserProfileResponse>,
                response: Response<UserProfileResponse>
            ) {
                if (response.isSuccessful) {
                    response.body()?.let { userProfile ->
                        
                        binding.tvUserName.text = userProfile.fullName
                        binding.tvUserEmail.text = userProfile.email
                        binding.ivUserProfile.setImageResource(R.drawable.default_profile)

                        binding.cardUserProfile.setOnClickListener {
                            val intent = Intent(this@MainActivity, ProfileActivity::class.java).apply {
                                putExtra(ProfileActivity.EXTRA_FULL_NAME, userProfile.fullName)
                                putExtra(ProfileActivity.EXTRA_EMAIL, userProfile.email)
                                userProfile.profileImageUrl?.let { imageUrl ->
                                    putExtra(ProfileActivity.EXTRA_PROFILE_IMAGE, imageUrl)
                                }
                            }
                            startActivity(intent)
                        }
                    }
                } else {
                    handleProfileError(response.code())
                }
            }

            override fun onFailure(call: Call<UserProfileResponse>, t: Throwable) {
                handleNetworkError(t)
            }
        })
    }

    private fun handleProfileError(errorCode: Int) {
        when (errorCode) {
            401 -> {
                binding.tvUserName.text = "Sesi Habis"
                binding.tvUserEmail.text = "Silakan login kembali"
                startActivity(Intent(this, LoginActivity::class.java))
                finish()
            }
            404 -> {
                binding.tvUserName.text = "Profil Tidak Ditemukan"
                binding.tvUserEmail.text = "Terjadi kesalahan"
            }
            else -> {
                binding.tvUserName.text = "Error Memuat Profil"
                binding.tvUserEmail.text = "Cek koneksi internet"
            }
        }
    }

    private fun handleNetworkError(throwable: Throwable) {
        
        Log.e("ProfileError", "Gagal memuat profil: ${throwable.message}")

        
        binding.tvUserName.text = "Gagal Memuat"
        binding.tvUserEmail.text = "Periksa koneksi internet"

        
        Snackbar.make(
            binding.root,
            "Gagal memuat profil. Periksa koneksi internet.",
            Snackbar.LENGTH_LONG
        ).show()
    }

    private fun checkCameraPermission(): Boolean {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.CAMERA), CAMERA_REQUEST_CODE)
            return false
        }
        return true
    }

    private fun openCamera() {
        val intent = Intent(this, CameraActivity::class.java)
        startActivityForResult(intent, CAMERA_REQUEST_CODE)
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == CAMERA_REQUEST_CODE && grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            openCamera()
        } else {
            Toast.makeText(this, "Izin kamera diperlukan untuk fitur ini", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == CAMERA_REQUEST_CODE && resultCode == RESULT_OK) {
            Toast.makeText(this, "Foto berhasil diambil", Toast.LENGTH_SHORT).show()
        }
    }

    companion object {
        private const val CAMERA_REQUEST_CODE = 1001
    }
}