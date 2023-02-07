package com.nmd.screenshotexample

import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.button.MaterialButton
import com.google.android.material.checkbox.MaterialCheckBox
import com.nmd.screenshot.Screenshot

class MainActivity : AppCompatActivity() {
    private var screenshot: Screenshot? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        initialize()
    }

    private fun initialize() {
        screenshot = Screenshot(this)
        screenshot?.fileName = "Test"

        findViewById<MaterialCheckBox>(R.id.sample_option_1)?.setOnCheckedChangeListener { _, isChecked ->
            screenshot?.preview = isChecked
        }

        findViewById<MaterialCheckBox>(R.id.sample_option_2)?.setOnCheckedChangeListener { _, isChecked ->
            screenshot?.shutterSound = isChecked
        }

        findViewById<MaterialCheckBox>(R.id.sample_option_3)?.setOnCheckedChangeListener { _, isChecked ->
            screenshot?.allowScreenshots(isChecked)
        }

        findViewById<MaterialButton>(R.id.sample_take_screenshot)?.setOnClickListener {
            screenshot?.takeScreenshot()
        }
    }

}