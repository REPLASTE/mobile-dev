package com.bangkit.replaste.ui

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.content.Intent
import android.os.Bundle
import android.util.Patterns
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.airbnb.lottie.LottieAnimationView
import com.bangkit.replaste.R
import com.bangkit.replaste.api.ApiClient
import com.bangkit.replaste.api.auth.MessageResponse
import com.bangkit.replaste.api.auth.ResetPasswordRequest
import com.bangkit.replaste.databinding.ActivityResetPasswordBinding
import com.bangkit.replaste.utils.NetworkUtils
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ResetPasswordActivity : AppCompatActivity() {

    private lateinit var binding: ActivityResetPasswordBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityResetPasswordBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupUI()
    }

    private fun setupUI() {

        binding.resetButton.setOnClickListener {
            val email = binding.emailEditText.text.toString().trim()

            
            if (!NetworkUtils.isInternetAvailable(this)) {
                showNoInternetDialog()
                return@setOnClickListener
            }

            if (isValidEmail(email)) {
                showLoading(true)
                performPasswordReset(email)
            } else {
                binding.emailEditText.error = "Invalid email format"
                showLoading(false)
            }
        }
    }

    private fun performPasswordReset(email: String) {
        val request = ResetPasswordRequest(email = email)
        ApiClient.authService.requestPasswordReset(request)
            .enqueue(object : Callback<MessageResponse> {
                override fun onResponse(
                    call: Call<MessageResponse>,
                    response: Response<MessageResponse>
                ) {
                    showLoading(false)

                    if (response.isSuccessful) {
                        showSuccessAnimation()
                    } else {
                        val errorMessage = response.body()?.message
                            ?: "Failed to send reset link. Please try again."
                        showErrorDialog(errorMessage)
                    }
                }

                override fun onFailure(call: Call<MessageResponse>, t: Throwable) {
                    showLoading(false)
                    showErrorDialog("Network error. Please check your connection.")
                }
            })
    }

    private fun showSuccessAnimation() {
        
        binding.emailInputLayout.visibility = View.GONE
        binding.emailEditText.visibility = View.GONE
        binding.resetButton.visibility = View.GONE

        
        binding.successContainer.visibility = View.VISIBLE

        
        val successAnimationView: LottieAnimationView = binding.successAnimationView
        successAnimationView.setAnimation(R.raw.success_checkmark)
        successAnimationView.playAnimation()

        
        successAnimationView.addAnimatorListener(object : AnimatorListenerAdapter() {
            override fun onAnimationEnd(animation: Animator) {
                CoroutineScope(Dispatchers.Main).launch {
                    delay(1500)
                    redirectToLogin()
                }
            }
        })
    }

    private fun redirectToLogin() {
        val intent = Intent(this, LoginActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
        }
        startActivity(intent)
        finish()
    }

    private fun showLoading(isLoading: Boolean) {
        binding.loadingIndicator.visibility = if (isLoading) View.VISIBLE else View.GONE
        binding.loadingText.visibility = if (isLoading) View.VISIBLE else View.GONE
        binding.resetButton.isEnabled = !isLoading
    }

    private fun isValidEmail(email: String): Boolean {
        return email.isNotEmpty() && Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }

    private fun showNoInternetDialog() {
        
        Toast.makeText(
            this,
            "No internet connection. Please check your network.",
            Toast.LENGTH_SHORT
        ).show()
    }

    private fun showErrorDialog(message: String) {
        
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }
}