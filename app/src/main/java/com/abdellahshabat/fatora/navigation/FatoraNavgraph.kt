package com.abdellahshabat.fatora.navigation

import android.widget.Toast
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.abdellahshabat.fatora.AddDebtScreen
import com.abdellahshabat.fatora.HomeScreen
import com.abdellahshabat.fatora.QueryResponseScreen
import com.abdellahshabat.fatora.customer.CustomersScreen
import com.abdellahshabat.fatora.screen1.InvoicesScreen
import com.abdellahshabat.fatora.di.AppContainer
import com.abdellahshabat.fatora.screen2.SalesReportScreen
import com.abdellahshabat.fatora.screen2.SalesReportViewModel
import com.abdellahshabat.fatora.screen2.SalesReportViewModelFactory
import com.abdellahshabat.fatora.viewmodel.AddDebtViewModel
import com.abdellahshabat.fatora.viewmodel.AddDebtViewModelFactory
import com.abdellahshabat.fatora.viewmodel.CustomerDetailViewModel
import com.abdellahshabat.fatora.viewmodel.CustomerDetailViewModelFactory
import com.abdellahshabat.fatora.viewmodel.CustomersViewModel
import com.abdellahshabat.fatora.viewmodel.CustomersViewModelFactory
import com.abdellahshabat.fatora.viewmodel.HomeViewModel
import com.abdellahshabat.fatora.viewmodel.HomeViewModelFactory
import com.abdellahshabat.fatora.viewmodel.InvoicesViewModel
import com.abdellahshabat.fatora.viewmodel.InvoicesViewModelFactory

/** أسماء المسارات (Routes) - مكان واحد بس عشان نتفادى أخطاء كتابة الأسماء يدوياً. */
object FatoraRoutes {
    const val HOME = "home"
    const val ADD_DEBT = "add_debt"
    const val INVOICES = "invoices"
    const val SALES_REPORT = "sales_report"
    const val CUSTOMERS = "customers"
    const val CUSTOMER_DETAIL = "customer_detail/{customerId}"

    fun customerDetail(customerId: String) = "customer_detail/$customerId"

    // لاحقاً:
    // const val CLARIFICATION = "clarification"
}

/**
 * نقطة الدخول الوحيدة للتنقل بالتطبيق.
 *
 * HOME: قراءة فقط - بيعرض بيانات حقيقية من Room عبر HomeViewModel.
 * ADD_DEBT: أول شاشة كتابة فعلية - بتحفظ عملية حقيقية عبر AddDebtUseCase.
 *
 * ClarificationScreen وQueryResponseScreen ما انضافوا هون لسا لأنهم
 * بيحتاجوا طبقة الصوت/AI الجاية عشان يكون عندهم بيانات حقيقية يتغذوا منها.
 */
@Composable
fun FatoraNavGraph(
    appContainer: AppContainer,
    navController: NavHostController = rememberNavController()
) {
    NavHost(
        navController = navController,
        startDestination = FatoraRoutes.HOME
    ) {
        composable(FatoraRoutes.HOME) {
            val viewModel: HomeViewModel = viewModel(
                factory = HomeViewModelFactory(
                    customerRepository = appContainer.customerRepository,
                    transactionRepository = appContainer.transactionRepository
                )
            )

            val uiState by viewModel.uiState.collectAsState()

            // نعيد تحميل البيانات كل مرة الشاشة ترجع تظهر (مثلاً بعد حفظ دين جديد)،
            // لأنه الـ ViewModel نفسه بيضل عايش بالـ back stack وما بيعيد init{} لحاله.
            LaunchedEffect(Unit) {
                viewModel.loadHomeData()
            }

            HomeScreen(
                state = uiState,
                onVoiceClick = { /* TODO: الـ Feature الجاية - تسجيل صوتي حقيقي */ },
                onTextClick = {
                    navController.navigate(FatoraRoutes.ADD_DEBT)
                },
                onProfileClick = { /* TODO */ },
                onCustomersClick = {
                    navController.navigate(FatoraRoutes.CUSTOMERS)
                },
                onTransactionsClick = { navController.navigate(FatoraRoutes.INVOICES) },
                onSettingsClick = { /* TODO */ },
                onViewAllTransactionsClick = {                    // ← جديد
                    navController.navigate(FatoraRoutes.INVOICES)
                }
            )
        }

        composable(FatoraRoutes.CUSTOMERS) {
            val viewModel: CustomersViewModel = viewModel(
                factory = CustomersViewModelFactory(
                    customerRepository = appContainer.customerRepository,
                    transactionRepository = appContainer.transactionRepository
                )
            )

            val uiState by viewModel.uiState.collectAsState()

            LaunchedEffect(Unit) {
                viewModel.loadCustomers()
            }

            CustomersScreen(
                state = uiState,
                onCustomerClick = { customerId ->
                    navController.navigate(FatoraRoutes.customerDetail(customerId))
                },
                onBackClick = {
                    navController.popBackStack()
                }
            )
        }


        composable(
            route = FatoraRoutes.CUSTOMER_DETAIL,
            arguments = listOf(navArgument("customerId") { type = NavType.StringType })
        ) { backStackEntry ->
            val customerId = backStackEntry.arguments?.getString("customerId") ?: return@composable

            val viewModel: CustomerDetailViewModel = viewModel(
                factory = CustomerDetailViewModelFactory(
                    customerId = customerId,
                    customerRepository = appContainer.customerRepository,
                    transactionRepository = appContainer.transactionRepository
                )
            )

            val uiState by viewModel.uiState.collectAsState()
            val context = LocalContext.current

            uiState?.let { state ->
                QueryResponseScreen(
                    state = state,
                    onExportPdfClick = {
                        viewModel.exportToPdf(context) { uri ->
                            if (uri != null) {
                                Toast.makeText(
                                    context,
                                    "تم حفظ سجل العميل PDF بمجلد التنزيلات",
                                    Toast.LENGTH_LONG
                                ).show()
                            } else {
                                Toast.makeText(
                                    context,
                                    "صار خطأ أثناء إنشاء ملف PDF",
                                    Toast.LENGTH_LONG
                                ).show()
                            }
                        }
                    }
                )
            }
        }


        composable(FatoraRoutes.INVOICES) {
            val viewModel: InvoicesViewModel = viewModel(
                factory = InvoicesViewModelFactory(
                    customerRepository = appContainer.customerRepository,
                    transactionRepository = appContainer.transactionRepository
                )
            )

            val uiState by viewModel.uiState.collectAsState()

            // نعيد التحميل كل مرة الشاشة تنفتح، عشان أي عملية جديدة
            // انضافت من شاشة تانية تظهر فوراً هون كمان.
            LaunchedEffect(Unit) {
                viewModel.loadInvoices()
            }

            InvoicesScreen(
                state = uiState,
                onBackClick = {
                    navController.popBackStack()
                },
                onReportClick = {
                    navController.navigate(FatoraRoutes.SALES_REPORT)
                }
            )
        }

        composable(FatoraRoutes.SALES_REPORT) {
            val viewModel: SalesReportViewModel = viewModel(
                factory = SalesReportViewModelFactory(
                    customerRepository = appContainer.customerRepository,
                    transactionRepository = appContainer.transactionRepository
                )
            )

            val uiState by viewModel.uiState.collectAsState()

            SalesReportScreen(
                state = uiState,
                onFilterModeChange = viewModel::onFilterModeChange,
                onTypeFilterChange = viewModel::onTypeFilterChange,
                onPreviousClick = viewModel::onPreviousClick,
                onNextClick = viewModel::onNextClick,
                onBackClick = {
                    navController.popBackStack()
                }
            )
        }

        composable(FatoraRoutes.ADD_DEBT) {
            val viewModel: AddDebtViewModel = viewModel(
                factory = AddDebtViewModelFactory(
                    addDebtUseCase = appContainer.addDebtUseCase
                )
            )

            val uiState by viewModel.uiState.collectAsState()

            AddDebtScreen(
                state = uiState,
                onCustomerNameChange = viewModel::onCustomerNameChange,
                onProductChange = viewModel::onProductChange,
                onAmountChange = viewModel::onAmountChange,
                onSaveClick = viewModel::save,
                onSaveSuccess = {
                    // نرجع لـ Home ونشيل ADD_DEBT من الـ back stack
                    // عشان لو ضغط المستخدم "رجوع" من الـ Home ما يرجعله عالشاشة القديمة.
                    navController.popBackStack()
                },
                onBackClick = {
                    navController.popBackStack()
                }
            )
        }
    }
}