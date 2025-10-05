package com.sztorm.notecalendar.components

import androidx.compose.foundation.gestures.snapping.SnapPosition
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PageSize
import androidx.compose.foundation.pager.PagerDefaults
import androidx.compose.foundation.pager.PagerScope
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

@Composable
fun InfiniteHorizontalPager(
    modifier: Modifier = Modifier,
    contentPadding: PaddingValues = PaddingValues(0.dp),
    pageSize: PageSize = PageSize.Fill,
    beyondViewportPageCount: Int = PagerDefaults.BeyondViewportPageCount,
    pageSpacing: Dp = 0.dp,
    verticalAlignment: Alignment.Vertical = Alignment.CenterVertically,
    userScrollEnabled: Boolean = true,
    reverseLayout: Boolean = false,
    key: ((Int) -> Any)? = null,
    snapPosition: SnapPosition = SnapPosition.Start,
    onPageChange: ((Int) -> Unit)? = null,
    pageContent: @Composable (PagerScope.(Int) -> Unit)
) {
    val pageCount = Int.MAX_VALUE
    val initialPage = pageCount / 2
    val pagerState = rememberPagerState(initialPage = initialPage, pageCount = { pageCount })
    val keyFixed: ((Int) -> Any)? = when (key) {
        null -> null
        else -> { page -> key(page - initialPage) }
    }
    if (onPageChange != null) {
        LaunchedEffect(pagerState) {
            snapshotFlow { pagerState.currentPage }
                .collect { page -> onPageChange(page - initialPage) }
        }
    }
    HorizontalPager(
        state = pagerState,
        modifier = modifier,
        contentPadding = contentPadding,
        pageSize = pageSize,
        beyondViewportPageCount = beyondViewportPageCount,
        pageSpacing = pageSpacing,
        verticalAlignment = verticalAlignment,
        userScrollEnabled = userScrollEnabled,
        reverseLayout = reverseLayout,
        key = keyFixed,
        snapPosition = snapPosition
    ) { page ->
        pageContent(page - initialPage)
    }
}