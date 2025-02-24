package com.blank.bookverse.presentation.ui.add_comment

import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.blank.bookverse.R
import com.blank.bookverse.presentation.common.BookVerseButton
import com.blank.bookverse.presentation.common.BookVerseToolbar

@Composable
fun AddCommentScreen(
    navController: NavController,
    viewModel: AddCommentViewModel = hiltViewModel()
) {
    val context = navController.context
    val state = viewModel.state.value
    val keyboardController = LocalSoftwareKeyboardController.current
    val focusRequester = remember { FocusRequester() }

    LaunchedEffect(Unit) {
        viewModel.effect.collect { effect ->
            when (effect) {
                is AddCommentEffect.ShowKeyboard -> {
                    keyboardController?.show()
                }

                is AddCommentEffect.FocusTextField -> {
                    focusRequester.requestFocus()
                }

                is AddCommentEffect.SaveSuccess -> {
                    navController.popBackStack()
                }

                is AddCommentEffect.SaveFailure -> {
                    Toast.makeText(context, effect.message, Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    Scaffold(
        topBar = {
            BookVerseToolbar(
                title = "나의 생각 기록",
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            painter = painterResource(R.drawable.ic_back_arrow),
                            contentDescription = null
                        )
                    }
                }
            )
        }
    ) { paddingValues ->
        AddCommentContent(
            modifier = Modifier.padding(paddingValues),
            text = state.text,
            onTextChange = viewModel::updateText,
            onSubmit = viewModel::submitComment,
            focusRequester = focusRequester,
        )
    }
}

@Composable
fun AddCommentContent(
    modifier: Modifier = Modifier,
    text: String,
    onTextChange: (String) -> Unit,
    onSubmit: () -> Unit,
    focusRequester: FocusRequester,
) {
    Box(
        modifier = modifier.fillMaxSize()
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight()
                .padding(bottom = 52.dp)
        ) {
            VerticalDivider(
                modifier = Modifier
                    .fillMaxHeight()
                    .padding(start = 42.dp)
            )
            VerticalDivider(
                modifier = Modifier
                    .fillMaxHeight()
                    .align(Alignment.TopEnd)
                    .padding(end = 42.dp)
            )
        }

        // 컨텐츠
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                Spacer(modifier = Modifier.height(16.dp))
                HorizontalDivider(
                    modifier = Modifier.fillMaxWidth()
                )
                Box(
                    modifier = Modifier
                        .padding(horizontal = 42.dp)
                        .height(170.dp)
                ) {
                    CommentTextField(
                        text = text,
                        onTextChange = onTextChange,
                        modifier = Modifier.fillMaxSize(),
                        focusRequester = focusRequester,
                    )
                }
                HorizontalDivider(
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "${text.length}/100",
                    color = Color.Gray,
                    fontSize = 12.sp,
                    modifier = Modifier
                        .align(Alignment.End)
                        .padding(end = 42.dp)
                        .padding(end = 8.dp)
                )
            }

            BookVerseButton(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                text = "기록하기",
                onClick = onSubmit,
                backgroundColor = Color.Black,
                textColor = Color.White,
                isEnable = text.isNotBlank()
            )
        }
    }
}

@Composable
fun CommentTextField(
    text: String,
    onTextChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    focusRequester: FocusRequester,
) {
    BasicTextField(
        value = text,
        onValueChange = onTextChange,
        textStyle = TextStyle(
            color = Color.Black,
            fontSize = 18.sp
        ),
        modifier = modifier
            .fillMaxWidth()
            .padding(16.dp)
            .focusRequester(focusRequester),
        decorationBox = { innerTextField ->
            Box {
                if (text.isEmpty()) {
                    Text(
                        text = "글귀에 대한 나의 생각을 기록해요",
                        color = Color.Gray
                    )
                }
                innerTextField()
            }
        }
    )
}

@Preview(showBackground = true)
@Composable
fun AddCommentContentPreview() {
    AddCommentContent(
        text = "",
        onTextChange = {},
        onSubmit = {},
        focusRequester = remember { FocusRequester() },
    )
}