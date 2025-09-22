package com.shadow.moodtracker.presentationLayer.screens

import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeContentPadding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.SheetState
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.shadow.moodtracker.R
import com.shadow.moodtracker.animation.EmptyStateCat
import com.shadow.moodtracker.animation.LoadingCat
import com.shadow.moodtracker.presentationLayer.reusableComponents.ConfirmationDialog
import com.shadow.moodtracker.viewmodel.HighlightOfTheDayScreenViewModel
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HighlightOfTheDaysPage(
    viewModel: HighlightOfTheDayScreenViewModel = viewModel(),
    navController: NavController,

    ) {
    val highlights by viewModel.highlightRecord.collectAsState()
    val totalDays by viewModel.totalDaysInMonth.collectAsState()
    val currentDay = viewModel.currentDay
    val todayYearMonth = SimpleDateFormat("yyyy-MM", Locale.getDefault()).format(Date())
    val currentYearMonth by viewModel.yearMonth.collectAsState()

    val showEmpty = highlights.isEmpty()
    val showCreateTill = highlights.size + 1

    var showDialog by remember { mutableStateOf(false) }
    var showDeleteDialog by remember { mutableStateOf(false) }

    var selectedDayForDialog by remember { mutableStateOf(0) }

    var editingDay by remember { mutableStateOf<Int?>(null) }
    var editingText by remember { mutableStateOf("") }
    var showEditDialog by remember { mutableStateOf(false) }

    var deletingDay by remember { mutableStateOf<Int?>(null) }

    val yearMonth by viewModel.yearMonth.collectAsState()
    val coroutineScope = rememberCoroutineScope()
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val snackbarHostState = remember { SnackbarHostState() }
    val context = LocalContext.current
    val isLoading by viewModel.isLoading.collectAsState()
    val listState = rememberLazyListState()

    LaunchedEffect(Unit) {
        listState.scrollToItem(currentDay - 1)
    }
    Column(
        modifier = Modifier
            .fillMaxSize()
            .safeContentPadding()
            .background(MaterialTheme.colorScheme.surfaceContainer),

        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        TopAppBar(
            modifier = Modifier.fillMaxWidth(),
            colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
            title = {},
            navigationIcon = {
                IconButton(onClick = { navController.popBackStack() }) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                }
            },
            actions = {
                Text(
                    modifier = Modifier.fillMaxWidth(),
                    text = "Highlight of the day",
                    style = MaterialTheme.typography.titleMedium,
                    textAlign = TextAlign.Center
                )
            })

        if (isLoading) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                LoadingCat()

            }
        } else {


            Surface(
                tonalElevation = 3.dp,

                color = MaterialTheme.colorScheme.surfaceVariant,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 0.dp, vertical = 1.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = yearMonth,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        IconButton(onClick = { viewModel.navigateToPreviousMonth() }) {
                            Icon(
                                Icons.AutoMirrored.Filled.KeyboardArrowLeft,
                                contentDescription = "Previous Month"
                            )
                        }
                        TextButton(onClick = { viewModel.resetToCurrentMonth() }) {
                            Text(
                                text = "Today",
                                style = MaterialTheme.typography.labelLarge,
                                fontWeight = FontWeight.Medium
                            )
                        }
                        IconButton(onClick = { viewModel.navigateToNextMonth() }) {
                            Icon(
                                Icons.AutoMirrored.Filled.KeyboardArrowRight,
                                contentDescription = "Next Month"
                            )
                        }
                    }
                }
            }
            HorizontalDivider()


            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    modifier = Modifier.size(40.dp),
                    painter = painterResource(R.drawable.notes_ic),
                    contentDescription = "",
                    tint = Color.Unspecified
                )
                Text(
                    text = "Here you can track and reflect on your highlight of each day this month. Tap the + to add a new highlight, or edit existing ones.",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp, horizontal = 12.dp),
                    textAlign = TextAlign.Start
                )
            }
            Spacer(modifier = Modifier.height(8.dp))


            if (showEmpty) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(32.dp),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                  EmptyStateCat()

                    Text(
                        text = "You don't have any highlights yet.\nStart by adding one!",
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color.Gray,
                        textAlign = TextAlign.Center
                    )
                    Spacer(modifier = Modifier.height(24.dp))
                    Button(onClick = {
                        viewModel.saveHighlight(currentDay, "", onResult = {})
                    }) {
                        Icon(Icons.Default.Add, contentDescription = null)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Add Highlight for Today")
                    }
                }
            } else {
                LazyColumn(
                    state = listState,
                    modifier = Modifier
                        .fillMaxSize()
                        .background(MaterialTheme.colorScheme.surfaceContainer)
                ) {
                    items(totalDays) { index ->
                        val day = index + 1
                        val record = highlights.find { it.day == day }
                        val isToday = (day == currentDay && currentYearMonth == todayYearMonth)


                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp, horizontal = 12.dp)
                                .animateItem(),
                            shape = RoundedCornerShape(12.dp),
                            border = BorderStroke(
                                1.dp,
                                color = if (isToday) MaterialTheme.colorScheme.outline else Color.Transparent
                            ),
                            elevation = CardDefaults.cardElevation(defaultElevation = if (isToday) 2.dp else 1.dp)
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .background(
                                        if (record != null)
                                            if (isToday) MaterialTheme.colorScheme.primaryContainer
                                            else MaterialTheme.colorScheme.secondaryContainer.copy(
                                                alpha = 0.4f
                                            )
                                        else
                                            MaterialTheme.colorScheme.surfaceContainerLow
                                    )
                                    .padding(12.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {

                                Column(modifier = Modifier.weight(1f)) {

                                    Row(verticalAlignment = Alignment.CenterVertically) {

                                        Text(
                                            text = "Day $day",
                                            style = MaterialTheme.typography.titleMedium.copy(
                                                fontWeight = FontWeight.Bold
                                            )
                                        )
                                        if (isToday) {
                                            Spacer(Modifier.width(8.dp))
                                            Surface(
                                                shape = RoundedCornerShape(50),
                                                color = MaterialTheme.colorScheme.primary,
                                                modifier = Modifier.padding(start = 4.dp)
                                            ) {
                                                Text(
                                                    "Today",
                                                    color = MaterialTheme.colorScheme.onPrimary,
                                                    style = MaterialTheme.typography.labelSmall,
                                                    modifier = Modifier.padding(
                                                        horizontal = 8.dp,
                                                        vertical = 2.dp
                                                    )
                                                )
                                            }
                                        }

                                    }

                                    Spacer(Modifier.height(4.dp))
                                    Text(
                                        text = record?.highlight?.takeIf { it.isNotBlank() }
                                            ?: "No highlight yet",
                                        style = MaterialTheme.typography.bodyMedium.copy(
                                            color = MaterialTheme.colorScheme.onSurface

                                        ),

                                    )
                                }


                                if (record != null) {
                                    Row {
                                        IconButton(
                                            onClick = {
                                                editingDay = day
                                                editingText = record.highlight
                                                showEditDialog = true
                                            },
                                            modifier = Modifier.size(36.dp)
                                        ) {
                                            Icon(Icons.Default.Edit, contentDescription = "Edit")
                                        }
                                        IconButton(
                                            onClick = {
                                                deletingDay = day
                                                showDeleteDialog = true

                                            },
                                            modifier = Modifier.size(36.dp)
                                        ) {
                                            Icon(
                                                Icons.Default.Delete,
                                                contentDescription = "Delete"
                                            )
                                        }
                                    }
                                } else {
                                    IconButton(
                                        onClick = {
                                            selectedDayForDialog = day
                                            showDialog = true
                                            coroutineScope.launch { sheetState.show() }
                                        },
                                        modifier = Modifier.size(36.dp)
                                    ) {
                                        Icon(
                                            Icons.Default.Add,
                                            contentDescription = "Add Highlight"
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }



            if (showEditDialog && editingDay != null) {
                CreateHighlightBottomSheet(
                    day = editingDay!!,
                    initialText = editingText,
                    onDismiss = {
                        coroutineScope.launch { sheetState.hide() }
                        showEditDialog = false
                        editingDay = null
                        editingText = ""
                    },
                    onSave = { day, text ->
                        viewModel.saveHighlight(day, text, onResult = {
                            if (it)
                                Toast.makeText(
                                    context,
                                    "Highlight Updated Successfully!",
                                    Toast.LENGTH_SHORT
                                ).show()
                            else
                                Toast.makeText(
                                    context,
                                    "Failed to Update Highlight!",
                                    Toast.LENGTH_SHORT
                                ).show()

                        })
                        coroutineScope.launch { sheetState.hide() }
                        showEditDialog = false
                    },
                    sheetState = sheetState
                )
            }


        }
    }
    if (showDeleteDialog) {

        ConfirmationDialog(
            title = "Delete Highlight for Day $deletingDay",
            message = "Are you sure you want to delete this highlight?",
            confirmButtonText = "Delete",
            dismissButtonText = "Cancel",
            onConfirm = {
                viewModel.deleteDayHighLight(deletingDay, onResult = {
                    if (it)
                        Toast.makeText(
                            context,
                            "Highlight Deleted Successfully!",
                            Toast.LENGTH_SHORT
                        ).show()
                    else
                        Toast.makeText(context, "Failed to Delete Highlight!", Toast.LENGTH_SHORT)
                            .show()
                    showDeleteDialog = false
                })
            },
            onDismiss = { showDeleteDialog = false }
        )

    }

    if (showDialog) {
        HighlightInputBottomSheet(
            day = selectedDayForDialog,
            initialText = "",
            onDismiss = {
                coroutineScope.launch { sheetState.hide() }
                showDialog = false
            },
            onConfirm = { text ->
                viewModel.saveHighlight(selectedDayForDialog, text, onResult = {
                    if (it)
                        Toast.makeText(
                            context,
                            "Highlight Updated Successfully!",
                            Toast.LENGTH_SHORT
                        ).show()
                    else
                        Toast.makeText(context, "Failed to Update Highlight!", Toast.LENGTH_SHORT)
                            .show()

                })
                showDialog = false
            },
            sheetState = sheetState
        )
    }


}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateHighlightBottomSheet(
    day: Int,
    onDismiss: () -> Unit,
    onSave: (Int, String) -> Unit,
    initialText: String = "",
    sheetState: SheetState,
) {
    var text by remember { mutableStateOf(initialText) }
    val isSaveEnabled = text.isNotBlank()
    val coroutineScope = rememberCoroutineScope()

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        shape = RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp),
        tonalElevation = 8.dp,
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier
                .padding(24.dp)
                .fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                text = "Highlight for Day $day",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.SemiBold
            )

            OutlinedTextField(
                value = text,
                onValueChange = { text = it },
                label = { Text("Highlight") },
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(min = 100.dp),
                maxLines = 5,
                singleLine = false,
                keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Done),
                keyboardActions = KeyboardActions(onDone = {
                    if (isSaveEnabled) {
                        onSave(day, text.trim())
                        coroutineScope.launch { sheetState.hide() }
                        onDismiss()
                    }
                }),
                textStyle = MaterialTheme.typography.bodyMedium
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End,
                verticalAlignment = Alignment.CenterVertically
            ) {
                TextButton(onClick = {
                    coroutineScope.launch { sheetState.hide() }
                    onDismiss()
                }) {
                    Text("Cancel")
                }

                Spacer(modifier = Modifier.width(12.dp))

                Button(
                    onClick = {
                        onSave(day, text.trim())
                        coroutineScope.launch { sheetState.hide() }
                        onDismiss()
                    },
                    enabled = isSaveEnabled
                ) {
                    Text("Save")
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HighlightInputBottomSheet(
    day: Int,
    initialText: String = "",
    onDismiss: () -> Unit,
    onConfirm: (String) -> Unit,
    sheetState: SheetState,
) {
    var text by remember { mutableStateOf("") }
    val isSaveEnabled = text.isNotBlank() && text != initialText
    val coroutineScope = rememberCoroutineScope()

    ModalBottomSheet(
        onDismissRequest = {
            coroutineScope.launch { sheetState.hide() }
            onDismiss()
        },
        sheetState = sheetState,
        shape = RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp),
        tonalElevation = 8.dp,
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier
                .padding(24.dp)
                .fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                text = "Highlight for Day $day",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.SemiBold
            )

            TextField(
                value = text,
                onValueChange = { text = it },
                placeholder = { Text("Write your highlight...") },
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(min = 100.dp),
                maxLines = 5,
                singleLine = false,
                keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Done),
                keyboardActions = KeyboardActions(onDone = {
                    if (isSaveEnabled) {
                        onConfirm(text.trim())
                        coroutineScope.launch { sheetState.hide() }
                        onDismiss()
                    }
                }),
                textStyle = MaterialTheme.typography.bodyMedium

            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End,
                verticalAlignment = Alignment.CenterVertically
            ) {
                TextButton(onClick = {
                    coroutineScope.launch { sheetState.hide() }
                    onDismiss()
                }) {
                    Text("Cancel")
                }

                Spacer(modifier = Modifier.width(12.dp))

                Button(
                    onClick = {
                        onConfirm(text.trim())
                        coroutineScope.launch { sheetState.hide() }
                        onDismiss()
                    },
                    enabled = isSaveEnabled
                ) {
                    Text("Save")
                }
            }
        }
    }
}


