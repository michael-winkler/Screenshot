package com.nmd.screenshot.helpers

import android.app.Activity
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.media.MediaActionSound
import android.os.Build
import android.os.Environment
import android.view.WindowManager
import androidx.appcompat.app.AlertDialog
import com.nmd.screenshot.Screenshot
import java.io.File
import java.io.FileInputStream

class Helper {

    companion object {

        fun blurAlertDialog(alertDialog: AlertDialog?) {
            alertDialog?.let {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                    it.window?.addFlags(WindowManager.LayoutParams.FLAG_BLUR_BEHIND)
                    it.window?.attributes?.blurBehindRadius = 8
                }
                it.window?.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
            }
        }

        fun saveBitmapToFile(bitmap: Bitmap?, activity: Activity, internalFileName: String, internalPreview: Boolean, internalShutterSound: Boolean, onResultListener: Screenshot.OnResultListener?, previewRunnable: Runnable) {
            val file: File? = getFile(bitmap, activity, internalFileName)
            if (file == null) {
                onResultListener?.result(false, "", bitmap)
            } else {
                if (internalShutterSound) {
                    MediaActionSound().apply {
                        load(MediaActionSound.SHUTTER_CLICK)
                        play(MediaActionSound.SHUTTER_CLICK)
                    }
                }
                if (internalPreview) {
                    previewRunnable.run()
                }
                onResultListener?.result(true, file.absolutePath, bitmap)
            }
        }

        private fun getFile(bitmap: Bitmap?, activity: Activity, filename: String): File? {
            bitmap ?: return null

            return try {
                val file = file(activity, filename)
                file.writeBitmap(bitmap, Bitmap.CompressFormat.PNG, 100)
                file
            } catch (e: Exception) {
                null
            }
        }

        fun getBitmap(activity: Activity, filename: String): Bitmap? {
            return try {
                BitmapFactory.decodeStream(
                    FileInputStream(file(activity, filename)),
                    null,
                    BitmapFactory.Options().apply {
                        inPreferredConfig = Bitmap.Config.ARGB_8888
                    })
            } catch (e: Exception) {
                null
            }
        }

        private fun file(activity: Activity, filename: String): File {
            return File(
                activity.getExternalFilesDir(Environment.DIRECTORY_PICTURES),
                "$filename.png"
            )
        }

        private fun File.writeBitmap(bitmap: Bitmap, format: Bitmap.CompressFormat, quality: Int) {
            outputStream().use { out ->
                bitmap.compress(format, quality, out)
                out.flush()
            }
        }
    }


}