/*
 * Copyright 2020-2022 Author @NMD [Next Mobile Development]
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.nmd.screenshot

import android.app.Activity
import android.graphics.Bitmap
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.WindowManager
import android.widget.ImageView
import androidx.annotation.RequiresApi
import androidx.core.view.drawToBitmap
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.nmd.screenshot.helpers.Helper

@Suppress("unused")
class Screenshot(private val activity: Activity) {
    var onResultListener: OnResultListener? = null

    private var internalFileName: String = "Screenshot"
    private var internalShutterSound = false
    private var internalPreview = false
    private var internalBlurBackgroundPreview = false

    /**
     * The Screenshot OnResultListener.
     */
    interface OnResultListener {
        /**
         * The Screenshot OnResultListener.
         *
         * @param success Boolean.
         * @param filePath String.
         * @param bitmap Bitmap?.
         */
        fun result(success: Boolean, filePath: String, bitmap: Bitmap?)
    }

    /**
     * Takes a screenshot from the current users display.
     */
    fun takeScreenshot() {
        val view = activity.window.decorView.rootView
        take(view)
    }

    /**
     * The view of which the screenshot is taken from.
     *
     * @param view View.
     */
    fun takeScreenshot(view: View) {
        take(view)
    }

    private fun take(view: View) {
        Handler(Looper.getMainLooper()).post {
            try {
                Helper.saveBitmapToFile(
                    bitmap = view.drawToBitmap(),
                    activity = activity,
                    internalFileName = internalFileName,
                    internalPreview = internalPreview,
                    internalShutterSound = internalShutterSound,
                    onResultListener = onResultListener
                ) { preview() }
            } catch (e: IllegalStateException) {
                Helper.saveBitmapToFile(
                    bitmap = null,
                    activity = activity,
                    internalFileName = internalFileName,
                    internalPreview = internalPreview,
                    internalShutterSound = internalShutterSound,
                    onResultListener = onResultListener
                ) { preview() }
            }
        }
    }

    var fileName: String
        get() {
            return internalFileName
        }
        /**
         * The filename of the new taken screenshot.
         *
         * @param name String.
         */
        set(name) {
            internalFileName = name
        }

    var preview: Boolean
        get() {
            return internalPreview
        }
        /**
         * If set to true a preview dialog of the screenshot will be shown.
         *
         * @param enabled Boolean.
         */
        set(enabled) {
            internalPreview = enabled
        }

    var shutterSound: Boolean
        get() {
            return internalShutterSound
        }
        /**
         * If set to true a shutter sound will be played.
         *
         * @param enabled Boolean.
         */
        set(enabled) {
            internalShutterSound = enabled
        }


    var blurBackgroundPreview: Boolean

        get() {
            return internalBlurBackgroundPreview
        }
        /**
         * If set to true the preview dialog will blur the background.
         * Requires Android 12+ to work.
         *
         * @param enabled Boolean.
         */
        @RequiresApi(31)
        set(enabled) {
            internalBlurBackgroundPreview = enabled
        }

    /**
     * You can use this option in your app to enable/disable that users can take a screenshot
     * from your app by the system screenshot method.
     *
     * @param enabled Boolean.
     */
    fun allowScreenshots(enabled: Boolean) {
        if (enabled) {
            activity.window.clearFlags(WindowManager.LayoutParams.FLAG_SECURE)
        } else {
            activity.window.setFlags(
                WindowManager.LayoutParams.FLAG_SECURE,
                WindowManager.LayoutParams.FLAG_SECURE
            )
        }
    }

    private fun preview() {
        activity.let {
            if (it.isFinishing || it.isDestroyed) return

            val view: View = LayoutInflater.from(it)
                .inflate(R.layout.dialog_preview, it.findViewById(android.R.id.content), false)
            val imageView: ImageView = view.findViewById(R.id.dialog_preview_image_view)
            imageView.setImageBitmap(Helper.getBitmap(activity, fileName))

            val alertDialog =
                MaterialAlertDialogBuilder(activity, R.style.DialogRoundTheme).create().apply {
                    setView(view)
                    setCancelable(false)
                    setCanceledOnTouchOutside(false)
                    if (internalBlurBackgroundPreview) {
                        Helper.blurAlertDialog(this)
                    }
                    show()
                }

            Handler(Looper.getMainLooper()).postDelayed({
                alertDialog.dismiss()
            }, 1250)

        }

    }

}