package com.example.eurocounter

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import org.tensorflow.lite.task.vision.detector.ObjectDetector
import org.tensorflow.lite.support.image.TensorImage
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.zip.ZipEntry
import java.util.zip.ZipOutputStream

class MainActivity : AppCompatActivity() {
    private val TAG = "MainActivity"
    private lateinit var lastPhotoFile: File
    private lateinit var imageView: ImageView
    private lateinit var resultText: TextView

    private val labelToValue = mapOf(
        "EUR_5" to 5,
        "EUR_10" to 10,
        "EUR_20" to 20,
        "EUR_50" to 50,
        "EUR_100" to 100,
        "EUR_200" to 200,
        "EUR_500" to 500
    )

    private var detector: ObjectDetector? = null

    private val takePhoto = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val bitmap = MediaStore.Images.Media.getBitmap(contentResolver, Uri.fromFile(lastPhotoFile))
            imageView.setImageBitmap(bitmap)
            runInferenceOnBitmap(bitmap)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        imageView = findViewById(R.id.imageView)
        resultText = findViewById(R.id.resultText)

        val btnCamera: Button = findViewById(R.id.btnCamera)
        val btnExport: Button = findViewById(R.id.btnExport)

        btnCamera.setOnClickListener {
            if (checkAndRequestPermissions()) {
                dispatchTakePictureIntent()
            }
        }

        btnExport.setOnClickListener {
            val zipFile = exportDatasetZip()
            if (zipFile != null) {
                shareZip(zipFile)
            }
        }

        try {
            val options = ObjectDetector.ObjectDetectorOptions.builder()
                .setMaxResults(50)
                .setScoreThreshold(0.4f)
                .build()
            detector = ObjectDetector.createFromFileAndOptions(this, "model.tflite", options)
            Log.i(TAG, "Modelo cargado desde assets/model.tflite")
        } catch (e: Exception) {
            Log.w(TAG, "No se pudo cargar model.tflite desde assets: ${e.message}")
            detector = null
        }
    }

    private fun checkAndRequestPermissions(): Boolean {
        val needed = mutableListOf<String>()
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            needed.add(Manifest.permission.CAMERA)
        }
        if (needed.isNotEmpty()) {
            ActivityCompat.requestPermissions(this, needed.toTypedArray(), 100)
            return false
        }
        return true
    }

    private fun dispatchTakePictureIntent() {
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        val photoFile = createImageFile()
        lastPhotoFile = photoFile
        val uri = FileProvider.getUriForFile(this, "${packageName}.fileprovider", photoFile)
        intent.putExtra(MediaStore.EXTRA_OUTPUT, uri)
        takePhoto.launch(intent)
    }

    private fun createImageFile(): File {
        val timestamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US).format(Date())
        val storageDir = File(getExternalFilesDir(Environment.DIRECTORY_PICTURES), "euro_dataset")
        if (!storageDir.exists()) storageDir.mkdirs()
        return File(storageDir, "euro_${timestamp}.jpg")
    }

    private fun exportDatasetZip(): File? {
        try {
            val storageDir = File(getExternalFilesDir(Environment.DIRECTORY_PICTURES), "euro_dataset")
            if (!storageDir.exists()) return null

            val zipFile = File(getExternalFilesDir(null), "euro_dataset_${System.currentTimeMillis()}.zip")
            val fos = FileOutputStream(zipFile)
            val zos = ZipOutputStream(fos)

            storageDir.listFiles()?.forEach { file ->
                val entry = ZipEntry(file.name)
                zos.putNextEntry(entry)
                file.inputStream().copyTo(zos)
                zos.closeEntry()
            }
            zos.close()
            return zipFile
        } catch (e: Exception) {
            Log.e(TAG, "Error al crear ZIP del dataset", e)
            return null
        }
    }

    private fun shareZip(zipFile: File) {
        val uri = FileProvider.getUriForFile(this, "${packageName}.fileprovider", zipFile)
        val share = Intent(Intent.ACTION_SEND).apply {
            type = "application/zip"
            putExtra(Intent.EXTRA_STREAM, uri)
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }
        startActivity(Intent.createChooser(share, "Exportar dataset"))
    }

    private fun runInferenceOnBitmap(bitmap: Bitmap) {
        if (detector == null) {
            resultText.text = "Modelo no cargado. Copia model.tflite en assets/ y reinstala la app."
            return
        }

        try {
            val tensorImage = TensorImage.fromBitmap(bitmap)
            val results = detector!!.detect(tensorImage)
            val counts = mutableMapOf<String, Int>()
            var total = 0

            for (result in results) {
                val category = result.categories.firstOrNull() ?: continue
                val label = category.label
                val value = labelToValue[label] ?: labelToValue[label.replace("EUR_", "EUR_")] ?: 0

                if (value > 0) {
                    counts[label] = counts.getOrDefault(label, 0) + 1
                    total += value
                }
            }

            val sb = StringBuilder()
            sb.append("Conteo por denominación:\n")
            if (counts.isEmpty()) {
                sb.append("No se detectó ninguna denominación fiable.\n")
            } else {
                for ((key, count) in counts) {
                    sb.append("${key} : ${count}\n")
                }
            }
            sb.append("\nSuma total: €${total}")
            resultText.text = sb.toString()
        } catch (e: Exception) {
            Log.e(TAG, "Error durante inferencia", e)
            resultText.text = "Error de inferencia: ${e.message}"
        }
    }
}
