package com.bangkit.replaste.ui

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.airbnb.lottie.LottieAnimationView
import com.bangkit.replaste.R
import com.bangkit.replaste.api.ApiClient
import com.bangkit.replaste.api.auth.ChangePasswordRequest
import com.bangkit.replaste.api.auth.MessageResponse
import com.bangkit.replaste.databinding.ActivityChangePasswordBinding
import com.bangkit.replaste.helper.SecurePreferences
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ChangePasswordActivity : AppCompatActivity() {
    private lateinit var binding: ActivityChangePasswordBinding
    private var loadingDialog: AlertDialog? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityChangePasswordBinding.inflate(layoutInflater)
        setContentView(binding.root)

        
        binding.toolbar.setNavigationOnClickListener { onBackPressed() }

        
        setupTextChangeListeners()

        
        binding.btnSimpanKataSandi.setOnClickListener {
            validateAndChangePassword()
        }
    }

    private fun setupTextChangeListeners() {
        binding.etPasswordLama.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                binding.layoutPasswordLama.error = null
            }
            override fun afterTextChanged(s: Editable?) {}
        })

        binding.etPasswordBaru.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                binding.layoutPasswordBaru.error = null
            }
            override fun afterTextChanged(s: Editable?) {}
        })

        binding.etKonfirmasiPassword.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                binding.layoutKonfirmasiPassword.error = null
            }
            override fun afterTextChanged(s: Editable?) {}
        })
    }

    private fun validateAndChangePassword() {
        val oldPassword = binding.etPasswordLama.text.toString().trim()
        val newPassword = binding.etPasswordBaru.text.toString().trim()
        val confirmPassword = binding.etKonfirmasiPassword.text.toString().trim()

        
        binding.layoutPasswordLama.error = null
        binding.layoutPasswordBaru.error = null
        binding.layoutKonfirmasiPassword.error = null

        
        val isValid = validatePasswords(oldPassword, newPassword, confirmPassword)

        if (isValid) {
            showConfirmationDialog(oldPassword, newPassword, confirmPassword)
        }
    }

    private fun validatePasswords(
        oldPassword: String,
        newPassword: String,
        confirmPassword: String
    ): Boolean {
        var isValid = true

        
        if (oldPassword.isEmpty()) {
            binding.layoutPasswordLama.error = "Password lama tidak boleh kosong"
            isValid = false
        }

        
        when {
            newPassword.isEmpty() -> {
                binding.layoutPasswordBaru.error = "Password baru tidak boleh kosong"
                isValid = false
            }
            newPassword.length < 8 -> {
                binding.layoutPasswordBaru.error = "Password minimal 8 karakter"
                isValid = false
            }
            !isPasswordStrong(newPassword) -> {
                binding.layoutPasswordBaru.error = "Password harus mengandung huruf besar, huruf kecil, dan angka"
                isValid = false
            }
        }

        
        when {
            confirmPassword.isEmpty() -> {
                binding.layoutKonfirmasiPassword.error = "Konfirmasi password tidak boleh kosong"
                isValid = false
            }
            newPassword != confirmPassword -> {
                binding.layoutKonfirmasiPassword.error = "Konfirmasi password tidak cocok"
                isValid = false
            }
        }

        return isValid
    }

    private fun isPasswordStrong(password: String): Boolean {
        val uppercaseRegex = ".*[A-Z].*".toRegex()
        val lowercaseRegex = ".*[a-z].*".toRegex()
        val digitRegex = ".*\\d.*".toRegex()

        return (password.matches(uppercaseRegex) &&
                password.matches(lowercaseRegex) &&
                password.matches(digitRegex))
    }

    private fun showConfirmationDialog(
        oldPassword: String,
        newPassword: String,
        confirmPassword: String
    ) {
        MaterialAlertDialogBuilder(this)
            .setTitle("Konfirmasi Perubahan")
            .setMessage("Apakah Anda yakin ingin mengubah password?")
            .setPositiveButton("Ya") { _, _ ->
                changePassword(oldPassword, newPassword, confirmPassword)
            }
            .setNegativeButton("Tidak", null)
            .show()
    }

    private fun changePassword(
        oldPassword: String,
        newPassword: String,
        confirmPassword: String
    ) {
        
        showLoadingDialog()

        

        val securePreferences = SecurePreferences(this)

        val userId = securePreferences.getJwtToken().toString()

        
        val request = ChangePasswordRequest(
            oldPassword = oldPassword,
            newPassword = newPassword,
            retypePassword = confirmPassword
        )

        
        ApiClient.authService.changePassword(userId, request)
            .enqueue(object : Callback<MessageResponse> {
                override fun onResponse(
                    call: Call<MessageResponse>,
                    response: Response<MessageResponse>
                ) {
                    
                    loadingDialog?.dismiss()

                    Log.d("ChangePasswordActivity", "Response code: ${response.code()}")
                    Log.d("ChangePasswordActivity", "Response body: ${response.body()}")
                    Log.d("ChangePasswordActivity", "Error body: ${response.errorBody()?.string()}")

                    if (response.isSuccessful) {
                        
                        showSuccessDialog(response.body()?.message ?: "Password berhasil diubah")
                    } else {
                        
                        val errorMessage = try {
                            val errorBody = response.errorBody()?.string()
                            val errorJson = JSONObject(errorBody ?: "")
                            errorJson.getString("message")
                        } catch (e: Exception) {
                            "Gagal mengubah password"
                        }
                        showErrorDialog(errorMessage)
                    }
                }

                override fun onFailure(call: Call<MessageResponse>, t: Throwable) {
                    
                    loadingDialog?.dismiss()

                    
                    showErrorDialog(t.message ?: "Terjadi kesalahan jaringan")
                }
            })
    }

    private fun showLoadingDialog() {
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
    }

    private fun showSuccessDialog(message: String) {
        MaterialAlertDialogBuilder(this)
            .setTitle("Berhasil")
            .setMessage(message)
            .setPositiveButton("OK") { dialog, _ ->
                dialog.dismiss()
                finish()
            }
            .show()
    }

    private fun showErrorDialog(message: String) {
        MaterialAlertDialogBuilder(this)
            .setTitle("Kesalahan")
            .setMessage(message)
            .setPositiveButton("OK", null)
            .show()
    }
}