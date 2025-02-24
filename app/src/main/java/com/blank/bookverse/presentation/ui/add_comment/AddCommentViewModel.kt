package com.blank.bookverse.presentation.ui.add_comment

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AddCommentViewModel @Inject constructor(
) : ViewModel() {
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

    fun submitComment() {
        val currentText = _state.value.text
        if (currentText.isBlank()) return

        viewModelScope.launch {
            _state.value = _state.value.copy(
                isLoading = true
            )

            // TODO: 실제 저장 로직 구현
            _effect.emit(AddCommentEffect.SaveSuccess)
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
    data class SaveError(val message: String) : AddCommentEffect()
}