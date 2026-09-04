package com.abdellahshabat.fatora

/**
 * حالة الشاشة الرئيسية، مبنية من بيانات حقيقية قادمة من Room
 * عبر HomeViewModel. لا يوجد أي بيانات وهمية (Hardcoded) هون.
 */
data class HomeUiState(
    val shopName: String = "محلي",
    val dateLabel: String = "",
    val totalDebts: Double = 0.0,
    val todaySales: Double = 0.0,
    val recentTransactions: List<RecentTransactionUi> = emptyList(),
    val isLoading: Boolean = true
)

/**
 * تمثيل عملية واحدة لعرضها بقائمة "آخر العمليات" بالشاشة الرئيسية.
 * مبنية بدمج Transaction مع اسم العميل المرتبط فيها.
 */
data class RecentTransactionUi(
    val customerName: String,
    val label: String,       // اسم المنتج، أو "دفعة" لو النوع PAYMENT
    val amount: Double,
    val isPositive: Boolean  // true = دفعة (تقلل الدين)، false = دين جديد
)