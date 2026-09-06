package com.abdellahshabat.fatora

/**
 * نفس فكرة AddDebtUiState بالضبط، بس بدون حقل المنتج (الدفعة ما إلها منتج).
 */
data class AddPaymentUiState(
    val customerName: String = "",
    val amountText: String = "",
    val isSaving: Boolean = false,
    val errorMessage: String? = null,
    val saveSuccess: Boolean = false,
    val clarification: ClarificationUiState? = null
) {
    val isSaveEnabled: Boolean
        get() = customerName.isNotBlank() &&
                amountText.toDoubleOrNull() != null &&
                !isSaving
}