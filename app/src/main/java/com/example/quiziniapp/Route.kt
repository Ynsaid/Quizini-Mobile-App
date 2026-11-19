package com.example.quiziniapp

object Route {
    const val Home = "home"
    const val Quizzes = "quizzes"
    const val Quiz = "quiz"
    fun quizWithCategory(category: String): String = "quiz/$category"
    const val CongsFailed = "congsfailed"
    const val Rank = "rank"
}
