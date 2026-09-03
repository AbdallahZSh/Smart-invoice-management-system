package com.abdellahshabat.fatora.data.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "transactions")
data class Transaction(
    @PrimaryKey
    val id: String,

    val customerId: String,

    val product: String?,

    val amount: Double,

    val currency: String = "ILS",

    val type: TransactionType,

    val createdAt: Long = System.currentTimeMillis()
)

enum class TransactionType {
    DEBT,
    PAYMENT
}