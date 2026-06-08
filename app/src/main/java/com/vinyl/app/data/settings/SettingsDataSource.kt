package com.vinyl.app.data.settings

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.*
import androidx.datastore.preferences.preferencesDataStore
import com.vinyl.app.data.model.SettingsDataModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "vinyl_settings")

@Singleton
class SettingsDataSource @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private object Keys {
        val THEME = stringPreferencesKey("theme")
        val VINYL_STYLE = stringPreferencesKey("vinyl_style")
        val VINYL_TEXTURE = stringPreferencesKey("vinyl_texture")
        val BATTERY_SAVER = booleanPreferencesKey("battery_saver")
        val TILT_PARALLAX = booleanPreferencesKey("tilt_parallax")
        val CONTROLS_TIMEOUT = intPreferencesKey("controls_timeout")
        val FIRST_LAUNCH = booleanPreferencesKey("first_launch")
    }

    val settings: Flow<SettingsDataModel> = context.dataStore.data.map { prefs ->
        SettingsDataModel(
            theme = prefs[Keys.THEME] ?: "OBSIDIAN",
            vinylStyle = prefs[Keys.VINYL_STYLE] ?: "CLASSIC",
            vinylTexture = prefs[Keys.VINYL_TEXTURE] ?: "STANDARD",
            batterySaver = prefs[Keys.BATTERY_SAVER] ?: false,
            tiltParallax = prefs[Keys.TILT_PARALLAX] ?: false,
            controlsTimeoutSeconds = prefs[Keys.CONTROLS_TIMEOUT] ?: 3,
            firstLaunch = prefs[Keys.FIRST_LAUNCH] ?: true
        )
    }

    suspend fun setTheme(value: String) {
        context.dataStore.edit { it[Keys.THEME] = value }
    }

    suspend fun setVinylStyle(value: String) {
        context.dataStore.edit { it[Keys.VINYL_STYLE] = value }
    }

    suspend fun setVinylTexture(value: String) {
        context.dataStore.edit { it[Keys.VINYL_TEXTURE] = value }
    }

    suspend fun setBatterySaver(enabled: Boolean) {
        context.dataStore.edit { it[Keys.BATTERY_SAVER] = enabled }
    }

    suspend fun setTiltParallax(enabled: Boolean) {
        context.dataStore.edit { it[Keys.TILT_PARALLAX] = enabled }
    }

    suspend fun setControlsTimeout(seconds: Int) {
        context.dataStore.edit { it[Keys.CONTROLS_TIMEOUT] = seconds }
    }

    suspend fun setFirstLaunchComplete() {
        context.dataStore.edit { it[Keys.FIRST_LAUNCH] = false }
    }
}
