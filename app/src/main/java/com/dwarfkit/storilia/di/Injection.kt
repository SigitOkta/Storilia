package com.dwarfkit.storilia.di

import android.content.Context
import com.dwarfkit.storilia.data.local.room.StoryDatabase
import com.dwarfkit.storilia.data.remote.retrofit.ApiConfig
import com.dwarfkit.storilia.data.repository.StoryRepository
import com.dwarfkit.storilia.data.repository.UserRepository

object Injection {
    fun provideUserRepository(): UserRepository {
        val apiService = ApiConfig.getApiService()
        return UserRepository.getInstance(apiService)
    }
    fun provideStoryRepository(context: Context): StoryRepository {
        val apiService = ApiConfig.getApiService()
        val database = StoryDatabase.getInstance(context)
        return StoryRepository.getInstance(apiService,database)
    }
}