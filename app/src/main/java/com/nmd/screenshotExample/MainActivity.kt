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

        with(binding) {
            sampleTakeScreenshot.setOnClickListener {
                screenshot.preview = sampleOption1.isChecked
                screenshot.shutterSound = sampleOption2.isChecked
                screenshot.takeScreenshot(object : Screenshot.OnResultListener {
                    override fun result(success: Boolean, bitmap: Bitmap?) {
                        if (success) {
                            sampleTakeScreenshotPreview.setImageBitmap(bitmap)
                            sampleTakeScreenshotPreviewText.visibility = View.GONE

                            sampleTakeScreenshotOpenLast.setOnClickListener {
                                screenshot.openLastScreenshot(showErrorToast = true)
                            }

                            sampleTakeScreenshotPreview.setOnClickListener {
                                screenshot.showDialogPreview(bitmap = bitmap, cancelable = true)
                            }
                        } else {
                            sampleTakeScreenshotPreview.setImageBitmap(null)
                            sampleTakeScreenshotPreviewText.visibility = View.VISIBLE

                            sampleTakeScreenshotOpenLast.setOnClickListener(null)
                            sampleTakeScreenshotPreview.setOnClickListener(null)
                        }
                        sampleTakeScreenshotOpenLast.isEnabled = success
                    }
                })
            }
        }
    }

}