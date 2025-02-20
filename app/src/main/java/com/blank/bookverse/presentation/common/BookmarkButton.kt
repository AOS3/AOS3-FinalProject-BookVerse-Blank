package com.blank.bookverse.presentation.common

import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import com.blank.bookverse.R

@Composable
fun BookmarkButton(
    modifier: Modifier = Modifier,
    isBookmark: Boolean = false,
    onBookmarkClick: () -> Unit = {}
) {
    IconButton(
        modifier = modifier,
        onClick = { onBookmarkClick() }
    ) {
        Icon(
            painter = if (isBookmark) {
                painterResource(R.drawable.ic_bookmark_fill)
            } else {
                painterResource(R.drawable.ic_bookmark)
            },
            contentDescription = "책갈피",
        )
    }
}