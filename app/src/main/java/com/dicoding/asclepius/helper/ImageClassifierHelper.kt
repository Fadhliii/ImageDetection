package com.dicoding.asclepius.helper

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.util.Log
import org.tensorflow.lite.support.image.ImageProcessor
import org.tensorflow.lite.support.image.TensorImage
import org.tensorflow.lite.support.image.ops.ResizeOp
import org.tensorflow.lite.task.vision.classifier.Classifications
import org.tensorflow.lite.task.vision.classifier.ImageClassifier

@Suppress("DEPRECATION")
//  create the ImageClassifierHelper class to classify the image
class ImageClassifierHelper(
    private val context: Context,
    private val classifierListener: ClassifierListener?,
    private val threshold: Float = 0.1f,
    private val maxResults: Int = 2,
    private val modelName: String = "cancer_classification.tflite",
)
{
//    create the image classifier variable to store the image classifier
    private var imageClassifier: ImageClassifier? = null
    private var inferenceTime: Long = 0

    init {
        setupImageClassifier()
    }
//    this function is used to setup the image classifier
    private fun setupImageClassifier() {
        val options = ImageClassifier.ImageClassifierOptions.builder()
            .setScoreThreshold(threshold)
            .setMaxResults(maxResults)
            .setNumThreads(4)
            .build()
//    create the image classifier from the model file and the options
        imageClassifier = ImageClassifier.createFromFileAndOptions(context, modelName, options)
    }

//    this function is used to classify the static image
    fun classifyStaticImage(imageUri: Uri) {
        val imageProcessor = ImageProcessor.Builder()
            .add(ResizeOp(224, 224, ResizeOp.ResizeMethod.NEAREST_NEIGHBOR))
            .build()
//    convert the image to tensor image and classify it using the imageclassifier
        val tensorImage = imageProcessor.process(TensorImage.fromBitmap(uriToBitmap(imageUri)))
//    get the inference time and the results of the classification and pass it to the classifierListener
        inferenceTime = System.currentTimeMillis()
//    classify the image and get the results and inference time and pass it to the classifierListener
        val results = imageClassifier?.classify(tensorImage)
        inferenceTime = System.currentTimeMillis() - inferenceTime
        classifierListener?.onResults(results, inferenceTime)
    }
//    this function is used to classify the bitmap image to convert it so the TFlite model can classify it
    private fun uriToBitmap(uri: Uri): Bitmap? {
//        context content resolver is used to open the input stream of the image uri and decode it to bitmap
        return context.contentResolver.openInputStream(uri)?.let { BitmapFactory.decodeStream(it) }
    }
//  this function is used to release the image classifier
interface ClassifierListener {
    fun onError(error: String)
    fun onResults(results: List<Classifications>?, inferenceTime: Long) {
        results?.let {
            val resultText = it.joinToString("\n") { classification: Classifications ->
                val confidenceLevel = classification.categories.first().score * 100
                val label = if (confidenceLevel < 90) "Non-cancer" else classification.categories.first().label
                "$label: $confidenceLevel%"
            }
            Log.d("Classification Result", resultText)
        }
    }
}
}