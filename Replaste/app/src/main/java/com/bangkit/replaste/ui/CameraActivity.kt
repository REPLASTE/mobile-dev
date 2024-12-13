package com.bangkit.replaste.ui

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.airbnb.lottie.LottieAnimationView
import com.bangkit.replaste.R
import com.bangkit.replaste.api.ApiClient
import com.bangkit.replaste.api.auth.PredictResponse
import com.bangkit.replaste.databinding.ActivityCameraBinding
import com.bangkit.replaste.helper.SecurePreferences
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File
import java.text.SimpleDateFormat
import java.util.Locale
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

class CameraActivity : AppCompatActivity() {

    private lateinit var classIndices: Map<String, Int>
    private lateinit var binding: ActivityCameraBinding
    private var imageCapture: ImageCapture? = null
    private lateinit var cameraExecutor: ExecutorService
    private lateinit var outputDirectory: File
    private var loadingDialog: AlertDialog? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCameraBinding.inflate(layoutInflater)
        setContentView(binding.root)


        loadClassLabels()

        outputDirectory = getOutputDirectory()

        if (allPermissionsGranted()) {
            startCamera()
        } else {
            ActivityCompat.requestPermissions(
                this, REQUIRED_PERMISSIONS, REQUEST_CODE_PERMISSIONS
            )
        }

        binding.imageCaptureButton.setOnClickListener { takePhoto() }
        cameraExecutor = Executors.newSingleThreadExecutor()
    }

    private fun getOutputDirectory(): File {
        val mediaDir = externalMediaDirs.firstOrNull()?.let {
            File(it, resources.getString(R.string.app_name)).apply { mkdirs() }
        }
        return if (mediaDir != null && mediaDir.exists()) mediaDir else filesDir
    }

    private fun takePhoto() {
        val imageCapture = imageCapture ?: return

        val photoFile = File(
            outputDirectory,
            SimpleDateFormat(FILENAME_FORMAT, Locale.US).format(System.currentTimeMillis()) + ".jpg"
        )

        val outputOptions = ImageCapture.OutputFileOptions.Builder(photoFile).build()

        imageCapture.takePicture(
            outputOptions,
            ContextCompat.getMainExecutor(this),
            object : ImageCapture.OnImageSavedCallback {
                override fun onImageSaved(output: ImageCapture.OutputFileResults) {
                    if (!isImageSizeValid(photoFile)) {
                        photoFile.delete()
                        Toast.makeText(
                            baseContext,
                            "Ukuran gambar melebihi 5 MB",
                            Toast.LENGTH_SHORT
                        ).show()
                        return
                    }

                    uploadImage(photoFile)
                }

                override fun onError(exc: ImageCaptureException) {
                    Log.e(TAG, "Photo capture failed: ${exc.message}", exc)
                }
            }
        )
    }

    private fun uploadImage(photoFile: File) {
        if (!photoFile.exists() || !photoFile.canRead()) {
            Toast.makeText(this, "File tidak ditemukan atau tidak dapat dibaca", Toast.LENGTH_SHORT)
                .show()
            return
        }

        showLoadingDialog()


        val requestFile = photoFile.asRequestBody("image/jpeg".toMediaTypeOrNull())
        val body = MultipartBody.Part.createFormData("file", photoFile.name, requestFile)
        val securePreferences = SecurePreferences(this)
        val userIdBody = securePreferences.getJwtToken().toString()
            .toRequestBody("text/plain".toMediaTypeOrNull())

        ApiClient.plasticService.predictPlastic(userIdBody, body)
            .enqueue(object : Callback<PredictResponse> {
                override fun onResponse(
                    call: Call<PredictResponse>,
                    response: Response<PredictResponse>
                ) {
                    dismissLoadingDialog()
                    if (response.isSuccessful) {
                        response.body()?.let { predictResponse ->
                            val intent =
                                Intent(this@CameraActivity, PredictActivity::class.java).apply {
                                    putExtra("PREDICT_RESPONSE", Gson().toJson(predictResponse))
                                }
                            startActivity(intent)
                        } ?: run {
                            showErrorDialog("Tidak ada data prediksi")
                        }
                    } else {
                        handleErrorResponse(response)
                    }
                }

                override fun onFailure(call: Call<PredictResponse>, t: Throwable) {
                    dismissLoadingDialog()
                    handleNetworkError(t)
                }
            })
    }

    private fun handleErrorResponse(response: Response<PredictResponse>) {
        val errorBody = response.errorBody()?.string()
        val errorMessage = try {
            val errorJson = Gson().fromJson(errorBody, Map::class.java)
            errorJson["message"]?.toString() ?: "Upload gagal"
        } catch (e: Exception) {
            errorBody ?: "Upload gagal"
        }
        Log.e(TAG, "Upload Error - HTTP ${response.code()}: $errorMessage")
        showErrorDialog(errorMessage)
    }

    private fun handleNetworkError(t: Throwable) {
        Log.e(TAG, "Network Error: ${t.message}", t)
        val errorMessage = when (t) {
            is java.net.UnknownHostException -> "Tidak ada koneksi internet"
            is java.net.ConnectException -> "Gagal terhubung ke server"
            is retrofit2.HttpException -> "Kesalahan server: ${t.code()}"
            else -> "Gagal mengirim data: ${t.message}"
        }
        showErrorDialog(errorMessage)
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


    private fun dismissLoadingDialog() {
        loadingDialog?.dismiss()
    }


    private fun showErrorDialog(message: String) {
        MaterialAlertDialogBuilder(this)
            .setTitle("Kesalahan")
            .setMessage(message)
            .setPositiveButton("OK", null)
            .show()
    }

    private fun isImageSizeValid(file: File): Boolean {
        val maxSizeBytes = 5 * 1024 * 1024
        return file.length() <= maxSizeBytes
    }

    private fun startCamera() {
        val cameraProviderFuture = ProcessCameraProvider.getInstance(this)

        cameraProviderFuture.addListener({
            val cameraProvider: ProcessCameraProvider = cameraProviderFuture.get()

            val preview = Preview.Builder()
                .build()
                .also {
                    it.setSurfaceProvider(binding.viewFinder.surfaceProvider)
                }

            imageCapture = ImageCapture.Builder().build()

            val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA

            try {
                cameraProvider.unbindAll()
                cameraProvider.bindToLifecycle(
                    this, cameraSelector, preview, imageCapture
                )
            } catch (exc: Exception) {
                Log.e(TAG, "Use case binding failed", exc)
            }
        }, ContextCompat.getMainExecutor(this))
    }

    private fun allPermissionsGranted() = REQUIRED_PERMISSIONS.all {
        ContextCompat.checkSelfPermission(baseContext, it) == PackageManager.PERMISSION_GRANTED
    }


    private fun loadClassLabels() {
        val json = assets.open("class_labels.json").bufferedReader().use { it.readText() }
        classIndices = Gson().fromJson(json, object : TypeToken<Map<String, Int>>() {}.type)
    }

    override fun onDestroy() {
        super.onDestroy()
        cameraExecutor.shutdown()
    }

    companion object {
        private const val TAG = "CameraXApp"
        private const val FILENAME_FORMAT = "yyyy-MM-dd-HH-mm-ss-SSS"
        private const val REQUEST_CODE_PERMISSIONS = 10
        private val REQUIRED_PERMISSIONS = arrayOf(Manifest.permission.CAMERA)
    }
}