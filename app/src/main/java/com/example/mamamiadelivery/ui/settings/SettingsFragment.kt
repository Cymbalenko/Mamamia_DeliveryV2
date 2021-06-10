package com.example.mamamiadelivery.ui.settings

import android.annotation.TargetApi
import android.os.Build
import android.os.Bundle
import androidx.preference.PreferenceFragmentCompat
import com.example.mamamiadelivery.R
import com.example.mamamiadelivery.R.*

class SettingsFragment : PreferenceFragmentCompat() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        onCreatePreferenceFragment()
    }
    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.root_preferences, rootKey)
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    private fun onCreatePreferenceFragment() {
        parentFragmentManager.beginTransaction()
            .replace(android.R.id.content, SettingsFragment())
            .commit()
    }


}