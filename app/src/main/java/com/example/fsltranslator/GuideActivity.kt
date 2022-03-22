package com.example.fsltranslator

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle

class GuideActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_guide)

        val actionBar = supportActionBar
        actionBar!!.title = "Guide"
        actionBar.setDisplayHomeAsUpEnabled(true)
        }
}
