package com.example.data

import android.content.Context
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull

class Repository(private val db: AppDatabase) {
    private val userProgressDao = db.userProgressDao()
    private val chatMessageDao = db.chatMessageDao()
    private val courseProgressDao = db.courseProgressDao()
    private val socialPostDao = db.socialPostDao()
    private val geminiService = GeminiService()

    // User Progress
    val userProgressFlow: Flow<UserProgress?> = userProgressDao.getUserProgressFlow()

    suspend fun getOrCreateUserProgress(): UserProgress {
        var progress = userProgressDao.getUserProgress()
        if (progress == null) {
            progress = UserProgress()
            userProgressDao.insertUserProgress(progress)
        }
        return progress
    }

    suspend fun saveUserProgress(progress: UserProgress) {
        userProgressDao.insertUserProgress(progress)
    }

    suspend fun updateXpAndStreak(xpAdded: Int, newStreak: Int? = null) {
        val current = getOrCreateUserProgress()
        val updated = current.copy(
            xpPoints = current.xpPoints + xpAdded,
            streakDays = newStreak ?: current.streakDays,
            level = (current.xpPoints + xpAdded) / 500 + 1
        )
        userProgressDao.insertUserProgress(updated)
    }

    suspend fun setLanguage(lang: String) {
        val current = getOrCreateUserProgress()
        userProgressDao.insertUserProgress(current.copy(currentLanguage = lang))
    }

    suspend fun setDarkMode(enabled: Boolean) {
        val current = getOrCreateUserProgress()
        userProgressDao.insertUserProgress(current.copy(isDarkMode = enabled))
    }

    suspend fun setUserRole(role: String) {
        val current = getOrCreateUserProgress()
        userProgressDao.insertUserProgress(current.copy(userRole = role))
    }

    suspend fun bookmarkCourse(courseId: String, isBookmarked: Boolean) {
        val current = getOrCreateUserProgress()
        val list = current.bookmarkedItems.split(",").filter { it.isNotEmpty() }.toMutableList()
        if (isBookmarked && !list.contains(courseId)) {
            list.add(courseId)
        } else if (!isBookmarked) {
            list.remove(courseId)
        }
        userProgressDao.insertUserProgress(current.copy(bookmarkedItems = list.joinToString(",")))
    }

    // Chat History
    val chatMessagesFlow: Flow<List<ChatMessage>> = chatMessageDao.getAllMessagesFlow()

    suspend fun sendChatMessage(userText: String): String {
        // Save user message
        chatMessageDao.insertMessage(ChatMessage(sender = "USER", message = userText))

        // Get full chat history for context
        val history = chatMessageDao.getAllMessagesFlow().firstOrNull() ?: emptyList()

        // Call Gemini API
        val reply = geminiService.getTutorResponse(userText, history)

        // Save AI reply
        chatMessageDao.insertMessage(ChatMessage(sender = "AI", message = reply))
        return reply
    }

    suspend fun clearChatHistory() {
        chatMessageDao.clearHistory()
    }

    // Course Progress
    val allCourseProgressFlow: Flow<List<CourseProgress>> = courseProgressDao.getAllCourseProgressFlow()

    suspend fun updateCourseStep(courseId: String, step: Int, isCompleted: Boolean = false, quizScore: Int = -1) {
        val currentProgress = courseProgressDao.getCourseProgress(courseId) ?: CourseProgress(courseId)
        val updated = currentProgress.copy(
            currentStep = step,
            isCompleted = isCompleted || currentProgress.isCompleted,
            quizScore = if (quizScore >= 0) quizScore else currentProgress.quizScore
        )
        courseProgressDao.insertCourseProgress(updated)

        // Award XP if completed
        if (isCompleted && !currentProgress.isCompleted) {
            updateXpAndStreak(150)
            
            // Mark course as completed in user progress string
            val user = getOrCreateUserProgress()
            val completedList = user.completedCourses.split(",").filter { it.isNotEmpty() }.toMutableList()
            if (!completedList.contains(courseId)) {
                completedList.add(courseId)
                userProgressDao.insertUserProgress(user.copy(completedCourses = completedList.joinToString(",")))
            }
        }
    }

    // Social Posts
    val socialPostsFlow: Flow<List<SocialPost>> = socialPostDao.getAllPostsFlow()

    suspend fun createSocialPost(authorName: String, authorRole: String, text: String) {
        socialPostDao.insertPost(
            SocialPost(
                authorName = authorName,
                authorRole = authorRole,
                text = text
            )
        )
    }

    suspend fun likePost(post: SocialPost) {
        socialPostDao.updatePost(post.copy(likesCount = post.likesCount + 1))
    }

    // Initialize with fallback mock posts if database is empty
    suspend fun prepopulateDbIfNeeded() {
        // Prepopulate some lovely posts if empty
        val posts = socialPostDao.getAllPostsFlow().firstOrNull() ?: emptyList()
        if (posts.isEmpty()) {
            socialPostDao.insertPost(
                SocialPost(
                    authorName = "Monsieur Halim",
                    authorRole = "Enseignant (معلم)",
                    text = "Bienvenue à tous sur notre plateforme éducative ! Prêts pour une merveilleuse année d'apprentissage du français avec excellence ? 🚀✨\nمرحباً بكم جميعاً في منصتنا التعليمية! هل أنتم مستعدون لعام رائع من تعلم اللغة الفرنسية بتميز؟",
                    likesCount = 24
                )
            )
            socialPostDao.insertPost(
                SocialPost(
                    authorName = "Khadidja (Maman de Rayan)",
                    authorRole = "Parent (ولي أمر)",
                    text = "Mon fils adore déjà les jeux d'orthographe ! C'est une excellente façon d'apprendre sans s'ennuyer. Merci Monsieur Halim ! 👍🌟",
                    likesCount = 12
                )
            )
            socialPostDao.insertPost(
                SocialPost(
                    authorName = "Yacine",
                    authorRole = "Élève (تلميذ)",
                    text = "L'assistant IA de Monsieur Halim m'a aidé à corriger mon paragraphe en 2 secondes ! C'est magique ! 🤖🔥",
                    likesCount = 18
                )
            )
        }
    }
}
