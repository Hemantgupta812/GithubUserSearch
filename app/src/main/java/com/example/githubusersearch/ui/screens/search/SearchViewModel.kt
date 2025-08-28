package com.example.githubusersearch.ui.screens.search

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

@HiltViewModel
class SearchViewModel @Inject constructor() : ViewModel() {

    private val _username = MutableStateFlow("")
    val username: StateFlow<String> = _username

    fun onUsernameChange(newUsername: String) {
        _username.value = newUsername
    }
}