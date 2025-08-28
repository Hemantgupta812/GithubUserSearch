package com.example.githubusersearch.ui.screens.profile

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import coil.network.HttpException
import com.example.githubusersearch.data.model.RepoDto
import com.example.githubusersearch.data.model.UserDto
import com.example.githubusersearch.data.repository.GithubRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
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

    val userRepos: Flow<PagingData<RepoDto>> = repository.getUserRepos(username).cachedIn(viewModelScope)

    init {
        // Initial fetch when ViewModel is created if not already handled by LaunchedEffect
        // This makes sure that if the ViewModel survives config changes, we don't refetch
        if (_userProfile.value == UserProfileUiState.Idle) {
            fetchUserProfile(username)
        }
    }

    fun fetchUserProfile(username: String) {
        _userProfile.value = UserProfileUiState.Loading
        viewModelScope.launch {
            val result = repository.getUser(username)
            result.onSuccess { user ->
                _userProfile.value = UserProfileUiState.Success(user)
            }.onFailure { throwable ->
                _userProfile.value = when (throwable) {
                    is HttpException -> {
                        if (throwable.response.code == 404) {
                            UserProfileUiState.Error("User not found: $username")
                        } else {
                            UserProfileUiState.Error("Network error: ${throwable.message}")
                        }
                    }
                    is IOException -> {
                        UserProfileUiState.Error("Network error: Check your internet connection")
                    }
                    else -> {
                        UserProfileUiState.Error("An unexpected error occurred: ${throwable.localizedMessage}")
                    }
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