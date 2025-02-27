package com.blank.bookverse.presentation.ui.more_qoute

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.blank.bookverse.data.repository.QuoteRepository
import com.blank.bookverse.presentation.model.HomeBookUiModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MoreQuoteViewModel @Inject constructor(
    private val quoteRepository: QuoteRepository,
): ViewModel() {
    private val _moreQuoteUiState = MutableStateFlow(MoreQuoteUiState())
    val moreQuoteUiState = _moreQuoteUiState.asStateFlow()

    private val _moreQuoteEffect = MutableSharedFlow<MoreQuoteEffect>()
    val moreQuoteEffect = _moreQuoteEffect.asSharedFlow()

    init {
        loadAllBooks()
    }

    private fun loadAllBooks() = viewModelScope.launch {
        runCatching {
            _moreQuoteUiState.value = _moreQuoteUiState.value.copy(isLoading = true)
            quoteRepository.getAllBooks()
        }.onSuccess { books ->
            _moreQuoteUiState.value = _moreQuoteUiState.value.copy(
                books = books.map { HomeBookUiModel.from(it) },
                isLoading = false
            )
        }.onFailure { error ->
            _moreQuoteUiState.value = _moreQuoteUiState.value.copy(isLoading = false)
            Log.e("MoreQuoteViewModel", "loadAllBooks: $error")
        }
    }

    fun navigateToBookDetail(bookDocId: String) = viewModelScope.launch {
        _moreQuoteEffect.emit(MoreQuoteEffect.NavigateToBookDetail(bookDocId))
    }
}

data class MoreQuoteUiState(
    val isLoading: Boolean = false,
    val books: List<HomeBookUiModel> = emptyList()
)

sealed class MoreQuoteEffect {
    data class NavigateToBookDetail(val bookDocId: String) : MoreQuoteEffect()
}