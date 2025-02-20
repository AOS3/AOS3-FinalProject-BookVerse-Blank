package com.blank.bookverse.presentation.ui.quote_detail

import android.util.Log
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.blank.bookverse.data.repository.QuoteRepository
import com.blank.bookverse.presentation.model.QuoteDetailUiModel
import com.blank.bookverse.presentation.navigation.MainNavItem
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class QuoteDetailViewModel @Inject constructor(
    private val savedStateHandle: SavedStateHandle,
    private val quoteRepository: QuoteRepository,
): ViewModel() {
    private val quoteDocId: String = checkNotNull(savedStateHandle[MainNavItem.QuoteDetail.ID_ARG])

    private val _quoteDetailUiState = MutableStateFlow(QuoteDetailUiState())
    val quoteDetailUiState = _quoteDetailUiState.asStateFlow()

    init {
        loadQuoteDetail()
    }
    private fun loadQuoteDetail() = viewModelScope.launch {
        _quoteDetailUiState.value = _quoteDetailUiState.value.copy(isLoading = true)

        val result = runCatching {
            val quote = quoteRepository.getQuoteDetail(quoteDocId)
            val comments = quoteRepository.getQuoteComments(quoteDocId)
            QuoteDetailUiModel.from(quote, comments)
        }

        result.onSuccess { quoteDetail ->
            _quoteDetailUiState.value = _quoteDetailUiState.value.copy(
                quoteDetail = quoteDetail,
                isLoading = false
            )
        }.onFailure { error ->
            Log.e("QuoteDetailViewModel", "Error loading quote detail", error)
            _quoteDetailUiState.value = _quoteDetailUiState.value.copy(isLoading = false)
        }
    }

    fun updateBookmark(isBookmark: Boolean) = viewModelScope.launch {
        runCatching {
            quoteRepository.updateBookmark(quoteDocId, isBookmark)
        }.onSuccess {
            _quoteDetailUiState.value = _quoteDetailUiState.value.copy(
                quoteDetail = _quoteDetailUiState.value.quoteDetail?.copy(
                    isBookmark = isBookmark
                )
            )
        }.onFailure { error ->
            Log.e("QuoteDetailViewModel", "Error updating bookmark", error)
        }
    }
}

data class QuoteDetailUiState(
    val isLoading: Boolean = false,
    val quoteDetail: QuoteDetailUiModel? = null
)