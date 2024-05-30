package com.nmd.screenshotExample

import android.graphics.Bitmap
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.nmd.screenshot.Screenshot
import com.nmd.screenshotExample.SharedPrefsHelper.Companion.SETTINGS_1
import com.nmd.screenshotExample.SharedPrefsHelper.Companion.SETTINGS_2
import com.nmd.screenshotExample.SharedPrefsHelper.Companion.SETTINGS_3
import com.nmd.screenshotExample.SharedPrefsHelper.Companion.get
import com.nmd.screenshotExample.SharedPrefsHelper.Companion.initSharedPreferences
import com.nmd.screenshotExample.SharedPrefsHelper.Companion.save
import com.nmd.screenshotExample.databinding.ActivityMainBinding
import com.nmd.screenshotExample.databinding.BottomSheetDialogSettingsBinding

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
        initSharedPreferences()
        val screenshot = Screenshot(this)

        with(binding) {
            toolbarSettings.setOnClickListener {
                val dialog = BottomSheetDialog(this@MainActivity, R.style.BottomSheetDialog)
                val dialogBinding = BottomSheetDialogSettingsBinding.inflate(layoutInflater)

                // Reload the saved options
                dialogBinding.sampleOption1.isChecked = SETTINGS_1.get(defaultValue = true)
                dialogBinding.sampleOption2.isChecked = SETTINGS_2.get(defaultValue = false)
                dialogBinding.sampleOption3.isChecked = SETTINGS_3.get(defaultValue = true)

                dialog.setContentView(dialogBinding.root)
                dialog.setOnDismissListener {
                    // Save now the options
                    SETTINGS_1.save(dialogBinding.sampleOption1.isChecked)
                    SETTINGS_2.save(dialogBinding.sampleOption2.isChecked)
                    SETTINGS_3.save(dialogBinding.sampleOption3.isChecked)
                }
                dialog.show()
            }

            sampleTakeScreenshot.setOnClickListener {
                screenshot.preview = SETTINGS_1.get(defaultValue = true)
                screenshot.shutterSound = SETTINGS_2.get(defaultValue = false)
                screenshot.saveScreenshot = SETTINGS_3.get(defaultValue = true)
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
                        sampleTakeScreenshotOpenLast.isEnabled =
                            success && SETTINGS_3.get(defaultValue = true)
                    }
                })
            }
        }

    }

}