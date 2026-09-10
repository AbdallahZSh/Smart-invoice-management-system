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

    /** حذف عملية واحدة بعينها (لو المستخدم غلط وسجلها بالغلط). */
    suspend fun deleteTransaction(transactionId: String) {
        transactionDao.deleteTransactionById(transactionId)
    }

    /** حذف كل عمليات عميل معيّن - تُستخدم عند حذف العميل نفسه (تنظيف تبعي). */
    suspend fun deleteTransactionsForCustomer(customerId: String) {
        transactionDao.deleteTransactionsByCustomerId(customerId)
    }

    /** تعديل منتج/مبلغ عملية موجودة (لو المستخدم غلط بالتسجيل وبدو يصحح بدل ما يحذف ويعيد). */
    suspend fun updateTransaction(transactionId: String, product: String?, amount: Double) {
        transactionDao.updateTransaction(transactionId, product, amount)
    }

    /** إدخال عملية جاهزة بنفس الـ id والتاريخ الأصلي - تُستخدم فقط عند استعادة نسخة احتياطية. */
    suspend fun insertTransactionDirect(transaction: Transaction) {
        transactionDao.insertTransaction(transaction)
    }
}