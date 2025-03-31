package com.imgoingonanadventure.ui.settings

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.imgoingonanadventure.App
import com.imgoingontheadventure.R

class SettingsFragment : Fragment() {

    private val viewModel: SettingsViewModel by viewModels<SettingsViewModel> {
        App.appModule.viewModuleModule.trackerViewModelFactory
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.fragment_settings, container, false)
    }

    companion object {
        fun newInstance() = SettingsFragment()
    }
}