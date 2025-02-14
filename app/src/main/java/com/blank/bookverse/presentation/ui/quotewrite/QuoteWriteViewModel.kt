package com.blank.bookverse.presentation.ui.quotewrite

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class QuoteWriteViewModel@Inject constructor(

):ViewModel() {

    val quoteText = mutableStateOf("")
    val thinkText = mutableStateOf("")
    val thinkSingleText = mutableStateOf("")
    val thinkList = mutableListOf<String>()
    val staticList = mutableListOf<String>("감동", "슬픔", "힐링", "공감", "명상",
        "고전", "배움", "성장", "성공", "로맨스")
    val bottomSheetVisible = mutableStateOf(false)

    val writeEnabled = mutableStateOf(false)
    val addChange = mutableStateOf(true)
    val thinkAddEnabled = mutableStateOf(false)
    val completeEnable = mutableStateOf<Boolean>(false)
    fun bottomSheetOpen(){
        bottomSheetVisible.value = true
    }
    fun onDismissAddChange(change: Boolean){
        addChange.value = change
        if (!(addChange.value)){
            thinkSingleText.value = ""
        }
    }

    fun completeScreen(change: Boolean){
        completeEnable.value = change
        if (addChange.value){

        }
    }
}