package com.abdellahshabat.fatora.screen2

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.abdellahshabat.fatora.data.database.entity.Transaction
import com.abdellahshabat.fatora.data.database.entity.TransactionType
import com.abdellahshabat.fatora.data.repository.CustomerRepository
import com.abdellahshabat.fatora.data.repository.TransactionRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

/**
 * ViewModel شاشة كشف المبيعات. بيقرا كل العمليات مرة وحدة من Room،
 * وبعدين كل فلترة (يوم/شهر/نوع) بتصير محلياً بالذاكرة بدون رجوع لقاعدة البيانات -
 * كافي جداً لحجم بيانات محل صغير بمرحلة الـ MVP.
 */
class SalesReportViewModel(
    private val customerRepository: CustomerRepository,
    private val transactionRepository: TransactionRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(SalesReportUiState())
    val uiState: StateFlow<SalesReportUiState> = _uiState.asStateFlow()

    private var allTransactions: List<Transaction> = emptyList()
    private var customerNameById: Map<String, String> = emptyMap()
    private var selectedDateMillis: Long = System.currentTimeMillis()

    init {
        loadAndApply()
    }

    fun onFilterModeChange(mode: ReportFilterMode) {
        _uiState.value = _uiState.value.copy(filterMode = mode)
        applyFilters()
    }

    fun onTypeFilterChange(filter: ReportTypeFilter) {
        _uiState.value = _uiState.value.copy(typeFilter = filter)
        applyFilters()
    }

    /** ينقل الفلترة يوم واحد أو شهر واحد للخلف، حسب الفلترة الحالية. */
    fun onPreviousClick() {
        shiftSelectedDate(step = -1)
    }

    /** ينقل الفلترة يوم واحد أو شهر واحد للأمام، حسب الفلترة الحالية. */
    fun onNextClick() {
        shiftSelectedDate(step = 1)
    }

    private fun shiftSelectedDate(step: Int) {
        val calendar = Calendar.getInstance().apply { timeInMillis = selectedDateMillis }
        val field = if (_uiState.value.filterMode == ReportFilterMode.DAY) {
            Calendar.DAY_OF_MONTH
        } else {
            Calendar.MONTH
        }
        calendar.add(field, step)
        selectedDateMillis = calendar.timeInMillis
        applyFilters()
    }

    fun loadAndApply() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)

            allTransactions = transactionRepository.getAllTransactions()
            val customers = customerRepository.getAllCustomers()
            customerNameById = customers.associate { it.id to it.name }

            applyFilters()
        }
    }

    private fun applyFilters() {
        val state = _uiState.value
        val calendar = Calendar.getInstance().apply { timeInMillis = selectedDateMillis }

        val filteredByDate = allTransactions.filter { transaction ->
            val transactionCal = Calendar.getInstance().apply { timeInMillis = transaction.createdAt }
            if (state.filterMode == ReportFilterMode.DAY) {
                transactionCal.get(Calendar.YEAR) == calendar.get(Calendar.YEAR) &&
                        transactionCal.get(Calendar.DAY_OF_YEAR) == calendar.get(Calendar.DAY_OF_YEAR)
            } else {
                transactionCal.get(Calendar.YEAR) == calendar.get(Calendar.YEAR) &&
                        transactionCal.get(Calendar.MONTH) == calendar.get(Calendar.MONTH)
            }
        }

        val filteredByType = when (state.typeFilter) {
            ReportTypeFilter.ALL -> filteredByDate
            ReportTypeFilter.DEBT -> filteredByDate.filter { it.type == TransactionType.DEBT }
            ReportTypeFilter.PAYMENT -> filteredByDate.filter { it.type == TransactionType.PAYMENT }
        }

        val rows = filteredByType
            .sortedByDescending { it.createdAt }
            .map { transaction ->
                ReportRowUi(
                    customerName = customerNameById[transaction.customerId] ?: "عميل محذوف",
                    product = transaction.product
                        ?: if (transaction.type == TransactionType.PAYMENT) "دفعة" else "-",
                    amount = transaction.amount,
                    timeLabel = SimpleDateFormat("h:mm a", Locale("ar")).format(Date(transaction.createdAt)),
                    type = transaction.type
                )
            }

        val totalDebt = filteredByType.filter { it.type == TransactionType.DEBT }.sumOf { it.amount }
        val totalPayments = filteredByType.filter { it.type == TransactionType.PAYMENT }.sumOf { it.amount }

        _uiState.value = state.copy(
            dateLabel = formatDateLabel(calendar, state.filterMode),
            rows = rows,
            totalCount = rows.size,
            netAmount = totalDebt - totalPayments,
            isLoading = false
        )
    }

    private fun formatDateLabel(calendar: Calendar, mode: ReportFilterMode): String {
        val date = calendar.time
        return if (mode == ReportFilterMode.DAY) {
            SimpleDateFormat("EEEE، d MMMM yyyy", Locale("ar")).format(date)
        } else {
            SimpleDateFormat("MMMM yyyy", Locale("ar")).format(date)
        }
    }
}

class SalesReportViewModelFactory(
    private val customerRepository: CustomerRepository,
    private val transactionRepository: TransactionRepository
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(SalesReportViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return SalesReportViewModel(customerRepository, transactionRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
    }
}