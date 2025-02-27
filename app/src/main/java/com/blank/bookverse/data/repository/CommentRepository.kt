package com.blank.bookverse.data.repository

import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CommentRepository @Inject constructor(
    private val firestore: FirebaseFirestore,
) {
    fun addComment(
        quoteDocId: String,
        commentContent: String
    ): Flow<CommentResult<Unit>> = flow {
        try {
            emit(CommentResult.Loading)
            val commentDocId = firestore.collection("Comments").document().id

            val comment = hashMapOf(
                "comment_doc_id" to commentDocId,
                "quote_doc_id" to quoteDocId,
                "comment_content" to commentContent,
                "is_delete" to false,
                "created_at" to System.currentTimeMillis()
            )

            firestore.collection("Comments")
                .document(commentDocId)
                .set(comment)
                .await()

            emit(CommentResult.Success(Unit))
        } catch (e: Exception) {
            emit(CommentResult.Error(e))
        }
    }
}


sealed class CommentResult<out T> {
    data object Loading : CommentResult<Nothing>()
    data class Success<T>(val data: T) : CommentResult<T>()
    data class Error(val exception: Exception) : CommentResult<Nothing>()
}