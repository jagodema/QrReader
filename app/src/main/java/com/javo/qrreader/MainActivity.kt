package com.javo.qrreader

import android.app.SearchManager
import android.content.Intent
import android.graphics.drawable.AnimationDrawable
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.google.zxing.integration.android.IntentIntegrator
import com.javo.qrreader.databinding.ActivityMainBinding


class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val animationDrawable: AnimationDrawable = binding.root.background as AnimationDrawable
        animationDrawable.setEnterFadeDuration(10)
        animationDrawable.setExitFadeDuration(5000)
        animationDrawable.start()
        binding.qrButton.setOnClickListener {
            val intentIntegrator = IntentIntegrator(this)
            intentIntegrator.setDesiredBarcodeFormats(listOf(IntentIntegrator.QR_CODE))
            intentIntegrator.initiateScan()
        }
        binding.generateqr.setOnClickListener {
            val intent = Intent(this@MainActivity, GenerateQR::class.java)
            startActivity(intent)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        val result = IntentIntegrator.parseActivityResult(resultCode, data)
        if (result != null) {
            if (resultCode != 0) {
                AlertDialog.Builder(this)
                    .setMessage("${getString(R.string.ira)} ${result.contents}?")
                    .setPositiveButton(R.string.si) { _, _ ->
                        val intent = Intent(Intent.ACTION_WEB_SEARCH)
                        intent.putExtra(SearchManager.QUERY, result.contents)
                        startActivity(intent)
                    }
                    .setNegativeButton("No") { _, _ -> }
                    .create()
                    .show()
            }
        }
    }

}