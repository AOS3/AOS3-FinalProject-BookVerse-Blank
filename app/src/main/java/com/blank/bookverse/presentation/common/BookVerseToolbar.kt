package com.blank.bookverse.presentation.common

import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BookVerseToolbar(
    title: String,
    navigationIcon: @Composable (() -> Unit)? = null,
    actions: @Composable (RowScope.() -> Unit)? = null,
) {
    TopAppBar(
        title = {
            Text(
                text = title,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        },
        navigationIcon = {
            navigationIcon?.invoke()
        },
        actions = {
            actions?.invoke(this)
        },
        modifier = Modifier.fillMaxWidth()
    )
}


/*
* 활용 방법
* title에 툴바 타이틀을 넣어준다. 타이틀은 필수값
* 네비게이션 아이콘, 메뉴는 넣어주지 않으면 null (선택)
* 네비게이션 아이콘, 메뉴는 아이콘 버튼을 넣어준다.
* 클릭 이벤트도 호출할 때 보낸다.

*             BookVerseToolbar(
                title = "북버스",
                navigationIcon = {
                    IconButton(onClick = { /* 뒤로가기 동작 */ }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "뒤로가기")
                    }
                },
                actions = {
                    IconButton(onClick = { /* 메뉴 동작 */ }) {
                        Icon(Icons.Default.Menu, contentDescription = "메뉴")
                    }
                }
            )
*
*
* */