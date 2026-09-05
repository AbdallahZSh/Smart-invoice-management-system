package com.abdellahshabat.fatora.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.abdellahshabat.fatora.AddPaymentUiState
import com.abdellahshabat.fatora.domain.usecase.AddPaymentUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/**
 * نفس فكرة AddDebtViewModel بالضبط بس لعملية AddPaymentUseCase.
 */
class AddPaymentViewModel(
    private val addPaymentUseCase: AddPaymentUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(AddPaymentUiState())
    val uiState: StateFlow<AddPaymentUiState> = _uiState.asStateFlow()

    fun onCustomerNameChange(value: String) {
        _uiState.value = _uiState.value.copy(customerName = value, errorMessage = null)
    }

    fun onAmountChange(value: String) {
        _uiState.value = _uiState.value.copy(amountText = value, errorMessage = null)
    }

    fun save() {
        val state = _uiState.value
        val amount = state.amountText.toDoubleOrNull() ?: return

        viewModelScope.launch {
            _uiState.value = state.copy(isSaving = true, errorMessage = null)

            val result = addPaymentUseCase(
                customerName = state.customerName.trim(),
                amount = amount
            )

            result.fold(
                onSuccess = {
                    _uiState.value = _uiState.value.copy(
                        isSaving = false,
                        saveSuccess = true
                    )
                },
                onFailure = { error ->
                    _uiState.value = _uiState.value.copy(
                        isSaving = false,
                        errorMessage = mapErrorToMessage(error)
                    )
                }
            )
        }
    }

    private fun mapErrorToMessage(error: Throwable): String {
        return when (error.message) {
            "AMBIGUOUS_CUSTOMER" ->
                "في أكثر من عميل بنفس الاسم - رح نضيف قريباً شاشة تختار منها بالتحديد. لهلق جرب اسم أدق."
            else -> error.message ?: "صار في خطأ غير متوقع، جرب مرة كمان"
        }
    }
}

class AddPaymentViewModelFactory(
    private val addPaymentUseCase: AddPaymentUseCase
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(AddPaymentViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return AddPaymentViewModel(addPaymentUseCase) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
    }
}