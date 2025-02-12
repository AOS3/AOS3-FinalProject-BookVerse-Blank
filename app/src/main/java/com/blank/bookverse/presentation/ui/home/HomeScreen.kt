package com.blank.bookverse.presentation.ui.home

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
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
import com.blank.bookverse.data.HomeQuote
import com.blank.bookverse.presentation.common.BookVerseToolbar
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
                is HomeEffect.NavigateToBookDetail -> {}
            }
        }
    }

    HomeContent(
        uiState = uiState,
        modifier = modifier
    )
}

@Composable
fun HomeContent(
    uiState: HomeUiState,
    modifier: Modifier = Modifier,
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
                    Text(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp),
                        text = "내 글귀",
                        fontSize = 18.sp,
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    LazyRow(
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        items(
                            items = uiState.homeQuoteList,
                            key = { it.bookTitle }
                        ) { quote ->
                            HomeQuoteItem(
                                quote = quote,
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
        HorizontalDivider()
        Text(
            text = uiState.recommendationContent.quote,
            modifier = Modifier
                .align(CenterHorizontally)
                .padding(vertical = 10.dp),
            textAlign = TextAlign.Center,
            fontSize = 16.sp
        )

        Text(
            text = "- ${uiState.recommendationContent.bookTitle} -",
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 4.dp),
            textAlign = TextAlign.Center,
            fontSize = 14.sp
        )
        HorizontalDivider()
    }
}

@Composable
fun HomeQuoteItem(
    quote: HomeQuote,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(start = 16.dp, end = 16.dp)
    ) {
        CoilImage(
            imageModel = { quote.bookCover },
            modifier = Modifier
                .fillMaxWidth()
                .height(420.dp)
                .shadow(
                    elevation = 3.dp,
                    shape = RoundedCornerShape(16.dp),
                    spotColor = Color.Black.copy(alpha = 0.2f)
                )
                .clip(RoundedCornerShape(16.dp))
                .border(0.1.dp, Color.LightGray, RoundedCornerShape(16.dp))
                .background(Color.LightGray),
            imageOptions = ImageOptions(
                contentScale = ContentScale.Crop,
                alignment = Alignment.Center
            ),
        )
        Spacer(modifier = Modifier.height(8.dp))
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 4.dp)
        ) {
            Text(
                text = quote.bookTitle,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
                lineHeight = 16.sp
            )
            Text(
                text = "남긴 글귀 ${quote.quoteCount}개",
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
    )
}

@Preview
@Composable
fun HomeQuoteItemPreview() {
    HomeQuoteItem(
        quote = HomeQuote(
            bookTitle = "광인",
            quoteCount = 3,
            bookCover = "https://contents.kyobobook.co.kr/sih/fit-in/400x0/pdt/9788937454677.jpg"
        ),
    )
}