package com.shadow.moodtracker.presentationLayer.screens

import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.shadow.moodtracker.R
import com.shadow.moodtracker.animation.EmptyStateCat
import com.shadow.moodtracker.animation.LoadingCat
import com.shadow.moodtracker.presentationLayer.components.MonthlySummaryCard
import com.shadow.moodtracker.presentationLayer.components.SummaryRowData
import com.shadow.moodtracker.viewmodel.CoffeeTrackerViewModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CoffeeTrackerScreen(
    viewModel: CoffeeTrackerViewModel = viewModel(),
    navController: NavController,
) {
    val yearMonth by viewModel.yearMonth.collectAsState()
    val totalDays by viewModel.totalDaysInMonth.collectAsState()
    val coffeeData by viewModel.coffeeData.collectAsState()
    val selectedDay by viewModel.selectedDay
    val isLoading by viewModel.isLoading.collectAsState()

    val today = viewModel.todayDay.collectAsState().value

    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val coroutineScope = rememberCoroutineScope()

    var selectedCupIndex by remember { mutableIntStateOf(-1) }
    val context = LocalContext.current

    val total by viewModel.totalCups.collectAsState()
    val best by viewModel.bestDay.collectAsState()
    val worst by viewModel.worstDay.collectAsState()
    val away by viewModel.cupsAwayFromPerfect.collectAsState()

    val listState = rememberLazyListState()

    LaunchedEffect(Unit) {
        listState.scrollToItem(today - 1)
    }
    Scaffold(
        topBar = {
            TopAppBar(
                modifier = Modifier.fillMaxWidth(),
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant
                ),
                title = {},
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    Text(
                        modifier = Modifier.fillMaxWidth(),
                        text = "Coffee Tracker",
                        style = MaterialTheme.typography.titleMedium,
                        textAlign = TextAlign.Center
                    )
                }
            )
        },
        modifier = Modifier
            .fillMaxSize()
            .safeContentPadding()
    ) { paddingValues ->
        if (isLoading) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                LoadingCat()
            }
        }

        else

            if (coffeeData.isEmpty()) {

                Surface(
                    tonalElevation = 3.dp,
                    shape = RoundedCornerShape(bottomStart = 12.dp, bottomEnd = 12.dp),
                    color = MaterialTheme.colorScheme.surfaceContainerHighest,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(paddingValues)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 12.dp, vertical = 10.dp),
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
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(top = paddingValues.calculateTopPadding())
                        .padding(horizontal = 24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    EmptyStateCat(modifier = Modifier.size(250.dp))

                    Text(
                        text = "Looks like your coffee journey hasn't started yet! ☕",
                        style = MaterialTheme.typography.headlineSmall,
                        textAlign = TextAlign.Center,
                        color = MaterialTheme.colorScheme.onSurface,
                        fontWeight = FontWeight.SemiBold
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = "Tap the button below to record your very first cup and kickstart your coffee tracking adventure!",
                        style = MaterialTheme.typography.bodyLarge,
                        textAlign = TextAlign.Center,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    Spacer(modifier = Modifier.height(12.dp))


                    Button(
                        onClick = {

                            viewModel.selectDay(viewModel.todayDay.value)
                            viewModel.toggleCup(
                                viewModel.selectedDay.value,
                                0, onResult = {
                                    Toast.makeText(context, "What a hero !", Toast.LENGTH_SHORT)
                                        .show()
                                }
                            )
                        },
                        modifier = Modifier

                    ) {
                        Text("Record Your First Cup!")
                    }
                }
            }

            else {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues)
                ) {

                    CoffeeTrackerHero()

                    Text(
                        modifier = Modifier.padding(
                            start = 16.dp,
                            end = 16.dp,
                            top = 16.dp
                        ),
                        text = "Monthly Summary",
                        style = MaterialTheme.typography.titleMedium,
                    )

                    val summaryOverview = listOf(
                        SummaryRowData(
                            icon = painterResource(id = R.drawable.total_ic),
                            label = "Total Cups",
                            value = total.toString()
                        ),
                        SummaryRowData(
                            icon = painterResource(id = R.drawable.best_ic),
                            label = "Best Day",
                            value = best?.let { "Day $it" } ?: "--",
                        ),
                        SummaryRowData(
                            icon = painterResource(id = R.drawable.perfectweek_ic),
                    label = "Cups from Perfect Week",
                    value = away?.toString() ?: "--"
                        ),
                    )

                    MonthlySummaryCard(
                        title = "Your Habit Summary",
                        summaryRows = summaryOverview,
                        decorativeImagePainter = painterResource(id = R.drawable.coffe_summary_ic),
                        decorativeImageContentDescription = "Habit Summary Background"
                    )


                    Surface(
                        tonalElevation = 3.dp,
                        shape = RoundedCornerShape(topStart = 12.dp, topEnd = 12.dp),
                        color = MaterialTheme.colorScheme.surfaceVariant,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 12.dp, vertical = 1.dp)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 12.dp, vertical = 10.dp),
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


                    LazyColumn(
                        modifier = Modifier.weight(0.3f),
                        state = listState
                    ) {
                        items(totalDays) { day ->
                            val cups = coffeeData[day + 1]?.cups ?: List(8) { false }

                            Column(
                                modifier = Modifier.padding(
                                    vertical = 8.dp,
                                    horizontal = 16.dp
                                )
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier.padding(bottom = 4.dp)
                                ) {
                                    Text(
                                        text = "Day ${day + 1}",
                                        style = MaterialTheme.typography.labelLarge
                                    )

                                    if (today == day + 1) {
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Surface(
                                            shape = RoundedCornerShape(50),
                                            color = MaterialTheme.colorScheme.primary.copy(alpha = 0.15f),
                                            contentColor = MaterialTheme.colorScheme.primary,
                                            tonalElevation = 2.dp
                                        ) {
                                            Text(
                                                text = "Today",
                                                style = MaterialTheme.typography.labelSmall,
                                                modifier = Modifier
                                                    .padding(horizontal = 8.dp, vertical = 2.dp)
                                            )
                                        }
                                    }
                                }

                                Row(
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    cups.forEachIndexed { index, filled ->
                                        Box(
                                            modifier = Modifier
                                                .size(48.dp)
                                                .clip(RoundedCornerShape(12.dp))
                                                .background(
                                                    if (filled)
                                                        MaterialTheme.colorScheme.secondaryContainer.copy(
                                                            alpha = 0.3f
                                                        )
                                                    else
                                                        MaterialTheme.colorScheme.surfaceContainer
                                                )
                                                .clickable {
                                                    viewModel.selectDay(day + 1)
                                                    selectedCupIndex = index
                                                    coroutineScope.launch { sheetState.show() }
                                                },
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Icon(
                                                modifier = Modifier
                                                    .size(48.dp)
                                                    .padding(2.dp),
                                                painter = if (filled) painterResource(R.drawable.coffee_filled) else painterResource(
                                                    R.drawable.coffee_empty
                                                ),
                                                contentDescription = null,
                                                tint = if (filled) Color.Unspecified else MaterialTheme.colorScheme.onSurfaceVariant
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }


        if (selectedCupIndex != -1) {
            ModalBottomSheet(
                onDismissRequest = { selectedCupIndex = -1 },
                sheetState = sheetState,
                containerColor = MaterialTheme.colorScheme.surfaceContainer,
                tonalElevation = 8.dp,
                shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp)
            ) {
                Column(
                    modifier = Modifier
                        .padding(24.dp)
                        .fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Icon(
                            painter = painterResource(R.drawable.coffee_filled),
                            contentDescription = "Coffee Icon",
                            tint = Color.Unspecified,
                            modifier = Modifier.size(48.dp)
                        )
                        Text(
                            text = "Cup #${selectedCupIndex + 1}",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.SemiBold
                        )
                    }

                    Spacer(Modifier.height(8.dp))


                    Text(
                        text = "Tap the button below to mark this cup as consumed or unmark it if it was a mistake. This helps keep your daily coffee count accurate.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    Spacer(Modifier.height(24.dp))


                    Button(
                        onClick = {
                            viewModel.toggleCup(selectedDay, selectedCupIndex, onResult = {
                                if (it)
                                    Toast.makeText(
                                        context,
                                        "Brewing status changed!",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                else
                                    Toast.makeText(
                                        context,
                                        "Failed to update cup. Please try again.",
                                        Toast.LENGTH_SHORT
                                    ).show()
                            })
                            coroutineScope.launch { sheetState.hide() }
                            selectedCupIndex = -1
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp)
                    ) {
                        Text("Toggle This Cup")
                    }

                    Spacer(Modifier.height(8.dp))


                    OutlinedButton(
                        onClick = {
                            coroutineScope.launch { sheetState.hide() }
                            selectedCupIndex = -1
                        },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Cancel", color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }
            }
        }
    }
}

@Composable
fun SummaryCard(viewModel: CoffeeTrackerViewModel) {
    val total by viewModel.totalCups.collectAsState()
    val best by viewModel.bestDay.collectAsState()
    val worst by viewModel.worstDay.collectAsState()
    val away by viewModel.cupsAwayFromPerfect.collectAsState()

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp, vertical = 8.dp)
    ) {

        Column(
            modifier = Modifier
                .padding(20.dp)
                .fillMaxWidth()
                .padding(bottom = 6.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                modifier = Modifier.padding(bottom = 12.dp),
                text = "Monthly Summary",
                style = MaterialTheme.typography.titleMedium,
            )


        }


        Image(
            painter = painterResource(id = R.drawable.summary_coffe),
            contentDescription = null,
            modifier = Modifier
                .align(Alignment.TopEnd)
                .size(72.dp)
                .padding(8.dp),
            alpha = 0.85f
        )
    }
}

@Composable
fun CoffeeTrackerHero(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(200.dp)
    ) {
        Image(
            painter = painterResource(id = R.drawable.hero_coffe),
            contentDescription = "Coffee background image",
            contentScale = ContentScale.Crop,
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
                            MaterialTheme.colorScheme.scrim.copy(alpha = 0.65f)
                        )
                    )
                )
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            verticalArrangement = Arrangement.Bottom,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Coffee first, then everything else.",
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Bold,
                color = Color.White,
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Coffee doesn’t count itself, but we do.",
                style = MaterialTheme.typography.bodySmall,
                color = Color.White.copy(alpha = 0.85f),
                textAlign = TextAlign.Center
            )
        }
    }
}



