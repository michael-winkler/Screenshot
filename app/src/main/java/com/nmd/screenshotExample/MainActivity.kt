package com.nmd.screenshotExample

import android.graphics.Bitmap
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.nmd.screenshot.Screenshot
import com.nmd.screenshotExample.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar)

        initialize()
    }

    private fun initialize() {
        val screenshot = Screenshot(this)

        binding.sampleTakeScreenshot.setOnClickListener {
            screenshot.preview = binding.sampleOption1.isChecked
            screenshot.shutterSound = binding.sampleOption2.isChecked
            screenshot.takeScreenshot(object : Screenshot.OnResultListener {
                override fun result(success: Boolean, bitmap: Bitmap?) {
                    if (success) {
                        binding.sampleTakeScreenshotPreview.setImageBitmap(bitmap)
                        binding.sampleTakeScreenshotPreviewText.visibility = View.GONE

                        binding.sampleTakeScreenshotOpenLast.setOnClickListener {
                            screenshot.openLastScreenshot(showErrorToast = true)
                        }

                        binding.sampleTakeScreenshotPreview.setOnClickListener {
                            screenshot.showDialogPreview(bitmap = bitmap, cancelable = true)
                        }
                    } else {
                        binding.sampleTakeScreenshotPreview.setImageBitmap(null)
                        binding.sampleTakeScreenshotPreviewText.visibility = View.VISIBLE

                        binding.sampleTakeScreenshotOpenLast.setOnClickListener(null)
                        binding.sampleTakeScreenshotPreview.setOnClickListener(null)
                    }
                    binding.sampleTakeScreenshotOpenLast.isEnabled = success
                }
            })
        }

    }

}