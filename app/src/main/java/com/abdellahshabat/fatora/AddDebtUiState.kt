package com.abdellahshabat.fatora

/**
 * حالة شاشة إضافة دين - نسخة بسيطة (نص فقط، بدون صوت أو AI بعد)
 * لاختبار أن AddDebtUseCase شغال فعلياً من واجهة حقيقية.
 */
data class AddDebtUiState(
    val customerName: String = "",
    val product: String = "",
    val amountText: String = "",
    val isSaving: Boolean = false,
    val errorMessage: String? = null,
    val saveSuccess: Boolean = false,
    val clarification: ClarificationUiState? = null   // null = فورم عادي، غير null = عرض شاشة التوضيح

) {
    /** الزر يفعل بس لو الاسم والمبلغ معبّيين، والمبلغ رقم صحيح. */
    val isSaveEnabled: Boolean
        get() = customerName.isNotBlank() &&
                amountText.toDoubleOrNull() != null &&
                !isSaving
}