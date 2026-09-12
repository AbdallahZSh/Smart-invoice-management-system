package com.abdellahshabat.fatora

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.AttachMoney
import androidx.compose.material.icons.filled.Cloud
import androidx.compose.material.icons.filled.Database
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.filled.Store
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun DataInformationScreen(
    onBackClick: () -> Unit
) {
    CompositionLocalProvider(
        LocalLayoutDirection provides LayoutDirection.Rtl
    ) {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = {
                        Text("معلومات عن البيانات")
                    },
                    navigationIcon = {
                        IconButton(
                            onClick = onBackClick
                        ) {
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
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {

                Text(
                    text = "ما البيانات التي يخزنها Fatora؟",
                    style = MaterialTheme.typography.titleLarge
                )

                Text(
                    text = "يوضح هذا القسم أنواع البيانات التي يستخدمها التطبيق وأماكن تخزينها والغرض منها.",
                    style = MaterialTheme.typography.bodyMedium
                )

                DataSection(
                    icon = Icons.Filled.People,
                    title = "بيانات العملاء",
                    storage = "يتم تخزينها محليًا باستخدام Room.",
                    description = """
                        يحتفظ التطبيق بالبيانات اللازمة لإدارة العملاء، ومنها:
                        
                        • اسم العميل
                        • رقم الهاتف عند توفره
                        • معرف العميل
                        • تاريخ إنشاء العميل
                    """.trimIndent()
                )

                DataSection(
                    icon = Icons.Filled.AttachMoney,
                    title = "المعاملات المالية",
                    storage = "يتم تخزينها محليًا باستخدام Room.",
                    description = """
                        يستخدم التطبيق هذه البيانات لإدارة الديون والمدفوعات، ومنها:
                        
                        • اسم المنتج عند توفره
                        • قيمة المبلغ
                        • العملة
                        • نوع العملية: دين أو دفعة
                        • العميل المرتبط بالعملية
                        • تاريخ العملية
                    """.trimIndent()
                )

                DataSection(
                    icon = Icons.Filled.Store,
                    title = "بيانات المحل",
                    storage = "يتم تخزينها محليًا باستخدام SharedPreferences.",
                    description = """
                        يحفظ التطبيق بعض معلومات المحل التي يتم إدخالها من الإعدادات، ومنها:
                        
                        • اسم المحل
                        • رقم هاتف صاحب المحل
                    """.trimIndent()
                )

                DataSection(
                    icon = Icons.Filled.Palette,
                    title = "تفضيلات التطبيق",
                    storage = "يتم تخزينها محليًا باستخدام SharedPreferences.",
                    description = """
                        يحفظ التطبيق تفضيلات بسيطة تساعده على تذكر اختيارك، ومنها:
                        
                        • وضع المظهر: فاتح
                        • وضع المظهر: داكن
                        • وضع المظهر: تلقائي حسب النظام
                    """.trimIndent()
                )

                DataSection(
                    icon = Icons.Filled.Lock,
                    title = "بيانات الحساب",
                    storage = "تتم إدارة بيانات المصادقة بواسطة Firebase Authentication.",
                    description = """
                        عند اكتمال نظام تسجيل الحسابات، ستتم إدارة بيانات المصادقة من خلال Firebase Authentication.

                        هذه الشاشة لا تعرض كلمة المرور ولا تقوم بتخزينها داخل قاعدة بيانات التطبيق.
                    """.trimIndent()
                )

                DataSection(
                    icon = Icons.Filled.Database,
                    title = "أين يتم تخزين البيانات؟",
                    storage = "التخزين المحلي الحالي للتطبيق.",
                    description = """
                        يستخدم Fatora حاليًا طريقتين رئيسيتين للتخزين المحلي:

                        • Room: لتخزين العملاء والمعاملات المالية.
                        • SharedPreferences: لتخزين إعدادات المحل وتفضيلات المظهر.

                        قاعدة Room الحالية تحتوي على جدول العملاء وجدول المعاملات.
                    """.trimIndent()
                )

                DataSection(
                    icon = Icons.Filled.AccountCircle,
                    title = "الذكاء الاصطناعي",
                    storage = "لا يملك الذكاء الاصطناعي وصولًا مباشرًا إلى قاعدة بيانات Room.",
                    description = """
                        يمكن استخدام الذكاء الاصطناعي لتنفيذ وظائف محددة يسمح بها التطبيق.

                        لا يستطيع الذكاء الاصطناعي قراءة قاعدة بيانات التطبيق من تلقاء نفسه أو الوصول المباشر إلى بيانات العملاء والمعاملات.
                    """.trimIndent()
                )

                DataSection(
                    icon = Icons.Filled.Info,
                    title = "ملاحظة مهمة",
                    storage = "معلومات التخزين تعكس البنية الحالية للتطبيق.",
                    description = """
                        قد تتغير طريقة تخزين البيانات أو الخدمات المستخدمة مستقبلًا مع تطوير Fatora وإضافة ميزات جديدة.
                    """.trimIndent()
                )

                Spacer(
                    modifier = Modifier.height(12.dp)
                )
            }
        }
    }
}

@Composable
private fun DataSection(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    title: String,
    storage: String,
    description: String
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null
                )

                Text(
                    text = title,
                    modifier = Modifier.padding(start = 12.dp),
                    style = MaterialTheme.typography.titleMedium
                )
            }

            Spacer(
                modifier = Modifier.height(8.dp)
            )

            Text(
                text = storage,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.primary
            )

            Spacer(
                modifier = Modifier.height(8.dp)
            )

            Text(
                text = description,
                style = MaterialTheme.typography.bodyMedium,
                lineHeight = 21.sp
            )
        }
    }
}