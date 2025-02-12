package com.blank.bookverse.presentation.common

import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import com.blank.bookverse.presentation.theme.notoSansFamily

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BookVerseToolbar(
    title: String,
    navigationIcon: @Composable (() -> Unit)? = null,
    actions: @Composable (RowScope.() -> Unit)? = null,
) {
    TopAppBar(
        title = {
            Text(
                text = title,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                style = MaterialTheme.typography.titleLarge.copy(
                    fontFamily = notoSansFamily,
                    fontWeight = FontWeight.Light
                )
            )
        },
        navigationIcon = {
            navigationIcon?.invoke()
        },
        actions = {
            actions?.invoke(this)
        },
        modifier = Modifier.fillMaxWidth()
    )
}