package com.example.quiziniapp.modals

data class Question(
    val question: String,
    val options: List<String>,
    val answerIndex: Int,
    val score: Int
)
