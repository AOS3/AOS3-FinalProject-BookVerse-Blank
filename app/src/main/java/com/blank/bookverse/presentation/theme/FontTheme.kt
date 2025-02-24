package com.blank.bookverse.presentation.theme

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

object FontTheme {
    private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "font_settings")
    private val FONT_TYPE_KEY = stringPreferencesKey("font_type")

    suspend fun Context.saveFont(fontType: FontType) {
        dataStore.edit { preferences ->
            preferences[FONT_TYPE_KEY] = fontType.name
        }
    }

    val Context.fontTypeFlow: Flow<FontType>
        get() = dataStore.data.map { preferences ->
            try {
                FontType.valueOf(preferences[FONT_TYPE_KEY] ?: FontType.NOTO_SANS.name)
            } catch (e: Exception) {
                FontType.NOTO_SANS
            }
        }
}
