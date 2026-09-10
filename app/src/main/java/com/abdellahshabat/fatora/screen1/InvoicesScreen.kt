package com.abdellahshabat.fatora

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

/**
 * شاشة قائمة الفواتير - كل عملية بطاقة مستقلة، مرتبة تنازلي (الأحدث فوق)،
 * مع أيقونتين بكل بطاقة: تعديل (منتج/مبلغ) وحذف (نهائي، بتأكيد).
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InvoicesScreen(
    state: InvoicesUiState,
    onBackClick: () -> Unit,
    onReportClick: () -> Unit,
    onDeleteInvoice: (String) -> Unit,
    onEditInvoice: (String, String?, Double) -> Unit
) {
    var invoicePendingDelete by remember { mutableStateOf<InvoiceCardUi?>(null) }
    var invoicePendingEdit by remember { mutableStateOf<InvoiceCardUi?>(null) }

    CompositionLocalProvider(
        LocalLayoutDirection provides LayoutDirection.Rtl
    ) {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text("الفواتير") },
                    navigationIcon = {
                        IconButton(onClick = onBackClick) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = "رجوع"
                            )
                        }
                    },
                    actions = {
                        IconButton(onClick = onReportClick) {
                            Icon(
                                imageVector = Icons.Default.BarChart,
                                contentDescription = "كشف المبيعات"
                            )
                        }
                    }
                )
            }
        ) { paddingValues ->

            when {
                state.isLoading -> {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(paddingValues),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator()
                    }
                }

                state.invoices.isEmpty() -> {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(paddingValues),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "لسا ما في فواتير مسجلة",
                            color = Color.Gray
                        )
                    }
                }

                else -> {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(
                            top = paddingValues.calculateTopPadding() + 12.dp,
                            bottom = paddingValues.calculateBottomPadding() + 16.dp,
                            start = 20.dp,
                            end = 20.dp
                        ),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        items(state.invoices, key = { it.id }) { invoice ->
                            InvoiceCard(
                                invoice = invoice,
                                onDeleteClick = { invoicePendingDelete = invoice },
                                onEditClick = { invoicePendingEdit = invoice }
                            )
                        }
                    }
                }
            }
        }
    }

    val deleteTarget = invoicePendingDelete
    if (deleteTarget != null) {
        AlertDialog(
            onDismissRequest = { invoicePendingDelete = null },
            title = { Text("حذف الفاتورة؟") },
            text = {
                Text("رح تتحذف عملية \"${deleteTarget.label}\" لـ ${deleteTarget.customerName} نهائياً. هاد الإجراء ما بينرجع.")
            },
            confirmButton = {
                TextButton(onClick = {
                    onDeleteInvoice(deleteTarget.id)
                    invoicePendingDelete = null
                }) {
                    Text("حذف", color = MaterialTheme.colorScheme.error)
                }
            },
            dismissButton = {
                TextButton(onClick = { invoicePendingDelete = null }) {
                    Text("إلغاء")
                }
            }
        )
    }

    val editTarget = invoicePendingEdit
    if (editTarget != null) {
        EditInvoiceDialog(
            invoice = editTarget,
            onDismiss = { invoicePendingEdit = null },
            onConfirm = { product, amount ->
                onEditInvoice(editTarget.id, product, amount)
                invoicePendingEdit = null
            }
        )
    }
}

@Composable
private fun EditInvoiceDialog(
    invoice: InvoiceCardUi,
    onDismiss: () -> Unit,
    onConfirm: (String?, Double) -> Unit
) {
    // الدفعة ما إلها منتج، فبنخفي حقل المنتج بحالتها.
    val isPayment = invoice.isPositive

    var productText by remember { mutableStateOf(invoice.rawProduct ?: "") }
    var amountText by remember { mutableStateOf(invoice.amount.toEditableString()) }

    val parsedAmount = amountText.toDoubleOrNull()
    val isSaveEnabled = parsedAmount != null && parsedAmount > 0

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("تعديل العملية") },
        text = {
            Column {
                Text(
                    text = invoice.customerName,
                    fontSize = 13.sp,
                    color = Color.Gray
                )

                Spacer(modifier = Modifier.height(12.dp))

                if (!isPayment) {
                    OutlinedTextField(
                        value = productText,
                        onValueChange = { productText = it },
                        label = { Text("المنتج (اختياري)") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                }

                OutlinedTextField(
                    value = amountText,
                    onValueChange = { amountText = it },
                    label = { Text("المبلغ (شيكل)") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    isError = parsedAmount == null || parsedAmount <= 0
                )
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    val amount = parsedAmount ?: return@TextButton
                    val product = if (isPayment) null else productText.trim().ifBlank { null }
                    onConfirm(product, amount)
                },
                enabled = isSaveEnabled
            ) {
                Text("حفظ التعديل")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("إلغاء")
            }
        }
    )
}

@Composable
private fun InvoiceCard(
    invoice: InvoiceCardUi,
    onDeleteClick: () -> Unit,
    onEditClick: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = invoice.customerName,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Medium
                    )
                    Text(
                        text = invoice.label,
                        fontSize = 13.sp,
                        color = Color.Gray
                    )
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    val sign = if (invoice.isPositive) "-" else "+"
                    Text(
                        text = "$sign${invoice.amount.toEditableString()} ₪",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Medium,
                        color = if (invoice.isPositive) Color(0xFF168A00) else Color(0xFF9A6500)
                    )

                    IconButton(
                        onClick = onEditClick,
                        modifier = Modifier.size(30.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Edit,
                            contentDescription = "تعديل العملية",
                            tint = Color.Gray,
                            modifier = Modifier.size(17.dp)
                        )
                    }

                    IconButton(
                        onClick = onDeleteClick,
                        modifier = Modifier.size(30.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Delete,
                            contentDescription = "حذف العملية",
                            tint = Color.Gray,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(4.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = invoice.dateLabel,
                    fontSize = 12.sp,
                    color = Color.Gray
                )

                val badgeColor = if (invoice.isPositive) Color(0xFF168A00) else Color(0xFF9A6500)
                val badgeBg = if (invoice.isPositive) Color(0xFFE1F5EE) else Color(0xFFFAEEDA)

                Surface(
                    color = badgeBg,
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(
                        text = if (invoice.isPositive) "دفعة" else "دين",
                        fontSize = 11.sp,
                        color = badgeColor,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 2.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "إجمالي على ${invoice.customerName} لحد الآن",
                    fontSize = 12.sp,
                    color = Color.Gray
                )
                Text(
                    text = "${invoice.customerTotalBalance.toEditableString()} ₪",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium,
                    color = Color(0xFF9A6500)
                )
            }
        }
    }
}

private fun Double.toEditableString(): String {
    return if (this == this.toLong().toDouble()) this.toLong().toString() else this.toString()
}