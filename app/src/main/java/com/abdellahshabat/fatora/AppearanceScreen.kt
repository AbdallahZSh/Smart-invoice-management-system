package com.abdellahshabat.fatora

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Scaffold
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
import com.abdellahshabat.fatora.util.ThemeMode

/**
 * شاشة "الشكل" - اختيار وضع المظهر (فاتح/غامق/حسب الجهاز) بشكل حقيقي وفعّال.
 *
 * ⚠️ ملاحظة صادقة: اختيار الوضع بيتحفظ ويتفعل فعلياً على مستوى الـ Theme،
 * بس أغلب الشاشات الحالية (Home، الفواتير...) مكتوبة بألوان ثابتة (أبيض/أسود)
 * مش بألوان الـ Theme - فهي لسا ما رح تتغير بصرياً بالوضع الغامق لحد ما نعدلها
 * لاحقاً تستخدم ألوان الـ Theme بدل الألوان الثابتة.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppearanceScreen(
    currentThemeMode: ThemeMode,
    onThemeModeChange: (ThemeMode) -> Unit,
    onBackClick: () -> Unit
) {
    CompositionLocalProvider(
        LocalLayoutDirection provides LayoutDirection.Rtl
    ) {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text("الشكل") },
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
                    .padding(horizontal = 20.dp, vertical = 16.dp)
            ) {

                Text("مظهر التطبيق", fontSize = 15.sp, fontWeight = FontWeight.Medium)

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = "التغيير هون بيأثر على شريط الحالة وبعض عناصر الواجهة. باقي الشاشات لسا قيد التطوير عشان تدعم الوضع الغامق بالكامل.",
                    fontSize = 12.sp,
                    color = Color.Gray
                )

                Spacer(modifier = Modifier.height(16.dp))

                Column(modifier = Modifier.selectableGroup()) {
                    ThemeOption(
                        label = "فاتح",
                        selected = currentThemeMode == ThemeMode.LIGHT,
                        onClick = { onThemeModeChange(ThemeMode.LIGHT) }
                    )
                    ThemeOption(
                        label = "غامق",
                        selected = currentThemeMode == ThemeMode.DARK,
                        onClick = { onThemeModeChange(ThemeMode.DARK) }
                    )
                    ThemeOption(
                        label = "حسب إعدادات الجهاز",
                        selected = currentThemeMode == ThemeMode.SYSTEM,
                        onClick = { onThemeModeChange(ThemeMode.SYSTEM) }
                    )
                }

                Spacer(modifier = Modifier.height(28.dp))

                Text("أيقونة التطبيق", fontSize = 15.sp, fontWeight = FontWeight.Medium, color = Color.Gray)
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "تغيير أيقونة التطبيق قريباً - يحتاج تعديل إضافي بإعدادات المشروع.",
                    fontSize = 12.sp,
                    color = Color.Gray
                )
            }
        }
    }
}

@Composable
private fun ThemeOption(
    label: String,
    selected: Boolean,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .selectable(
                selected = selected,
                onClick = onClick
            )
            .padding(vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        RadioButton(selected = selected, onClick = onClick)
        Text(label, fontSize = 14.sp, modifier = Modifier.padding(start = 8.dp))
    }
}