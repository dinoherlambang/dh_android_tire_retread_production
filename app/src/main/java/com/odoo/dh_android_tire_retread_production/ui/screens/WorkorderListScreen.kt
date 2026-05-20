package com.odoo.dh_android_tire_retread_production.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.material3.Button
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.odoo.dh_android_tire_retread_production.AppContainer
import com.odoo.dh_android_tire_retread_production.data.model.QueueItem
import com.odoo.dh_android_tire_retread_production.ui.viewmodel.WorkorderListUiState
import com.odoo.dh_android_tire_retread_production.ui.viewmodel.WorkorderListViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WorkorderListScreen(
    container: AppContainer,
    onNavigateToDetail: (Int) -> Unit,
    onLogout: () -> Unit
) {
    val viewModel: WorkorderListViewModel = viewModel(factory = object : androidx.lifecycle.ViewModelProvider.Factory {
        override fun <T : androidx.lifecycle.ViewModel> create(modelClass: Class<T>): T {
            return WorkorderListViewModel(container.workorderRepository) as T
        }
    })

    val uiState by viewModel.uiState.collectAsState()
    val stationCode by container.sessionManager.stationCode.collectAsState(initial = "")
    var searchQuery by remember { mutableStateOf("") }

    LaunchedEffect(uiState) {
        if (uiState is WorkorderListUiState.NavigateToDetail) {
            val workorderId = (uiState as WorkorderListUiState.NavigateToDetail).workorderId
            onNavigateToDetail(workorderId)
            viewModel.resetNavigation()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Station: $stationCode") },
                actions = {
                    IconButton(onClick = { viewModel.loadWorkorders() }) {
                        Icon(Icons.Default.Refresh, contentDescription = "Refresh")
                    }
                    TextButton(onClick = onLogout) {
                        Text("Logout")
                    }
                }
            )
        }
    ) { innerPadding ->
        Column(modifier = Modifier.padding(innerPadding)) {
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                placeholder = { Text("Search WO / Serial / Paper WO") },
                trailingIcon = {
                    IconButton(onClick = { viewModel.resolveWorkorder(searchQuery) }) {
                        Icon(Icons.Default.Search, contentDescription = "Search")
                    }
                }
            )

            when (val state = uiState) {
                is WorkorderListUiState.Loading -> {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator()
                    }
                }
                is WorkorderListUiState.Success -> {
                    if (state.workorders.isEmpty()) {
                        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                            Text("Queue is empty")
                        }
                    } else {
                        LazyColumn {
                            items(state.workorders) { item ->
                                WorkorderItem(item = item, onClick = { onNavigateToDetail(item.workorder_id) })
                            }
                        }
                    }
                }
                is WorkorderListUiState.Error -> {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(state.message, color = MaterialTheme.colorScheme.error)
                            Button(onClick = { viewModel.loadWorkorders() }) {
                                Text("Retry")
                            }
                        }
                    }
                }
                else -> {}
            }
        }
    }
}

@Composable
fun WorkorderItem(item: QueueItem, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .clickable(onClick = onClick)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text(item.wo_number, style = MaterialTheme.typography.titleMedium)
                Text(item.station_status, style = MaterialTheme.typography.labelMedium)
            }
            Spacer(modifier = Modifier.height(4.dp))
            Text("Serial: ${item.serial_number ?: "N/A"}")
            Text("Customer: ${item.customer_name ?: "N/A"}")
            Text("Service: ${item.service_type ?: "N/A"}")
            Spacer(modifier = Modifier.height(4.dp))
            Text("Last Update: ${item.last_update}", style = MaterialTheme.typography.bodySmall)
        }
    }
}
