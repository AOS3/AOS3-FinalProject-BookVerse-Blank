package com.blank.bookverse.presentation.ui.book_detail

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.blank.bookverse.data.HomeQuote
import com.blank.bookverse.data.repository.BookDetailRepository
import com.blank.bookverse.presentation.model.QuoteUiModel
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
        getQuoteList()
    }

    // 책 정보 불러오기
    private fun getBookInfo(bookTitle: String) = viewModelScope.launch {
        runCatching {
            _bookDetailUiState.value = BookDetailUiState(isLoading = true)
            repository.getBookInfo(bookTitle)
        }.onSuccess {
            _bookDetailUiState.value = BookDetailUiState(
                isLoading = false,
                quote = it
            )
        }.onFailure {
            _bookDetailUiState.value = BookDetailUiState(isLoading = false)
        }
    }

    private fun getQuoteList() = viewModelScope.launch {
        runCatching {
            repository.getQuoteList(title)
        }.onSuccess { quoteList ->
            _bookDetailUiState.value = _bookDetailUiState.value.copy(
                quoteList = quoteList.map { QuoteUiModel.from(it) }
            )
        }
    }


    fun navigateToQuoteDetail(quoteContent: String) = viewModelScope.launch {
        _bookDetailEffect.emit(BookDetailEffect.NavigateToQuoteDetail(quoteContent))
    }

}

data class BookDetailUiState(
    val isLoading: Boolean = false,
    val quote: HomeQuote? = null,
    val quoteList: List<QuoteUiModel> = emptyList(),
)

sealed class BookDetailEffect {
    data class NavigateToQuoteDetail(val quoteContent: String): BookDetailEffect()
}