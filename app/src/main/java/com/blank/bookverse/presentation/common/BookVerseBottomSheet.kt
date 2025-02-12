package com.blank.bookverse.presentation.common


import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BookVerseBottomSheet(
    visible: MutableState<Boolean>,
    content: @Composable () -> Unit
) {
    // visible 값에 따라 BottomSheet를 열고 닫는 방식
    val sheetState = remember { mutableStateOf(visible.value) }

    // visible 값이 변경될 때마다 상태를 반영
    LaunchedEffect(visible.value) {
        sheetState.value = visible.value
    }

    if (sheetState.value) {
        ModalBottomSheet(
            onDismissRequest = {
                visible.value = false
            }
        ) {
            content()
        }
    }
}


//   val customBottomSheetVisible = remember { mutableStateOf(true) }
//       BookVerseBottomSheet(
//           visible = customBottomSheetVisible
//       ) {
//           Text(text = "여기는 BottomSheet 내용입니다.")
//       }