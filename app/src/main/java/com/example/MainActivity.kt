package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.ui.*
import com.example.ui.theme.LabourBookTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            LabourBookTheme {
                val viewModel: LabourViewModel = viewModel()
                var isLoggedIn by remember { mutableStateOf(false) }

                if (!isLoggedIn) {
                    LoginScreen(viewModel = viewModel, onLoginSuccess = { isLoggedIn = true })
                } else {
                    AppNavigation(viewModel)
                }
            }
        }
    }
}

@Composable
fun AppNavigation(viewModel: LabourViewModel) {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = "dashboard") {
        composable("dashboard") {
            DashboardScreen(viewModel, navController)
        }
        composable("worker_list") {
            WorkerListScreen(viewModel, navController)
        }
        composable("add_worker") {
            AddWorkerScreen(viewModel, navController)
        }
        composable("attendance") {
            AttendanceScreen(viewModel, navController)
        }
        composable("reports") {
            ReportsScreen(viewModel, navController)
        }
        composable(
            "worker_details/{workerId}",
            arguments = listOf(navArgument("workerId") { type = NavType.LongType })
        ) { backStackEntry ->
            val workerId = backStackEntry.arguments?.getLong("workerId") ?: 0L
            WorkerDetailsScreen(workerId, viewModel, navController)
        }
    }
}
