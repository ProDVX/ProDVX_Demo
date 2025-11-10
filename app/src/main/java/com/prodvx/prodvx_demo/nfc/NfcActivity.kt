package com.prodvx.prodvx_demo.nfc

import android.annotation.SuppressLint
import android.app.Activity
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.nfc.NfcAdapter
import android.nfc.Tag
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.prodvx.prodvx_demo.ui.theme.AndroidTestTheme

class NfcActivity : ComponentActivity() {
    private val TAG = "NfcActivity"
    private var nfcAdapter: NfcAdapter? = null
    private var nfcIdState by mutableStateOf("Waiting for NFC Tag...")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        nfcAdapter = NfcAdapter.getDefaultAdapter(this)
        setContent {
            AndroidTestTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    NfcScanScreen(nfcId = nfcIdState)
                }
            }
        }
    }

    @SuppressLint("UnsafeIntentLaunch")
    override fun onResume() {
        super.onResume()
        if(nfcAdapter == null) {
            return
        }

        if (nfcAdapter!!.isEnabled) {
            intent = Intent(this, NfcActivity::class.java)
            intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP)

            nfcAdapter!!.enableForegroundDispatch(this,
                PendingIntent.getActivity(
                    this, 0, intent, PendingIntent.FLAG_MUTABLE)
                , null, null)

        }
    }

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        if (intent.action == NfcAdapter.ACTION_TAG_DISCOVERED ||
            intent.action == NfcAdapter.ACTION_NDEF_DISCOVERED ||
            intent.action == NfcAdapter.ACTION_TECH_DISCOVERED
        ) {
            val tag: Tag? = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG, Tag::class.java)

            tag?.id?.let { idBytes ->
                val nfcTagId = bytesToHex(idBytes)
                nfcIdState = "Tag ID: $nfcTagId"
            }
        }
    }

    override fun onPause() {
        super.onPause()
        if (nfcAdapter?.isEnabled == true) {
            nfcAdapter?.disableForegroundDispatch(this)
        }
    }

    private fun bytesToHex(bytes: ByteArray): String {
        val sb = StringBuilder()
        for (b in bytes) {
            sb.append(String.format("%02X", b))
        }
        return sb.toString()
    }
}

@Composable
fun NfcScanScreen(nfcId: String) {
    var nfcTagId by remember { mutableStateOf("") }
    val nfcResultLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val data: Intent? = result.data
            nfcTagId = data?.getStringExtra("nfc_tag_id") ?: "No ID Found"
        } else if (result.resultCode == Activity.RESULT_CANCELED) {
            nfcTagId = "NFC Scan Cancelled or Failed."
        }
    }

    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = "NFC Scanner", style = MaterialTheme.typography.headlineMedium)
        Text(
            text = nfcId,
            modifier = Modifier.padding(top = 16.dp),
            style = MaterialTheme.typography.bodyLarge
        )
        if(nfcId != "Waiting for NFC Tag...") {
            Text("Still scanning")
        }
        CircularProgressIndicator(modifier = Modifier.padding(top = 16.dp))
    }
}

@Composable
fun NfcReaderButton(
    currentNfcId: String,
    onLaunchNfcScan: (Context) -> Unit
) {
    val context = LocalContext.current

    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Button(
            onClick = { onLaunchNfcScan(context) },
            modifier = Modifier.padding(top = 24.dp)
        ) {
            Text("Scan NFC Tag")
        }

        TextField(
            value = currentNfcId,
            onValueChange = { /* NFC ID is read-only */ },
            label = { Text("Last Scanned NFC ID") },
            readOnly = true,
            modifier = Modifier.padding(top = 16.dp)
        )
    }
}
