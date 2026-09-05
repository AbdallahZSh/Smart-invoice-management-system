package com.abdellahshabat.fatora.domain.usecase

import com.abdellahshabat.fatora.data.database.entity.Transaction
import com.abdellahshabat.fatora.data.repository.CustomerRepository
import com.abdellahshabat.fatora.data.repository.TransactionRepository

/**
 * نفس منطق AddDebtUseCase بالضبط (البحث عن العميل بالاسم، معالجة التكرار،
 * إنشاء عميل جديد لو ما وجد) بس بيسجل دفعة بدل دين.
 */
class AddPaymentUseCase(
    private val customerRepository: CustomerRepository,
    private val transactionRepository: TransactionRepository
) {

    suspend operator fun invoke(
        customerName: String,
        amount: Double,
        currency: String = "ILS"
    ): Result<Transaction> {

        if (customerName.isBlank()) {
            return Result.failure(
                IllegalArgumentException("Customer name is required")
            )
        }

        if (amount <= 0) {
            return Result.failure(
                IllegalArgumentException("Amount must be greater than zero")
            )
        }

        val customers =
            customerRepository.findCustomers(customerName)

        if (customers.size > 1) {
            return Result.failure(
                IllegalStateException("AMBIGUOUS_CUSTOMER")
            )
        }

        val customer = if (customers.size == 1) {
            customers.first()
        } else {
            customerRepository.createCustomer(
                name = customerName
            )
        }

        val transaction = transactionRepository.addPayment(
            customerId = customer.id,
            amount = amount,
            currency = currency
        )

        return Result.success(transaction)
    }
}