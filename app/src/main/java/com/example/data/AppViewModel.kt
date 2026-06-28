package com.example.data

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class AppViewModel(application: Application) : AndroidViewModel(application) {
    private val db = AppDatabase.getDatabase(application)
    val repository = Repository(db)

    // UI state flows
    private val _currentTab = MutableStateFlow(0) // 0: Accueil, 1: Cours, 2: Bibliothèque, 3: Jeux, 4: Profil
    val currentTab = _currentTab.asStateFlow()

    private val _selectedCourseId = MutableStateFlow<String?>(null)
    val selectedCourseId = _selectedCourseId.asStateFlow()

    private val _selectedStoryId = MutableStateFlow<String?>(null)
    val selectedStoryId = _selectedStoryId.asStateFlow()

    private val _selectedLibraryId = MutableStateFlow<String?>(null)
    val selectedLibraryId = _selectedLibraryId.asStateFlow()

    private val _activeGameId = MutableStateFlow<String?>(null)
    val activeGameId = _activeGameId.asStateFlow()

    private val _isAiLoading = MutableStateFlow(false)
    val isAiLoading = _isAiLoading.asStateFlow()

    private val _toastMessage = MutableStateFlow<String?>(null)
    val toastMessage = _toastMessage.asStateFlow()

    // Room Database Flows
    val userProgress = repository.userProgressFlow.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = UserProgress()
    )

    val chatHistory = repository.chatMessagesFlow.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    val courseProgressList = repository.allCourseProgressFlow.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    val socialPosts = repository.socialPostsFlow.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    init {
        viewModelScope.launch {
            repository.getOrCreateUserProgress()
            repository.prepopulateDbIfNeeded()
        }
    }

    fun setTab(index: Int) {
        _currentTab.value = index
        // Reset sub-screen flows
        _selectedCourseId.value = null
        _selectedStoryId.value = null
        _selectedLibraryId.value = null
        _activeGameId.value = null
    }

    fun selectCourse(courseId: String?) {
        _selectedCourseId.value = courseId
    }

    fun selectStory(storyId: String?) {
        _selectedStoryId.value = storyId
    }

    fun selectLibraryItem(itemId: String?) {
        _selectedLibraryId.value = itemId
    }

    fun selectGame(gameId: String?) {
        _activeGameId.value = gameId
    }

    // Role, Theme & Language toggles
    fun setUserRole(role: String) {
        viewModelScope.launch {
            repository.setUserRole(role)
            showToast("Mode $role activé !")
        }
    }

    fun toggleLanguage() {
        viewModelScope.launch {
            val current = userProgress.value?.currentLanguage ?: "FR"
            val next = if (current == "FR") "AR" else "FR"
            repository.setLanguage(next)
            val msg = if (next == "FR") "Langue changée en Français" else "تم تغيير اللغة إلى العربية"
            showToast(msg)
        }
    }

    fun toggleDarkMode() {
        viewModelScope.launch {
            val current = userProgress.value?.isDarkMode ?: false
            repository.setDarkMode(!current)
        }
    }

    fun showToast(message: String?) {
        _toastMessage.value = message
    }

    fun clearToast() {
        _toastMessage.value = null
    }

    // Chatbot Action
    fun sendChatMessage(text: String) {
        if (text.isBlank()) return
        _isAiLoading.value = true
        viewModelScope.launch {
            try {
                repository.sendChatMessage(text)
            } catch (e: Exception) {
                showToast("Erreur IA: ${e.message}")
            } finally {
                _isAiLoading.value = false
            }
        }
    }

    fun clearChat() {
        viewModelScope.launch {
            repository.clearChatHistory()
        }
    }

    // Course state management
    fun completeCourseStep(courseId: String, currentStep: Int) {
        viewModelScope.launch {
            val nextStep = currentStep + 1
            if (nextStep > 2) {
                // Completed Course!
                repository.updateCourseStep(courseId, 2, isCompleted = true)
                showToast("Félicitations ! Cours terminé ! +150 XP 🎉")
            } else {
                repository.updateCourseStep(courseId, nextStep)
            }
        }
    }

    fun submitQuizScore(courseId: String, score: Int, total: Int) {
        viewModelScope.launch {
            repository.updateCourseStep(courseId, 2, isCompleted = true, quizScore = score)
            // Add custom extra XP for correct answers
            val xpEarned = score * 20
            repository.updateXpAndStreak(xpEarned)
            showToast("Quiz terminé ! Score : $score/$total (+ $xpEarned XP) 🌟")
        }
    }

    // Social actions
    fun addPost(text: String) {
        if (text.isBlank()) return
        viewModelScope.launch {
            val role = userProgress.value?.userRole ?: "STUDENT"
            val roleName = when (role) {
                "STUDENT" -> "Élève (تلميذ)"
                "PARENT" -> "Parent (ولي أمر)"
                else -> "Enseignant (معلم)"
            }
            val name = when (role) {
                "TEACHER" -> "Monsieur Halim"
                "PARENT" -> "Parent de l'élève"
                else -> "Élève Brillant"
            }
            repository.createSocialPost(name, roleName, text)
            showToast("Message publié ! 📝")
        }
    }

    fun likePost(post: SocialPost) {
        viewModelScope.launch {
            repository.likePost(post)
        }
    }

    fun bookmarkItem(courseId: String, isBookmarked: Boolean) {
        viewModelScope.launch {
            repository.bookmarkCourse(courseId, isBookmarked)
            if (isBookmarked) {
                showToast("Ajouté aux favoris ! ❤️")
            } else {
                showToast("Retiré des favoris.")
            }
        }
    }

    fun claimDailyReward() {
        viewModelScope.launch {
            repository.updateXpAndStreak(50, (userProgress.value?.streakDays ?: 12) + 1)
            showToast("Récompense quotidienne réclamée ! +50 XP, Série +1 🔥")
        }
    }

    fun addXp(xp: Int) {
        viewModelScope.launch {
            repository.updateXpAndStreak(xp)
        }
    }
}
