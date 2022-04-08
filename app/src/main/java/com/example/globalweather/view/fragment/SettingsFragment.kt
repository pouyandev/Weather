package com.example.globalweather.view.fragment

import android.os.Bundle
import androidx.preference.PreferenceFragmentCompat
import com.example.globalweather.R
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SettingsFragment : PreferenceFragmentCompat() {
    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        addPreferencesFromResource(R.xml.preferences)
    }


}