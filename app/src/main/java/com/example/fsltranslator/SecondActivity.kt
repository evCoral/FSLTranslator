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

        val actionBar = supportActionBar

        actionBar!!.title = "Main"
        val guideBtn = findViewById<Button>(R.id.guideBtn)
        val trnsBtn = findViewById<Button>(R.id.translateBtn)
        val fdbckBtn = findViewById<Button>(R.id.feedbackBtn)

        guideBtn.setOnClickListener {
            val intent = Intent(this, GuideActivity::class.java)
            startActivity(intent)
            }
        trnsBtn.setOnClickListener {
            val trans = Intent(this, TranslateActivity::class.java)
            startActivity(trans)
            }
        fdbckBtn.setOnClickListener {
            val url = "https://www.youtube.com/watch?v=dQw4w9WgXcQ"
            val i = Intent(Intent.ACTION_VIEW)
            i.data = Uri.parse(url)
            startActivity(i)
        }
        }
}
