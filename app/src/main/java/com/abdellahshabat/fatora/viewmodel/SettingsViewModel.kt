package com.abdellahshabat.fatora.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.abdellahshabat.fatora.SettingsUiState
import com.abdellahshabat.fatora.data.database.entity.TransactionType
import com.abdellahshabat.fatora.data.repository.CustomerRepository
import com.abdellahshabat.fatora.data.repository.TransactionRepository
import com.abdellahshabat.fatora.util.ShopPreferences
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/**
 * ViewModel شاشة الإعدادات - بيانات المحل (مخزنة محلياً عبر ShopPreferences)
 * + إحصائيات سريعة (عدد العملاء، إجمالي الديون المستحقة) + خيار تصفير البيانات.
 */
class SettingsViewModel(
    private val shopPreferences: ShopPreferences,
    private val customerRepository: CustomerRepository,
    private val transactionRepository: TransactionRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(SettingsUiState())
    val uiState: StateFlow<SettingsUiState> = _uiState.asStateFlow()

    init {
        load()
    }

    fun load() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)

            val customers = customerRepository.getAllCustomers()
            val transactions = transactionRepository.getAllTransactions()

            val totalDebt = transactions
                .filter { it.type == TransactionType.DEBT }
                .sumOf { it.amount }
            val totalPayments = transactions
                .filter { it.type == TransactionType.PAYMENT }
                .sumOf { it.amount }

            _uiState.value = SettingsUiState(
                shopName = shopPreferences.getShopName(),
                ownerPhone = shopPreferences.getOwnerPhone(),
                totalCustomers = customers.size,
                totalOutstandingDebt = (totalDebt - totalPayments).coerceAtLeast(0.0),
                isLoading = false
            )
        }
    }

    fun onShopNameChange(value: String) {
        _uiState.value = _uiState.value.copy(shopName = value, savedMessage = null)
    }

    fun onOwnerPhoneChange(value: String) {
        _uiState.value = _uiState.value.copy(ownerPhone = value, savedMessage = null)
    }

    fun saveProfile() {
        val state = _uiState.value

        viewModelScope.launch {
            _uiState.value = state.copy(isSaving = true)

            shopPreferences.saveShopProfile(
                shopName = state.shopName.trim(),
                ownerPhone = state.ownerPhone.trim()
            )

            _uiState.value = _uiState.value.copy(
                isSaving = false,
                savedMessage = "تم حفظ بيانات المحل"
            )
        }
    }

    /** حذف كل العملاء وكل العمليات نهائياً - لتصفير التطبيق بالكامل (للاستخدام الحذر). */
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

class SettingsViewModelFactory(
    private val shopPreferences: ShopPreferences,
    private val customerRepository: CustomerRepository,
    private val transactionRepository: TransactionRepository
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(SettingsViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return SettingsViewModel(shopPreferences, customerRepository, transactionRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
    }
}