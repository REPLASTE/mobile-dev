package com.bangkit.replaste.ui

import android.app.Activity
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.addTextChangedListener
import com.airbnb.lottie.LottieAnimationView
import com.bangkit.replaste.R
import com.bangkit.replaste.api.ApiClient
import com.bangkit.replaste.api.auth.MessageResponse
import com.bangkit.replaste.api.auth.UpdateProfileRequest
import com.bangkit.replaste.databinding.ActivityEditProfileBinding
import com.bangkit.replaste.helper.SecurePreferences
import com.bangkit.replaste.ui.ProfileActivity.Companion.EXTRA_EMAIL
import com.bangkit.replaste.ui.ProfileActivity.Companion.EXTRA_FULL_NAME
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class EditProfileActivity : AppCompatActivity() {
    private lateinit var binding: ActivityEditProfileBinding
    private var loadingDialog: AlertDialog? = null
    private var initialName: String = ""
    private var currentName: String = ""
    private var email: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEditProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupToolbar()
        initializeProfileData()
        setupListeners()
    }

    private fun setupToolbar() {
        binding.toolbar.setNavigationOnClickListener { onBackPressed() }
    }

    private fun initializeProfileData() {
        initialName = intent.getStringExtra(EXTRA_FULL_NAME) ?: ""
        email = intent.getStringExtra(EXTRA_EMAIL) ?: "email@example.com"

        binding.etNamaLengkap.setText(initialName)
        binding.tvEmail.text = email

        currentName = initialName

        binding.btnSimpanProfil.isEnabled = false

        binding.etNamaLengkap.addTextChangedListener { text ->
            currentName = text.toString().trim()
            binding.btnSimpanProfil.isEnabled =
                currentName != initialName && currentName.isNotEmpty()
        }
    }

    private fun setupListeners() {
        binding.btnSimpanProfil.setOnClickListener {
            showConfirmationDialog()
        }
    }

    private fun showConfirmationDialog() {
        MaterialAlertDialogBuilder(this)
            .setTitle("Konfirmasi Perubahan")
            .setMessage("Apakah Anda yakin ingin menyimpan perubahan nama profil?")
            .setPositiveButton("Ya") { _, _ ->
                saveProfile()
            }
            .setNegativeButton("Tidak", null)
            .show()
    }

    private fun saveProfile() {
        if (currentName.length < 3) {
            showErrorDialog("Nama profil harus terdiri dari minimal 3 karakter.")
            return
        }

        val dialogView = layoutInflater.inflate(R.layout.dialog_save_loading, null)
        val animationView = dialogView.findViewById<LottieAnimationView>(R.id.animationView)
        animationView.setAnimation(R.raw.loading_animation)
        animationView.playAnimation()

        loadingDialog = MaterialAlertDialogBuilder(this)
            .setView(dialogView)
            .setCancelable(false)
            .create()
        loadingDialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        loadingDialog?.show()

        val updateProfileRequest = UpdateProfileRequest(fullName = currentName)

        val securePreferences = SecurePreferences(this)

        val userId = securePreferences.getJwtToken().toString()

        ApiClient.authService.changeProfile(userId, updateProfileRequest)
            .enqueue(object : Callback<MessageResponse> {
                override fun onResponse(
                    call: Call<MessageResponse>,
                    response: Response<MessageResponse>
                ) {
                    loadingDialog?.dismiss()
                    if (response.isSuccessful) {
                        val resultIntent = Intent().apply {
                            putExtra(EXTRA_FULL_NAME, currentName)
                        }
                        setResult(Activity.RESULT_OK, resultIntent)
                        showSuccessDialog()
                    } else {
                        showErrorDialog("Gagal memperbarui profil. Silakan coba lagi.")
                    }
                }

                override fun onFailure(call: Call<MessageResponse>, t: Throwable) {
                    loadingDialog?.dismiss()
                    showErrorDialog("Terjadi kesalahan: ${t.message}")
                }
            })
    }

    private fun showSuccessDialog() {
        MaterialAlertDialogBuilder(this)
            .setTitle("Berhasil")
            .setMessage("Nama profil berhasil diperbarui")
            .setPositiveButton("OK") { dialog, _ ->
                dialog.dismiss()
                finish()
            }
            .show()
    }

    private fun showErrorDialog(message: String) {
        loadingDialog?.dismiss()
        MaterialAlertDialogBuilder(this)
            .setTitle("Kesalahan")
            .setMessage(message)
            .setPositiveButton("OK", null)
            .show()
    }
}
