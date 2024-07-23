package com.dicoding.asclepius.view

import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.dicoding.asclepius.databinding.ActivityResultBinding
import com.dicoding.asclepius.helper.ImageClassifierHelper
import com.google.android.material.snackbar.Snackbar
import org.tensorflow.lite.task.vision.classifier.Classifications

class ResultActivity : AppCompatActivity() {
    private lateinit var binding: ActivityResultBinding
    //    Initialize ImageClassifierHelper class in ResultActivity class to classify the image and get the results
    private lateinit var imageClassifierHelper: ImageClassifierHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityResultBinding.inflate(layoutInflater)
        setContentView(binding.root)
        // get image uri from intent extra
        // show the image in the resultImage ImageView
        val imageUri = Uri.parse(intent.getStringExtra(EXTRA_IMAGE_URI))
        imageUri?.let {
            Log.d("Image URI", "showImage: $it")
            binding.resultImage.setImageURI(it)
        }

        // Initialize the ImageClassifierHelper
        imageClassifierHelper =
            ImageClassifierHelper(this, object : ImageClassifierHelper.ClassifierListener {
               override fun onError(error: String) {
                    binding.resultText.text = error
                   Snackbar.make(binding.root, error, Snackbar.LENGTH_LONG).show()

                }
                //  display the result in the resultText TextView
                override fun onResults(results: List<Classifications>?, inferenceTime: Long) {
                    if (results?.isNotEmpty() == true && results[0].categories.isNotEmpty()) {
                        val maxScoreCategory = results[0].categories.maxByOrNull { it.score }
                        maxScoreCategory?.let {
                            val resultText = "${it.label}: ${it.score * 100}%"
                            binding.resultText.text = resultText
                        }
                    } else {
                        binding.resultText.text = ""
                    }
                }
            })
        // Classify the image and get the results using the ImageClassifierHelper class
        imageClassifierHelper.classifyStaticImage(imageUri)
    }
    
//  companion object to pass the image uri and the result to the ResultActivity
    companion object {
        const val EXTRA_IMAGE_URI = "extra_image_uri"
        const val EXTRA_RESULT = "extra_result"
    }
}