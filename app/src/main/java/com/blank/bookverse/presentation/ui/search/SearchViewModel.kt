package com.blank.bookverse.presentation.ui.search

import android.content.Context
import android.util.Log
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.blank.bookverse.data.api.ocr.FieldObject
import com.blank.bookverse.data.api.search.DocumentsObject
import com.blank.bookverse.data.local.MemberDatabase
import com.blank.bookverse.data.model.Book
import com.blank.bookverse.data.repository.QuoteRepository
import com.blank.bookverse.data.repository.SearchRepository
import com.blank.bookverse.presentation.navigation.MainNavItem
import com.kakao.sdk.common.KakaoSdk.init
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import timber.log.Timber
import java.net.URLEncoder
import javax.inject.Inject


@HiltViewModel
class SearchViewModel @Inject constructor(
    private val searchRepository: SearchRepository
) : ViewModel() {
    val searchText = mutableStateOf("")
    private var reSearchList = mutableStateListOf<String>()
    private val resultList = mutableStateListOf<DocumentsObject>()

    fun getReSearchList() = reSearchList
    fun getReSearchList(idx: Int) = reSearchList[idx]
    fun deleteReSearchList(idx: Int,context: Context) {
        viewModelScope.launch{
            reSearchList.remove(reSearchList[idx])
            viewModelScope.async{
                updateReSearchList(context = context)
            }.await()
        }


    }

    fun getResultList() = resultList
    fun getResultList(idx: Int) = resultList[idx]
    fun getResultListNotEmpty() = resultList.isNotEmpty()

    fun onSearch(context: Context){
        viewModelScope.launch{

            if (searchText.value == ""|| searchText.value == " ") return@launch

            val response = viewModelScope.async(Dispatchers.IO){
                searchRepository.getSearchApi(searchText.value)
            }.await()
            Timber.tag("test5").d("resultList")
            Timber.tag("test5").d("${response.body()!!.documents}")
            resultList.clear()
            resultList.addAll(response.body()!!.documents)
            val text = searchText.value.replace("\n", "")
            val not = if (reSearchList.isNotEmpty())
                reSearchList.fold(false) { init,it ->

                    if (it==text){
                        return@fold false
                    }
                    true

            }else true
            Log.d("st","$not")
            if (not){
                reSearchList.add(text)
                viewModelScope.async{
                    searchRepository.updateReSearchList(context,reSearchList)
                }.await()
            }

        }
    }
    fun onQueryChange(){
        resultList.clear()
        Timber.tag("test5").d("isNotEmpty ${resultList.isNotEmpty()}")
    }

    fun setReSearchList(context:Context){
        viewModelScope.launch{
            reSearchList.clear()
            viewModelScope.async{
                reSearchList.addAll(searchRepository.getReSearchList(context))
            }.await()
            Log.d("st","$reSearchList")
        }
    }

    fun updateReSearchList(context: Context){
        viewModelScope.launch{
            Log.d("st","$reSearchList")
            viewModelScope.async{
                searchRepository.updateReSearchList(context,reSearchList)
            }.await()

        }
    }

    fun writeScreen(idx: Int,navController: NavHostController){
        val title =getResultList(idx).title
        val image = URLEncoder.encode(getResultList(idx).thumbnail,"UTF-8")
        Log.d("st","$image")
        navController.navigate(MainNavItem.QuoteWrite.createRoute(title,image))
    }
}