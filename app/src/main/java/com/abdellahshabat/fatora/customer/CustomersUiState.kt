package com.abdellahshabat.fatora.customer

/** بطاقة عميل واحد بقائمة العملاء - الاسم + رقم الهاتف (إن وجد) + رصيده الحالي. */
data class CustomerCardUi(
    val id: String,
    val name: String,
    val phone: String?,
    val balance: Double
)

data class CustomersUiState(
    val customers: List<CustomerCardUi> = emptyList(),
    val isLoading: Boolean = true
)