package com.example.quiziniapp

object Route {
    const val Home = "home"
    const val Quizzes = "quizzes"
    fun quizWithCategory(category: String): String = "quiz/$category"
    const val CongsFailed = "congsfailed"
    const val Rank = "rank"
}
