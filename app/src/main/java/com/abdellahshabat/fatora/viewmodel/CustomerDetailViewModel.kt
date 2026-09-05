package com.abdellahshabat.fatora.viewmodel

import android.content.Context
import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.abdellahshabat.fatora.QueryResponseUiState
import com.abdellahshabat.fatora.RecentTransactionItem
import com.abdellahshabat.fatora.TransactionDirection
import com.abdellahshabat.fatora.data.database.entity.TransactionType
import com.abdellahshabat.fatora.data.repository.CustomerRepository
import com.abdellahshabat.fatora.data.repository.TransactionRepository
import com.abdellahshabat.fatora.util.CustomerPdfExporter
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

/**
 * ViewModel شاشة تفاصيل عميل واحد - أول استخدام حقيقي لـ QueryResponseScreen
 * الموجودة أصلاً بالمشروع (كانت جاهزة بدون بيانات حقيقية توصلها).
 *
 * @param customerId معرف العميل المطلوب عرض تفاصيله
 */
class CustomerDetailViewModel(
    private val customerId: String,
    private val customerRepository: CustomerRepository,
    private val transactionRepository: TransactionRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<QueryResponseUiState?>(null)
    val uiState: StateFlow<QueryResponseUiState?> = _uiState.asStateFlow()

    init {
        loadCustomerDetail()
    }

    fun loadCustomerDetail() {
        viewModelScope.launch {
            val customer = customerRepository.getCustomerById(customerId) ?: return@launch
            val allTransactions = transactionRepository.getCustomerTransactions(customerId)

            val totalDebt = allTransactions
                .filter { it.type == TransactionType.DEBT }
                .sumOf { it.amount }
            val totalPayments = allTransactions
                .filter { it.type == TransactionType.PAYMENT }
                .sumOf { it.amount }
            val balance = (totalDebt - totalPayments).coerceAtLeast(0.0)

            val recentItems = allTransactions
                .sortedByDescending { it.createdAt }
                .take(5)
                .map { transaction ->
                    RecentTransactionItem(
                        label = transaction.product
                            ?: if (transaction.type == TransactionType.PAYMENT) "دفعة" else "-",
                        amount = transaction.amount,
                        direction = if (transaction.type == TransactionType.PAYMENT) {
                            TransactionDirection.PAYMENT
                        } else {
                            TransactionDirection.DEBT
                        }
                    )
                }

            _uiState.value = QueryResponseUiState(
                heardText = "كم على ${customer.name}؟",
                customerName = customer.name,
                balance = balance,
                recentTransactions = recentItems
            )
        }
    }


    /**
     * يصدّر سجل عمليات هذا العميل الحالي كملف PDF ويحفظه بمجلد التنزيلات.
     * بيرجع النتيجة عبر onResult على الـ Main thread عشان الواجهة تقدر تعرض
     * رسالة نجاح/فشل مباشرة (Toast مثلاً).
     */
    fun exportToPdf(context: Context, onResult: (Uri?) -> Unit) {
        val state = _uiState.value
        if (state == null) {
            onResult(null)
            return
        }

        viewModelScope.launch(Dispatchers.IO) {
            val uri = CustomerPdfExporter.exportCustomerHistory(
                context = context,
                customerName = state.customerName,
                balance = state.balance,
                transactions = state.recentTransactions
            )

            withContext(Dispatchers.Main) {
                onResult(uri)
            }
        }
    }
}

class CustomerDetailViewModelFactory(
    private val customerId: String,
    private val customerRepository: CustomerRepository,
    private val transactionRepository: TransactionRepository
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(CustomerDetailViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return CustomerDetailViewModel(
                customerId,
                customerRepository,
                transactionRepository
            ) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
    }
}