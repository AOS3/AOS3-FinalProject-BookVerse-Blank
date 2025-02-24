package com.blank.bookverse.presentation.theme

import androidx.compose.ui.text.font.FontFamily


enum class FontType(val fontFamily: FontFamily) {
    NOTO_SANS(notoSansFamily);

    // 화면에 표시할 이름
    fun displayName(): String {
        return when (this) {
            NOTO_SANS -> "Noto Sans"
        }
    }
}