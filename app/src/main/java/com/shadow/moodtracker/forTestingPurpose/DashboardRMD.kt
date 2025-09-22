package com.shadow.moodtracker.forTestingPurpose
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex

// Dummy data class to simulate record
data class RateMyDayRecord(val day: Int, val color: Int)
data class MoodOption(val color: Int?, val label: String)

val moodOptions = listOf(
    MoodOption(null, "Clear"),
    MoodOption(0xFF4CAF50.toInt(), "Happy"),
    MoodOption(0xFFCDDC39.toInt(), "Calm"),
    MoodOption(0xFFFFC107.toInt(), "Neutral"),
    MoodOption(0xFFFF5722.toInt(), "Stressed"),
    MoodOption(0xFFF44336.toInt(), "Angry")
)

val colorToMood = moodOptions.associate { it.color to it.label }

@Composable
fun MoodTrackerDashboard(records: List<RateMyDayRecord>) {
    val moodCounts: Map<String, Int> = records
        .mapNotNull { record -> colorToMood[record.color] }
        .groupingBy { it }
        .eachCount()

    val mostFrequentMood = moodCounts.maxByOrNull { it.value }?.key ?: "None"
    val happyDaysCount = moodCounts["Happy"] ?: 0

    val moodLabels = records.sortedBy { it.day }
        .mapNotNull { colorToMood[it.color] }

    val moodChanges = moodLabels.zipWithNext().count { (a, b) -> a != b }
    val stability = when {
        moodChanges <= 5 -> "Stable"
        moodChanges <= 10 -> "Moderate"
        else -> "Unstable"
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Month Selector
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = { /* prev month */ }) {
                Icon(Icons.Default.ArrowBack, contentDescription = "Previous Month")
            }
            Text("May 2025", style = MaterialTheme.typography.headlineSmall)
            IconButton(onClick = { /* next month */ }) {
                Icon(Icons.Default.ArrowForward, contentDescription = "Next Month")
            }
        }

        // Summary Section
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = Color(0xFFE8F5E9))
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text("You were Happy for $happyDaysCount days 😊")
                Text("Most frequent mood: $mostFrequentMood")
                Text("Mood stability: $stability")
            }
        }

        // Rate Today
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFFFFF9C4))
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text("How do you feel today?")
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    listOf("😊", "😐", "😔").forEach { emoji ->
                        Button(onClick = { /* rate mood */ }) {
                            Text(emoji)
                        }
                    }
                }
            }
        }

        // Graph Placeholder
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(250.dp)
                .background(Color.LightGray, RoundedCornerShape(8.dp)),
            contentAlignment = Alignment.Center
        ) {
            Text("[Graph Canvas Here]")
        }

        // Mood Legend
        Column {
            Text(
                text = "Mood Color Legends",
                color = MaterialTheme.colorScheme.secondary,
                style = MaterialTheme.typography.labelLarge,
                modifier = Modifier.fillMaxWidth()
            )
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceAround
            ) {
                moodOptions.filter { it.color != null }.forEach { (color, label) ->
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(12.dp)
                                .background(color = Color(color!!), shape = RoundedCornerShape(2.dp))
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(label)
                    }
                }
            }
        }

        // Motivational Quote
        Card(modifier = Modifier.fillMaxWidth()) {
            Text(
                text = "\"Every day may not be good, but there's something good in every day.\"",
                modifier = Modifier.padding(16.dp),
                style = MaterialTheme.typography.bodyMedium
            )
        }

    }
}

@Preview(showBackground = true)
@Composable
fun MoodTrackerDashboardPreview() {
    MaterialTheme {
        val sampleRecords = listOf(
            RateMyDayRecord(1, 0xFF4CAF50.toInt()),
            RateMyDayRecord(2, 0xFF4CAF50.toInt()),
            RateMyDayRecord(3, 0xFFFFC107.toInt()),
            RateMyDayRecord(4, 0xFFF44336.toInt()),
            RateMyDayRecord(5, 0xFF4CAF50.toInt()),
            RateMyDayRecord(6, 0xFF4CAF50.toInt()),
            RateMyDayRecord(7, 0xFFFF5722.toInt()),
            RateMyDayRecord(8, 0xFF4CAF50.toInt()),
            RateMyDayRecord(9, 0xFFFFC107.toInt()),
            RateMyDayRecord(10, 0xFF4CAF50.toInt())
        )
        MoodTrackerDashboard(records = sampleRecords)
    }
}

@Composable
fun StackedMotivationalCards(notes: List<String>) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy((-40).dp)
    ) {
        itemsIndexed(notes) { index, note ->
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(160.dp)
                    .zIndex(index.toFloat()),
                elevation = CardDefaults.cardElevation(defaultElevation = (4 + index * 2).dp),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFFF0F4C3))
            ) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = note,
                        textAlign = TextAlign.Center,
                        style = MaterialTheme.typography.bodyLarge,
                        modifier = Modifier.padding(16.dp)
                    )
                }
            }
        }
    }
}





@Composable
fun MoodSummaryCard(label: String, value: String, icon: ImageVector, color: Color) {
    Card(
        modifier = Modifier,
        colors = CardDefaults.cardColors(containerColor = color.copy(alpha = 0.15f)),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(icon, contentDescription = label, tint = color, modifier = Modifier.size(36.dp))
            Spacer(modifier = Modifier.height(8.dp))
            Text(value, style = MaterialTheme.typography.titleMedium, color = color)
            Text(label, style = MaterialTheme.typography.bodySmall, color = color.copy(alpha = 0.7f))
        }
    }
}

@Composable
fun MoodSummarySection(
    happyDaysCount: Int,
    mostFrequentMood: String,
    stability: String,
    totalDays: Int = 30
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        MoodSummaryCard(
            label = "Happy Days",
            value = "$happyDaysCount / $totalDays",
            icon = Icons.Default.Build,
            color = Color(0xFF4CAF50)
        )
        MoodSummaryCard(
            label = "Most Frequent Mood",
            value = mostFrequentMood,
            icon = Icons.Default.Star,
            color = when (mostFrequentMood) {
                "Happy" -> Color(0xFF4CAF50)
                "Calm" -> Color(0xFFCDDC39)
                "Neutral" -> Color(0xFFFFC107)
                "Stressed" -> Color(0xFFFF5722)
                "Angry" -> Color(0xFFF44336)
                else -> Color.Gray
            }
        )
        MoodSummaryCard(
            label = "Mood Stability",
            value = stability,
            icon = Icons.Default.Star,
            color = when (stability) {
                "Stable" -> Color.Green
                "Moderate" -> Color.Yellow
                else -> Color.Red
            }
        )
    }
}

@Preview(showBackground = true)
@Composable
fun MoodSummarySectionPreview() {
    MaterialTheme {
        MoodSummarySection(
            happyDaysCount = 15,
            mostFrequentMood = "Happy",
            stability = "Moderate"
        )
    }
}