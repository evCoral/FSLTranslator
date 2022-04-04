package com.example.fsltranslator

import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageCapture
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.LifecycleOwner
import com.example.fsltranslator.databinding.ActivityTranslateBinding
import com.google.common.util.concurrent.ListenableFuture
import com.google.mlkit.common.model.LocalModel
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.objects.ObjectDetection
import com.google.mlkit.vision.objects.custom.CustomObjectDetectorOptions
import com.google.mlkit.vision.text.TextRecognition
import com.google.mlkit.vision.text.latin.TextRecognizerOptions
import java.util.concurrent.Executors


class TranslateActivity : AppCompatActivity() {
    private lateinit var binding: ActivityTranslateBinding;
    private var imageCapture: ImageCapture? = null
    private lateinit var cameraProviderFuture: ListenableFuture<ProcessCameraProvider>


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTranslateBinding.inflate(layoutInflater)
        setContentView(binding.root)


        if (allPermissionGranted()) {
            cameraProviderFuture = ProcessCameraProvider.getInstance(this)
            cameraProviderFuture.addListener(Runnable {
                val cameraProvider = cameraProviderFuture.get()
                bindPreview(cameraProvider)
            }, ContextCompat.getMainExecutor(this))
        } else {
            ActivityCompat.requestPermissions(
                this,
                Constants.REQUIRED_PERMISSION,
                Constants.REQUEST_CODE_PERMISSIONS
            )
        }
    }

    private fun bindPreview(cameraProvider: ProcessCameraProvider) {
        val preview: Preview = Preview.Builder()
            .build()

        val cameraSelector: CameraSelector = CameraSelector.Builder()
            .requireLensFacing(CameraSelector.LENS_FACING_BACK)
            .build()
        preview.setSurfaceProvider(binding.viewFinder.getSurfaceProvider())

        var prevPredict = ""

        val imageAnalysis = ImageAnalysis.Builder()
            .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
            .build()
        imageCapture = ImageCapture.Builder()
            .build()
        imageAnalysis.setAnalyzer(
            Executors.newSingleThreadExecutor(),
            ImageAnalysis.Analyzer { image ->
                val rotationDegrees = image.imageInfo.rotationDegrees
                // Initialize Text Recognition
                val recognizer = TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS)

                // Initialize Object Detection
                val localModel = LocalModel.Builder()
                    .setAssetFilePath("model21.tflite")
                    .build()
                val customObjectDetectorOptions =
                    CustomObjectDetectorOptions.Builder(localModel)
                        .setDetectorMode(CustomObjectDetectorOptions.SINGLE_IMAGE_MODE)
                        .enableMultipleObjects()
                        .enableClassification()
                        .setClassificationConfidenceThreshold(0.5f)
                        .setMaxPerObjectLabelCount(3)
                        .build()
                val objectDetector = ObjectDetection.getClient(customObjectDetectorOptions)


                // Image Processing
                @androidx.camera.core.ExperimentalGetImage
                val mediaImage = image.image
                @androidx.camera.core.ExperimentalGetImage
                if (mediaImage != null) {
                    val image1 = InputImage.fromMediaImage(mediaImage, rotationDegrees)

                    // Object Processing
                    objectDetector.process(image1)
                        .addOnSuccessListener { detectedObjects ->
                            for (detectedObject in detectedObjects) {
                                val boundingBox = detectedObject.boundingBox
                                val trackingId = detectedObject.trackingId
                                for (label in detectedObject.labels) {
                                    binding.textView10.text = label.text
                                    val text = label.text
                                    val confidence = label.confidence
                                    binding.textView10.text = "Gesture: " + label.text + "\n" + " " +"Accuracy: "+ label.confidence * 100 + "%"
                                }
                            }
                            image.close()
                        }
                        .addOnFailureListener { e ->
                            binding.textView10.text =
                                "Error"
                            image.close()
                        }
                } else {
                    image.close()
                }
            })
        var camera = cameraProvider.bindToLifecycle(
            this as LifecycleOwner, cameraSelector,
            imageCapture,
            imageAnalysis,
            preview
        )
    }

    private fun String.onlyLetters() = all { it.isLetter() }
    private fun String.onlyDigits() = all { it.isDigit() }
    private fun isPlateNumber(lineText: String): Boolean {
        if (lineText.length == 6) {
            return (lineText.take(3).onlyLetters() && lineText.takeLast(3).onlyDigits())
        } else if (lineText.length == 7) {
            return (lineText.take(3).onlyLetters() && lineText.takeLast(4).onlyDigits())
        }
        return false
    }


    @SuppressLint("MissingSuperCall")
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray,
    ) {

        if (requestCode == Constants.REQUEST_CODE_PERMISSIONS) {
            if (allPermissionGranted()) {
                cameraProviderFuture = ProcessCameraProvider.getInstance(this)
                cameraProviderFuture.addListener(Runnable {
                    val cameraProvider = cameraProviderFuture.get()
                    bindPreview(cameraProvider)
                }, ContextCompat.getMainExecutor(this))
            } else {
                Toast.makeText(this, "hello", Toast.LENGTH_LONG).show()
                finish()
            }
        }
    }

    private fun allPermissionGranted() =
        Constants.REQUIRED_PERMISSION.all {
            ContextCompat.checkSelfPermission(baseContext, it) == PackageManager.PERMISSION_GRANTED
        }

}