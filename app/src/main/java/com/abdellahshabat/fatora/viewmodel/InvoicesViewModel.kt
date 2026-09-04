package com.abdellahshabat.fatora.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.abdellahshabat.fatora.screen1.InvoiceCardUi
import com.abdellahshabat.fatora.screen1.InvoicesUiState
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
 * ViewModel شاشة الفواتير. بيبني قائمة العمليات مرتبة تنازلي (الأحدث فوق)،
 * ومع كل عملية بيحسب "الرصيد الحالي الكلي" لنفس العميل (مش مجموع وقت
 * تلك العملية بالذات) - عشان المستخدم يشوف فوراً كم على هذا العميل
 * إجمالاً وهو عم يقلب بسجل العمليات.
 */
class InvoicesViewModel(
    private val customerRepository: CustomerRepository,
    private val transactionRepository: TransactionRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(InvoicesUiState())
    val uiState: StateFlow<InvoicesUiState> = _uiState.asStateFlow()

    init {
        loadInvoices()
    }

    fun loadInvoices() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)

            val transactions = transactionRepository.getAllTransactions()
            val customers = customerRepository.getAllCustomers()
            val customerNameById = customers.associate { it.id to it.name }

            // الرصيد الحالي الكلي لكل عميل = مجموع كل الديون - مجموع كل الدفعات.
            val balanceByCustomerId = transactions
                .groupBy { it.customerId }
                .mapValues { (_, customerTransactions) ->
                    val totalDebt = customerTransactions
                        .filter { it.type == TransactionType.DEBT }
                        .sumOf { it.amount }

                    val totalPayments = customerTransactions
                        .filter { it.type == TransactionType.PAYMENT }
                        .sumOf { it.amount }

                    (totalDebt - totalPayments).coerceAtLeast(0.0)
                }

            val invoices = transactions
                .sortedByDescending { it.createdAt }
                .map { transaction ->
                    InvoiceCardUi(
                        customerName = customerNameById[transaction.customerId] ?: "عميل محذوف",
                        label = transaction.product
                            ?: if (transaction.type == TransactionType.PAYMENT) "دفعة" else "-",
                        amount = transaction.amount,
                        isPositive = transaction.type == TransactionType.PAYMENT,
                        dateLabel = formatRelativeDayLabel(transaction.createdAt),
                        customerTotalBalance = balanceByCustomerId[transaction.customerId] ?: 0.0
                    )
                }

            _uiState.value = InvoicesUiState(
                invoices = invoices,
                isLoading = false
            )
        }
    }

    private fun formatRelativeDayLabel(timestampMillis: Long): String {
        val timeText = SimpleDateFormat("h:mm a", Locale("ar")).format(Date(timestampMillis))

        val transactionCal = Calendar.getInstance().apply { timeInMillis = timestampMillis }
        val todayCal = Calendar.getInstance()

        val transactionDayKey = transactionCal.get(Calendar.YEAR) * 1000 + transactionCal.get(Calendar.DAY_OF_YEAR)
        val todayDayKey = todayCal.get(Calendar.YEAR) * 1000 + todayCal.get(Calendar.DAY_OF_YEAR)
        val diffDays = todayDayKey - transactionDayKey

        return when {
            diffDays <= 0 -> "اليوم، $timeText"
            diffDays == 1 -> "أمس، $timeText"
            diffDays == 2 -> "قبل يومين، $timeText"
            diffDays in 3..6 -> "قبل $diffDays أيام، $timeText"
            else -> {
                val dateText = SimpleDateFormat("d/M/yyyy", Locale("ar")).format(Date(timestampMillis))
                "$dateText، $timeText"
            }
        }
    }
}

class InvoicesViewModelFactory(
    private val customerRepository: CustomerRepository,
    private val transactionRepository: TransactionRepository
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(InvoicesViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return InvoicesViewModel(customerRepository, transactionRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
    }
}