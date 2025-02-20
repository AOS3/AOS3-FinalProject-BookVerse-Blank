package com.blank.bookverse.presentation.ui.bookmark

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.blank.bookverse.data.repository.QuoteRepository
import com.blank.bookverse.presentation.model.BookmarkUiModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class BookmarkViewModel @Inject constructor(
    private val quoteRepository: QuoteRepository,
): ViewModel() {
    private val _bookmarkUiState = MutableStateFlow(BookmarkUiState())
    val bookmarkUiState = _bookmarkUiState.asStateFlow()

    init {
        getUserBookmarkedQuotes()
    }

    private fun getUserBookmarkedQuotes() = viewModelScope.launch {
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
        }.onFailure {
            _bookmarkUiState.value = _bookmarkUiState.value.copy(isLoading = false)
        }
    }
}

data class BookmarkUiState(
    val bookmarkedQuotes: List<BookmarkUiModel> = emptyList(),
    val isLoading: Boolean = false,
)