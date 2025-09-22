package com.shadow.moodtracker.presentationLayer.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import com.shadow.moodtracker.data.repository.habitColorLegends

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddHabitForm(
    modifier: Modifier = Modifier,
    onSave: (String, String, String) -> Unit, // name, reward, color
    onCancel: () -> Unit
) {
    val habitColorLegends= habitColorLegends()
    var habitName by remember { mutableStateOf("") }
    var reward by remember { mutableStateOf("") }
    var selectedColor by remember { mutableStateOf(habitColorLegends.first()) }

    var nameError by remember { mutableStateOf<String?>(null) }
    var rewardError by remember { mutableStateOf<String?>(null) }

    Column(
        modifier = modifier
            .padding(16.dp)
            .fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {

        Text("Add New Habit", style = MaterialTheme.typography.headlineSmall)

        // Habit Name
        OutlinedTextField(
            value = habitName,
            onValueChange = {
                habitName = it
                nameError = if (it.isBlank()) "Name cannot be empty" else null
            },
            label = { Text("Habit Name") },
            isError = nameError != null,
            modifier = Modifier.fillMaxWidth(),
            keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Next)
        )
        if (nameError != null) {
            Text(nameError!!, color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodySmall)
        }

        // Reward
        OutlinedTextField(
            value = reward,
            onValueChange = {
                reward = it
                rewardError = if (it.isBlank()) "Reward cannot be empty" else null
            },
            label = { Text("Reward") },
            isError = rewardError != null,
            modifier = Modifier.fillMaxWidth(),
            keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Done)
        )
        if (rewardError != null) {
            Text(rewardError!!, color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodySmall)
        }

        // Color Picker
        Text("Pick a Color", style = MaterialTheme.typography.labelMedium)
        FlowRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            habitColorLegends.forEach { color ->
                val isSelected = selectedColor == color
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .background(color, shape = RoundedCornerShape(12.dp))
                        .border(
                            width = if (isSelected) 3.dp else 1.dp,
                            color = if (isSelected) MaterialTheme.colorScheme.primary else Color.Gray,
                            shape = RoundedCornerShape(12.dp)
                        )
                        .clickable { selectedColor = color }
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Save & Cancel buttons
        Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {

            OutlinedButton(
                onClick = onCancel,
                modifier = Modifier.weight(1f)
            ) {
                Text("Cancel")
            }
            Button(
                onClick = {
                    val nameValid = habitName.isNotBlank()
                    val rewardValid = reward.isNotBlank()
                    nameError = if (!nameValid) "Name cannot be empty" else null
                    rewardError = if (!rewardValid) "Reward cannot be empty" else null

                    if (nameValid && rewardValid) {
                        onSave(habitName.trim(), reward.trim(), selectedColor.toHex())
                    }
                },
                modifier = Modifier.weight(1f),
                enabled = !(habitName.isEmpty()||reward.isEmpty())
            ) {
                Text("Save")
            }

        }
    }
}
