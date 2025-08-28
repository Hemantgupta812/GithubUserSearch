package com.example.githubusersearch.data.repository

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.example.githubusersearch.data.model.RepoDto
import com.example.githubusersearch.data.model.UserDto
import com.example.githubusersearch.data.paging.RepoPagingSource
import com.example.githubusersearch.data.remote.GithubApiService
import kotlinx.coroutines.flow.Flow
import retrofit2.HttpException
import javax.inject.Inject

class GithubRepository @Inject constructor(
    private val apiService: GithubApiService
) {
    suspend fun getUser(username: String): Result<UserDto?> {
        return try {
            val response = apiService.getUser(username)
            when {
                response.isSuccessful -> Result.success(response.body())
                response.code() == 404 -> Result.success(null) // treat as "user not found"
                else -> Result.failure(HttpException(response))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    fun getUserRepos(username: String): Flow<PagingData<RepoDto>> {
        return Pager(
            config = PagingConfig(
                pageSize = 20, // Number of items to load per page
                enablePlaceholders = false,
                initialLoadSize = 20
            ),
            pagingSourceFactory = { RepoPagingSource(apiService, username) }
        ).flow
    }
}