package com.abdellahshabat.fatora.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.abdellahshabat.fatora.AddDebtUiState
import com.abdellahshabat.fatora.ClarificationUiState
import com.abdellahshabat.fatora.CustomerOption
import com.abdellahshabat.fatora.domain.usecase.AddDebtUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

private const val CREATE_NEW_CUSTOMER_ID = "__NEW__"

/**
 * ViewModel شاشة إضافة دين - أول شاشة بتكتب فعلياً بقاعدة البيانات
 * (بدل ما نكتفي بالقراءة زي HomeViewModel).
 *
 * كل منطق التحقق (الاسم فاضي، المبلغ سالب، تطابق حرفي/جزئي لاسم العميل) موجود
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

    /**
     * بيجيب كل العملاء المشابهين للاسم (تطابق جزئي) ويعرض شاشة التوضيح.
     * بيضيف كمان خيار "عميل جديد باسم [X]" بآخر القائمة، لحالة إنه المستخدم
     * فعلاً يقصد شخص جديد ما إله علاقة بالأسماء المشابهة الموجودة.
     */
    private fun showClarification(customerName: String) {
        viewModelScope.launch {
            val matches = addDebtUseCase.findMatchingCustomers(customerName)

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
            val productLabel = state.product.trim().ifBlank { "بدون منتج" }

            _uiState.value = state.copy(
                isSaving = false,
                clarification = ClarificationUiState(
                    heardText = "${state.customerName.trim()} - $productLabel - ${state.amountText} ₪",
                    message = "في أسماء تشبه \"$customerName\"",
                    subMessage = "حدد مين بالضبط، أو أضف عميل جديد لو مش أي منهم",
                    duplicateCustomers = options
                )
            )
        }
    }

    /** يُستدعى لما المستخدم يضغط على أحد العملاء (أو خيار "عميل جديد") بشاشة التوضيح. */
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

    /** يُستدعى بزر "تابع التسجيل" بشاشة التوضيح - يحفظ الدين للعميل المحدد أخيراً. */
    fun onClarificationContinue() {
        val state = _uiState.value
        val clarification = state.clarification ?: return
        val customerId = clarification.selectedCustomerId ?: return
        val amount = state.amountText.toDoubleOrNull() ?: return
        val product = state.product.trim().ifBlank { null }

        viewModelScope.launch {
            val result = if (customerId == CREATE_NEW_CUSTOMER_ID) {
                addDebtUseCase.createNewCustomerAndAdd(
                    customerName = state.customerName.trim(),
                    product = product,
                    amount = amount
                )
            } else {
                addDebtUseCase.withResolvedCustomer(
                    customerId = customerId,
                    product = product,
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