package com.dwarfkit.storilia.di

import com.dwarfkit.storilia.data.remote.retrofit.ApiConfig
import com.dwarfkit.storilia.data.repository.UserRepository

object Injection {
    fun provideUserRepository(): UserRepository {
        val apiService = ApiConfig.getApiService()
        return UserRepository.getInstance(apiService)
    }
}