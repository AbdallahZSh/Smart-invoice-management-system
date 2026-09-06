package com.abdellahshabat.fatora.domain.usecase

import com.abdellahshabat.fatora.data.database.entity.Transaction
import com.abdellahshabat.fatora.data.repository.CustomerRepository
import com.abdellahshabat.fatora.data.repository.TransactionRepository

/** نفس منطق AddDebtUseCase بالضبط (exact match ثم partial match) بس لعملية دفعة. */
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

        val exactMatches = customerRepository.findCustomersByExactName(customerName)

        if (exactMatches.size > 1) {
            return Result.failure(IllegalStateException("AMBIGUOUS_CUSTOMER"))
        }

        if (exactMatches.size == 1) {
            val customer = exactMatches.first()
            val transaction = transactionRepository.addPayment(
                customerId = customer.id,
                amount = amount,
                currency = currency
            )
            return Result.success(transaction)
        }

        val partialMatches = customerRepository.findCustomers(customerName)
        if (partialMatches.isNotEmpty()) {
            return Result.failure(IllegalStateException("AMBIGUOUS_CUSTOMER"))
        }

        val newCustomer = customerRepository.createCustomer(name = customerName)
        val transaction = transactionRepository.addPayment(
            customerId = newCustomer.id,
            amount = amount,
            currency = currency
        )
        return Result.success(transaction)
    }

    suspend fun findMatchingCustomers(name: String) =
        customerRepository.findCustomers(name)

    suspend fun withResolvedCustomer(
        customerId: String,
        amount: Double,
        currency: String = "ILS"
    ): Result<Transaction> {

        if (amount <= 0) {
            return Result.failure(
                IllegalArgumentException("Amount must be greater than zero")
            )
        }

        val transaction = transactionRepository.addPayment(
            customerId = customerId,
            amount = amount,
            currency = currency
        )

        return Result.success(transaction)
    }

    suspend fun createNewCustomerAndAdd(
        customerName: String,
        amount: Double,
        currency: String = "ILS"
    ): Result<Transaction> {

        if (amount <= 0) {
            return Result.failure(
                IllegalArgumentException("Amount must be greater than zero")
            )
        }

        val newCustomer = customerRepository.createCustomer(name = customerName)
        val transaction = transactionRepository.addPayment(
            customerId = newCustomer.id,
            amount = amount,
            currency = currency
        )

        return Result.success(transaction)
    }
}