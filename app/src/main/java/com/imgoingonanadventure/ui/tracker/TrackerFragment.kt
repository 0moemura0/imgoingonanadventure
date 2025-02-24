package com.imgoingonanadventure.ui.tracker

import android.Manifest.permission.ACTIVITY_RECOGNITION
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.PermissionChecker
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.imgoingonanadventure.App
import com.imgoingonanadventure.ui.character.CharacterFragment
import com.imgoingonanadventure.ui.notes.NotesFragment
import com.imgoingonanadventure.ui.service.StepTrackerService
import com.imgoingontheadventure.R
import kotlinx.coroutines.flow.collectLatest
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

        val subtitle: TextView = view.findViewById(R.id.trackerSubtitle)
        val title: TextView = view.findViewById(R.id.trackerTitle)
        val titleSteps: TextView = view.findViewById(R.id.trackerTitleSteps)
        val buttonToCharacter: View = view.findViewById(R.id.viewToCharacter)
        val buttonToNotes: View = view.findViewById(R.id.viewToNotes)
        val buttonToTracker: View = view.findViewById(R.id.viewToTracker)

        setButtons(buttonToCharacter, buttonToNotes,buttonToTracker)
        observeData(subtitle, title, titleSteps)

        viewModel.getStepState()

        checkPermission()
    }

    private fun observeData(subtitle: TextView, title: TextView, titleSteps: TextView) {

        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.state.collectLatest { state ->
                    when(state){
                        is StepState.Loading -> Unit
                        is StepState.Data -> {
                            subtitle.text = state.event.map { it.event }.toString()//todo вынести
                            title.text = state.distance.toString()
                            titleSteps.text = state.steps?.count.toString()
                        }
                        is StepState.Error -> Log.e(TAG, "onViewCreated: ${state.error.stackTrace.take(5)}")
                    }
                }
            }
        }

        StepTrackerService.liveStepCount.observe(viewLifecycleOwner) { stepCount ->
            viewModel.setStepCount(stepCount)
        }
    }

    private fun setButtons(buttonToCharacter: View, buttonToNotes: View, buttonToTracker: View) {
        buttonToNotes.setOnClickListener { parentFragmentManager.beginTransaction().replace(R.id.container, NotesFragment.newInstance()).commit() }
        buttonToCharacter.setOnClickListener { parentFragmentManager.beginTransaction().replace(R.id.container, CharacterFragment.newInstance()).commit() }
        buttonToTracker.setOnClickListener {
            context?.startForegroundService(Intent(requireContext(), StepTrackerService::class.java))
        }
    }

    private fun checkPermission() {
        val activityPermission = ACTIVITY_RECOGNITION
        val activityRecognitionPermission = PermissionChecker.checkSelfPermission(requireContext(), activityPermission)

        if (activityRecognitionPermission != PermissionChecker.PERMISSION_GRANTED) {
            @Suppress("DEPRECATION")
            requestPermissions(arrayOf(activityPermission) , 1)//todo add result
        }
    }

    companion object {
        fun newInstance() = TrackerFragment()
        private const val TAG = "TrackerFragment"
    }
}
