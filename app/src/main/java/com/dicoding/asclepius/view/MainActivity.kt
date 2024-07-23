package com.dicoding.asclepius.view

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.dicoding.asclepius.databinding.ActivityMainBinding
import com.google.android.material.snackbar.Snackbar

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private var currentImageUri: Uri? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        installSplashScreen()

        
        // binding for the buttons
        binding.analyzeButton.setOnClickListener{ analyzeImage() }
        binding.galleryButton.setOnClickListener { startGallery() }
        // Set progress bar visibility to GONE
        binding.progressBar.visibility = android.view.View.GONE
        // Set progress bar visibility to VISIBLE
        binding.analyzeButton.setOnClickListener {
            binding.progressBar.visibility = android.view.View.VISIBLE
            analyzeImage()
        }

    }

    override fun onResume() {
        super.onResume()
        // Set progress bar visibility to GONE when activity resumes
        binding.progressBar.visibility = android.view.View.GONE
    }
//  start the gallery to pick an image
    private fun startGallery() {
        launcherGallery.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
    }
//  register the activity result to pick an image from the gallery
    private val launcherGallery = registerForActivityResult(
        ActivityResultContracts.PickVisualMedia()
    ) { uri: Uri? ->
        if (uri != null) {
            currentImageUri = uri
            showImage()
        } else {
            Log.d("Photo Picker", "No media selected")
        }
    }
//  show the image in the image view
    private fun showImage() {
        currentImageUri?.let {
            binding.previewImageView.setImageURI(it)
            Snackbar.make(binding.root, "Image Selected", Snackbar.LENGTH_LONG).show()
        }
    }
//  analyze the image
    private fun analyzeImage() {
//      check if the image uri is not null
        if (currentImageUri !== null) {
            val intent = Intent(this, ResultActivity::class.java)
            intent.putExtra(ResultActivity.EXTRA_IMAGE_URI, currentImageUri.toString())
            startActivity(intent)
        } else{
            binding.progressBar.visibility = android.view.View.GONE
            showToast("Please select an image first")
        }
    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

}