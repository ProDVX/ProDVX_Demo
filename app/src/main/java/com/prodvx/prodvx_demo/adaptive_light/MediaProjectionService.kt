// MediaProjectionService.kt
package com.prodvx.prodvx_demo.adaptive_light

import android.app.Activity
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Context
import android.content.Intent
import android.content.pm.ServiceInfo
import android.graphics.PixelFormat
import android.hardware.display.DisplayManager
import android.hardware.display.VirtualDisplay
import android.media.ImageReader
import android.media.projection.MediaProjection
import android.media.projection.MediaProjectionManager
import android.os.Binder
import android.os.Build
import android.os.IBinder
import android.util.DisplayMetrics
import android.util.Log
import android.view.Surface
import android.view.WindowManager
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.core.app.NotificationCompat
import com.prodvx.prodvx_demo.LRGB
import com.prodvx.prodvx_demo.R
import com.prodvx.prodvx_demo.api.sendRequest
import io.ktor.http.HttpMethod
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch

class MediaProjectionService : Service() {

    var verticalOffset by mutableIntStateOf(40)
    var horizontalOffset by mutableIntStateOf(40)

    var fuzz by mutableStateOf(true)

    var fuzzSampleSize by mutableIntStateOf(4)

    var sseries by mutableStateOf(true)

    private var isCapturing = false
    private lateinit var mediaProjection: MediaProjection
    private var virtualDisplay: VirtualDisplay? = null
    private var imageReader: ImageReader? = null
    private val coroutineScope = CoroutineScope(Dispatchers.Default)
    private var captureJob: Job? = null

    override fun onCreate() {
        super.onCreate()

        val channel = NotificationChannel(
            "screen_capture_channel",
            "Screen Capture",
            NotificationManager.IMPORTANCE_DEFAULT
        )

        val manager = getSystemService(NotificationManager::class.java)
        manager.createNotificationChannel(channel)
        val notification = NotificationCompat.Builder(this, "screen_capture_channel")
            .setContentTitle("Screen Capturing")
            .setContentText("Your screen is being captured.")
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .build()

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            startForeground(1, notification, ServiceInfo.FOREGROUND_SERVICE_TYPE_MEDIA_PROJECTION)
        } else {
            startForeground(1, notification)
        }
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val resultCode = intent?.getIntExtra("resultCode", Activity.RESULT_CANCELED)
        val data = intent?.getParcelableExtra<Intent>("data")

        if (resultCode != Activity.RESULT_CANCELED && data != null) {
            val mediaProjectionManager = getSystemService(Context.MEDIA_PROJECTION_SERVICE) as MediaProjectionManager
            mediaProjection = mediaProjectionManager.getMediaProjection(resultCode!!, data)!!
            startCapture()
        }
        return START_NOT_STICKY
    }

    private fun startCapture() {
        // Check the guard variable to prevent recursive calls
        if (isCapturing) {
            return
        }
        isCapturing = true

        val windowManager = getSystemService(Context.WINDOW_SERVICE) as WindowManager
        val displayMetrics = DisplayMetrics()
        windowManager.defaultDisplay.getMetrics(displayMetrics)

        val currentWidth = displayMetrics.widthPixels
        val currentHeight = displayMetrics.heightPixels

        // Create a new ImageReader if it doesn't exist or if dimensions have changed
        if (imageReader == null || imageReader?.width != currentWidth || imageReader?.height != currentHeight) {
            imageReader?.close()
            imageReader = ImageReader.newInstance(currentWidth, currentHeight, PixelFormat.RGBA_8888, 2)
        }

        // Cancel any previous capture job before starting a new one
        captureJob?.cancel()

        virtualDisplay?.release()
        virtualDisplay = mediaProjection.createVirtualDisplay(
            "ScreenCapture",
            currentWidth,
            currentHeight,
            displayMetrics.densityDpi,
            DisplayManager.VIRTUAL_DISPLAY_FLAG_AUTO_MIRROR,
            imageReader?.surface,
            object : VirtualDisplay.Callback() {
                override fun onResumed() {
                    super.onResumed()
                    captureJob = coroutineScope.launch {
                        while (isActive) {
                            captureFrame()
                            delay(100)
                        }
                        isCapturing = false
                    }
                    startCapture()
                }

                override fun onStopped() {
                    stopSelf()
                }
            },
            null
        )
    }

    private suspend fun captureFrame() {
        if (captureJob == null || !captureJob!!.isActive) {
            return
        }

        val windowManager = getSystemService(Context.WINDOW_SERVICE) as WindowManager
        val orient = windowManager.defaultDisplay.rotation

        // Use the nullable imageReader with a safe call
        val image = imageReader?.acquireLatestImage()
        if (image == null) return

        try {
            val plane = image.planes[0]
            val buffer = plane.buffer

            val currentWidth = image.width
            val currentHeight = image.height
            val pixels = IntArray(currentWidth * currentHeight)
            buffer.asIntBuffer().get(pixels)

            val averagedPixels = mutableListOf<Int>()
            val samplePointCoordinates =
                getSamplePointCoordinates(currentWidth, currentHeight, orient)

            println("Fuzzing values: $fuzz")
            if (fuzz) {
                val sampleSize = fuzzSampleSize
                for ((xPos, yPos) in samplePointCoordinates) {
                    val currentSamplePixels = mutableListOf<Int>()
                    for (y in -sampleSize until sampleSize) {
                        for (x in -sampleSize until sampleSize) {
                            val finalYPos = yPos + y
                            val finalXPos = xPos + x
                            if (finalYPos in 0 until currentHeight && finalXPos in 0 until currentWidth) {
                                currentSamplePixels.add(pixels[finalYPos * currentWidth + finalXPos])
                            }
                        }
                    }
                    averagedPixels.add(averagePixels(currentSamplePixels))
                }
            } else {
                for ((xPos, yPos) in samplePointCoordinates) {
                    if (yPos in 0 until currentHeight && xPos in 0 until currentWidth) {
                        averagedPixels.add(pixels[yPos * currentWidth + xPos])
                    }
                }
            }

            val finalLEDValues = mapSamplesToLeds(averagedPixels, orient)
            val lrgbValues = processPixelsToLRGB(finalLEDValues)
            setLRGB(lrgbValues)
        } finally {
            image.close()
        }
    }

    private fun getSamplePointCoordinates(width: Int, height: Int, rotation: Int): List<Pair<Int, Int>> {
        println("Vertical offset of $verticalOffset")
        println("Horizontal offset of $horizontalOffset")

        val points = mutableListOf<Pair<Int, Int>>()

        // Generate points for the standard landscape (ROTATION_0) orientation
        val landscapeWidth = if (rotation == Surface.ROTATION_0 || rotation == Surface.ROTATION_180) width else height
        val landscapeHeight = if (rotation == Surface.ROTATION_0 || rotation == Surface.ROTATION_180) height else width

        val boundaryWidth = landscapeWidth - 2 * horizontalOffset
        val boundaryHeight = landscapeHeight - 2 * verticalOffset

        when(rotation) {
            Surface.ROTATION_0, Surface.ROTATION_180 -> {
                // Left side points (top to bottom) starting with the top-left corner
                points.add(Pair(horizontalOffset, verticalOffset))
                points.add(Pair(horizontalOffset, verticalOffset + boundaryHeight / 4))
                points.add(Pair(horizontalOffset, verticalOffset + boundaryHeight / 2))
                points.add(Pair(horizontalOffset, verticalOffset + boundaryHeight / 4 * 3))

                // Bottom side points (left to right)
                points.add(Pair(horizontalOffset, landscapeHeight - verticalOffset))
                points.add(Pair(horizontalOffset + boundaryWidth / 4, landscapeHeight - verticalOffset))
                points.add(Pair(horizontalOffset + boundaryWidth / 2, landscapeHeight - verticalOffset))
                points.add(Pair(horizontalOffset + boundaryWidth / 4 * 3, landscapeHeight - verticalOffset))

                // Right side points (bottom to top)
                points.add(Pair(landscapeWidth - horizontalOffset, landscapeHeight - verticalOffset))
                points.add(Pair(landscapeWidth - horizontalOffset, verticalOffset + boundaryHeight / 4 * 3))
                points.add(Pair(landscapeWidth - horizontalOffset, verticalOffset + boundaryHeight / 2))
                points.add(Pair(landscapeWidth - horizontalOffset, verticalOffset + boundaryHeight / 4))

                // Top side points (right to left)
                points.add(Pair(landscapeWidth - horizontalOffset, verticalOffset))
                points.add(Pair(landscapeWidth - horizontalOffset - boundaryWidth / 4, verticalOffset))
                points.add(Pair(landscapeWidth - horizontalOffset - boundaryWidth / 2, verticalOffset))
                points.add(Pair(landscapeWidth - horizontalOffset - boundaryWidth / 4 * 3, verticalOffset))
            }
            Surface.ROTATION_90, Surface.ROTATION_270 -> {
                // Left side points (top to bottom) starting with the top-left corner
                points.add(Pair(verticalOffset, horizontalOffset))
                points.add(Pair(verticalOffset, horizontalOffset + boundaryHeight / 4))
                points.add(Pair(verticalOffset, horizontalOffset + boundaryHeight / 2))
                points.add(Pair(verticalOffset, horizontalOffset + boundaryHeight / 4 * 3))

                // Bottom side points (left to right)
                points.add(Pair(verticalOffset, landscapeHeight - horizontalOffset))
                points.add(Pair(verticalOffset + boundaryWidth / 4, landscapeHeight - horizontalOffset))
                points.add(Pair(verticalOffset + boundaryWidth / 2, landscapeHeight - horizontalOffset))
                points.add(Pair(verticalOffset + boundaryWidth / 4 * 3, landscapeHeight - horizontalOffset))

                // Right side points (bottom to top)
                points.add(Pair(landscapeWidth - verticalOffset, landscapeHeight - horizontalOffset))
                points.add(Pair(landscapeWidth - verticalOffset, horizontalOffset + boundaryHeight / 4 * 3))
                points.add(Pair(landscapeWidth - verticalOffset, horizontalOffset + boundaryHeight / 2))
                points.add(Pair(landscapeWidth - verticalOffset, horizontalOffset + boundaryHeight / 4))

                // Top side points (right to left)
                points.add(Pair(landscapeWidth - verticalOffset, horizontalOffset))
                points.add(Pair(landscapeWidth - verticalOffset - boundaryWidth / 4, horizontalOffset))
                points.add(Pair(landscapeWidth - verticalOffset - boundaryWidth / 2, horizontalOffset))
                points.add(Pair(landscapeWidth - verticalOffset - boundaryWidth / 4 * 3, horizontalOffset))
            }
        }

        // Rotate the generated points based on the current orientation
        return points.map { (x, y) -> getRotatedPoint(x, y, rotation, width, height) }
    }

    private fun getRotatedPoint(x: Int, y: Int, rotation: Int, width: Int, height: Int): Pair<Int, Int> {
        return when (rotation) {
            Surface.ROTATION_0 -> Pair(x, y)
            Surface.ROTATION_90 -> Pair(width - 1 - y, x)
            Surface.ROTATION_180 -> Pair(width - 1 - x, height - 1 - y)
            Surface.ROTATION_270 -> Pair(y, height - 1 - x)
            else -> Pair(x, y)
        }
    }

    fun averagePixels(pixels: List<Int>): Int {
        if (pixels.isEmpty()) return 0

        var redSum = 0
        var greenSum = 0
        var blueSum = 0

        for (pixel in pixels) {
            redSum += (pixel shr 16) and 0xFF
            greenSum += (pixel shr 8) and 0xFF
            blueSum += pixel and 0xFF
        }

        val count = pixels.size
        val avgRed = redSum / count
        val avgGreen = greenSum / count
        val avgBlue = blueSum / count

        // Reconstruct the averaged pixel as a single integer
        return (0xFF shl 24) or (avgRed shl 16) or (avgGreen shl 8) or avgBlue
    }

    fun mapSamplesToLeds(sampleValues: List<Int>, orient: Int ): List<Int> {
        val finalValues = MutableList( if(!sseries) 52 else 54) { 0 }

        val ledSamplesCountPerSide = sampleValues.size / 4 + 1
        val longSideLedCount = 16
        val shortSideLedCount = 11

        val topLeft = processPixelsToLRGB(listOf(sampleValues[0]))
        println("Top Left pixel: LRGB(${topLeft[0].L},${topLeft[0].R},${topLeft[0].G},${topLeft[0].B})")

        when(orient) {
            Surface.ROTATION_0, Surface.ROTATION_180 -> {

                // Physical Left Side (starts at index 0)
                mapSide(finalValues, listOf(sampleValues[0], sampleValues[1], sampleValues[2], sampleValues[3], sampleValues[4]), shortSideLedCount-1, ledSamplesCountPerSide, 0)
                // Physical Bottom Side
                mapSide(finalValues, sampleValues.subList(4, 9), longSideLedCount, ledSamplesCountPerSide, 10)
                // Physical Right Side
                mapSide(finalValues, sampleValues.subList(8, 13), shortSideLedCount, ledSamplesCountPerSide, 26)
                // Physical Top Side
                mapSide(finalValues, listOf(sampleValues[12], sampleValues[13], sampleValues[14], sampleValues[15], sampleValues[0]), longSideLedCount, ledSamplesCountPerSide, 37)
                // Wrap-around LED at index 53
                mapSide(finalValues, listOf(sampleValues[0]), 1, 1, 53)
            }
            Surface.ROTATION_90, Surface.ROTATION_270 -> {
                // Physical Left Side (starts at index 0)
                mapSide(finalValues, sampleValues.subList(8, 13), shortSideLedCount -1, ledSamplesCountPerSide, 0)
                // Physical Bottom Side
                mapSide(finalValues, listOf(sampleValues[12], sampleValues[13], sampleValues[14], sampleValues[15], sampleValues[0]), longSideLedCount, ledSamplesCountPerSide, 10)
                // Physical Right Side
                mapSide(finalValues, sampleValues.subList(0, 5), shortSideLedCount, ledSamplesCountPerSide, 26)
                // Physical Top Side
                mapSide(finalValues, sampleValues.subList(4, 9), longSideLedCount, ledSamplesCountPerSide, 37)
                // Wrap-around LED at index 53
                mapSide(finalValues, listOf(sampleValues[8]), 1, 1, 53)
            }
        }
        return finalValues
    }


    private fun mapSide(
        finalValues: MutableList<Int>,
        samples: List<Int>,
        ledCount: Int,
        sampleCount: Int,
        startLedIndex: Int
    ) {
        val baseChunkSize = ledCount / sampleCount
        var remainder = ledCount % sampleCount
        var currentLedIndex = startLedIndex

        for (sample in samples) {
            val currentChunkSize = baseChunkSize + if (remainder-- > 0) 1 else 0
            for (i in 0 until currentChunkSize) {
                finalValues[currentLedIndex++] = sample
            }
        }
    }

    fun processPixelsToLRGB(pixels: List<Int>): List<LRGB> {
        val lrgbList = mutableListOf<LRGB>()
        for (pixel in pixels) {
            val blue = (pixel shr 16) and 0xFF
            val green = (pixel shr 8) and 0xFF
            val red = pixel and 0xFF
            val l = (red + green + blue) / 3
            lrgbList.add(LRGB(255, red, green, blue))
        }
        return lrgbList
    }
    suspend fun setLRGB(values: List<LRGB>) {
        val res = values.joinToString(separator = ";") {
            "${it.L},${it.R},${it.G},${it.B}"
        }
        println(sendRequest(HttpMethod.Get, "/setLeds", mapOf ("lrgb" to res)))
    }

    inner class MyBinder : Binder() {
        fun getService(): MediaProjectionService = this@MediaProjectionService
    }

    private val binder = MyBinder()


    override fun onDestroy() {
        super.onDestroy()
        if (::mediaProjection.isInitialized) {
            mediaProjection.stop()
        }

        captureJob?.cancel()
        virtualDisplay?.release()
        imageReader?.close()
        isCapturing = false
        stopForeground(STOP_FOREGROUND_REMOVE)
    }

    override fun onBind(intent: Intent): IBinder {
        // Must call super.onBind for LifecycleService
        Log.d("MediaProjectionService", "Service bound")
        return binder
    }

    override fun onUnbind(intent: Intent?): Boolean {
        return super.onUnbind(intent)
    }

}