package com.sztorm.notecalendar.feature.search

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.sztorm.notecalendar.core.logging.AppLogger
import com.sztorm.notecalendar.domain.repositories.NoteRepository
import com.sztorm.notecalendar.feature.app.AppViewModel

@Composable
fun SearchScreen(
    @Suppress("unused") logger: AppLogger,
    appViewModel: AppViewModel,
    navController: NavController,
    noteRepository: NoteRepository
) {
    val themeColors = appViewModel.state.themeColors
    val viewModel = viewModel<SearchScreenViewModel>(
        factory = SearchScreenViewModelFactory(
            initialState = SearchScreenState(
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
                onValueChange = {
                    viewModel.onEvent(SearchScreenEvent.SearchTextValueChange(it))
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
        Spacer(modifier = Modifier.weight(1f))
        Text(
            text = "No results", // TODO: Add to strings.xml
            textAlign = TextAlign.Center,
            style = MaterialTheme.typography.bodyLarge,
        )
        Spacer(modifier = Modifier.weight(1f))
    }
}