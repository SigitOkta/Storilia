package com.dwarfkit.storilia.pkg.home

import androidx.lifecycle.*
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.dwarfkit.storilia.data.local.datastore.UserPreferences
import com.dwarfkit.storilia.data.local.entity.StoryEntity
import com.dwarfkit.storilia.data.local.entity.UserEntity
import com.dwarfkit.storilia.data.repository.StoryRepository

class HomeViewModel(
    private val userPreferences: UserPreferences,
    private val storyRepository: StoryRepository
) : ViewModel() {

    fun getUser(): LiveData<UserEntity> {
        return userPreferences.getUser().asLiveData()
    }

    fun getAllStories(token : String): LiveData<PagingData<StoryEntity>> =
        storyRepository.getAllStories(token).cachedIn(viewModelScope)
}
