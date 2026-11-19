package com.example.quiziniapp.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.quiziniapp.heplers.UserPreferences
import com.example.quiziniapp.R
import com.example.quiziniapp.Route
import com.example.quiziniapp.ui.theme.APP_BG
import com.example.quiziniapp.ui.theme.Start_BTN_Color
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(navController: NavController) {
    val context = LocalContext.current
    var name by remember { mutableStateOf("") }
    val coroutineScope = rememberCoroutineScope()
    var isError by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        UserPreferences.getName(context).collect { savedName ->
            if (!savedName.isNullOrBlank()) {
                name = savedName
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(APP_BG.copy(alpha = 0.7f)),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Welcome to Quizini App",
            fontSize = 32.sp,
            fontFamily = FontFamily(Font(R.font.b2_bold)),
            modifier = Modifier.padding(top = 48.dp),
            color = Color.White
        )
        Spacer(modifier = Modifier.height(60.dp))
        Image(
            painter = painterResource(id = R.drawable.logo),
            contentDescription = "Logo",
            modifier = Modifier.size(160.dp)
        )
        Column(
            modifier = Modifier.padding(top = 36.dp)
        ) {
            Text(
                text = "Enter your name",
                fontSize = 18.sp,
                fontFamily = FontFamily(Font(R.font.b2_semibold)),
                color = Color.White,
                textAlign = TextAlign.Start,
                modifier = Modifier.padding(horizontal = 16.dp)
            )
            Spacer(modifier = Modifier.height(8.dp))
            OutlinedTextField(
                value = name,
                onValueChange = {
                    name = it
                    if (it.isNotBlank()) isError = false
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                shape = RoundedCornerShape(20.dp),
                textStyle = TextStyle(color = Color.White),
                isError = isError,
                colors = TextFieldDefaults.outlinedTextFieldColors(
                    focusedBorderColor = if (isError) Color.Red else Color.White,
                    unfocusedBorderColor = if (isError) Color.Red else Color.White,
                    cursorColor = Color.White,
                    errorBorderColor = Color.Red
                )
            )
            if (isError) {
                Text(
                    text = "Please enter your name",
                    color = Color.Red,
                    fontSize = 14.sp,
                    fontFamily = FontFamily(Font(R.font.b2_semibold)),
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp)
                )
            }
        }
        Spacer(modifier = Modifier.height(16.dp))
        Button(
            onClick = {
                if (name.isBlank()) {
                    isError = true
                } else {
                    coroutineScope.launch {
                        UserPreferences.saveName(context, name)
                        navController.navigate(Route.Quizzes)
                    }
                }
            },
            colors = ButtonDefaults.buttonColors(
                containerColor = Start_BTN_Color,
            ),
            shape = RoundedCornerShape(20.dp),
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 16.dp, end = 16.dp, top = 48.dp)
        ) {
            Text(
                text = "Start",
                fontSize = 24.sp,
                fontFamily = FontFamily(Font(R.font.b2_semibold)),
                color = Color.White
            )
        }
    }
}
