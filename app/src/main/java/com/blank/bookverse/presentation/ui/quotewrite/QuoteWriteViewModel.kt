package com.blank.bookverse.presentation.ui.quotewrite

import android.util.Log
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.blank.bookverse.data.repository.QuoteRepository
import com.blank.bookverse.presentation.navigation.MainNavItem
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class QuoteWriteViewModel@Inject constructor(
    private val quoteRepository: QuoteRepository
):ViewModel() {
    val bookTitle = mutableStateOf("")
    val bookCover = mutableStateOf("")
    val quoteText = mutableStateOf("")
    val thinkText = mutableStateOf("")
    val thinkSingleText = mutableStateOf("")
    val thinkList = mutableStateListOf<String>()
    val staticList = mutableStateListOf<String>("감동", "슬픔", "힐링", "공감", "명상",
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

    fun thinkTextClearAdd(think: String){
        thinkSingleText.value = ""
        thinkListAdd(think)
    }

    fun thinkListAdd(think: String){
        val addThink = think.filterIndexed {idx,char->
            !(idx== 0 && char == '#')
        }
        thinkList.add(addThink)
    }

    fun thinkListRemoveAt(it:Int){
        if (it != thinkList.size){
            thinkList.removeAt(it)
        }

    }

    fun bookCoverUpdate(bookCover: String){
        Log.d("st","bookCover $bookCover")
        this.bookCover.value = bookCover
    }

    fun bookTitleUpdate(bookTitle: String){
        Log.d("st","bookTitle $bookTitle")
        this.bookTitle.value = bookTitle
    }

    fun quoteUpdate(quote: String){
        Log.d("st","bookTitle $quote")
        this.quoteText.value = quote
    }
}