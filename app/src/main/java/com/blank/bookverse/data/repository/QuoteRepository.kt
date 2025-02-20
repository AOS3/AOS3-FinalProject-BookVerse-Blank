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
            .orderBy("created_at", Query.Direction.DESCENDING)
            .get()
            .await()
            .documents
            .map { it.toComment() }
    }

    // 더보기 화면 - 모든 책 목록
    suspend fun getAllBooks(): List<Book> {
        return firestore.collection("Books")
            .whereEqualTo("memberId", firestoreAuth.uid)
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
        // 트랜잭션으로 처리
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

    suspend fun updateBookmark(quoteDocId: String, isBookmark: Boolean) {
        try {
            // 여기서 await()를 호출하지 않으면 요청이 실제로 실행되지 않을 수 있습니다
            firestore.collection("Quotes")
                .document(quoteDocId)
                .update("is_bookmark", isBookmark)
                .await()
        } catch (e: Exception) {
            Log.e("QuoteRepository", "Error updating bookmark", e)
            throw e
        }
    }
}