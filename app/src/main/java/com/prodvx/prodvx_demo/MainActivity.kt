package com.prodvx.prodvx_demo

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
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
import androidx.compose.ui.unit.dp
import com.prodvx.prodvx_demo.adaptive_light.AdaptiveLightActivity
import com.prodvx.prodvx_demo.api.TOKEN
import com.prodvx.prodvx_demo.api.initApi
import com.prodvx.prodvx_demo.api.updateToken
import com.prodvx.prodvx_demo.api_demo.ApiDemoActivity
import com.prodvx.prodvx_demo.led.LedActivity
import com.prodvx.prodvx_demo.nfc.NfcActivity
import com.prodvx.prodvx_demo.test.TestActivity
import com.prodvx.prodvx_demo.ui.theme.AndroidTestTheme
import org.json.JSONObject
import java.io.File

class MainActivity : ComponentActivity() {

    // Holder for API token insertion
    private var apiToken by mutableStateOf<String?>(null)

    // State holder for token insertion dialog
    private var showTokenDialog by mutableStateOf(false)

    // Token file picker launcher
    private val pickFileLauncher =
        registerForActivityResult(ActivityResultContracts.OpenDocument()) {
            uri: Uri? -> if (uri != null) {
                val token = readTokenFromUri(uri)
                if (token != null) {
                    saveTokenToFile(token)
                    apiToken = token
                    showTokenDialog = false
                }
            }
        }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initApi()
        enableEdgeToEdge()
        setContent {
            AndroidTestTheme {
                // Create a folder to store the configuration file
                val folder = File(this.filesDir, "configuration")
                if (!folder.exists()) {
                    folder.mkdir()
                }
                // Initialization for API Token
                apiToken = loadApiTokenFromFile()
                println("Token read: $apiToken")

                var dev by remember { mutableStateOf(false) }
                var tokenAvailable by remember { mutableStateOf(TOKEN != null && TOKEN != "") }

                // Dev Build switch for testing purposes
                if(BuildConfig.IS_DEVELOPMENT){
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                    ) {
                        Text("DevMode")
                        Checkbox(
                            checked = dev,
                            onCheckedChange = { dev = it }
                        )
                    }
                }

                Column(
                    modifier = Modifier.fillMaxSize(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center,
                ) {
                    // AdaptiveLight Activity Launcher
                    ActivityLauncher(
                        this@MainActivity,
                        Intent(
                            this@MainActivity,
                            AdaptiveLightActivity::class.java
                        ),
                        "Adaptive Lighting",
                        tokenAvailable,
                    )
                    // LED: SLED: Activity Launcher
                    ActivityLauncher(
                        this@MainActivity,
                        Intent(
                            this@MainActivity,
                            LedActivity::class.java
                        ),
                        "LED Demo",
                    )
                    // NFC: Activity Launcher
                    ActivityLauncher(
                        this@MainActivity,
                        Intent(
                            this@MainActivity,
                            NfcActivity::class.java
                        ),
                        "NFC",
                    )

                    // Internal Testing Activity Launchers
                    if(dev) {
                        ActivityLauncher(
                            this@MainActivity,
                            Intent(
                                this@MainActivity,
                                TestActivity::class.java
                            ),
                            "Test"
                        )
                        ActivityLauncher(
                            this@MainActivity,
                            Intent(
                                this@MainActivity,
                                ApiDemoActivity::class.java
                            ),
                            "ProDVX API Demo"
                        )
                    }

                    // Start API Token insertion dialog
                    Button(onClick = { showTokenDialog = true }, modifier = Modifier.padding(16.dp)) { Text("Set new API Token")}
                    if (showTokenDialog) {
                        TokenInputDialog(
                            onDismissRequest = { showTokenDialog = false },
                            onConfirm = { inputToken ->
                                saveTokenToFile(inputToken)
                                apiToken = inputToken
                                showTokenDialog = false
                            },
                            onPickFile = {
                                pickFileLauncher.launch(arrayOf("application/json"))
                            }
                        )
                    }
                }

                // Update the API token when it is updated through the application
                LaunchedEffect(key1 = apiToken) {
                    if (apiToken != null) {
                        updateToken(apiToken!!)
                    }
                }
            }
        }
    }

    /**
     * Helper function to load the API token from the application's file directory if it exists.
     */
    private fun loadApiTokenFromFile(tokenFile: File? = null): String? {
        val file = tokenFile ?: File(filesDir, "configuration/configuration.json")
        return if (file.exists()) {
            try {
                val jsonString = file.bufferedReader().useLines { lines ->
                    lines.joinToString("")
                }
                val jsonObject = JSONObject(jsonString)
                jsonObject.getString("api_token")
            } catch (e: Exception) {
                null
            }
        } else {
            null
        }
    }

    /**
     * Helper function to save the inserted token to file
     */
    private fun saveTokenToFile(token: String) {
        val folder = File(filesDir, "configuration")
        if (!folder.exists()) folder.mkdir()
        val file = File(folder, "configuration.json")
        try {
            val jsonObject = JSONObject()
            jsonObject.put("api_token", token)
            file.writeText(jsonObject.toString())
        } catch (e: Exception) {
            // Handle save error
        }
    }

    /**
     * Helper function to read the API token from a JSON file
     */
    private fun readTokenFromUri(uri: Uri): String? {
        return try {
            contentResolver.openInputStream(uri)?.bufferedReader().use { reader ->
                val jsonString = reader?.readText()
                if (jsonString != null) {
                    val jsonObject = JSONObject(jsonString)
                    jsonObject.getString("api_token")
                } else {
                    null
                }
            }
        } catch (e: Exception) {
            // Handle parsing or file reading errors
            null
        }
    }

}

/**
 * Composable that launches an activity using a Button
 */
@Composable
fun ActivityLauncher(ctx: Context, int: Intent, text: String, enabled: Boolean = true) {
    Button(
        modifier = Modifier.padding(16.dp),
        onClick = { ctx.startActivity(int) },
        enabled = enabled
    ){
        Text(text)
    }
}

/**
 * Composable that displays a dialog for inserting the API token using either a filepicker or text input
 */
@Composable
fun TokenInputDialog(
    onDismissRequest: () -> Unit,
    onConfirm: (String) -> Unit,
    onPickFile: () -> Unit
) {
    var tokenInput by remember { mutableStateOf("") }
    AlertDialog(
        onDismissRequest = onDismissRequest,
        title = { Text("API Token Not Found") },
        text = {
            Column {
                TextField(
                    value = tokenInput,
                    onValueChange = { tokenInput = it },
                    label = { Text("Enter or paste API token") }
                )
            }
        },
        confirmButton = {
            Row {
                Button(onClick = onPickFile) {
                    Text("Pick from file")
                }
                Spacer(modifier = Modifier.width(8.dp))
                Button(
                    onClick = { onConfirm(tokenInput) },
                    enabled = tokenInput.isNotBlank()
                ) {
                    Text("Confirm")
                }
            }
        }
    )
}