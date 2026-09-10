package com.abdellahshabat.fatora.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.abdellahshabat.fatora.StorageDataUiState
import com.abdellahshabat.fatora.data.repository.CustomerRepository
import com.abdellahshabat.fatora.data.repository.TransactionRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class StorageDataViewModel(
    private val customerRepository: CustomerRepository,
    private val transactionRepository: TransactionRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(StorageDataUiState())
    val uiState: StateFlow<StorageDataUiState> = _uiState.asStateFlow()

    init {
        load()
    }

    fun load() {
        viewModelScope.launch {
            val customers = customerRepository.getAllCustomers()
            val transactions = transactionRepository.getAllTransactions()
            _uiState.value = StorageDataUiState(
                totalCustomers = customers.size,
                totalTransactions = transactions.size,
                isLoading = false
            )
        }
    }

    fun resetAllData() {
        viewModelScope.launch {
            val customers = customerRepository.getAllCustomers()
            customers.forEach { customer ->
                transactionRepository.deleteTransactionsForCustomer(customer.id)
                customerRepository.deleteCustomer(customer.id)
            }
            load()
        }
    }
}

class StorageDataViewModelFactory(
    private val customerRepository: CustomerRepository,
    private val transactionRepository: TransactionRepository
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(StorageDataViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return StorageDataViewModel(customerRepository, transactionRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
    }
}