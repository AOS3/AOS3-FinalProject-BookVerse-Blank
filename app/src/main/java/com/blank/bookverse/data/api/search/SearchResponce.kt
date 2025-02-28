package com.blank.bookverse.data.api.search

import com.blank.bookverse.data.api.ocr.ImageItem
import com.google.gson.annotations.SerializedName

data class SearchResponse(
    @SerializedName("meta")
    var meta: MetaObject,
    @SerializedName("documents")
    var documents: List<DocumentsObject>,
)

data class MetaObject(
    @SerializedName("total_count")
    var totalCount: Int,
    @SerializedName("pageable_count")
    var pageableCount: Int,
    @SerializedName("is_end")
    var isEnd: Boolean,
)

data class DocumentsObject(
    @SerializedName("title")
    var title: String,
    @SerializedName("contents")
    var contents: String,
    @SerializedName("url")
    var url: String,
    @SerializedName("isbn")
    var isbn: String,
    @SerializedName("datetime")
    var datetime: String,
    @SerializedName("authors")
    var authors: List<String>,
    @SerializedName("publisher")
    var publisher: String,
    @SerializedName("translators")
    var translators: List<String>,
    @SerializedName("thumbnail")
    var thumbnail: String,
)