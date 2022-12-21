package com.dwarfkit.storilia.data.remote.retrofit

import com.dwarfkit.storilia.data.remote.response.LoginResponse
import com.dwarfkit.storilia.data.remote.response.SignUpResponse
import retrofit2.http.*

interface ApiService {
    @FormUrlEncoded
    @POST("login")
    suspend fun loginUser(
        @Field("email") email: String,
        @Field("password") password: String
    ): LoginResponse

    @FormUrlEncoded
    @POST("register")
    suspend fun signupUser(
        @Field("name") name: String,
        @Field("email") email: String,
        @Field("password") password: String
    ): SignUpResponse
}