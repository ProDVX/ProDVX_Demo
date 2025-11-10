package com.prodvx.prodvx_demo

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
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
import com.prodvx.prodvx_demo.led.LedActivity
import com.prodvx.prodvx_demo.nfc.NfcActivity
import com.prodvx.prodvx_demo.test.TestActivity
import com.prodvx.prodvx_demo.ui.theme.AndroidTestTheme
import org.json.JSONObject
import java.io.File

class MainActivity : ComponentActivity() {

    private var apiToken by mutableStateOf<String?>(null)
    private var showTokenDialog by mutableStateOf(false)

    private val pickFileLauncher = registerForActivityResult(
        ActivityResultContracts.OpenDocument()
    ) { uri: Uri? ->
        if (uri != null) {
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
                val folder = File(this.filesDir, "configuration")
                if (!folder.exists()) {
                    folder.mkdir()
                }

                apiToken = loadApiTokenFromFile()
                println("Token read: $apiToken")

                var dev by remember{mutableStateOf(false)}

                /**
                 * Dev Build
                 */
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
                    val adaptiveLightIntent = Intent(this@MainActivity, AdaptiveLightActivity::class.java)
                    ActivityLauncher(this@MainActivity, adaptiveLightIntent, "Adaptive Lighting", TOKEN != null && TOKEN != "")
                    val ledIntent = Intent(this@MainActivity, LedActivity::class.java)
                    ActivityLauncher(this@MainActivity, ledIntent, "LED Demo")

                    val nfcIntent = Intent(this@MainActivity, NfcActivity::class.java)
                    ActivityLauncher(this@MainActivity, nfcIntent, "NFC")

                    if(dev) {
                        val testIntent = Intent(this@MainActivity, TestActivity::class.java)
                        ActivityLauncher(this@MainActivity, testIntent, "Test")
                        Button(
                            modifier = Modifier.padding(16.dp),
                            onClick = {
                                val intent = Intent(Settings.ACTION_WIRELESS_SETTINGS)
                                startActivity(intent)
                        }){ Text("ConnectionSettings") }
                    }

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

                    Button(onClick = { showTokenDialog = true }) { Text("Set new API Token")}
                }

                LaunchedEffect(key1 = apiToken) {
                    if (apiToken != null) {
                        updateToken(apiToken!!)
                    }
                }
            }
        }
    }

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