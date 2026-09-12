package com.abdellahshabat.fatora

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.height
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.filled.DeleteForever
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.PrivacyTip
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.VpnKey
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.runtime.CompositionLocalProvider

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PrivacyScreen(
    onBackClick: () -> Unit,
    onChangePassword: () -> Unit,
    onAccountSecurity: () -> Unit,
    onPrivacyPolicy: () -> Unit,
    onDeleteAccount: () -> Unit,
    onDataInformation: () -> Unit
) {

    CompositionLocalProvider(
        LocalLayoutDirection provides LayoutDirection.Rtl
    ) {

        Scaffold(
            topBar = {
                TopAppBar(
                    title = {
                        Text("الخصوصية والأمان")
                    },
                    navigationIcon = {
                        IconButton(
                            onClick = onBackClick
                        ) {
                            Icon(
                                imageVector =
                                    Icons.AutoMirrored.Filled.ArrowBack,
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

                Spacer(
                    modifier = Modifier.height(8.dp)
                )

                PrivacyRow(
                    icon = Icons.Filled.VpnKey,
                    title = "تغيير كلمة المرور",
                    subtitle = "تحديث كلمة مرور الحساب"
                ) {
                    onChangePassword()
                }

                PrivacyRow(
                    icon = Icons.Filled.Security,
                    title = "حماية الحساب",
                    subtitle = "معلومات عن أمان حسابك"
                ) {
                    onAccountSecurity()
                }

                PrivacyRow(
                    icon = Icons.Filled.PrivacyTip,
                    title = "سياسة الخصوصية",
                    subtitle = "كيف نتعامل مع بياناتك"
                ) {
                    onPrivacyPolicy()
                }

                PrivacyRow(
                    icon = Icons.Filled.DeleteForever,
                    title = "حذف الحساب",
                    subtitle = "حذف الحساب والبيانات"
                ) {
                    onDeleteAccount()
                }

                PrivacyRow(
                    icon = Icons.Filled.Info,
                    title = "معلومات عن البيانات",
                    subtitle = "البيانات التي يخزنها Fatora"
                ) {
                    onDataInformation()
                }
            }
        }
    }
}

@Composable
private fun PrivacyRow(
    icon: ImageVector,
    title: String,
    subtitle: String,
    onClick: () -> Unit
) {

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable {
                onClick()
            }
            .padding(
                horizontal = 20.dp,
                vertical = 16.dp
            ),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {

        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {

            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = Color(0xFF555555),
                modifier = Modifier.size(23.dp)
            )

            Column(
                modifier = Modifier.padding(
                    start = 16.dp
                )
            ) {

                Text(
                    text = title,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Medium
                )

                Spacer(
                    modifier = Modifier.height(3.dp)
                )

                Text(
                    text = subtitle,
                    fontSize = 12.sp,
                    color = Color.Gray
                )
            }
        }

        Icon(
            imageVector =
                Icons.AutoMirrored.Filled.KeyboardArrowLeft,
            contentDescription = null,
            tint = Color.LightGray
        )
    }
}