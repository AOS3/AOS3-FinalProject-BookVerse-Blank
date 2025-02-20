package com.blank.bookverse.presentation.ui.quote_detail

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.blank.bookverse.R
import com.blank.bookverse.presentation.common.BookVerseToolbar
import com.blank.bookverse.presentation.model.QuoteDetailUiModel
import com.blank.bookverse.presentation.util.toFormattedDateString
import com.skydoves.landscapist.ImageOptions
import com.skydoves.landscapist.coil.CoilImage

@Composable
fun QuoteDetailScreen(
    navController: NavController,
    viewModel: QuoteDetailViewModel = hiltViewModel(),
) {
    val uiState by viewModel.quoteDetailUiState.collectAsStateWithLifecycle()

    Scaffold(
        topBar = {
            BookVerseToolbar(
                title = "",
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            painter = painterResource(R.drawable.ic_back_arrow),
                            contentDescription = "뒤로가기"
                        )
                    }
                },
                actions = {
                    IconButton(onClick = { /* 삭제 동작 */ }) {
                        Icon(
                            painter = painterResource(R.drawable.ic_delete),
                            contentDescription = "삭제"
                        )
                    }
                    IconButton(onClick = { /* 수정 동작 */ }) {
                        Icon(
                            painter = painterResource(R.drawable.ic_edit),
                            contentDescription = "수정"
                        )
                    }
                }
            )
        }
    ) { paddingValues ->
        QuoteDetailContent(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            uiState = uiState
        )
    }
}

@Composable
fun QuoteDetailContent(
    modifier: Modifier = Modifier,
    uiState: QuoteDetailUiState,
) {
    Box(modifier = modifier.fillMaxSize()) {
        if (uiState.isLoading) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        } else {
            LazyColumn(modifier = Modifier.fillMaxSize()) {
                item {
                    HorizontalDivider()
                    CoilImage(
                        modifier = Modifier
                            .padding(horizontal = 42.dp)
                            .fillMaxWidth()
                            .height(220.dp)
                            .background(Color.Gray),
                        imageModel = { uiState.quoteDetail?.photoUrl },
                        imageOptions = ImageOptions(
                            contentScale = ContentScale.Crop,
                        )
                    )

                    Spacer(modifier = Modifier.height(18.dp))
                    Text(
                        text = uiState.quoteDetail?.quoteContent ?: "",
                        modifier = Modifier
                            .padding(horizontal = 54.dp)
                    )
                    Spacer(modifier = Modifier.height(18.dp))
                    HorizontalDivider()

                    Text(
                        text = "나의 생각",
                        modifier = Modifier
                            .padding(vertical = 8.dp, horizontal = 42.dp)
                            .padding(start = 6.dp)
                    )
                    HorizontalDivider()
                }

                items(uiState.quoteDetail?.comments ?: emptyList()) { comment ->
                    QuoteCommentItem(comment = comment)
                }
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
}

@Composable
fun QuoteCommentItem(
    comment: QuoteDetailUiModel.CommentItem,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = Modifier.fillMaxWidth()
    ) {
        Spacer(modifier = Modifier.height(10.dp))
        Text(
            text = comment.commentContent,
            textAlign = TextAlign.Center,
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.CenterHorizontally)
                .padding(horizontal = 54.dp)
        )

        Spacer(modifier = Modifier.height(22.dp))
        Text(
            text = comment.createdAt.toFormattedDateString(),
            textAlign = TextAlign.Center,
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.CenterHorizontally)
                .padding(horizontal = 16.dp)
        )
        Spacer(modifier = Modifier.height(22.dp))

        HorizontalDivider()
    }
}

@Preview(showBackground = true)
@Composable
fun QuoteDetailScreenPreview() {
    QuoteDetailContent(
        uiState = QuoteDetailUiState()
    )
}