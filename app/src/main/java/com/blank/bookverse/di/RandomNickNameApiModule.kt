package com.blank.bookverse.di

import com.blank.bookverse.data.api.NicknameApiService
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object RandomNickNameApiModule {

    @Singleton
    @Provides
    fun getRetrofitInstance(): Retrofit {
        // HTTP 요청 로깅을 위한 Interceptor
        val loggingInterceptor = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }

        // OkHttpClient 설정
        val provideOkHttpClient = OkHttpClient.Builder()
            .addInterceptor(loggingInterceptor)
            .build()

        return Retrofit.Builder()
            .baseUrl("https://www.rivestsoft.com/")
            // Gson을 이용한 JSON 변환기
            .addConverterFactory(GsonConverterFactory.create())
            .client(provideOkHttpClient)  // OkHttpClient 설정
            .build()
    }

    @Singleton
    @Provides
    fun getRandomNickNameApiInstance(retrofit: Retrofit) : NicknameApiService {
        return retrofit.create(NicknameApiService::class.java)
    }
}