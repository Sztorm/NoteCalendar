package com.sztorm.notecalendar.feature.search

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.sztorm.notecalendar.core.common.isEven
import com.sztorm.notecalendar.core.logging.AppLogger
import com.sztorm.notecalendar.domain.models.Note
import com.sztorm.notecalendar.domain.models.toNote
import com.sztorm.notecalendar.domain.repositories.NoteRepository
import com.sztorm.notecalendar.feature.app.AppEvent
import com.sztorm.notecalendar.feature.app.AppViewModel
import com.sztorm.notecalendar.feature.app.NavigationBarDestination
import com.sztorm.notecalendar.feature.app.Screen
import com.sztorm.notecalendar.feature.settings.theme.ThemeColors
import com.sztorm.notecalendar.ui.components.IconButton
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@Composable
fun SearchItemRow(
    index: Int,
    note: Note,
    appViewModel: AppViewModel,
    themeColors: ThemeColors,
    navController: NavController
) {
    val backgroundColor = when {
        index.isEven -> themeColors.backgroundColorVariant
        else -> themeColors.backgroundColor
    }
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .background(color = backgroundColor)
            .padding(horizontal = 8.dp, vertical = 8.dp)
    ) {
        Text(
            text = note.date.toString(),
            textAlign = TextAlign.Center
        )
        Spacer(Modifier.weight(1f))
        IconButton(
            onClick = {
                appViewModel.onEvent(
                    AppEvent.DayScreenDateChange(note.date)
                )
                appViewModel.onEvent(
                    AppEvent.NavigationBarDestinationChange(
                        NavigationBarDestination.Day
                    )
                )
                navController.navigate(Screen.Day())
            },
            onLongClick = {
                appViewModel.onEvent(
                    AppEvent.DayScreenDateChange(note.date)
                )
                appViewModel.onEvent(
                    AppEvent.NavigationBarDestinationChange(
                        NavigationBarDestination.Day
                    )
                )
                navController.navigate(Screen.Day(isCreateOrEditRequested = true))
            },
            onClickLabel = "Go to note", // TODO: add to strings.xml
            onLongClickLabel = "Go to note and edit", // TODO: add to strings.xml
        ) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                contentDescription = "Go to note", // TODO: add to strings.xml
            )
        }
    }
}

@Composable
fun SearchScreen(
    @Suppress("unused") logger: AppLogger,
    appViewModel: AppViewModel,
    navController: NavController,
    noteRepository: NoteRepository
) {
    val themeColors = appViewModel.state.themeColors
    val coroutineScope = rememberCoroutineScope()
    val viewModel = viewModel<SearchScreenViewModel>(
        factory = SearchScreenViewModelFactory(
            initialState = SearchScreenState(
                uiState = SearchUiState.Success(emptyList()),
                searchTextValue = TextFieldValue()
            )
        )
    )
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.fillMaxSize()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp)
        ) {
            OutlinedTextField(
                value = viewModel.state.searchTextValue,
                onValueChange = { textValue ->
                    viewModel.onEvent(
                        SearchScreenEvent.UiStateChange(
                            state =
                                if (textValue.text.isEmpty())
                                    SearchUiState.Success(emptyList())
                                else SearchUiState.Loading
                        )
                    )
                    viewModel.onEvent(SearchScreenEvent.SearchTextValueChange(textValue))

                    if (textValue.text.isNotEmpty()) {
                        coroutineScope.launch {
                            val value = withContext(Dispatchers.IO) {
                                noteRepository
                                    .getAll()
                                    .map { it.toNote() }
                                    .filter {
                                        it.text.contains(textValue.text, ignoreCase = true)
                                    }
                            }
                            viewModel.onEvent(
                                SearchScreenEvent.UiStateChange(
                                    SearchUiState.Success(value)
                                )
                            )
                        }
                    }
                },
                leadingIcon = {
                    IconButton(onClick = { navController.navigateUp() }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            tint = themeColors.textColor,
                            contentDescription = null
                        )
                    }
                },
                trailingIcon = {
                    IconButton(
                        onClick = {
                            viewModel.onEvent(
                                SearchScreenEvent.UiStateChange(
                                    SearchUiState.Success(emptyList())
                                )
                            )
                            viewModel.onEvent(
                                SearchScreenEvent.SearchTextValueChange(TextFieldValue())
                            )
                        }
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Close,
                            tint = themeColors.textColor,
                            contentDescription = null
                        )
                    }
                },
                placeholder = {
                    Text(
                        text = "Search notes" // TODO: Add to strings.xml
                    )
                },
                modifier = Modifier.fillMaxWidth()
            )
        }
        when (val uiState = viewModel.state.uiState) {
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
                            key = { (_, note) -> note.date }
                        ) { (index, note) ->
                            SearchItemRow(index, note, appViewModel, themeColors, navController)
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
}