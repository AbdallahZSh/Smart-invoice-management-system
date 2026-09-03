package com.abdellahshabat.fatora.data.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "customers")
data class Customer(
    @PrimaryKey
    val id: String,

    val name: String,

    val phone: String? = null,

    val createdAt: Long = System.currentTimeMillis()
)