package com.javo.qrreader

import android.content.ContentValues
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.drawable.AnimationDrawable
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.view.View.VISIBLE
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.graphics.drawable.toBitmap
import com.google.zxing.BarcodeFormat
import com.google.zxing.MultiFormatWriter
import com.google.zxing.WriterException
import com.google.zxing.common.BitMatrix
import com.javo.qrreader.databinding.ActivityGenerateQrBinding
import java.io.File
import java.io.FileOutputStream
import java.io.OutputStream

class GenerateQR : AppCompatActivity() {
    private lateinit var binding: ActivityGenerateQrBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityGenerateQrBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val animationDrawable: AnimationDrawable = binding.root.background as AnimationDrawable
        animationDrawable.setEnterFadeDuration(10)
        animationDrawable.setExitFadeDuration(5000)
        animationDrawable.start()
        binding.generateqr.setOnClickListener {
            if (!binding.inputtext.text.isNullOrBlank()) {
                val bm = textToQRBitmap(binding.inputtext.text.toString())
                if (bm != null) {
                    binding.qrimage.setImageBitmap(bm)
                    binding.saveqr.visibility = VISIBLE


                }
            }

        }
        binding.saveqr.setOnClickListener {
            saveBitMap(binding.qrimage.drawable.toBitmap())
            Toast.makeText(applicationContext, "Saved", Toast.LENGTH_SHORT).show()
        }


    }

    @Throws(WriterException::class)
    fun textToQRBitmap(Value: String): Bitmap? {
        val bitMatrix: BitMatrix
        try {
            bitMatrix = MultiFormatWriter().encode(Value, BarcodeFormat.QR_CODE, 500, 500, null)
        } catch (e: IllegalArgumentException) {
            return null
        }

        val matrixWidth = bitMatrix.width
        val matrixHeight = bitMatrix.height
        val pixels = IntArray(matrixWidth * matrixHeight)

        for (y in 0 until matrixHeight) {
            val offset = y * matrixWidth
            for (x in 0 until matrixWidth) {
                pixels[offset + x] = if (bitMatrix.get(x, y)) Color.BLACK else Color.WHITE
            }
        }
        val bitmap = Bitmap.createBitmap(matrixWidth, matrixHeight, Bitmap.Config.RGB_565)
        bitmap.setPixels(pixels, 0, 500, 0, 0, matrixWidth, matrixHeight)
        return bitmap
    }

    private fun saveBitMap(bitmap: Bitmap) {
        val filename = "${System.currentTimeMillis()}.jpg"
        var fos: OutputStream? = null
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            this.contentResolver?.also { resolver ->
                val contentValues = ContentValues().apply {
                    put(MediaStore.MediaColumns.DISPLAY_NAME, filename)
                    put(MediaStore.MediaColumns.MIME_TYPE, "image/jpg")
                    put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_PICTURES)
                }

                val imageUri: Uri? =
                    resolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues)
                fos = imageUri?.let { resolver.openOutputStream(it) }
            }
        } else {
            val imagesDir =
                Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES)
            val image = File(imagesDir, filename)
            fos = FileOutputStream(image)
        }

        fos?.use {
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, it)
        }
    }
}