package com.example.quiziniapp.Screens

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.quiziniapp.Heplers.UserPreferences
import com.example.quiziniapp.R
import com.example.quiziniapp.Route
import com.example.quiziniapp.ui.theme.APP_BG
import kotlinx.coroutines.launch

data class Category(
    val name: String,
    val icon: ImageVector,
    val totalQuestions: Int = 2
)

@Composable
fun QuizzesScreen(navController: NavController) {
    val context = LocalContext.current

    val categoryList = remember {
        listOf(
            Category("Knowledge", Icons.Default.Face, totalQuestions = 2),
            Category("Sports", Icons.Default.Star, totalQuestions = 2),
            Category("Education", Icons.Default.Star, totalQuestions = 2),
            Category("For Kids", Icons.Default.Star, totalQuestions = 2),
            Category("Tech & Games", Icons.Default.Star, totalQuestions = 2),
            Category("Culture & Society", Icons.Default.Star, totalQuestions = 2)
        )
    }

    val solvedMap = remember { mutableStateMapOf<String, Int>() }

    LaunchedEffect(Unit) {
        categoryList.forEach { category ->
            launch {
                UserPreferences.getProgress(context, category.name).collect { solved ->
                    solvedMap[category.name] = solved
                }
            }
        }
    }

    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        Text(
            text = "My Rank",
            fontSize = 24.sp,
            fontFamily = FontFamily(Font(R.font.b2_semibold)),
            color = APP_BG,
            modifier = Modifier
                .padding(top = 16.dp, start = 16.dp)
                .clickable { navController.navigate(Route.Rank) }
        )

        Spacer(modifier = Modifier.height(32.dp))

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
                .height(200.dp)
        ) {
            Image(
                painter = painterResource(id = R.drawable.image_crown),
                contentDescription = "Logo",
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .fillMaxSize()
                    .clip(RoundedCornerShape(20.dp))
            )
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .clip(RoundedCornerShape(20.dp))
                    .background(APP_BG.copy(alpha = 0.66f)),
                contentAlignment = Alignment.TopStart
            ) {
                Column(
                    modifier = Modifier.padding(20.dp),
                    horizontalAlignment = Alignment.Start,
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = "Test Your Knowledge with\nQuizzes",
                        color = Color.White,
                        fontSize = 16.sp,
                        fontFamily = FontFamily(Font(R.font.b2_semibold))
                    )
                    Text(
                        text = "If you're looking for a playful way to learn\nnew facts, our quizzes are designed to\nentertain and educate.",
                        color = Color.White,
                        fontSize = 10.sp,
                        lineHeight = 14.sp
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Button(
                        onClick = {
                            if (categoryList.isNotEmpty()) {
                                navController.navigate(Route.quizWithCategory(categoryList.first().name))
                            }
                        },
                        shape = RoundedCornerShape(8.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color.White,
                            contentColor = APP_BG
                        )
                    ) {
                        Text("Play")
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(32.dp))

        Text(
            text = "Categories",
            fontSize = 24.sp,
            fontFamily = FontFamily(Font(R.font.b2_semibold)),
            color = APP_BG,
            modifier = Modifier.padding(start = 16.dp)
        )

        Spacer(modifier = Modifier.height(16.dp))

        LazyColumn {
            items(categoryList) { category ->
                val solved = solvedMap[category.name] ?: 0
                CategoryItem(
                    icon = category.icon,
                    numOfQuestions = category.totalQuestions,
                    nameCategory = category.name,
                    solvedQuestionsNumber = solved,
                    onClick = {
                        navController.navigate(Route.quizWithCategory(category.name))
                    }
                )
            }
        }
    }
}

@Composable
fun CategoryItem(
    icon: ImageVector,
    numOfQuestions: Int,
    nameCategory: String,
    solvedQuestionsNumber: Int,
    onClick: () -> Unit = {}
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .height(70.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(Color(0xFFEEEEEE))
            .clickable { onClick() }
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = Color.Black,
                    modifier = Modifier.size(44.dp)
                )
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Text(
                        text = nameCategory,
                        style = MaterialTheme.typography.titleMedium
                    )
                    Text(
                        text = "$numOfQuestions Questions",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.Gray
                    )
                }
            }

            SolvedProgressCircle(
                solved = solvedQuestionsNumber,
                total = numOfQuestions
            )
        }
    }
}

@Composable
fun SolvedProgressCircle(
    solved: Int,
    total: Int,
    size: Dp = 60.dp,
    strokeWidth: Dp = 6.dp,
    color: Color = Color(0xFF4CAF50)
) {
    val progress = remember(solved, total) {
        if (total == 0) 0f else (solved.toFloat() / total).coerceIn(0f, 1f)
    }

    val animatedProgress by animateFloatAsState(
        targetValue = progress,
        animationSpec = tween(durationMillis = 700),
        label = "progressAnimation"
    )

    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier.size(size)
    ) {
        CircularProgressIndicator(
            progress = { 1f },
            color = Color.LightGray,
            strokeWidth = strokeWidth,
            modifier = Modifier.fillMaxSize()
        )

        CircularProgressIndicator(
            progress = { animatedProgress },
            modifier = Modifier.fillMaxSize(),
            color = color,
            strokeWidth = strokeWidth,
        )

        Text(
            text = "$solved/$total",
            color = Color.Black,
            fontFamily = FontFamily(Font(R.font.b2_medium)),
            fontSize = 10.sp
        )
    }
}
