package com.example.photogallery

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStoreFile
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import java.lang.IllegalStateException

class PreferencesRepository private constructor(private val dataStore: DataStore<Preferences>) {

    val searchQueryFlow: Flow<String> = dataStore.data.map { preferences ->
        preferences[SEARCH_QUERY_PREFERENCES_KEY] ?: ""
    }.distinctUntilChanged()

    suspend fun saveSearchQuery(searchQuery: String) {
        dataStore.edit { preferences ->
            preferences[SEARCH_QUERY_PREFERENCES_KEY] = searchQuery
        }
    }

    companion object {
        private var INSTANCE: PreferencesRepository? = null
        private const val PREFERENCES_DATA_STORE_FILE_NAME = "settings"

        val SEARCH_QUERY_PREFERENCES_KEY = stringPreferencesKey("searchQuery")

        fun initialise(context: Context) {
            if (INSTANCE == null) {
                val dataStore = PreferenceDataStoreFactory.create {
                    context.preferencesDataStoreFile(PREFERENCES_DATA_STORE_FILE_NAME)
                }
                INSTANCE = PreferencesRepository(dataStore)
            }
        }

        fun getInstance(): PreferencesRepository {
            return INSTANCE ?: throw IllegalStateException("PreferencesRepository must be initialised")
        }
    }
}