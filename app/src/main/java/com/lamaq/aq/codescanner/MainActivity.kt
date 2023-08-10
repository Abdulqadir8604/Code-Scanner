package com.lamaq.aq.codescanner

import android.annotation.SuppressLint
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.google.mlkit.vision.barcode.common.Barcode
import com.google.mlkit.vision.codescanner.GmsBarcodeScannerOptions
import com.google.mlkit.vision.codescanner.GmsBarcodeScanning
import com.lamaq.aq.codescanner.ui.theme.CodeScannerTheme
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    @SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            CodeScannerTheme {
                val scope = rememberCoroutineScope()
                val snackbarHostState = remember { SnackbarHostState() }
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val options = GmsBarcodeScannerOptions.Builder()
                        .setBarcodeFormats(
                            Barcode.FORMAT_ALL_FORMATS,
                        )
                        .enableAutoZoom()
                        .allowManualInput()
                        .build()
                    val scanner = GmsBarcodeScanning.getClient(this, options)
                    var rawValue by remember {
                        mutableStateOf("")
                    }
                    Scaffold(
                        snackbarHost = {
                            SnackbarHost(hostState = snackbarHostState)
                        },
                        content = {
                            OutlinedCard(
                                modifier = Modifier.fillMaxWidth().blur(4.dp),
                                onClick = {
                                    Toast.makeText(
                                        this,
                                        "Click on the button to scan the code",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                },
                                elevation = CardDefaults.cardElevation(4.dp),
                                shape = MaterialTheme.shapes.medium,

                                ) {
                                Column(
                                    modifier = Modifier.fillMaxSize(),
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    verticalArrangement = Arrangement.Center,
                                ) {
                                    Text(text = rawValue)
                                    ElevatedButton(onClick = {
                                        scanner.startScan()
                                            .addOnSuccessListener { barcode ->
                                                rawValue = barcode.rawValue.toString()
                                            }
                                            .addOnCanceledListener {
                                                scope.launch {
                                                    snackbarHostState.showSnackbar("Task Cancelled")
                                                }
                                            }
                                            .addOnFailureListener { e ->
                                                scope.launch {
                                                    snackbarHostState.showSnackbar("Task Failed")
                                                }
                                            }
                                    }) {
                                        Text(text = "Scan Code")
                                    }
                                }
                            }
                        }
                    )
                }
            }
        }
    }
}