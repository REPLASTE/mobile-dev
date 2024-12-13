package com.bangkit.replaste.ui

import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Patterns
import android.view.View
import android.view.animation.AlphaAnimation
import android.view.animation.Animation
import android.view.animation.DecelerateInterpolator
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.airbnb.lottie.LottieAnimationView
import com.auth0.android.jwt.JWT
import com.bangkit.replaste.R
import com.bangkit.replaste.api.ApiClient
import com.bangkit.replaste.api.auth.AuthResponse
import com.bangkit.replaste.api.auth.LoginRequest
import com.bangkit.replaste.helper.SecurePreferences
import com.google.android.material.button.MaterialButton
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.textfield.TextInputEditText
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class LoginActivity : AppCompatActivity() {

    private lateinit var emailEditText: TextInputEditText
    private lateinit var passwordEditText: TextInputEditText
    private lateinit var loginButton: MaterialButton
    private lateinit var registerTextView: TextView
    private lateinit var loginTitle: TextView
    private var loadingDialog: AlertDialog? = null
    private lateinit var forgotPasswordTextView: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        emailEditText = findViewById(R.id.emailEditText)
        passwordEditText = findViewById(R.id.passwordEditText)
        loginButton = findViewById(R.id.loginButton)
        registerTextView = findViewById(R.id.registerTextView)
        forgotPasswordTextView = findViewById(R.id.forgotPasswordTextView)
        loginTitle = findViewById(R.id.loginTitle)

        loginButton.setOnClickListener {
            if (validateInputs()) {
                performLogin()
            }
        }

        registerTextView.setOnClickListener {
            val intent = Intent(this, RegisterActivity::class.java)
            startActivity(intent)
        }

        forgotPasswordTextView.setOnClickListener {
            startActivity(Intent(this@LoginActivity,ResetPasswordActivity::class.java))
        }

        animateFadeIn(loginTitle, 1000, 0)
        animateFadeIn(loginButton, 1000, 500)
        animateFadeIn(registerTextView, 1000, 1000)
    }

    private fun animateFadeIn(view: View, duration: Int, startOffset: Int) {
        val fadeIn: Animation = AlphaAnimation(0f, 1f)
        fadeIn.interpolator = DecelerateInterpolator()
        fadeIn.duration = duration.toLong()
        fadeIn.startOffset = startOffset.toLong()
        view.startAnimation(fadeIn)
        view.visibility = View.VISIBLE
    }

    private fun showLoadingDialog() {
        val view = layoutInflater.inflate(R.layout.dialog_loading, null)

        val animationView = view.findViewById<LottieAnimationView>(R.id.animationView)
        animationView.setAnimation(R.raw.loading_animation)
        animationView.playAnimation()

        
        loadingDialog = AlertDialog.Builder(this)
            .setView(view)
            .setCancelable(false)
            .create()

        loadingDialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        loadingDialog?.show()
    }
    private fun dismissLoadingDialog() {
        loadingDialog?.dismiss()
    }


    private fun validateInputs(): Boolean {
        val email = emailEditText.text.toString().trim()
        val password = passwordEditText.text.toString()

        if (email.isEmpty()) {
            emailEditText.error = "Email is required"
            return false
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            emailEditText.error = "Please enter a valid email"
            return false
        }

        if (password.isEmpty()) {
            passwordEditText.error = "Password is required"
            return false
        }

        return true
    }
    private fun performLogin() {
        val email = emailEditText.text.toString().trim()
        val password = passwordEditText.text.toString()

        loginUser(email, password)
    }

    private fun loginUser(email: String, password: String) {
        
        showLoadingDialog()

        val loginRequest = LoginRequest(email, password)

        ApiClient.authService.login(loginRequest).enqueue(object : Callback<AuthResponse> {
            override fun onResponse(call: Call<AuthResponse>, response: Response<AuthResponse>) {
                
                dismissLoadingDialog()

                if (response.isSuccessful) {
                    val token = response.body()?.token
                    if (token != null) {
                        try {
                            val jwt = JWT(token)
                            val userId = jwt.getClaim("userId").asString()

                            val securePreferences = SecurePreferences(this@LoginActivity)
                            securePreferences.saveJwtToken(userId!!)

                            
                            Snackbar.make(
                                findViewById(R.id.main),
                                "Login berhasil!",
                                Snackbar.LENGTH_SHORT
                            ).setBackgroundTint(
                                ContextCompat.getColor(this@LoginActivity, R.color.primary)
                            ).show()

                            
                            Handler(Looper.getMainLooper()).postDelayed({
                                val intent = Intent(this@LoginActivity, MainActivity::class.java)
                                startActivity(intent)
                                finish()
                            }, 500)
                        } catch (e: Exception) {
                            showErrorDialog("Terjadi kesalahan", e.message ?: "Error tidak diketahui")
                        }
                    } else {
                        showErrorDialog("Login Gagal", "Token tidak ditemukan")
                    }
                } else {
                    
                    val errorBody = response.errorBody()?.string()
                    val errorMessage = when {
                        response.code() == 401 -> "Email atau password salah"
                        response.code() == 404 -> "Akun tidak ditemukan"
                        else -> "Gagal login: ${response.message()}"
                    }

                    showErrorDialog("Login Gagal", errorMessage)
                }
            }

            override fun onFailure(call: Call<AuthResponse>, t: Throwable) {
                
                dismissLoadingDialog()

                showErrorDialog(
                    "Koneksi Bermasalah",
                    "Periksa koneksi internet Anda"
                )
            }
        })
    }

    
    private fun showErrorDialog(title: String, message: String) {
        MaterialAlertDialogBuilder(this)
            .setTitle(title)
            .setMessage(message)
            .setPositiveButton("OK") { dialog, _ ->
                dialog.dismiss()
            }
            .show()
    }
}