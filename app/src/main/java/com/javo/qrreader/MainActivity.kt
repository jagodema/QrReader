package com.javo.qrreader

import android.app.SearchManager
import android.content.Intent
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
        binding.qrButton.setOnClickListener {
            val intentIntegrator = IntentIntegrator(this)
            intentIntegrator.setDesiredBarcodeFormats(listOf(IntentIntegrator.QR_CODE))
            intentIntegrator.initiateScan()
        }
    }
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        val result = IntentIntegrator.parseActivityResult(resultCode, data)
        if (result != null) {
            if (resultCode!=0) {
                AlertDialog.Builder(this)
                    .setMessage("Would you like to go to ${result.contents}?")
                    .setPositiveButton("Yes") { dialogInterface, i ->
                        val intent = Intent(Intent.ACTION_WEB_SEARCH)
                        intent.putExtra(SearchManager.QUERY, result.contents)
                        startActivity(intent)
                    }
                    .setNegativeButton("No", { dialogInterface, i -> })
                    .create()
                    .show()
            }
        }
    }
}