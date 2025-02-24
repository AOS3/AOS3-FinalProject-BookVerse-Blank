package com.blank.bookverse.presentation.ui.quote_detail

import androidx.compose.foundation.background
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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
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
import com.blank.bookverse.presentation.common.BookmarkButton
import com.blank.bookverse.presentation.model.QuoteDetailUiModel
import com.blank.bookverse.presentation.navigation.MainNavItem
import com.blank.bookverse.presentation.util.toFormattedDateString
import com.skydoves.landscapist.ImageOptions
import com.skydoves.landscapist.coil.CoilImage

@Composable
fun QuoteDetailScreen(
    navController: NavController,
    viewModel: QuoteDetailViewModel = hiltViewModel(),
) {
    val uiState by viewModel.quoteDetailUiState.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        viewModel.quoteDetailEffect.collect { effect ->
            when (effect) {
                is QuoteDetailEffect.NavigateBack -> navController.popBackStack()
                is QuoteDetailEffect.NavigateToAddComment -> {
                    navController.navigate(MainNavItem.AddComment.createRoute(effect.quoteDocId))
                }
            }
        }
    }

    LaunchedEffect(navController.currentBackStackEntry) {
        viewModel.loadQuoteDetail()
    }

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
                    IconButton(onClick = { viewModel.deleteQuote() }) {
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
                    BookmarkButton(
                        isBookmark = uiState.quoteDetail?.isBookmark ?: false,
                        onBookmarkClick = {
                            viewModel.updateBookmark(!uiState.quoteDetail?.isBookmark!!)
                        }
                    )
                }
            )
        }
    ) { paddingValues ->
        QuoteDetailContent(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            uiState = uiState,
            onCommentDelete = viewModel::deleteComment,
            onNavigateToAddComment = { viewModel.navigateToAddComment() }
        )
    }
}

@Composable
fun QuoteDetailContent(
    modifier: Modifier = Modifier,
    uiState: QuoteDetailUiState,
    onCommentDelete: (String) -> Unit = {},
    onNavigateToAddComment: () -> Unit = {},
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

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 42.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Text(
                            text = "나의 생각",
                            modifier = Modifier.padding(start = 6.dp)
                        )

                        CommentAddButton(
                            modifier = Modifier.padding(end = 6.dp),
                            onNavigateToAddComment = { onNavigateToAddComment() },
                        )
                    }

                    HorizontalDivider()
                }

                items(uiState.quoteDetail?.comments ?: emptyList()) { comment ->
                    QuoteCommentItem(
                        comment = comment,
                        onCommentDelete = onCommentDelete,
                        )
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
    onCommentDelete: (String) -> Unit = {},
) {

    Box(
        modifier = Modifier.fillMaxWidth()
    ) {
        CommentDeleteButton(
            modifier = Modifier
                .align(Alignment.TopEnd),
            onCommentDelete = { onCommentDelete(comment.commentDocId) }
        )

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
}

@Composable
fun CommentDeleteButton(
    modifier: Modifier = Modifier,
    onCommentDelete: () -> Unit = {},
) {
    Box(
        modifier = modifier
            .padding(5.dp)
            .size(32.dp)
            .border(0.5.dp, Color.LightGray, RoundedCornerShape(6.dp))
            .clip(RoundedCornerShape(4.dp))
            .clickable(
                onClick = { onCommentDelete() }
            ),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            painter = painterResource(R.drawable.ic_remove),
            contentDescription = "삭제",
            modifier = Modifier.padding(8.dp)
        )
    }
}

@Composable
fun CommentAddButton(
    modifier: Modifier = Modifier,
    onNavigateToAddComment: () -> Unit = {},
) {
    Box(
        modifier = modifier
            .padding(5.dp)
            .size(32.dp)
            .border(0.5.dp, Color.LightGray, RoundedCornerShape(6.dp))
            .clip(RoundedCornerShape(4.dp))
            .clickable(
                onClick = { onNavigateToAddComment() }
            ),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            painter = painterResource(R.drawable.ic_add),
            contentDescription = "작성",
            modifier = Modifier.padding(8.dp)
        )
    }
}

@Composable
@Preview(showBackground = true)
fun CommentDeleteButtonPreview() {
    CommentDeleteButton()
}

@Composable
@Preview(showBackground = true)
fun CommentAddButtonPreview() {
    CommentAddButton()
}

@Preview(showBackground = true)
@Composable
fun QuoteDetailScreenPreview() {
    QuoteDetailContent(
        uiState = QuoteDetailUiState(),
    )
}