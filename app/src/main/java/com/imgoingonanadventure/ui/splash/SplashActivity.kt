package com.imgoingonanadventure.ui.splash

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.imgoingonanadventure.App
import com.imgoingonanadventure.ui.MainActivity
import com.imgoingontheadventure.R

@SuppressLint("CustomSplashScreen")
//todo animation
class SplashActivity : AppCompatActivity() {

    private val viewModel: SplashViewModel by viewModels<SplashViewModel> {
        App.appModule.viewModuleModule.splashViewModelFactory
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        window.decorView.apply {
            systemUiVisibility =
                View.SYSTEM_UI_FLAG_HIDE_NAVIGATION or View.SYSTEM_UI_FLAG_FULLSCREEN
        }

        viewModel.checkEventChunk()

        viewModel.liveData.observe(this) { startActivity(Intent(this, MainActivity::class.java)) }
    }
}