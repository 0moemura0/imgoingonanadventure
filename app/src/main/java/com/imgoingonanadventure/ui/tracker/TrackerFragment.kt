package com.imgoingonanadventure.ui.tracker

import android.Manifest.permission.ACTIVITY_RECOGNITION
import android.Manifest.permission.POST_NOTIFICATIONS
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.PermissionChecker
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.imgoingonanadventure.App
import com.imgoingonanadventure.ui.service.StepTrackerService
import com.imgoingonanadventure.ui.settings.SettingsFragment
import com.imgoingontheadventure.R
import com.imgoingontheadventure.databinding.FragmentTrackerBinding
import kotlinx.coroutines.launch


class TrackerFragment : Fragment() {

    private val viewModel: TrackerViewModel by viewModels<TrackerViewModel> {
        App.appModule.viewModuleModule.trackerViewModelFactory
    }

    private var _binding: FragmentTrackerBinding? = null
    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel.getStepState()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentTrackerBinding.inflate(inflater, container, false)
        val view = binding.root
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setButtons()
        observeData()
        checkPermission()
    }

    override fun onResume() {
        super.onResume()
        if (StepTrackerService.liveStepCount.isInitialized && StepTrackerService.liveStepCount.value != null) {
            viewModel.setStepCount(StepTrackerService.liveStepCount.value!!)
        }
    }

    private fun observeData() {
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.stateEvent.collect {
                    //StepTrackerService.liveEvent.postValue(it)
                    binding.trackerSubtitle.text = it
                }
            }
        }
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.stateDistance.collect { binding.trackerTitle.text = "$it km" }
            }
        }
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.stateImage.collect {
                    binding.viewGrass.setImageResource(it.grassId)
                    binding.imageBackground.setImageResource(it.imageId)
                }
            }
        }

        StepTrackerService.liveStepCount.observe(viewLifecycleOwner) { stepCount ->
            viewModel.setStepCount(stepCount)
        }
    }

    private fun setButtons() {
//        binding.viewToNotes.setOnClickListener {
//            parentFragmentManager.beginTransaction()
//                .replace(R.id.container, NotesFragment.newInstance())
//                .addToBackStack(BACK_STACK)
//                .commit()
//        }
        binding.viewToSettings.setOnClickListener {
            parentFragmentManager.beginTransaction()
                .replace(R.id.container, SettingsFragment.newInstance())
                .addToBackStack(BACK_STACK)
                .commit()
        }
        binding.viewToTracker.setOnClickListener {
            context?.startForegroundService(
                Intent(
                    requireContext(),
                    StepTrackerService::class.java
                )
            )
        }
    }

    private fun checkPermission() {
        val activityPermission = ACTIVITY_RECOGNITION
        val requestedPermissionArray = arrayOf(activityPermission)
        val activityRecognitionPermission =
            PermissionChecker.checkSelfPermission(requireContext(), activityPermission)

        if (activityRecognitionPermission != PermissionChecker.PERMISSION_GRANTED) {
            @Suppress("DEPRECATION")
            requestPermissions(
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    requestedPermissionArray.plus(POST_NOTIFICATIONS)
                } else {
                    requestedPermissionArray
                },
                1
            )//todo add result
        }
    }

    companion object {
        fun newInstance() = TrackerFragment()
        private const val BACK_STACK = "trackerBackStack"
    }
}
