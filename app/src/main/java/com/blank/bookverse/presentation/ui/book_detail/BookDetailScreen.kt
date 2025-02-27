package com.blank.bookverse.presentation.ui.book_detail

import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.blank.bookverse.R
import com.blank.bookverse.presentation.common.BookVerseToolbar
import com.blank.bookverse.presentation.common.BookmarkButton
import com.blank.bookverse.presentation.model.BookDetailUiModel
import com.blank.bookverse.presentation.navigation.MainNavItem
import com.blank.bookverse.presentation.util.toFormattedDateString
import com.skydoves.landscapist.coil.CoilImage

@Composable
fun BookDetailScreen(
    navController: NavController,
    modifier: Modifier = Modifier,
    viewModel: BookDetailViewModel = hiltViewModel(),
) {
    val uiState by viewModel.bookDetailUiState.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        viewModel.bookDetailEffect.collect { effect ->
            when (effect) {
                is BookDetailEffect.NavigateToQuoteDetail -> {
                    navController.navigate(MainNavItem.QuoteDetail.createRoute(effect.quoteDocId))
                }
            }
        }
    }

    LaunchedEffect(navController.currentBackStackEntry) {
        viewModel.loadBookDetail()
    }

    Scaffold(
        topBar = {
            BookVerseToolbar(
                title = uiState.bookDetail?.bookInfo?.bookTitle ?: "북버스",
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            painter = painterResource(R.drawable.ic_back_arrow),
                            contentDescription = "뒤로가기"
                        )
                    }
                }
            )
        }
    ) { paddingValues ->
        BookDetailContent(
            uiState = uiState,
            modifier = modifier
                .fillMaxSize()
                .padding(paddingValues),
            onNavigateToQuoteDetail = viewModel::navigateToQuoteDetail,
            onBookmarkClick = viewModel::updateBookmark,
        )
    }
}

@Composable
fun BookDetailContent(
    uiState: BookDetailUiState,
    modifier: Modifier = Modifier,
    onNavigateToQuoteDetail: (String) -> Unit = {},
    onBookmarkClick: (String, Boolean) -> Unit = { _, _ -> },
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
                    Spacer(modifier = Modifier.height(22.dp))

                    HorizontalDivider(
                        modifier = Modifier
                            .border(0.5.dp, Color.LightGray)
                    )

                    CoilImage(
                        modifier = Modifier
                            .padding(horizontal = 42.dp)
                            .fillMaxWidth()
                            .height(520.dp),
                        imageModel = { uiState.bookDetail?.bookInfo?.bookCover },
                    )

                    HorizontalDivider(
                        modifier = Modifier
                            .border(0.5.dp, Color.LightGray)
                    )

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
                            text = uiState.bookDetail?.bookInfo?.bookTitle ?: "",
                            style = MaterialTheme.typography.titleLarge.copy(
                                fontWeight = FontWeight.Bold,
                            )
                        )

                        Text(
                            modifier = Modifier
                                .padding(end = 12.dp)
                                .alignByBaseline(),
                            text = "남긴 글귀 ${uiState.bookDetail?.bookInfo?.quoteCount ?: 0}개",
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }

                    HorizontalDivider(
                        modifier = Modifier
                            .border(0.5.dp, Color.LightGray)
                    )

                }

                items(
                    items = uiState.bookDetail?.quotes ?: emptyList(),
                    key = { it.quoteDocId }
                ) { quote ->
                    BookDetailQuoteItem(
                        quote = quote,
                        onNavigateToQuoteDetail = onNavigateToQuoteDetail,
                        onBookmarkClick = onBookmarkClick
                    )
                }

                item {
                    Spacer(modifier = Modifier.height(22.dp))
                }
            }

            VerticalDivider(
                modifier = Modifier
                    .padding(start = 42.dp)
                    .border(0.5.dp, Color.LightGray)
                    .fillMaxHeight()
            )
            VerticalDivider(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(end = 42.dp)
                    .border(0.5.dp, Color.LightGray)
                    .fillMaxHeight()
            )
        }
    }
}

@Composable
fun BookDetailQuoteItem(
    quote: BookDetailUiModel.QuoteItem,
    modifier: Modifier = Modifier,
    onNavigateToQuoteDetail: (String) -> Unit = {},
    onBookmarkClick: (String, Boolean) -> Unit = { _, _ -> },
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onNavigateToQuoteDetail(quote.quoteDocId) }
    ) {
        BookmarkButton(
            modifier = Modifier
                .align(Alignment.TopEnd),
            isBookmark = quote.isBookmark,
            onBookmarkClick = {
                onBookmarkClick(quote.quoteDocId, !quote.isBookmark)
            }
        )

        Column(
            modifier = Modifier.fillMaxWidth()
        ) {
            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = quote.quoteContent,
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.CenterHorizontally)
                    .padding(horizontal = 58.dp)
            )

            Spacer(modifier = Modifier.height(22.dp))
            Text(
                text = quote.createdAt.toFormattedDateString(),
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.labelSmall,
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.CenterHorizontally)
                    .padding(horizontal = 16.dp)
            )
            Spacer(modifier = Modifier.height(22.dp))

            HorizontalDivider()
        }
    }
}

@Preview(showBackground = true)
@Composable
fun BookDetailScreenPreview() {
    BookDetailContent(
        uiState = BookDetailUiState()
    )
}

@Preview(showBackground = true)
@Composable
fun BookDetailQuoteItemPreview() {
    BookDetailQuoteItem(
        quote = BookDetailUiModel.QuoteItem(
            quoteDocId = "",
            quoteContent = "아름답다는 건 그런 거지. 뭘 숨길 필요가 없는 거, 똑같이 해도 그냥 아름다운 거.",
            photoUrl = "",
            createdAt = System.currentTimeMillis()
        )
    )
}