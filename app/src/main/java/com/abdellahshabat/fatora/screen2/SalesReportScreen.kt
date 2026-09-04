package com.abdellahshabat.fatora.screen2

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ChevronLeft
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.abdellahshabat.fatora.data.database.entity.TransactionType

/**
 * شاشة كشف المبيعات المفلتر - جدول قابل للتمرير أفقياً، مع فلترة
 * بيوم أو شهر (تنقل بالأسهم بدل Date Picker، عشان أبسط بمرحلة الـ MVP)
 * وفلترة إضافية بنوع العملية.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SalesReportScreen(
    state: SalesReportUiState,
    onFilterModeChange: (ReportFilterMode) -> Unit,
    onTypeFilterChange: (ReportTypeFilter) -> Unit,
    onPreviousClick: () -> Unit,
    onNextClick: () -> Unit,
    onBackClick: () -> Unit
) {
    CompositionLocalProvider(
        LocalLayoutDirection provides LayoutDirection.Rtl
    ) {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text("كشف المبيعات") },
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
                    .padding(horizontal = 16.dp, vertical = 12.dp)
            ) {

                FilterModeToggle(
                    selectedMode = state.filterMode,
                    onModeChange = onFilterModeChange
                )

                Spacer(modifier = Modifier.height(10.dp))

                DateNavigator(
                    dateLabel = state.dateLabel,
                    onPreviousClick = onPreviousClick,
                    onNextClick = onNextClick
                )

                Spacer(modifier = Modifier.height(10.dp))

                TypeFilterChips(
                    selectedFilter = state.typeFilter,
                    onFilterChange = onTypeFilterChange
                )

                Spacer(modifier = Modifier.height(12.dp))

                SummaryStrip(
                    totalCount = state.totalCount,
                    netAmount = state.netAmount
                )

                Spacer(modifier = Modifier.height(10.dp))

                when {
                    state.isLoading -> {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            CircularProgressIndicator()
                        }
                    }

                    state.rows.isEmpty() -> {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "ما في عمليات بهاي الفترة",
                                color = Color.Gray
                            )
                        }
                    }

                    else -> {
                        ReportTable(rows = state.rows)

                        Spacer(modifier = Modifier.height(6.dp))

                        Text(
                            text = "مرّر يمين/يسار لعرض كل الأعمدة ←",
                            fontSize = 11.sp,
                            color = Color.Gray,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun FilterModeToggle(
    selectedMode: ReportFilterMode,
    onModeChange: (ReportFilterMode) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color(0xFFF2F2F2), RoundedCornerShape(10.dp))
            .padding(3.dp)
    ) {
        ToggleOption(
            text = "حسب اليوم",
            isSelected = selectedMode == ReportFilterMode.DAY,
            modifier = Modifier.weight(1f),
            onClick = { onModeChange(ReportFilterMode.DAY) }
        )
        ToggleOption(
            text = "حسب الشهر",
            isSelected = selectedMode == ReportFilterMode.MONTH,
            modifier = Modifier.weight(1f),
            onClick = { onModeChange(ReportFilterMode.MONTH) }
        )
    }
}

@Composable
private fun ToggleOption(
    text: String,
    isSelected: Boolean,
    modifier: Modifier,
    onClick: () -> Unit
) {
    val backgroundColor = if (isSelected) Color(0xFF287BD5) else Color.Transparent
    val textColor = if (isSelected) Color.White else Color.Gray

    Box(
        modifier = modifier
            .background(backgroundColor, RoundedCornerShape(8.dp))
            .clickable { onClick() }
            .padding(vertical = 7.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            fontSize = 13.sp,
            color = textColor
        )
    }
}

@Composable
private fun DateNavigator(
    dateLabel: String,
    onPreviousClick: () -> Unit,
    onNextClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color(0xFFF9F9F9), RoundedCornerShape(10.dp))
            .padding(horizontal = 6.dp, vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(onClick = onPreviousClick) {
            Icon(Icons.Default.ChevronRight, contentDescription = "السابق")
        }

        Text(
            text = dateLabel,
            fontSize = 13.sp,
            fontWeight = FontWeight.Medium
        )

        IconButton(onClick = onNextClick) {
            Icon(Icons.Default.ChevronLeft, contentDescription = "التالي")
        }
    }
}

@Composable
private fun TypeFilterChips(
    selectedFilter: ReportTypeFilter,
    onFilterChange: (ReportTypeFilter) -> Unit
) {
    Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
        FilterChip(
            text = "الكل",
            isSelected = selectedFilter == ReportTypeFilter.ALL,
            onClick = { onFilterChange(ReportTypeFilter.ALL) }
        )
        FilterChip(
            text = "ديون",
            isSelected = selectedFilter == ReportTypeFilter.DEBT,
            onClick = { onFilterChange(ReportTypeFilter.DEBT) }
        )
        FilterChip(
            text = "دفعات",
            isSelected = selectedFilter == ReportTypeFilter.PAYMENT,
            onClick = { onFilterChange(ReportTypeFilter.PAYMENT) }
        )
    }
}

@Composable
private fun FilterChip(
    text: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    if (isSelected) {
        Button(
            onClick = onClick,
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFDCEBFF)),
            contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 14.dp, vertical = 6.dp)
        ) {
            Text(text, fontSize = 12.sp, color = Color(0xFF1976D2))
        }
    } else {
        OutlinedButton(
            onClick = onClick,
            contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 14.dp, vertical = 6.dp)
        ) {
            Text(text, fontSize = 12.sp)
        }
    }
}

@Composable
private fun SummaryStrip(
    totalCount: Int,
    netAmount: Double
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color(0xFFF9F9F9), RoundedCornerShape(10.dp))
            .padding(horizontal = 12.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(text = "عدد العمليات: $totalCount", fontSize = 12.sp, color = Color.Gray)
        Text(
            text = "الصافي: ${netAmount.toDisplayString()} ₪",
            fontSize = 13.sp,
            fontWeight = FontWeight.Medium,
            color = Color(0xFF9A6500)
        )
    }
}

@Composable
private fun ReportTable(rows: List<ReportRowUi>) {
    val columnWidth = 90.dp

    Surface(
        shape = RoundedCornerShape(10.dp),
        color = Color.White
    ) {
        Row(
            modifier = Modifier.horizontalScroll(rememberScrollState())
        ) {
            Column {
                // Header row
                Row(modifier = Modifier.background(Color(0xFFF2F2F2))) {
                    HeaderCell("العميل", columnWidth)
                    HeaderCell("المنتج", columnWidth)
                    HeaderCell("المبلغ", columnWidth)
                    HeaderCell("الوقت", columnWidth)
                    HeaderCell("النوع", columnWidth)
                }

                rows.forEach { row ->
                    Row {
                        DataCell(row.customerName, columnWidth)
                        DataCell(row.product, columnWidth)
                        DataCell(
                            text = "${row.amount.toDisplayString()} ₪",
                            width = columnWidth,
                            color = if (row.type == TransactionType.PAYMENT) Color(0xFF168A00) else Color(0xFF9A6500)
                        )
                        DataCell(row.timeLabel, columnWidth, color = Color.Gray)
                        Box(
                            modifier = Modifier
                                .width(columnWidth)
                                .padding(horizontal = 10.dp, vertical = 8.dp),
                            contentAlignment = Alignment.CenterStart
                        ) {
                            TypeBadge(row.type)
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun HeaderCell(text: String, width: androidx.compose.ui.unit.Dp) {
    Text(
        text = text,
        fontSize = 12.sp,
        fontWeight = FontWeight.Medium,
        color = Color.Gray,
        modifier = Modifier
            .width(width)
            .padding(horizontal = 10.dp, vertical = 8.dp)
    )
}

@Composable
private fun DataCell(text: String, width: androidx.compose.ui.unit.Dp, color: Color = Color.Black) {
    Text(
        text = text,
        fontSize = 12.sp,
        color = color,
        modifier = Modifier
            .width(width)
            .padding(horizontal = 10.dp, vertical = 8.dp)
    )
}

@Composable
private fun TypeBadge(type: TransactionType) {
    val isPayment = type == TransactionType.PAYMENT
    val bg = if (isPayment) Color(0xFFE1F5EE) else Color(0xFFFAEEDA)
    val fg = if (isPayment) Color(0xFF168A00) else Color(0xFF9A6500)

    Surface(color = bg, shape = RoundedCornerShape(6.dp)) {
        Text(
            text = if (isPayment) "دفعة" else "دين",
            fontSize = 10.sp,
            color = fg,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
        )
    }
}

private fun Double.toDisplayString(): String {
    return if (this == this.toLong().toDouble()) this.toLong().toString() else this.toString()
}