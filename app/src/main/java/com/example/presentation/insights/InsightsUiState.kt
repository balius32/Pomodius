package com.example.presentation.insights

import com.example.domain.model.InsightsData

data class InsightsUiState(
    val data: InsightsData = InsightsData(),
    val isLoading: Boolean = false,
    val exportedToastMessage: String? = null
)
