package com.shadow.moodtracker.presentationLayer.components

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MenuAnchorType
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.shadow.moodtracker.data.repository.Habit
import com.shadow.moodtracker.data.repository.HabitProgress

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HabitLogEntry(
    habitProgressList: List<HabitProgress>,
    totalDaysInMonth: Int,
    onSaveLog: (habit: Habit, day: String) -> Unit,
    onCancel: () -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
    var selectedHabitProgress by remember { mutableStateOf(habitProgressList.firstOrNull()) }
    var dayInput by remember { mutableStateOf("") }
    var dayInputError by remember { mutableStateOf<String?>(null) }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        verticalArrangement = Arrangement.Top
    ) {
        // Title & description
        Text(
            text = "Log Your Habit Progress",
            style = MaterialTheme.typography.headlineSmall,
            modifier = Modifier.padding(bottom = 8.dp)
        )
        Text(
            text = "Select a habit and the day you completed it to track your progress.",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(bottom = 24.dp)
        )

        // Section: Habit Selection
        Text(
            text = "Habit",
            style = MaterialTheme.typography.labelMedium,
            modifier = Modifier.padding(bottom = 8.dp)

        )
        ExposedDropdownMenuBox(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 24.dp),
            expanded = expanded,
            onExpandedChange = { expanded = !expanded }) {
            TextField(
                value = selectedHabitProgress?.habit?.name ?: "",
                onValueChange = {},
                readOnly = true,
                label = { Text("Select Habit") },
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded) },
                modifier = Modifier
                    .menuAnchor(type = MenuAnchorType.PrimaryNotEditable, enabled = true)
                    .fillMaxWidth()
            )
            DropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false },
                modifier = Modifier
                    .fillMaxWidth(0.9f)
                    .border(1.dp, color = MaterialTheme.colorScheme.outlineVariant)
            ) {
                habitProgressList.forEach { habitProgress ->
                    DropdownMenuItem(
                        text = { Text(habitProgress.habit.name) },
                        onClick = {
                            selectedHabitProgress = habitProgress
                            expanded = false
                        }
                    )
                }
            }
        }


        // Section: Day Input
        Text(
            text = "Day Completed",
            style = MaterialTheme.typography.labelMedium,
        )
        OutlinedTextField(
            value = dayInput,
            onValueChange = { new ->
                if (new.isEmpty()) {
                    dayInput = new
                    dayInputError = "Day cannot be empty"
                } else if (new.all { it.isDigit() }) {
                    val dayInt = new.toIntOrNull()
                    if (dayInt != null && dayInt in 1..totalDaysInMonth) {
                        dayInput = new
                        dayInputError = null
                    } else {
                        dayInputError = "Day must be between 1 and $totalDaysInMonth"
                    }
                }
            },
            isError = dayInputError != null,
            label = { Text("Enter Day (1 - $totalDaysInMonth)") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 24.dp)
        )
        if (dayInputError != null) {
            Text(
                text = dayInputError!!,
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier.padding(top = 4.dp)
            )
        }


        // Buttons Row
        Row(
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            OutlinedButton(
                onClick = {
                    dayInput = ""
                    dayInputError = null
                    onCancel()
                },
                modifier = Modifier.weight(1f)
            ) {
                Text("Cancel")
            }

            Button(
                enabled = selectedHabitProgress != null && dayInputError == null && dayInput.isNotBlank(),
                onClick = {
                    selectedHabitProgress?.habit?.let { habit ->
                        onSaveLog(habit, dayInput)
                        dayInput = ""
                        dayInputError = null
                    }
                },
                modifier = Modifier.weight(1f)
            ) {
                Text("Save")
            }
        }
    }
}
