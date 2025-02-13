package com.blank.bookverse.data.model

data class RegisterModel(
    val memberDocId: String,
    val memberName: String,
    val memberProfileImage: String,
    val memberId: String,
    val memberPassword: String,
    val memberPhoneNumber: String,
    val memberNickName: String,
    val LoginType: String,
    val createAt: Long,
    val isDelete: Boolean = false,
)