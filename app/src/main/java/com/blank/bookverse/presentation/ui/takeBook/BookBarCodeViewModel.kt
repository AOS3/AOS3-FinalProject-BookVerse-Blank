package com.blank.bookverse.presentation.ui.takeBook

import android.R.attr.text
import android.net.Uri
import android.util.Log
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.blank.bookverse.data.api.ocr.OcrCLOVA
import com.blank.bookverse.data.api.search.DocumentsObject
import com.blank.bookverse.data.repository.SearchRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class BookBarCodeViewModel @Inject constructor(
    private val searchRepository: SearchRepository
) : ViewModel() {
    private val searchNotEnabled = mutableStateOf(true)
    private val searchApi = MutableLiveData<DocumentsObject?>(null)

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


    fun onSearch(isbn: String){
        viewModelScope.launch{
            val response = viewModelScope.async(Dispatchers.IO){
                searchRepository.getSearchApi(isbn,true)
            }.await()
            Timber.tag("test5").d("resultList")
            Timber.tag("test5").d("${response.body()!!.documents}")
            searchApi.value = viewModelScope.async{
                response.body()!!.documents.first()
            }.await()

        }.onJoin
    }
}