package com.blank.bookverse.presentation.ui.add_comment

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.blank.bookverse.R
import com.blank.bookverse.presentation.common.BookVerseButton
import com.blank.bookverse.presentation.common.BookVerseToolbar

@Composable
fun AddCommentScreen(
    navController: NavController,
) {
    Scaffold(
        topBar = {
            BookVerseToolbar(
                title = "나의 생각 기록",
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            painter = painterResource(R.drawable.ic_back_arrow),
                            contentDescription = null
                        )
                    }
                }
            )
        }
    ) { paddingValues ->
        AddCommentContent(
            modifier = Modifier.padding(paddingValues)
        )
    }
}

@Composable
fun AddCommentContent(
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxSize(),
        verticalArrangement = Arrangement.SpaceBetween,
    ) {
        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxSize()
        ) {
            VerticalDivider(
                modifier = Modifier
                    .padding(start = 42.dp)
                    .fillMaxHeight()
            )
            VerticalDivider(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(end = 42.dp)
                    .fillMaxHeight()
            )
        }

        BookVerseButton(
            modifier = Modifier.fillMaxWidth().height(52.dp),
            text = "기록하기",
            onClick = {},
            backgroundColor = Color.Black,
            textColor = Color.White,
        )
    }
}

@Preview(showBackground = true)
@Composable
fun AddCommentContentPreview() {
    AddCommentContent()
}