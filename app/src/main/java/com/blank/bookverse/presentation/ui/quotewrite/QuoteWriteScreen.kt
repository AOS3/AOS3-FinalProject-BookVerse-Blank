package com.blank.bookverse.presentation.ui.quotewrite

import android.R.attr.onClick
import android.R.attr.text
import androidx.compose.ui.platform.LocalDensity
import android.annotation.SuppressLint
import android.util.Log
import android.widget.GridView
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.ime
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material.icons.outlined.Warning
import androidx.compose.material.icons.rounded.Warning
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CardElevation
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.setValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.boundsInRoot
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.blank.bookverse.R
import com.blank.bookverse.presentation.common.BookVerseBottomSheet
import com.blank.bookverse.presentation.common.BookVerseButton
import com.blank.bookverse.presentation.common.BookVerseTextField
import com.blank.bookverse.presentation.common.BookVerseToolbar
import com.kakao.sdk.friend.l.b
import kotlinx.coroutines.launch

@OptIn(ExperimentalLayoutApi::class)
@SuppressLint("UnrememberedMutableState")
@Composable
fun QuoteWriteScreen(
    navController:NavHostController,
    viewModel: QuoteWriteViewModel = hiltViewModel()
) {
    val density = LocalDensity.current
    // FocusManager 가져오기
    val focusManager = LocalFocusManager.current
    val imeHeight = WindowInsets.ime.getBottom(density) // 키보드 높이
    // 전체 화면 높이
    val screenHeight = with(density) { LocalConfiguration.current.screenHeightDp.dp.toPx() }
    val screenWidth = with(density) { LocalConfiguration.current.screenWidthDp.dp.toPx() }
    val boolean = mutableStateOf(true)
    val scrollState = rememberScrollState()
    val coroutineScope = rememberCoroutineScope()
    Scaffold(
        modifier = Modifier.clickable(
            indication = null,
            interactionSource = remember{ MutableInteractionSource() },
            onClick = {focusManager.clearFocus()}
        ),
        topBar = {
            BookVerseToolbar(

                title = "글귀 작성",
                navigationIcon = {
                    IconButton(
                        onClick = {

                        }
                    ) {
                        Icon(
                            imageVector = ImageVector.vectorResource(R.drawable.ic_reply_24px),
                            contentDescription = null,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                },
                actions = {
                    IconButton(
                        onClick = {

                        }
                    ) {
                        Icon(
                            imageVector = ImageVector.vectorResource(R.drawable.ic_photo_camera_24px),
                            contentDescription = null,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                }
            )
        }
    ) {
            Column(
                modifier = Modifier
                    .padding(it)
                    .fillMaxSize()
                    .verticalScroll(scrollState)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp)
                        .background(Color.LightGray)
                ) {

                }
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    LazyRow(
                        modifier = Modifier
                            .padding(top = 5.dp, bottom = 5.dp)
                            .fillMaxWidth(0.85f),
                        contentPadding = PaddingValues(start = 10.dp, end = 10.dp)
                    ) {
                        items(viewModel.thinkList.size){it->
                            ChipCard(viewModel.thinkList[it])
                            Spacer(Modifier.width(10.dp))
                        }
                    }

                    IconButton(
                        modifier = Modifier.padding(3.dp),
                        onClick = {
                            viewModel.bottomSheetOpen()
                        }
                    ) {

                        Icon(
                            imageVector = Icons.Filled.Add,
                            contentDescription = null
                        )

                    }
                }


                HorizontalDivider()
                QuoteWriteTextField(
                    text = viewModel.quoteText,
                    modifier = Modifier.height(280.dp),
                    placeholder = "마음에 드는 글귀를 적어주세요. (필수)",
                    input = viewModel.writeEnabled,
                    screenHeight = screenHeight,
                    imeHeight = imeHeight,
                    density = density,
                    scrollMethod = {
                        coroutineScope.launch{
                            scrollState.animateScrollTo(
                                value = 380,
                            )
                        }.onJoin
                    }
                )
                // 위쪽 그림자 효과
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(8.dp) // 그림자 두께 조절
                        .background(
                            Brush.verticalGradient(
                                colors = listOf(
                                    Color.Transparent,
                                    Color.Black.copy(alpha = 0.04f),
                                    Color.Black.copy(alpha = 0.08f),
                                    Color.Black.copy(alpha = 0.14f)
                                )
                            )
                        )
                ){
                    HorizontalDivider(
                        modifier = Modifier.align(Alignment.BottomStart)
                    )
                }

                Box(
                    Modifier.fillMaxHeight()
                ) {
                    QuoteWriteTextField(
                        text = viewModel.thinkText,
                        modifier = Modifier.height(220.dp),
                        placeholder = "왜 인상 깊었는지 적어주세요. (선택)",
                        input = null,
                        screenHeight = screenHeight,
                        imeHeight = imeHeight,
                        density = density,
                        scrollMethod = {
                            coroutineScope.launch{
                                scrollState.animateScrollTo(
                                    value = scrollState.maxValue,
                                )
                            }.onJoin
                        }
                    )
                    BookVerseButton(
                        modifier = Modifier
                            .fillMaxWidth()
                            .align(Alignment.BottomStart),
                        text = "작성하기",
                        onClick = {
                            // 작성 완료
                            // 작성 경고
                            viewModel.completeScreen(!viewModel.writeEnabled.value)
                            Log.d("st","${viewModel.completeEnable.value}")
                        },
                        backgroundColor = Color.Black
                    )
                }

                BookVerseBottomSheet(
                    visible = viewModel.bottomSheetVisible,
                    scrimColor = Color.Transparent.copy(0f),
                    onDismissChange = {
                        viewModel.onDismissAddChange(true)
                    },
                    containerColor = Color.White
                ) {
                    val manager = LocalFocusManager.current
                    Column(
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.clickable(
                            indication = null,
                            interactionSource = remember{ MutableInteractionSource() },
                            onClick = {
                                Log.d("st","click")
                                manager.clearFocus()
                            }
                        ),
                    ){
                        Row {
                            val fix = screenWidth/16 - 8
                            GridView(viewModel.staticList)
                            Spacer(Modifier.width(fix.dp))
                        }


                        if (viewModel.addChange.value) {
                            Spacer(Modifier.size(340.dp))
                            BookVerseButton(
                                modifier = Modifier
                                    .fillMaxWidth(),
                                text = "직접적기",
                                onClick = {
                                    viewModel.onDismissAddChange(false)
                                },
                                isEnable = viewModel.addChange.value,
                                backgroundColor = Color.Black,
                                cornerRadius = 10f
                            )

                        }else{
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.Center
                            ) {
                                QuoteWriteTextField(
                                    text = viewModel.thinkSingleText,
                                    modifier = Modifier.height(70.dp),
                                    placeholder = "태그를 입력하세요",
                                    input = viewModel.thinkAddEnabled,
                                    screenHeight = screenHeight,
                                    imeHeight = imeHeight,
                                    density = density,
                                    scrollMethod = {
                                        coroutineScope.launch{
                                            scrollState.animateScrollTo(
                                                value = scrollState.maxValue,
                                            )
                                        }.onJoin
                                    }
                                )
                                BookVerseButton(
                                    text = "추가",
                                    onClick = {

                                    },
                                    isEnable = viewModel.thinkAddEnabled.value,
                                    backgroundColor = Color.Black,
                                    cornerRadius = 10f
                                )
                                Spacer(Modifier.size(270.dp))
                            }

                        }

                    }

                }

                if (viewModel.completeEnable.value == true) {
                    AlertDialog(
                        modifier = Modifier.size(320.dp,270.dp),
                        onDismissRequest = {
                            viewModel.completeScreen(false)
                        },
                        icon = {
                            Icon(
                                modifier = Modifier.size(40.dp),
                                imageVector = Icons.Rounded.Warning,
                                contentDescription = null
                            )
                        },
                        title = {
                            Text(modifier = Modifier.padding(bottom = 10.dp),
                                text = "글귀 작성")
                        },
                        text = {
                            Text(text = "글귀를 작성해주세요.")
                        },
                        confirmButton = {
                            BookVerseButton(
                                backgroundColor = Color.Transparent.copy(0f),
                                textColor = Color.DarkGray,
                                cornerRadius = 10f,
                                text = "확인",
                                onClick = {
                                    viewModel.completeScreen(false)
                                }
                            )
                        },
                        shape = RoundedCornerShape(10.dp),
                        containerColor = Color.White
                    )
                }
            }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun QuoteWriteTextField(
    text: MutableState<String>,
    placeholder: String,
    modifier: Modifier,
    input: MutableState<Boolean>?,
    screenHeight: Float? = null,
    imeHeight: Int? = null,
    density: Density? = null,
    scrollMethod:()-> Unit = {}
){
    val errorPx = if (density != null)
     with(density) { 11.dp.toPx() }
    else
        0f
    var textFieldYBottom by remember { mutableFloatStateOf(0f) } // TextField의 Y 좌표
    var isCovered = if (density!=null && screenHeight != null && imeHeight != null)
        textFieldYBottom > screenHeight - imeHeight
    else false
    var hasFocus by remember { mutableStateOf(false) }
    var hasSize by remember { mutableIntStateOf(0) }
    Row (
        modifier = modifier
    ) {
        Box(
            modifier = Modifier.weight(3f)
        ) {

            BookVerseTextField(
                modifier = Modifier
                    .onFocusChanged {
                        hasFocus = it.hasFocus
                        if (!(it.hasFocus))
                            hasSize = 0
                    }
                    .onSizeChanged {
                        // 화면 기준 Y 위치 저장 (px 단위)
                        if (density != null) {
                            if (hasFocus && isCovered) {
                                if (hasSize == 0) {
                                    hasSize = it.height
                                } else if (hasSize != it.height) {
                                    scrollMethod()
                                    hasSize = it.height
                                }
                            }
//                            Timber.tag("st").d(placeholder)
//                            Timber.tag("st").d("hasFocus")
//                            Timber.tag("st").d(" $hasFocus")
//                            Timber.tag("st").d("isCovered")
//                            Timber.tag("st").d(" $isCovered")
//                            Timber.tag("st").d("textFieldYBottom")
//                            Timber.tag("st").d(" $textFieldYBottom")
                        }
                    }
                    .onGloballyPositioned { coordinates ->
                        // 화면 기준 Y 위치 저장 (px 단위)
                        if (density != null) {
                            textFieldYBottom =
                                with(density) { coordinates.boundsInRoot().bottom } - errorPx

                            isCovered = if (screenHeight != null && imeHeight != null)
                                textFieldYBottom > screenHeight - imeHeight
                            else false
                        }
                    },
                textFieldValue = text,
                placeHolder = placeholder,
                isError = input,
                focusedBorderColor = Color.Transparent,
                unfocusedBorderColor = Color.Transparent
            )
        }
        Box(
            Modifier.weight(0.5f),
        ) {
            ClearTextIconButton(
                text = text
            )
        }



    }
}

@SuppressLint("SuspiciousIndentation")
@Composable
fun ClearTextIconButton(
    modifier: Modifier = Modifier,
    text: MutableState<String>
){
    val isNotBlank = text.value.isNotBlank()
        IconButton(
            enabled = isNotBlank,
            modifier = modifier.padding(start = 3.dp, top = 5.dp, end = 3.dp),
            onClick = {
                text.value = ""
            }
        ) {
            if(isNotBlank) {
                Icon(
                    imageVector = Icons.Filled.Clear,
                    contentDescription = null
                )
            }
        }
}

@ExperimentalLayoutApi
@Composable
fun GridView(
    list: List<String>
){
    FlowRow(
        modifier = Modifier.padding(bottom = 5.dp),
        verticalArrangement = Arrangement.spacedBy(5.dp),
        horizontalArrangement = Arrangement.spacedBy(5.dp),
        maxItemsInEachRow = 5,
        maxLines = 3
    ) {
        repeat(list.size){
            ChipCard(
                list[it],
                0.5.dp,
                false
            )
        }
    }
}

@Composable
fun ChipCard(
    think: String,
    elevation: Dp = 5.dp,
    clear:Boolean = true
){
    Card (
        modifier = Modifier
            .wrapContentWidth()
            .height(30.dp),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        ),
        elevation = CardDefaults.elevatedCardElevation(
            elevation
        )
    ){
        Row(
            modifier = Modifier.padding(start = 5.dp, end = 2.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Start
        ) {

            Text(modifier = Modifier.padding(end = 3.dp),
                text = "#${think}"
            )
            if (clear) {
                Card(
                    modifier = Modifier
                        .clip(RoundedCornerShape(10.dp))
                        .clickable(
                            onClick = {

                            }
                        ),
                    colors = CardDefaults.cardColors(
                        containerColor = Color.Transparent
                    )
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

