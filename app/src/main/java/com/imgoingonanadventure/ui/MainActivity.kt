package com.imgoingonanadventure.ui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.imgoingontheadventure.R
import com.imgoingonanadventure.ui.tracker.TrackerFragment

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        supportFragmentManager.beginTransaction()
            .replace(R.id.container, TrackerFragment.newInstance())
            .commit()
    }
}
