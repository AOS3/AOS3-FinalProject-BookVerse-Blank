package com.blank.bookverse.data.model

import com.google.gson.annotations.SerializedName

data class RandomNicknameResponse(
    @SerializedName("msg") val msg: String,
    @SerializedName("code") val code: String,
    @SerializedName("data") val nickname: String
)
