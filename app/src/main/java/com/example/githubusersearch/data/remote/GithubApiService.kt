package com.example.githubusersearch.data.remote

import com.example.githubusersearch.data.model.RepoDto
import com.example.githubusersearch.data.model.UserDto
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface GithubApiService {

    @GET("users/{username}")
    suspend fun getUser(@Path("username") username: String): Response<UserDto>

    @GET("users/{username}/repos")
    suspend fun getUserRepos(
        @Path("username") username: String,
        @Query("page") page: Int,
        @Query("per_page") perPage: Int
    ): List<RepoDto>
}