package com.example.githubusersearch.ui.screens.profile

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.example.githubusersearch.data.model.RepoDto
import com.example.githubusersearch.data.model.UserDto
import com.example.githubusersearch.data.repository.GithubRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import retrofit2.HttpException
import java.io.IOException
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val repository: GithubRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val username: String = checkNotNull(savedStateHandle["username"])

    private val _userProfile = MutableStateFlow<UserProfileUiState>(UserProfileUiState.Idle)
    val userProfile: StateFlow<UserProfileUiState> = _userProfile

    val userRepos: Flow<PagingData<RepoDto>> =
        repository.getUserRepos(username).cachedIn(viewModelScope)

    init {
        // Initial fetch when ViewModel is created if not already handled by LaunchedEffect
        // This makes sure that if the ViewModel survives config changes, we don't refetch
        if (_userProfile.value == UserProfileUiState.Idle) {
            fetchUserProfile(username)
        }
    }

    // ProfileViewModel.kt
    fun fetchUserProfile(username: String) {
        _userProfile.value = UserProfileUiState.Loading

        viewModelScope.launch {
            val result = repository.getUser(username)

            result.onSuccess { user ->
                if (user == null) {
                    _userProfile.value = UserProfileUiState.Error("User not found: '$username'")
                } else {
                    _userProfile.value = UserProfileUiState.Success(user)
                }
            }.onFailure { throwable ->
                _userProfile.value = when (throwable) {
                    is IOException -> UserProfileUiState.Error("Network error: Check your internet connection")
                    is HttpException -> UserProfileUiState.Error("Network error (${throwable.code()}): ${throwable.message}")
                    else -> UserProfileUiState.Error("Unexpected error: ${throwable.localizedMessage ?: "Unknown error"}")
                }
            }
        }
    }


}

sealed class UserProfileUiState {
    object Idle : UserProfileUiState()
    object Loading : UserProfileUiState()
    data class Success(val user: UserDto) : UserProfileUiState()
    data class Error(val message: String) : UserProfileUiState()
}