package com.pankxu.arenacompanion.core.storage

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.prefsDataStore: DataStore<Preferences> by preferencesDataStore(name = "app_settings")

/**
 * Unified preferences manager.
 * Sensitive credentials are NOT stored here — they go to CredentialVault.
 */
class PreferencesManager(private val context: Context) {

    private val dataStore = context.prefsDataStore

    val themeMode: Flow<String> = dataStore.data.map { it[stringPreferencesKey("theme_mode")] ?: "system" }
    val dynamicColor: Flow<Boolean> = dataStore.data.map { it[booleanPreferencesKey("dynamic_color")] ?: true }
    val defaultModel: Flow<String> = dataStore.data.map { it[stringPreferencesKey("default_model")] ?: "" }
    val defaultMode: Flow<String> = dataStore.data.map { it[stringPreferencesKey("default_mode")] ?: "agent" }
    val notificationEnabled: Flow<Boolean> = dataStore.data.map { it[booleanPreferencesKey("notifications_enabled")] ?: false }
    val biometricEnabled: Flow<Boolean> = dataStore.data.map { it[booleanPreferencesKey("biometric_enabled")] ?: false }
    val autoSync: Flow<Boolean> = dataStore.data.map { it[booleanPreferencesKey("auto_sync")] ?: true }

    suspend fun setThemeMode(mode: String) {
        dataStore.edit { it[stringPreferencesKey("theme_mode")] = mode }
    }

    suspend fun setDynamicColor(enabled: Boolean) {
        dataStore.edit { it[booleanPreferencesKey("dynamic_color")] = enabled }
    }

    suspend fun setDefaultModel(model: String) {
        dataStore.edit { it[stringPreferencesKey("default_model")] = model }
    }

    suspend fun setDefaultMode(mode: String) {
        dataStore.edit { it[stringPreferencesKey("default_mode")] = mode }
    }

    suspend fun setNotificationEnabled(enabled: Boolean) {
        dataStore.edit { it[booleanPreferencesKey("notifications_enabled")] = enabled }
    }

    suspend fun setBiometricEnabled(enabled: Boolean) {
        dataStore.edit { it[booleanPreferencesKey("biometric_enabled")] = enabled }
    }

    suspend fun setAutoSync(enabled: Boolean) {
        dataStore.edit { it[booleanPreferencesKey("auto_sync")] = enabled }
    }

    val storageThresholdMB: Flow<Int> = dataStore.data.map { it[intPreferencesKey("storage_threshold_mb")] ?: 80 }

    suspend fun setStorageThresholdMB(mb: Int) {
        dataStore.edit { it[intPreferencesKey("storage_threshold_mb")] = mb }
    }
}
