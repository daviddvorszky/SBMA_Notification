package com.example.sbma_notification

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
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

    val context = LocalContext.current
    val channelId = "SBMA_NotifChannel"
    val notificationId = 0
    
    LaunchedEffect(Unit){
        createNotificationChannel(channelId, context)
    }

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
        Column {
            Button(
                onClick = {
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
                },
            ) {
                Text("Change Color!")
            }

            Button(onClick = {
                showNotification(
                    context,
                    channelId,
                    notificationId,
                    "Simple Notification",
                    "This is a simple notification with default priority"
                )
            }) {
                Text("Simple Notification")
            }

            Button(onClick = {
                showNotification(
                    context,
                    channelId,
                    notificationId,
                    "HIGH PRIORITY NOTIFICATION",
                    "This is a simple notification with high priority",
                    NotificationCompat.PRIORITY_HIGH
                )
            }) {
                Text("High Priority Notification")
            }
        }
    }
}

fun createNotificationChannel(channelId: String, context: Context){
    //if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
        val name = "SBMA Notification Channel"
        val descriptionText = "Assignment test channel"
        val importance = NotificationManager.IMPORTANCE_DEFAULT
        val channel = NotificationChannel(channelId, name, importance).apply {
            description = descriptionText
        }

        val notificationManager: NotificationManager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.createNotificationChannel(channel)
    //}
}

fun showNotification(
    context: Context,
    channelId: String,
    notificationId: Int,
    textTitle: String,
    textContent: String,
    priority: Int = NotificationCompat.PRIORITY_DEFAULT
){
    val builder = NotificationCompat.Builder(context, channelId)
        .setSmallIcon(R.drawable.ic_launcher_foreground)
        .setContentTitle(textTitle)
        .setContentText(textContent)
        .setPriority(priority)

    with(NotificationManagerCompat.from(context)){
        notify(notificationId, builder.build())
    }
}