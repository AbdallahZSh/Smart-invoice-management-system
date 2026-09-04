package com.abdellahshabat.fatora.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.abdellahshabat.fatora.HomeUiState
import com.abdellahshabat.fatora.RecentTransactionUi
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
 * ViewModel الشاشة الرئيسية. يقرأ العملاء والعمليات فعلياً من Room
 * عبر الـ Repositories، ويبني منهم HomeUiState جاهزة للعرض.
 *
 * لا يوجد أي بيانات وهمية هون - كل رقم بيطلع من قاعدة البيانات الحقيقية.
 */
class HomeViewModel(
    private val customerRepository: CustomerRepository,
    private val transactionRepository: TransactionRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    init {
        loadHomeData()
    }

    /** يُعاد استدعاؤها بعد أي عملية جديدة (دين/دفعة) عشان تحدّث الأرقام. */
    fun loadHomeData() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)

            val transactions = transactionRepository.getAllTransactions()
            val customers = customerRepository.getAllCustomers()
            val customerNameById = customers.associate { it.id to it.name }

            val startOfToday = startOfTodayMillis()

            val todaySales = transactions
                .filter { it.type == TransactionType.DEBT && it.createdAt >= startOfToday }
                .sumOf { it.amount }

            val totalDebt = transactions
                .filter { it.type == TransactionType.DEBT }
                .sumOf { it.amount }

            val totalPayments = transactions
                .filter { it.type == TransactionType.PAYMENT }
                .sumOf { it.amount }

            val outstandingDebt = (totalDebt - totalPayments).coerceAtLeast(0.0)

            val recentTransactions = transactions
                .sortedByDescending { it.createdAt }
                .take(5)
                .map { transaction ->
                    RecentTransactionUi(
                        customerName = customerNameById[transaction.customerId] ?: "عميل محذوف",
                        label = transaction.product
                            ?: if (transaction.type == TransactionType.PAYMENT) "دفعة" else "-",
                        amount = transaction.amount,
                        isPositive = transaction.type == TransactionType.PAYMENT
                    )
                }

            _uiState.value = HomeUiState(
                dateLabel = formatTodayLabel(),
                totalDebts = outstandingDebt,
                todaySales = todaySales,
                recentTransactions = recentTransactions,
                isLoading = false
            )
        }
    }

    private fun startOfTodayMillis(): Long {
        val calendar = Calendar.getInstance()
        calendar.set(Calendar.HOUR_OF_DAY, 0)
        calendar.set(Calendar.MINUTE, 0)
        calendar.set(Calendar.SECOND, 0)
        calendar.set(Calendar.MILLISECOND, 0)
        return calendar.timeInMillis
    }

    private fun formatTodayLabel(): String {
        val formatter = SimpleDateFormat("EEEE، d MMMM", Locale("ar"))
        return formatter.format(Date())
    }
}

/**
 * Factory بسيطة لبناء HomeViewModel مع الـ Repositories المطلوبة،
 * لأنه HomeViewModel عندها constructor بمعاملات (مش فاضية).
 */
class HomeViewModelFactory(
    private val customerRepository: CustomerRepository,
    private val transactionRepository: TransactionRepository
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(HomeViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return HomeViewModel(customerRepository, transactionRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
    }
}