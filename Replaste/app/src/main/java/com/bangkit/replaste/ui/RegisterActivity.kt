package com.bangkit.replaste.ui

import android.os.Bundle
import android.util.Patterns
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.bangkit.replaste.R
import com.bangkit.replaste.api.ApiClient
import com.bangkit.replaste.api.auth.AuthResponse
import com.bangkit.replaste.api.auth.RegisterRequest
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class RegisterActivity : AppCompatActivity() {

    private lateinit var nameEditText: TextInputEditText
    private lateinit var emailEditText: TextInputEditText
    private lateinit var passwordEditText: TextInputEditText
    private lateinit var confirmPasswordEditText: TextInputEditText
    private lateinit var registerButton: MaterialButton
    private lateinit var loginTextView: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        
        nameEditText = findViewById(R.id.nameEditText)
        emailEditText = findViewById(R.id.emailEditText)
        passwordEditText = findViewById(R.id.passwordEditText)
        confirmPasswordEditText = findViewById(R.id.confirmPasswordEditText)
        registerButton = findViewById(R.id.registerButton)
        loginTextView = findViewById(R.id.loginTextView)

        
        registerButton.setOnClickListener {
            if (validateInputs()) {
                val email = emailEditText.text.toString().trim()
                val password = passwordEditText.text.toString()
                val name = nameEditText.text.toString().trim()
                registerUser(email, password, name)
            }
        }

        loginTextView.setOnClickListener {
            
            finish()
        }


    }
    private fun validateInputs(): Boolean {
        val name = nameEditText.text.toString().trim()
        val email = emailEditText.text.toString().trim()
        val password = passwordEditText.text.toString()
        val confirmPassword = confirmPasswordEditText.text.toString()

        if (name.isEmpty()) {
            nameEditText.error = "Name is required"
            return false
        }

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

        if (password.length < 6) {
            passwordEditText.error = "Password must be at least 6 characters"
            return false
        }

        if (password != confirmPassword) {
            confirmPasswordEditText.error = "Passwords do not match"
            return false
        }

        return true
    }

    private fun registerUser(email: String, password: String, fullName: String) {
        val registerRequest = RegisterRequest(email, password, fullName)

        ApiClient.authService.register(registerRequest).enqueue(object : Callback<AuthResponse> {
            override fun onResponse(call: Call<AuthResponse>, response: Response<AuthResponse>) {
                if (response.isSuccessful) {
                    Toast.makeText(this@RegisterActivity,
                        "Registrasi berhasil!",
                        Toast.LENGTH_SHORT).show()
                    finish() 
                } else {
                    Toast.makeText(this@RegisterActivity,
                        "Registrasi gagal: ${response.message()}",
                        Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<AuthResponse>, t: Throwable) {
                Toast.makeText(this@RegisterActivity,
                    "Error: ${t.message}",
                    Toast.LENGTH_SHORT).show()
            }
        })
    }
}