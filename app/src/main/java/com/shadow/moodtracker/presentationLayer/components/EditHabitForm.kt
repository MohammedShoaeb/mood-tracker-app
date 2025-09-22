package com.shadow.moodtracker.presentationLayer.components

import android.widget.Toast
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
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MenuAnchorType
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.graphics.toColorInt
import com.shadow.moodtracker.R
import com.shadow.moodtracker.data.repository.Habit
import com.shadow.moodtracker.data.repository.HabitProgress
import com.shadow.moodtracker.data.repository.habitColorLegends
import com.shadow.moodtracker.presentationLayer.reusableComponents.ConfirmationDialog


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HabitEditScreen(
    habitProgressList: List<HabitProgress>,
    onDeleteHabit: (Habit) -> Unit,
    onSaveHabit: (Habit) -> Unit,
    onCancelEdit: () -> Unit,
) {
    // Keep track of selected habitProgress and editable state
    var selectedHabitProgress by remember { mutableStateOf(habitProgressList.firstOrNull()) }
    var expanded by remember { mutableStateOf(false) }

    // Editable fields with initial values from selected habit
    var habitName by remember { mutableStateOf(selectedHabitProgress?.habit?.name ?: "") }
    var reward by remember { mutableStateOf(selectedHabitProgress?.habit?.reward ?: "") }
    var selectedColor by remember {
        mutableStateOf(
            selectedHabitProgress?.habit?.color?.let { Color(it.toColorInt()) }
        )
    }
    val context = LocalContext.current
    val habitColorLegends= habitColorLegends()

    // Editing mode flags
    var editHabitNameActive by remember { mutableStateOf(false) }
    var editRewardActive by remember { mutableStateOf(false) }
    var colorSelectionActive by remember { mutableStateOf(false) } // can enable/disable color changes
    val rewardFocusRequester = remember { FocusRequester() }
    var showDeleteHabitDialog by remember { mutableStateOf(false) }
    var isConfirmedToDelete by remember { mutableStateOf(false) }

    // Update editable fields when selected habit changes
    LaunchedEffect(selectedHabitProgress) {
        selectedHabitProgress?.let {
            habitName = it.habit.name
            reward = it.habit.reward
            selectedColor = Color(it.habit.color.toColorInt())
            editHabitNameActive = false
            editRewardActive = false
            colorSelectionActive = false
        }
    }
    val hasChanges = selectedHabitProgress?.let {
        habitName != it.habit.name ||
                reward != it.habit.reward ||
                selectedColor?.toHex() != it.habit.color
    } ?: false


    Column(
        modifier = Modifier
//            .fillMaxSize()
//            .background(MaterialTheme.colorScheme.surface)
            .padding(16.dp),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Dropdown for selecting habit
       Row(verticalAlignment = Alignment.CenterVertically){

           ExposedDropdownMenuBox(
               expanded = expanded,
               onExpandedChange = { expanded = !expanded }
           ) {
               TextField(
                   value = selectedHabitProgress?.habit?.name ?: "",
                   onValueChange = {},
                   readOnly = true,
                   label = { Text("Select Habit") },
                   trailingIcon = {
                       ExposedDropdownMenuDefaults.TrailingIcon(expanded)
                   },
                   modifier = Modifier
                       .menuAnchor(type = MenuAnchorType.PrimaryNotEditable, enabled = true)
                       .fillMaxWidth(0.87f)
               )
               DropdownMenu(
                   expanded = expanded,
                   onDismissRequest = { expanded = false },
                   modifier = Modifier.fillMaxWidth(0.8f)
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
           IconButton(
               onClick = {
                   selectedHabitProgress?.habit?.let { habit ->
                       // TODO: call ViewModel to delete habit
                       showDeleteHabitDialog = true
                   }
               },
               modifier = Modifier.padding(start = 8.dp)
           ) {
               Icon(
                   imageVector = Icons.Default.Delete,
                   contentDescription = "Delete Habit",
                   tint = MaterialTheme.colorScheme.error
               )
           }
       }

        Spacer(modifier = Modifier.height(24.dp))

        // Editable Habit Name field
        Row(verticalAlignment = Alignment.CenterVertically) {
            OutlinedTextField(
                value = habitName,
                onValueChange = { if (editHabitNameActive) habitName = it.trimStart() },
                label = { Text("Habit Name") },
                enabled = editHabitNameActive,
                modifier = Modifier.weight(1f),
                keyboardOptions = KeyboardOptions.Default.copy(
                    imeAction = ImeAction.Next
                ),
                keyboardActions = KeyboardActions(
                    onNext = {
                        editRewardActive = true
                        rewardFocusRequester.requestFocus()
                        editHabitNameActive = false
                    }
                )
            )
            if (!editHabitNameActive) {
                IconButton(onClick = { editHabitNameActive = true }) {
                    Icon(Icons.Default.Edit, contentDescription = "Edit Habit Name")
                }
            } else {
                IconButton(onClick = {
                    // Confirm habit name edit
                    editHabitNameActive = false
                }) {
                    Icon(Icons.Default.Check, contentDescription = "Save Habit Name")
                }
                IconButton(onClick = {
                    // Cancel edit, reset to original value
                    habitName = selectedHabitProgress?.habit?.name ?: ""
                    editHabitNameActive = false
                }) {
                    Icon(Icons.Default.Close, contentDescription = "Cancel Habit Name Edit")
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Editable Reward field
        Row(verticalAlignment = Alignment.CenterVertically) {
            OutlinedTextField(
                value = reward,
                onValueChange = {
                    if (editRewardActive) {
                        reward = it.trimStart()
                    }
                },
                label = { Text("Reward") },
                enabled = editRewardActive,
                modifier = Modifier
                    .weight(1f)
                    .focusRequester(rewardFocusRequester),
                keyboardOptions = KeyboardOptions.Default.copy(
                    imeAction = ImeAction.Done
                ),
                keyboardActions = KeyboardActions(
                    onDone = {
                        defaultKeyboardAction(ImeAction.Done)
                        editRewardActive=false
                    }
                )
            )
            if (!editRewardActive) {
                IconButton(onClick = { editRewardActive = true }) {
                    Icon(Icons.Default.Edit, contentDescription = "Edit Reward")
                }
            } else {
                IconButton(onClick = {
                    // Confirm reward edit
                    editRewardActive = false
                }) {
                    Icon(Icons.Default.Check, contentDescription = "Save Reward")
                }
                IconButton(onClick = {
                    // Cancel edit, reset to original reward
                    reward = selectedHabitProgress?.habit?.reward ?: ""
                    editRewardActive = false
                }) {
                    Icon(Icons.Default.Close, contentDescription = "Cancel Reward Edit")
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Color selection
        Text("Pick a Color", style = MaterialTheme.typography.labelMedium)
        Spacer(modifier = Modifier.height(8.dp))
        FlowRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            habitColorLegends.forEach { color ->
                val isSelected = (selectedColor?.value == color.value)
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .background(color, shape = RoundedCornerShape(12.dp))
                        .border(
                            width = if (isSelected) 3.dp else 1.dp,
                            color = if (isSelected) MaterialTheme.colorScheme.primary else Color.Gray,
                            shape = RoundedCornerShape(12.dp)
                        )
                        .clickable(enabled = true) {
                            selectedColor = color
                        }
                )
            }
        }

        Spacer(modifier = Modifier.height(32.dp))

        // Save & Cancel buttons for the whole form
        Row(
            horizontalArrangement = Arrangement.spacedBy(24.dp),
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            OutlinedButton(onClick = {
                // Reset fields to selected habit data (cancel edits)
                selectedHabitProgress?.let {
                    habitName = it.habit.name
                    reward = it.habit.reward
                    selectedColor = Color(it.habit.color.toColorInt())
                    editHabitNameActive = false
                    editRewardActive = false
                }
                onCancelEdit()
            }, modifier = Modifier.weight(1f)) {
                Text("Cancel")
            }

            Button(
                onClick = {
                    if (habitName.isBlank() || reward.isBlank()) {
                        Toast.makeText(
                            context,
                            "Habit name and reward cannot be empty",
                            Toast.LENGTH_SHORT
                        ).show()
                        return@Button
                    }
                    selectedHabitProgress?.habit?.let { habit ->
                        // Create updated Habit with new data
                        val updatedHabit = habit.copy(
                            name = habitName,
                            reward = reward,
                            color = selectedColor?.toHex() ?: habit.color
                        )

                        // TODO: call ViewModel to save updated habit
                        onSaveHabit(updatedHabit)
                    }
                }, enabled = hasChanges,
                modifier = Modifier.weight(1f)
            ) {
                Text("Save Changes")
            }


            if(showDeleteHabitDialog){
                ConfirmationDialog(
                    modifier = Modifier.padding(horizontal = 12.dp),
                    title = "Delete Habit",
                    message = "Are you sure you want to delete this habit? This action cannot be undone.",
                    icon = painterResource(id = R.drawable.alert),
                    confirmButtonText = "Delete",
                    dismissButtonText = "Cancel",
                    onConfirm = {
                        showDeleteHabitDialog = false
                        selectedHabitProgress?.habit?.let {
                            onDeleteHabit(it)
                        }
                    },
                    onDismiss = {
                        showDeleteHabitDialog = false
                    }
                )
            }
            LaunchedEffect(habitProgressList) {
                // If the current selected habit is deleted, select the first habit from updated list or null
                if (selectedHabitProgress != null &&
                    habitProgressList.none { it.habit.id == selectedHabitProgress!!.habit.id }) {
                    selectedHabitProgress = habitProgressList.firstOrNull()
                }
            }

        }
    }
}


@Preview(showBackground = true)
@Composable
fun PreviewHabitEditScreen() {
    val dummyHabits = listOf(
        HabitProgress(
            habit = Habit(
                id = "1",
                name = "Morning Run",
                color = "#FF5722",
                reward = "Smoothie",
                order = 0
            ),
            daysDone = listOf("2024-05-30", "2024-05-31")
        ),
        HabitProgress(
            habit = Habit(
                id = "2",
                name = "Read Book",
                color = "#4CAF50",
                reward = "Watch a movie",
                order = 1
            ),
            daysDone = listOf("2024-05-29")
        )
    )

    HabitEditScreen(
        habitProgressList = dummyHabits,
        onDeleteHabit = {},
        onSaveHabit = {},
        onCancelEdit = {}
    )
}


// Extension function to convert Compose Color to hex string like "#F06292"
fun Color.toHex(): String {
    return String.format(
        "#%02X%02X%02X",
        (red * 255).toInt(),
        (green * 255).toInt(),
        (blue * 255).toInt()
    )
}
