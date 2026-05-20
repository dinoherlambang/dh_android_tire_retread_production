package com.odoo.dh_android_tire_retread_production.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.odoo.dh_android_tire_retread_production.AppContainer
import com.odoo.dh_android_tire_retread_production.data.model.MaterialLine
import com.odoo.dh_android_tire_retread_production.ui.viewmodel.WorkorderDetailUiState
import com.odoo.dh_android_tire_retread_production.ui.viewmodel.WorkorderDetailViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WorkorderDetailScreen(
    container: AppContainer,
    workorderId: Int,
    onBack: () -> Unit
) {
    val viewModel: WorkorderDetailViewModel = viewModel(factory = object : androidx.lifecycle.ViewModelProvider.Factory {
        override fun <T : androidx.lifecycle.ViewModel> create(modelClass: Class<T>): T {
            return WorkorderDetailViewModel(container.workorderRepository) as T
        }
    })

    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(workorderId) {
        viewModel.loadDetail(workorderId)
    }

    LaunchedEffect(uiState) {
        if (uiState is WorkorderDetailUiState.Done) {
            onBack()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("WO Detail: $workorderId") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { innerPadding ->
        when (val state = uiState) {
            is WorkorderDetailUiState.Loading -> {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            }
            is WorkorderDetailUiState.Success -> {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding)
                        .padding(16.dp)
                ) {
                    Text("Workorder: ${state.data.workorder.wo_number}", style = MaterialTheme.typography.headlineSmall)
                    Text("Station: ${state.data.station.name}", style = MaterialTheme.typography.titleMedium)
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    Text("Materials:", style = MaterialTheme.typography.titleMedium)
                    LazyColumn(modifier = Modifier.weight(1.0f)) {
                        items(state.data.materials) { material ->
                            MaterialItem(material)
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))
                    Button(
                        onClick = { viewModel.markAsDone(workorderId) },
                        modifier = Modifier.fillMaxWidth(),
                        enabled = state.data.workorder.can_done
                    ) {
                        Text("Mark as Done")
                    }
                }
            }
            is WorkorderDetailUiState.Error -> {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(state.message, color = MaterialTheme.colorScheme.error)
                        Spacer(modifier = Modifier.height(8.dp))
                        Button(onClick = { viewModel.loadDetail(workorderId) }) {
                            Text("Retry")
                        }
                    }
                }
            }
            else -> {}
        }
    }
}

@Composable
fun MaterialItem(material: MaterialLine) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(material.product_name, style = MaterialTheme.typography.bodyLarge)
                if (material.is_wastage) {
                    Text("WASTAGE", color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.labelSmall)
                }
            }
            Text("${material.quantity} ${material.uom}")
        }
    }
}
