package com.abdellahshabat.fatora.screen1

/**
 * حالة شاشة قائمة الفواتير. كل بطاقة تمثل عملية واحدة، لكن معها
 * أيضاً "الرصيد الحالي الكلي" لنفس العميل لحد هلق - مو مجموع وقت
 * تلك العملية بالذات، فلهيك نفس الرقم بيتكرر بكل بطاقات نفس العميل
 * وبيتحدث تلقائياً مع أي عملية جديدة.
 */
data class InvoicesUiState(
    val invoices: List<InvoiceCardUi> = emptyList(),
    val isLoading: Boolean = true
)

data class InvoiceCardUi(
    val customerName: String,
    val label: String,           // اسم المنتج، أو "دفعة" لو النوع PAYMENT
    val amount: Double,
    val isPositive: Boolean,     // true = دفعة (تقلل الدين)، false = دين جديد
    val dateLabel: String,       // "اليوم، 5:41 م" / "أمس، ..." / تاريخ كامل
    val customerTotalBalance: Double
)