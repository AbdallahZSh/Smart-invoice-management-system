package com.abdellahshabat.fatora.domain.usecase

import com.abdellahshabat.fatora.data.database.entity.Transaction
import com.abdellahshabat.fatora.data.repository.CustomerRepository
import com.abdellahshabat.fatora.data.repository.TransactionRepository

class AddDebtUseCase(
    private val customerRepository: CustomerRepository,
    private val transactionRepository: TransactionRepository
) {

    suspend operator fun invoke(
        customerName: String,
        product: String?,
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

        val transaction = transactionRepository.addDebt(
            customerId = customer.id,
            product = product,
            amount = amount,
            currency = currency
        )

        return Result.success(transaction)
    }
}