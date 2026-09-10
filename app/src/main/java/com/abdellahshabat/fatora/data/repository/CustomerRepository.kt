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

    suspend fun findCustomersByExactName(name: String): List<Customer> {
        return customerDao.getCustomersByExactName(name)
    }

    suspend fun getCustomerById(id: String): Customer? {
        return customerDao.getCustomerById(id)
    }

    suspend fun getAllCustomers(): List<Customer> {
        return customerDao.getAllCustomers()
    }

    /** حذف العميل نفسه - لازم يترافق مع حذف عملياته من TransactionRepository برة هالكلاس. */
    suspend fun deleteCustomer(customerId: String) {
        customerDao.deleteCustomerById(customerId)
    }

    /** إدخال عميل جاهز بنفس الـ id والتاريخ الأصلي - تُستخدم فقط عند استعادة نسخة احتياطية. */
    suspend fun insertCustomerDirect(customer: Customer) {
        customerDao.insertCustomer(customer)
    }
}