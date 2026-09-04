package com.abdellahshabat.fatora.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.abdellahshabat.fatora.AddDebtUiState
import com.abdellahshabat.fatora.domain.usecase.AddDebtUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/**
 * ViewModel شاشة إضافة دين - أول شاشة بتكتب فعلياً بقاعدة البيانات
 * (بدل ما نكتفي بالقراءة زي HomeViewModel).
 *
 * كل منطق التحقق (الاسم فاضي، المبلغ سالب، تكرار اسم العميل) موجود
 * أصلاً جوا AddDebtUseCase - هاد الـ ViewModel بس بيوصل حالة الشاشة فيه.
 */
class AddDebtViewModel(
    private val addDebtUseCase: AddDebtUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(AddDebtUiState())
    val uiState: StateFlow<AddDebtUiState> = _uiState.asStateFlow()

    fun onCustomerNameChange(value: String) {
        _uiState.value = _uiState.value.copy(customerName = value, errorMessage = null)
    }

    fun onProductChange(value: String) {
        _uiState.value = _uiState.value.copy(product = value, errorMessage = null)
    }

    fun onAmountChange(value: String) {
        _uiState.value = _uiState.value.copy(amountText = value, errorMessage = null)
    }

    fun save() {
        val state = _uiState.value
        val amount = state.amountText.toDoubleOrNull() ?: return

        viewModelScope.launch {
            _uiState.value = state.copy(isSaving = true, errorMessage = null)

            val result = addDebtUseCase(
                customerName = state.customerName.trim(),
                product = state.product.trim().ifBlank { null },
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

class AddDebtViewModelFactory(
    private val addDebtUseCase: AddDebtUseCase
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(AddDebtViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return AddDebtViewModel(addDebtUseCase) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
    }
}