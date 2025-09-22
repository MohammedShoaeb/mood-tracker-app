package com.shadow.moodtracker.presentationLayer.screens

import android.annotation.SuppressLint
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeContentPadding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.TagFaces
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.graphics.toColorInt
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.shadow.moodtracker.animation.LoadingCat
import com.shadow.moodtracker.model.MoodOption
import com.shadow.moodtracker.presentationLayer.components.AnxietyTrackerGraph
import com.shadow.moodtracker.presentationLayer.components.StatCardData
import com.shadow.moodtracker.presentationLayer.components.StatCardRow
import com.shadow.moodtracker.utils.toHexString
import com.shadow.moodtracker.viewmodel.AnxietyTrackerViewModel

@SuppressLint("DefaultLocale")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AnxietyTracker(
    viewModel: AnxietyTrackerViewModel = viewModel(),
    navController: NavController,
) {

    val yearMonth by viewModel.yearMonth.collectAsState()
    val totalDays by viewModel.totalDaysInMonth.collectAsState()
    val selectedDay by viewModel.selectedDay
    val firstDayOfMonthIndex = viewModel.firstDayOfMonthIndex
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    var isSheetOpen by rememberSaveable {
        mutableStateOf(false)
    }

    val context = LocalContext.current

    val isLoading by viewModel.isLoading.collectAsState()

    val notes by viewModel.randomAnxietyNotes.collectAsState()
    val recordsCurrentMonth by viewModel.monthlyRecords.collectAsState()
    var selectedColor by rememberSaveable { mutableStateOf<Color?>(null) }

    val anxietyOptions = listOf(
        MoodOption(null, "Empty"),
        MoodOption(Color(0xffB5EAD7), "None"),
        MoodOption(Color(0xffE2F0CC), "Low"),
        MoodOption(Color(0xffFFDAC0), "Medium"),
        MoodOption(Color(0xffFEB7B1), "High"),
        MoodOption(Color(0xffFF9AA2), "Severe")
    )
//    listOf(
//        MoodOption(null, "Empty"),
//        MoodOption(Color(0xffB5EAD7), "Happy"),
//        MoodOption(Color(0xffE2F0CC), "Calm"),
//        MoodOption(Color(0xffFFDAC0), "Neutral"),
//        MoodOption(Color(0xffFEB7B1), "Stressed"),
//        MoodOption(Color(0xffFF9AA2), "Angry")

    Column(
        modifier = Modifier
            .fillMaxSize()
            .safeContentPadding()
            .background(MaterialTheme.colorScheme.surface)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally,
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
                    text = "Anxiety Tracker",
                    style = MaterialTheme.typography.titleMedium,
                    textAlign = TextAlign.Center
                )
            }
        )

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(MaterialTheme.colorScheme.surfaceVariant)
                .padding(12.dp, bottom = 0.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {

            Text(
                text = yearMonth.toString(),
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

        Column(
            modifier = Modifier
                .padding(horizontal = 8.dp)
                .fillMaxWidth()
                .background(MaterialTheme.colorScheme.background),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            if (isLoading) {
                LoadingCat()
            } else
                AnxietyTrackerGraph(
            daysInMonth = totalDays,
            selectedDay = selectedDay,
            firstDayOfMonthIndex = firstDayOfMonthIndex,
            onDayClicked = { day -> viewModel.setSelectedDay(day) },
            onDaySelected = { day ->
                isSheetOpen = true
                val result = recordsCurrentMonth.find { it.day == (day).toString() }
                selectedColor = result?.anxietyLevel?.toColorInt()?.let { Color(it) }

            },
            records = recordsCurrentMonth
        )}




            Text(
                text = "Anxiety Color Legend",
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.SemiBold,
                textAlign = TextAlign.Start,                modifier = Modifier
                    .padding(start = 12.dp, top = 16.dp)
                    .fillMaxWidth()
                    .padding(bottom = 4.dp)
                    .align(Alignment.Start)

            )
            Card(
                modifier = Modifier
                    .padding(horizontal = 12.dp)
                    .fillMaxWidth()
            ) {

                Row(
                    modifier = Modifier
                        .padding(12.dp)
                        .fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceAround
                ) {
                    anxietyOptions.forEach { (color, label) ->
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceEvenly
                        ) {

                            Box(
                                modifier = Modifier
                                    .size(12.dp)
                                    .border(
                                        width = 0.4.dp,
                                        color = MaterialTheme.colorScheme.onSurface,
                                        shape = RoundedCornerShape(2.dp)
                                    )
                                    .background(
                                        color ?: MaterialTheme.colorScheme.surfaceContainerHigh,
                                        shape = RoundedCornerShape(2.dp)
                                    )
                            )
                            Spacer(modifier = Modifier.size(4.dp))
                            Text(text = label, fontSize = 10.sp)

                        }
                    }
                }
            }


            val stats = viewModel.calculateAnxietyStats()

        Text(
            text = "Monthly Activity Overview",
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.SemiBold,
            textAlign = TextAlign.Start,
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 16.dp, start = 12.dp)
        )
            StatCardRow(
                stats = listOf(
                    StatCardData(
                        title = "Frequency",
                        value = stats.mostFrequentFeeling,
                        icon = Icons.Filled.TagFaces,
                        color = MaterialTheme.colorScheme.primaryContainer,
                        onColor = MaterialTheme.colorScheme.onPrimaryContainer
                    ),
                    StatCardData(
                        title = "Days Tracked",
                        value = stats.totalDaysTracked.toString(),
                        icon = Icons.Filled.CheckCircle,
                        color = MaterialTheme.colorScheme.secondaryContainer,
                        onColor = MaterialTheme.colorScheme.onSecondaryContainer
                    ),
                    StatCardData(
                        title = "Calm Days",
                        value = stats.calmDaysCount.toString(),
                        icon = Icons.Filled.Star,
                        color = MaterialTheme.colorScheme.tertiaryContainer,
                        onColor = MaterialTheme.colorScheme.onTertiaryContainer
                    )
                )
            )


            Text(
                text = "Tips & Advice",
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.SemiBold,
                textAlign = TextAlign.Start,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 2.dp, horizontal = 16.dp)
            )
        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .height(300.dp)
                .padding(horizontal = 8.dp)
        ) {
            items(notes.size) { index ->
                val tip = notes[index]
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

            if (isSheetOpen) {
                ModalBottomSheet(
                    onDismissRequest = {
                        isSheetOpen = false
                    }, sheetState = sheetState
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        Text(
                            textAlign = TextAlign.Center,
                            text = "How are you feeling today?",
                            style = MaterialTheme.typography.titleLarge
                        )
                        Text(
                            textAlign = TextAlign.Center,
                            text = "Logging your anxiety level today can help you notice patterns and take better care of yourself over time.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    MoodColorOptionsFlowRow(
                        modifier = Modifier.padding(12.dp),
                        moodOptions = anxietyOptions,
                        selectedColor = selectedColor,
                        onColorSelected = { selectedColor = it }
                    )

                    val existingColor =
                        recordsCurrentMonth.find { it.day == selectedDay.toString() }?.anxietyLevel
                    val isNoChange =
                        (selectedColor == null && existingColor == null) || (selectedColor?.toHexString() == existingColor)


                    Row(
                        horizontalArrangement = Arrangement.spacedBy(24.dp),
                        modifier = Modifier.fillMaxWidth().padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        OutlinedButton(
                            onClick = {
                                isSheetOpen = false
                                selectedColor = null
//                                        viewModel.uploadGlobalAnxietyTrackerQ()
                            },
                            modifier = Modifier.weight(1f)

                        ) {
                            Text("Cancel")
                        }


                        Button(
                            onClick = {
                                if (selectedColor == null) {
                                    viewModel.deleteDayRecord(day = selectedDay.toString(),
                                        onResult = {
                                            Toast.makeText(
                                                context,
                                                if (it) "Anxiety record removed. You can always log it again later."
                                                else
                                                    "Failed to remove the record. Try again shortly.",
                                                Toast.LENGTH_SHORT
                                            ).show()
                                        })
                                } else {
                                    viewModel.saveAnxietyTrackerRecord(
                                        day = selectedDay.toString(),
                                        color = selectedColor!!.toHexString(),
                                        onResult = {
                                            Toast.makeText(
                                                context,
                                                if (it) "Logged successfully! Tracking helps you understand your anxiety better."
                                                else
                                                    "Something went wrong. Please try again to log your anxiety.",
                                                Toast.LENGTH_SHORT
                                            ).show()
                                        }
                                    )
                                }
                                isSheetOpen = false
                                selectedColor = null
                            }, enabled = !isNoChange,
                            modifier = Modifier.weight(1f)
                        ) {
                            Text("Save")
                        }
                    }
                }




            }

        }



    }





