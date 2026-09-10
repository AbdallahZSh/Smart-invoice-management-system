package com.abdellahshabat.fatora.util

import android.content.ContentValues
import android.content.Context
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import com.abdellahshabat.fatora.data.database.entity.Customer
import com.abdellahshabat.fatora.data.database.entity.Transaction
import com.abdellahshabat.fatora.data.database.entity.TransactionType
import org.json.JSONArray
import org.json.JSONObject
import java.io.BufferedReader
import java.io.InputStreamReader

/**
 * تصدير/استيراد كل بيانات التطبيق (عملاء + عمليات) كملف JSON واحد.
 * نستخدم org.json المدمجة بأندرويد - بدون أي مكتبة خارجية جديدة.
 *
 * صيغة الملف:
 * {
 *   "version": 1,
 *   "customers": [ {id, name, phone, createdAt}, ... ],
 *   "transactions": [ {id, customerId, product, amount, currency, type, createdAt}, ... ]
 * }
 */
object BackupManager {

    private const val BACKUP_VERSION = 1

    fun buildBackupJson(customers: List<Customer>, transactions: List<Transaction>): String {
        val root = JSONObject()
        root.put("version", BACKUP_VERSION)

        val customersArray = JSONArray()
        customers.forEach { customer ->
            val obj = JSONObject()
            obj.put("id", customer.id)
            obj.put("name", customer.name)
            obj.put("phone", customer.phone ?: JSONObject.NULL)
            obj.put("createdAt", customer.createdAt)
            customersArray.put(obj)
        }
        root.put("customers", customersArray)

        val transactionsArray = JSONArray()
        transactions.forEach { transaction ->
            val obj = JSONObject()
            obj.put("id", transaction.id)
            obj.put("customerId", transaction.customerId)
            obj.put("product", transaction.product ?: JSONObject.NULL)
            obj.put("amount", transaction.amount)
            obj.put("currency", transaction.currency)
            obj.put("type", transaction.type.name)
            obj.put("createdAt", transaction.createdAt)
            transactionsArray.put(obj)
        }
        root.put("transactions", transactionsArray)

        return root.toString(2)
    }

    /** يحفظ نص الـ JSON كملف بمجلد التنزيلات، ويرجع الـ Uri تبعه. */
    fun saveBackupToDownloads(context: Context, json: String): Uri? {
        val resolver = context.contentResolver
        val fileName = "fatora_backup_${System.currentTimeMillis()}.json"

        val contentValues = ContentValues().apply {
            put(MediaStore.MediaColumns.DISPLAY_NAME, fileName)
            put(MediaStore.MediaColumns.MIME_TYPE, "application/json")
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_DOWNLOADS)
            }
        }

        val collectionUri = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            MediaStore.Downloads.EXTERNAL_CONTENT_URI
        } else {
            @Suppress("DEPRECATION")
            MediaStore.Files.getContentUri("external")
        }

        val itemUri = resolver.insert(collectionUri, contentValues) ?: return null

        resolver.openOutputStream(itemUri)?.use { outputStream ->
            outputStream.write(json.toByteArray(Charsets.UTF_8))
        }

        return itemUri
    }

    /** يقرا نص JSON كامل من ملف اختاره المستخدم (عبر منتقي الملفات). */
    fun readTextFromUri(context: Context, uri: Uri): String? {
        return context.contentResolver.openInputStream(uri)?.use { inputStream ->
            BufferedReader(InputStreamReader(inputStream, Charsets.UTF_8)).readText()
        }
    }

    /** نتيجة تحليل ملف نسخة احتياطية - عملاء وعمليات جاهزين للإدخال بقاعدة البيانات. */
    data class ParsedBackup(
        val customers: List<Customer>,
        val transactions: List<Transaction>
    )

    /** يحوّل نص JSON لقوائم Customer/Transaction. يرجع null لو الملف مش بالصيغة الصح. */
    fun parseBackupJson(json: String): ParsedBackup? {
        return runCatching {
            val root = JSONObject(json)

            val customersArray = root.getJSONArray("customers")
            val customers = (0 until customersArray.length()).map { index ->
                val obj = customersArray.getJSONObject(index)
                Customer(
                    id = obj.getString("id"),
                    name = obj.getString("name"),
                    phone = if (obj.isNull("phone")) null else obj.getString("phone"),
                    createdAt = obj.getLong("createdAt")
                )
            }

            val transactionsArray = root.getJSONArray("transactions")
            val transactions = (0 until transactionsArray.length()).map { index ->
                val obj = transactionsArray.getJSONObject(index)
                Transaction(
                    id = obj.getString("id"),
                    customerId = obj.getString("customerId"),
                    product = if (obj.isNull("product")) null else obj.getString("product"),
                    amount = obj.getDouble("amount"),
                    currency = obj.optString("currency", "ILS"),
                    type = TransactionType.valueOf(obj.getString("type")),
                    createdAt = obj.getLong("createdAt")
                )
            }

            ParsedBackup(customers = customers, transactions = transactions)
        }.getOrNull()
    }
}