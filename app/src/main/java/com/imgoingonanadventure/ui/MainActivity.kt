package com.imgoingonanadventure.ui

import android.graphics.Color
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.imgoingonanadventure.ui.tracker.TrackerFragment
import com.imgoingontheadventure.R

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.container)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            v.setBackgroundColor(Color.TRANSPARENT)
            insets
        }
        supportFragmentManager.beginTransaction()
            .replace(R.id.container, TrackerFragment.newInstance())
            .commit()
    }
}
