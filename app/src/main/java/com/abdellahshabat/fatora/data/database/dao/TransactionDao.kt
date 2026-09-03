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
}