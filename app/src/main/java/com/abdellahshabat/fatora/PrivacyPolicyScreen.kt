package com.abdellahshabat.fatora

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Storage
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun PrivacyPolicyScreen(
    onBackClick: () -> Unit
) {
    CompositionLocalProvider(
        LocalLayoutDirection provides LayoutDirection.Rtl
    ) {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = {
                        Text("سياسة الخصوصية")
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
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {

                PrivacyHeaderCard()

                PrivacySection(
                    icon = Icons.Filled.Storage,
                    title = "ما هي البيانات التي يخزنها التطبيق؟",
                    text = """
                        يقوم تطبيق Fatora بتخزين بعض البيانات اللازمة لتشغيل التطبيق وإدارة حسابات العملاء والمعاملات المالية.

                        وتشمل هذه البيانات:
                        
                        • اسم العميل.
                        • رقم هاتف العميل إذا تم إدخاله.
                        • تاريخ إنشاء العميل.
                        • بيانات المعاملات المالية.
                        • اسم المنتج المرتبط بالمعاملة إذا تم إدخاله.
                        • قيمة المعاملة.
                        • العملة المستخدمة.
                        • نوع المعاملة، مثل الدين أو الدفع.
                        • تاريخ إنشاء المعاملة.
                    """.trimIndent()
                )

                PrivacySection(
                    icon = Icons.Filled.Info,
                    title = "بيانات المحل",
                    text = """
                        يمكن للتطبيق حفظ بعض بيانات المحل التي يقوم المستخدم بإدخالها، مثل:

                        • اسم المحل.
                        • رقم هاتف صاحب المحل.

                        يتم استخدام هذه البيانات لعرض معلومات المحل داخل التطبيق وتحسين تجربة الاستخدام.
                    """.trimIndent()
                )

                PrivacySection(
                    icon = Icons.Filled.Security,
                    title = "تفضيلات التطبيق",
                    text = """
                        يحفظ التطبيق بعض التفضيلات المحلية التي يختارها المستخدم، مثل وضع المظهر.

                        ومن أمثلة ذلك:

                        • الوضع الفاتح.
                        • الوضع الداكن.
                        • الوضع التلقائي حسب إعدادات الجهاز.
                    """.trimIndent()
                )

                PrivacySection(
                    icon = Icons.Filled.Lock,
                    title = "كيف تتم حماية البيانات؟",
                    text = """
                        يتم تخزين بيانات العملاء والمعاملات المالية محليًا باستخدام قاعدة بيانات Room داخل التطبيق.

                        كما يتم تخزين بعض إعدادات التطبيق باستخدام SharedPreferences.

                        لا يتم تخزين كلمات المرور داخل قاعدة بيانات Fatora.

                        عند اكتمال نظام تسجيل الدخول، سيتم استخدام Firebase Authentication لإدارة عملية المصادقة والحسابات.
                    """.trimIndent()
                )

                PrivacySection(
                    icon = Icons.Filled.Storage,
                    title = "أين يتم تخزين البيانات؟",
                    text = """
                        يتم حاليًا تخزين بيانات العملاء والمعاملات داخل قاعدة البيانات المحلية للتطبيق.

                        ويتم تخزين إعدادات المحل وبعض تفضيلات التطبيق باستخدام التخزين المحلي.

                        أما بيانات المصادقة والحساب، فسيتم التعامل معها من خلال Firebase Authentication بعد اكتمال نظام تسجيل الدخول في التطبيق.
                    """.trimIndent()
                )

                PrivacySection(
                    icon = Icons.Filled.Info,
                    title = "الذكاء الاصطناعي",
                    text = """
                        قد يحتوي التطبيق على ميزات تعتمد على الذكاء الاصطناعي.

                        الذكاء الاصطناعي لا يمتلك وصولًا مباشرًا إلى قاعدة بيانات Room الخاصة بالتطبيق.

                        أي ميزة تعتمد على خدمة خارجية يجب أن يتم التعامل معها وفق طريقة الاتصال والصلاحيات الخاصة بتلك الخدمة.
                    """.trimIndent()
                )

                PrivacySection(
                    icon = Icons.Filled.Security,
                    title = "مشاركة البيانات",
                    text = """
                        لا يقوم التطبيق بمشاركة بيانات العملاء أو المعاملات مع أطراف أخرى بشكل تلقائي من خلال قاعدة البيانات المحلية.

                        وأي خدمة خارجية تتم إضافتها مستقبلًا يجب أن يتم تحديد البيانات التي تحتاج إليها وطريقة استخدامها بشكل واضح.
                    """.trimIndent()
                )

                PrivacySection(
                    icon = Icons.Filled.Info,
                    title = "حقوق المستخدم",
                    text = """
                        يهدف التطبيق إلى إعطاء المستخدم القدرة على إدارة البيانات التي يدخلها داخل التطبيق.

                        وتشمل ذلك إدارة العملاء والمعاملات وبيانات المحل.

                        سيتم تطوير خيارات إضافية لإدارة الحساب والبيانات عند اكتمال نظام Authentication.
                    """.trimIndent()
                )

                PrivacySection(
                    icon = Icons.Filled.Security,
                    title = "تحديث سياسة الخصوصية",
                    text = """
                        قد يتم تحديث سياسة الخصوصية عند إضافة ميزات جديدة أو تغيير طريقة تخزين البيانات أو التعامل معها.

                        عند إجراء تغييرات مهمة، سيتم تحديث هذه الصفحة لتوضيح طريقة التعامل مع البيانات.
                    """.trimIndent()
                )

                Spacer(
                    modifier = Modifier.height(8.dp)
                )

                Text(
                    text = "آخر تحديث: سبتمبر 2026",
                    modifier = Modifier.fillMaxWidth(),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Composable
private fun PrivacyHeaderCard() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer
        )
    ) {
        Column(
            modifier = Modifier.padding(18.dp),
            horizontalAlignment = Alignment.Start
        ) {
            Text(
                text = "خصوصيتك مهمة",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )

            Spacer(
                modifier = Modifier.height(8.dp)
            )

            Text(
                text = "يوضح هذا القسم أنواع البيانات التي يستخدمها تطبيق Fatora وكيف يتم تخزينها واستخدامها.",
                style = MaterialTheme.typography.bodyMedium,
                lineHeight = 21.sp
            )
        }
    }
}

@Composable
private fun PrivacySection(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    title: String,
    text: String
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {

            androidx.compose.foundation.layout.Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null
                )

                Text(
                    text = title,
                    modifier = Modifier.padding(start = 12.dp),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(
                modifier = Modifier.height(10.dp)
            )

            Text(
                text = text,
                style = MaterialTheme.typography.bodyMedium,
                lineHeight = 22.sp
            )
        }
    }
}