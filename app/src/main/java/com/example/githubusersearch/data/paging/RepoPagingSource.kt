package com.example.githubusersearch.data.paging

import androidx.paging.PagingSource
import androidx.paging.PagingState
import coil.network.HttpException
import com.example.githubusersearch.data.model.RepoDto
import com.example.githubusersearch.data.remote.GithubApiService
import java.io.IOException

class RepoPagingSource(
    private val apiService: GithubApiService,
    private val username: String
) : PagingSource<Int, RepoDto>() {

    override fun getRefreshKey(state: PagingState<Int, RepoDto>): Int? {
        return state.anchorPosition?.let { anchorPosition ->
            state.closestPageToPosition(anchorPosition)?.prevKey?.plus(1)
                ?: state.closestPageToPosition(anchorPosition)?.nextKey?.minus(1)
        }
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, RepoDto> {
        return try {
            val position = params.key ?: 1 // Start page
            val response = apiService.getUserRepos(username, position, params.loadSize)

            LoadResult.Page(
                data = response,
                prevKey = if (position == 1) null else position - 1,
                nextKey = if (response.isEmpty()) null else position + 1
            )
        } catch (exception: IOException) {
            LoadResult.Error(exception)
        } catch (exception: HttpException) {
            LoadResult.Error(exception)
        }
    }
}