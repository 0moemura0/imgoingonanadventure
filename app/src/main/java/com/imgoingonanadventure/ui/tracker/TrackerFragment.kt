package com.imgoingonanadventure.ui.tracker

import android.Manifest.permission.ACTIVITY_RECOGNITION
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.PermissionChecker
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.imgoingonanadventure.App
import com.imgoingonanadventure.ui.notes.NotesFragment
import com.imgoingonanadventure.ui.service.StepTrackerService
import com.imgoingonanadventure.ui.settings.SettingsFragment
import com.imgoingontheadventure.R
import kotlinx.coroutines.launch

class TrackerFragment : Fragment() {

    private val viewModel: TrackerViewModel by viewModels<TrackerViewModel> {
        App.appModule.viewModuleModule.trackerViewModelFactory
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        return inflater.inflate(R.layout.fragment_tracker, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val image: ImageView = view.findViewById(R.id.imageBackground)
        val imageGrass: ImageView = view.findViewById(R.id.viewGrass)
        val subtitle: TextView = view.findViewById(R.id.trackerSubtitle)
        val title: TextView = view.findViewById(R.id.trackerTitle)
        val buttonToSettings: View = view.findViewById(R.id.viewToSettings)
        val buttonToNotes: View = view.findViewById(R.id.viewToNotes)
        val buttonToTracker: View = view.findViewById(R.id.viewToTracker)

        setButtons(buttonToSettings, buttonToNotes, buttonToTracker)
        observeData(subtitle, title, image, imageGrass)

        viewModel.getStepState()

        checkPermission()
    }

    private fun observeData(
        subtitle: TextView,
        title: TextView,
        image: ImageView,
        imageGrass: ImageView
    ) {
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.stateEvent.collect { subtitle.text = it }
            }
        }
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.stateDistance.collect { title.text = it.toString() }
            }
        }
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.stateImage.collect {
                    imageGrass.setImageResource(it.grassId)
                    image.setImageResource(it.imageId)
                }
            }
        }

        StepTrackerService.liveStepCount.observe(viewLifecycleOwner) { stepCount ->
            viewModel.setStepCount(stepCount)
        }
    }

    private fun setButtons(buttonToSettings: View, buttonToNotes: View, buttonToTracker: View) {
        buttonToNotes.setOnClickListener {
            parentFragmentManager.beginTransaction()
                .add(R.id.container, NotesFragment.newInstance()).commit()
        }
        buttonToSettings.setOnClickListener {
            parentFragmentManager.beginTransaction()
                .add(R.id.container, SettingsFragment.newInstance()).commit()
        }
        buttonToTracker.setOnClickListener {
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
        val activityRecognitionPermission =
            PermissionChecker.checkSelfPermission(requireContext(), activityPermission)

        if (activityRecognitionPermission != PermissionChecker.PERMISSION_GRANTED) {
            @Suppress("DEPRECATION")
            requestPermissions(arrayOf(activityPermission), 1)//todo add result
        }
    }

    companion object {
        fun newInstance() = TrackerFragment()
    }
}
