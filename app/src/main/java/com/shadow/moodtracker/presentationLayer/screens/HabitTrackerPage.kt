package com.shadow.moodtracker.presentationLayer.screens

import android.util.Log
import android.widget.Toast
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeContentPadding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.automirrored.twotone.PlaylistAdd
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.outlined.EmojiEvents
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.shadow.moodtracker.R
import com.shadow.moodtracker.animation.HabitCat
import com.shadow.moodtracker.animation.LoadingCat
import com.shadow.moodtracker.data.repository.Habit
import com.shadow.moodtracker.presentationLayer.components.AddHabitForm
import com.shadow.moodtracker.presentationLayer.components.HabitEditScreen
import com.shadow.moodtracker.presentationLayer.components.HabitLogEntry
import com.shadow.moodtracker.presentationLayer.components.HabitProgressEmptyState
import com.shadow.moodtracker.presentationLayer.components.MonthlySummaryCard
import com.shadow.moodtracker.presentationLayer.components.SummaryRowData
import com.shadow.moodtracker.presentationLayer.components.WheelHabitsNew
import com.shadow.moodtracker.viewmodel.HabitTrackerViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HabitPage(
    viewModel: HabitTrackerViewModel = viewModel(),
    navControllers: NavController,
) {
    val today = viewModel.todayDay.collectAsState().value


    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    var isSheetOpen by rememberSaveable {
        mutableStateOf(false)
    }
    val totalDaysInMonth = viewModel.totalDaysInMonth.collectAsState()
    val yearMonth = viewModel.yearMonth.collectAsState()

    val habitList by viewModel.habitList.observeAsState()

    val habitProgressList by viewModel.habitProgressList.collectAsState()
    val context = LocalContext.current

    val scrollState = rememberScrollState()

    val showEditSheet = remember { mutableStateOf(false) }
    val editBottomSheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    val showLogEntrySheet = remember { mutableStateOf(false) }
    val logEntrySheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    val overallCompletionRate by viewModel.overallCompletionRate.collectAsState()
    val daysAllHabitsCompleted by viewModel.daysAllHabitsCompleted.collectAsState()
    val mostConsistentHabit by viewModel.mostConsistentHabit.collectAsState()
    val tips by viewModel.tipsList.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    Log.d("tips List", tips.toString())
    Column(
        modifier = Modifier
            .fillMaxSize(1f)
            .background(MaterialTheme.colorScheme.background)
            .safeContentPadding()
    ) {
        TopAppBar(
            modifier = Modifier.fillMaxWidth(),
            colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
            title = {},
            navigationIcon = {
                IconButton(onClick = { navControllers.popBackStack() }) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                }
            },
            actions = {
                Text(
                    modifier = Modifier.fillMaxWidth(),
                    text = "Wheel Of Habits",
                    style = MaterialTheme.typography.titleMedium,
                    textAlign = TextAlign.Center
                )
            }


        )
        Column(
            modifier = Modifier
                .background(MaterialTheme.colorScheme.background, shape = RoundedCornerShape(24.dp))
                .verticalScroll(scrollState),
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.surfaceVariant)
                    .padding(12.dp, bottom = 0.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {

                Text(
                    text = yearMonth.value,
                    textAlign = TextAlign.Center,
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onBackground,
                    fontWeight = FontWeight.Bold
                )
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {

                    IconButton(onClick = {
                        viewModel.navigateToPreviousMonth()
                    }) {

                        Icon(
                            Icons.AutoMirrored.Filled.KeyboardArrowLeft, contentDescription = ""
                        )
                    }

                    TextButton(onClick = { viewModel.resetToCurrentMonth() }) {
                        Text("Today")
                    }

                    IconButton(onClick = { viewModel.navigateToNextMonth() }) {

                        Icon(
                            Icons.AutoMirrored.Filled.KeyboardArrowRight, contentDescription = ""
                        )
                    }
                }
            }

            Box(
                modifier = Modifier
                    .aspectRatio(1f)

                    .padding(8.dp)
                    .background(MaterialTheme.colorScheme.surfaceContainer)
                    .border(
                        border = if (isLoading) BorderStroke(0.dp, Color.Transparent)
                        else BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
                    ),
                contentAlignment = Alignment.Center
            ) {
                when {
                    isLoading -> {
                        LoadingCat()
                    }

                    habitProgressList.isEmpty() -> {
                        HabitProgressEmptyState()
                    }

                    else -> {
                        TextButton(
                            onClick = { showEditSheet.value = true },
                            modifier = Modifier.align(Alignment.TopStart)
                        ) {
                            Icon(
                                Icons.Default.Settings,
                                contentDescription = "Edit Habit",
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(Modifier.width(1.dp))
                            Text(
                                text = "Edit/Delete",
                                fontSize = MaterialTheme.typography.labelSmall.fontSize
                            )
                        }

                        HabitCat(modifier = Modifier.size(124.dp))

                        WheelHabitsNew(
                            data = habitProgressList,
                            days = totalDaysInMonth.value,
                            today =today
                        )
                    }
                }
            }



            Row(
                modifier = Modifier
                    .fillMaxWidth(0.95f)
                    .padding(top = 4.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                FilledTonalButton(
                    onClick = { isSheetOpen = true },
                    enabled = habitList?.size != 8,
                    modifier = Modifier
                        .weight(1f)
                        .alpha(0.75f)
                ) {
                    Icon(
                        Icons.Default.Add,
                        contentDescription = "Add Habit",
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(Modifier.width(6.dp))
                    Text(
                        text = "Add Habit (${habitList?.size ?: 0}/8)",
                        fontSize = MaterialTheme.typography.labelSmall.fontSize
                    )
                }

                OutlinedButton(
                    onClick = {
                        showLogEntrySheet.value = true

                    },
                    modifier = Modifier.weight(1f)
                ) {
                    Icon(
                        Icons.AutoMirrored.TwoTone.PlaylistAdd,
                        contentDescription = "Log Progress",
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(Modifier.width(6.dp))
                    Text("Add Progress", fontSize = MaterialTheme.typography.labelSmall.fontSize)
                }

            }



            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),

            ) {
                Text(
                    modifier = Modifier.padding(horizontal = 16.dp),
                    text = "Monthly Activity OverView",
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.SemiBold,
                    textAlign = TextAlign.Start,
                )

                val summaryOverview = listOf(
                    SummaryRowData(
                        icon = painterResource(id = R.drawable.total_ic),
                        label = "Overall Completion",
                        value = overallCompletionRate
                    ),
                    SummaryRowData(
                        icon = painterResource(id = R.drawable.best_ic),
                        label = "Days All Habits Done",
                        value = "$daysAllHabitsCompleted Days"
                    ),
                    SummaryRowData(
                        icon = painterResource(R.drawable.perfectweek_ic),
                        label = "Most Consistent Habit",
                        value = mostConsistentHabit?.let { "${it.habit.name} (${it.streakLength}d)" }
                            ?: "-"
                    )
                )
                MonthlySummaryCard(
                    title = "Your Habit Summary",
                    summaryRows = summaryOverview,
                    decorativeImagePainter = painterResource(id = R.drawable.deco_habit),
                    decorativeImageContentDescription = "Habit Summary Background"
                )

            }


            habitList?.let { list ->
                if (list.isNotEmpty()) {
                    HabitRewardSection(habitList = list)
                }
            }
            Text(
                text = "Tips & Advice",
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.SemiBold,
                textAlign = TextAlign.Start,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 2.dp, horizontal = 16.dp)
            )

            if (isSheetOpen) {

                ModalBottomSheet(
                    onDismissRequest = {
                        isSheetOpen = false
                    },
                    sheetState = sheetState
                ) {

                    AddHabitForm(
                        onSave = { name, reward, colorHex ->
                            val newHabit = Habit(
                                name = name,
                                reward = reward,
                                color = colorHex
                            )
                            viewModel.addHabits(
                                habit = newHabit,
                                onResult = {
                                    Toast.makeText(
                                        context,
                                        if (it) "Habit added successfully! Let’s make progress!"
                                        else "Failed to add habit. Please try again.",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                })
                            isSheetOpen = false
                        },
                        onCancel = { isSheetOpen = false }
                    )

                }
            }



            if (showEditSheet.value) {
                ModalBottomSheet(
                    onDismissRequest = { showEditSheet.value = false },
                    sheetState = editBottomSheetState
                ) {
                    HabitEditScreen(
                        habitProgressList = habitProgressList,
                        onDeleteHabit = { habitToDelete ->
                            viewModel.deleteHabit(habitToDelete) {

                                Toast.makeText(
                                    context,
                                    if (it) "Habit deleted successfully."
                                    else "Failed to delete habit. Please try again.",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        },
                        onSaveHabit = { updatedHabit ->
                            viewModel.updateHabit(
                                updatedHabit = updatedHabit,
                                onResult = {
                                    Toast.makeText(
                                        context,
                                        if (it) "Habit updated successfully! Keep it up!"
                                        else "Failed to update habit. Please try again.",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                })
                            showEditSheet.value = false
                        },
                        onCancelEdit = { showEditSheet.value = false }
                    )
                }

            }

                        if (showLogEntrySheet.value)
                ModalBottomSheet(
                    onDismissRequest = { showLogEntrySheet.value = false },
                    sheetState = logEntrySheetState
                ) {
                    HabitLogEntry(
                        totalDaysInMonth = totalDaysInMonth.value,
                        habitProgressList = habitProgressList,
                        onCancel = { showLogEntrySheet.value = false },
                        onSaveLog = { habit, day ->
                            viewModel.saveProgress(
                                habitId = habit.id,
                                day = day,
                                onResult = {
                                    Toast.makeText(
                                        context,
                                        if (it) "Habit recorded successfully! Keep it up!"
                                        else "Oops! Failed to record habit. Please try again.",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                })
                            showLogEntrySheet.value = false
                        }
                    )

                }

            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(300.dp)
                    .padding(horizontal = 8.dp)
            ) {
                items(tips.size) { index ->
                    val tip = tips[index]
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(12.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Info,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.tertiaryContainer,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(text = tip.note, style = MaterialTheme.typography.bodyMedium)
                    }
                    HorizontalDivider()
                }
            }
        }


    }


}



@OptIn(ExperimentalFoundationApi::class)
@Composable
fun HabitRewardSection(habitList: List<Habit>) {
    val sortedHabits = remember(habitList) { habitList.sortedBy { it.order } }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(12.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {

        Text(
            text = "Your Habits & Rewards",
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.SemiBold,
            textAlign = TextAlign.Start,
        )

        Card(
            shape = RoundedCornerShape(12.dp),

            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surfaceContainerHigh.copy(0.75f),

                )
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp)
                    .animateContentSize(animationSpec = tween(durationMillis = 300))
            ) {
                if (sortedHabits.isEmpty()) {
                    Text(
                        text = "No habits set yet! Start adding some to earn rewards.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(

                            vertical = 8.dp
                        )
                    )
                } else {
                    LazyVerticalGrid(
                        columns = GridCells.Fixed(1),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(200.dp),
                        contentPadding = PaddingValues(2.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(sortedHabits) { habit ->

                            HabitItemCard(habit = habit) {

                                println("Habit clicked: ${habit.name}")
                            }
                        }
                    }
                }
            }
        }
    }
}



@Composable
fun HabitItemCard(habit: Habit, onClick: () -> Unit) {

    Row(
        modifier = Modifier
            .clickable(onClick = onClick)
            .padding(8.dp)
            .height(IntrinsicSize.Min),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {

        Box(
            modifier = Modifier
                .size(24.dp)
                .clip(CircleShape)
                .background(
                    brush = Brush.linearGradient(
                        colors = listOf(
                            MaterialTheme.colorScheme.primaryContainer.copy(1f),
                            MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.001f),
                        )
                    )
                ),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "${habit.order + 1}",
                style = MaterialTheme.typography.labelSmall,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onPrimaryContainer
            )
        }

        Spacer(modifier = Modifier.width(8.dp))


        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = habit.name,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSurface,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )
            Text(
                text = "🎁 ${habit.reward}",
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.8f)
            )
        }

        Spacer(modifier = Modifier.width(8.dp))


        Box(
            modifier = Modifier
                .size(36.dp)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.tertiaryContainer.copy(alpha = 0.2f)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Outlined.EmojiEvents,
                contentDescription = "Reward Icon",
                tint = MaterialTheme.colorScheme.tertiaryContainer,
                modifier = Modifier.size(24.dp)
            )
        }
    }
}

