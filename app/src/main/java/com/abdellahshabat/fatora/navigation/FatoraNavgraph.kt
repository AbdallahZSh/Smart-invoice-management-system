package com.abdellahshabat.fatora.navigation

import android.net.Uri
import android.widget.Toast
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.abdellahshabat.fatora.AccountSecurityScreen
import com.abdellahshabat.fatora.AddDebtScreen
import com.abdellahshabat.fatora.AddPaymentScreen
import com.abdellahshabat.fatora.AppearanceScreen
import com.abdellahshabat.fatora.BackupScreen
import com.abdellahshabat.fatora.ClarificationScreen
import com.abdellahshabat.fatora.ComingSoonScreen
import com.abdellahshabat.fatora.DataInformationScreen
import com.abdellahshabat.fatora.HelpFeedbackScreen
import com.abdellahshabat.fatora.HomeScreen
import com.abdellahshabat.fatora.InvoicesScreen
import com.abdellahshabat.fatora.ListsScreen
import com.abdellahshabat.fatora.PrivacyPolicyScreen
import com.abdellahshabat.fatora.PrivacyScreen
import com.abdellahshabat.fatora.QueryResponseScreen
import com.abdellahshabat.fatora.SettingsScreen
import com.abdellahshabat.fatora.StorageDataScreen
import com.abdellahshabat.fatora.customer.CustomersScreen
import com.abdellahshabat.fatora.di.AppContainer
import com.abdellahshabat.fatora.screen2.SalesReportScreen
import com.abdellahshabat.fatora.screen2.SalesReportViewModel
import com.abdellahshabat.fatora.screen2.SalesReportViewModelFactory
import com.abdellahshabat.fatora.util.ShopPreferences
import com.abdellahshabat.fatora.util.ThemeMode
import com.abdellahshabat.fatora.viewmodel.AddDebtViewModel
import com.abdellahshabat.fatora.viewmodel.AddDebtViewModelFactory
import com.abdellahshabat.fatora.viewmodel.AddPaymentViewModel
import com.abdellahshabat.fatora.viewmodel.AddPaymentViewModelFactory
import com.abdellahshabat.fatora.viewmodel.BackupViewModel
import com.abdellahshabat.fatora.viewmodel.BackupViewModelFactory
import com.abdellahshabat.fatora.viewmodel.CustomerDetailViewModel
import com.abdellahshabat.fatora.viewmodel.CustomerDetailViewModelFactory
import com.abdellahshabat.fatora.viewmodel.CustomersViewModel
import com.abdellahshabat.fatora.viewmodel.CustomersViewModelFactory
import com.abdellahshabat.fatora.viewmodel.HomeViewModel
import com.abdellahshabat.fatora.viewmodel.HomeViewModelFactory
import com.abdellahshabat.fatora.viewmodel.InvoicesViewModel
import com.abdellahshabat.fatora.viewmodel.InvoicesViewModelFactory
import com.abdellahshabat.fatora.viewmodel.SettingsViewModel
import com.abdellahshabat.fatora.viewmodel.SettingsViewModelFactory
import com.abdellahshabat.fatora.viewmodel.StorageDataViewModel
import com.abdellahshabat.fatora.viewmodel.StorageDataViewModelFactory

/** أسماء المسارات (Routes) - مكان واحد بس عشان نتفادى أخطاء كتابة الأسماء يدوياً. */
object FatoraRoutes {
    const val HOME = "home"
    const val ADD_DEBT = "add_debt"
    const val ADD_PAYMENT = "add_payment"
    const val INVOICES = "invoices"
    const val SALES_REPORT = "sales_report"
    const val CUSTOMERS = "customers"
    const val CUSTOMER_DETAIL = "customer_detail/{customerId}"
    const val SETTINGS = "settings"
    const val LISTS = "lists"
    const val BACKUP = "backup"
    const val STORAGE_DATA = "storage_data"
    const val APPEARANCE = "appearance"
    const val HELP = "help"
    const val ACCOUNT_STUB = "account_stub"
    const val PRIVACY_STUB = "privacy_stub"
    const val DATA_INFORMATION = "data_information"
    const val ACCOUNT_SECURITY = "account_security"
    const val PRIVACY_POLICY = "privacy_policy"
    const val NOTIFICATIONS_STUB = "notifications_stub"
    const val LANGUAGE_STUB = "language_stub"

    fun customerDetail(customerId: String) = "customer_detail/$customerId"
}

/**
 * نقطة الدخول الوحيدة للتنقل بالتطبيق.
 *
 * themeMode / onThemeModeChange: ممرّرين من MainActivity عشان شاشة "الشكل"
 * تقدر تغيّر الـ Theme فوراً وتحفظ الاختيار.
 */
@Composable
fun FatoraNavGraph(
    appContainer: AppContainer,
    themeMode: ThemeMode,
    onThemeModeChange: (ThemeMode) -> Unit,
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
                onTransactionsClick = {
                    navController.navigate(FatoraRoutes.INVOICES)
                },
                onSettingsClick = {
                    navController.navigate(FatoraRoutes.SETTINGS)
                },
                onViewAllTransactionsClick = {
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
                onDeleteCustomer = { customerId ->
                    viewModel.deleteCustomer(customerId)
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
                },
                onDeleteInvoice = { transactionId ->
                    viewModel.deleteInvoice(transactionId)
                },
                onEditInvoice = { transactionId, product, amount ->
                    viewModel.updateInvoice(transactionId, product, amount)
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

            val clarification = uiState.clarification
            if (clarification != null) {
                ClarificationScreen(
                    state = clarification,
                    onMissingFieldChange = { /* ما في حقل ناقص بهاي الحالة */ },
                    onCustomerSelected = viewModel::onClarificationCustomerSelected,
                    onContinue = viewModel::onClarificationContinue
                )
            } else {
                AddDebtScreen(
                    state = uiState,
                    onCustomerNameChange = viewModel::onCustomerNameChange,
                    onProductChange = viewModel::onProductChange,
                    onAmountChange = viewModel::onAmountChange,
                    onSaveClick = viewModel::save,
                    onSaveSuccess = {
                        navController.popBackStack()
                    },
                    onBackClick = {
                        navController.popBackStack()
                    },
                    onSwitchToPaymentClick = {
                        navController.navigate(FatoraRoutes.ADD_PAYMENT) {
                            popUpTo(FatoraRoutes.ADD_DEBT) { inclusive = true }
                        }
                    }
                )
            }
        }

        composable(FatoraRoutes.ADD_PAYMENT) {
            val viewModel: AddPaymentViewModel = viewModel(
                factory = AddPaymentViewModelFactory(
                    addPaymentUseCase = appContainer.addPaymentUseCase
                )
            )

            val uiState by viewModel.uiState.collectAsState()

            val clarification = uiState.clarification
            if (clarification != null) {
                ClarificationScreen(
                    state = clarification,
                    onMissingFieldChange = { /* ما في حقل ناقص بهاي الحالة */ },
                    onCustomerSelected = viewModel::onClarificationCustomerSelected,
                    onContinue = viewModel::onClarificationContinue
                )
            } else {
                AddPaymentScreen(
                    state = uiState,
                    onCustomerNameChange = viewModel::onCustomerNameChange,
                    onAmountChange = viewModel::onAmountChange,
                    onSaveClick = viewModel::save,
                    onSaveSuccess = {
                        navController.popBackStack()
                    },
                    onBackClick = {
                        navController.popBackStack()
                    },
                    onSwitchToDebtClick = {
                        navController.navigate(FatoraRoutes.ADD_DEBT) {
                            popUpTo(FatoraRoutes.ADD_PAYMENT) { inclusive = true }
                        }
                    }
                )
            }
        }

        composable(FatoraRoutes.SETTINGS) {
            val context = LocalContext.current
            val shopPreferences = remember { ShopPreferences(context) }

            val viewModel: SettingsViewModel = viewModel(
                factory = SettingsViewModelFactory(
                    shopPreferences = shopPreferences,
                    customerRepository = appContainer.customerRepository,
                    transactionRepository = appContainer.transactionRepository
                )
            )

            val uiState by viewModel.uiState.collectAsState()

            LaunchedEffect(Unit) {
                viewModel.load()
            }

            SettingsScreen(
                state = uiState,
                onShopNameChange = viewModel::onShopNameChange,
                onOwnerPhoneChange = viewModel::onOwnerPhoneChange,
                onSaveClick = viewModel::saveProfile,
                onOpenLists = { navController.navigate(FatoraRoutes.LISTS) },
                onOpenBackup = { navController.navigate(FatoraRoutes.BACKUP) },
                onOpenStorage = { navController.navigate(FatoraRoutes.STORAGE_DATA) },
                onOpenAppearance = { navController.navigate(FatoraRoutes.APPEARANCE) },
                onOpenHelp = { navController.navigate(FatoraRoutes.HELP) },
                onOpenAccount = { navController.navigate(FatoraRoutes.ACCOUNT_STUB) },
                onOpenPrivacy = { navController.navigate(FatoraRoutes.PRIVACY_STUB) },
                onOpenNotifications = { navController.navigate(FatoraRoutes.NOTIFICATIONS_STUB) },
                onOpenLanguage = { navController.navigate(FatoraRoutes.LANGUAGE_STUB) },
                onBackClick = {
                    navController.popBackStack()
                }
            )
        }

        composable(FatoraRoutes.LISTS) {
            ListsScreen(
                onOpenCustomersClick = { navController.navigate(FatoraRoutes.CUSTOMERS) },
                onOpenInvoicesClick = { navController.navigate(FatoraRoutes.INVOICES) },
                onBackClick = { navController.popBackStack() }
            )
        }

        composable(FatoraRoutes.BACKUP) {
            val context = LocalContext.current

            val viewModel: BackupViewModel = viewModel(
                factory = BackupViewModelFactory(
                    customerRepository = appContainer.customerRepository,
                    transactionRepository = appContainer.transactionRepository
                )
            )

            val uiState by viewModel.uiState.collectAsState()

            LaunchedEffect(Unit) {
                viewModel.loadCounts()
            }

            BackupScreen(
                state = uiState,
                onExportClick = { viewModel.exportBackup(context) },
                onImportFileSelected = { uri: Uri -> viewModel.importBackup(context, uri) },
                onOpenInvoicesClick = { navController.navigate(FatoraRoutes.INVOICES) },
                onBackClick = { navController.popBackStack() }
            )
        }

        composable(FatoraRoutes.STORAGE_DATA) {
            val viewModel: StorageDataViewModel = viewModel(
                factory = StorageDataViewModelFactory(
                    customerRepository = appContainer.customerRepository,
                    transactionRepository = appContainer.transactionRepository
                )
            )

            val uiState by viewModel.uiState.collectAsState()

            LaunchedEffect(Unit) {
                viewModel.load()
            }

            StorageDataScreen(
                state = uiState,
                onOpenBackupClick = { navController.navigate(FatoraRoutes.BACKUP) },
                onResetAllData = { viewModel.resetAllData() },
                onBackClick = { navController.popBackStack() }
            )
        }

        composable(FatoraRoutes.APPEARANCE) {
            AppearanceScreen(
                currentThemeMode = themeMode,
                onThemeModeChange = onThemeModeChange,
                onBackClick = { navController.popBackStack() }
            )
        }

        composable(FatoraRoutes.HELP) {
            HelpFeedbackScreen(
                onBackClick = { navController.popBackStack() }
            )
        }

        composable(FatoraRoutes.ACCOUNT_STUB) {
            ComingSoonScreen(
                title = "الحساب",
                explanation = "إشعارات الأمان بتحتاج نظام حسابات/تسجيل دخول، وهاد مش موجود بالتطبيق لسا.",
                onBackClick = { navController.popBackStack() }
            )
        }

        composable(FatoraRoutes.PRIVACY_STUB) {
            PrivacyScreen(
                onBackClick = {
                    navController.popBackStack()
                },

                onChangePassword = {
                    // سنضيف شاشة تغيير كلمة المرور في الخطوة القادمة
                },

                onAccountSecurity = {
                    navController.navigate(FatoraRoutes.ACCOUNT_SECURITY)
                },

                onPrivacyPolicy = {
                    navController.navigate(FatoraRoutes.PRIVACY_POLICY)
                },

                onDeleteAccount = {
                    // سنضيف حذف الحساب لاحقاً
                },

                onDataInformation = {
                    navController.navigate(FatoraRoutes.DATA_INFORMATION)
                }
            )
        }
        composable(FatoraRoutes.DATA_INFORMATION) {
            DataInformationScreen(
                onBackClick = {
                    navController.popBackStack()
                }
            )
        }

        composable(FatoraRoutes.ACCOUNT_SECURITY) {
            AccountSecurityScreen(
                onBackClick = {
                    navController.popBackStack()
                },
                onChangePasswordClick = {
                    // سنفعله بعد اكتمال Authentication
                }
            )
        }

        composable(FatoraRoutes.PRIVACY_POLICY) {
            PrivacyPolicyScreen(
                onBackClick = {
                    navController.popBackStack()
                }
            )
        }

        composable(FatoraRoutes.NOTIFICATIONS_STUB) {
            ComingSoonScreen(
                title = "الإشعارات",
                explanation = "تذكيرات الديون بتحتاج صلاحيات إشعارات وجدولة - شغل منفصل جاي.",
                onBackClick = { navController.popBackStack() }
            )
        }

        composable(FatoraRoutes.LANGUAGE_STUB) {
            ComingSoonScreen(
                title = "لغة التطبيق",
                explanation = "كل نصوص التطبيق مكتوبة عربي مباشر بالكود - تغيير اللغة يحتاج نقلها لملفات strings.xml أول.",
                onBackClick = { navController.popBackStack() }
            )
        }
    }
}