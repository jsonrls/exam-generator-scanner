package com.pbec.preboardexamchecker.ui.programs

import android.content.ContentValues
import android.content.Context
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.FactCheck
import androidx.compose.material.icons.rounded.ChevronRight
import androidx.compose.material.icons.rounded.Description
import androidx.compose.material.icons.rounded.Download
import androidx.compose.material.icons.rounded.ElectricBolt
import androidx.compose.material.icons.rounded.TableChart
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.pbec.preboardexamchecker.R
import com.pbec.preboardexamchecker.ui.Screen
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

private const val ANSWER_SHEET_ASSET = "answer-sheet.pdf"
private const val ANSWER_SHEET_FILE_NAME = "answer-sheet.pdf"
private const val PDF_MIME_TYPE = "application/pdf"

private data class HomeOption(
    val title: String,
    val description: String,
    val icon: ImageVector,
    val featured: Boolean = false,
    val trailingIcon: ImageVector? = null,
    val onClick: () -> Unit
)

@Composable
fun ProgramsScreen(navController: NavController) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val unavailable: (String) -> Unit = { title ->
        Toast.makeText(context, "$title is not available yet.", Toast.LENGTH_SHORT).show()
    }
    val savePicker = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.CreateDocument(PDF_MIME_TYPE)
    ) { uri ->
        if (uri != null) {
            scope.launch {
                saveAnswerSheet(
                    context = context,
                    targetUri = uri,
                    successMessage = "Answer Sheet saved"
                )
            }
        }
    }
    val downloadAnswerSheet: () -> Unit = {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            scope.launch {
                try {
                    val uri = createAnswerSheetDownload(context)
                    saveAnswerSheet(
                        context = context,
                        targetUri = uri,
                        successMessage = "Answer Sheet downloaded"
                    )
                } catch (error: Exception) {
                    Toast.makeText(
                        context,
                        "Download failed: ${error.message ?: "Please try again."}",
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
        } else {
            savePicker.launch(ANSWER_SHEET_FILE_NAME)
        }
        Unit
    }
    val options = listOf(
        HomeOption(
            title = "Extreme Electrical Engineering",
            description = "Browse subjects and examination content",
            icon = Icons.Rounded.ElectricBolt,
            featured = true,
            trailingIcon = Icons.Rounded.ChevronRight,
            onClick = { navController.navigate(Screen.Subjects.route) }
        ),
        HomeOption(
            title = "Answer Sheet",
            description = "Download the official examination response form",
            icon = Icons.AutoMirrored.Rounded.FactCheck,
            trailingIcon = Icons.Rounded.Download,
            onClick = downloadAnswerSheet
        ),
        HomeOption(
            title = "Table of Specification (TOS)",
            description = "Review exam coverage and subject weighting",
            icon = Icons.Rounded.TableChart,
            onClick = { unavailable("Table of Specification") }
        ),
        HomeOption(
            title = "Handouts",
            description = "Browse shared review materials and practice files",
            icon = Icons.Rounded.Description,
            trailingIcon = Icons.Rounded.ChevronRight,
            onClick = { navController.navigate(Screen.Handouts.route) }
        )
    )

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 20.dp),
        contentPadding = PaddingValues(top = 28.dp, bottom = 32.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        item {
            HomeHeader(
                modifier = Modifier.padding(bottom = 14.dp)
            )
        }

        items(options, key = { it.title }) { option ->
            HomeOptionCard(option)
        }
    }
}

@Composable
private fun HomeHeader(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Image(
            painter = painterResource(R.mipmap.ic_launcher_foreground),
            contentDescription = "Extreme Electrical Engineering logo",
            modifier = Modifier.size(128.dp)
        )
        Text(
            text = "Resources & tools",
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 4.dp),
            color = MaterialTheme.colorScheme.onBackground,
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center
        )
        Text(
            text = "Choose a resource to get started.",
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 6.dp),
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            style = MaterialTheme.typography.bodyLarge,
            textAlign = TextAlign.Center
        )
    }
}

@Composable
private fun HomeOptionCard(
    option: HomeOption,
    modifier: Modifier = Modifier
) {
    val containerColor = if (option.featured) {
        MaterialTheme.colorScheme.primaryContainer
    } else {
        MaterialTheme.colorScheme.surface
    }
    val borderColor = if (option.featured) {
        MaterialTheme.colorScheme.primary.copy(alpha = 0.55f)
    } else {
        MaterialTheme.colorScheme.outlineVariant
    }

    Card(
        onClick = option.onClick,
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = containerColor,
            contentColor = MaterialTheme.colorScheme.onSurface
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = if (option.featured) 3.dp else 1.dp,
            pressedElevation = 0.dp
        ),
        border = BorderStroke(
            width = 1.dp,
            color = borderColor
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(min = 104.dp)
                .padding(horizontal = 18.dp, vertical = 18.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Surface(
                modifier = Modifier.size(52.dp),
                shape = RoundedCornerShape(16.dp),
                color = if (option.featured) {
                    MaterialTheme.colorScheme.primary
                } else {
                    MaterialTheme.colorScheme.primaryContainer
                }
            ) {
                Icon(
                    imageVector = option.icon,
                    contentDescription = null,
                    modifier = Modifier.padding(13.dp),
                    tint = if (option.featured) {
                        MaterialTheme.colorScheme.onPrimary
                    } else {
                        MaterialTheme.colorScheme.primary
                    }
                )
            }

            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(5.dp)
            ) {
                Text(
                    text = option.title,
                    color = MaterialTheme.colorScheme.onSurface,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold
                )
                Text(
                    text = option.description,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    style = MaterialTheme.typography.bodyMedium
                )
            }

            if (option.trailingIcon != null) {
                Icon(
                    imageVector = option.trailingIcon,
                    contentDescription = null,
                    modifier = Modifier.size(24.dp),
                    tint = if (option.featured) {
                        MaterialTheme.colorScheme.onSurfaceVariant
                    } else {
                        MaterialTheme.colorScheme.primary
                    }
                )
            } else {
                Surface(
                    shape = RoundedCornerShape(50),
                    color = MaterialTheme.colorScheme.surfaceVariant
                ) {
                    Text(
                        text = "SOON",
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 5.dp),
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 0.6.sp
                    )
                }
            }
        }
    }
}

@RequiresApi(Build.VERSION_CODES.Q)
private suspend fun createAnswerSheetDownload(context: Context): Uri =
    withContext(Dispatchers.IO) {
        val values = ContentValues().apply {
            put(MediaStore.Downloads.DISPLAY_NAME, ANSWER_SHEET_FILE_NAME)
            put(MediaStore.Downloads.MIME_TYPE, PDF_MIME_TYPE)
            put(MediaStore.Downloads.RELATIVE_PATH, Environment.DIRECTORY_DOWNLOADS)
            put(MediaStore.Downloads.IS_PENDING, 1)
        }

        context.contentResolver.insert(
            MediaStore.Downloads.EXTERNAL_CONTENT_URI,
            values
        ) ?: error("Unable to create the download.")
    }

private suspend fun saveAnswerSheet(
    context: Context,
    targetUri: Uri,
    successMessage: String
) {
    try {
        withContext(Dispatchers.IO) {
            context.assets.open(ANSWER_SHEET_ASSET).use { input ->
                context.contentResolver.openOutputStream(targetUri, "w")?.use { output ->
                    input.copyTo(output)
                } ?: error("Unable to open the selected location.")
            }

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                val values = ContentValues().apply {
                    put(MediaStore.Downloads.IS_PENDING, 0)
                }
                context.contentResolver.update(targetUri, values, null, null)
            }
        }
        Toast.makeText(context, successMessage, Toast.LENGTH_SHORT).show()
    } catch (error: Exception) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            context.contentResolver.delete(targetUri, null, null)
        }
        Toast.makeText(
            context,
            "Save failed: ${error.message ?: "Please try again."}",
            Toast.LENGTH_LONG
        ).show()
    }
}
