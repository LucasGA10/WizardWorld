package com.example.wizardworld.ui.components

import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.SearchBar
import androidx.compose.material3.SearchBarDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WizardSearchBar(
    searchQuery: String,
    placeholder: String,
    onSearchQueryChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    onCloseSearch: (() -> Unit)? = null,
) {
    SearchBar(
        inputField = {
            SearchBarDefaults.InputField(
                query = searchQuery,
                onQueryChange = onSearchQueryChange,
                onSearch = {},
                expanded = false,
                onExpandedChange = {},
                placeholder = { Text(text = placeholder) },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = null,
                    )
                },
                trailingIcon = {
                    if (searchQuery.isNotEmpty()) {
                        IconButton(
                            onClick = {
                                onSearchQueryChange("")
                            },
                        ) {
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = "Limpiar texto",
                            )
                        }
                    } else if (onCloseSearch != null) {
                        IconButton(onClick = onCloseSearch) {
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = "Cerrar búsqueda",
                            )
                        }
                    }
                },
            )
        },
        expanded = false,
        onExpandedChange = {},
        windowInsets = WindowInsets(0, 0, 0, 0),
        modifier =
            modifier
                .fillMaxWidth()
                .padding(end = 16.dp, top = 4.dp, bottom = 4.dp),
    ) {}
}
