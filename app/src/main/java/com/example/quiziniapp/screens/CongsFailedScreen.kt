package com.example.quiziniapp.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.quiziniapp.heplers.UserPreferences
import com.example.quiziniapp.R
import com.example.quiziniapp.Route

@Composable
fun CongsFailedScreen(navController: NavController) {
    val context = LocalContext.current
    val savedScore = navController.previousBackStackEntry
        ?.savedStateHandle
        ?.get<Int>("score")
    val lastQuizScore by UserPreferences.getLastQuizScore(context).collectAsState(initial = 0)
    val score = savedScore ?: lastQuizScore

    val nameFlow = remember { UserPreferences.getName(context) }
    val playerNameState by nameFlow.collectAsState(initial = "")
    val playerName = if (playerNameState.isNullOrBlank()) "Player" else playerNameState

    val isFailed = score == 0
    val primaryColor = if (isFailed) Color(0xFF9B2226) else Color(0xFF004643)
    val outerCircleColor = if (isFailed) Color(0xFFFCA5A5) else Color(0xFFABD1C6)
    val titleText = if (isFailed) "Failed" else "Congratulations!"
    val subText = if (isFailed) "Better luck next time, $playerName!" else "Great job, $playerName! You did it."

    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "You got $score point${if (score != 1) "s" else ""}",
                fontFamily = FontFamily(Font(R.font.b2_medium)),
                fontSize = 28.sp
            )

            Spacer(modifier = Modifier.height(32.dp))

            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .size(160.dp)
                    .background(color = outerCircleColor, shape = CircleShape)
            ) {
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .size(120.dp)
                        .background(color = primaryColor, shape = CircleShape)
                ) {
                    Text(
                        text = "Your Score\n$score",
                        color = Color.White,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        lineHeight = 24.sp,
                        textAlign = TextAlign.Center
                    )
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            Text(
                text = titleText,
                fontSize = 28.sp,
                fontFamily = FontFamily(Font(R.font.b2_medium)),
                color = primaryColor
            )

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = subText,
                fontSize = 18.sp,
                color = Color.Gray,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(48.dp))

            Box(
                modifier = Modifier
                    .background(color = primaryColor, shape = RoundedCornerShape(20.dp))
                    .clickable {
                        navController.navigate(Route.Quizzes) {
                            popUpTo(Route.Home) { inclusive = false }
                        }
                    }
                    .padding(vertical = 12.dp, horizontal = 32.dp)
            ) {
                Text(
                    text = "Back to Home",
                    color = Color.White,
                    fontSize = 18.sp
                )
            }
        }
    }
}
