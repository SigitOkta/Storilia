package com.dwarfkit.storilia.data.local.datastore

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import com.dwarfkit.storilia.data.local.entity.UserEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class UserPreferences private constructor(private val dataStore: DataStore<Preferences>) {

    fun getUser(): Flow<UserEntity> {
        return dataStore.data.map { preferences ->
            UserEntity(
                preferences[ID_KEY] ?: "",
                preferences[NAME_KEY] ?: "",
                preferences[TOKEN] ?: ""
            )
        }
    }

    suspend fun saveUser(user: UserEntity) {
        dataStore.edit { preferences ->
            preferences[ID_KEY] = user.userId
            preferences[NAME_KEY] = user.userName
            preferences[TOKEN] = user.token
        }
    }

    suspend fun login(token: String){
        dataStore.edit { preferences ->
            preferences[TOKEN] = token
        }
    }

    suspend fun logout(){
        dataStore.edit { preferences ->
            preferences[TOKEN] = ""
        }
    }

    companion object {
        @Volatile
        private var instance: UserPreferences? = null
        private val ID_KEY = stringPreferencesKey("userId")
        private val NAME_KEY = stringPreferencesKey("userName")
        private val TOKEN = stringPreferencesKey("token")
        fun getInstance(dataStore: DataStore<Preferences>): UserPreferences =
            instance ?: synchronized(this) {
                instance ?: UserPreferences(dataStore)
            }.also { instance = it }
    }
}