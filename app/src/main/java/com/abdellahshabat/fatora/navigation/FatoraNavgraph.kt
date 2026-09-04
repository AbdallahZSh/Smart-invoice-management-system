package com.abdellahshabat.fatora.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.abdellahshabat.fatora.AddDebtScreen
import com.abdellahshabat.fatora.HomeScreen
import com.abdellahshabat.fatora.InvoicesScreen
import com.abdellahshabat.fatora.di.AppContainer
import com.abdellahshabat.fatora.viewmodel.AddDebtViewModel
import com.abdellahshabat.fatora.viewmodel.AddDebtViewModelFactory
import com.abdellahshabat.fatora.viewmodel.HomeViewModel
import com.abdellahshabat.fatora.viewmodel.HomeViewModelFactory
import com.abdellahshabat.fatora.viewmodel.InvoicesViewModel
import com.abdellahshabat.fatora.viewmodel.InvoicesViewModelFactory

/** أسماء المسارات (Routes) - مكان واحد بس عشان نتفادى أخطاء كتابة الأسماء يدوياً. */
object FatoraRoutes {
    const val HOME = "home"
    const val ADD_DEBT = "add_debt"
    const val INVOICES = "invoices"

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
                onCustomersClick = { /* TODO */ },
                onTransactionsClick = { navController.navigate(FatoraRoutes.INVOICES) },
                onSettingsClick = { /* TODO */ }
            )
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