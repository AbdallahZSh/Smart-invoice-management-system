package com.abdellahshabat.fatora

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.SwapHoriz
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp

/**
 * شاشة تسجيل دفعة - نسخة طبق الأصل من AddDebtScreen، بدون حقل المنتج
 * (الدفعة ما إلها منتج مرتبط فيها).
 *
 * @param onSaveSuccess يُستدعى مرة وحدة بعد نجاح الحفظ (للرجوع لـ Home مثلاً)
 * @param onSwitchToDebtClick يُستدعى لو المستخدم بدو يبدّل لشاشة "إضافة دين" بدالها
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddPaymentScreen(
    state: AddPaymentUiState,
    onCustomerNameChange: (String) -> Unit,
    onAmountChange: (String) -> Unit,
    onSaveClick: () -> Unit,
    onSaveSuccess: () -> Unit,
    onBackClick: () -> Unit,
    onSwitchToDebtClick: () -> Unit
) {

    LaunchedEffect(state.saveSuccess) {
        if (state.saveSuccess) {
            onSaveSuccess()
        }
    }

    CompositionLocalProvider(
        LocalLayoutDirection provides LayoutDirection.Rtl
    ) {

        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text("تسجيل دفعة") },
                    navigationIcon = {
                        IconButton(onClick = onBackClick) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = "رجوع"
                            )
                        }
                    },
                    actions = {
                        IconButton(onClick = onSwitchToDebtClick) {
                            Icon(
                                imageVector = Icons.Filled.SwapHoriz,
                                contentDescription = "التبديل لإضافة دين"
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
                    .padding(horizontal = 24.dp, vertical = 16.dp)
            ) {

                OutlinedTextField(
                    value = state.customerName,
                    onValueChange = onCustomerNameChange,
                    label = { Text("اسم العميل") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(16.dp))

                OutlinedTextField(
                    value = state.amountText,
                    onValueChange = onAmountChange,
                    label = { Text("المبلغ (شيكل)") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(8.dp))

                if (state.errorMessage != null) {
                    Text(
                        text = state.errorMessage,
                        color = MaterialTheme.colorScheme.error
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                }

                Spacer(modifier = Modifier.height(16.dp))

                Button(
                    onClick = onSaveClick,
                    enabled = state.isSaveEnabled,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp)
                ) {
                    if (state.isSaving) {
                        CircularProgressIndicator(
                            modifier = Modifier.height(20.dp),
                            color = Color.White
                        )
                    } else {
                        Text("حفظ الدفعة")
                    }
                }
            }
        }
    }
}