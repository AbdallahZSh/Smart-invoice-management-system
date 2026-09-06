package com.abdellahshabat.fatora.data.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.abdellahshabat.fatora.data.database.entity.Customer

@Dao
interface CustomerDao {

    @Insert
    suspend fun insertCustomer(customer: Customer)

    @Query("SELECT * FROM customers WHERE id = :id LIMIT 1")
    suspend fun getCustomerById(id: String): Customer?

    @Query("SELECT * FROM customers WHERE name LIKE '%' || :name || '%'")
    suspend fun searchCustomers(name: String): List<Customer>

    @Query("SELECT * FROM customers WHERE name = :name COLLATE NOCASE")
    suspend fun getCustomersByExactName(name: String): List<Customer>

    @Query("SELECT * FROM customers ORDER BY createdAt DESC")
    suspend fun getAllCustomers(): List<Customer>
}