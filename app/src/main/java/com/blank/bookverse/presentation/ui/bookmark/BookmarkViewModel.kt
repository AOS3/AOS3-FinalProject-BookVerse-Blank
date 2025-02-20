package com.blank.bookverse.presentation.ui.bookmark

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.blank.bookverse.data.repository.QuoteRepository
import com.blank.bookverse.presentation.model.BookmarkUiModel
import com.blank.bookverse.presentation.ui.book_detail.BookDetailEffect
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class BookmarkViewModel @Inject constructor(
    private val quoteRepository: QuoteRepository,
): ViewModel() {
    private val _bookmarkUiState = MutableStateFlow(BookmarkUiState())
    val bookmarkUiState = _bookmarkUiState.asStateFlow()

    private val _bookDetailEffect = MutableSharedFlow<BookDetailEffect>()
    val bookDetailEffect = _bookDetailEffect.asSharedFlow()

    init {
        getUserBookmarkedQuotes()
    }

    fun getUserBookmarkedQuotes() = viewModelScope.launch {
        runCatching {
            _bookmarkUiState.value = _bookmarkUiState.value.copy(isLoading = true)
            quoteRepository.getUserBookmarkedQuotes()
        }.onSuccess { quotes ->
            val bookmarkUiModels = quotes.map { quote ->
                BookmarkUiModel.from(quote)
            }
            _bookmarkUiState.value = _bookmarkUiState.value.copy(
                bookmarkedQuotes = bookmarkUiModels,
                isLoading = false
            )
        }.onFailure { error ->
            Log.e("BookmarkViewModel", "Error loading bookmarked quotes", error)
            _bookmarkUiState.value = _bookmarkUiState.value.copy(isLoading = false)
        }
    }

    fun updateBookmark(quoteDocId: String, isBookmark: Boolean) = viewModelScope.launch {
        runCatching {
            quoteRepository.updateBookmark(quoteDocId, isBookmark)
        }.onSuccess {
            // 북마크가 false로 변경되면 목록에서 제거
            if (!isBookmark) {
                val updatedQuotes = _bookmarkUiState.value.bookmarkedQuotes.filter {
                    it.quoteDocId != quoteDocId
                }
                _bookmarkUiState.value = _bookmarkUiState.value.copy(
                    bookmarkedQuotes = updatedQuotes
                )
            }
        }.onFailure { error ->
            Log.e("BookmarkViewModel", "Error updating bookmark", error)
        }
    }

    fun navigateToQuoteDetail(quoteDocId: String) = viewModelScope.launch {
        _bookDetailEffect.emit(BookDetailEffect.NavigateToQuoteDetail(quoteDocId))
    }
}

data class BookmarkUiState(
    val bookmarkedQuotes: List<BookmarkUiModel> = emptyList(),
    val isLoading: Boolean = false,
)