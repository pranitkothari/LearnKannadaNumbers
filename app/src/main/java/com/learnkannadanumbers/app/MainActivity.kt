package com.learnkannadanumbers.app

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.core.content.ContextCompat
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.learnkannadanumbers.app.ui.MainScreen
import com.learnkannadanumbers.app.ui.MainViewModel
import com.learnkannadanumbers.app.ui.theme.LearnKannadaNumbersTheme

class MainActivity : ComponentActivity() {

    private val viewModel: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            var hasMicPermission by remember {
                mutableStateOf(hasRecordAudioPermission())
            }

            val permissionLauncher = rememberLauncherForActivityResult(
                ActivityResultContracts.RequestPermission(),
            ) { granted -> hasMicPermission = granted }

            val uiState by viewModel.uiState.collectAsStateWithLifecycle()

            LearnKannadaNumbersTheme {
                MainScreen(
                    uiState = uiState,
                    hasMicPermission = hasMicPermission,
                    onNumberInputChanged = viewModel::onNumberInputChanged,
                    onMicTapped = viewModel::onMicTapped,
                    onRequestMicPermission = {
                        permissionLauncher.launch(Manifest.permission.RECORD_AUDIO)
                    },
                )
            }
        }
    }

    private fun hasRecordAudioPermission(): Boolean =
        ContextCompat.checkSelfPermission(
            this,
            Manifest.permission.RECORD_AUDIO,
        ) == PackageManager.PERMISSION_GRANTED
}
