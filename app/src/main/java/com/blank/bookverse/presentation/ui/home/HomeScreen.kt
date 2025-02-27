package com.blank.bookverse.presentation.ui.home

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Alignment.Companion.CenterHorizontally
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.blank.bookverse.presentation.common.BookVerseToolbar
import com.blank.bookverse.presentation.model.HomeBookUiModel
import com.blank.bookverse.presentation.navigation.MainNavItem
import com.skydoves.landscapist.ImageOptions
import com.skydoves.landscapist.coil.CoilImage
import kotlinx.coroutines.flow.collectLatest

@Composable
fun HomeScreen(
    navController: NavController,
    modifier: Modifier = Modifier,
    viewModel: HomeViewModel = hiltViewModel(),
) {
    val uiState by viewModel.homeUiState.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        viewModel.homeEffect.collectLatest { effect ->
            when (effect) {
                is HomeEffect.NavigateToBookDetail -> {
                    navController.navigate(MainNavItem.BookDetail.createRoute(effect.bookDocId))
                }
            }
        }
    }

    LaunchedEffect(navController.currentBackStackEntry) {
        viewModel.loadBooks()
    }


    HomeContent(
        uiState = uiState,
        modifier = modifier,
        onNavigateToMore = {
            navController.navigate(MainNavItem.MoreQuote.route)
        },
        onNavigateToDetail = viewModel::navigateToBookDetail
    )
}

@Composable
fun HomeContent(
    uiState: HomeUiState,
    modifier: Modifier = Modifier,
    onNavigateToMore: () -> Unit,
    onNavigateToDetail: (String) -> Unit = {},
) {
    Scaffold(
        topBar = {
            BookVerseToolbar(
                title = "북버스",
            )
        }
    ) { paddingValues ->
        Surface(
            modifier = modifier
                .fillMaxSize()
                .padding(paddingValues),
            color = MaterialTheme.colorScheme.background
        ) {
            if (uiState.isLoading) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            } else {
                Column {
                    RecommendationCard(uiState)
                    Spacer(modifier = Modifier.height(16.dp))
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "내 글귀",
                            style = MaterialTheme.typography.bodyLarge.copy(
                                fontWeight = FontWeight.Medium
                            ),
                            modifier = Modifier
                                .weight(1f)
                                .alignByBaseline()
                        )
                        Text(
                            text = "전체보기 >",
                            style = MaterialTheme.typography.bodyLarge.copy(
                                fontWeight = FontWeight.Medium,
                                color = MaterialTheme.colorScheme.primary
                            ),
                            modifier = Modifier
                                .clickable { onNavigateToMore() }
                                .padding(start = 8.dp)
                                .alignByBaseline()
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))
                    LazyRow(
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        items(
                            items = uiState.books,
                            key = { it.bookDocId }
                        ) { book ->
                            HomeBookItem(
                                book = book,
                                onNavigateToDetail = { onNavigateToDetail(book.bookDocId) }
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun RecommendationCard(uiState: HomeUiState) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
    ) {
        HorizontalDivider(
            thickness = 0.7.dp,
            color = Color.LightGray,
        )
        Text(
            text = uiState.recommendationContent.quote,
            modifier = Modifier
                .align(CenterHorizontally)
                .padding(vertical = 10.dp),
            textAlign = TextAlign.Center,
            style = MaterialTheme.typography.bodyLarge.copy(
                fontWeight = FontWeight.Light,
            )
        )

        Text(
            text = "- ${uiState.recommendationContent.bookTitle} -",
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 4.dp),
            textAlign = TextAlign.Center,
            style = MaterialTheme.typography.bodyMedium.copy(
                fontWeight = FontWeight.Light,
            )
        )
        HorizontalDivider(
            thickness = 0.7.dp,
            color = Color.LightGray,
        )
    }
}

@Composable
fun HomeBookItem(
    book: HomeBookUiModel,
    modifier: Modifier = Modifier,
    onNavigateToDetail: () -> Unit = {}
) {
    Column(
        modifier = modifier
            .padding(start = 16.dp, end = 16.dp)
    ) {
        CoilImage(
            imageModel = { book.bookCover },
            modifier = Modifier
                .width(280.dp)
                .height(420.dp)
                .shadow(
                    elevation = 3.dp,
                    shape = RoundedCornerShape(16.dp),
                    spotColor = Color.Black.copy(alpha = 0.2f)
                )
                .clip(RoundedCornerShape(16.dp))
                .border(0.1.dp, Color.LightGray, RoundedCornerShape(16.dp))
                .background(Color.LightGray)
                .clickable { onNavigateToDetail() },
            imageOptions = ImageOptions(
                contentScale = ContentScale.Crop,
                alignment = Alignment.Center
            ),
        )
        Spacer(modifier = Modifier.height(8.dp))
        Column(
            modifier = Modifier
                .width(280.dp)
                .padding(horizontal = 4.dp)
        ) {
            Text(
                text = book.bookTitle,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
                lineHeight = 16.sp
            )
            Text(
                text = "남긴 글귀 ${book.quoteCount}개",
                fontSize = 12.sp,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                lineHeight = 16.sp
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun HomeScreenPreview() {
    HomeContent(
        uiState = HomeUiState(),
        onNavigateToMore = { },
    )
}

@Preview(showBackground = true)
@Composable
fun HomeBookItemPreview() {
    HomeBookItem(
        book = HomeBookUiModel(
            bookDocId = "1",
            bookTitle = "어린 왕자",
            bookCover = "",
            quoteCount = 3
        )
    )
}