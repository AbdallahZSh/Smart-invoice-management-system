package com.abdellahshabat.fatora

/** حالة شاشة الإعدادات/بروفايل المحل. */
data class SettingsUiState(
    val shopName: String = "",
    val ownerPhone: String = "",
    val totalCustomers: Int = 0,
    val totalOutstandingDebt: Double = 0.0,
    val isSaving: Boolean = false,
    val savedMessage: String? = null,
    val isLoading: Boolean = true
)