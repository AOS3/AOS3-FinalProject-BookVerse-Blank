package com.blank.bookverse.presentation.ui.book_detail

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.blank.bookverse.data.HomeQuote
import com.blank.bookverse.data.repository.BookDetailRepository
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
    private val repository: BookDetailRepository,
): ViewModel() {
    private val title: String = checkNotNull(savedStateHandle["title"])

    private val _bookDetailUiState = MutableStateFlow(BookDetailUiState())
    val bookDetailUiState = _bookDetailUiState.asStateFlow()

    private val _bookDetailEffect = MutableSharedFlow<BookDetailEffect>()
    val bookDetailEffect = _bookDetailEffect.asSharedFlow()

    init {
        getBookInfo(title)
    }

    // 책 정보 불러오기
    fun getBookInfo(bookTitle: String) = viewModelScope.launch {
        runCatching {
            _bookDetailUiState.value = BookDetailUiState(isLoading = true)
            repository.getBookInfo(bookTitle)
        }.onSuccess {
            _bookDetailUiState.value = BookDetailUiState(
                quote = it
            )
            _bookDetailUiState.value = BookDetailUiState(isLoading = false)
        }.onFailure {
            _bookDetailUiState.value = BookDetailUiState(isLoading = false)
        }
    }

}

data class BookDetailUiState(
    val isLoading: Boolean = false,
    val quote: HomeQuote? = null,
)

sealed class BookDetailEffect {
    object NavigateToQuoteDetail: BookDetailEffect()
}