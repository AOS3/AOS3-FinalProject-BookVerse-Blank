package com.blank.bookverse.presentation.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.blank.bookverse.data.HomeQuote
import com.blank.bookverse.data.RecommendationContent
import com.blank.bookverse.data.Storage
import com.blank.bookverse.data.repository.HomeRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val homeRepository: HomeRepository,
): ViewModel() {
    private val _homeUiState = MutableStateFlow(HomeUiState())
    val homeUiState: StateFlow<HomeUiState> = _homeUiState.asStateFlow()

    private val _homeEffect = MutableSharedFlow<HomeEffect>()
    val homeEffect: MutableSharedFlow<HomeEffect> = _homeEffect

    init {
        loadRecommendationContent()
        loadHomeQuoteList()
    }

    private fun loadRecommendationContent() = viewModelScope.launch {
        runCatching {
            homeRepository.getRecommendationContent()
        }.onSuccess {
            _homeUiState.value = _homeUiState.value.copy(
                recommendationContent = it,
                isLoading = false,
            )
        }.onFailure {
            _homeUiState.value = _homeUiState.value.copy(
                isLoading = false
            )
        }

    }

    private fun loadHomeQuoteList() = viewModelScope.launch {
        runCatching {
            homeRepository.getHomeQuoteList()
        }.onSuccess { quotes ->
            _homeUiState.value = _homeUiState.value.copy(
                homeQuoteList = quotes,
                isLoading = false,
                isMore = quotes.size >= 7
            )
        }.onFailure {
            _homeUiState.value = _homeUiState.value.copy(
                isLoading = false
            )
        }
    }
}

data class HomeUiState(
    val recommendationContent: RecommendationContent =
        RecommendationContent(
            quote = "북버스에서 내 글귀를 남겨보세요",
            bookTitle = "북버스"
        ),
    val homeQuoteList: List<HomeQuote> = emptyList(),
    val isLoading: Boolean = true,
    val isMore: Boolean = false,
)

sealed class HomeEffect {
    data class NavigateToBookDetail(val id: String): HomeEffect()
}