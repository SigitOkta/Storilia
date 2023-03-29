package com.dwarfkit.storilia.pkg.map

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import com.dwarfkit.storilia.data.local.datastore.UserPreferences
import com.dwarfkit.storilia.data.local.entity.UserEntity
import com.dwarfkit.storilia.data.repository.StoryRepository

class MapViewModel(
    private val userPreferences: UserPreferences,
    private val storyRepository: StoryRepository
) : ViewModel() {
    fun getUser(): LiveData<UserEntity> {
        return userPreferences.getUser().asLiveData()
    }
    fun getAllStoriesWithLocation(token: String) = storyRepository.getAllStoriesWithLocation(token)
}