package com.imgoingonanadventure.data

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.core.handlers.ReplaceFileCorruptionHandler
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.doublePreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.preferencesOf
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.imgoingonanadventure.model.RouteSequence
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map

private const val DATA_STORE_NAME = "settings"
private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(
    name = DATA_STORE_NAME,
    corruptionHandler = ReplaceFileCorruptionHandler { preferencesOf() },
)

class SettingsDataStore(private val context: Context) {

    suspend fun setOnboarding() {
        context.dataStore.edit { settings ->
            val key: Preferences.Key<Boolean> = booleanPreferencesKey(ONBOARDING_PREF)
            val currentValue: Boolean = settings[key] ?: false
            settings[key] = currentValue.not()
        }
    }

    fun getOnboarding(): Flow<Boolean> {
        val key: Preferences.Key<Boolean> = booleanPreferencesKey(ONBOARDING_PREF)
        return context.dataStore.data
            .map { preferences -> preferences[key] ?: false }
    }

    suspend fun setStepLength() {
        context.dataStore.edit { settings ->
            val key: Preferences.Key<Boolean> = booleanPreferencesKey(STEP_LENGTH)
            val currentValue: Boolean = settings[key] ?: false
            settings[key] = currentValue.not()
        }
    }

    fun getStepLength(): Flow<Double> {
        val key: Preferences.Key<Double> = doublePreferencesKey(STEP_LENGTH)
        return context.dataStore.data
            .map { preferences -> preferences[key] ?: DEFAULT_STEP_LENGTH }
    }

    suspend fun setNextEventChunkId() {
        context.dataStore.edit { settings ->
            val key: Preferences.Key<String> = stringPreferencesKey(EVENT_CHUNK)
            val current: String = context.dataStore.data
                .map { preferences ->
                    preferences[key] ?: RouteSequence.sequenceMain.first().routeId
                }
                .first()
            settings[key] =
                RouteSequence.sequenceMain[RouteSequence.sequenceMain.indexOfFirst { it.routeId == current }
                    .inc()].routeId // todo
        }
    }

    fun getEventChunkId(): Flow<String> {
        val key: Preferences.Key<String> = stringPreferencesKey(EVENT_CHUNK)
        return context.dataStore.data
            .map { preferences ->
                preferences[key] ?: RouteSequence.sequenceMain.first().routeId
            } //todo ??
    }

    companion object {
        private const val ONBOARDING_PREF = "onboarding"
        private const val STEP_LENGTH = "step_length"
        private const val EVENT_CHUNK = "event_chunk"
        private const val DEFAULT_STEP_LENGTH: Double = 0.5
    }
}