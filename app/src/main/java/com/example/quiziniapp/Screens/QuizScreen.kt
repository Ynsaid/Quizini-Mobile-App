package com.example.quiziniapp.Screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.KeyboardArrowLeft
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.quiziniapp.Heplers.UserPreferences
import com.example.quiziniapp.R
import com.example.quiziniapp.Route
import com.example.quiziniapp.data.Question
import com.example.quiziniapp.data.loadQuestionsFromRaw
import com.example.quiziniapp.ui.theme.APP_BG
import com.example.quiziniapp.ui.theme.Card_Color
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.time.Duration.Companion.seconds

@Composable
fun QuizScreen(navController: NavController, category: String) {
    val context = LocalContext.current
    val questions = remember { mutableStateListOf<Question>() }
    var currentQuestionIndex by remember { mutableStateOf(0) }
    var selectedIndex by remember { mutableStateOf(-1) }
    var score by remember { mutableStateOf(0) }
    var correctAnswersCount by remember { mutableStateOf(0) }
    var answered by remember { mutableStateOf(false) }
    val coroutineScope = rememberCoroutineScope()

    LaunchedEffect(category) {
        questions.clear()
        questions.addAll(loadQuestionsFromRaw(context, category))
    }

    if (questions.isEmpty()) {
        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator()
        }
        return
    }

    val currentQuestion = questions[currentQuestionIndex]

    Column(modifier = Modifier.fillMaxSize()) {
        // Header with Back arrow
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 16.dp, top = 16.dp, end = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.KeyboardArrowLeft,
                contentDescription = "Back",
                modifier = Modifier
                    .size(32.dp)
                    .clickable { navController.popBackStack() }
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = category,
                fontSize = 20.sp,
                fontFamily = FontFamily(Font(R.font.b2_semibold))
            )
        }

        QuizCardWithFloatingProgress(
            totalQuestions = questions.size,
            answeredQuestions = currentQuestionIndex + 1,
            questionText = currentQuestion.question
        )

        Spacer(modifier = Modifier.height(16.dp))

        Column(modifier = Modifier.fillMaxWidth()) {
            currentQuestion.options.forEachIndexed { index, option ->
                val isCorrect = answered && index == currentQuestion.answerIndex
                val isWrong = answered && index == selectedIndex && index != currentQuestion.answerIndex

                solutionCard(
                    text = option,
                    selected = index == selectedIndex,
                    isCorrect = isCorrect,
                    isWrong = isWrong,
                    onClick = {
                        if (!answered) selectedIndex = index
                    }
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        Button(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
                .height(48.dp),
            shape = RoundedCornerShape(20.dp),
            enabled = selectedIndex != -1 || answered,
            onClick = {
                if (answered) return@Button

                answered = true
                if (selectedIndex == currentQuestion.answerIndex) {
                    score += currentQuestion.score
                    correctAnswersCount++
                }

                coroutineScope.launch {
                    delay(1000)
                    if (currentQuestionIndex < questions.lastIndex) {
                        currentQuestionIndex++
                        selectedIndex = -1
                        answered = false
                    } else {
                        UserPreferences.saveLastQuizScore(context, score)
                        UserPreferences.saveProgress(context, category, correctAnswersCount)
                        UserPreferences.saveCorrectAnswersCount(context, correctAnswersCount)

                        navController.currentBackStackEntry?.savedStateHandle?.set("score", score)
                        navController.currentBackStackEntry?.savedStateHandle?.set("totalQuestions", questions.size)

                        navController.navigate(Route.CongsFailed)
                    }
                }
            },
            colors = ButtonDefaults.buttonColors(
                containerColor = APP_BG,
                contentColor = Color.White
            )
        ) {
            Text(if (currentQuestionIndex == questions.lastIndex) "Finish" else "Next")
        }
    }
}

@Composable
fun QuizCardWithFloatingProgress(
    totalQuestions: Int,
    answeredQuestions: Int,
    questionText: String
) {
    var time by remember(answeredQuestions) { mutableStateOf(60) }
    val totalTime = 60
    val progress = time / totalTime.toFloat()

    LaunchedEffect(answeredQuestions) {
        time = 60
        while (time > 0) {
            delay(1.seconds)
            time--
        }
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        Card(
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = Card_Color),
            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
            modifier = Modifier
                .fillMaxWidth()
                .height(230.dp)
                .padding(top = 30.dp)
        ) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = questionText,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium,
                    textAlign = TextAlign.Center
                )
            }
        }

        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .size(64.dp)
                .align(Alignment.TopCenter)
                .offset(y = (-8).dp)
                .background(Color.White, shape = CircleShape)
        ) {
            CircularProgressIndicator(
                progress = { progress },
                strokeWidth = 6.dp,
                color = Color(0xFF164863),
                modifier = Modifier.fillMaxSize()
            )
            Text(
                text = time.toString(),
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
fun solutionCard(
    text: String,
    selected: Boolean = false,
    isCorrect: Boolean = false,
    isWrong: Boolean = false,
    onClick: () -> Unit = {}
) {
    val backgroundColor = when {
        isCorrect -> Color(0xFF86EFAC) // Green
        isWrong -> Color(0xFFFCA5A5)   // Red
        selected -> Color(0xFFABD1C6)
        else -> Card_Color
    }

    val textColor = when {
        isCorrect || isWrong -> Color.Black
        selected -> Color(0xFF164863)
        else -> Color.Black
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .background(backgroundColor, RoundedCornerShape(20.dp))
            .clip(RoundedCornerShape(20.dp))
            .clickable { onClick() }
            .padding(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = text,
                fontSize = 20.sp,
                fontFamily = FontFamily(Font(R.font.b2_semibold)),
                color = textColor
            )
            if (selected) {
                Icon(
                    imageVector = Icons.Default.CheckCircle,
                    contentDescription = "Selected",
                    tint = Color(0xFF164863)
                )
            } else {
                Image(
                    painter = painterResource(id = R.drawable.radio_button_unchecked_icon),
                    contentDescription = "Unselected"
                )
            }
        }
    }
}
