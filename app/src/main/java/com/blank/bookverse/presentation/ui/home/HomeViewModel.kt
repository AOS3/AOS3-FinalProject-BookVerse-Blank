package com.blank.bookverse.presentation.ui.home

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.blank.bookverse.data.model.RecommendationContent
import com.blank.bookverse.data.repository.QuoteRepository
import com.blank.bookverse.data.repository.RecommendationRepository
import com.blank.bookverse.presentation.model.HomeBookUiModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val quoteRepository: QuoteRepository,
    private val recommendationRepository: RecommendationRepository,
) : ViewModel() {
    private val _homeUiState = MutableStateFlow(HomeUiState())
    val homeUiState: StateFlow<HomeUiState> = _homeUiState.asStateFlow()

    private val _homeEffect = MutableSharedFlow<HomeEffect>()
    val homeEffect: SharedFlow<HomeEffect> = _homeEffect

    init {
        loadRecommendationContent()
        loadBooks()
    }

    private fun loadRecommendationContent() = viewModelScope.launch {
        runCatching {
            _homeUiState.value = _homeUiState.value.copy(isLoading = true)
            recommendationRepository.getTodayRecommendationContent()
        }.onSuccess { recommendation ->
            Log.d("HomeViewModel", "Loaded recommendation: $recommendation")
            _homeUiState.value = _homeUiState.value.copy(
                recommendationContent = recommendation,
                isLoading = false
            )
        }.onFailure { error ->
            Log.e("HomeViewModel", "Error loading recommendation", error)
            _homeUiState.value = _homeUiState.value.copy(isLoading = false)
        }
    }

    fun loadBooks() = viewModelScope.launch {
        runCatching {
            _homeUiState.value = _homeUiState.value.copy(isLoading = true)
            quoteRepository.getHomeBookList()
        }.onSuccess { books ->
            val bookUiModels = books.map { HomeBookUiModel.from(it) }
            _homeUiState.value = _homeUiState.value.copy(
                books = bookUiModels,
                isLoading = false,
            )
        }.onFailure { error ->
            _homeUiState.value = _homeUiState.value.copy(isLoading = false)
        }
    }

    fun navigateToBookDetail(bookDocId: String) = viewModelScope.launch {
        _homeEffect.emit(HomeEffect.NavigateToBookDetail(bookDocId))
    }
}

data class HomeUiState(
    val isLoading: Boolean = false,
    val recommendationContent: RecommendationContent = RecommendationContent(
        quote = "북버스에서 내 글귀를 남겨보세요",
        bookTitle = "북버스"
    ),
    val books: List<HomeBookUiModel> = emptyList(),
)

sealed class HomeEffect {
    data class NavigateToBookDetail(val bookDocId: String) : HomeEffect()
}