package com.blank.bookverse.presentation.ui.book_detail

import android.util.Log
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.blank.bookverse.data.repository.QuoteRepository
import com.blank.bookverse.presentation.model.BookDetailUiModel
import com.blank.bookverse.presentation.navigation.MainNavItem
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class BookDetailViewModel @Inject constructor(
    private val savedStateHandle: SavedStateHandle,
    private val quoteRepository: QuoteRepository,
): ViewModel() {
    private val bookDocId: String = checkNotNull(savedStateHandle[MainNavItem.BookDetail.ID_ARG])

    private val _bookDetailUiState = MutableStateFlow(BookDetailUiState())
    val bookDetailUiState = _bookDetailUiState.asStateFlow()

    private val _bookDetailEffect = MutableSharedFlow<BookDetailEffect>()
    val bookDetailEffect = _bookDetailEffect.asSharedFlow()

    init {
        loadBookDetail()
    }

    fun loadBookDetail() = viewModelScope.launch {
        _bookDetailUiState.value = _bookDetailUiState.value.copy(isLoading = true)

        val result = runCatching {
            val book = quoteRepository.getBookDetail(bookDocId)
            val quotes = quoteRepository.getBookQuotes(bookDocId)
            BookDetailUiModel.from(book, quotes)
        }

        result.onSuccess { bookDetailUiModel ->
            _bookDetailUiState.value = _bookDetailUiState.value.copy(
                bookDetail = bookDetailUiModel,
                isLoading = false
            )
        }.onFailure { error ->
            Log.e("BookDetailViewModel", "Error loading book detail", error)
            _bookDetailUiState.value = _bookDetailUiState.value.copy(isLoading = false)
        }
    }

    fun updateBookmark(quoteDocId: String, isBookmark: Boolean) = viewModelScope.launch {
        runCatching {
            // 서버 요청
            quoteRepository.updateBookmark(quoteDocId, isBookmark)
        }.onSuccess {
            // 서버 요청 성공 시에만 UI 상태 업데이트
            val currentBookDetail = _bookDetailUiState.value.bookDetail
            val updatedQuotes = currentBookDetail?.quotes?.map { quote ->
                if (quote.quoteDocId == quoteDocId) {
                    quote.copy(isBookmark = isBookmark)
                } else {
                    quote
                }
            }

            _bookDetailUiState.value = _bookDetailUiState.value.copy(
                bookDetail = currentBookDetail?.copy(
                    quotes = updatedQuotes ?: emptyList()
                )
            )
        }.onFailure { error ->
            Log.e("BookDetailViewModel", "Error updating bookmark", error)
        }
    }
    
    fun navigateToQuoteDetail(quoteDocId: String) = viewModelScope.launch {
        _bookDetailEffect.emit(BookDetailEffect.NavigateToQuoteDetail(quoteDocId))
    }
}

data class BookDetailUiState(
    val isLoading: Boolean = false,
    val bookDetail: BookDetailUiModel? = null
)

sealed class BookDetailEffect {
    data class NavigateToQuoteDetail(val quoteDocId: String): BookDetailEffect()
}