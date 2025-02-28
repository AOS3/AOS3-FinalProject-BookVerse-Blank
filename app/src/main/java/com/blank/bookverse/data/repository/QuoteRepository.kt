package com.blank.bookverse.data.repository

import android.util.Log
import com.blank.bookverse.data.mapper.toBook
import com.blank.bookverse.data.mapper.toComment
import com.blank.bookverse.data.mapper.toQuote
import com.blank.bookverse.data.model.Book
import com.blank.bookverse.data.model.Comment
import com.blank.bookverse.data.model.Quote
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class QuoteRepository @Inject constructor(
    private val firestoreAuth: FirebaseAuth,
    private val firestore: FirebaseFirestore,
) {
    // 홈 화면 - 사용자가 작성한 글귀가 있는 책 목록
    suspend fun getHomeBookList(): List<Book> {
        return firestore.collection("Books")
            .whereEqualTo("member_id", firestoreAuth.uid)
            .whereEqualTo("is_delete", false)
            .orderBy("quote_count", Query.Direction.DESCENDING)
            .limit(8)
            .get()
            .await()
            .documents
            .map { it.toBook() }
    }

    // 책 상세 화면 - 책 정보
    suspend fun getBookDetail(bookDocId: String): Book {
        return firestore.collection("Books")
            .document(bookDocId)
            .get()
            .await()
            .toBook()
    }


    // 책 상세 화면 - 해당 책의 글귀 목록
    suspend fun getBookQuotes(bookDocId: String): List<Quote> {
        return firestore.collection("Quotes")
            .whereEqualTo("book_doc_id", bookDocId)
            .whereEqualTo("is_delete", false)
            .orderBy("created_at", Query.Direction.DESCENDING)
            .get()
            .await()
            .documents
            .map { it.toQuote() }
    }

    // 글귀 상세 화면 - 글귀 정보
    suspend fun getQuoteDetail(quoteDocId: String): Quote {
        return firestore.collection("Quotes")
            .document(quoteDocId)
            .get()
            .await()
            .toQuote()
    }

    // 글귀 상세 화면 - 코멘트 목록
    suspend fun getQuoteComments(quoteDocId: String): List<Comment> {
        return firestore.collection("Comments")
            .whereEqualTo("quote_doc_id", quoteDocId)
            .whereEqualTo("is_delete", false)
            .orderBy("created_at", Query.Direction.DESCENDING)
            .get()
            .await()
            .documents
            .map { it.toComment() }
    }

    // 더보기 화면 - 모든 책 목록
    suspend fun getAllBooks(): List<Book> {
        return firestore.collection("Books")
            .whereEqualTo("member_id", firestoreAuth.uid)
            .whereEqualTo("is_delete", false)
            .orderBy("quoteCount", Query.Direction.DESCENDING)
            .get()
            .await()
            .documents
            .map { it.toBook() }
    }

    // 현 유저의 가장 많은 글귀 책
    suspend fun getTopBook(): Book? {
        return firestore.collection("Books")
            .whereEqualTo("member_id", firestoreAuth.uid)
            .whereEqualTo("is_delete", false)
            .orderBy("quote_count", Query.Direction.DESCENDING)
            .limit(1)
            .get()
            .await()
            .documents
            .firstOrNull()
            ?.toBook()
    }

    // 새 글귀 저장 (책 정보가 없으면 책도 함께 생성)
    suspend fun saveQuote(quote: Quote, book: Book) {
        firestore.runTransaction { transaction ->
            val bookRef = firestore.collection("Books").document(book.bookDocId)
            val existingBook = transaction.get(bookRef)

            if (!existingBook.exists()) {
                transaction.set(bookRef, book)
            } else {
                transaction.update(
                    bookRef, "quoteCount",
                    (existingBook.getLong("quoteCount") ?: 0) + 1
                )
            }

            val quoteRef = firestore.collection("Quotes").document(quote.quoteDocId)
            transaction.set(quoteRef, quote)
        }.await()
    }

    // 책 글귀 삭제 (soft delete)
    suspend fun deleteQuote(quoteDocId: String, bookDocId: String) {
        firestore.runTransaction { transaction ->
            val bookRef = firestore.collection("Books").document(bookDocId)
            val quoteRef = firestore.collection("Quotes").document(quoteDocId)

            val book = transaction.get(bookRef)

            if (book.exists()) {
                val currentCount = book.getLong("quoteCount") ?: 0
                if (currentCount > 0) {
                    transaction.update(bookRef, "quoteCount", currentCount - 1)
                }

                if (currentCount <= 1) {
                    transaction.update(bookRef, "is_delete", true)
                }
            }
            transaction.update(quoteRef, "is_delete", true)
        }.await()
    }

    // 북마크 상태 업데이트
    suspend fun updateBookmark(quoteDocId: String, isBookmark: Boolean) {
        try {
            firestore.collection("Quotes")
                .document(quoteDocId)
                .update("is_bookmark", isBookmark)
                .await()
        } catch (e: Exception) {
            Log.e("QuoteRepository", "Error updating bookmark", e)
            throw e
        }
    }

    suspend fun getUserBookmarkedQuotes(): List<Quote> {
        return firestore.collection("Quotes")
            .whereEqualTo("is_bookmark", true)
            .whereEqualTo("is_delete", false)
            .get()
            .await()
            .documents
            .map { document -> document.toQuote() }
    }

    fun deleteComment(commentDocId: String) {
        firestore.collection("Comments")
            .document(commentDocId)
            .update("is_delete", true)
    }
}