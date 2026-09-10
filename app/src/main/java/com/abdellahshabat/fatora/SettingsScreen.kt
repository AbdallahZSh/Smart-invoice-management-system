package com.abdellahshabat.fatora

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Chat
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.HelpOutline
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.Storage
import androidx.compose.material.icons.filled.Storefront
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

/**
 * شاشة الإعدادات الرئيسية - بروفايل المحل بالأعلى (عرض + تعديل بـ Popup)،
 * وتحته قائمة تصنيفات (زي واتساب بالضبط) بتودي لشاشات فرعية.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    state: SettingsUiState,
    onShopNameChange: (String) -> Unit,
    onOwnerPhoneChange: (String) -> Unit,
    onSaveClick: () -> Unit,
    onOpenLists: () -> Unit,
    onOpenBackup: () -> Unit,
    onOpenStorage: () -> Unit,
    onOpenAppearance: () -> Unit,
    onOpenHelp: () -> Unit,
    onOpenAccount: () -> Unit,
    onOpenPrivacy: () -> Unit,
    onOpenNotifications: () -> Unit,
    onOpenLanguage: () -> Unit,
    onBackClick: () -> Unit
) {
    var showEditProfileDialog by remember { mutableStateOf(false) }

    CompositionLocalProvider(
        LocalLayoutDirection provides LayoutDirection.Rtl
    ) {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text("الإعدادات") },
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

            if (state.isLoading) {
                Box(
                    modifier = Modifier.fillMaxSize().padding(paddingValues),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
                return@Scaffold
            }

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
            ) {

                Spacer(modifier = Modifier.height(16.dp))

                // --- بروفايل المحل ---
                Box(
                    modifier = Modifier
                        .size(64.dp)
                        .align(Alignment.CenterHorizontally)
                        .background(Color(0xFFDCEBFF), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Filled.Storefront,
                        contentDescription = null,
                        tint = Color(0xFF1976D2),
                        modifier = Modifier.size(32.dp)
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                Text(
                    text = state.shopName.ifBlank { "اسم المحل" },
                    fontSize = 17.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (state.shopName.isBlank()) Color.Gray else Color.Black,
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(2.dp))

                Text(
                    text = state.ownerPhone.ifBlank { "رقم صاحب المحل" },
                    fontSize = 13.sp,
                    color = Color.Gray,
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(6.dp))

                TextButton(
                    onClick = { showEditProfileDialog = true },
                    modifier = Modifier.align(Alignment.CenterHorizontally)
                ) {
                    Icon(Icons.Filled.Edit, contentDescription = null, modifier = Modifier.size(14.dp))
                    Spacer(modifier = Modifier.size(6.dp))
                    Text("تعديل بيانات المحل", fontSize = 13.sp)
                }

                Spacer(modifier = Modifier.height(8.dp))

                // --- قائمة التصنيفات ---
                SettingsRow(Icons.Filled.AccountCircle, "الحساب", "إشعارات الأمان") { onOpenAccount() }
                SettingsRow(Icons.Filled.Lock, "الخصوصية", "العملاء المحظورين") { onOpenPrivacy() }
                SettingsRow(Icons.Filled.List, "القوائم", "إدارة العملاء والفواتير") { onOpenLists() }
                SettingsRow(Icons.Filled.Chat, "الدردشات", "سجل الفواتير ونسخة احتياطية") { onOpenBackup() }
                SettingsRow(Icons.Filled.Palette, "الشكل", "سمة التطبيق وأيقونته") { onOpenAppearance() }
                SettingsRow(Icons.Filled.Notifications, "الإشعارات", "") { onOpenNotifications() }
                SettingsRow(Icons.Filled.Storage, "التخزين والبيانات", "إحصائيات وحذف البيانات") { onOpenStorage() }
                SettingsRow(Icons.Filled.Language, "لغة التطبيق", "") { onOpenLanguage() }
                SettingsRow(Icons.Filled.HelpOutline, "المساعدة والملاحظات", "") { onOpenHelp() }

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "Fatora — نسخة 1.0",
                    fontSize = 12.sp,
                    color = Color.Gray,
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }

    if (showEditProfileDialog) {
        EditShopProfileDialog(
            state = state,
            onShopNameChange = onShopNameChange,
            onOwnerPhoneChange = onOwnerPhoneChange,
            onSave = {
                onSaveClick()
                showEditProfileDialog = false
            },
            onDismiss = { showEditProfileDialog = false }
        )
    }
}

@Composable
private fun SettingsRow(
    icon: ImageVector,
    title: String,
    subtitle: String,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(horizontal = 20.dp, vertical = 14.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(icon, contentDescription = null, tint = Color(0xFF555555), modifier = Modifier.size(22.dp))
            Column(modifier = Modifier.padding(start = 16.dp)) {
                Text(title, fontSize = 15.sp)
                if (subtitle.isNotBlank()) {
                    Text(subtitle, fontSize = 12.sp, color = Color.Gray)
                }
            }
        }
        Icon(
            imageVector = Icons.AutoMirrored.Filled.KeyboardArrowLeft,
            contentDescription = null,
            tint = Color.LightGray
        )
    }
}

@Composable
private fun EditShopProfileDialog(
    state: SettingsUiState,
    onShopNameChange: (String) -> Unit,
    onOwnerPhoneChange: (String) -> Unit,
    onSave: () -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("تعديل بيانات المحل") },
        text = {
            Column {
                OutlinedTextField(
                    value = state.shopName,
                    onValueChange = onShopNameChange,
                    label = { Text("اسم المحل") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )
                Spacer(modifier = Modifier.height(12.dp))
                OutlinedTextField(
                    value = state.ownerPhone,
                    onValueChange = onOwnerPhoneChange,
                    label = { Text("رقم صاحب المحل") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )
                if (state.savedMessage != null) {
                    Spacer(modifier = Modifier.height(10.dp))
                    Text(state.savedMessage, color = Color(0xFF168A00), fontSize = 13.sp)
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onSave, enabled = !state.isSaving) { Text("حفظ") }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("إلغاء") }
        }
    )
}