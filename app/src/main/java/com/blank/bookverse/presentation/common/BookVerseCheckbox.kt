package com.blank.bookverse.presentation.common

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.blank.bookverse.R

@Composable
fun BookVerseCheckBox(
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    text: String? = null
) {
    Row(
        modifier = Modifier
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                onClick = { onCheckedChange(!checked) }
            )
            .padding(4.dp)
            .height(40.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        val icon = if (checked) {
            painterResource(id = R.drawable.ic_check_on)
        } else {
            painterResource(id = R.drawable.ic_check_off)
        }

        Icon(
            painter = icon,
            contentDescription = null,
            modifier = Modifier.size(20.dp),
            tint = Color.Unspecified
        )

        if (!text.isNullOrEmpty()) {
            Spacer(modifier = Modifier.width(8.dp))

            Text(
                text = text,
            )
        }
    }
}