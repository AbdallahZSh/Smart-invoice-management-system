package com.abdellahshabat.fatora

/** حالة شاشة النسخ الاحتياطي والاستعادة. */
data class BackupUiState(
    val totalCustomers: Int = 0,
    val totalTransactions: Int = 0,
    val isWorking: Boolean = false,
    val message: String? = null,
    val isError: Boolean = false,
    val isLoading: Boolean = true
)