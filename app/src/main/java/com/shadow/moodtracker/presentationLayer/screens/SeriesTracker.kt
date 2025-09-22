package com.shadow.moodtracker.presentationLayer.screens

import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeContentPadding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.shadow.moodtracker.R
import com.shadow.moodtracker.animation.EmptyStateMovie
import com.shadow.moodtracker.data.repository.Episode
import com.shadow.moodtracker.data.repository.Season
import com.shadow.moodtracker.data.repository.Series
import com.shadow.moodtracker.presentationLayer.components.SeriesDetailCard
import com.shadow.moodtracker.presentationLayer.reusableComponents.ConfirmationDialog
import com.shadow.moodtracker.viewmodel.SeriesTrackerViewModel
import kotlinx.coroutines.launch
import kotlin.math.roundToInt


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SeriesTrackerScreen(
    viewModel: SeriesTrackerViewModel,
    onSeriesClick: (String, Series) -> Unit,
    navController: NavController,
) {
    val series by viewModel.seriesList.collectAsState()
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val coroutineScope = rememberCoroutineScope()
    var showEditSheet by remember { mutableStateOf(false) }
    var showAddSheet by remember { mutableStateOf(false) }

    var seriesToDelete by remember { mutableStateOf<Pair<String, Series>?>(null) }
    var seriesToEdit by remember { mutableStateOf<Pair<String, Series>?>(null) }
val context = LocalContext.current

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                modifier = Modifier.fillMaxWidth(),
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                title = {
                    Text(
                        modifier = Modifier.fillMaxWidth(),
                        text = "Series Tracker",
                        style = MaterialTheme.typography.titleMedium,
                        textAlign = TextAlign.Center
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = MaterialTheme.colorScheme.onSurface
                        )
                    }
                },
                actions = {
                    Spacer(Modifier.width(48.dp))
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = {
                showAddSheet = true
            }) {
                Icon(Icons.Default.Add, contentDescription = "Add Series")
            }
        },
    ) { padding ->

        if (series.isEmpty()) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                EmptyStateMovie()
                Text(
                    "Looks like your series list is empty.\nHit the + button and add your favorites!",
                    style = MaterialTheme.typography.bodyLarge,
                    textAlign = TextAlign.Center
                )
            }
        } else {
            Column(modifier = Modifier.padding(padding)) {
                HeroSection()

                LazyColumn() {
                    items(series) { (id, item) ->
                        val total = item.seasons.sumOf { it.episodes.size }
                        val watched = item.seasons.sumOf { s -> s.episodes.count { it.watched } }
                        val progress = if (total > 0) watched / total.toFloat() else 0f
                        val isCompleted = watched == total && total > 0

                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp, vertical = 10.dp)
                                .clickable { onSeriesClick(id, item) },
                            shape = RoundedCornerShape(20.dp),
                            colors = CardDefaults.cardColors(
                                containerColor =
                                    if (isCompleted) MaterialTheme.colorScheme.primaryContainer.copy(
                                        alpha = 0.5f
                                    )
                                    else MaterialTheme.colorScheme.surfaceContainerHigh
                            )
                        ) {
                            Box(modifier = Modifier.fillMaxWidth()) {

                                Image(
                                    painter = painterResource(R.drawable.popcorn),
                                    contentDescription = null,
                                    modifier = Modifier
                                        .align(Alignment.BottomEnd)
                                        .size(100.dp)
                                        .offset(x = 20.dp, y = 20.dp)
                                        .clip(RoundedCornerShape(20.dp))
                                        .alpha(0.15f),
                                    contentScale = ContentScale.Crop
                                )

                                Column(modifier = Modifier.fillMaxWidth()) {
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(start = 16.dp, end = 16.dp, top = 16.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Box(
                                            modifier = Modifier
                                                .size(56.dp)
                                                .clip(RoundedCornerShape(12.dp))
                                                .background(
                                                    MaterialTheme.colorScheme.secondaryContainer.copy(
                                                        alpha = 0.5f
                                                    )
                                                ),
                                            contentAlignment = Alignment.BottomCenter
                                        ) {
                                            Icon(
                                                painter = painterResource(R.drawable.movie_icon),
                                                contentDescription = "Movie Thumbnail",
                                                tint = Color.Unspecified,
                                                modifier = Modifier.size(48.dp)
                                            )
                                        }

                                        Spacer(Modifier.width(16.dp))

                                        Column(modifier = Modifier.weight(1f)) {
                                            Row(
                                                verticalAlignment = Alignment.CenterVertically,
                                                horizontalArrangement = Arrangement.SpaceBetween,
                                                modifier = Modifier.fillMaxWidth()
                                            ) {
                                                Text(
                                                    text = item.name,
                                                    style = MaterialTheme.typography.titleMedium,
                                                    maxLines = 1,
                                                    overflow = TextOverflow.Ellipsis
                                                )

                                                if (isCompleted) {
                                                    Text(
                                                        text = "✓ Completed",
                                                        style = MaterialTheme.typography.labelMedium,
                                                        color = MaterialTheme.colorScheme.primary
                                                    )
                                                }
                                            }

                                            Spacer(Modifier.height(4.dp))

                                            LinearProgressIndicator(
                                                progress = { progress },
                                                modifier = Modifier
                                                    .fillMaxWidth()
                                                    .height(6.dp)
                                                    .clip(RoundedCornerShape(50)),
                                                color = MaterialTheme.colorScheme.primary,
                                                trackColor = MaterialTheme.colorScheme.onSurface.copy(
                                                    alpha = 0.1f
                                                ),
                                            )

                                            Spacer(Modifier.height(4.dp))

                                            Text(
                                                text = "Watched: $watched / $total episodes",
                                                style = MaterialTheme.typography.labelSmall,
                                                color = MaterialTheme.colorScheme.onSurfaceVariant
                                            )

                                        }

                                        Spacer(Modifier.width(12.dp))

                                        Icon(
                                            modifier = Modifier.size(36.dp),
                                            imageVector = Icons.Default.ChevronRight,
                                            contentDescription = "More details",
                                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    }
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(horizontal = 4.dp),
                                        horizontalArrangement = Arrangement.End,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        TextButton(
                                            onClick = {
                                                seriesToEdit = id to item
                                                showEditSheet = true
                                            }) {
                                            Text(
                                                "Edit",
                                                style = MaterialTheme.typography.labelMedium
                                            )
                                        }

                                        Spacer(modifier = Modifier.width(8.dp))

                                        TextButton(onClick = { seriesToDelete = id to item }) {
                                            Text(
                                                "Delete", color = MaterialTheme.colorScheme.error,
                                                style = MaterialTheme.typography.labelMedium
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }


        seriesToDelete?.let { (id, series) ->
            ConfirmationDialog(
                title = "Delete Series?",
                message = "Are you sure you want to delete '${series.name}'? This cannot be undone.",
                icon = painterResource(id = R.drawable.delete_icon),
                onConfirm = {
                    viewModel.deleteSeries(seriesId = id,
                        onResult={
                            Toast.makeText(
                                context,
                                if (it) "Series deleted successfully."
                                else "Failed to delete series. Please try again.",
                                Toast.LENGTH_SHORT
                            ).show()
                        })
                    seriesToDelete = null
                },
                onDismiss = {
                    seriesToDelete = null
                }
            )
        }


        if (showAddSheet) {
            ModalBottomSheet(
                modifier = Modifier.safeContentPadding(),
                onDismissRequest = {
                    coroutineScope.launch { sheetState.hide() }
                    showAddSheet = false
                },
                sheetState = sheetState
            ) {
                SeriesFormContent(
                    modalTitle = "Add New Series",
                    initialName = "",
                    initialStartDate = "",
                    initialEndDate = "",
                    initialSeasons = listOf(1 to 0),
                    isEditMode = false,
                    onSaveUpdateClick = { newSeries ->
                        viewModel.addSeries(newSeries,
                            onResult = { success ->
                                Toast.makeText(
                                    context,
                                    if (success) "Series added successfully!"
                                    else "Failed to add series. Please try again.",
                                    Toast.LENGTH_SHORT
                                ).show()
                            })
                        coroutineScope.launch { sheetState.hide() }
                        showAddSheet = false
                    },
                    onDismiss = {
                        coroutineScope.launch { sheetState.hide() }
                        showAddSheet = false
                    }
                )
            }
        }


        seriesToEdit?.let { (id, seriesItem) ->
            ModalBottomSheet(
                modifier = Modifier.safeContentPadding(),
                onDismissRequest = {
                    coroutineScope.launch { sheetState.hide() }
                    seriesToEdit = null
                    showEditSheet = false
                },
                sheetState = sheetState
            ) {
                val initialSeasonsForEdit = remember(seriesItem.seasons) {
                    seriesItem.seasons.map { it.seasonNumber to it.episodes.size }
                }

                SeriesFormContent(
                    modalTitle = "Edit Series",
                    initialName = seriesItem.name,
                    initialStartDate = seriesItem.dateStarted,
                    initialEndDate = seriesItem.dateFinished,
                    initialSeasons = initialSeasonsForEdit,
                    isEditMode = true,

                    originalSeries = seriesItem,

                    onSaveUpdateClick = { updatedSeries ->
                        viewModel.updateSeries(seriesId = id, updatedSeries = updatedSeries,
                            onResult = {
                                Toast.makeText(
                                    context,
                                    if (it) "Series updated successfully!"
                                    else "Failed to update series. Please try again.",
                                    Toast.LENGTH_SHORT
                                ).show()
                            })
                        coroutineScope.launch { sheetState.hide() }
                        seriesToEdit = null
                        showEditSheet = false

                    },
                    onDismiss = {
                        coroutineScope.launch { sheetState.hide() }
                        seriesToEdit = null
                        showEditSheet = false
                    }
                )
            }
        }
    }
}


private fun List<Season>.contentEquals(other: List<Season>): Boolean {
    if (this.size != other.size) return false
    this.forEachIndexed { index, season ->
        val otherSeason = other[index]
        if (season.seasonNumber != otherSeason.seasonNumber || season.episodes.size != otherSeason.episodes.size) {
            return false
        }
    }
    return true
}


@Composable
fun SeriesFormContent(
    modalTitle: String,
    initialName: String,
    initialStartDate: String,
    initialEndDate: String,
    initialSeasons: List<Pair<Int, Int>>,
    onSaveUpdateClick: (Series) -> Unit,
    onDismiss: () -> Unit,
    isEditMode: Boolean = false,
    originalSeries: Series? = null
) {
    var name by remember { mutableStateOf(initialName) }
    var startDate by remember { mutableStateOf(initialStartDate) }
    var endDate by remember { mutableStateOf(initialEndDate) }
    val seasons = remember { mutableStateListOf<Pair<Int, Int>>().apply { addAll(initialSeasons) } }
    var showDeleteDialogForIndex by remember { mutableStateOf<Int?>(null) }
    val context = LocalContext.current

    val nameFocusRequester = remember { FocusRequester() }
    val startDateFocusRequester = remember { FocusRequester() }
    val endDateFocusRequester = remember { FocusRequester() }
    val focusManager = LocalFocusManager.current
    val keyboardController = LocalSoftwareKeyboardController.current


    val hasChanges by remember(name, startDate, endDate, seasons) {
        derivedStateOf {
            if (!isEditMode || originalSeries == null) {

                true
            } else {
                val currentSeasonModels = seasons.map { (num, count) -> Season(num, List(count) { Episode() }) }
                val currentSeriesState = Series(
                    name = name.trim(),
                    dateStarted = startDate.trim(),
                    dateFinished = endDate.trim(),
                    seasons = currentSeasonModels
                )

                !(currentSeriesState.name == originalSeries.name &&
                        currentSeriesState.dateStarted == originalSeries.dateStarted &&
                        currentSeriesState.dateFinished == originalSeries.dateFinished &&

                        currentSeriesState.seasons == originalSeries.seasons)
            }
        }
    }


    Column(
        Modifier
            .padding(16.dp)
            .verticalScroll(rememberScrollState())
    ) {

        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp)
        ) {
            Icon(
                painter = painterResource(id = R.drawable.create_ic),
                contentDescription = "$modalTitle Icon",
                modifier = Modifier
                    .size(48.dp)
                    .padding(end = 12.dp),
                tint = Color.Unspecified
            )
            Text(modalTitle, style = MaterialTheme.typography.headlineSmall)
        }


        OutlinedTextField(
            value = name,
            onValueChange = { name = it },
            label = { Text("Series Name (required)") },
            singleLine = true,
            isError = name.isBlank(),
            modifier = Modifier
                .fillMaxWidth()
                .focusRequester(nameFocusRequester),
            keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Next),
            keyboardActions = KeyboardActions(
                onNext = { startDateFocusRequester.requestFocus() }
            )
        )
        if (name.isBlank()) {
            Text(
                "Name cannot be empty",
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.labelSmall
            )
        }

        Spacer(Modifier.height(12.dp))


        OutlinedTextField(
            value = startDate,
            onValueChange = { startDate = it },
            label = { Text("Start Date (e.g. 2025-06-01) (required)") },
            singleLine = true,
            modifier = Modifier
                .fillMaxWidth()
                .focusRequester(startDateFocusRequester),
            keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Next),
            keyboardActions = KeyboardActions(
                onNext = { endDateFocusRequester.requestFocus() }
            )
        )
        Spacer(Modifier.height(12.dp))


        OutlinedTextField(
            value = endDate,
            onValueChange = { endDate = it },
            label = { Text("End Date (optional)") },
            singleLine = true,
            modifier = Modifier
                .fillMaxWidth()
                .focusRequester(endDateFocusRequester),
            keyboardOptions = KeyboardOptions.Default.copy(
                imeAction = ImeAction.Next,
            ),
            keyboardActions = KeyboardActions(
                onNext = {
                    focusManager.clearFocus()
                    keyboardController?.hide()
                }
            )
        )

        Text("Seasons", style = MaterialTheme.typography.titleMedium)
        Text(
            "Add one or more seasons and specify episode count",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Spacer(Modifier.height(12.dp))

        seasons.forEachIndexed { index, (seasonNumber, episodeCount) ->
            var episodeInput by remember(index, episodeCount) {
                mutableStateOf(if (isEditMode && episodeCount == 0) "" else episodeCount.toString())
            }

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp),
                shape = RoundedCornerShape(10.dp),
                elevation = CardDefaults.cardElevation(2.dp),
            ) {
                Row(
                    Modifier
                        .fillMaxWidth()
                        .padding(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Season $seasonNumber", modifier = Modifier.weight(1f))

                    OutlinedTextField(
                        value = episodeInput,
                        onValueChange = { newValue ->
                            if (newValue.all { ch -> ch.isDigit() }) {
                                episodeInput = newValue
                                val newCount = newValue.toIntOrNull() ?: 0
                                seasons[index] = seasonNumber to newCount
                            }
                        },
                        label = { Text("Episodes") },
                        modifier = Modifier.weight(0.4f),
                        keyboardOptions = KeyboardOptions.Default.copy(
                            keyboardType = KeyboardType.Number,
                            imeAction = ImeAction.Done
                        ),
                        keyboardActions = KeyboardActions(onDone = { focusManager.clearFocus() })
                    )

                    IconButton(onClick = { showDeleteDialogForIndex = index }) {
                        Icon(
                            modifier = Modifier.size(24.dp),
                            imageVector = Icons.Filled.Delete,
                            contentDescription = "Delete Season",
                            tint = MaterialTheme.colorScheme.error
                        )
                    }
                }
            }
        }

        OutlinedButton(
            onClick = {
                val nextSeasonNum = if (seasons.isNotEmpty()) {
                    seasons.maxOf { it.first } + 1
                } else {
                    1
                }
                seasons.add(nextSeasonNum to 0)
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Icon(Icons.Default.Add, contentDescription = "Add Season")
            Spacer(Modifier.width(8.dp))
            Text("Add Season")
        }

        Spacer(Modifier.height(24.dp))

        Button(
            onClick = {
                val valid = name.isNotBlank() &&
                        startDate.isNotBlank() &&
                        seasons.all { it.second > 0 }

                if (valid) {
                    val seasonModels = seasons.map { (seasonNumber, count) ->
                        Season(seasonNumber, List(count) { Episode() })
                    }

                    val newSeries = Series(
                        name = name.trim(),
                        dateStarted = startDate.trim(),
                        dateFinished = endDate.trim(),
                        seasons = seasonModels
                    )
                    onSaveUpdateClick(newSeries)
                } else {
                    Toast.makeText(
                        context,
                        "Please fill all the fields.",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            },
            modifier = Modifier.fillMaxWidth(),

            enabled = name.isNotBlank() && startDate.isNotBlank() && seasons.all { it.second > 0 } && hasChanges
        ) {
            Text(if (isEditMode) "Update Series" else "Save Series")
        }

        Spacer(Modifier.height(8.dp))

        TextButton(
            onClick = onDismiss,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Close")
        }
    }


    showDeleteDialogForIndex?.let { index ->
        AlertDialog(
            onDismissRequest = { showDeleteDialogForIndex = null },
            confirmButton = {
                TextButton(onClick = {
                    seasons.removeAt(index)
                    showDeleteDialogForIndex = null
                    Toast.makeText(context, "Season Successfully Deleted!", Toast.LENGTH_SHORT).show()
                }) {
                    Text("Delete")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialogForIndex = null }) {
                    Text("Cancel")
                }
            },
            title = { Text("Delete Season?") },
            text = { Text("Are you sure you want to delete Season ${seasons[index].first}? This cannot be undone.") }
        )
    }
}



@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SeriesDetailsScreen(
    seriesId: String,
    viewModel: SeriesTrackerViewModel,
    navController: NavController,
) {
    val context = LocalContext.current
    val series by viewModel.seriesList.collectAsState()
    val seriesData = series.find { it.first == seriesId }?.second ?: return

    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val coroutineScope = rememberCoroutineScope()

    var selectedEpisode by remember { mutableStateOf<Triple<Int, Int, Episode>?>(null) }
    val total = seriesData.seasons.sumOf { it.episodes.size }
    val watched = seriesData.seasons.sumOf { it.episodes.count { it.watched } }
    val progress = if (total > 0) watched / total.toFloat() else 0f
    Column(modifier = Modifier.fillMaxWidth()) {

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
                    text = "${seriesData.name} Show",
                    style = MaterialTheme.typography.titleLarge,
                    textAlign = TextAlign.Center
                )
            }
        )



        SeriesDetailCard(
            seriesData = seriesData,
            movieImagePainter = { painterResource(id = R.drawable.watch_movie) }
        )

        Text(
            "Watching Progress",
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.padding(start = 16.dp, top = 8.dp)
        )

        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            shape = RoundedCornerShape(12.dp),
            elevation = CardDefaults.cardElevation(4.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.progress_ic),
                        contentDescription = "Progress Icon",
                        modifier = Modifier
                            .size(36.dp)
                            .padding(end = 8.dp),
                        tint = Color.Unspecified
                    )
                    Text(
                        "Your Progress",
                        style = MaterialTheme.typography.titleSmall
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                LinearProgressIndicator(
                    progress = { progress },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(8.dp)
                        .clip(RoundedCornerShape(8.dp)),
                    color = MaterialTheme.colorScheme.primary,
                    trackColor = MaterialTheme.colorScheme.surfaceContainer,
                )

                Spacer(modifier = Modifier.height(4.dp))
                val percent = (progress * 100).roundToInt()
                Text(
                    "$watched of $total episodes watched ($percent%)",
                    style = MaterialTheme.typography.labelSmall
                )

            }
        }


        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {

            items(seriesData.seasons) { season ->
                val seasonIndex =
                    seriesData.seasons.indexOfFirst { it.seasonNumber == season.seasonNumber }
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp)
                ) {
                    Text(
                        "Season ${season.seasonNumber}",
                        style = MaterialTheme.typography.labelMedium
                    )
                    Row(
                        Modifier
                            .fillMaxWidth()
                            .horizontalScroll(rememberScrollState())
                    ) {
                        season.episodes.forEachIndexed { epIndex, episode ->
                            Box(
                                modifier = Modifier
                                    .size(48.dp)
                                    .padding(4.dp)
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(
                                        if (episode.watched)
                                            MaterialTheme.colorScheme.primaryContainer
                                        else MaterialTheme.colorScheme.surfaceVariant
                                    )
                                    .clickable {
                                        selectedEpisode = Triple(seasonIndex, epIndex, episode)
                                        coroutineScope.launch { sheetState.show() }
                                    },
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = "${epIndex + 1}",
                                    color = if (episode.watched) MaterialTheme.colorScheme.onPrimaryContainer
                                    else MaterialTheme.colorScheme.onSurfaceVariant,
                                    style = MaterialTheme.typography.labelLarge
                                )
                            }


                        }

                    }

                    val episodesWithNotes = season.episodes.filter { it.note.isNotBlank() }

                    if (episodesWithNotes.isNotEmpty()) {
                        Spacer(modifier = Modifier.height(8.dp))
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Icon(
                                painter = painterResource(id = R.drawable.notes_ic),
                                contentDescription = "Progress Icon",
                                modifier = Modifier
                                    .size(24.dp)
                                    .padding(end = 8.dp),
                                tint = Color.Unspecified
                            )
                            Text("Notes", style = MaterialTheme.typography.labelLarge)
                        }
                        episodesWithNotes.forEach { ep ->
                            Text(
                                buildAnnotatedString {
                                    withStyle(SpanStyle(fontWeight = FontWeight.Bold)) {
                                        append("Episode ${season.episodes.indexOf(ep) + 1}: ")
                                    }
                                    append(ep.note)
                                },
                                style = MaterialTheme.typography.bodyMedium
                            )
                        }
                        HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)

                    }
                }
            }
        }
    }

    selectedEpisode?.let { (seasonIdx, epIdx, ep) ->
        ModalBottomSheet(
            onDismissRequest = { selectedEpisode = null },
            sheetState = sheetState
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp)
                    .verticalScroll(rememberScrollState())
                    .imePadding()
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 8.dp)
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.edit_ic),
                        contentDescription = "Edit Icon",
                        modifier = Modifier
                            .size(24.dp)
                            .padding(end = 8.dp),
                        tint = Color.Unspecified
                    )

                    Text(
                        text = "Edit Episode",
                        style = MaterialTheme.typography.headlineSmall
                    )
                }
                Text(
                    text = "Episode ${epIdx + 1} from Season ${seasonIdx + 1}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Spacer(modifier = Modifier.height(16.dp))

                var note by remember { mutableStateOf(ep.note) }
                var watched by remember { mutableStateOf(ep.watched) }

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 12.dp)
                ) {
                    Checkbox(
                        checked = watched,
                        onCheckedChange = { watched = it }
                    )
                    Text("Mark as Watched")
                }

                OutlinedTextField(
                    value = note,
                    onValueChange = { note = it },
                    label = { Text("Note (optional)") },
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(24.dp))

                Row(
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Button(
                        onClick = {
                            viewModel.updateEpisode(
                                seriesId,
                                seasonIdx,
                                epIdx,
                                Episode(watched, note)
                            ){
                                Toast.makeText(
                                    context,
                                    if (it) "Episode progress updated!"
                                    else "Failed to update episode progress. Try again.",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                            selectedEpisode = null
                        },
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("Save")
                    }

                    OutlinedButton(
                        onClick = {
                            selectedEpisode = null
                            coroutineScope.launch { sheetState.hide() }
                        },
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("Cancel")
                    }
                }
            }
        }
    }
}

@Composable
fun HeroSection() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(240.dp)
    ) {
        Image(
            painter = painterResource(id = R.drawable.hero_series_img),
            contentDescription = "Featured Series",
            contentScale = ContentScale.Fit,
            modifier = Modifier
                .fillMaxSize()
                .clip(RoundedCornerShape(bottomStart = 12.dp, bottomEnd = 12.dp))
        )


        Box(
            modifier = Modifier
                .fillMaxSize()
                .clip(RoundedCornerShape(bottomStart = 12.dp, bottomEnd = 12.dp))
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            Color.Transparent,
                            MaterialTheme.colorScheme.scrim.copy(alpha = 0.8f)
                        ),
                        startY = 100f
                    )
                )
        )


        Column(
            modifier = Modifier
                .align(Alignment.BottomStart)
                .padding(16.dp)
        ) {
            Text(
                text = "Featured Series",
                style = MaterialTheme.typography.headlineSmall.copy(color = Color.White),
                maxLines = 1
            )
            Text(
                text = "Continue watching now",
                style = MaterialTheme.typography.bodyMedium.copy(color = Color.White.copy(alpha = 0.8f))
            )
        }
    }
}


