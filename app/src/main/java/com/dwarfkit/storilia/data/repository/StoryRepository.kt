package com.dwarfkit.storilia.data.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.liveData
import androidx.paging.*
import com.dwarfkit.storilia.data.Resource
import com.dwarfkit.storilia.data.StoryRemoteMediator
import com.dwarfkit.storilia.data.local.entity.StoryEntity
import com.dwarfkit.storilia.data.local.room.StoryDatabase
import com.dwarfkit.storilia.data.remote.response.AddStoryResponse
import com.dwarfkit.storilia.data.remote.response.StoriesResponse
import com.dwarfkit.storilia.data.remote.retrofit.ApiService
import com.google.gson.Gson
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.HttpException
import java.io.IOException

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

    fun addNewStory(
        token: String,
        description: RequestBody,
        imageMultipart: MultipartBody.Part,
        lat: RequestBody?,
        lon: RequestBody?,
    ): LiveData<Resource<AddStoryResponse>> = liveData {
        emit(Resource.Loading)
        try {
            val response = apiService.addStory("Bearer $token",description,imageMultipart,lat,lon)
            if (!response.error) {
                emit(Resource.Success(response))
            } else {
                emit(Resource.Error(response.message))
            }
        }catch (e: HttpException) {
            val responseBody =
                Gson().fromJson(e.response()?.errorBody()?.string(), StoriesResponse::class.java)
            emit(Resource.Error(responseBody.message))
        } catch (e: IOException) {
            emit(Resource.Error(e.message.toString()))
        } catch (e: Exception) {
            emit(Resource.Error(e.message.toString()))
        }
    }

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