package com.dwarfkit.storilia.pkg.main

import androidx.lifecycle.*
import com.dwarfkit.storilia.data.local.datastore.UserPreferences
import com.dwarfkit.storilia.data.local.entity.UserEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MainViewModel(
    private val userPreferences: UserPreferences,
    /* private val storyRepository: StoryRepository*/
) : ViewModel() {
    fun getUser(): LiveData<UserEntity> {
        return userPreferences.getUser().asLiveData()
    }

    fun logout() {
        viewModelScope.launch(Dispatchers.IO) {
            userPreferences.logout()
        }
    }

    /* fun getAllStories(token : String): LiveData<PagingData<StoryEntity>> =
         storyRepository.getAllStories(token).cachedIn(viewModelScope)*/

}

class MainViewModelFactory private constructor(
    private val userPreferences: UserPreferences
) : ViewModelProvider.NewInstanceFactory() {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(MainViewModel::class.java)) {
            return MainViewModel(userPreferences) as T
        }

        throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
    }

    companion object {
        @Volatile
        private var instance: MainViewModelFactory? = null

        fun getInstance(
            userPreferences: UserPreferences
        ): MainViewModelFactory = instance ?: synchronized(this) {
            instance ?: MainViewModelFactory(userPreferences)
        }
    }
}