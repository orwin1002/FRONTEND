package com.example.evmate

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.evmate.databinding.ActivityProfileBinding

class ProfileActivity : AppCompatActivity() {
    private lateinit var binding: ActivityProfileBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.tvName.text = "Sandhya G"
    }
}
