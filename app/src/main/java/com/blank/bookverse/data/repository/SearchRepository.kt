package com.blank.bookverse.data.repository

import android.R.attr.visibility
import android.content.Context
import android.provider.SyncStateContract.Helpers.update
import android.util.Log
import com.blank.bookverse.data.api.search.SearchResponse
import com.blank.bookverse.data.api.search.SearchRetrofitHeader
import com.blank.bookverse.data.local.MemberDatabase
import com.blank.bookverse.data.local.MemberDatabase.Companion.memberDatabase
import com.blank.bookverse.data.local.entity.MemberRepoEntity
import com.blank.bookverse.data.mapper.toBook
import com.blank.bookverse.data.model.Book
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentId
import com.google.firebase.firestore.FieldPath.documentId
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.kakao.sdk.common.KakaoSdk.init
import com.kakao.sdk.template.model.ItemContent
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.tasks.await
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SearchRepository @Inject constructor(

){

    lateinit var localMemberDatabase: MemberDatabase

    fun getSearchApi(query: String) : Response<SearchResponse> {
        val builder = Retrofit.Builder()
        builder.baseUrl("https://dapi.kakao.com/")
        //"v3/search/book"
        builder.addConverterFactory(GsonConverterFactory.create())
        val retrofit = builder.build()

        val repository = retrofit.create(SearchRetrofitHeader::class.java)
        val REST_API_KEY = "c205288a22434072c3cf9db44ac4ebac"
        // 데이터를 받아온다.
        val response = repository.getSearchApi(
            restKey = "KakaoAK $REST_API_KEY",
            query = query,
        ).execute()

        return response
    }

    suspend fun getReSearchList(content: Context): List<String>{
        localMemberDatabase =
            (if (memberDatabase == null)
                MemberDatabase.getInstance(content)
            else memberDatabase)!!

        val memberDocId = FirebaseAuth.getInstance().currentUser?.uid

        if (memberDocId != null){
            val member = selectRoomDataMember(documentId = memberDocId)
            if (member == null){
                addRoomDataMember(documentId = memberDocId)
                Log.d("st","db : New list")
                return emptyList()
            }
            Log.d("st","db : Success list")
            return getReSearchListType(member.reSearchList)


        }else{
            Log.e("st","db : documentId null")
            return emptyList()
        }
    }

    suspend fun updateReSearchList(content: Context,reSearchList: List<String>): Boolean{
        localMemberDatabase =
            (if (memberDatabase == null)
                MemberDatabase.getInstance(content)
            else memberDatabase)!!

        val memberDocId = FirebaseAuth.getInstance().currentUser?.uid

        if (memberDocId != null){
            val member = selectRoomDataMember(documentId = memberDocId)
            if (member ==null){
                Log.d("st","db : member Nothing")
                return false
            }
            val updateMember = member.copy(reSearchList = reSearchListFold(reSearchList))
            updateMemberData(repoEntity = updateMember)
            Log.d("st","db : update list")
            return true
        }else{
            Log.e("st","db : documentId null")
            return false
        }
    }

    suspend fun addRoomDataMember(documentId: String){
        val addList =
            listOf(
            MemberRepoEntity(
                id = 0,
                documentId = documentId,
                reSearchList = "",
                visibility = ""
            )
        )


        localMemberDatabase.dao().insertRepos(addList)
    }


    suspend fun selectRoomDataMember(documentId: String): MemberRepoEntity?{
        return localMemberDatabase.dao().getReposFlow(documentId).first()
    }
    suspend fun updateMemberData(repoEntity: MemberRepoEntity)
            = localMemberDatabase.dao().updateMemberData(repoEntity)

    fun reSearchListFold(reSearchList: List<String>)
    = reSearchList.fold(""){init,it->
        when{
            init == "" -> it
            else -> "$init;$it"
        }
    }

    fun getReSearchListType(reSearchList: String) = reSearchList.split(';')
}