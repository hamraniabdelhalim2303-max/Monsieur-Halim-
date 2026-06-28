package com.example.data

import android.util.Log
import com.example.BuildConfig
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject
import java.util.concurrent.TimeUnit

class GeminiService {
    private val client = OkHttpClient.Builder()
        .connectTimeout(30, TimeUnit.SECONDS)
        .readTimeout(30, TimeUnit.SECONDS)
        .build()

    private val mediaType = "application/json; charset=utf-8".toMediaType()

    suspend fun getTutorResponse(userMessage: String, chatHistory: List<ChatMessage>): String = withContext(Dispatchers.IO) {
        val apiKey = BuildConfig.GEMINI_API_KEY
        if (apiKey.isEmpty() || apiKey == "MY_GEMINI_API_KEY" || apiKey.contains("PLACEHOLDER")) {
            return@withContext getLocalFallbackResponse(userMessage)
        }

        try {
            val systemInstruction = """
                Tu es Monsieur Halim, un enseignant de français d'Algérie, passionné, bienveillant, et extrêmement professionnel. Tu aides les élèves du primaire, du moyen, les parents et les apprenants adultes à maîtriser le français avec excellence, créativité et motivation.
                Réponds toujours en français, avec parfois des explications ou traductions en arabe si nécessaire (RTL-friendly) pour éclaircir un concept difficile.
                Sois toujours très encourageant, utilise des termes motivants ("Excellent !", "Bravo !", "Magnifique !"), et adapte tes réponses pour qu'elles soient simples et pédagogiques.
                Si un élève te demande de corriger un texte, explique ses erreurs de grammaire, d'orthographe ou de conjugaison de manière claire.
                Génère des exercices courts, des petits quiz interactifs, des conseils de vocabulaire ou aide à la prononciation en épelant phonétiquement.
            """.trimIndent()

            // Build the contents array
            val contentsArray = org.json.JSONArray()

            // Optional: Include recent history for context
            chatHistory.takeLast(10).forEach { msg ->
                val role = if (msg.sender == "USER") "user" else "model"
                val contentObj = JSONObject().apply {
                    put("role", role)
                    val partsArray = org.json.JSONArray().apply {
                        put(JSONObject().put("text", msg.message))
                    }
                    put("parts", partsArray)
                }
                contentsArray.put(contentObj)
            }

            // Append the new message
            val newUserContentObj = JSONObject().apply {
                put("role", "user")
                val partsArray = org.json.JSONArray().apply {
                    put(JSONObject().put("text", userMessage))
                }
                put("parts", partsArray)
            }
            contentsArray.put(newUserContentObj)

            // Construct full JSON body
            val requestBodyJson = JSONObject().apply {
                put("contents", contentsArray)
                val systemObj = JSONObject().apply {
                    val partsArray = org.json.JSONArray().apply {
                        put(JSONObject().put("text", systemInstruction))
                    }
                    put("parts", partsArray)
                }
                put("systemInstruction", systemObj)
            }

            val requestBodyStr = requestBodyJson.toString()
            Log.d("GeminiService", "Sending request: $requestBodyStr")

            val request = Request.Builder()
                .url("https://generativelanguage.googleapis.com/v1beta/models/gemini-3.5-flash:generateContent?key=$apiKey")
                .post(requestBodyStr.toRequestBody(mediaType))
                .build()

            client.newCall(request).execute().use { response ->
                if (!response.isSuccessful) {
                    val errBody = response.body?.string() ?: ""
                    Log.e("GeminiService", "API error: ${response.code} $errBody")
                    return@withContext "Désolé, j'ai rencontré un problème de connexion (${response.code}). Essayons encore une fois ! (عذراً، واجهت مشكلة في الاتصال)"
                }

                val responseBodyStr = response.body?.string() ?: ""
                Log.d("GeminiService", "Response received: $responseBodyStr")

                val responseJson = JSONObject(responseBodyStr)
                val candidates = responseJson.getJSONArray("candidates")
                if (candidates.length() > 0) {
                    val firstCandidate = candidates.getJSONObject(0)
                    val contentObj = firstCandidate.getJSONObject("content")
                    val parts = contentObj.getJSONArray("parts")
                    if (parts.length() > 0) {
                        return@withContext parts.getJSONObject(0).getString("text")
                    }
                }
                return@withContext "Je n'ai pas pu générer de réponse. Peux-tu reformuler ta question ? (لم أتمكن من توليد إجابة. هل يمكنك إعادة صياغة سؤالك؟)"
            }
        } catch (e: Exception) {
            Log.e("GeminiService", "Error during API call", e)
            return@withContext "Oups ! Une erreur s'est produite. Vérifie ta connexion internet. (${e.message})"
        }
    }

    private fun getLocalFallbackResponse(userMessage: String): String {
        val lower = userMessage.lowercase()
        return when {
            lower.contains("bonjour") || lower.contains("salut") || lower.contains("أهلا") || lower.contains("مرحبا") -> {
                "Bonjour mon cher élève ! Je suis ravi de te retrouver sur ma plateforme éducative. Comment puis-je t'aider aujourd'hui ? 😊\n\nأهلاً بك يا بني! أنا سعيد جداً بلقائك في منصتي التعليمية. كيف يمكنني مساعدتك اليوم؟"
            }
            lower.contains("conjugaison") || lower.contains("passé composé") || lower.contains("تصريف") -> {
                "Excellent sujet ! Le **Passé Composé** se forme avec l'auxiliaire *Être* ou *Avoir* au présent + le *Participe Passé* du verbe.\n\nExemple : \n- *J'ai mangé* une pomme. (لقد أكلت تفاحة)\n- *Je suis parti* à l'école. (لقد ذهبت للمدرسة)\n\nVeux-tu faire un petit exercice pratique ? 📝"
            }
            lower.contains("correction") || lower.contains("corrige") || lower.contains("تصحيح") -> {
                "Absolument ! Écris-moi ta phrase ou ton paragraphe, et je vais corriger tes fautes avec plaisir et t'expliquer les règles. ✍️\n\nأكيد! اكتب لي جملتك وسأقوم بتصحيحها بكل سرور وشرح القواعد لك."
            }
            lower.contains("quiz") || lower.contains("exercice") || lower.contains("تمرين") -> {
                "Voici un mini-quiz très sympa pour toi !\n\nTrouve la bonne forme du verbe *Avoir* :\n*Tu ___ un beau cahier d'exercices.*\n\nA) as\nB) es\nC) a\n\nRéponds-moi par A, B ou C ! 🌟"
            }
            else -> {
                "Merveilleuse question ! En tant que ton tuteur de français, je te conseille de pratiquer régulièrement. Dis-moi en plus sur ce que tu souhaites apprendre (grammaire, lecture, vocabulaire, préparation aux examens) ! 🚀\n\nسؤال رائع! بصفتي معلمك للغة الفرنسية، أنصحك بالممارسة المستمرة. أخبرني بالمزيد عما تريد تعلمه!"
            }
        }
    }
}
