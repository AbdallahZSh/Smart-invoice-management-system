package com.abdellahshabat.fatora.di

import android.content.Context
import com.abdellahshabat.fatora.data.database.FatoraDatabase
import com.abdellahshabat.fatora.data.repository.CustomerRepository
import com.abdellahshabat.fatora.data.repository.TransactionRepository
import com.abdellahshabat.fatora.domain.usecase.AddDebtUseCase

/**
 * حاوية بسيطة للـ Dependency Injection اليدوي (بدون Hilt/Koin بمرحلة الـ MVP).
 * تُبنى مرة وحدة بـ MainActivity وتُمرَّر لكل الشاشات عبر Navigation.
 *
 * لما المشروع يكبر ونحتاج DI framework حقيقي، هاد الكلاس هو المكان
 * الوحيد يلي بده يتغيّر - باقي الكود (ViewModels, Repositories) ما بتحتاج تعديل.
 */
class AppContainer(context: Context) {

    private val database: FatoraDatabase =
        FatoraDatabase.getInstance(context.applicationContext)

    val customerRepository: CustomerRepository =
        CustomerRepository(database.customerDao())

    val transactionRepository: TransactionRepository =
        TransactionRepository(database.transactionDao())

    val addDebtUseCase: AddDebtUseCase =
        AddDebtUseCase(customerRepository, transactionRepository)
}