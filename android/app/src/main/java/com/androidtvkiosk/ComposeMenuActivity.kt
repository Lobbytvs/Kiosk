package com.androidtvkiosk

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.focusable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.tv.material3.ClickableSurfaceDefaults
import androidx.tv.material3.ClickableSurfaceScale
import androidx.tv.material3.Surface

class ComposeMenuActivity : ComponentActivity() {
    companion object {
        private const val PREFS_NAME = "KioskPrefs"
        private const val KEY_URL = "kiosk_url"
        const val EXTRA_MENU_TYPE = "menu_type"
        const val MENU_TYPE_MAIN = 0
        const val MENU_TYPE_SETTINGS = 1
        const val EXTRA_RESULT = "result"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val menuType = intent.getIntExtra(EXTRA_MENU_TYPE, MENU_TYPE_MAIN)

        setContent {
            MaterialTheme {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color.Black.copy(alpha = 0.8f)),
                    contentAlignment = Alignment.Center
                ) {
                    when (menuType) {
                        MENU_TYPE_MAIN -> MainMenu()
                        MENU_TYPE_SETTINGS -> SettingsMenu()
                    }
                }
            }
        }
    }

    @Composable
    fun MainMenu() {
        val menuItems = listOf("⚙️ Settings", "🚪 Exit App", "✕ Close Menu")

        Column(
            modifier = Modifier
                .width(400.dp)
                .background(Color(0xFF1E1E1E))
                .padding(24.dp)
                .selectableGroup(),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                text = "Kiosk Menu",
                fontSize = 32.sp,
                color = Color.White,
                modifier = Modifier.padding(bottom = 24.dp)
            )

            menuItems.forEachIndexed { index, item ->
                MenuButton(
                    text = item,
                    requestFocus = index == 0
                ) {
                    handleMainMenuClick(index)
                }
            }
        }
    }

    @Composable
    fun SettingsMenu() {
        val prefs = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val currentUrl = prefs.getString(KEY_URL, "https://example.com") ?: "https://example.com"

        val menuItems = listOf(
            "📱 Current: $currentUrl",
            "✏️ Edit URL",
            "↩️ Back"
        )

        Column(
            modifier = Modifier
                .width(500.dp)
                .background(Color(0xFF1E1E1E))
                .padding(24.dp)
                .selectableGroup(),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                text = "⚙️ Settings",
                fontSize = 32.sp,
                color = Color.White,
                modifier = Modifier.padding(bottom = 24.dp)
            )

            menuItems.forEachIndexed { index, item ->
                MenuButton(
                    text = item,
                    requestFocus = index == 0
                ) {
                    handleSettingsMenuClick(index)
                }
            }
        }
    }

    @OptIn(androidx.tv.material3.ExperimentalTvMaterial3Api::class)
    @Composable
    fun MenuButton(
        text: String,
        requestFocus: Boolean = false,
        onClick: () -> Unit
    ) {
        val focusRequester = remember { FocusRequester() }

        LaunchedEffect(Unit) {
            if (requestFocus) {
                focusRequester.requestFocus()
            }
        }

        Surface(
            onClick = onClick,
            modifier = Modifier
                .fillMaxWidth()
                .height(60.dp)
                .focusRequester(focusRequester)
                .focusable(),
            shape = MaterialTheme.shapes.medium,
            colors = ClickableSurfaceDefaults.colors(
                containerColor = Color(0xFF2C2C2C),
                focusedContainerColor = Color(0xFF4CAF50),
                pressedContainerColor = Color(0xFF45A049)
            ),
            scale = ClickableSurfaceScale.None
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                contentAlignment = Alignment.CenterStart
            ) {
                Text(
                    text = text,
                    fontSize = 20.sp,
                    color = Color.White
                )
            }
        }
    }

    private fun handleMainMenuClick(index: Int) {
        when (index) {
            0 -> { // Settings
                val intent = Intent(this, ComposeMenuActivity::class.java)
                intent.putExtra(EXTRA_MENU_TYPE, MENU_TYPE_SETTINGS)
                startActivityForResult(intent, 100)
            }
            1 -> { // Exit
                setResult(Activity.RESULT_OK)
                finishAndRemoveTask()
            }
            2 -> { // Close
                setResult(Activity.RESULT_CANCELED)
                finish()
            }
        }
    }

    private fun handleSettingsMenuClick(index: Int) {
        when (index) {
            0 -> { // Current URL display - do nothing
            }
            1 -> { // Edit URL
                val resultIntent = Intent()
                resultIntent.putExtra(EXTRA_RESULT, "edit_url")
                setResult(Activity.RESULT_OK, resultIntent)
                finish()
            }
            2 -> { // Back
                finish()
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK && data != null) {
            val result = data.getStringExtra(EXTRA_RESULT)
            if (result != null) {
                val resultIntent = Intent()
                resultIntent.putExtra(EXTRA_RESULT, result)
                setResult(Activity.RESULT_OK, resultIntent)
            }
        }
        finish()
    }
}
