package com.example.sbma_notification

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.example.sbma_notification.ui.theme.SBMA_NotificationTheme
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import kotlin.random.Random.Default.nextLong

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            SBMA_NotificationTheme {
                NotificationMain()
            }
        }
    }
}

fun randomColor(): Color{
    val colorLong = nextLong(4278190080, 4294967295)
    return Color(colorLong)
}

@Composable
fun NotificationMain() {
    val scaffoldState: ScaffoldState = rememberScaffoldState()
    val coroutineScope: CoroutineScope = rememberCoroutineScope()
    var prevColor by remember { mutableStateOf(randomColor())}
    var currentColor by remember { mutableStateOf(randomColor())}


    Scaffold(
        scaffoldState = scaffoldState,
        snackbarHost = {
          SnackbarHost(it){ data ->
              Snackbar(
                  backgroundColor = MaterialTheme.colors.primaryVariant,
                  actionColor = MaterialTheme.colors.onPrimary,
                  snackbarData = data
              )
          }
        },
        backgroundColor = currentColor,
        modifier = Modifier.fillMaxSize()
    ){
        Button(onClick = {
            coroutineScope.launch {
                prevColor = currentColor
                currentColor = randomColor()
                val snackbarResult = scaffoldState.snackbarHostState.showSnackbar(
                    message = "Color changed",
                    actionLabel = "Undo"
                )
                when (snackbarResult) {
                    SnackbarResult.ActionPerformed -> {
                        currentColor = prevColor
                    }
                    SnackbarResult.Dismissed -> {}
                }
            }
        }){
            Text("Change Color!")
        }
    }
}