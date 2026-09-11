package com.example.presentation.settings

import com.example.domain.model.UserPreferences

data class SettingsUiState(
    val preferences: UserPreferences = UserPreferences(),
    val toastMessage: String? = null
)
