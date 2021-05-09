package com.mkpatir.androidswipeablebutton

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import android.widget.Toast
import com.mkpatir.androidswipeablebutton.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(LayoutInflater.from(this))
        setContentView(binding.root)

        binding.apply {
            swipeableButton.onSwipeCompleted = {
                Toast.makeText(this@MainActivity,"swipeCompleted",Toast.LENGTH_LONG).show()
            }

            swipe.setOnClickListener {
                swipeableButton.swipeButton()
            }

            clear.setOnClickListener {
                swipeableButton.clearSwipe()
            }
        }
    }
}