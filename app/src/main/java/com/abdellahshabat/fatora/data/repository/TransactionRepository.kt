package com.abdellahshabat.fatora.data.repository

import com.abdellahshabat.fatora.data.database.dao.TransactionDao
import com.abdellahshabat.fatora.data.database.entity.Transaction
import com.abdellahshabat.fatora.data.database.entity.TransactionType
import java.util.UUID

class TransactionRepository(
    private val transactionDao: TransactionDao
) {

    suspend fun addDebt(
        customerId: String,
        product: String?,
        amount: Double,
        currency: String = "ILS"
    ): Transaction {

        val transaction = Transaction(
            id = UUID.randomUUID().toString(),
            customerId = customerId,
            product = product,
            amount = amount,
            currency = currency,
            type = TransactionType.DEBT
        )

        transactionDao.insertTransaction(transaction)

        return transaction
    }

    suspend fun addPayment(
        customerId: String,
        amount: Double,
        currency: String = "ILS"
    ): Transaction {

        val transaction = Transaction(
            id = UUID.randomUUID().toString(),
            customerId = customerId,
            product = null,
            amount = amount,
            currency = currency,
            type = TransactionType.PAYMENT
        )

        transactionDao.insertTransaction(transaction)

        return transaction
    }

    suspend fun getCustomerTransactions(
        customerId: String
    ): List<Transaction> {
        return transactionDao.getCustomerTransactions(customerId)
    }

    suspend fun getAllTransactions(): List<Transaction> {
        return transactionDao.getAllTransactions()
    }
}