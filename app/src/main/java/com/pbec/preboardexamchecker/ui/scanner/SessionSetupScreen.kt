package com.pbec.preboardexamchecker.ui.scanner

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.AccountTree
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.Info
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.hilt.navigation.compose.hiltViewModel
import com.pbec.preboardexamchecker.ui.clusters.ClusterViewModel
import com.pbec.preboardexamchecker.ui.theme.BrandTopAppBar

/**
 * A scan session grades against a whole cluster, not a single subject: the scanned subject bubble
 * routes each paper to its exam (see ScannerViewModel.loadClusterSession). The instructor only
 * picks the cluster and scan mode here.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SessionSetupScreen(
    viewModel: ScannerViewModel,
    onCreateCluster: () -> Unit,
    clusterViewModel: ClusterViewModel = hiltViewModel(),
) {
    val context = LocalContext.current
    val clusters by clusterViewModel.clusters.collectAsState()
    val clustersLoaded by clusterViewModel.clustersLoaded.collectAsState()
    var selectedClusterId by remember { mutableStateOf<Long?>(null) }
    var scanMode by remember { mutableStateOf(ScanSettings.getMode(context)) }
    var showNoClusterDialog by rememberSaveable { mutableStateOf(false) }

    LaunchedEffect(clustersLoaded, clusters.isEmpty()) {
        if (clustersLoaded) {
            showNoClusterDialog = clusters.isEmpty()
        }
    }

    if (showNoClusterDialog) {
        NoClusterDialog(
            onDismiss = { showNoClusterDialog = false },
            onCreateCluster = {
                showNoClusterDialog = false
                onCreateCluster()
            },
        )
    }

    Scaffold(
        contentWindowInsets = WindowInsets(0, 0, 0, 0),
        topBar = { BrandTopAppBar(title = "New Scan Session") },
    ) { innerPadding ->
    Column(
        modifier = Modifier.fillMaxSize().padding(innerPadding).padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Column {
            Text("Scan mode", style = MaterialTheme.typography.labelLarge)
            Spacer(Modifier.height(4.dp))
            SingleChoiceSegmentedButtonRow(modifier = Modifier.fillMaxWidth()) {
                SegmentedButton(
                    selected = scanMode == ScanMode.SINGLE,
                    onClick = { scanMode = ScanMode.SINGLE; ScanSettings.setMode(context, ScanMode.SINGLE) },
                    shape = SegmentedButtonDefaults.itemShape(index = 0, count = 2)
                ) { Text("1-Capture") }
                SegmentedButton(
                    selected = scanMode == ScanMode.TWO_PHASE,
                    onClick = { scanMode = ScanMode.TWO_PHASE; ScanSettings.setMode(context, ScanMode.TWO_PHASE) },
                    shape = SegmentedButtonDefaults.itemShape(index = 1, count = 2)
                ) { Text("2-Phase") }
            }
        }

        if (clusters.isEmpty()) {
            Text("Select Exam Cluster", style = MaterialTheme.typography.labelLarge)
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant,
                ),
            ) {
                Column(
                    modifier = Modifier.fillMaxWidth().padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                ) {
                    Icon(
                        imageVector = Icons.Rounded.AccountTree,
                        contentDescription = null,
                        modifier = Modifier.size(40.dp),
                        tint = MaterialTheme.colorScheme.primary,
                    )
                    Text(
                        text = if (clustersLoaded) "No exam clusters available" else "Loading exam clusters…",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold,
                        textAlign = TextAlign.Center,
                    )
                    if (clustersLoaded) {
                        OutlinedButton(onClick = { showNoClusterDialog = true }) {
                            Icon(Icons.Rounded.Info, contentDescription = null)
                            Spacer(Modifier.width(8.dp))
                            Text("What do I need?")
                        }
                    } else {
                        CircularProgressIndicator(modifier = Modifier.size(28.dp), strokeWidth = 3.dp)
                    }
                }
            }
            Spacer(Modifier.weight(1f))
        } else {
            Text("Select Exam Cluster", style = MaterialTheme.typography.labelLarge)
            LazyColumn(modifier = Modifier.weight(1f)) {
                items(clusters, key = { it.id }) { cluster ->
                    val isSelected = selectedClusterId == cluster.id
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp)
                            .clickable { selectedClusterId = cluster.id },
                        colors = CardDefaults.cardColors(
                            containerColor = if (isSelected)
                                MaterialTheme.colorScheme.primaryContainer
                            else
                                MaterialTheme.colorScheme.surfaceVariant
                        )
                    ) {
                        Column(Modifier.padding(16.dp)) {
                            Text(cluster.name, style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.Bold)
                            cluster.schoolYear?.let {
                                Text("SY $it", style = MaterialTheme.typography.bodySmall)
                            }
                        }
                    }
                }
            }
        }

        Button(
            onClick = {
                val id = selectedClusterId ?: return@Button
                viewModel.loadClusterSession(id, scanMode)
            },
            enabled = selectedClusterId != null,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Load Session")
        }
    }
    }
}

@Composable
private fun NoClusterDialog(
    onDismiss: () -> Unit,
    onCreateCluster: () -> Unit,
) {
    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = MaterialTheme.shapes.extraLarge,
            color = MaterialTheme.colorScheme.surface,
            tonalElevation = 6.dp,
        ) {
            Column(
                modifier = Modifier.fillMaxWidth().padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Surface(
                    modifier = Modifier.size(64.dp),
                    shape = MaterialTheme.shapes.extraLarge,
                    color = MaterialTheme.colorScheme.primaryContainer,
                ) {
                    Icon(
                        imageVector = Icons.Rounded.AccountTree,
                        contentDescription = null,
                        modifier = Modifier.padding(16.dp),
                        tint = MaterialTheme.colorScheme.primary,
                    )
                }

                Spacer(Modifier.height(18.dp))
                Text(
                    text = "Create an exam cluster first",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center,
                )
                Spacer(Modifier.height(10.dp))
                Text(
                    text = "A capture session needs one cluster containing an exam for Mathematics, ESAS, and Professional EE.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center,
                )
                Spacer(Modifier.height(20.dp))
                Button(
                    onClick = onCreateCluster,
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    Icon(Icons.Rounded.Add, contentDescription = null)
                    Spacer(Modifier.width(8.dp))
                    Text("Create Exam Cluster")
                }
                TextButton(
                    onClick = onDismiss,
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    Text("Not now")
                }
            }
        }
    }
}
