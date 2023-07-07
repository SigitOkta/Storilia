package com.dwarfkit.storilia.pkg.splash

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.dwarfkit.storilia.data.local.datastore.UserPreferences
import com.dwarfkit.storilia.data.local.entity.UserEntity
import com.dwarfkit.storilia.pkg.main.MainViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class SplashScreenViewModel(
    private val userPreferences: UserPreferences,
) : ViewModel() {

    fun getUser(): LiveData<UserEntity> {
        return userPreferences.getUser().asLiveData()
    }

    fun logout() {
        viewModelScope.launch(Dispatchers.IO) {
            userPreferences.logout()
        }
    }
}


class SplashScreenViewModelFactory private constructor(
    private val userPreferences: UserPreferences
) : ViewModelProvider.NewInstanceFactory() {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(SplashScreenViewModel::class.java)) {
            return SplashScreenViewModel(userPreferences) as T
        }

        throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
    }

    companion object {
        @Volatile
        private var instance: SplashScreenViewModelFactory? = null

        fun getInstance(
            userPreferences: UserPreferences
        ): SplashScreenViewModelFactory = instance ?: synchronized(this) {
            instance ?: SplashScreenViewModelFactory(userPreferences)
        }
    }
}