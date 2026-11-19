package com.example.quiziniapp.data

import android.content.Context
import com.example.quiziniapp.R
import org.json.JSONObject

data class Question(
    val question: String,
    val options: List<String>,
    val answerIndex: Int,
    val score: Int
)

fun loadQuestionsFromRaw(context: Context, category: String): List<Question> {
    return try {
        val inputStream = context.resources.openRawResource(R.raw.questions)
        val json = inputStream.bufferedReader().use { it.readText() }
        val jsonObject = JSONObject(json)
        if (!jsonObject.has(category)) return emptyList()

        val questionsArray = jsonObject.getJSONArray(category)
        val questions = mutableListOf<Question>()

        for (i in 0 until questionsArray.length()) {
            val obj = questionsArray.getJSONObject(i)
            val optionsArray = obj.getJSONArray("options")
            val options = List(optionsArray.length()) { j -> optionsArray.getString(j) }

            questions.add(
                Question(
                    question = obj.getString("question"),
                    options = options,
                    answerIndex = obj.getInt("answerIndex"),
                    score = obj.getInt("score")
                )
            )
        }
        questions
    } catch (e: Exception) {
        e.printStackTrace()
        emptyList()
    }
}
