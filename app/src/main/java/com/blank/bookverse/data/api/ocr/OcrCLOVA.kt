package com.blank.bookverse.data.api.ocr

import com.google.gson.annotations.SerializedName

data class OcrCLOVA(
    @SerializedName("response")
    var responseApiClass:ResponseOcr
)

data class ResponseOcr(
    @SerializedName("body")
    var bodyApiClass:BodyOcr,

    @SerializedName("header")
    var headerApiClass:HeaderOcr
)

data class HeaderOcr(
    @SerializedName("resultMsg")
    var resultMsg:String,

    @SerializedName("resultCode")
    var resultCode:String
)

data class BodyOcr(
    @SerializedName("items")
    var itemsApiList:List<BodyItem>
)