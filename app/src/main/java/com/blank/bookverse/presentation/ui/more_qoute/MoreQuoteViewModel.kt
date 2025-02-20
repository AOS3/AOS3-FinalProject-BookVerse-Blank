package com.blank.bookverse.presentation.ui.more_qoute

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.blank.bookverse.data.model.HomeQuote
import com.blank.bookverse.data.repository.QuoteRepository
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
        loadAllQuoteList()
    }

    private fun loadAllQuoteList() = viewModelScope.launch {
        runCatching {
            quoteRepository.getAllQuoteList()
        }.onSuccess {
            _moreQuoteUiState.value = _moreQuoteUiState.value.copy(
                quoteList = it
            )
        }.onFailure {
            it.printStackTrace()
        }
    }

    fun navigateToBookDetail(quoteDocId: String) = viewModelScope.launch {
        _moreQuoteEffect.emit(MoreQuoteEffect.NavigateToBookDetail(quoteDocId))
    }
}

data class MoreQuoteUiState(
    val quoteList: List<HomeQuote> = emptyList(),
    )

sealed class MoreQuoteEffect {
    data class NavigateToBookDetail(val id: String) : MoreQuoteEffect()
}