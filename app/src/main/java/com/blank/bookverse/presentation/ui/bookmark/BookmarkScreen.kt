package com.blank.bookverse.presentation.ui.bookmark

import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import com.blank.bookverse.presentation.common.BookVerseToolbar
import com.blank.bookverse.presentation.common.BookmarkButton
import com.blank.bookverse.presentation.model.BookDetailUiModel
import com.blank.bookverse.presentation.model.BookmarkUiModel
import com.blank.bookverse.presentation.navigation.MainNavItem
import com.blank.bookverse.presentation.ui.book_detail.BookDetailEffect

@Composable
fun BookmarkScreen(
    navController: NavHostController,
    modifier: Modifier = Modifier,
    viewModel: BookmarkViewModel = hiltViewModel(),
) {
    val uiState by viewModel.bookmarkUiState.collectAsStateWithLifecycle()

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
        viewModel.getUserBookmarkedQuotes()
    }

    BookmarkContent(
        modifier = modifier,
        uiState = uiState,
        onNavigateToQuoteDetail = viewModel::navigateToQuoteDetail,
        onBookmarkClick = viewModel::updateBookmark,
    )

}

@Composable
fun BookmarkContent(
    modifier: Modifier = Modifier,
    uiState: BookmarkUiState,
    onNavigateToQuoteDetail: (String) -> Unit = {},
    onBookmarkClick: (String, Boolean) -> Unit = { _, _ -> },
) {
    Scaffold(
        topBar = {
            BookVerseToolbar(
                title = "책갈피",
            )
        }
    ) { paddingValues ->
        Box(
            modifier = modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            if (uiState.isLoading) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            } else {
                LazyColumn(modifier = Modifier.fillMaxSize()) {
                    items(uiState.bookmarkedQuotes) { quote ->
                        BookMarkQuoteItem(
                            quote,
                            onNavigateToQuoteDetail = onNavigateToQuoteDetail,
                            onBookmarkClick = onBookmarkClick
                        )
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
}

@Composable
fun BookMarkQuoteItem(
    quote: BookmarkUiModel,
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
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.CenterHorizontally)
                    .padding(horizontal = 58.dp)
            )

            Spacer(modifier = Modifier.height(22.dp))
            Text(
                text = quote.createdAt,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.CenterHorizontally)
                    .padding(horizontal = 16.dp)
            )
            Spacer(modifier = Modifier.height(22.dp))

            HorizontalDivider(
                modifier = Modifier
                    .fillMaxWidth()
                    .border(0.5.dp, Color.LightGray)
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun BookDetailQuoteItemPreview() {
    com.blank.bookverse.presentation.ui.book_detail.BookDetailQuoteItem(
        quote = BookDetailUiModel.QuoteItem(
            quoteDocId = "",
            quoteContent = "아름답다는 건 그런 거지. 뭘 숨길 필요가 없는 거, 똑같이 해도 그냥 아름다운 거.",
            photoUrl = "",
            createdAt = System.currentTimeMillis()
        )
    )
}