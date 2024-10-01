package com.nmd.screenshotExample

import android.graphics.Bitmap
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.isVisible
import androidx.core.view.updatePadding
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
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.activityMainMaterialToolbar)

        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { view, windowInsets ->
            val insets =
                windowInsets.getInsets(WindowInsetsCompat.Type.systemBars() + WindowInsetsCompat.Type.displayCutout())

            binding.activityMainAppBarLayout.updatePadding(
                left = insets.left,
                right = insets.right,
                top = insets.top
            )

            binding.activityMainNestedScrollView.updatePadding(
                left = insets.left,
                right = insets.right,
                bottom = insets.bottom
            )

            ViewCompat.onApplyWindowInsets(view, windowInsets)
        }

        initialize()
    }

    private fun initialize() {
        initSharedPreferences()
        val screenshot = Screenshot(this)

        with(binding) {
            activityMainSettings.setOnClickListener {
                val dialog = BottomSheetDialog(this@MainActivity, R.style.BottomSheetDialog)
                val dialogBinding = BottomSheetDialogSettingsBinding.inflate(layoutInflater)

                with(dialogBinding) {
                    // Reload the saved options
                    sampleOption1.isChecked = SETTINGS_1.get(defaultValue = true)
                    sampleOption2.isChecked = SETTINGS_2.get(defaultValue = false)
                    sampleOption3.isChecked = SETTINGS_3.get(defaultValue = true)

                    dialog.setContentView(root)
                    dialog.setOnDismissListener {
                        // Save now the options
                        SETTINGS_1.save(sampleOption1.isChecked)
                        SETTINGS_2.save(sampleOption2.isChecked)
                        SETTINGS_3.save(sampleOption3.isChecked)
                    }
                    dialog.show()
                }
            }

            activityMainSampleTakeScreenshot.setOnClickListener {
                screenshot.preview = SETTINGS_1.get(defaultValue = true)
                screenshot.shutterSound = SETTINGS_2.get(defaultValue = false)
                screenshot.saveScreenshot = SETTINGS_3.get(defaultValue = true)
                screenshot.takeScreenshot(object : Screenshot.OnResultListener {
                    override fun result(success: Boolean, bitmap: Bitmap?) {
                        activityMainSampleTakeScreenshotPreviewText.isVisible = !success

                        if (success) {
                            activityMainSampleTakeScreenshotPreview.setImageBitmap(bitmap)

                            activityMainSampleTakeScreenshotOpenLast.setOnClickListener {
                                screenshot.openLastScreenshot(showErrorToast = true)
                            }

                            activityMainSampleTakeScreenshotPreview.setOnClickListener {
                                screenshot.showDialogPreview(bitmap = bitmap, cancelable = true)
                            }
                        } else {
                            activityMainSampleTakeScreenshotPreview.setImageBitmap(null)

                            activityMainSampleTakeScreenshotOpenLast.setOnClickListener(null)
                            activityMainSampleTakeScreenshotPreview.setOnClickListener(null)
                        }
                        activityMainSampleTakeScreenshotOpenLast.isEnabled =
                            success && SETTINGS_3.get(defaultValue = true)
                    }
                })
            }
        }

    }

}