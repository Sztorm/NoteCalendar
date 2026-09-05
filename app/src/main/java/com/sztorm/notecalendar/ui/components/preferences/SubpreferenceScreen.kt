package com.sztorm.notecalendar.ui.components.preferences

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarColors
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.lerp
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SubpreferenceScreen(
    title: String,
    onBackButtonClick: () -> Unit,
    modifier: Modifier = Modifier,
    backButtonIcon: ImageVector = Icons.AutoMirrored.Filled.ArrowBack,
    iconTint: Color = LocalContentColor.current,
    titleExpandedColor: Color = MaterialTheme.colorScheme.onBackground,
    titleCollapsedColor: Color = MaterialTheme.colorScheme.onSecondaryContainer,
    backgroundExpandedColor: Color = MaterialTheme.colorScheme.background,
    backgroundCollapsedColor: Color = MaterialTheme.colorScheme.secondaryContainer,
    scrollState: ScrollState = rememberScrollState(),
    content: @Composable ColumnScope.() -> Unit
) {
    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior(
        canScroll = { scrollState.canScrollBackward || scrollState.canScrollForward }
    )
    val isAppBarExpanded by remember {
        derivedStateOf { scrollBehavior.state.collapsedFraction < 0.7f }
    }
    val topBarColors = TopAppBarDefaults.topAppBarColors(
        containerColor = Color.Transparent,
        scrolledContainerColor = Color.Transparent,
        navigationIconContentColor = iconTint
    )
    Scaffold(
        topBar = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        lerp(
                            backgroundExpandedColor,
                            backgroundCollapsedColor,
                            scrollBehavior.state.collapsedFraction
                        )
                    )
            ) {
                CollapsedAppBar(
                    title = title,
                    textColor = titleCollapsedColor,
                    backButtonIcon = backButtonIcon,
                    onBackButtonClick = onBackButtonClick,
                    topBarColors = topBarColors,
                    visible = !isAppBarExpanded
                )
                ExpandedAppBar(
                    title = title,
                    textColor = titleExpandedColor,
                    scrollBehavior = scrollBehavior,
                    topBarColors = topBarColors,
                    visible = isAppBarExpanded
                )
            }
        },
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection)
    ) { paddingValues ->
        Column(
            modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(scrollState)
        ) {

            this.content()
        }
    }

}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun CollapsedAppBar(
    modifier: Modifier = Modifier,
    title: String,
    textColor: Color,
    backButtonIcon: ImageVector,
    topBarColors: TopAppBarColors,
    onBackButtonClick: () -> Unit,
    visible: Boolean
) {
    TopAppBar(
        modifier = modifier,
        navigationIcon = {
            IconButton(onClick = onBackButtonClick) {
                Icon(
                    imageVector = backButtonIcon,
                    contentDescription = null,
                )
            }
        },
        title = {
            AnimatedVisibility(
                visible = visible,
                enter = fadeIn(animationSpec = tween()),
                exit = fadeOut(animationSpec = tween()),
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(horizontal = 20.dp)
                ) {
                    Text(
                        text = title,
                        color = textColor,
                        fontSize = 20.sp
                    )
                }
            }
        },
        expandedHeight = 56.dp,
        colors = topBarColors
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ExpandedAppBar(
    modifier: Modifier = Modifier,
    title: String,
    textColor: Color,
    topBarColors: TopAppBarColors,
    scrollBehavior: TopAppBarScrollBehavior,
    visible: Boolean
) {
    val expandedAppBarHeight = 116.dp
    val headerTranslation = expandedAppBarHeight * 0.5f

    TopAppBar(
        modifier = modifier,
        title = {
            AnimatedVisibility(
                visible = visible,
                enter = fadeIn(animationSpec = tween()),
                exit = fadeOut(animationSpec = tween()),
                modifier = Modifier.graphicsLayer {
                    translationY =
                        scrollBehavior.state.collapsedFraction * -headerTranslation.toPx()
                    alpha = (1f - scrollBehavior.state.collapsedFraction * 2f).coerceIn(0f, 1f)
                }
            ) {
                Row(
                    modifier = Modifier.padding(
                        start = 8.dp, end = 8.dp, top = 32.dp, bottom = 0.dp
                    )
                ) {
                    Text(
                        text = title,
                        color = textColor,
                        fontSize = 36.sp
                    )
                }
            }
        },
        colors = topBarColors,
        expandedHeight = expandedAppBarHeight,
        windowInsets = WindowInsets(),
        scrollBehavior = scrollBehavior,
    )
}