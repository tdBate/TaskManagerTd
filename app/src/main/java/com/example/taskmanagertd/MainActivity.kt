package com.example.taskmanagertd

import android.R
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
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
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.taskmanagertd.ui.theme.TaskManagerTdTheme

data class Task (
    val id:Int,
    val name: String,
    val kategoria: String,
    val priortias: Int,
    val finished: Boolean
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