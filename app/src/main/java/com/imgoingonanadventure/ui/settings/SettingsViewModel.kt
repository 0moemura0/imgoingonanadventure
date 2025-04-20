package com.imgoingonanadventure.ui.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.imgoingonanadventure.data.SettingsDataStore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class SettingsViewModel(private val dataStore: SettingsDataStore) : ViewModel() {
    private val _state: MutableStateFlow<Double> = MutableStateFlow(0.5)
    val state: StateFlow<Double>
        get() = _state.asStateFlow()

    fun getStepLength() {
        viewModelScope.launch { dataStore.getStepLength().collect { _state.update { it } } }
    }

    fun setStepLength(newLength: Double) {
        viewModelScope.launch { dataStore.setStepLength(newLength) }
    }

    fun cleanOnboarding() {
        viewModelScope.launch { dataStore.setOnboarding() }
    }
}