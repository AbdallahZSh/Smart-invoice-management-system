package com.abdellahshabat.fatora

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Help
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp


/**
 * الحالة الخاصة بشاشة طلب التوضيح.
 *
 * تظهر هذه الشاشة عندما يكون أمر المستخدم غير واضح
 * أو توجد معلومة ناقصة لتنفيذ العملية.
 */
data class ClarificationUiState(
    val heardText: String,

    val message: String = "بدي أتأكد كم شغلة",

    val subMessage: String =
        "في معلومة ناقصة عشان أسجلها صح",

    val missingFieldQuestion: String? = null,

    val missingFieldValue: String = "",

    val duplicateCustomers: List<CustomerOption> = emptyList(),

    val selectedCustomerId: String? = null,

    val isContinueEnabled: Boolean = false
)


/**
 * عميل من قائمة العملاء المتشابهين.
 */
data class CustomerOption(
    val id: String,
    val displayName: String
)


/**
 * شاشة طلب التوضيح.
 */
@Composable
fun ClarificationScreen(
    state: ClarificationUiState,

    onMissingFieldChange: (String) -> Unit,

    onCustomerSelected: (CustomerOption) -> Unit,

    onContinue: () -> Unit,

    modifier: Modifier = Modifier
) {

    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(
                MaterialTheme.colorScheme.background
            )
            .padding(24.dp)
    ) {

        // -----------------------------------------
        // النص الذي فهمه التطبيق
        // -----------------------------------------

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


        // -----------------------------------------
        // النص الأصلي
        // -----------------------------------------

        Card(
            colors = CardDefaults.cardColors(
                containerColor =
                    MaterialTheme.colorScheme.surfaceVariant
            ),

            shape = RoundedCornerShape(12.dp)
        ) {

            Text(
                text = "\"${state.heardText}\"",

                fontSize = 14.sp,

                fontStyle = FontStyle.Italic,

                color =
                    MaterialTheme.colorScheme.onSurfaceVariant,

                modifier = Modifier.padding(
                    horizontal = 16.dp,
                    vertical = 14.dp
                )
            )
        }


        Spacer(
            modifier = Modifier.height(24.dp)
        )


        // -----------------------------------------
        // رسالة التوضيح
        // -----------------------------------------

        Row(
            verticalAlignment = Alignment.Top
        ) {

            Box(
                modifier = Modifier
                    .size(34.dp)
                    .background(
                        color =
                            MaterialTheme.colorScheme.tertiaryContainer,

                        shape = CircleShape
                    )
                    .wrapContentSize(
                        Alignment.Center
                    )
            ) {

                Icon(
                    imageVector = Icons.Filled.Help,

                    contentDescription = null,

                    tint =
                        MaterialTheme.colorScheme.tertiary,

                    modifier = Modifier.size(18.dp)
                )
            }


            Spacer(
                modifier = Modifier.size(10.dp)
            )


            Column {

                Text(
                    text = state.message,

                    fontSize = 15.sp,

                    fontWeight = FontWeight.Medium
                )

                Spacer(
                    modifier = Modifier.height(3.dp)
                )

                Text(
                    text = state.subMessage,

                    fontSize = 13.sp,

                    color =
                        MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }


        Spacer(
            modifier = Modifier.height(20.dp)
        )


        // -----------------------------------------
        // المعلومة الناقصة
        // -----------------------------------------

        state.missingFieldQuestion?.let { question ->

            Text(
                text = question,

                fontSize = 14.sp
            )

            Spacer(
                modifier = Modifier.height(10.dp)
            )


            OutlinedTextField(
                value = state.missingFieldValue,

                onValueChange = onMissingFieldChange,

                placeholder = {
                    Text(
                        text = "اكتب اسم المنتج..."
                    )
                },

                modifier = Modifier.fillMaxWidth(),

                singleLine = true
            )


            Spacer(
                modifier = Modifier.height(20.dp)
            )
        }


        // -----------------------------------------
        // العملاء المتشابهين
        // -----------------------------------------

        if (state.duplicateCustomers.isNotEmpty()) {

            Card(
                colors = CardDefaults.cardColors(
                    containerColor =
                        MaterialTheme.colorScheme.surfaceVariant
                ),

                shape = RoundedCornerShape(12.dp),

                modifier = Modifier.fillMaxWidth()
            ) {

                Column(
                    modifier = Modifier.padding(16.dp)
                ) {

                    Text(
                        text =
                            "في أكثر من عميل بنفس الاسم:",

                        fontSize = 12.sp,

                        color =
                            MaterialTheme.colorScheme.onSurfaceVariant
                    )


                    Spacer(
                        modifier = Modifier.height(8.dp)
                    )


                    state.duplicateCustomers.forEach { customer ->

                        val isSelected =
                            customer.id ==
                                    state.selectedCustomerId


                        OutlinedButton(

                            onClick = {
                                onCustomerSelected(customer)
                            },

                            colors =
                                ButtonDefaults.outlinedButtonColors(

                                    containerColor =
                                        if (isSelected) {

                                            MaterialTheme
                                                .colorScheme
                                                .primaryContainer

                                        } else {

                                            MaterialTheme
                                                .colorScheme
                                                .surface
                                        }
                                ),

                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 3.dp)
                        ) {

                            Text(
                                text = customer.displayName,

                                fontSize = 13.sp
                            )
                        }
                    }
                }
            }


            Spacer(
                modifier = Modifier.height(20.dp)
            )
        }


        // -----------------------------------------
        // زر المتابعة
        // -----------------------------------------

        Button(

            onClick = onContinue,

            enabled = state.isContinueEnabled,

            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp),

            shape = RoundedCornerShape(12.dp)
        ) {

            Text(
                text = "تابع التسجيل"
            )
        }
    }
}