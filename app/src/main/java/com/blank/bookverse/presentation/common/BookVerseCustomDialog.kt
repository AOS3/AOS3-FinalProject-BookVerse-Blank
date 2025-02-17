package com.blank.bookverse.presentation.common

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog

@Composable
fun BookVerseCustomDialog(
    onDismiss: () -> Unit = {},
    onConfirm: () -> Unit = {},
    onCancel: () -> Unit = {},
    negativeText: String = "아니오",
    positiveText: String = "예",
    title: String = "",
    message: String = ".",
    icon: ImageVector = Icons.Default.Warning,
) {
    Dialog(onDismissRequest = { onDismiss() }) {
        Surface(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            color = Color(0xFFEAEAEA) // 연한 회색 배경
        ) {
            Column(
                modifier = Modifier.padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    modifier = Modifier.size(40.dp),
                    tint = Color.Black
                )
                Spacer(modifier = Modifier.height(12.dp))
                Text(text = title, fontSize = 20.sp, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(8.dp))
                Text(text = message, fontSize = 16.sp, color = Color.DarkGray)
                Spacer(modifier = Modifier.height(20.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    Text(
                        text = positiveText,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.Black,
                        modifier = Modifier.clickable { onConfirm(); onDismiss() }
                    )
                    Text(
                        text = negativeText,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.Black,
                        modifier = Modifier.clickable { onCancel(); onDismiss() }
                    )
                }
            }
        }
    }
}

@Preview
@Composable
fun PreviewBookVerseCustomDialog() {
    BookVerseCustomDialog(
        title = "탈퇴",
        message = "탈퇴하시겠습니까? 그동안 저장했던 글귀들은 복구할 수 없습니다.",
        onConfirm = {},
        onCancel = {}
    )
}

/*package com.blank.bookverse.presentation.common

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun BookVerseCustomDialog(
    onDismiss: () -> Unit = {},
    onConfirm: () -> Unit= {},
    onCancel: () -> Unit= {},
    negativeText:String = "취소",
    positiveText:String = "확인",
    title: String = "",
    message: String = "",
    icon: ImageVector = Icons.Default.Warning,
) {
    AlertDialog(
        onDismissRequest = { onDismiss() },
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    modifier = Modifier.size(24.dp),
                    tint = Color.Black
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(text = title, fontWeight = FontWeight.Bold, fontSize = 18.sp)
            }
        },
        text = {
            Text(text = message, fontSize = 16.sp)
        },
        confirmButton = {
            Button(
                onClick = {
                    onConfirm()
                    onDismiss()
                }
            ) {
                Text(positiveText)
            }
        },
        dismissButton = {
            Button(
                onClick = {
                    onCancel()
                    onDismiss()
                }
            ) {
                Text(negativeText)
            }
        }
    )
}*/
