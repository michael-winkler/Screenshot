/*
 * Copyright Author @NMD [Next Mobile Development - Michael Winkler]
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

import android.content.Intent
import android.graphics.Bitmap
import android.media.MediaActionSound
import android.net.Uri
import android.os.Environment
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.PixelCopy
import android.widget.Toast
import androidx.annotation.FloatRange
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.FileProvider
import androidx.core.view.drawToBitmap
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.nmd.screenshot.databinding.DialogPreviewBinding
import java.io.File

class Screenshot(private val appCompatActivity: AppCompatActivity?) {

    private var lastFileObject: File? = null
    private var internalSaveScreenshot = true
    private var internalShutterSound = false
    private var internalPreview = true
    private var internalDimAmount = 0.5f
    private var internalFilename = "Screenshot.png"

    /**
     * If set to true a preview dialog of the screenshot will be shown.
     * Default is true.
     */
    var preview: Boolean
        get() {
            return internalPreview
        }
        set(enabled) {
            internalPreview = enabled
        }

    /**
     * If set to true a shutter sound will be played.
     * Default is false.
     */
    var shutterSound: Boolean
        get() {
            return internalShutterSound
        }
        set(enabled) {
            internalShutterSound = enabled
        }

    /**
     * If set to true the taken screenshot will be saved into the internal app directory.
     * Default is true.
     */
    var saveScreenshot: Boolean
        get() {
            return internalSaveScreenshot
        }
        set(enabled) {
            internalSaveScreenshot = enabled
        }

    /**
     * Set the amount of dim behind the preview dialog.
     * Use '0.0f' for no dim and '1.0f' for full dim.
     * Default is 0.5f.
     */
    var dimAmount: Float
        get() {
            return internalDimAmount
        }
        set(@FloatRange(from = 0.0, to = 1.0) amount) {
            internalDimAmount = amount
        }

    /**
     * The filename for the taken screenshot.
     * Default is "Screenshot.png".
     */
    var fileName: String
        get() {
            return internalFilename
        }
        set(name) {
            internalFilename = name
        }

    /**
     * The Screenshot OnResultListener.
     */
    interface OnResultListener {
        /**
         * The Screenshot OnResultListener.
         *
         * @param success Boolean
         * @param bitmap Bitmap?
         */
        fun result(success: Boolean, bitmap: Bitmap?)
    }

    /**
     * Takes a screenshot from the current users display.
     */
    fun takeScreenshot(onResultListener: OnResultListener?) {
        val view = appCompatActivity?.window?.decorView?.rootView
        if (appCompatActivity == null || view == null) {
            onResultListener?.result(success = false, bitmap = null)
            return
        }
        val bitmap = try {
            view.drawToBitmap()
        } catch (ignored: IllegalStateException) {
            null
        }
        if (bitmap == null) {
            onResultListener?.result(success = false, bitmap = null)
            return
        }

        PixelCopy.request(
            appCompatActivity.window, bitmap,
            {
                val success = it == PixelCopy.SUCCESS
                onResultListener?.result(success = success, bitmap = bitmap)

                if (success) {
                    if (internalShutterSound) {
                        MediaActionSound().apply {
                            load(MediaActionSound.SHUTTER_CLICK)
                            play(MediaActionSound.SHUTTER_CLICK)
                        }
                    }
                    if (internalPreview) {
                        showDialogPreview(bitmap)
                    }
                    if (saveScreenshot) {
                        bitmap.saveScreenshotToAppDirectory()
                    } else {
                        lastFileObject = null
                    }
                }
            }, Handler(Looper.getMainLooper())
        )
    }

    /**
     * Use this method to display a Bitmap inside the Material Dialog.
     *
     * @param bitmap Bitmap?
     * @param cancelable Boolean = false
     */
    fun showDialogPreview(bitmap: Bitmap?, cancelable: Boolean = false) {
        appCompatActivity ?: return
        bitmap ?: return

        if (appCompatActivity.isFinishing || appCompatActivity.isDestroyed) return
        val binding: DialogPreviewBinding =
            DialogPreviewBinding.inflate(LayoutInflater.from(appCompatActivity))
        binding.dialogPreviewImageView.setImageBitmap(bitmap)

        val alertDialog =
            MaterialAlertDialogBuilder(appCompatActivity, R.style.MaterialThemeDialog).create()
                .apply {
                    setView(binding.root)
                    setCancelable(cancelable)
                    setCanceledOnTouchOutside(cancelable)

                    window?.setDimAmount(dimAmount)
                    show()
                }
        if (!cancelable) {
            Handler(Looper.getMainLooper()).postDelayed({
                alertDialog.dismiss()
            }, 1500)
        }
    }

    /**
     * Use this method to open the last taken screenshot file.
     *
     * @param showErrorToast Boolean = false
     */
    fun openLastScreenshot(showErrorToast: Boolean = false) {
        try {
            appCompatActivity ?: return
            lastFileObject?.let { lastFile ->
                val fileUri: Uri = FileProvider.getUriForFile(
                    appCompatActivity,
                    appCompatActivity.packageName + ".provider",
                    lastFile
                )

                val intent = Intent(Intent.ACTION_VIEW, fileUri).apply {
                    addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                }
                appCompatActivity.startActivity(intent)
            }
        } catch (e: Exception) {
            if (showErrorToast) {
                Toast.makeText(appCompatActivity, e.message ?: "Exception!", Toast.LENGTH_LONG)
                    .show()
            }
        }
    }

    private fun getFileObject(): File? {
        val name = if (fileName.trim().isEmpty()) {
            "Screenshot.png"
        } else {
            fileName
        }

        lastFileObject = if (appCompatActivity == null) {
            null
        } else {
            File(
                appCompatActivity.getExternalFilesDir(Environment.DIRECTORY_PICTURES),
                "${name}.png"
            )
        }
        return lastFileObject
    }

    private fun Bitmap?.saveScreenshotToAppDirectory() {
        this ?: return
        getFileObject()?.writeBitmap(this)
    }

    private fun File.writeBitmap(bitmap: Bitmap) {
        outputStream().use { out ->
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, out)
            out.flush()
        }
    }

}