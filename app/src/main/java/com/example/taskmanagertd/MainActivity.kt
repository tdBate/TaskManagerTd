package com.example.taskmanagertd

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.taskmanagertd.ui.theme.TaskManagerTdTheme

data class Task(
    val id: Int,
    val name: String,
    val kategoria: String,
    val prioritas: Int,
    val finished: Boolean
)

enum class FilterType { OSSZES, AKTIV, ELKESZULT }

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            TaskManagerTdTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    TaskManagerScreen()
                }
            }
        }
    }
}

@Composable
fun TaskManagerScreen() {
    val context = LocalContext.current

    var name by remember { mutableStateOf("") }
    var nameError by remember { mutableStateOf(false) }

    var kategoria by remember { mutableStateOf("") }
    var kategoriaError by remember { mutableStateOf(false) }

    var prioritasInput by remember { mutableStateOf("1") }
    var prioritasError by remember { mutableStateOf(false) }

    val taskok = remember { mutableStateListOf<Task>() }
    var nextId by remember { mutableIntStateOf(1) }

    var currentFilter by remember { mutableStateOf(FilterType.OSSZES) }

    var taskToEdit by remember { mutableStateOf<Task?>(null) }
    var taskToDelete by remember { mutableStateOf<Task?>(null) }

    val filteredTasks = taskok.filter { task ->
        when (currentFilter) {
            FilterType.OSSZES -> true
            FilterType.AKTIV -> !task.finished
            FilterType.ELKESZULT -> task.finished
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(top = 40.dp, start = 20.dp, end = 20.dp, bottom = 20.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text(
            "Feladatkezelő",
            style = MaterialTheme.typography.headlineLarge,
            fontWeight = FontWeight.Bold
        )
        Text("Szervezd meg a napi teendőidet!")

        OutlinedTextField(
            value = name,
            onValueChange = { name = it; nameError = false },
            label = { Text("Feladat neve") },
            isError = nameError,
            supportingText = {
                if (nameError) Text("A feladat neve kötelező!", color = MaterialTheme.colorScheme.error)
            },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )

        OutlinedTextField(
            value = kategoria,
            onValueChange = { kategoria = it; kategoriaError = false },
            label = { Text("Kategória") },
            isError = kategoriaError,
            supportingText = {
                if (kategoriaError) Text("A kategória kötelező!", color = MaterialTheme.colorScheme.error)
            },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )

        OutlinedTextField(
            value = prioritasInput,
            onValueChange = { prioritasInput = it; prioritasError = false },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            label = { Text("Prioritás (1-3)") },
            isError = prioritasError,
            supportingText = {
                if (prioritasError) Text("A prioritás csak 1 és 3 közötti érték lehet!", color = MaterialTheme.colorScheme.error)
            },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )

        Button(
            modifier = Modifier.fillMaxWidth(),
            onClick = {
                val p = prioritasInput.toIntOrNull()
                val isNameValid = name.isNotBlank()
                val isKategoriaValid = kategoria.isNotBlank()
                val isPrioritasValid = p != null && p in 1..3

                nameError = !isNameValid
                kategoriaError = !isKategoriaValid
                prioritasError = !isPrioritasValid

                if (isNameValid && isKategoriaValid && isPrioritasValid) {
                    val newTask = Task(
                        id = nextId++,
                        name = name.trim(),
                        kategoria = kategoria.trim(),
                        prioritas = p!!,
                        finished = false
                    )
                    taskok.add(newTask)

                    name = ""
                    kategoria = ""
                    prioritasInput = "1"

                    Toast.makeText(context, "Feladat sikeresen hozzáadva!", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(context, "Ellenőrizd a megadott adatokat!", Toast.LENGTH_SHORT).show()
                }
            }
        ) { Text("Feladat hozzáadása") }

        HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            FilterButton("Összes", currentFilter == FilterType.OSSZES) { currentFilter = FilterType.OSSZES }
            FilterButton("Aktív", currentFilter == FilterType.AKTIV) { currentFilter = FilterType.AKTIV }
            FilterButton("Elkészült", currentFilter == FilterType.ELKESZULT) { currentFilter = FilterType.ELKESZULT }
        }

        if (taskok.isEmpty()) {
            Text("Még nincs feladatod.", style = MaterialTheme.typography.bodyLarge, modifier = Modifier.padding(top = 16.dp))
        } else if (filteredTasks.isEmpty()) {
            val emptyMsg = if (currentFilter == FilterType.AKTIV) "Nincs aktív feladat." else "Még nincs elkészült feladat."
            Text(emptyMsg, style = MaterialTheme.typography.bodyLarge, modifier = Modifier.padding(top = 16.dp))
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(items = filteredTasks, key = { it.id }) { task ->
                    TaskCard(
                        task = task,
                        onDeleteClick = { taskToDelete = task },
                        onEditClick = { taskToEdit = task },
                        onToggleStatus = {
                            val index = taskok.indexOfFirst { it.id == task.id }
                            if (index != -1) {
                                taskok[index] = taskok[index].copy(finished = !task.finished)
                            }
                        }
                    )
                }
            }
        }
    }

    if (taskToDelete != null) {
        AlertDialog(
            onDismissRequest = { taskToDelete = null },
            title = { Text("Feladat törlése") },
            text = { Text("Biztosan törölni szeretnéd az „${taskToDelete?.name}” feladatot?") },
            confirmButton = {
                TextButton(onClick = {
                    taskok.remove(taskToDelete)
                    taskToDelete = null
                }) { Text("Törlés", color = MaterialTheme.colorScheme.error) }
            },
            dismissButton = {
                TextButton(onClick = { taskToDelete = null }) { Text("Mégse") }
            }
        )
    }

    if (taskToEdit != null) {
        EditTaskDialog(
            task = taskToEdit!!,
            onDismiss = { taskToEdit = null },
            onSave = { updatedTask ->
                val index = taskok.indexOfFirst { it.id == updatedTask.id }
                if (index != -1) {
                    taskok[index] = updatedTask
                }
                taskToEdit = null
            }
        )
    }
}

@Composable
fun FilterButton(text: String, isSelected: Boolean, onClick: () -> Unit) {
    Button(
        onClick = onClick,
        colors = ButtonDefaults.buttonColors(
            containerColor = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.secondaryContainer,
            contentColor = if (isSelected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSecondaryContainer
        )
    ) {
        Text(text)
    }
}

@Composable
fun TaskCard(
    task: Task,
    onDeleteClick: () -> Unit,
    onEditClick: () -> Unit,
    onToggleStatus: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(8.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            TaskPriorityAvatar(task.prioritas)

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(task.name, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium)
                Text(task.kategoria, style = MaterialTheme.typography.bodyMedium)
                Text(
                    text = when (task.prioritas) {
                        1 -> "Alacsony prioritás"
                        2 -> "Közepes prioritás"
                        else -> "Magas prioritás"
                    },
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.Gray
                )
                if (task.finished) {
                    Text("Elkészült", color = Color(0xFF388E3C), fontWeight = FontWeight.Bold)
                }
            }

            Column(horizontalAlignment = Alignment.End) {
                Row {
                    IconButton(onClick = onEditClick) {
                        Icon (
                            painter = painterResource(
                                id = R.drawable.modosit
                            ), contentDescription = "Módosít")
                    }
                    IconButton(onClick = onDeleteClick) {
                        Icon (
                        painter = painterResource(
                            id = R.drawable.torles
                        ), contentDescription = "Törlés")
                    }
                }
                Button(onClick = onToggleStatus) {
                    Text(if (task.finished) "Újra aktív" else "Elkészült")
                }
            }
        }
    }
}

@Composable
fun TaskPriorityAvatar(prioritas: Int) {
    val bgColor = when (prioritas) {
        1 -> Color(0xFF81C784)
        2 -> Color(0xFFFFB74D)
        else -> Color(0xFFE57373)
    }

    Box(
        modifier = Modifier
            .size(48.dp)
            .clip(CircleShape)
            .background(bgColor),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = prioritas.toString(),
            color = Color.White,
            fontWeight = FontWeight.Bold,
            style = MaterialTheme.typography.titleLarge
        )
    }
}

@Composable
fun EditTaskDialog(task: Task, onDismiss: () -> Unit, onSave: (Task) -> Unit) {
    val context = LocalContext.current
    var name by remember { mutableStateOf(task.name) }
    var kategoria by remember { mutableStateOf(task.kategoria) }
    var prioritasInput by remember { mutableStateOf(task.prioritas.toString()) }

    var nameError by remember { mutableStateOf(false) }
    var kategoriaError by remember { mutableStateOf(false) }
    var prioritasError by remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Feladat szerkesztése") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it; nameError = false },
                    label = { Text("Név") },
                    isError = nameError,
                    singleLine = true
                )
                OutlinedTextField(
                    value = kategoria,
                    onValueChange = { kategoria = it; kategoriaError = false },
                    label = { Text("Kategória") },
                    isError = kategoriaError,
                    singleLine = true
                )
                OutlinedTextField(
                    value = prioritasInput,
                    onValueChange = { prioritasInput = it; prioritasError = false },
                    label = { Text("Prioritás (1-3)") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    isError = prioritasError,
                    singleLine = true
                )
            }
        },
        confirmButton = {
            Button(onClick = {
                val p = prioritasInput.toIntOrNull()
                val isValid = name.isNotBlank() && kategoria.isNotBlank() && p != null && p in 1..3

                if (isValid) {
                    onSave(task.copy(name = name.trim(), kategoria = kategoria.trim(), prioritas = p!!))
                } else {
                    nameError = name.isBlank()
                    kategoriaError = kategoria.isBlank()
                    prioritasError = p == null || p !in 1..3
                    Toast.makeText(context, "Ellenőrizd a megadott adatokat!", Toast.LENGTH_SHORT).show()
                }
            }) { Text("Mentés") }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Mégse") }
        }
    )
}

@Preview(showBackground = true)
@Composable
fun TaskManagerPreview() {
    TaskManagerTdTheme {
        TaskManagerScreen()
    }
}