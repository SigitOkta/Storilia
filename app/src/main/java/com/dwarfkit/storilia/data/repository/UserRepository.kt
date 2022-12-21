package com.dwarfkit.storilia.data.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.liveData
import com.dwarfkit.storilia.data.Resource
import com.dwarfkit.storilia.data.remote.response.LoginResponse
import com.dwarfkit.storilia.data.remote.response.SignUpResponse
import com.dwarfkit.storilia.data.remote.retrofit.ApiService
import com.google.gson.Gson
import okio.IOException
import retrofit2.HttpException

class UserRepository constructor(
    private val apiService: ApiService,
) {
    fun signup(name: String, email: String, password: String): LiveData<Resource<SignUpResponse>> =
        liveData {
            emit(Resource.Loading)
            try {
                val response = apiService.signupUser(name, email, password)
                if (!response.error) emit(Resource.Success(response)) else emit(Resource.Error(
                    response.message))
            } catch (e: HttpException) {
                val responseBody =
                    Gson().fromJson(e.response()?.errorBody()?.string(), SignUpResponse::class.java)
                emit(Resource.Error(responseBody.message))
            } catch (e: IOException) {
                emit(Resource.Error(e.message.toString()))
            }
        }

    fun login(email: String, password: String): LiveData<Resource<LoginResponse>> =
        liveData {
            emit(Resource.Loading)
            try {
                val response = apiService.loginUser(email, password)
                if (!response.error) emit(Resource.Success(response)) else emit(Resource.Error(
                    response.message))
            } catch (e: HttpException) {
                val responseBody =
                    Gson().fromJson(e.response()?.errorBody()?.string(), SignUpResponse::class.java)
                emit(Resource.Error(responseBody.message))
            } catch (e: IOException) {
                emit(Resource.Error(e.message.toString()))
            }
        }

    companion object {
        @Volatile
        private var instance: UserRepository? = null
        fun getInstance(
            apiService: ApiService,
        ): UserRepository =
            instance ?: synchronized(this) {
                instance ?: UserRepository(apiService)
            }.also { instance = it }
    }

}