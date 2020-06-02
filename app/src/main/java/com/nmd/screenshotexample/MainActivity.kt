package com.nmd.screenshotexample

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.nmd.screenshot.Screenshot

class MainActivity : AppCompatActivity() {
    private lateinit var screenshot: Screenshot

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        screenshot = Screenshot(this)
    }

    fun buttonClick(view: View) {
        screenshot.takeScreenshot()
    }
}