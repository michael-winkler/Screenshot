package com.nmd.screenshotExample

import android.content.Context
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity

class SharedPrefsHelper {

    companion object {
        const val SETTINGS_1 = "1"
        const val SETTINGS_2 = "2"
        const val SETTINGS_3 = "3"


        var sharedPreferences: SharedPreferences? = null

        fun AppCompatActivity?.initSharedPreferences() {
            if (sharedPreferences == null) {
                sharedPreferences = this?.getPreferences(Context.MODE_PRIVATE)
            }
        }

        fun String.save(value: Boolean) {
            sharedPreferences?.edit()?.putBoolean(this, value)?.apply()
        }

        fun String.get(defaultValue: Boolean = false): Boolean {
            return sharedPreferences?.getBoolean(this, defaultValue) ?: return defaultValue
        }

    }

}