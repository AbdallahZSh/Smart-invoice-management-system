package com.abdellahshabat.fatora.data.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.abdellahshabat.fatora.data.database.entity.Transaction

@Dao
interface TransactionDao {

    @Insert
    suspend fun insertTransaction(transaction: Transaction)

    @Query("""
        SELECT * FROM transactions
        WHERE customerId = :customerId
        ORDER BY createdAt DESC
    """)
    suspend fun getCustomerTransactions(
        customerId: String
    ): List<Transaction>

    @Query("""
        SELECT * FROM transactions
        ORDER BY createdAt DESC
    """)
    suspend fun getAllTransactions(): List<Transaction>

    @Query("DELETE FROM transactions WHERE id = :transactionId")
    suspend fun deleteTransactionById(transactionId: String)

    @Query("DELETE FROM transactions WHERE customerId = :customerId")
    suspend fun deleteTransactionsByCustomerId(customerId: String)

    @Query("UPDATE transactions SET product = :product, amount = :amount WHERE id = :transactionId")
    suspend fun updateTransaction(transactionId: String, product: String?, amount: Double)
}