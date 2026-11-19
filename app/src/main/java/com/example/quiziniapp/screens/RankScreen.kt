package com.example.quiziniapp.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowLeft
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.quiziniapp.heplers.UserPreferences
import com.example.quiziniapp.R
import kotlinx.coroutines.launch

@Composable
fun RankScreen(
    navController: NavController,
    onResetConfirmed: () -> Unit = {}
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()

    var showDialog by remember { mutableStateOf(false) }
    var totalScore by remember { mutableIntStateOf(0) }
    var categoryScores by remember { mutableStateOf(mapOf<String, Int>()) }
    var userName by remember { mutableStateOf("Player") }

    val categories = UserPreferences.ALL_CATEGORIES
    val totalPossibleScore = categories.size * 2 // 6 categories * 2 questions = 12 total possible points

    LaunchedEffect(Unit) {
        launch {
            UserPreferences.getScore(context).collect { score ->
                totalScore = score
            }
        }

        launch {
            UserPreferences.getName(context).collect { name ->
                userName = if (!name.isNullOrBlank()) name else "Player"
            }
        }

        categories.forEach { cat ->
            launch {
                UserPreferences.getProgress(context, cat).collect { progress ->
                    categoryScores = categoryScores + (cat to progress)
                }
            }
        }
    }

    val progressFraction = if (totalPossibleScore > 0) {
        (totalScore.toFloat() / totalPossibleScore).coerceIn(0f, 1f)
    } else 0f

    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Back Button
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp, bottom = 16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.KeyboardArrowLeft,
                    contentDescription = "Back",
                    modifier = Modifier
                        .clickable { navController.popBackStack() }
                        .size(32.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Previous",
                    fontSize = 16.sp,
                    fontFamily = FontFamily(Font(R.font.b2_semibold)),
                    modifier = Modifier.clickable {
                        navController.popBackStack()
                    }
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            CircularProgressIndicator(
                progress = { progressFraction },
                color = Color(0xFF004643),
                strokeWidth = 8.dp,
                modifier = Modifier.size(120.dp)
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "Total Score: $totalScore",
                fontSize = 18.sp,
                fontFamily = FontFamily(Font(R.font.b2_medium))
            )

            Text(
                text = "Well done, $userName!",
                fontSize = 16.sp,
                fontFamily = FontFamily(Font(R.font.b2_medium))
            )

            Spacer(modifier = Modifier.height(32.dp))

            categories.forEach { category ->
                val score = categoryScores[category] ?: 0
                Box(
                    modifier = Modifier
                        .fillMaxWidth(0.9f)
                        .height(50.dp)
                        .background(Color(0xFFABD1C6), RoundedCornerShape(12.dp))
                        .padding(horizontal = 16.dp),
                    contentAlignment = Alignment.CenterStart
                ) {
                    Text(
                        text = "You got $score points from $category quiz",
                        fontSize = 14.sp,
                        fontFamily = FontFamily(Font(R.font.b2_medium))
                    )
                }
                Spacer(modifier = Modifier.height(12.dp))
            }

            Spacer(modifier = Modifier.height(32.dp))

            Box(
                modifier = Modifier
                    .fillMaxWidth(0.9f)
                    .height(50.dp)
                    .background(Color(0xFF9B2226), RoundedCornerShape(20.dp))
                    .clickable { showDialog = true },
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "Reset Score",
                    fontSize = 16.sp,
                    color = Color.White,
                    fontFamily = FontFamily(Font(R.font.b2_semibold))
                )
            }

            Spacer(modifier = Modifier.height(32.dp))
        }
    }

    if (showDialog) {
        AlertDialog(
            onDismissRequest = { showDialog = false },
            title = { Text("Reset Scores") },
            text = { Text("Are you sure you want to reset all your scores?") },
            confirmButton = {
                TextButton(onClick = {
                    coroutineScope.launch {
                        UserPreferences.resetAll(context)
                        totalScore = 0
                        categoryScores = UserPreferences.ALL_CATEGORIES.associateWith { 0 }
                        showDialog = false
                        onResetConfirmed()
                    }
                }) {
                    Text("Yes")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }
}
