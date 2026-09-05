package com.abdellahshabat.fatora

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

/**
 * نوع العملية في سجل التعاملات مع العميل.
 *
 * DEBT    = دين جديد على العميل
 * PAYMENT = دفعة قام العميل بتسديدها
 */
enum class TransactionDirection {
    DEBT,
    PAYMENT
}

/**
 * عملية واحدة في سجل العميل.
 */
data class RecentTransactionItem(
    val label: String,
    val amount: Double,
    val currency: String = "₪",
    val direction: TransactionDirection
)

/**
 * البيانات التي تحتاجها شاشة نتيجة الاستعلام.
 *
 * مثال:
 *
 * heardText = "كم على أحمد؟"
 * customerName = "أحمد"
 * balance = 45.0
 */
data class QueryResponseUiState(
    val heardText: String,
    val customerName: String,
    val balance: Double,
    val currency: String = "₪",
    val balanceLabel: String = "إجمالي الدين المتبقي",
    val recentTransactions: List<RecentTransactionItem> = emptyList()
)

/**
 * شاشة عرض نتيجة الاستعلام.
 *
 * هذه الشاشة Read Only.
 *
 * يعني لو المستخدم قال:
 *
 * "كم على أحمد؟"
 *
 * والـ AI فهم أن المطلوب:
 *
 * GET_CUSTOMER_BALANCE
 *
 * يتم تجهيز QueryResponseUiState ثم عرض النتيجة هنا.
 */
@Composable
fun QueryResponseScreen(
    state: QueryResponseUiState,
    onExportPdfClick: () -> Unit,
    modifier: Modifier = Modifier
) {

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(24.dp)
    ) {

        // ------------------------------------------------
        // النص الذي قاله المستخدم
        // ------------------------------------------------

        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {

            Icon(
                imageVector = Icons.Filled.Mic,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.size(18.dp)
            )

            Spacer(
                modifier = Modifier.size(8.dp)
            )

            Text(
                text = "سمعتك تقول",
                fontSize = 13.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        Spacer(
            modifier = Modifier.height(12.dp)
        )

        // ------------------------------------------------
        // النص المسموع
        // ------------------------------------------------

        Card(
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surfaceVariant
            ),
            shape = RoundedCornerShape(12.dp)
        ) {

            Text(
                text = "\"${state.heardText}\"",
                fontSize = 14.sp,
                fontStyle = FontStyle.Italic,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(
                    horizontal = 16.dp,
                    vertical = 14.dp
                )
            )
        }

        Spacer(
            modifier = Modifier.height(24.dp)
        )

        // ------------------------------------------------
        // بطاقة نتيجة العميل
        // ------------------------------------------------

        Card(
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surfaceVariant
            ),
            shape = RoundedCornerShape(16.dp),
            modifier = Modifier.fillMaxWidth()
        ) {

            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 20.dp)
            ) {

                // أيقونة العميل
                Box(
                    modifier = Modifier
                        .size(52.dp)
                        .background(
                            color = MaterialTheme.colorScheme.primaryContainer,
                            shape = CircleShape
                        )
                        .wrapContentSize(
                            Alignment.Center
                        )
                ) {

                    Icon(
                        imageVector = Icons.Filled.Person,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(24.dp)
                    )
                }

                Spacer(
                    modifier = Modifier.height(10.dp)
                )

                // اسم العميل
                Text(
                    text = state.customerName,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Medium
                )

                Spacer(
                    modifier = Modifier.height(4.dp)
                )

                // الرصيد
                Text(
                    text = "${state.balance.toBalanceString()} ${state.currency}",
                    fontSize = 28.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = if (state.balance > 0) {
                        Color(0xFFB8860B)
                    } else {
                        MaterialTheme.colorScheme.primary
                    }
                )

                Spacer(
                    modifier = Modifier.height(6.dp)
                )

                // وصف الرصيد
                Text(
                    text = state.balanceLabel,
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        Spacer(
            modifier = Modifier.height(24.dp)
        )

        // ------------------------------------------------
        // آخر العمليات
        // ------------------------------------------------

        if (state.recentTransactions.isNotEmpty()) {

            Text(
                text = "كل العمليات مع ${state.customerName} (${state.recentTransactions.size})",
                fontSize = 13.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(
                modifier = Modifier.height(8.dp)
            )

            Column {

                state.recentTransactions.forEach { item ->

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(
                                color = MaterialTheme.colorScheme.surfaceVariant,
                                shape = RoundedCornerShape(8.dp)
                            )
                            .padding(
                                horizontal = 10.dp,
                                vertical = 8.dp
                            ),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {

                        // اسم العملية
                        Text(
                            text = item.label,
                            fontSize = 13.sp
                        )

                        // المبلغ
                        Text(
                            text = buildAmountText(item),
                            fontSize = 12.sp,
                            color = if (
                                item.direction == TransactionDirection.PAYMENT
                            ) {
                                MaterialTheme.colorScheme.primary
                            } else {
                                Color(0xFFB8860B)
                            }
                        )
                    }

                    Spacer(
                        modifier = Modifier.height(6.dp)
                    )
                }
            }

            // ------------------------------------------------
            // زر عرض السجل الكامل
            // ------------------------------------------------

            OutlinedButton(
                onClick = onExportPdfClick,
                modifier = Modifier.fillMaxWidth()
            ) {

                Icon(
                    imageVector = Icons.Filled.Description,
                    contentDescription = null,
                    modifier = Modifier.size(16.dp)
                )

                Spacer(
                    modifier = Modifier.size(8.dp)
                )

                Text(
                    text = "تحميل PDF"
                )
            }
        }
    }}

/**
 * تجهيز المبلغ مع + أو -
 *
 * الدين:
 * +5 ₪
 *
 * الدفعة:
 * -20 ₪
 */
private fun buildAmountText(
    item: RecentTransactionItem
): String {

    val sign = if (
        item.direction == TransactionDirection.DEBT
    ) {
        "+"
    } else {
        "-"
    }

    return "$sign${item.amount.toBalanceString()} ${item.currency}"
}

/**
 * تحويل:
 *
 * 45.0 -> 45
 * 45.5 -> 45.5
 */
private fun Double.toBalanceString(): String {

    return if (
        this == this.toLong().toDouble()
    ) {
        this.toLong().toString()
    } else {
        this.toString()
    }
}