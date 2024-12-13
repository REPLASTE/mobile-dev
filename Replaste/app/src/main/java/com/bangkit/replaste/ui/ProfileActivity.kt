package com.bangkit.replaste.ui

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.bangkit.replaste.R
import com.bangkit.replaste.databinding.ActivityProfileBinding
import com.bangkit.replaste.helper.SecurePreferences
import com.bumptech.glide.Glide
import com.google.android.material.dialog.MaterialAlertDialogBuilder

class ProfileActivity : AppCompatActivity() {
    private lateinit var binding: ActivityProfileBinding
    private lateinit var securePreferences: SecurePreferences
    private lateinit var editProfileLauncher: ActivityResultLauncher<Intent>

    companion object {
        const val EXTRA_FULL_NAME = "extra_full_name"
        const val EXTRA_EMAIL = "extra_email"
        const val EXTRA_PROFILE_IMAGE = "extra_profile_image"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)

        securePreferences = SecurePreferences(this)

        
        initEditProfileLauncher()

        
        setupProfileData()

        
        setupClickListeners()
    }

    private fun initEditProfileLauncher() {
        editProfileLauncher = registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                
                val updatedName = result.data?.getStringExtra(EXTRA_FULL_NAME)
                updatedName?.let {
                    binding.tvProfileName.text = it
                }
            }
        }
    }

    private fun setupProfileData() {
        val fullName = intent.getStringExtra(EXTRA_FULL_NAME)
        val email = intent.getStringExtra(EXTRA_EMAIL)
        val profileImageUrl = intent.getStringExtra(EXTRA_PROFILE_IMAGE)

        binding.tvProfileName.text = fullName ?: "Nama Pengguna"
        binding.tvProfileEmail.text = email ?: "email@example.com"

        
        profileImageUrl?.let { url ->
            Glide.with(this)
                .load(url)
                .placeholder(R.drawable.default_profile)
                .error(R.drawable.default_profile)
                .into(binding.ivProfileImage)
        }
    }

    private fun setupClickListeners() {
        binding.btnLogout.setOnClickListener {
            showLogoutConfirmationDialog()
        }

        binding.layoutEditProfile.setOnClickListener {
            val intent = Intent(this, EditProfileActivity::class.java).apply {
                putExtra(EXTRA_FULL_NAME, binding.tvProfileName.text.toString())
                putExtra(EXTRA_EMAIL, binding.tvProfileEmail.text.toString())
            }
            editProfileLauncher.launch(intent)
        }

        binding.layoutChangePassword.setOnClickListener {
            startActivity(Intent(this, ChangePasswordActivity::class.java))
        }

        binding.toolbar.setNavigationOnClickListener {
            onBackPressed()
        }
    }

    private fun showLogoutConfirmationDialog() {
        MaterialAlertDialogBuilder(this)
            .setTitle("Konfirmasi Logout")
            .setMessage("Apakah Anda yakin ingin keluar dari aplikasi?")
            .setPositiveButton("Keluar") { _, _ ->
                performLogout()
            }
            .setNegativeButton("Batal", null)
            .show()
    }

    private fun performLogout() {
        securePreferences.clearJwtToken()

        val intent = Intent(this, LoginActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
        }
        startActivity(intent)
        finish()
    }
}