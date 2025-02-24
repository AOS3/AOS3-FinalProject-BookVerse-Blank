package com.blank.bookverse.presentation.theme

import androidx.compose.ui.text.font.FontFamily


enum class FontType(val fontFamily: FontFamily) {
    NOTO_SANS(notoSansFamily),
    NANUM_MYEONGJO(nanumMyeongjoFamily),
    BINGGRAE(binggraeFamily),
    BINGGRAE_TOGETHER(binggraetogetherFamily),
    KIMJUNGCHUL(kimjungchulFamily);

    // 화면에 표시할 이름
    fun displayName(): String {
        return when (this) {
            NOTO_SANS -> "Noto Sans"
            NANUM_MYEONGJO -> "나눔명조체"
            BINGGRAE -> "빙그레체"
            BINGGRAE_TOGETHER -> "투게더체"
            KIMJUNGCHUL -> "김정철체"
        }
    }
}