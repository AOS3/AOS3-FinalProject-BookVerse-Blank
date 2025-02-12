package com.blank.bookverse.presentation.common

import android.R.attr.text
import androidx.compose.animation.Crossfade
import androidx.compose.animation.core.EaseIn
import androidx.compose.animation.core.FastOutLinearInEasing
import androidx.compose.animation.core.animateIntOffsetAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuDefaults.TrailingIcon
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.SearchBar
import androidx.compose.material3.SearchBarDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import com.blank.bookverse.R
import timber.log.Timber

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchBar(
    searchValue:MutableState<String>,
    placeholder:String = "검색어를 입력해주세요",
    trailingList:List<Pair<ImageVector?,()->Unit>> = listOf(),
    trailingIcon: ImageVector?,
    trailingIconOnClick:(Boolean)-> Unit,
    moreIcon: @Composable ((Boolean) -> ImageVector)?,
    moreIconOnClick:(Boolean)-> Unit,
    searchResultComposable : @Composable () -> Unit,
    contentComposable: @Composable () -> Unit,
    composableContent: @Composable (List<String>) -> Unit, // 최근 검색어 전달
    onSearch: () -> Unit = {}, // 엔터키 입력 시 호출할 콜백 추가
    alignment: Alignment = Alignment.TopCenter,
) {
    var expandedValue by rememberSaveable {
        mutableStateOf(false)
    }
    val recentSearches = remember { mutableStateListOf<String>() } // 최근 검색어 상태

    val textFieldOffset by animateIntOffsetAsState(
        targetValue = if (expandedValue) {
            IntOffset.Zero
        } else {
            IntOffset(-60, 0)
        },
        label = "textFieldOffset"
    )

    val trailingIconOffset by animateIntOffsetAsState(
        targetValue = if (expandedValue) {
            IntOffset.Zero
        } else {
            IntOffset(50, 0)
        },
        label = "textFieldOffset"
    )
    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        // SearchBar
        SearchBar(
            modifier = Modifier
                .align(alignment),
            inputField = {
                SearchBarDefaults.InputField(
                    modifier = Modifier.fillMaxWidth(0.9f).offset {
                        textFieldOffset
                    },
                    placeholder = {
                        Text(text = placeholder)
                    },
                    // 키보드의 엔터키를 누르면 동작하는 부분
                    onSearch = {
                        onSearch()
                    },
                    expanded = expandedValue,
                    onExpandedChange = {
                        expandedValue = it
                        searchValue.value = ""
                    },
                    query = searchValue.value,
                    onQueryChange = {
                        searchValue.value = it
                    },
                    leadingIcon = {
                        Crossfade(
                            expandedValue,
                            label = "",
                            animationSpec = tween(120),
                        ) {
                            if (it) {
                                Card(
                                    modifier = Modifier
                                        .padding(start = 10.dp, end = 5.dp)
                                        .clip(RoundedCornerShape(5.dp))
                                ) {
                                    Icon(
                                        imageVector = ImageVector.vectorResource(R.drawable.ic_reply_24px),
                                        contentDescription = null,
                                        modifier = Modifier.clickable {
                                            expandedValue = false
                                            searchValue.value = ""
                                        }
                                    )
                                }
                            } else null
                        }

                    },
                    trailingIcon = {
                        Crossfade(
                            expandedValue,
                            label = "",
                            animationSpec = tween(120),
                            modifier = Modifier.offset {
                                trailingIconOffset
                            }
                        ) {
                            Row(
                                modifier = Modifier.padding(end = 10.dp),
                                horizontalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                TrailingIcon(
                                    expandedValue = it,
                                    expandedOnClick = {
                                        searchValue.value = ""
                                    },
                                    trailingIcon = trailingIcon,
                                    trailingIconOnClick = {
                                        trailingIconOnClick(expandedValue)
                                    }
                                )
                                if (moreIcon != null) {
                                    val moreExpandedIcon = moreIcon(true)
                                    val moreNotExpandedIcon = moreIcon(false)
                                    TrailingIcon(
                                        expandedValue = it,
                                        expandedIcon = moreExpandedIcon,
                                        expandedOnClick = {
                                            searchValue.value = ""
                                        },
                                        trailingIcon = moreNotExpandedIcon,
                                        trailingIconOnClick = {
                                            moreIconOnClick(expandedValue)
                                        }
                                    )
                                }
                            }
                        }
                    },
                    colors = TextFieldDefaults.colors(

                    )
                )
            },
            expanded = expandedValue,
            onExpandedChange = {
                expandedValue = it
            },
            colors = SearchBarDefaults.colors(

            ),
            shape = RoundedCornerShape(20.dp)
        ) {
            if (searchValue.value.isNotBlank()) {
                // 검색 결과를 구성해주는 부분
                searchResultComposable()
            } else {
                // 최근 검색어 표시 부분
                composableContent(recentSearches)

            }
        }

        // 검색바 하단의 본문 부분
        // contentComposable()
        Column(
            modifier = Modifier.padding(top = 120.dp)
        ) {
            contentComposable()
        }

    }
}


@Composable
fun TrailingIcon(
    expandedValue: Boolean,
    expandedIcon: ImageVector = Icons.Filled.Clear,
    expandedOnClick:()-> Unit = {},
    trailingIcon: ImageVector? = null,
    trailingIconOnClick: () -> Unit = {}
){
    val size:Dp = 30.dp
    if (expandedValue) {
        IconButton(
            onClick = {
                expandedOnClick()
            },
            modifier = Modifier.size(size+10.dp),
            colors = IconButtonDefaults.iconButtonColors(

            )
        ) {
            Icon(
                imageVector = expandedIcon,
                contentDescription = null,
                modifier = Modifier.size(size)
            )

        }
    } else {
        if (trailingIcon != null) {
            IconButton(
                onClick = {
                    trailingIconOnClick()
                },
                modifier = Modifier.size(size+10.dp),
                colors = IconButtonDefaults.iconButtonColors(

                )
            ) {

                Icon(
                    imageVector = trailingIcon,
                    contentDescription = null,
                    modifier = Modifier.size(size)
                )
            }
        }
    }
}
