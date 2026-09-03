package com.abdellahshabat.fatora.data.repository


import com.abdellahshabat.fatora.data.database.dao.CustomerDao
import com.abdellahshabat.fatora.data.database.entity.Customer
import java.util.UUID

class CustomerRepository(
    private val customerDao: CustomerDao
) {

    suspend fun createCustomer(
        name: String,
        phone: String? = null
    ): Customer {

        val customer = Customer(
            id = UUID.randomUUID().toString(),
            name = name,
            phone = phone
        )

        customerDao.insertCustomer(customer)

        return customer
    }

    suspend fun findCustomers(name: String): List<Customer> {
        return customerDao.searchCustomers(name)
    }

    suspend fun getCustomerById(id: String): Customer? {
        return customerDao.getCustomerById(id)
    }

    suspend fun getAllCustomers(): List<Customer> {
        return customerDao.getAllCustomers()
    }
}