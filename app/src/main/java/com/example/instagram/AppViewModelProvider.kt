package com.example.instagram

import android.app.Application
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory
import androidx.lifecycle.createSavedStateHandle
import androidx.lifecycle.viewmodel.CreationExtras
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.instagram.messages.ChatViewModel
import com.example.instagram.profile.ProfileViewModel

object AppViewModelProvider {
    val Factory = viewModelFactory {
        initializer {
            ProfileViewModel(
                this.createSavedStateHandle()
            )
        }
        initializer {
            ChatViewModel(
                this.createSavedStateHandle()
            )
        }

    }
}