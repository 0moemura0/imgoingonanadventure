package com.imgoingonanadventure.ui.settings

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.FileProvider
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.imgoingonanadventure.App
import com.imgoingontheadventure.databinding.FragmentSettingsBinding
import kotlinx.coroutines.launch
import java.io.File

class SettingsFragment : Fragment() {

    private val viewModel: SettingsViewModel by viewModels<SettingsViewModel> {
        App.appModule.viewModuleModule.settingsViewModelFactory
    }

    private var _binding: FragmentSettingsBinding? = null
    private val binding get() = _binding!!
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSettingsBinding.inflate(inflater, container, false)
        val view = binding.root
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.state.collect { binding.stepLengthEditText.setText(it.toString()) }
            }
        }
        binding.crashButton.setOnClickListener {
            val sendIntent: Intent = Intent().apply {
                val uri = FileProvider.getUriForFile(
                    requireContext(),
                    requireContext().applicationContext?.packageName + ".provider",
                    File(context?.cacheDir, "logs.log")
                )
                putExtra(Intent.EXTRA_STREAM, uri)
                action = Intent.ACTION_SEND
                setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                type = "text/*"
            }

            val shareIntent = Intent.createChooser(sendIntent, null)
            startActivity(shareIntent)
        }

//        binding.introButton.setOnClickListener {
//            binding.introButton.isClickable = false
//            binding.introButton.isEnabled = false
//            viewModel.cleanOnboarding()
//        }

        viewModel.getStepLength()
    }

    override fun onPause() {
        super.onPause()
        if (binding.stepLengthEditText.text.isNotBlank() && binding.stepLengthEditText.text.isNotEmpty()) {
            viewModel.setStepLength(binding.stepLengthEditText.text.toString().toDouble())
        }
    }

    companion object {
        fun newInstance() = SettingsFragment()
    }
}