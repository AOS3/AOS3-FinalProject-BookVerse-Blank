package com.blank.bookverse.presentation.common

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.internal.enableLiveLiterals
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun BookVerseButton(
    text: String = "",
    onClick: () -> Unit,
    backgroundColor: Color = Color.White,
    textColor: Color = Color.White,
    cornerRadius: Float = 0f,
    modifier: Modifier = Modifier,
    textStyle: TextStyle = TextStyle(),
    isEnable: Boolean = true
) {
    Button(
        onClick = onClick,
        modifier = modifier,
        colors = ButtonDefaults.buttonColors(containerColor = backgroundColor),
        shape = RoundedCornerShape(cornerRadius.dp), // corner radius 설정
        enabled = isEnable
    ) {
        Text(
            text = text,
            color = textColor,
            style = textStyle
        )
    }
}