package com.blank.bookverse.presentation.theme

import androidx.compose.ui.text.font.FontFamily


enum class FontType(val fontFamily: FontFamily) {
    NOTO_SANS(notoSansFamily),
    NANUM_MYEONGJO(nanumMyungjoFamily);

    // 화면에 표시할 이름
    fun displayName(): String {
        return when (this) {
            NOTO_SANS -> "Noto Sans"
            NANUM_MYEONGJO -> "나눔 명조"
        }
    }
}