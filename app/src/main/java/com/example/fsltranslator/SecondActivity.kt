package com.example.fsltranslator

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity

class SecondActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_second)

        val guideBtn = findViewById<Button>(R.id.guideBtn)
        val translateBtn = findViewById<Button>(R.id.translateBtn)
        val feedbackBtn = findViewById<Button>(R.id.feedbackBtn)
        val dataBtn = findViewById<Button>(R.id.dataBtn)

        guideBtn.setOnClickListener {
            val intent = Intent(this, GuideActivity::class.java)
            startActivity(intent)
            }
        translateBtn.setOnClickListener {
            val trans = Intent(this, TranslateActivity::class.java)
            startActivity(trans)
            }
        feedbackBtn.setOnClickListener {
            val url = "https://www.youtube.com/watch?v=dQw4w9WgXcQ"
            val i = Intent(Intent.ACTION_VIEW)
            i.data = Uri.parse(url)
            startActivity(i)
        }
        }
}
