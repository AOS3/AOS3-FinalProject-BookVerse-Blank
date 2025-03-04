package com.blank.bookverse.presentation.ui.takeBook

import android.R.attr.text
import android.net.Uri
import android.util.Log
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavController
import com.blank.bookverse.data.api.ocr.OcrCLOVA
import com.blank.bookverse.data.api.search.DocumentsObject
import com.blank.bookverse.data.repository.SearchRepository
import com.blank.bookverse.presentation.navigation.CameraNavItem
import com.blank.bookverse.presentation.navigation.MainNavItem
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import timber.log.Timber
import java.net.URLEncoder
import javax.inject.Inject

@HiltViewModel
class BookBarCodeViewModel @Inject constructor(
    private val searchRepository: SearchRepository
) : ViewModel() {
    private val searchNotEnabled = mutableStateOf(true)
    private val searchApi = MutableLiveData<DocumentsObject?>(null)
    private val searchEnabled = mutableStateOf(true)

    var observer = Observer<DocumentsObject?>{}

    fun observerSetting(backValue:()-> Unit){
        observer = Observer<DocumentsObject?>{
            searchNotEnabled.value = searchApi.value != null
            Timber.tag("test5").d("getOcrNotEnabled ${getSearchNotEnabled()}")
            if (getSearchNotEnabled()) {
                backValue()
            }
        }
    }

    override fun onCleared() {
        searchNotEnabled.value = true
        searchApi.removeObserver(observer)
        super.onCleared()
    }
    fun uploadFailed() =
        searchApi.value != null

    fun getSearchField() = searchApi.value!!

    fun getSearchNotEnabled() =
        searchNotEnabled.value


    fun onSearch(isbn: String,navController: NavController){
        viewModelScope.launch{
            if (isbn != "" && searchEnabled.value){
                searchEnabled.value = false
                Timber.tag("st").d("isbn $isbn")
            val response = viewModelScope.async(Dispatchers.IO){
                searchRepository.getSearchApi(isbn,true).body()!!
            }.await()
                if (response.documents.isNotEmpty()) {
                    Timber.tag("st").d("resultList")
                    Timber.tag("st").d("${response.documents.first()}")
                    searchApi.postValue(
                        viewModelScope.async(Dispatchers.IO) {
                            response.documents.first()
                        }.await()
                    )

                    Timber.tag("st").d("searchApi ${searchApi.value}")
                    if (searchApi.value != null) {
                        val book = searchApi.value!!
                        navController.navigate(
                            MainNavItem.QuoteWrite.createRoute(
                                book.isbn,
                                book.title,
                                URLEncoder.encode(book.thumbnail, "UTF-8"),
                                null
                            )
                        ){
                            popUpTo(CameraNavItem.BookBarCode.route) {
                                inclusive = true
                            }
                        }

                    }
                }
                searchEnabled.value = true

            }else{

            }

        }.onJoin
    }
}