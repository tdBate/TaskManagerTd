package com.example.taskmanagertd

import android.R
import android.inputmethodservice.Keyboard
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.HorizontalAlignmentLine
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.LineHeightStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.taskmanagertd.ui.theme.TaskManagerTdTheme
import org.intellij.lang.annotations.JdkConstants

data class Task (
    val id:Int,
    val name: String,
    val kategoria: String,
    val priortias: Int,
    var finished: Boolean
)


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MaterialTheme {
                TaskManagerScreen()
            }
        }
    }
}

@Composable
fun TaskManagerScreen() {
    val context = LocalContext.current

    //adatok
    var name: String by remember {
        mutableStateOf("")
    }

    var nameError: Boolean by remember {
        mutableStateOf(false)
    }

    var kategoria: String by remember {
        mutableStateOf("")
    }

    var kategoriaError: Boolean by remember {
        mutableStateOf(false)
    }

    var priortias: Int by remember {
        mutableIntStateOf(1)
    }

    var priortiasError: Boolean by remember {
        mutableStateOf(false)
    }

    val taskok = remember {
        mutableStateListOf<Task>()
    }

    var nextId: Int by remember {
        mutableIntStateOf(1)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text("Feladatkezelő",
            style = MaterialTheme.typography.headlineLarge,
            fontWeight = FontWeight.Bold)
        Text("Szervezd meg a napi teendőidet!")

        //inputok
        OutlinedTextField(
            value = name,
            onValueChange = {
                name = it
                nameError = false
            },
            label = { Text("Név") },
            supportingText = {
                if (nameError)
                    Text(
                        "A név megadása kötelező!",
                        color = MaterialTheme.colorScheme.error
                    )
            },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )

        OutlinedTextField(
            value = kategoria,
            onValueChange = {
                kategoria = it
                kategoriaError = false
            },
            label = { Text("Kategória") },
            supportingText = {
                if (kategoriaError)
                    Text(
                        "A kategória megadása kötelező!",
                        color = MaterialTheme.colorScheme.error
                    )
            },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )

        OutlinedTextField(
            value = priortias.toString(),
            onValueChange = {
                if (it != ""){
                    priortias = it.toInt()
                } else {
                    priortias = 1
                }
                priortiasError = false
            },
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Number
            ),
            label = { Text("Prioritás") },
            supportingText = {
                if (priortiasError)
                    Text(
                        "A prioritás megadása kötelező!",
                        color = MaterialTheme.colorScheme.error
                    )
            },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )

        Button(
            onClick = {
                if (name=="") {
                    nameError = true
                }

                if (kategoria=="") {
                    kategoriaError = true
                }

                if (!(priortias ==1 || priortias==2 || priortias==3)) {
                    priortiasError = true
                }

                if (!(nameError||kategoriaError||priortiasError)) {
                    val t1:Task = Task(
                        id=++nextId,
                        name= name,
                        kategoria = kategoria,
                        priortias = priortias,
                        finished = false //ezt is javítani
                    )
                    taskok.add(t1)

                    name = ""
                    kategoria = ""
                    priortias = 1

                    Toast.makeText(
                        context, "Feladat sikeresen hozzáadva!",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        ) { Text("Hozzáad") }

        if (taskok.isEmpty())
            Text(
                "Nincsenek taskok felvéve a rendszerben",
                style = MaterialTheme.typography.bodyLarge
            )
        else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(
                    items = taskok,
                    key = { task -> task.id }
                ) { task -> TaskCard(task) }
}}}}

@Composable
fun TaskCard(task: Task) {
    Box() {
        Row(modifier = Modifier.fillMaxWidth()) {
            TaskPriorityAvatar(task.priortias.toString())
            Column() {
                Text(task.name)
                Text(task.kategoria)
                Text(task.priortias.toString())
            }
            Row() {
                Button(onClick = {}){ Text("Szerkeszt")}
                Button(onClick = {}){ Text("Töröl")}
                Button(onClick = {
                    task.finished = !task.finished
                }){ Text("Elkészült")}
            }
        }
    }
}

@Composable
fun TaskPriorityAvatar(name: String) {
    val initials = name.uppercase()

    Box(
        modifier = Modifier
            .size(52.dp)
            .clip(CircleShape)
            .background(Color.Green),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = initials,
            color = Color.White,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Hello $name!",
        modifier = modifier
    )
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    TaskManagerTdTheme {
        Greeting("Android")
    }
}