package com.shay.testapplication

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.shay.testapplication.databinding.ActivityMainBinding
import java.text.CollationElementIterator
import java.util.*

class MainActivity : AppCompatActivity() {
    private lateinit var _binding:ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //setContentView(R.layout.activity_main)
        _binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(_binding.root)

        //_binding.text.setText()
    }
}