package com.blank.bookverse.data.repository

import android.net.Uri
import android.util.Log
import com.blank.bookverse.data.mapper.toBook
import com.blank.bookverse.data.mapper.toComment
import com.blank.bookverse.data.mapper.toQuote
import com.blank.bookverse.data.model.Book
import com.blank.bookverse.data.model.Comment
import com.blank.bookverse.data.model.Quote
import com.blank.bookverse.presentation.model.QuoteDetailUiModel
import com.blank.bookverse.presentation.util.Constant.captureName
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.storageMetadata
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import java.io.FileInputStream
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class QuoteRepository @Inject constructor(
    private val firestoreAuth: FirebaseAuth,
    private val firestore: FirebaseFirestore,
    private val firebaseStorage: FirebaseStorage
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
            .orderBy("quote_count", Query.Direction.DESCENDING)
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
    suspend fun saveQuote(quote: Quote, book: Book,tagList: List<String>) {
        firestore.runTransaction { transaction ->
            val memberUid = firestoreAuth.uid.toString()
            val saveBook = book.run {
                 hashMapOf(
                    "book_doc_id" to bookDocId,
                     "member_id" to memberUid,
                     "book_title" to bookTitle,
                     "book_cover" to bookCover,
                     "quote_count" to quoteCount,
                     "created_at" to createdAt,
                     "is_delete" to false
                )
            }
            val quoteCollection = firestore.collection("Quotes")
            val quoteRef = quoteCollection.document(quote.quoteDocId)
            val existingQuote = transaction.get(quoteRef)

            val bookRef = firestore.collection("Books").document(book.bookDocId)
            val existingBook = transaction.get(bookRef)


                val quoteCount = if (!existingBook.exists()) {
                    saveBook["quote_count"] = 1
                    transaction.set(bookRef, saveBook)
                    saveBook["quote_count"]
                } else {
                    val countInit = (existingBook.getLong("quote_count") ?: 0) + 1
                    transaction.update(
                        bookRef, "quote_count",
                        countInit
                    )
                    countInit
                }

                val saveQuote = quote.run {
                    hashMapOf(
                        "quote_doc_id" to quoteDocId,
                        "book_doc_id" to bookDocId,
                        "member_id" to memberUid,
                        "photo_url" to photoUrl,
                        "tag" to tagList,
                        "quote_content" to quoteContent,
                        "is_bookmark" to isBookmark,
                        "quote_count" to quoteCount,
                        "created_at" to createdAt,
                        "is_delete" to false
                    )
                }


                transaction.set(quoteRef, saveQuote)
        }.await()
    }

    // 기존 글귀 업데이트
    fun updateQuote(quote: Quote) {
        firestore.runTransaction { transaction ->
            val quoteCollection = firestore.collection("Quotes")
            val bookRef = firestore.collection("Books").document(quote.bookDocId)
            val existingBook = transaction.get(bookRef)
            val memberUid = firestoreAuth.uid.toString()
            val quoteCount = if (!existingBook.exists()) {
                1
            } else {
                (existingBook.getLong("quote_count") ?: 1)
            }
            val saveQuote = quote.run {
                hashMapOf(
                    "quote_doc_id" to quoteDocId,
                    "book_doc_id" to bookDocId,
                    "member_id" to memberUid,
                    "photo_url" to photoUrl,// 들어오기 전에 검사
                    "tag" to tags,// 들어오기 전에 검사
                    "quote_content" to quoteContent,// 들어오기 전에 검사
                    "is_bookmark" to isBookmark,
                    "quote_count" to quoteCount,
                    "created_at" to System.currentTimeMillis(),
                    "is_delete" to false
                )
            }
            val quoteRef = quoteCollection.document(quote.quoteDocId)
            transaction.set(quoteRef,saveQuote)

        }
    }

    // 책 글귀 삭제 (soft delete)
    suspend fun deleteQuote(quoteDocId: String, bookDocId: String) {
        firestore.runTransaction { transaction ->
            val bookRef = firestore.collection("Books").document(bookDocId)
            val quoteRef = firestore.collection("Quotes").document(quoteDocId)

            val book = transaction.get(bookRef)

            if (book.exists()) {
                val currentCount = book.getLong("quote_count") ?: 0
                if (currentCount > 0) {
                    transaction.update(bookRef, "quote_count", currentCount - 1)
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

    // 프로필 이미지 업로드 및 URL 반환
    suspend fun uploadCaptureImage(imageFile: FileInputStream,imageName: String): Uri?
            = withContext(Dispatchers.IO) {
        try {
            val storageRef = firebaseStorage.reference.child("capture_image/").child(imageName)
            val metadata = storageMetadata{
                contentType = "image/png"
            }
            storageRef.putStream(imageFile,metadata).await()
            storageRef.downloadUrl.await()
        } catch (e: Exception) {
            null
        }
    }

    // 프로필 이미지 업로드 및 URL 반환
    suspend fun deleteCaptureImage(imageName: String)
            = withContext(Dispatchers.IO) {
        try {
            val storageRef = firebaseStorage.reference.child("capture_image/").child(imageName)
            storageRef.delete().await()
        } catch (e: Exception) {
            null
        }
    }
}