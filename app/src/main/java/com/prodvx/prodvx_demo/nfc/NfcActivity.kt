package com.prodvx.prodvx_demo.nfc

import android.annotation.SuppressLint
import android.app.Activity
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.nfc.NdefMessage
import android.nfc.NdefRecord
import android.nfc.NfcAdapter
import android.nfc.Tag
import android.os.Build
import android.os.Bundle
import android.os.Parcelable
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.prodvx.prodvx_demo.ui.theme.AndroidTestTheme

/**
 * NFC: Activity for scanning NFC Tags and displaying info
 */
class NfcActivity : ComponentActivity() {
    private val TAG = "NfcActivity"

    // State for NfcAdapter (required)
    private var nfcAdapter: NfcAdapter? = null
    private var nfcIdState by mutableStateOf("Waiting for NFC Tag...")
    private var nfcDataState by mutableStateOf("No Data")

    companion object {
        private const val ACTION_EXTERNAL_TAG = "TAG1_DISCOVERED"
    }

    /**
     * Retrieves the NfcAdapter as soon as the activity is created
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        nfcAdapter = NfcAdapter.getDefaultAdapter(this)

        // PNFC: Trigger the hardware initialization by setting the system property
        // This effectively "wakes up" the NFC driver
        setSystemProperty("persist.sys.set_ct_tag_type", "5")

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

        //PNFC: Required for Pogo NFC implementation
        registerExternalNfcReceiver()

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

    /**
     * Receives the NFC intent and tries to get the information from it.
     */
    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        if (intent.action == NfcAdapter.ACTION_TAG_DISCOVERED ||
            intent.action == NfcAdapter.ACTION_NDEF_DISCOVERED ||
            intent.action == NfcAdapter.ACTION_TECH_DISCOVERED
        ) {
            Log.i(TAG, "${intent.action}")
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
        unregisterReceiver(getIdReceiver)
        if (nfcAdapter?.isEnabled == true) {
            nfcAdapter?.disableForegroundDispatch(this)
        }
    }

    /**
     * Helper function to translate the bytes to hexadecimals for the ID
     */
    private fun bytesToHex(bytes: ByteArray): String {
        val sb = StringBuilder()
        for (b in bytes) {
            sb.append(String.format("%02X", b))
        }
        return sb.toString()
    }

    /**
     * Helper function to read data from NDEF cards
     */
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

    /**
     * PNFC: Required for PogoNFC implementation
     */
    private lateinit var getIdReceiver : GetIdReceiver
    private inner class GetIdReceiver : BroadcastReceiver() {
        /**
         * PNFC: Receives the intent sent by Pogo reader
         */
        override fun onReceive(context: Context, intent: Intent) {
            Log.d(TAG, "Intent action: ${intent.action}")
            if (intent.action == ACTION_EXTERNAL_TAG) {
                val tagId = intent.getStringExtra("data")
                val memoryData = intent.getStringExtra("memoryData")

                tagId?.let { nfcIdState = "Tag ID: $it" }
                memoryData?.let { nfcDataState = parseMemoryDataNdef(it) }
            }
        }
    }

    /**
     * PNFC: Registers the receiver for intents
     */
    @SuppressLint("UnspecifiedRegisterReceiverFlag")
    private fun registerExternalNfcReceiver() {
        val filter = IntentFilter(ACTION_EXTERNAL_TAG)
        getIdReceiver = GetIdReceiver()
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU){
            registerReceiver(getIdReceiver, filter, RECEIVER_EXPORTED)
        } else registerReceiver(getIdReceiver, filter)
    }

    /**
     * PNFC: Parses the data from an NDEF formatted card for data extraction
     */
    private fun parseMemoryDataNdef(hex: String): String {
        return try {
            val bytes = hex.chunked(2).map { it.toInt(16).toByte() }.toByteArray()

            // Skip first 8 header bytes, then find NDEF TLV (0x03)
            var ndefStart = -1
            for (i in 8 until bytes.size - 1) {
                if (bytes[i] == 0x03.toByte()) {
                    ndefStart = i
                    break
                }
            }

            if (ndefStart == -1) return "No NDEF data found"

            val ndefLength = bytes[ndefStart + 1].toInt() and 0xFF
            val availableBytes = bytes.size - (ndefStart + 2)
            val actualLength = minOf(ndefLength, availableBytes)

            Log.d(TAG, "ndefStart: $ndefStart, ndefLength: $ndefLength, actualLength: $actualLength")

            val ndefBytes = bytes.copyOfRange(ndefStart + 2, ndefStart + 2 + actualLength)
            Log.d(TAG, "ndefBytes hex: ${ndefBytes.joinToString("") { "%02X".format(it) }}")

            val ndefMessage = NdefMessage(ndefBytes)
            readNdefRecords(listOf(ndefMessage))
        } catch (e: Exception) {
            Log.e(TAG, "parseMemoryDataNdef error", e)
            "Error parsing data: ${e.localizedMessage}"
        }
    }

    /** PNFC:
     * Sets a system property using reflection.
     * This is necessary because SystemProperties is hidden from the public Android SDK.
     */
    private fun setSystemProperty(key: String, value: String) {
        try {
            val systemPropertiesClass = Class.forName("android.os.SystemProperties")
            val setMethod = systemPropertiesClass.getMethod("set", String::class.java, String::class.java)
            setMethod.invoke(null, key, value)
            Log.d(TAG, "Successfully set system property: $key = $value")
        } catch (e: Exception) {
            Log.e(TAG, "Failed to set system property: $key", e)
        }
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
        Button(
            onClick = {
                val localContext = LocalContext as Activity
                localContext.finish()
            },
            modifier = Modifier.padding(top = 16.dp)
        ) {
            Text("Go Back")
        }
    }
}