package com.sztorm.notecalendar.components

import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Modifier
import com.sztorm.notecalendar.reachedBottom
import com.sztorm.notecalendar.reachedTop

@Composable
fun <T> InfiniteColumn(
    modifier: Modifier = Modifier,
    items: SnapshotStateList<T>,
    state: LazyListState = rememberLazyListState(),
    key: (Int, T) -> Any,
    onReachTop: () -> Unit,
    onReachBottom: () -> Unit,
    itemContent: @Composable (Int, T) -> Unit,
) {
    val reachedBottom: Boolean by remember {
        derivedStateOf { state.reachedBottom() }
    }
    val reachedTop: Boolean by remember {
        derivedStateOf { state.reachedTop() }
    }
    LaunchedEffect(reachedBottom) {
        if (reachedBottom) {
            onReachBottom()
        }
    }
    LaunchedEffect(reachedTop) {
        if (reachedTop) {
            onReachTop()
        }
    }
    LazyColumn(state = state, modifier = modifier) {
        itemsIndexed(items, key) { index, item ->
            itemContent(index, item)
        }
    }
}