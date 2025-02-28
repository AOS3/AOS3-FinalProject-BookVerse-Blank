package com.blank.bookverse.presentation.ui.search

import android.R.attr.contentDescription
import android.R.attr.moreIcon
import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.LazyColumn
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
import androidx.compose.material3.IconButtonDefaults.iconButtonColors
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.DisposableEffectResult
import androidx.compose.runtime.LaunchedEffect
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.LinkAnnotation
import androidx.compose.ui.text.LinkAnnotation.Url
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.net.toUri
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import com.blank.bookverse.presentation.common.SearchBar
import com.blank.bookverse.R
import com.blank.bookverse.data.api.search.DocumentsObject
import com.blank.bookverse.data.local.MemberDatabase
import com.blank.bookverse.presentation.navigation.CameraNavItem
import com.blank.bookverse.presentation.navigation.MainNavItem
import com.google.firebase.auth.FirebaseAuth
import com.kakao.sdk.friend.m.c
import com.skydoves.landscapist.components.imageComponent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import timber.log.Timber

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun SearchScreen(
    navController:NavHostController,
    viewModel: SearchViewModel = hiltViewModel()
) {

    val context = LocalContext.current
    LaunchedEffect(Unit) {
        viewModel.setReSearchList(context)
    }
    DisposableEffect(Unit) {
        onDispose {
            viewModel.updateReSearchList(context)
        }
    }

    Scaffold {
        it
            Column{
                SearchBar(
                    searchValue = viewModel.searchText,
                    composableContent = {
                        FlowRow(
                            modifier = Modifier.padding(
                                start = 20.dp, end = 20.dp,
                                top = 10.dp, bottom = 10.dp
                            ).verticalScroll(rememberScrollState()),
                            maxItemsInEachRow = 10,
                        ) {
                            repeat(viewModel.getReSearchList().size){
                                SearchCard(
                                    viewModel.getReSearchList(it),
                                    onClickable = {
                                        viewModel.searchText.value = viewModel.getReSearchList(it)
                                    },
                                    clear = {
                                        viewModel.deleteReSearchList(it,context)
                                    }
                                )
                            }
                        }
                    },
                    contentComposable = {
                        LazyColumn{
                            items(viewModel.getResultList().size){
                                BookCard(
                                    viewModel.getResultList(it),
                                    writeClick = {
                                        viewModel.writeScreen(it,navController)
                                    }
                                )
                            }
                        }
                    },
                    searchResultComposable = {
                        LazyColumn{
                            items(viewModel.getResultList().size){
                                BookCard(
                                    viewModel.getResultList(it),
                                    Color(0xFFF5F3F6),
                                    writeClick = {
                                        viewModel.writeScreen(it,navController)
                                    }
                                )
                            }
                        }
                    },
                    trailingIcon = Icons.Default.Search,
                    trailingIconOnClick = {
                        viewModel.onSearch(context)
                    },
                    moreIcon = {it->
                        ImageVector.vectorResource(R.drawable.ic_crop_free_24px)
                    },
                    moreIconOnClick = {it->
                        //navController.navigate(CameraNavItem.TakeBook.route)
                    },
                    onSearch = {
                        viewModel.onSearch(context)
                    },
                    onQueryChange = {
                        viewModel.onQueryChange()
                    },
                    contentComposableNotEnabled = viewModel.getResultListNotEmpty()
                )
            }
        Timber.tag("st").d(viewModel.searchText.value)
    }

}

@Composable
fun BookCard(
    document: DocumentsObject,
    iconButtonColors: Color = Color.White,
    writeClick:()-> Unit= {}
){
    val date = remember { mutableStateOf(document.datetime.split('-')) }
    val dateYY = date.value.first()
    val dateMM = date.value[1]
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
                AsyncImage(
                    document.thumbnail,
                    contentDescription = null,
                    modifier = Modifier.size(101.dp,141.dp).background(Color.LightGray)
                )

                Column(
                    modifier = Modifier.padding(start = 10.dp)
                ) {
                    Text(
                        text = document.title,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Text(
                        text = "${document.publisher} | ${dateYY}년 ${dateMM}월",
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
                        containerColor = iconButtonColors
                    ),
                    onClick = {
                        writeClick()
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

@Composable
fun SearchCard(
    think: String,
    onClickable: (()-> Unit)? = null,
    clear: (()-> Unit)? = null,
    fontSize:Int? = null
){
    Card (
        modifier = Modifier
            .wrapContentWidth()
            .padding(start = 8.dp, top = 5.dp, bottom = 5.dp, end = 8.dp),
        colors = CardDefaults.cardColors(
            Color(0xFFD9D9D9)
        ),
        shape = RoundedCornerShape(50.dp)
    ){
        Row(
            modifier = if (onClickable == null)
                Modifier
            else {
                Modifier
                    .clickable(onClick = {
                        onClickable()
                    })
            },
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Start
        ) {

            Text(modifier = Modifier.padding(start = 5.dp, bottom = 4.dp, top = 4.dp, end = 3.dp),
                text = think,
                fontSize = if (fontSize == null)TextUnit.Unspecified
                else fontSize.sp
            )
            if (clear != null) {
                Card(
                    modifier = Modifier.padding(end = 2.dp)
                        .clip(RoundedCornerShape(20.dp))
                        .clickable(
                            onClick = {
                                clear()
                            }
                        ),
                    colors = CardDefaults.cardColors(
                        containerColor = Color.Transparent
                    ),
                ) {
                    Icon(
                        imageVector = Icons.Filled.Clear,
                        contentDescription = null
                    )
                }
            }
        }

    }
}