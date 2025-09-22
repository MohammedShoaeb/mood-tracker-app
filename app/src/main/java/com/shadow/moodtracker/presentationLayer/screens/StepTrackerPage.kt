package com.shadow.moodtracker.presentationLayer.screens

import android.os.Build
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.focusable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
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
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.Check
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.asIntState
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.text.isDigitsOnly
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.shadow.moodtracker.animation.EmptyStateCat
import com.shadow.moodtracker.animation.LoadingCat
import com.shadow.moodtracker.data.repository.StepCountsLegend
import com.shadow.moodtracker.data.repository.stepCountLegends
import com.shadow.moodtracker.presentationLayer.components.StatCardData
import com.shadow.moodtracker.presentationLayer.components.StatCardRow
import com.shadow.moodtracker.presentationLayer.components.StepTrackerChart
import com.shadow.moodtracker.utils.toHexString
import com.shadow.moodtracker.viewmodel.StepTrackerViewModel


@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StepTrackerPage(viewModel: StepTrackerViewModel = viewModel(),
                    navController: NavController) {

    var isSheetOpen by rememberSaveable { mutableStateOf(false) }
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val context = LocalContext.current

    val yearMonth by viewModel.yearMonth.collectAsState()

    val totalDaysInMonth by viewModel.totalDaysInMonth.collectAsState()
    var dayInput by rememberSaveable { mutableStateOf("") }
    val records by viewModel.stepTrackerCurrentMonth.collectAsState()
    val selectedDay by viewModel.selectedDay.asIntState()
    val selectedColor by viewModel.selectedColor.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()


    val isNoChange = remember {
        derivedStateOf {
            val existingColor = records.find { it.day == selectedDay.toString() }?.color
            (selectedColor == null && existingColor == null) ||
                    (selectedColor?.toHexString() == existingColor)
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .safeContentPadding(),
    ) {

        TopAppBar(modifier = Modifier.fillMaxWidth(),
            colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
            title = {
            },
            navigationIcon = {
                IconButton(onClick = {navController.popBackStack()}) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                }
            },
            actions = {  Text(modifier = Modifier.fillMaxWidth(),
                text = "Step Tracker",
                style = MaterialTheme.typography.titleMedium,
                textAlign = TextAlign.Center
            )}


        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .weight(1f)
                .background(MaterialTheme.colorScheme.surface)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.CenterHorizontally,
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
                    text = yearMonth.toString(),
                    textAlign = TextAlign.Center,
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onBackground,
                    fontWeight = FontWeight.Bold
                )
                Row(    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ){

                    IconButton(onClick = {
                        viewModel.navigateToPreviousMonth()
                    }) {

                        Icon(
                            Icons.AutoMirrored.Filled.KeyboardArrowLeft,
                            contentDescription = ""
                        )
                    }

                    TextButton(onClick = {viewModel.resetToCurrentMonth()}) {
                        Text("Today")
                    }

                    IconButton(onClick = {viewModel.navigateToNextMonth()}) {

                        Icon(
                            Icons.AutoMirrored.Filled.KeyboardArrowRight,
                            contentDescription = ""
                        )
                    }
                }
            }

                        if(isLoading){
                LoadingCat()

            }
            else {
             if(records.isEmpty()){


                 EmptyStateCat()
                 Text(
                     text = "You haven’t logged any steps yet.\nLet’s get moving, your journey starts today!",
                     style = MaterialTheme.typography.bodyMedium,
                     textAlign = TextAlign.Center,
                     color = MaterialTheme.colorScheme.onSurfaceVariant,
                     modifier = Modifier.padding(horizontal = 24.dp).padding(bottom = 24.dp)
                 )
             }else {
                 StepTrackerChart(records, days = totalDaysInMonth)

             }

            }
            Button(
                onClick = {
                    viewModel.setSelectedColor(null)
                    isSheetOpen = true
                },
                modifier = Modifier
                    .fillMaxWidth(0.5f)
                    .align(Alignment.End)
                    .padding(horizontal = 16.dp),
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primaryContainer,
                    contentColor = MaterialTheme.colorScheme.onPrimaryContainer)
            ) {
                Text("Add Steps")
            }


            Text(
                text = "Step Count Legends",
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.SemiBold,
                textAlign = TextAlign.Start,
                modifier = Modifier
                    .padding(start = 12.dp)
                    .fillMaxWidth()
                    .padding(bottom = 1.dp)
            )

            Card(
                modifier = Modifier
                                .padding(12.dp)
                                .fillMaxWidth(),
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.surfaceContainerLow
                            ),
                            border = BorderStroke(
                                width = 0.5.dp,
                                color = MaterialTheme.colorScheme.outlineVariant
                            ),
                            shape = RoundedCornerShape(16.dp),
                            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)

            ) {

                FlowRow(
                    modifier = Modifier
                        .padding(16.dp)
                        .fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly,

                    verticalArrangement = Arrangement.spacedBy(12.dp, Alignment.CenterVertically),
                    maxItemsInEachRow = 5
                ) {
                    stepCountLegends.forEach { legend ->
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center,
                            modifier = Modifier.padding(horizontal = 4.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(14.dp)
                                    .border(
                                        width = 0.5.dp,
                                        color = MaterialTheme.colorScheme.onSurface,
                                        shape = RoundedCornerShape(2.dp)
                                    )
                                    .background(
                                        legend.color ?: MaterialTheme.colorScheme.surfaceVariant,
                                        shape = RoundedCornerShape(2.dp)
                                    )
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = legend.label,
                                fontSize = 10.sp,
                                color = MaterialTheme.colorScheme.onSurface,
                                textAlign = TextAlign.Center
                            )
                        }
                    }
                }
            }


            val consistencyRate by viewModel.consistencyRate.collectAsState()

            Text(
                text = "Monthly Activity Overview",
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.SemiBold,
                textAlign = TextAlign.Start,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 6.dp, start = 12.dp)
            )
            StatCardRow(
                stats = listOf(
                    StatCardData(
                        title = "Total Days Tracked",
                        value = "${records.size}/${totalDaysInMonth}",
                        icon = Icons.Default.DateRange,
                        color = MaterialTheme.colorScheme.primaryContainer,
                        onColor = MaterialTheme.colorScheme.onPrimaryContainer
                    ),
                    StatCardData(
                        title = "Longest Streak",
                        value = "${viewModel.calculateLongestStreak(records)}",
                        icon = Icons.Default.Star,
                        color = MaterialTheme.colorScheme.secondaryContainer,
                        onColor = MaterialTheme.colorScheme.onSecondaryContainer
                    ),
                    StatCardData(
                        title = "Consistency Rate",
                        value = "$consistencyRate%",
                        icon = Icons.Default.CheckCircle,
                        color = MaterialTheme.colorScheme.tertiaryContainer,
                        onColor = MaterialTheme.colorScheme.onTertiaryContainer
                    ),
                )
            )



            val adviceList = listOf(
                "Tracking your steps helps you stay aware of your daily activity levels.",
                "Consistent step tracking encourages you to move more throughout the day.",
                "Walking regularly improves cardiovascular health and reduces heart disease risk.",
                "Monitoring steps motivates you to set achievable daily movement goals.",
                "Increased physical activity can help control weight and improve metabolism.",
                "Walking boosts your mood by releasing endorphins and reducing stress.",
                "Step tracking helps build healthy habits and long-term fitness routines.",
                "Regular walking improves muscle strength, balance, and coordination.",
                "Tracking allows you to recognize inactive periods and take action.",
                "Small increases in daily steps can have big health benefits over time.",
                "Walking aids digestion and promotes better sleep quality.",
                "Step tracking supports mental clarity and reduces anxiety.",
                "Monitoring progress increases motivation and celebrates your successes.",
                "Walking is a low-impact exercise accessible to most people.",
                "Step goals can be adjusted as your fitness improves for continuous growth.",
                "Tracking daily activity reduces risk of chronic diseases like diabetes.",
                "Walking outdoors exposes you to fresh air and sunlight, boosting vitamin D.",
                "Using colors to mark step ranges helps visually track your activity trends.",
                "Step tracking keeps you accountable and mindful of your health choices.",
                "Regular physical activity, even walking, improves longevity and quality of life."
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
                items(adviceList.size) { index ->
                    val tip = adviceList[index]
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
                        Text(text = tip, style = MaterialTheme.typography.bodyMedium)
                    }
                    HorizontalDivider()
                }
            }




            if (isSheetOpen) {

                ModalBottomSheet(
                    modifier = Modifier.focusable(true),
                    sheetState = sheetState,
                    onDismissRequest = {
                        dayInput = ""
                        viewModel.setSelectedDay(0)
                        viewModel.setSelectedColor(null)
                        isSheetOpen = false
                    }) {
                    val focusManager = LocalFocusManager.current
                    val focusRequester = remember { FocusRequester() }
                    Column(
                        modifier = Modifier

                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "How many steps did you take today?",
                            style = MaterialTheme.typography.titleLarge
                        )

                        Text(
                            text = "Select the color that matches your step count for today. Each color represents a step range.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )


                        var dayInputError by remember { mutableStateOf<String?>(null) }

                        OutlinedTextField(
                            modifier = Modifier.fillMaxWidth(),
                            value = dayInput,
                            onValueChange = { new ->
                                if (new.isEmpty()) {
                                    dayInput = new
                                    dayInputError = "Day cannot be empty"
                                } else if (new.isDigitsOnly()) {
                                    val dayInt = new.toIntOrNull()
                                    if (dayInt != null && dayInt in 1..totalDaysInMonth) {
                                        dayInput = new
                                        dayInputError = null
                                        viewModel.setSelectedDay(dayInt)
                                    } else {
                                        dayInputError = "Day must be between 1 and $totalDaysInMonth"
                                    }
                                }
                            },
                            isError = dayInputError != null,
                            label = { Text("Select Day (1 - $totalDaysInMonth)") }
                        )
                        if (dayInputError != null) {
                            Text(dayInputError!!, color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodySmall)
                        }



                        Text(
                            text = "Pick a color",
                            style = MaterialTheme.typography.titleSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier
                                .align(Alignment.Start)
                                .padding(bottom = 0.dp, top = 8.dp)
                        )
                        StepColorOptionsFlowRow(
                            stepCountLegends = stepCountLegends,
                            selectedColor = selectedColor,
                            onColorSelected = { viewModel.setSelectedColor(it) }
                        )

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(6.dp, Alignment.End),
                            verticalAlignment = Alignment.CenterVertically
                        ) {


                            OutlinedButton(
                                onClick = {
                                    dayInput = ""
                                    viewModel.setSelectedColor(null)
                                    viewModel.setSelectedDay(0)
                                    isSheetOpen = false
                                },
                                modifier = Modifier.weight(1f)

                            ) {
                                Text("Cancel")
                            }
                            Button(
                                onClick = {
                                    if (selectedColor != null) {
                                        viewModel.addStepTrackerRecord(
                                            day = selectedDay.toString(),
                                            color = selectedColor!!.toHexString(),
                                            onResult = {Toast.makeText(
                                                context,
                                                if (it) "Nice! Your steps for the day are saved."
                                                else "Hmm... Couldn't save your steps. Try again!",
                                                Toast.LENGTH_SHORT
                                            ).show()}
                                        )
                                    } else {
                                        viewModel.removeStepTrackerRecord(day = selectedDay.toString(),
                                            onResult = {
                                                Toast.makeText(
                                                    context,
                                                    if (it) "Step record deleted. You can log it again anytime!"
                                                    else "Failed to delete steps. Please try later.",
                                                    Toast.LENGTH_SHORT
                                                ).show()
                                            })

                                    }
                                    viewModel.setSelectedDay(0)
                                    dayInput = ""
                                    viewModel.setSelectedColor(null)

                                    isSheetOpen = false

                                },
                                enabled = (!isNoChange.value) && (viewModel.selectedDay.value != 0),
                                modifier = Modifier.weight(1f)

                            ) {
                                Text("Save")
                            }
                        }

                    }

                }
            }


        }
    }


}



@Composable
fun StepColorOptionsFlowRow(
    modifier: Modifier = Modifier,
    stepCountLegends: List<StepCountsLegend>,
    selectedColor: Color?,
    onColorSelected: (Color?) -> Unit,
) {
    FlowRow(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(6.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        stepCountLegends.forEach { (_, label, color) ->
            val isSelected = selectedColor == color

            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.width(64.dp)
            ) {
                Button(
                    onClick = { onColorSelected(color) },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = color ?: MaterialTheme.colorScheme.surfaceContainerHigh,
                        contentColor = if (color == null) MaterialTheme.colorScheme.onSurface else Color.White
                    ),
                    modifier = Modifier.size(48.dp),
                    elevation = ButtonDefaults.buttonElevation(
                        defaultElevation = if (isSelected) 6.dp else 2.dp
                    ),
                    shape = RoundedCornerShape(12.dp),
                    contentPadding = PaddingValues(0.dp)
                ) {
                    if (isSelected) {
                        Icon(
                            imageVector = Icons.Outlined.Check,
                            contentDescription = "Selected",
                            tint = if (color == null) MaterialTheme.colorScheme.onSurface else Color.Black
                        )
                    }
                }

                Text(
                    text = label,
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurface,
                    maxLines = 1,
                    softWrap = false,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(top = 4.dp)
                )
            }
        }
    }
}
