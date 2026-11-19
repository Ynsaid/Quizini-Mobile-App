package com.example.quiziniapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.quiziniapp.Screens.CongsFailedScreen
import com.example.quiziniapp.Screens.HomeScreen
import com.example.quiziniapp.Screens.QuizScreen
import com.example.quiziniapp.Screens.QuizzesScreen
import com.example.quiziniapp.Screens.RankScreen
import com.example.quiziniapp.ui.theme.QuiziniAppTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            val navController = rememberNavController()
            QuiziniAppTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { paddingValues ->
                    NavHost(
                        navController = navController,
                        startDestination = Route.Home,
                        modifier = Modifier.padding(paddingValues)
                    ) {
                        composable(Route.Home) { HomeScreen(navController) }
                        composable(Route.Quizzes) { QuizzesScreen(navController) }
                        composable("quiz/{category}") { backStackEntry ->
                            val category = backStackEntry.arguments?.getString("category") ?: ""
                            QuizScreen(navController, category = category)
                        }
                        composable(Route.CongsFailed) { CongsFailedScreen(navController) }
                        composable(Route.Rank) { RankScreen(navController) }
                    }
                }
            }
        }
    }
}
