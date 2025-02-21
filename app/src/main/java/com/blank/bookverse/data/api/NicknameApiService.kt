package com.blank.bookverse.data.api

import com.blank.bookverse.data.model.RandomNicknameResponse
import retrofit2.Response
import retrofit2.http.POST

interface NicknameApiService {
    @POST("nickname/getRandomNickname.ajax")
    suspend fun getRandomNickname(): Response<RandomNicknameResponse>
}