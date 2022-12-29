package com.dwarfkit.storilia.data.repository

import androidx.lifecycle.LiveData
import androidx.paging.*
import com.dwarfkit.storilia.data.StoryRemoteMediator
import com.dwarfkit.storilia.data.local.entity.StoryEntity
import com.dwarfkit.storilia.data.local.room.StoryDatabase
import com.dwarfkit.storilia.data.remote.retrofit.ApiService

class StoryRepository constructor(
    private val apiService: ApiService,
    private val storyDatabase: StoryDatabase
) {
    fun getAllStories(token: String): LiveData<PagingData<StoryEntity>> =
        @OptIn(ExperimentalPagingApi::class)
        Pager(
            config = PagingConfig(
                pageSize = 10
            ),
            remoteMediator = StoryRemoteMediator(storyDatabase, apiService, token),
            pagingSourceFactory = {
                storyDatabase.storyDao().getAllStory()
            }
        ).liveData

    companion object {
        @Volatile
        private var instance: StoryRepository? = null
        fun getInstance(
            apiService: ApiService,
            storyDatabase: StoryDatabase
        ): StoryRepository =
            instance ?: synchronized(this) {
                instance ?: StoryRepository(apiService,storyDatabase)
            }.also { instance = it }
    }
}