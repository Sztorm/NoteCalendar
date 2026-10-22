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
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DatePickerDefaults
import androidx.compose.material3.DatePickerFormatter
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
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.intl.Locale
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.sztorm.notecalendar.R
import com.sztorm.notecalendar.core.common.isEven
import com.sztorm.notecalendar.core.common.plus
import com.sztorm.notecalendar.core.common.toEpochMillis
import com.sztorm.notecalendar.core.logging.AppLogger
import com.sztorm.notecalendar.domain.repositories.NoteRepository
import com.sztorm.notecalendar.feature.app.AppViewModel
import com.sztorm.notecalendar.feature.settings.theme.ThemeColors
import com.sztorm.notecalendar.ui.components.DateRangePickerDialog
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
    maxLines = 1,
    leadingIcon = {
        IconButton(onClick = onBackButtonClick) {
            Icon(
                painter = painterResource(R.drawable.icon_rounded_arrow_back),
                tint = MaterialTheme.colorScheme.onBackground,
                contentDescription = null
            )
        }
    },
    trailingIcon = {
        Row {
            BadgedBox(
                badge = {
                    if (state.searchFiltersByType.isNotEmpty()) {
                        Badge(containerColor = MaterialTheme.colorScheme.secondary) {
                            Text(state.searchFiltersByType.size.toString())
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
                        contentDescription = stringResource(R.string.ToggleFilters)
                    )
                }
            }
            IconButton(
                onClick = onClearButtonClick
            ) {
                Icon(
                    painter = painterResource(R.drawable.icon_rounded_close),
                    tint = MaterialTheme.colorScheme.onBackground,
                    contentDescription = stringResource(R.string.ClearSearch)
                )
            }
        }
    },
    placeholder = {
        Text(
            text = stringResource(R.string.SearchNotes)
        )
    },
    modifier = Modifier
        .fillMaxWidth()
        .padding(8.dp)
)

@Composable
private fun ColumnScope.SearchFilters(
    state: SearchScreenState,
    dateFormatter: DatePickerFormatter,
    filtersExpanded: Boolean,
    onFilterClearAllClick: () -> Unit,
    onMatchCaseFilterChange: (Boolean) -> Unit,
    onDateRangeFilterChange: (LocalDate?, LocalDate?) -> Unit,
    themeColors: ThemeColors
) {
    var showDateRangePicker by remember { mutableStateOf(false) }

    AnimatedVisibility(visible = filtersExpanded) {
        Column(modifier = Modifier.padding(top = 8.dp, bottom = 8.dp, start = 16.dp, end = 8.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = stringResource(R.string.Filters),
                    style = MaterialTheme.typography.titleSmall,
                    modifier = Modifier.padding(vertical = 12.dp)
                )
                Spacer(modifier = Modifier.weight(1f))

                if (state.searchFiltersByType.isNotEmpty()) {
                    TextButton(onClick = onFilterClearAllClick) {
                        Text(text = stringResource(R.string.ClearFilters))
                    }
                }
            }
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(text = stringResource(R.string.MatchCase))
                Spacer(modifier = Modifier.weight(1f))
                Switch(
                    checked = state.searchFiltersByType.contains(SearchFilter.MatchCase::class),
                    onCheckedChange = onMatchCaseFilterChange
                )
            }
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(text = stringResource(R.string.DateRange))
                Spacer(modifier = Modifier.weight(1f))
                val dateRange = state.searchFiltersByType
                    .getTyped(SearchFilter.DateRange::class)

                if (dateRange == null) {
                    TextButton(onClick = { showDateRangePicker = true }) {
                        Text(text = stringResource(R.string.Select))
                    }
                } else {
                    TextButton(onClick = { showDateRangePicker = true }) {
                        val locale = Locale.current.platformLocale
                        val startDate = dateFormatter.formatDate(
                            dateMillis = dateRange.start.toEpochMillis(),
                            locale = locale
                        )
                        val endDate = dateFormatter.formatDate(
                            dateMillis = dateRange.end.toEpochMillis(),
                            locale = locale
                        )
                        Text(
                            text = "$startDate - $endDate"
                        )
                    }
                    IconButton(onClick = { onDateRangeFilterChange(null, null) }) {
                        Icon(
                            painter = painterResource(R.drawable.icon_rounded_close),
                            contentDescription = stringResource(R.string.RemoveDateRangeFilter)
                        )
                    }
                }
            }
        }
    }
    if (showDateRangePicker) {
        DateRangePickerDialog(
            onDateRangeSelected = { startDate, endDate ->
                showDateRangePicker = false
                onDateRangeFilterChange(startDate, endDate)
            },
            onDismiss = {
                showDateRangePicker = false
            },
            dateFormatter = dateFormatter,
            buttonColors = ButtonDefaults.textButtonColors(
                contentColor = themeColors.primaryColor,
                disabledContentColor = themeColors.inactiveTextColor
            )
        )
    }
}

@Composable
private fun SearchBar(
    state: SearchScreenState,
    dateFormatter: DatePickerFormatter,
    onBackButtonClick: () -> Unit,
    onClearButtonClick: () -> Unit,
    onTextValueChange: (TextFieldValue) -> Unit,
    onFilterClearAllClick: () -> Unit,
    onMatchCaseFilterChange: (Boolean) -> Unit,
    onDateRangeFilterChange: (LocalDate?, LocalDate?) -> Unit,
    themeColors: ThemeColors
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
            dateFormatter = dateFormatter,
            filtersExpanded = filtersExpanded,
            onFilterClearAllClick = onFilterClearAllClick,
            onMatchCaseFilterChange = onMatchCaseFilterChange,
            onDateRangeFilterChange = onDateRangeFilterChange,
            themeColors = themeColors,
        )
        HorizontalDivider(color = MaterialTheme.colorScheme.outline)
    }
}

@Composable
private fun SearchResultRow(
    index: Int,
    result: SearchResult,
    searchText: String,
    dateFormatter: DatePickerFormatter,
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
            .padding(top = 8.dp, bottom = 8.dp, start = 16.dp, end = 16.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            val formattedDate = dateFormatter.formatDate(
                dateMillis = noteDate.toEpochMillis(),
                locale = Locale.current.platformLocale
            ) ?: noteDate.toString()
            Text(
                text = formattedDate,
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
                onClickLabel = stringResource(R.string.OpenNote),
                onLongClickLabel = stringResource(R.string.OpenAndEditNote)
            ) {
                Icon(
                    painter = painterResource(R.drawable.icon_rounded_arrow_forward),
                    contentDescription = stringResource(R.string.OpenNote)
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
    dateFormatter: DatePickerFormatter,
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
                    text = stringResource(R.string.NoResults),
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
                            dateFormatter = dateFormatter,
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
    appViewModel: AppViewModel,
    onBackButtonClick: () -> Unit,
    onNavigateToNote: (isLongClick: Boolean, date: LocalDate) -> Unit,
    noteRepository: NoteRepository
) {
    val viewModel = viewModel<SearchScreenViewModel>(
        factory = SearchScreenViewModelFactory(
            initialState = SearchScreenState(
                uiState = SearchUiState.Success(emptyList()),
                searchTextValue = TextFieldValue(),
                searchFiltersByType = emptyMap(),
                defaultSearchErrorMessage = stringResource(R.string.SearchFailed)
            ),
            noteRepository = noteRepository,
            logger = logger,
        )
    )
    val state by viewModel.state.collectAsStateWithLifecycle()
    val dateFormatter = remember {
        DatePickerDefaults.dateFormatter(selectedDateSkeleton = "yyyyMMdd")
    }
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.fillMaxSize()
    ) {
        SearchBar(
            state = state,
            dateFormatter = dateFormatter,
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
                    SearchScreenEvent.SearchFiltersChange(emptyMap())
                )
            },
            onMatchCaseFilterChange = { isChecked ->
                if (isChecked) {
                    viewModel.onEvent(
                        SearchScreenEvent.SearchFiltersChange(
                            state.searchFiltersByType + SearchFilter.MatchCase
                        )
                    )
                } else {
                    viewModel.onEvent(
                        SearchScreenEvent.SearchFiltersChange(
                            state.searchFiltersByType - SearchFilter.MatchCase::class
                        )
                    )
                }
            },
            onDateRangeFilterChange = { startDate, endDate ->
                if (startDate != null && endDate != null) {
                    viewModel.onEvent(
                        SearchScreenEvent.SearchFiltersChange(
                            state.searchFiltersByType +
                                SearchFilter.DateRange(startDate, endDate)
                        )
                    )
                } else {
                    viewModel.onEvent(
                        SearchScreenEvent.SearchFiltersChange(
                            state.searchFiltersByType - SearchFilter.DateRange::class
                        )
                    )
                }
            },
            themeColors = appViewModel.state.themeColors
        )
        SearchResults(
            state = state,
            dateFormatter = dateFormatter,
            onNavigateToNote = onNavigateToNote
        )
    }
}