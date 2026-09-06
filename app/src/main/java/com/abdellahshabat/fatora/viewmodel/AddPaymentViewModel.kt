package com.abdellahshabat.fatora.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.abdellahshabat.fatora.AddPaymentUiState
import com.abdellahshabat.fatora.ClarificationUiState
import com.abdellahshabat.fatora.CustomerOption
import com.abdellahshabat.fatora.domain.usecase.AddPaymentUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

private const val CREATE_NEW_CUSTOMER_ID = "__NEW__"

/** نفس فكرة AddDebtViewModel بالضبط (تطابق حرفي أولاً، بعدين جزئي) بس لعملية AddPaymentUseCase. */
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
                    if (error.message == "AMBIGUOUS_CUSTOMER") {
                        showClarification(state.customerName.trim())
                    } else {
                        _uiState.value = _uiState.value.copy(
                            isSaving = false,
                            errorMessage = mapErrorToMessage(error)
                        )
                    }
                }
            )
        }
    }

    private fun showClarification(customerName: String) {
        viewModelScope.launch {
            val matches = addPaymentUseCase.findMatchingCustomers(customerName)

            val options = matches.map { customer ->
                CustomerOption(
                    id = customer.id,
                    displayName = if (customer.phone != null) {
                        "${customer.name} - ${customer.phone}"
                    } else {
                        customer.name
                    }
                )
            } + CustomerOption(
                id = CREATE_NEW_CUSTOMER_ID,
                displayName = "➕ عميل جديد باسم \"$customerName\""
            )

            val state = _uiState.value

            _uiState.value = state.copy(
                isSaving = false,
                clarification = ClarificationUiState(
                    heardText = "${state.customerName.trim()} - ${state.amountText} ₪ دفعة",
                    message = "في أسماء تشبه \"$customerName\"",
                    subMessage = "حدد مين بالضبط، أو أضف عميل جديد لو مش أي منهم",
                    duplicateCustomers = options
                )
            )
        }
    }

    fun onClarificationCustomerSelected(option: CustomerOption) {
        val state = _uiState.value
        val clarification = state.clarification ?: return

        _uiState.value = state.copy(
            clarification = clarification.copy(
                selectedCustomerId = option.id,
                isContinueEnabled = true
            )
        )
    }

    fun onClarificationContinue() {
        val state = _uiState.value
        val clarification = state.clarification ?: return
        val customerId = clarification.selectedCustomerId ?: return
        val amount = state.amountText.toDoubleOrNull() ?: return

        viewModelScope.launch {
            val result = if (customerId == CREATE_NEW_CUSTOMER_ID) {
                addPaymentUseCase.createNewCustomerAndAdd(
                    customerName = state.customerName.trim(),
                    amount = amount
                )
            } else {
                addPaymentUseCase.withResolvedCustomer(
                    customerId = customerId,
                    amount = amount
                )
            }

            result.fold(
                onSuccess = {
                    _uiState.value = _uiState.value.copy(
                        clarification = null,
                        saveSuccess = true
                    )
                },
                onFailure = { error ->
                    _uiState.value = _uiState.value.copy(
                        clarification = null,
                        errorMessage = mapErrorToMessage(error)
                    )
                }
            )
        }
    }

    private fun mapErrorToMessage(error: Throwable): String {
        return when (error.message) {
            "AMBIGUOUS_CUSTOMER" ->
                "في أكثر من اسم مشابه - رجاءً حدد العميل من القائمة"
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