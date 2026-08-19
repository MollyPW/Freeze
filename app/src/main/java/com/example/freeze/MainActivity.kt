package com.example.freeze

import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val layout = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(60, 200, 60, 60)
        }

        val title = TextView(this).apply {
            text = "Freeze"
            textSize = 24f
            setPadding(0, 0, 0, 40)
        }

        val instructions = TextView(this).apply {
            text = "To use Freeze, turn it on in Accessibility settings. " +
                    "Then a 'Pause' button will float on your screen.\n\n" +
                    "Tap Pause to freeze the screen.\n" +
                    "Hold Pause down to hide it.\n" +
                    "Double-press a volume button to unfreeze."
            textSize = 16f
            setPadding(0, 0, 0, 40)
        }

        val openSettingsButton = Button(this).apply {
            text = "Open Accessibility Settings"
            setOnClickListener {
                startActivity(Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS))
            }
        }

        layout.addView(title)
        layout.addView(instructions)
        layout.addView(openSettingsButton)

        setContentView(layout)
    }
}