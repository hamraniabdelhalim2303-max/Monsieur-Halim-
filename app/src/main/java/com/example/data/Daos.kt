package com.example.data

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface UserProgressDao {
    @Query("SELECT * FROM user_progress WHERE id = 1")
    fun getUserProgressFlow(): Flow<UserProgress?>

    @Query("SELECT * FROM user_progress WHERE id = 1")
    suspend fun getUserProgress(): UserProgress?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUserProgress(progress: UserProgress)

    @Update
    suspend fun updateUserProgress(progress: UserProgress)
}

@Dao
interface ChatMessageDao {
    @Query("SELECT * FROM chat_message ORDER BY timestamp ASC")
    fun getAllMessagesFlow(): Flow<List<ChatMessage>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMessage(message: ChatMessage)

    @Query("DELETE FROM chat_message")
    suspend fun clearHistory()
}

@Dao
interface CourseProgressDao {
    @Query("SELECT * FROM course_progress")
    fun getAllCourseProgressFlow(): Flow<List<CourseProgress>>

    @Query("SELECT * FROM course_progress WHERE courseId = :courseId")
    suspend fun getCourseProgress(courseId: String): CourseProgress?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCourseProgress(progress: CourseProgress)
}

@Dao
interface SocialPostDao {
    @Query("SELECT * FROM social_post ORDER BY timestamp DESC")
    fun getAllPostsFlow(): Flow<List<SocialPost>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPost(post: SocialPost)

    @Update
    suspend fun updatePost(post: SocialPost)
}
