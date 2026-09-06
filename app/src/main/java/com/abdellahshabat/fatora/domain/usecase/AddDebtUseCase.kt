package com.abdellahshabat.fatora.domain.usecase

import com.abdellahshabat.fatora.data.database.entity.Transaction
import com.abdellahshabat.fatora.data.repository.CustomerRepository
import com.abdellahshabat.fatora.data.repository.TransactionRepository

class AddDebtUseCase(
    private val customerRepository: CustomerRepository,
    private val transactionRepository: TransactionRepository
) {

    /**
     * منطق تحديد العميل:
     * 1. تطابق حرفي واحد بالضبط -> نحفظ عليه مباشرة (زي "omer" اللي انكتبت أكتر من مرة).
     * 2. تطابق حرفي أكتر من واحد -> AMBIGUOUS_CUSTOMER (نادر، لعملاء بنفس الاسم بالضبط).
     * 3. ولا تطابق حرفي، بس في أسماء تحتوي نفس الكلمة (تطابق جزئي) -> AMBIGUOUS_CUSTOMER
     *    كمان (حتى لو نتيجة وحدة بس) - ما نخمّن، نسأل المستخدم.
     * 4. ولا تطابق حرفي ولا جزئي إطلاقاً -> عميل جديد فعلاً، ننشئه مباشرة.
     */
    suspend operator fun invoke(
        customerName: String,
        product: String?,
        amount: Double,
        currency: String = "ILS"
    ): Result<Transaction> {

        if (customerName.isBlank()) {
            return Result.failure(
                IllegalArgumentException("Customer name is required")
            )
        }

        if (amount <= 0) {
            return Result.failure(
                IllegalArgumentException("Amount must be greater than zero")
            )
        }

        val exactMatches = customerRepository.findCustomersByExactName(customerName)

        if (exactMatches.size > 1) {
            return Result.failure(IllegalStateException("AMBIGUOUS_CUSTOMER"))
        }

        if (exactMatches.size == 1) {
            val customer = exactMatches.first()
            val transaction = transactionRepository.addDebt(
                customerId = customer.id,
                product = product,
                amount = amount,
                currency = currency
            )
            return Result.success(transaction)
        }

        val partialMatches = customerRepository.findCustomers(customerName)
        if (partialMatches.isNotEmpty()) {
            return Result.failure(IllegalStateException("AMBIGUOUS_CUSTOMER"))
        }

        val newCustomer = customerRepository.createCustomer(name = customerName)
        val transaction = transactionRepository.addDebt(
            customerId = newCustomer.id,
            product = product,
            amount = amount,
            currency = currency
        )
        return Result.success(transaction)
    }

    /** يرجع الأسماء المشابهة (تطابق جزئي) - تستخدمها الـ ViewModel لبناء قائمة الاختيار. */
    suspend fun findMatchingCustomers(name: String) =
        customerRepository.findCustomers(name)

    /** تسجيل الدين مباشرة لعميل معروف الـ id (بعد ما المستخدم يختاره من شاشة التوضيح). */
    suspend fun withResolvedCustomer(
        customerId: String,
        product: String?,
        amount: Double,
        currency: String = "ILS"
    ): Result<Transaction> {

        if (amount <= 0) {
            return Result.failure(
                IllegalArgumentException("Amount must be greater than zero")
            )
        }

        val transaction = transactionRepository.addDebt(
            customerId = customerId,
            product = product,
            amount = amount,
            currency = currency
        )

        return Result.success(transaction)
    }

    /** ينشئ عميل جديد فعلياً بنفس الاسم اللي كتبه المستخدم - لما يختار "عميل جديد" من شاشة التوضيح. */
    suspend fun createNewCustomerAndAdd(
        customerName: String,
        product: String?,
        amount: Double,
        currency: String = "ILS"
    ): Result<Transaction> {

        if (amount <= 0) {
            return Result.failure(
                IllegalArgumentException("Amount must be greater than zero")
            )
        }

        val newCustomer = customerRepository.createCustomer(name = customerName)
        val transaction = transactionRepository.addDebt(
            customerId = newCustomer.id,
            product = product,
            amount = amount,
            currency = currency
        )

        return Result.success(transaction)
    }
}