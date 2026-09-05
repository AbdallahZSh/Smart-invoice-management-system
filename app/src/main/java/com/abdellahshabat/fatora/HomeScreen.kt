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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.filled.PersonOutline
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

/**
 * الشاشة الرئيسية - أصبحت الآن تُغذّى بالكامل من HomeUiState (بيانات حقيقية من Room)
 * بدل الأرقام والعمليات الوهمية اللي كانت مكتوبة يدوياً.
 */
@Composable
fun HomeScreen(
    state: HomeUiState,
    onVoiceClick: () -> Unit = {},
    onTextClick: () -> Unit = {},
    onProfileClick: () -> Unit = {},
    onCustomersClick: () -> Unit = {},
    onTransactionsClick: () -> Unit = {},
    onSettingsClick: () -> Unit = {},
    onViewAllTransactionsClick: () -> Unit = {}

) {

    CompositionLocalProvider(
        LocalLayoutDirection provides LayoutDirection.Rtl
    ) {

        Scaffold(
            containerColor = Color.White,

            bottomBar = {
                BottomNavigationBar(
                    onHomeClick = {},
                    onCustomersClick = onCustomersClick,
                    onTransactionsClick = onTransactionsClick,
                    onSettingsClick = onSettingsClick
                )
            }

        ) { paddingValues ->

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())   // ← جديد
                    .padding(paddingValues)
                    .padding(horizontal = 24.dp)
            ) {

                Spacer(modifier = Modifier.height(20.dp))

                Header(
                    shopName = state.shopName,
                    dateLabel = state.dateLabel,
                    onProfileClick = onProfileClick
                )

                Spacer(modifier = Modifier.height(26.dp))

                if (state.isLoading) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(94.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator()
                    }
                } else {
                    StatisticsSection(
                        totalDebts = state.totalDebts,
                        todaySales = state.todaySales
                    )

                    Spacer(modifier = Modifier.height(28.dp))

                    RecentTransactions(
                        transactions = state.recentTransactions,
                        onViewAllClick = onViewAllTransactionsClick
                    )
                }

                Spacer(modifier = Modifier.weight(1f))

                VoiceSection(
                    onVoiceClick = onVoiceClick
                )

                Spacer(modifier = Modifier.height(16.dp))

                TextInputButton(
                    onClick = onTextClick
                )

                Spacer(modifier = Modifier.height(20.dp))

            }
        }
    }
}

//2. الـ Header
@Composable
private fun Header(
    shopName: String,
    dateLabel: String,
    onProfileClick: () -> Unit
) {

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {

        Column {

            Text(
                text = shopName,
                fontSize = 14.sp,
                color = Color.Gray
            )

            Spacer(modifier = Modifier.height(3.dp))

            Text(
                text = dateLabel,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold
            )
        }

        Box(
            modifier = Modifier
                .size(52.dp)
                .clip(CircleShape)
                .background(Color(0xFFDCEBFF))
                .clickable {
                    onProfileClick()
                },
            contentAlignment = Alignment.Center
        ) {

            Icon(
                imageVector = Icons.Default.PersonOutline,
                contentDescription = "الملف الشخصي",
                tint = Color(0xFF1976D2),
                modifier = Modifier.size(28.dp)
            )
        }
    }
}

//3. بطاقات المبيعات والديون
@Composable
private fun StatisticsSection(
    totalDebts: Double,
    todaySales: Double
) {

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(14.dp)
    ) {

        StatisticCard(
            modifier = Modifier.weight(1f),
            title = "إجمالي الديون",
            value = totalDebts.toDisplayString(),
            isDebt = true
        )

        StatisticCard(
            modifier = Modifier.weight(1f),
            title = "مبيعات اليوم",
            value = todaySales.toDisplayString(),
            isDebt = false
        )
    }
}

@Composable
private fun StatisticCard(
    modifier: Modifier,
    title: String,
    value: String,
    isDebt: Boolean
) {

    Card(
        modifier = modifier.height(94.dp),
        shape = RoundedCornerShape(10.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        )
    ) {

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),

            verticalArrangement = Arrangement.Center
        ) {

            Text(
                text = title,
                fontSize = 14.sp,
                color = Color.Gray
            )

            Spacer(modifier = Modifier.height(4.dp))

            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {

                Text(
                    text = value,
                    fontSize = 23.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (isDebt) {
                        Color(0xFF9A6500)
                    } else {
                        Color.Black
                    }
                )

                Spacer(modifier = Modifier.width(5.dp))

                Text(
                    text = "₪",
                    fontSize = 17.sp,
                    color = if (isDebt) {
                        Color(0xFF9A6500)
                    } else {
                        Color.Black
                    }
                )
            }
        }
    }
}

//4. آخر العمليات
@Composable
private fun RecentTransactions(
    transactions: List<RecentTransactionUi>,
    onViewAllClick: () -> Unit
) {

    Column(
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "آخر العمليات",
                fontSize = 18.sp,
                fontWeight = FontWeight.Medium
            )

            TextButton(onClick = onViewAllClick) {
                Text(text = "عرض الكل", fontSize = 13.sp)
            }
        }

        Spacer(modifier = Modifier.height(4.dp))

        if (transactions.isEmpty()) {
            Text(
                text = "لسا ما في عمليات مسجلة",
                fontSize = 14.sp,
                color = Color.Gray
            )
            return@Column
        }

        transactions.forEachIndexed { index, transaction ->
            TransactionItem(
                customer = transaction.customerName,
                label = transaction.label,
                amount = transaction.amount,
                isPositive = transaction.isPositive
            )

            if (index != transactions.lastIndex) {
                Spacer(modifier = Modifier.height(8.dp))
            }
        }
    }
}


@Composable
private fun TransactionItem(
    customer: String,
    label: String,
    amount: Double,
    isPositive: Boolean
) {

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .height(48.dp),

        shape = RoundedCornerShape(12.dp),

        color = Color(0xFFFAFAFA)
    ) {

        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 14.dp),

            horizontalArrangement = Arrangement.SpaceBetween,

            verticalAlignment = Alignment.CenterVertically
        ) {

            Text(
                text = "$customer - $label",
                fontSize = 14.sp
            )

            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {

                val sign = if (isPositive) "-" else "+"

                Text(
                    text = "$sign${amount.toDisplayString()}",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (isPositive) {
                        Color(0xFF168A00)
                    } else {
                        Color(0xFF174A8B)
                    }
                )

                Spacer(modifier = Modifier.width(3.dp))

                Text(
                    text = "₪",
                    fontSize = 12.sp
                )
            }
        }
    }
}

//5. زر التسجيل الصوتي 🎙️
@Composable
private fun VoiceSection(
    onVoiceClick: () -> Unit
) {

    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Box(
            modifier = Modifier
                .size(110.dp)
                .clip(CircleShape)
                .background(Color(0xFF287BD5))
                .clickable {
                    onVoiceClick()
                },

            contentAlignment = Alignment.Center
        ) {

            Icon(
                imageVector = Icons.Default.Mic,
                contentDescription = "تسجيل صوتي",
                tint = Color.White,
                modifier = Modifier.size(48.dp)
            )
        }

        Spacer(modifier = Modifier.height(14.dp))

        Text(
            text = "“احكي: \"كم على أحمد؟\" أو \"سجل دين\"”",
            fontSize = 14.sp,
            color = Color.DarkGray,
            textAlign = TextAlign.Center
        )
    }
}

//6. زر الكتابة
@Composable
private fun TextInputButton(
    onClick: () -> Unit
) {

    OutlinedButton(
        onClick = onClick,

        modifier = Modifier
            .fillMaxWidth()
            .height(56.dp),

        shape = RoundedCornerShape(9.dp)
    ) {

        Icon(
            imageVector = Icons.Default.Edit,
            contentDescription = null,
            modifier = Modifier.size(21.dp)
        )

        Spacer(modifier = Modifier.width(10.dp))

        Text(
            text = "اكتب بدل ما تحكي",
            fontSize = 16.sp
        )
    }
}

//7. Bottom Navigation
@Composable
private fun BottomNavigationBar(
    onHomeClick: () -> Unit,
    onCustomersClick: () -> Unit,
    onTransactionsClick: () -> Unit,
    onSettingsClick: () -> Unit
) {

    NavigationBar(
        containerColor = Color.White,
        tonalElevation = 0.dp
    ) {

        NavigationBarItem(
            selected = true,
            onClick = onHomeClick,

            icon = {
                Icon(
                    imageVector = Icons.Default.Home,
                    contentDescription = "الرئيسية"
                )
            }
        )

        NavigationBarItem(
            selected = false,
            onClick = onCustomersClick,

            icon = {
                Icon(
                    imageVector = Icons.Default.People,
                    contentDescription = "العملاء"
                )
            }
        )

        NavigationBarItem(
            selected = false,
            onClick = onTransactionsClick,

            icon = {
                Icon(
                    imageVector = Icons.Default.Description,
                    contentDescription = "الفواتير"
                )
            }
        )

        NavigationBarItem(
            selected = false,
            onClick = onSettingsClick,

            icon = {
                Icon(
                    imageVector = Icons.Default.Settings,
                    contentDescription = "الإعدادات"
                )
            }
        )
    }
}

/** تنسيق الرقم بدون كسور عشرية إذا كان صحيحاً (45 بدل 45.0). */
private fun Double.toDisplayString(): String {
    return if (this == this.toLong().toDouble()) this.toLong().toString() else this.toString()
}