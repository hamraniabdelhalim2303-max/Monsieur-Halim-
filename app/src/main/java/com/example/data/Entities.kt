package com.example.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "user_progress")
data class UserProgress(
    @PrimaryKey val id: Int = 1,
    val xpPoints: Int = 2450,
    val streakDays: Int = 12,
    val level: Int = 5,
    val completedCourses: String = "1", // comma-separated course IDs
    val bookmarkedItems: String = "", // comma-separated library item IDs
    val currentLanguage: String = "FR", // "FR" or "AR"
    val isDarkMode: Boolean = false,
    val userRole: String = "STUDENT" // "STUDENT", "PARENT", "TEACHER"
)

@Entity(tableName = "chat_message")
data class ChatMessage(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val sender: String, // "USER", "AI"
    val message: String,
    val timestamp: Long = System.currentTimeMillis()
)

@Entity(tableName = "course_progress")
data class CourseProgress(
    @PrimaryKey val courseId: String,
    val isCompleted: Boolean = false,
    val currentStep: Int = 0, // 0: Video, 1: PDF, 2: Quiz
    val quizScore: Int = -1 // -1 means not taken
)

@Entity(tableName = "social_post")
data class SocialPost(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val authorName: String,
    val authorRole: String, // "Monsieur Halim", "Parent", "Élève"
    val text: String,
    val likesCount: Int = 0,
    val timestamp: Long = System.currentTimeMillis()
)
