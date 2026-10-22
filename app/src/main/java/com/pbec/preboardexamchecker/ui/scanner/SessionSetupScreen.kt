package com.pbec.preboardexamchecker.ui.scanner

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.pbec.preboardexamchecker.ui.theme.BrandTopAppBar
import java.text.DateFormat
import java.util.Date

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SessionSetupScreen(
    viewModel: ScannerViewModel,
    onCreateCluster: () -> Unit,
) {
    val context = LocalContext.current
    val setup by viewModel.setup.collectAsState()
    var selectedClusterId by rememberSaveable { mutableStateOf<Long?>(null) }
    var scanMode by remember { mutableStateOf(ScanSettings.getMode(context)) }
    LaunchedEffect(viewModel) { viewModel.refreshSessionCatalog() }
    LaunchedEffect(setup.clusters) {
        if (setup.clusters.none { it.id == selectedClusterId }) selectedClusterId = null
    }
    val selected = setup.clusters.firstOrNull { it.id == selectedClusterId }
    val saved = setup.saved.firstOrNull { it.cluster.id == selectedClusterId }
    val preparing = setup.preparingId != null

    Scaffold(
        contentWindowInsets = WindowInsets(0, 0, 0, 0),
        topBar = { BrandTopAppBar(title = "New Scan Session") },
    ) { padding ->
        LazyColumn(
            modifier = Modifier.fillMaxSize().padding(padding).selectableGroup(),
            contentPadding = PaddingValues(24.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            item {
                Text("Scan mode", style = MaterialTheme.typography.labelLarge)
                Spacer(Modifier.height(4.dp))
                SingleChoiceSegmentedButtonRow(modifier = Modifier.fillMaxWidth()) {
                    SegmentedButton(
                        selected = scanMode == ScanMode.SINGLE,
                        onClick = { scanMode = ScanMode.SINGLE; ScanSettings.setMode(context, scanMode) },
                        shape = SegmentedButtonDefaults.itemShape(0, 2),
                        enabled = !preparing,
                    ) { Text("1-Capture") }
                    SegmentedButton(
                        selected = scanMode == ScanMode.TWO_PHASE,
                        onClick = { scanMode = ScanMode.TWO_PHASE; ScanSettings.setMode(context, scanMode) },
                        shape = SegmentedButtonDefaults.itemShape(1, 2),
                        enabled = !preparing,
                    ) { Text("2-Phase") }
                }
            }
            item {
                Text("Select Exam Cluster", style = MaterialTheme.typography.labelLarge)
                Text(
                    "Connect to the internet once to save this cluster’s exams, answer keys, and students.",
                    style = MaterialTheme.typography.bodyMedium,
                )
            }
            items(setup.clusters, key = { it.id }) { cluster ->
                val isSelected = selectedClusterId == cluster.id
                val local = setup.saved.firstOrNull { it.cluster.id == cluster.id }
                Card(
                    modifier = Modifier.fillMaxWidth().selectable(
                        selected = isSelected,
                        enabled = !preparing,
                        role = Role.RadioButton,
                        onClick = { selectedClusterId = cluster.id; viewModel.clearSetupMessage() },
                    ),
                    colors = CardDefaults.cardColors(containerColor = if (isSelected)
                        MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surfaceVariant),
                ) {
                    Row(Modifier.padding(16.dp)) {
                        RadioButton(selected = isSelected, onClick = null, enabled = !preparing)
                        Spacer(Modifier.width(12.dp))
                        Column {
                            Text(cluster.name, fontWeight = FontWeight.Bold)
                            cluster.schoolYear?.let { Text("SY $it", style = MaterialTheme.typography.bodySmall) }
                            Text(if (local == null) "Not saved" else "Ready offline",
                                style = MaterialTheme.typography.labelMedium)
                            local?.let {
                                Text("Saved ${DateFormat.getDateTimeInstance(DateFormat.MEDIUM, DateFormat.SHORT).format(Date(it.savedAt))}",
                                    style = MaterialTheme.typography.bodySmall)
                            }
                        }
                    }
                }
            }
            item {
                if (setup.loading) {
                    LinearProgressIndicator(Modifier.fillMaxWidth())
                    Text("Refreshing available clusters…", style = MaterialTheme.typography.bodySmall)
                }
                setup.message?.let { Text(it, style = MaterialTheme.typography.bodyMedium) }
                if (setup.clusters.isEmpty() && setup.serverLoaded) {
                    Text("No exam clusters available. Create a cluster with Mathematics, ESAS, and Professional EE.")
                    OutlinedButton(onClick = onCreateCluster, modifier = Modifier.fillMaxWidth()) {
                        Text("Create Exam Cluster")
                    }
                }
                if (setup.clusters.isEmpty() && !setup.loading && !setup.serverLoaded) {
                    Text("No usable session is saved on this device. Connect and prepare a cluster before scanning.")
                }
                TextButton(onClick = { viewModel.refreshSessionCatalog() },
                    enabled = !preparing && !setup.loading) { Text("Refresh cluster list") }
            }
            item {
                if (saved != null) {
                    Button(
                        onClick = { viewModel.loadClusterSession(saved.cluster.id, scanMode) },
                        enabled = !preparing,
                        modifier = Modifier.fillMaxWidth(),
                    ) { Text("Use Saved Session") }
                    Text("Saved results will sync when you’re back online.",
                        style = MaterialTheme.typography.bodySmall)
                    Spacer(Modifier.height(8.dp))
                }
                val prepareLabel = if (preparing) "Saving session…" else if (saved != null)
                    "Refresh saved session" else "Prepare for offline scanning"
                if (saved == null) {
                Button(
                    onClick = { selected?.let { viewModel.prepareOfflineSession(it.id) } },
                    enabled = selected != null && !preparing,
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    Text(prepareLabel)
                }
                } else {
                    OutlinedButton(
                        onClick = { selected?.let { viewModel.prepareOfflineSession(it.id) } },
                        enabled = selected != null && !preparing,
                        modifier = Modifier.fillMaxWidth(),
                    ) { Text(prepareLabel) }
                }
                if (preparing) {
                    LinearProgressIndicator(Modifier.fillMaxWidth())
                    Text("Saving all three exams, complete answer keys, and students…",
                        style = MaterialTheme.typography.bodySmall)
                }
            }
        }
    }
}
