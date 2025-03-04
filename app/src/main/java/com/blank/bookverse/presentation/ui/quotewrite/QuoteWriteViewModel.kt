package com.blank.bookverse.presentation.ui.quotewrite

import android.content.Context
import android.util.Log
import androidx.compose.runtime.CompositionContext
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import coil.compose.AsyncImagePainter
import com.blank.bookverse.data.model.Book
import com.blank.bookverse.data.model.Quote
import com.blank.bookverse.data.repository.QuoteRepository
import com.blank.bookverse.presentation.model.QuoteDetailUiModel
import com.blank.bookverse.presentation.navigation.BottomNavItem
import com.blank.bookverse.presentation.navigation.MainNavItem
import com.blank.bookverse.presentation.navigation.navigateSingleTop
import com.blank.bookverse.presentation.util.Constant.captureName
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.kakao.sdk.common.KakaoSdk.init
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import java.io.FileInputStream
import java.net.URLDecoder
import javax.inject.Inject

@HiltViewModel
class QuoteWriteViewModel@Inject constructor(
    private val quoteRepository: QuoteRepository
):ViewModel() {
    var quote = MutableLiveData<Quote?>(null)
    val bookDocId = mutableStateOf("")
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
    val loadingNotEnabled = mutableStateOf(true)
    val thinkAddEnabled = mutableStateOf(false)
    val completeEnable = mutableStateOf<Boolean>(false)

    fun getQuoteNull() = quote.value == null

    fun bottomSheetOpen(){
        bottomSheetVisible.value = true
    }
    fun onDismissAddChange(change: Boolean){
        addChange.value = change
        if (!(addChange.value)){
            thinkSingleText.value = ""
        }
    }

    fun completeScreen(change: Boolean, context: Context, navController: NavController
                       , photoEnabled: Boolean = false){
        Log.d("st","${bookDocId.value}")
        Log.d("st","${bookTitle.value}")
        Log.d("st","${bookCover.value}")
        Log.d("st","${quoteText.value}")
        Log.d("st","${thinkList}")


        completeEnable.value = change
        if (addChange.value){
            viewModelScope.launch{
                loadingNotEnabled.value = false
                val quote = quote.value
                if (quote == null) {
                    val content = quoteText.value

                    val book = Book(
                        bookDocId = bookDocId.value,
                        bookTitle = bookTitle.value,
                        bookCover = bookCover.value,
                    )
                    val file = context.openFileInput(captureName)
                    val quoteDocId = FirebaseFirestore.getInstance().collection("Quotes")
                        .document().id
                    val photoUrl = viewModelScope.async {
                        quoteRepository.uploadCaptureImage(file, quoteDocId)
                    }.await()

                    val quote = Quote(
                        quoteDocId = quoteDocId,
                        bookDocId = book.bookDocId,
                        photoUrl = photoUrl.toString(),
                        quoteContent = content,
                    )
                    viewModelScope.async {
                        quoteRepository.saveQuote(quote, book, thinkList)
                    }.await()

                }else{
                    val file = context.openFileInput(captureName)
                    val photoUrl = viewModelScope.async {
                        quoteRepository.uploadCaptureImage(file, quote.quoteDocId)
                    }.await()

                    val update =
                        quote.run {
                        val url = if(photoEnabled) photoUrl.toString() else this.photoUrl
                        val tagEqual = tags.fold(false){init,it->
                            val tag = it
                            val not = thinkList.fold(false){init,it->
                                if (tag == it) return@fold true
                                else false
                            }
                            if(!not) return@fold false
                            else true
                        }
                        quote.copy(
                            photoUrl = url,
                            quoteContent = if (quoteContent==quoteText.value)quoteContent
                            else quoteText.value,
                            tags = if(tagEqual)tags else thinkList
                        )
                    }


                    viewModelScope.async {
                        quoteRepository.updateQuote(update)
                    }.await()
                }
                navController.navigateSingleTop(BottomNavItem.Home.route)
            }

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

    fun thinkListAddAll(think: List<String>){
        thinkList.clear()
        thinkList.addAll(think)
    }

    fun thinkListRemoveAt(it:Int){
        if (it != thinkList.size){
            thinkList.removeAt(it)
        }

    }

    fun bookDocIdUpdate(bookDocId: String){
        Log.d("st","bookDocId $bookDocId")
        this.bookDocId.value = bookDocId
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
        Log.d("st","- quote")
        Log.d("st"," $quote")
        this.quoteText.value = quote
    }

    fun getBookData(){
        viewModelScope.launch{
            if (quote.value != null){
                val quote = quote.value!!
                val book = quoteRepository.getBookDetail(quote.bookDocId)
                if (book.bookDocId != "") {
                    bookDocIdUpdate(book.bookDocId)
                }
                if (book.bookTitle != "") {
                    bookTitleUpdate(book.bookTitle)
                }
                if (book.bookCover != "") {
                    bookCoverUpdate(book.bookCover)
                }
            }
        }

    }
}