package com.imgoingonanadventure.ui.settings

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.imgoingonanadventure.data.SettingsDataStore
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class SettingsViewModel(private val dataStore: SettingsDataStore) : ViewModel() {
    private val _state: MutableLiveData<Double> = MutableLiveData()
    val state: LiveData<Double>
        get() = _state

    fun getStepLength() {
        viewModelScope.launch { dataStore.getStepLength().collect { _state.value = it } }
    }

    fun setStepLength(newLength: Double) {
        val scope = CoroutineScope(Dispatchers.IO)

        scope.launch { dataStore.setStepLength(newLength) }
    }

    fun cleanOnboarding() {
        viewModelScope.launch { dataStore.setOnboarding() }
    }
}