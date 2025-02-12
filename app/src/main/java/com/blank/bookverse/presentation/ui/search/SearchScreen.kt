package com.blank.bookverse.presentation.ui.search

import android.R.attr.onClick
import android.util.Log
import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.ScrollableState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.FlowRowOverflow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonColors
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Outline
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.blank.bookverse.presentation.common.SearchBar
import com.blank.bookverse.R
import com.kakao.sdk.friend.m.c
import com.skydoves.landscapist.components.imageComponent
import timber.log.Timber

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun SearchScreen(
    navController:NavHostController,
    //viewModel: SearchViewModel = hiltViewModel()
) {
    val text = remember { mutableStateOf("") }
    val list = List(100){ "" }
    Scaffold {
        it
            Column {
                SearchBar(
                    searchValue = text,
                    composableContent = {
                        FlowRow(
                            modifier = Modifier.verticalScroll(rememberScrollState()),
                            maxItemsInEachRow = 3,
                        ) {
                            repeat(list.size){
                                Card(
                                    modifier = Modifier
                                        .padding(start = 8.dp, top = 5.dp, bottom = 5.dp, end = 8.dp)
                                ) {
                                    Text(
                                        text = "text",
                                        fontSize = 18.sp
                                    )
                                }
                            }
                        }
                    },
                    contentComposable = {
                        LazyColumn{
                            items(list.size){
                                BookCard()
                            }
                        }
                    },
                    searchResultComposable = {
                        LazyColumn{
                            items(list.size){
                                BookCard()
                            }
                        }
                    },
                    trailingIcon = Icons.Default.Search,
                    trailingIconOnClick = {

                    },
                    moreIcon = {it->
                        ImageVector.vectorResource(R.drawable.ic_crop_free_24px)
                    },
                    moreIconOnClick = {it->

                    }
                )
            }
        Timber.tag("st").d(text.value)
    }

}

@Composable
fun BookCard(){
    Card(
        modifier = Modifier.height(160.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.Transparent
        )
    ) {
        Box(
            modifier = Modifier.padding(10.dp).fillMaxWidth()
        ) {
            Row(
                modifier = Modifier.padding(start = 10.dp).fillMaxHeight()
            ){
                Box(
                    modifier = Modifier.size(101.dp,141.dp).background(Color.Gray)
                ) {

                }

                Column(
                    modifier = Modifier.padding(start = 10.dp)
                ) {
                    Text(
                        text = "한강 스페셜 에디션(작별하지 않는다)",
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Text(
                        text = "문학동네 | 2024년 12월",
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }
            Row(
                modifier = Modifier
                    .size(50.dp,44.dp)
                    .padding(end = 5.dp)
                    .align(Alignment.BottomEnd)
                    .shadow(3.dp, shape = CircleShape)
            ) {
                IconButton(
                    modifier = Modifier.offset((0).dp,(-0.7).dp),
                    colors = IconButtonDefaults.iconButtonColors(
                        containerColor = Color.White
                    ),
                    onClick = {

                    }
                ) {
                    Icon(
                        imageVector = ImageVector.vectorResource(R.drawable.ic_quill_pen_14px),
                        contentDescription = null,
                        modifier = Modifier.size(24.dp)
                    )
                }
            }

        }

    }
    HorizontalDivider()
}