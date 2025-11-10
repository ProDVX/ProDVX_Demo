package com.prodvx.prodvx_demo.nfc

import android.annotation.SuppressLint
import android.app.PendingIntent
import android.content.Intent
import android.nfc.NdefMessage
import android.nfc.NdefRecord
import android.nfc.NfcAdapter
import android.nfc.Tag
import android.os.Build
import android.os.Bundle
import android.os.Parcelable
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.prodvx.prodvx_demo.ui.theme.AndroidTestTheme

class NfcActivity : ComponentActivity() {
    private val TAG = "NfcActivity"
    private var nfcAdapter: NfcAdapter? = null
    private var nfcIdState by mutableStateOf("Waiting for NFC Tag...")
    private var nfcDataState by mutableStateOf("No Data")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        nfcAdapter = NfcAdapter.getDefaultAdapter(this)
        setContent {
            AndroidTestTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    NfcScanScreen(nfcId = nfcIdState, nfcData = nfcDataState)
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

            val rawMessages = intent.getParcelableArrayExtra(NfcAdapter.EXTRA_NDEF_MESSAGES, Parcelable::class.java)
            if(rawMessages != null) {
                val messages: List<NdefMessage> = rawMessages
                    .filterIsInstance<NdefMessage>()
                    .toList()

                nfcDataState = readNdefRecords(messages)
            } else {
                nfcDataState = "No NDEF Data Found"
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

    private fun readNdefRecords(messages: List<NdefMessage>): String {
        val stringBuilder = StringBuilder()

        for (message in messages) {
            for (record in message.records) {
                if (record.tnf == NdefRecord.TNF_WELL_KNOWN && record.type.contentEquals(NdefRecord.RTD_TEXT)) {
                    try {
                        val payload = record.payload
                        val textEncoding = if ((payload[0].toInt() and 0x80) == 0) Charsets.UTF_8 else Charsets.UTF_16
                        val langugeCodeLength = payload[0].toInt() and 0x3F


                        val text = String(
                            payload,
                            1 + langugeCodeLength,
                            payload.size - 1 - langugeCodeLength,
                            textEncoding
                        )
                        stringBuilder.append("Text: $text\n")
                    } catch(e: Exception) {
                        stringBuilder.append("Error reading TEXT record: ${e.localizedMessage}\n")
                    }
                }
            }
        }
        return if (stringBuilder.isNotEmpty()) stringBuilder.toString().trim() else "No NDEF Text Records Found"
    }
}

@Composable
fun NfcScanScreen(nfcId: String, nfcData: String) {
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
        Text(
            text = nfcData,
            modifier = Modifier.padding(top = 8.dp),
            style = MaterialTheme.typography.bodyMedium
        )
        Spacer(modifier = Modifier.height(16.dp))
        if(nfcId != "Waiting for NFC Tag...") {
            Text("Still scanning")
        }
        CircularProgressIndicator(modifier = Modifier.padding(top = 16.dp))
    }
}
