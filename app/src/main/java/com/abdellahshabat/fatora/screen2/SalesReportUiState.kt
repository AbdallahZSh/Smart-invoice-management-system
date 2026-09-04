package com.abdellahshabat.fatora.screen2

import com.abdellahshabat.fatora.data.database.entity.TransactionType

/** نوع فلترة التاريخ - يوم محدد أو شهر محدد. */
enum class ReportFilterMode { DAY, MONTH }

/** فلترة إضافية على نوع العملية بالجدول. */
enum class ReportTypeFilter { ALL, DEBT, PAYMENT }

/** صف واحد بجدول كشف المبيعات. */
data class ReportRowUi(
    val customerName: String,
    val product: String,
    val amount: Double,
    val timeLabel: String,   // مثلاً "5:41 م"
    val type: TransactionType
)

/**
 * حالة شاشة كشف المبيعات المفلتر. كل البيانات (rows, netAmount, totalCount)
 * محسوبة مسبقاً بالـ ViewModel حسب الفلاتر الحالية - الشاشة بس بتعرض.
 */
data class SalesReportUiState(
    val filterMode: ReportFilterMode = ReportFilterMode.DAY,
    val typeFilter: ReportTypeFilter = ReportTypeFilter.ALL,
    val dateLabel: String = "",       // "الأحد، ٣١ آب ٢٠٢٥" أو "أغسطس ٢٠٢٥" حسب الفلترة
    val rows: List<ReportRowUi> = emptyList(),
    val totalCount: Int = 0,
    val netAmount: Double = 0.0,      // مجموع الديون - مجموع الدفعات بنفس الفترة المفلترة
    val isLoading: Boolean = true
)