# Screenshot

[![](https://jitpack.io/v/michael-winkler/Screenshot.svg)](https://jitpack.io/#michael-winkler/Screenshot)
[![Last commit](https://img.shields.io/github/last-commit/michael-winkler/Screenshot?style=flat)](https://github.com/michael-winkler/Screenshot/commits)
![GitHub all releases](https://img.shields.io/github/downloads/michael-winkler/Screenshot/total)
[![API](https://img.shields.io/badge/API-26%2B-orange.svg?style=flat)](https://android-arsenal.com/api?level=26)
[![License Apache 2.0](https://img.shields.io/badge/License-Apache%202.0-blue.svg?style=true)](http://www.apache.org/licenses/LICENSE-2.0)

A new and nice working android library to take screenshots which provides many functions.    
If enabled you will see a screenshot preview.    
While the screenshot is being taken, you will also hear the shutter click sound if enabled.

This library uses Android X depencies and is written in Kotlin.


## Usage
Add a dependency to your build.gradle file:
```kotlin
allprojects {
    repositories {
        ...
        maven { url 'https://jitpack.io' }
    }
}

dependencies {
    implementation 'com.github.michael-winkler:Screenshot:1.1.0'
}
```

```kotlin
import com.nmd.screenshot.Screenshot
```

| Public constructors |
| --- |
| `Screenshot(appCompatActivity: AppCompatActivity)` |


## Interface

| type | name |
| --- | --- |
| `interface` | OnResultListener<br>*Interface definition for a callback to be invoked when a screenshot was created.* |

Public methods

| type | function |
| --- | --- |
| `fun` | result(success: Boolean, bitmap: Bitmap?)<br>*Called when a screenshot was created.* |

```kotlin
screenshot.takeScreenshot(object : Screenshot.OnResultListener {
    override fun result(success: Boolean, bitmap: Bitmap?) {
        // do whatever you want
    }
})
```

## Public methods

| type          | function | description | default value |
|---------------| --- |----------------|------------------|
| `fun`         | takeScreenshot(onResultListener: OnResultListener?) | *Takes a screenshot from the current users display.* | - |
| `fun`         | showDialogPreview(bitmap: Bitmap?, cancelable: Boolean = false) | *Use this method to display a Bitmap inside the Material Dialog.* | - |
| `fun`         | openLastScreenshot(showErrorToast: Boolean = false) | *Use this method to open the last taken screenshot file.* | - |
| `var Boolean` | preview | *If set to true a preview dialog of the screenshot will be shown.* | true |
| `var Boolean` | shutterSound | *If set to true a shutter sound will be played.* | false |
| `var Boolean` | saveScreenshot | *If set to true the taken screenshot will be saved into the internal app directory.* | true |
| `var Float`   | dimAmount | *Set the amount of dim behind the preview dialog. Use '0.0f' for no dim and '1.0f' for full dim.* | 0.5f |
| `var String`  | fileName | *The filename for the taken screenshot.* | "Screenshot.png" |

## Exampe code

```kotlin
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
```

## Screenshots
![Screenshot](https://github.com/michael-winkler/Screenshot/blob/master/Images/Screenshot.png)

## Download Sample App
https://github.com/michael-winkler/Screenshot/releases/download/1.1.0/app-release.apk

## License
```
Copyright Author @NMD [Next Mobile Development - Michael Winkler]

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

   http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
```
## Last words
If you like this library feel free to "star" it:<br>
![star](https://github.com/michael-winkler/Screenshot/blob/master/Images/star.png)

```
This library has been successfully tested with:
Android Studio Ladybug Feature Drop | 2024.2.2 Patch 1
```