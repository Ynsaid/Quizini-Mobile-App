package com.example.quiziniapp.Modals

data class Question(
    val question: String,
    val options: List<String>,
    val answerIndex: Int,
    val score: Int
)
