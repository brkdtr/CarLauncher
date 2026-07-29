package com.example.carplaylauncher

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.view.WindowInsets
import android.view.WindowInsetsController
import android.view.WindowManager
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import java.text.SimpleDateFormat
import java.util.*
import kotlinx.coroutines.delay

class MainActivity : ComponentActivity() {

    private val closeAppReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            if (intent?.action == "com.example.carplaylauncher.CLOSE_APP") {
                finishAndRemoveTask()
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        hideSystemUI()
        checkOverlayPermission()
        registerReceiver(closeAppReceiver, IntentFilter("com.example.carplaylauncher.CLOSE_APP"))

        setContent {
            MaterialTheme(
                colorScheme = darkColorScheme(
                    background = Color(0xFF1C1C1E),
                    surface = Color(0xFF2C2C2E)
                )
            ) {
                CarPlayScreen()
            }
        }
    }

    override fun onResume() {
        super.onResume()
        hideSystemUI()
    }

    override fun onDestroy() {
        super.onDestroy()
        unregisterReceiver(closeAppReceiver)
    }

    private fun hideSystemUI() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            window.setDecorFitsSystemWindows(false)
            window.insetsController?.let {
                it.hide(WindowInsets.Type.statusBars() or WindowInsets.Type.navigationBars())
                it.systemBarsBehavior = WindowInsetsController.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
            }
        } else {
            @Suppress("DEPRECATION")
            window.decorView.systemUiVisibility = (
                android.view.View.SYSTEM_UI_FLAG_FULLSCREEN
                or android.view.View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                or android.view.View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                or android.view.View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                or android.view.View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                or android.view.View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
            )
        }
    }

    private fun checkOverlayPermission() {
        if (!Settings.canDrawOverlays(this)) {
            val intent = Intent(
                Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                Uri.parse("package:$packageName")
            )
            startActivity(intent)
        }
    }
}

@Composable
fun CarPlayScreen() {
    Row(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
    ) {
        Sidebar(modifier = Modifier.weight(0.15f))
        MainGrid(modifier = Modifier.weight(0.85f))
    }
}

@Composable
fun Sidebar(modifier: Modifier = Modifier) {
    var timeText by remember { mutableStateOf("") }
    
    LaunchedEffect(Unit) {
        while (true) {
            val sdf = SimpleDateFormat("HH:mm", Locale.getDefault())
            timeText = sdf.format(Date())
            delay(1000)
        }
    }

    Column(
        modifier = modifier
            .fillMaxHeight()
            .background(Color(0x802C2C2E))
            .padding(vertical = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = timeText,
            color = Color.White,
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold
        )

        Column(
            verticalArrangement = Arrangement.spacedBy(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            SidebarIcon(color = Color(0xFF1DB954))
            SidebarIcon(color = Color(0xFF4285F4))
            SidebarIcon(color = Color(0xFF25D366))
        }

        Box(
            modifier = Modifier
                .size(40.dp)
                .background(Color.White, RoundedCornerShape(8.dp)),
            contentAlignment = Alignment.Center
        ) {
            Row(horizontalArrangement = Arrangement.spacedBy(2.dp)) {
                Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                    Box(modifier = Modifier.size(8.dp).background(Color.Black))
                    Box(modifier = Modifier.size(8.dp).background(Color.Black))
                }
                Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                    Box(modifier = Modifier.size(8.dp).background(Color.Black))
                    Box(modifier = Modifier.size(8.dp).background(Color.Black))
                }
            }
        }
    }
}

@Composable
fun SidebarIcon(color: Color) {
    Box(
        modifier = Modifier
            .size(48.dp)
            .background(color, RoundedCornerShape(12.dp))
    )
}

@Composable
fun MainGrid(modifier: Modifier = Modifier) {
    val context = LocalContext.current
    
    val appList = listOf(
        AppItem("Spotify", Color(0xFF1DB954), "com.spotify.music"),
        AppItem("Maps", Color(0xFF4285F4), "com.google.android.apps.maps"),
        AppItem("Telefon", Color(0xFF34C759), "com.android.dialer"),
        AppItem("Mesajlar", Color(0xFF5AC8FA), "com.google.android.apps.messaging"),
        AppItem("Müzik", Color(0xFFFF2D55), ""),
        AppItem("Ayarlar", Color(0xFF8E8E93), "com.android.settings"),
        AppItem("Podcast", Color(0xFFAF52DE), ""),
        AppItem("Takvim", Color(0xFFFF3B30), "")
    )

    LazyVerticalGrid(
        columns = GridCells.Fixed(4),
        modifier = modifier
            .fillMaxSize()
            .padding(32.dp),
        horizontalArrangement = Arrangement.spacedBy(32.dp),
        verticalArrangement = Arrangement.spacedBy(32.dp),
        contentPadding = PaddingValues(16.dp)
    ) {
        items(appList) { app ->
            AppIconItem(app) {
                if (app.packageName.isNotEmpty()) {
                    val launchIntent = context.packageManager.getLaunchIntentForPackage(app.packageName)
                    if (launchIntent != null) {
                        context.startActivity(launchIntent)
                    }
                }
            }
        }
    }
}

@Composable
fun AppIconItem(appItem: AppItem, onClick: () -> Unit) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.clickable { onClick() }
    ) {
        Box(
            modifier = Modifier
                .size(80.dp)
                .clip(RoundedCornerShape(20.dp))
                .background(appItem.color),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = appItem.name.first().toString(),
                color = Color.White,
                fontSize = 32.sp,
                fontWeight = FontWeight.Bold
            )
        }
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = appItem.name,
            color = Color.White,
            fontSize = 14.sp
        )
    }
}

data class AppItem(val name: String, val color: Color, val packageName: String)
