package com.abdellahshabat.fatora.customer

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
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
 * شاشة قائمة العملاء - كل عميل مع رصيده الحالي، مرتبين تنازلي حسب الرصيد.
 * فيها حقل بحث بالاسم (فلترة محلية فورية على القائمة المحمّلة أصلاً).
 * الضغط العادي بينقل لتفاصيل العميل، والضغط الطويل بيعرض تأكيد حذف.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CustomersScreen(
    state: CustomersUiState,
    onCustomerClick: (String) -> Unit,
    onDeleteCustomer: (String) -> Unit,
    onBackClick: () -> Unit
) {
    var customerPendingDelete by remember { mutableStateOf<CustomerCardUi?>(null) }
    var searchQuery by remember { mutableStateOf("") }

    val filteredCustomers = remember(state.customers, searchQuery) {
        if (searchQuery.isBlank()) {
            state.customers
        } else {
            state.customers.filter { it.name.contains(searchQuery, ignoreCase = true) }
        }
    }

    CompositionLocalProvider(
        LocalLayoutDirection provides LayoutDirection.Rtl
    ) {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text("العملاء") },
                    navigationIcon = {
                        IconButton(onClick = onBackClick) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = "رجوع"
                            )
                        }
                    }
                )
            }
        ) { paddingValues ->

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
            ) {

                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    placeholder = { Text("دور على اسم عميل...") },
                    leadingIcon = {
                        Icon(Icons.Default.Search, contentDescription = null)
                    },
                    singleLine = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp, vertical = 10.dp)
                )

                when {
                    state.isLoading -> {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            CircularProgressIndicator()
                        }
                    }

                    state.customers.isEmpty() -> {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "لسا ما في عملاء مسجلين",
                                color = Color.Gray
                            )
                        }
                    }

                    filteredCustomers.isEmpty() -> {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "ما في نتائج لـ \"$searchQuery\"",
                                color = Color.Gray
                            )
                        }
                    }

                    else -> {
                        LazyColumn(
                            modifier = Modifier.fillMaxSize(),
                            contentPadding = PaddingValues(
                                bottom = 16.dp,
                                start = 20.dp,
                                end = 20.dp
                            ),
                            verticalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            items(filteredCustomers, key = { it.id }) { customer ->
                                CustomerCard(
                                    customer = customer,
                                    onClick = { onCustomerClick(customer.id) },
                                    onLongClick = { customerPendingDelete = customer }
                                )
                            }
                        }
                    }
                }
            }
        }
    }

    val target = customerPendingDelete
    if (target != null) {
        AlertDialog(
            onDismissRequest = { customerPendingDelete = null },
            title = { Text("حذف ${target.name}؟") },
            text = { Text("رح يتحذف العميل وكل عملياته المسجلة معاه. هاد الإجراء ما بينرجع.") },
            confirmButton = {
                TextButton(onClick = {
                    onDeleteCustomer(target.id)
                    customerPendingDelete = null
                }) {
                    Text("حذف", color = MaterialTheme.colorScheme.error)
                }
            },
            dismissButton = {
                TextButton(onClick = { customerPendingDelete = null }) {
                    Text("إلغاء")
                }
            }
        )
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun CustomerCard(
    customer: CustomerCardUi,
    onClick: () -> Unit,
    onLongClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .combinedClickable(
                onClick = onClick,
                onLongClick = onLongClick
            ),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(42.dp)
                        .background(Color(0xFFDCEBFF), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Person,
                        contentDescription = null,
                        tint = Color(0xFF1976D2),
                        modifier = Modifier.size(22.dp)
                    )
                }

                Column(modifier = Modifier.padding(start = 12.dp)) {
                    Text(
                        text = customer.name,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Medium
                    )
                    if (customer.phone != null) {
                        Text(
                            text = customer.phone,
                            fontSize = 12.sp,
                            color = Color.Gray
                        )
                    }
                }
            }

            Column(horizontalAlignment = Alignment.End) {
                Text(
                    text = "${customer.balance.toDisplayString()} ₪",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium,
                    color = if (customer.balance > 0) Color(0xFF9A6500) else Color(0xFF168A00)
                )
                Text(
                    text = if (customer.balance > 0) "دين مستحق" else "لا يوجد دين",
                    fontSize = 11.sp,
                    color = Color.Gray
                )
            }
        }
    }
}

private fun Double.toDisplayString(): String {
    return if (this == this.toLong().toDouble()) this.toLong().toString() else this.toString()
}