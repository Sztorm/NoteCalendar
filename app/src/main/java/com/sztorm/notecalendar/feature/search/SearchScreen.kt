package com.sztorm.notecalendar.feature.search

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.sztorm.notecalendar.R
import com.sztorm.notecalendar.core.common.isEven
import com.sztorm.notecalendar.core.logging.AppLogger
import com.sztorm.notecalendar.domain.repositories.NoteRepository
import com.sztorm.notecalendar.ui.components.IconButton
import java.time.LocalDate

@Composable
private fun SearchTextField(
    state: SearchScreenState,
    onBackButtonClick: () -> Unit,
    onClearButtonClick: () -> Unit,
    onTextValueChange: (TextFieldValue) -> Unit,
    onFiltersExpand: () -> Unit
) = OutlinedTextField(
    value = state.searchTextValue,
    onValueChange = onTextValueChange,
    leadingIcon = {
        IconButton(onClick = onBackButtonClick) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                tint = MaterialTheme.colorScheme.onBackground,
                contentDescription = null
            )
        }
    },
    trailingIcon = {
        Row {
            BadgedBox(
                badge = {
                    if (state.searchFilters.isNotEmpty()) {
                        Badge(containerColor = MaterialTheme.colorScheme.secondary) {
                            Text(state.searchFilters.size.toString())
                        }
                    }
                }
            ) {
                IconButton(
                    onClick = onFiltersExpand
                ) {
                    Icon(
                        painter = painterResource(R.drawable.icon_outline_rounded_filter_list),
                        tint = MaterialTheme.colorScheme.onBackground,
                        contentDescription = "Toggle filter list" // TODO: add to strings.xml
                    )
                }
            }
            IconButton(
                onClick = onClearButtonClick
            ) {
                Icon(
                    imageVector = Icons.Filled.Close,
                    tint = MaterialTheme.colorScheme.onBackground,
                    contentDescription = "Clear search term"// TODO: add to strings.xml
                )
            }
        }
    },
    placeholder = {
        Text(
            text = "Search notes" // TODO: Add to strings.xml
        )
    },
    modifier = Modifier
        .fillMaxWidth()
        .padding(8.dp)
)

@Composable
private fun ColumnScope.SearchFilters(
    state: SearchScreenState,
    filtersExpanded: Boolean,
    onFilterClearAllClick: () -> Unit,
    onMatchCaseFilterChange: (Boolean) -> Unit
) {
    AnimatedVisibility(visible = filtersExpanded) {
        Column(modifier = Modifier.padding(8.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = "Filters", // TODO: add to strings.xml
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.padding(vertical = 12.dp)
                )
                Spacer(modifier = Modifier.weight(1f))

                if (state.searchFilters.isNotEmpty()) {
                    TextButton(onClick = onFilterClearAllClick) {
                        Text(
                            text = "Clear all" // TODO: add to strings.xml
                        )
                    }
                }
            }
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = "Match case", // TODO: add to strings.xml

                )
                Spacer(modifier = Modifier.weight(1f))
                Switch(
                    checked = state.searchFilters.contains(SearchFilter.MatchCase),
                    onCheckedChange = onMatchCaseFilterChange
                )
            }
            // TODO: date range selection
        }
    }
}

@Composable
private fun SearchBar(
    state: SearchScreenState,
    onBackButtonClick: () -> Unit,
    onClearButtonClick: () -> Unit,
    onTextValueChange: (TextFieldValue) -> Unit,
    onFilterClearAllClick: () -> Unit,
    onMatchCaseFilterChange: (Boolean) -> Unit
) {
    var filtersExpanded by rememberSaveable {
        mutableStateOf(false)
    }
    Column(modifier = Modifier.fillMaxWidth()) {
        SearchTextField(
            state = state,
            onBackButtonClick = onBackButtonClick,
            onClearButtonClick = onClearButtonClick,
            onTextValueChange = onTextValueChange,
            onFiltersExpand = { filtersExpanded = !filtersExpanded }
        )
        SearchFilters(
            state = state,
            filtersExpanded = filtersExpanded,
            onFilterClearAllClick = onFilterClearAllClick,
            onMatchCaseFilterChange = onMatchCaseFilterChange
        )
        HorizontalDivider(color = MaterialTheme.colorScheme.outline)
    }
}

@Composable
private fun SearchResultRow(
    index: Int,
    result: SearchResult,
    searchText: String,
    onResultGoButtonClick: (isLongClick: Boolean, date: LocalDate) -> Unit
) {
    val backgroundColor =
        if (index.isEven) MaterialTheme.colorScheme.surfaceVariant
        else MaterialTheme.colorScheme.surface
    val noteDate = result.note.date
    var expanded by rememberSaveable(noteDate) { mutableStateOf(false) }
    val noteText = buildAnnotatedString {
        append(result.note.text)

        result.matchIndices.forEach { startIndex ->
            addStyle(
                style = SpanStyle(
                    background = MaterialTheme.colorScheme.secondaryContainer,
                    color = MaterialTheme.colorScheme.onSecondaryContainer
                ),
                start = startIndex,
                end = startIndex + searchText.length
            )
        }
    }
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(backgroundColor)
            .clickable { expanded = !expanded }
            .animateContentSize()
            .padding(all = 8.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = noteDate.toString(),
                textAlign = TextAlign.Center
            )
            if (!expanded) {
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = noteText,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.weight(1f)
                )
                Spacer(modifier = Modifier.width(8.dp))
            } else {
                Spacer(Modifier.weight(1f))
            }
            IconButton(
                onClick = { onResultGoButtonClick(false, noteDate) },
                onLongClick = { onResultGoButtonClick(true, noteDate) },
                onClickLabel = "Go to note", // TODO: add to strings.xml
                onLongClickLabel = "Go to note and edit", // TODO: add to strings.xml
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                    contentDescription = "Go to note" // TODO: add to strings.xml
                )
            }
        }
        if (expanded) {
            Text(
                text = noteText,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 4.dp)
            )
        }
    }
}

@Composable
private fun ColumnScope.SearchResults(
    state: SearchScreenState,
    onNavigateToNote: (isLongClick: Boolean, date: LocalDate) -> Unit
) {
    when (val uiState = state.uiState) {
        is SearchUiState.Loading -> {
            Spacer(modifier = Modifier.weight(1f))
            CircularProgressIndicator()
            Spacer(modifier = Modifier.weight(1f))
        }

        is SearchUiState.Success -> {
            if (uiState.results.isEmpty()) {
                Spacer(modifier = Modifier.weight(1f))
                Text(
                    text = "No results", // TODO: Add to strings.xml
                    textAlign = TextAlign.Center,
                    style = MaterialTheme.typography.bodyLarge,
                )
                Spacer(modifier = Modifier.weight(1f))
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize()
                ) {
                    items(
                        items = uiState.results.mapIndexed { i, note -> i to note },
                        key = { (_, result) -> result.note.date }
                    ) { (index, result) ->
                        SearchResultRow(
                            index = index,
                            result = result,
                            searchText = state.searchTextValue.text,
                            onResultGoButtonClick = onNavigateToNote
                        )
                    }
                }
            }
        }

        is SearchUiState.Error -> {
            Spacer(modifier = Modifier.weight(1f))
            Text(
                text = uiState.message,
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.bodyLarge,
            )
            Spacer(modifier = Modifier.weight(1f))
        }
    }
}

@Composable
fun SearchScreen(
    logger: AppLogger,
    onBackButtonClick: () -> Unit,
    onNavigateToNote: (isLongClick: Boolean, date: LocalDate) -> Unit,
    noteRepository: NoteRepository
) {
    val viewModel = viewModel<SearchScreenViewModel>(
        factory = SearchScreenViewModelFactory(
            initialState = SearchScreenState(
                uiState = SearchUiState.Success(emptyList()),
                searchTextValue = TextFieldValue(),
                searchFilters = emptySet()
            ),
            noteRepository = noteRepository,
            logger = logger,
        )
    )
    val state by viewModel.state.collectAsStateWithLifecycle()

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.fillMaxSize()
    ) {
        SearchBar(
            state = state,
            onBackButtonClick = onBackButtonClick,
            onClearButtonClick = {
                viewModel.onEvent(
                    SearchScreenEvent.SearchTextValueChange(TextFieldValue())
                )
            },
            onTextValueChange = {
                viewModel.onEvent(SearchScreenEvent.SearchTextValueChange(it))
            },
            onFilterClearAllClick = {
                viewModel.onEvent(
                    SearchScreenEvent.SearchFiltersChange(emptySet())
                )
            },
            onMatchCaseFilterChange = { isChecked ->
                if (isChecked) {
                    viewModel.onEvent(
                        SearchScreenEvent.SearchFiltersChange(
                            state.searchFilters + SearchFilter.MatchCase
                        )
                    )
                } else {
                    viewModel.onEvent(
                        SearchScreenEvent.SearchFiltersChange(
                            state.searchFilters - SearchFilter.MatchCase
                        )
                    )
                }
            }
        )
        SearchResults(
            state = state,
            onNavigateToNote = onNavigateToNote
        )
    }
}