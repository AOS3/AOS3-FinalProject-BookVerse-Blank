package com.blank.bookverse.presentation.ui.book_detail

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.blank.bookverse.R
import com.blank.bookverse.presentation.common.BookVerseToolbar
import com.skydoves.landscapist.coil.CoilImage

@Composable
fun BookDetailScreen(
    navController: NavController,
    modifier: Modifier = Modifier,
    viewModel: BookDetailViewModel = hiltViewModel(),
) {
    val uiState by viewModel.bookDetailUiState.collectAsStateWithLifecycle()

    Scaffold(
        topBar = {
            BookVerseToolbar(
                title = "북버스",
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            painter = painterResource(R.drawable.ic_back_arrow),
                            contentDescription = "뒤로가기"
                        )
                    }
                },
            )
        }
    ) { paddingValues ->
        BookDetailContent(
            uiState = uiState,
            modifier = modifier
                .fillMaxSize()
                .padding(paddingValues)
        )
    }
}

@Composable
fun BookDetailContent(
    uiState: BookDetailUiState,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier.fillMaxSize()
    ) {
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            Spacer(modifier = Modifier.height(22.dp))
            HorizontalDivider()

            CoilImage(
                modifier = Modifier
                    .padding(horizontal = 42.dp)
                    .fillMaxWidth()
                    .height(520.dp),
                imageModel = { uiState.quote?.bookCover },
            )

            HorizontalDivider()

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 42.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Bottom,
            ) {
                Text(
                    modifier = Modifier
                        .padding(start = 12.dp, top = 8.dp, bottom = 8.dp)
                        .alignByBaseline(),
                    text = "${uiState.quote?.bookTitle}",
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontWeight = FontWeight.Bold,
                    )
                )

                Text(
                    modifier = Modifier
                        .padding(end = 12.dp)
                        .alignByBaseline(),
                    text = "남긴 글귀 ${uiState.quote?.quoteCount}개",
                    style = MaterialTheme.typography.bodyMedium
                )
            }
            HorizontalDivider()

        }

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
}

@Preview(showBackground = true)
@Composable
fun BookDetailScreenPreview() {
    BookDetailContent(
        uiState = BookDetailUiState()
    )
}