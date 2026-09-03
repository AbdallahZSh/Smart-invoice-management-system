package com.abdellahshabat.fatora.model

data class Product(
    val id: Int,
    val name: String,
    val purchasePrice: Double,
    val sellingPrice: Double,
    val quantity: Int
)