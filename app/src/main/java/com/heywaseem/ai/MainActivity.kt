
package com.heywaseem.ai

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.speech.RecognitionListener
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import android.view.Gravity
import android.graphics.Color
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat

class MainActivity : AppCompatActivity() {

    private lateinit var subtitle: TextView
    private var speechRecognizer: SpeechRecognizer? = null

    companion object {
        private const val MIC_PERMISSION_CODE = 100
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val layout = LinearLayout(this)
        layout.orientation = LinearLayout.VERTICAL
        layout.gravity = Gravity.CENTER
        layout.setPadding(30, 30, 30, 30)
        layout.setBackgroundColor(Color.rgb(10, 15, 30))

        val title = TextView(this)
        title.text = "HEY WASEEM AI"
        title.textSize = 30f
        title.setTextColor(Color.CYAN)
        title.gravity = Gravity.CENTER

        subtitle = TextView(this)
        subtitle.text = "آپ کا ذاتی AI اسسٹنٹ"
        subtitle.textSize = 20f
        subtitle.setTextColor(Color.WHITE)
        subtitle.gravity = Gravity.CENTER

        val button = Button(this)
        button.text = "🎤 بول کر حکم دیں"

        button.setOnClickListener {
            checkMicrophonePermission()
        }

        layout.addView(title)
        layout.addView(subtitle)
        layout.addView(button)

        setContentView(layout)
    }

    private fun checkMicrophonePermission() {
        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.RECORD_AUDIO
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            startListening()
        } else {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.RECORD_AUDIO),
                MIC_PERMISSION_CODE
            )
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(
            requestCode, permissions, grantResults
        )

        if (requestCode == MIC_PERMISSION_CODE) {
            if (grantResults.isNotEmpty() &&
                grantResults[0] == PackageManager.PERMISSION_GRANTED
            ) {
                startListening()
            } else {
                subtitle.text = "مائیکروفون کی اجازت درکار ہے"
            }
        }
    }

    private fun startListening() {
        if (!SpeechRecognizer.isRecognitionAvailable(this)) {
            subtitle.text = "آواز پہچاننے کی سہولت دستیاب نہیں"
            return
        }

        speechRecognizer?.destroy()
        speechRecognizer = SpeechRecognizer.createSpeechRecognizer(this)

        speechRecognizer?.setRecognitionListener(
            object : RecognitionListener {

                override fun onReadyForSpeech(params: Bundle?) {
                    subtitle.text = "جی، میں سن رہا ہوں..."
                }

                override fun onResults(results: Bundle?) {
                    val matches = results?.getStringArrayList(
                        SpeechRecognizer.RESULTS_RECOGNITION
                    )
                    subtitle.text = matches?.firstOrNull()
                        ?: "آواز سمجھ نہیں آئی"
                }

                override fun onError(error: Int) {
                    subtitle.text = "دوبارہ کوشش کریں"
                }

                override fun onBeginningOfSpeech() {}
                override fun onBufferReceived(buffer: ByteArray?) {}
                override fun onEndOfSpeech() {}
                override fun onEvent(eventType: Int, params: Bundle?) {}
                override fun onPartialResults(partialResults: Bundle?) {}
                override fun onRmsChanged(rmsdB: Float) {}
            }
        )

        val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH)
        intent.putExtra(
            RecognizerIntent.EXTRA_LANGUAGE,
            "ur-PK"
        )
        intent.putExtra(
            RecognizerIntent.EXTRA_LANGUAGE_MODEL,
            RecognizerIntent.LANGUAGE_MODEL_FREE_FORM
        )

        speechRecognizer?.startListening(intent)
    }

    override fun onDestroy() {
        speechRecognizer?.destroy()
        super.onDestroy()
    }
}
