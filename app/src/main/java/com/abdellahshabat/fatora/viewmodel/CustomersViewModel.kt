package com.abdellahshabat.fatora.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.abdellahshabat.fatora.customer.CustomerCardUi
import com.abdellahshabat.fatora.customer.CustomersUiState
import com.abdellahshabat.fatora.data.database.entity.TransactionType
import com.abdellahshabat.fatora.data.repository.CustomerRepository
import com.abdellahshabat.fatora.data.repository.TransactionRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/**
 * ViewModel شاشة قائمة العملاء. بيحسب رصيد كل عميل (مجموع ديونه - مجموع دفعاته)
 * ويرتب القائمة تنازلي حسب الرصيد - العميل يلي عليه أكتر دين يطلع فوق،
 * لأنه هاد أكثر شي مفيد لصاحب المحل يشوفه أول ما يفتح الشاشة.
 */
class CustomersViewModel(
    private val customerRepository: CustomerRepository,
    private val transactionRepository: TransactionRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(CustomersUiState())
    val uiState: StateFlow<CustomersUiState> = _uiState.asStateFlow()

    init {
        loadCustomers()
    }

    fun loadCustomers() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)

            val customers = customerRepository.getAllCustomers()
            val transactions = transactionRepository.getAllTransactions()

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

            val cards = customers
                .map { customer ->
                    CustomerCardUi(
                        id = customer.id,
                        name = customer.name,
                        phone = customer.phone,
                        balance = balanceByCustomerId[customer.id] ?: 0.0
                    )
                }
                .sortedWith(compareByDescending<CustomerCardUi> { it.balance }.thenBy { it.name })

            _uiState.value = CustomersUiState(
                customers = cards,
                isLoading = false
            )
        }
    }
}

class CustomersViewModelFactory(
    private val customerRepository: CustomerRepository,
    private val transactionRepository: TransactionRepository
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(CustomersViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return CustomersViewModel(customerRepository, transactionRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
    }
}