package com.blank.bookverse.presentation.ui.add_comment

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.blank.bookverse.data.repository.CommentRepository
import com.blank.bookverse.data.repository.CommentResult
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AddCommentViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val commentRepository: CommentRepository,
) : ViewModel() {
    private val quoteDocId: String = checkNotNull(savedStateHandle["quoteDocId"])

    private val _state = mutableStateOf(AddCommentState())
    val state: State<AddCommentState> = _state

    private val _effect = MutableSharedFlow<AddCommentEffect>()
    val effect = _effect.asSharedFlow()

    init {
        viewModelScope.launch {
            _effect.emit(AddCommentEffect.ShowKeyboard)
            _effect.emit(AddCommentEffect.FocusTextField)
        }
    }

    fun updateText(text: String) {
        if (text.length <= MAX_COMMENT_LENGTH) {
            _state.value = _state.value.copy(
                text = text
            )
        }
    }

    fun submitComment() = viewModelScope.launch {
        commentRepository.addComment(quoteDocId, state.value.text).collect { result ->
            when (result) {
                is CommentResult.Loading -> _state.value = _state.value.copy(isLoading = true)
                is CommentResult.Success -> {
                    _state.value = _state.value.copy(isLoading = false, text = "")
                    _effect.emit(AddCommentEffect.SaveSuccess)
                }

                is CommentResult.Error -> {
                    _state.value = _state.value.copy(isLoading = false)
                    _effect.emit(
                        AddCommentEffect.SaveFailure(
                            result.exception.message ?: "알 수 없는 오류가 발생했습니다."
                        )
                    )
                }
            }
        }
    }

    companion object {
        const val MAX_COMMENT_LENGTH = 100
    }
}

data class AddCommentState(
    val isLoading: Boolean = false,
    val text: String = "",
)

sealed class AddCommentEffect {
    data object ShowKeyboard : AddCommentEffect()
    data object FocusTextField : AddCommentEffect()
    data object SaveSuccess : AddCommentEffect()
    data class SaveFailure(val message: String) : AddCommentEffect()
}