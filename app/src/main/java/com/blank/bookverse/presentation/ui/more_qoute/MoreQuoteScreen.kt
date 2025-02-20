package com.blank.bookverse.presentation.ui.more_qoute

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.blank.bookverse.R
import com.blank.bookverse.data.model.HomeQuote
import com.blank.bookverse.presentation.common.BookVerseToolbar
import com.blank.bookverse.presentation.navigation.MainNavItem
import com.skydoves.landscapist.ImageOptions
import com.skydoves.landscapist.coil.CoilImage
import kotlinx.coroutines.flow.collectLatest

@Composable
fun MoreQuoteScreen(
    navController: NavController,
    viewModel: MoreQuoteViewModel = hiltViewModel(),
) {
    val uiState by viewModel.moreQuoteUiState.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        viewModel.moreQuoteEffect.collectLatest { effect ->
            when (effect) {
                is MoreQuoteEffect.NavigateToBookDetail -> {
                    navController.navigate(MainNavItem.BookDetail.createRoute(effect.id))
                }
            }
        }
    }

    MoreQuoteContent(
        quoteList = uiState.quoteList,
        onBackClick = { navController.popBackStack() },
        onNavigateToDetail = viewModel::navigateToBookDetail,
    )
}

@Composable
fun MoreQuoteContent(
    quoteList: List<HomeQuote>,
    onBackClick: () -> Unit,
    onNavigateToDetail: (String) -> Unit = {},
) {
    Scaffold(
        topBar = {
            BookVerseToolbar(
                title = "글귀 목록",
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            painter = painterResource(R.drawable.ic_back_arrow),
                            contentDescription = "뒤로가기",
                        )
                    }
                },
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier.padding(paddingValues)
        ) {
            LazyColumn(
                modifier = Modifier.fillMaxWidth()
            ) {
                items(
                    items = quoteList,
                    key = { quote -> "${quote.quoteDocId}" }
                ) { quote ->
                    MoreQuoteItem(
                        quote = quote,
                        onNavigateToDetail = { onNavigateToDetail(quote.quoteDocId) }
                    )
                }
            }
        }
    }
}

@Composable
fun MoreQuoteItem(
    quote: HomeQuote,
    onNavigateToDetail: () -> Unit = {},
    ) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp, horizontal = 16.dp)
            .clickable { onNavigateToDetail() },
    ) {
        CoilImage(
            imageModel = { quote.bookCover },
            modifier = Modifier
                .width(82.dp)
                .height(120.dp)
                .clip(RoundedCornerShape(16.dp))
                .border(0.1.dp, Color.LightGray, RoundedCornerShape(16.dp))
                .background(Color.LightGray),
            imageOptions = ImageOptions(
                contentScale = ContentScale.Crop,
                alignment = Alignment.Center
            ),
        )

        Spacer(modifier = Modifier.width(8.dp))

        Column(
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = quote.bookTitle,
                fontWeight = FontWeight.Bold,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.padding(bottom = 4.dp)
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text("내 글귀 ${quote.quoteCount}개")
        }
    }
}

@Preview(showBackground = true)
@Composable
fun MoreQuoteScreenPreview() {
    MoreQuoteItem(
        quote = HomeQuote(
            bookTitle = "광인",
            quoteCount = 3,
            bookCover = "https://contents.kyobobook.co.kr/sih/fit-in/400x0/pdt/9788937454677.jpg",
            quoteDocId = "",
            memberDocId = "",
        )
    )
}