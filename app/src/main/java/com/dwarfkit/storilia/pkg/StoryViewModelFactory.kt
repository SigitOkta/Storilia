package com.dwarfkit.storilia.pkg

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.dwarfkit.storilia.data.local.datastore.UserPreferences
import com.dwarfkit.storilia.data.repository.StoryRepository
import com.dwarfkit.storilia.di.Injection
import com.dwarfkit.storilia.pkg.home.HomeViewModel

class StoryViewModelFactory(
    private val userPreferences: UserPreferences,
    private val storyRepository: StoryRepository
) : ViewModelProvider.NewInstanceFactory() {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return when {
            modelClass.isAssignableFrom(HomeViewModel::class.java) -> {
                HomeViewModel(userPreferences,storyRepository) as T
            }
           /* modelClass.isAssignableFrom(AddStoryViewModel::class.java) -> {
                AddStoryViewModel(userPreferences,storyRepository) as T
            }
            modelClass.isAssignableFrom(MapViewModel::class.java) -> {
                MapViewModel(userPreferences,storyRepository) as T
            }*/
            else -> throw IllegalArgumentException("Unknown ViewModel class: " + modelClass.name)
        }
    }

    companion object {
        @Volatile
        private var instance: StoryViewModelFactory? = null

        fun getInstance(
            userPreferences: UserPreferences,
            context: Context
        ): StoryViewModelFactory = instance ?: synchronized(this) {
            instance ?: StoryViewModelFactory(
                userPreferences, Injection.provideStoryRepository(context)
            )
        }.also { instance = it }
    }

}