package com.abdellahshabat.fatora.viewmodel

import android.content.Context
import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.abdellahshabat.fatora.BackupUiState
import com.abdellahshabat.fatora.data.repository.CustomerRepository
import com.abdellahshabat.fatora.data.repository.TransactionRepository
import com.abdellahshabat.fatora.util.BackupManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

/**
 * ViewModel النسخ الاحتياطي - تصدير كل البيانات كملف JSON بمجلد التنزيلات،
 * واستعادتها من ملف سابق (بيستبدل البيانات الحالية بالكامل، بعد تأكيد بالواجهة).
 */
class BackupViewModel(
    private val customerRepository: CustomerRepository,
    private val transactionRepository: TransactionRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(BackupUiState())
    val uiState: StateFlow<BackupUiState> = _uiState.asStateFlow()

    init {
        loadCounts()
    }

    fun loadCounts() {
        viewModelScope.launch {
            val customers = customerRepository.getAllCustomers()
            val transactions = transactionRepository.getAllTransactions()
            _uiState.value = _uiState.value.copy(
                totalCustomers = customers.size,
                totalTransactions = transactions.size,
                isLoading = false
            )
        }
    }

    fun exportBackup(context: Context) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isWorking = true, message = null)

            val result = withContext(Dispatchers.IO) {
                val customers = customerRepository.getAllCustomers()
                val transactions = transactionRepository.getAllTransactions()
                val json = BackupManager.buildBackupJson(customers, transactions)
                BackupManager.saveBackupToDownloads(context, json)
            }

            _uiState.value = if (result != null) {
                _uiState.value.copy(
                    isWorking = false,
                    message = "تم حفظ النسخة الاحتياطية بمجلد التنزيلات",
                    isError = false
                )
            } else {
                _uiState.value.copy(
                    isWorking = false,
                    message = "صار خطأ أثناء إنشاء النسخة الاحتياطية",
                    isError = true
                )
            }
        }
    }

    /**
     * يستعيد نسخة احتياطية من ملف اختاره المستخدم. **بيحذف كل البيانات الحالية**
     * ويستبدلها بمحتوى الملف بالكامل - الشاشة لازم تأكد من المستخدم قبل ما تنادي هاي الدالة.
     */
    fun importBackup(context: Context, uri: Uri) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isWorking = true, message = null)

            val parsed = withContext(Dispatchers.IO) {
                val text = BackupManager.readTextFromUri(context, uri)
                text?.let { BackupManager.parseBackupJson(it) }
            }

            if (parsed == null) {
                _uiState.value = _uiState.value.copy(
                    isWorking = false,
                    message = "الملف يلي اخترته مش نسخة احتياطية صحيحة",
                    isError = true
                )
                return@launch
            }

            withContext(Dispatchers.IO) {
                // حذف كل شي حالي أول (استعادة = استبدال كامل، مش دمج)
                val existingCustomers = customerRepository.getAllCustomers()
                existingCustomers.forEach { customer ->
                    transactionRepository.deleteTransactionsForCustomer(customer.id)
                    customerRepository.deleteCustomer(customer.id)
                }

                parsed.customers.forEach { customer ->
                    customerRepository.insertCustomerDirect(customer)
                }
                parsed.transactions.forEach { transaction ->
                    transactionRepository.insertTransactionDirect(transaction)
                }
            }

            _uiState.value = _uiState.value.copy(
                isWorking = false,
                message = "تم استعادة ${parsed.customers.size} عميل و${parsed.transactions.size} عملية بنجاح",
                isError = false
            )

            loadCounts()
        }
    }
}

class BackupViewModelFactory(
    private val customerRepository: CustomerRepository,
    private val transactionRepository: TransactionRepository
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(BackupViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return BackupViewModel(customerRepository, transactionRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
    }
}