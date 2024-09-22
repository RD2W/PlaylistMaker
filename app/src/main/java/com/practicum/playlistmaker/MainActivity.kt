package com.practicum.playlistmaker

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.practicum.playlistmaker.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        enableEdgeToEdge()

        binding.btSettings.setOnClickListener {
            val msg = "Вы нажали на кнопку ${binding.btSettings.text}."
            Toast.makeText(this@MainActivity, msg, Toast.LENGTH_SHORT).show()
            Log.d("clickOnButton", msg)
        }
    }
}