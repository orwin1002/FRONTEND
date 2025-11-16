package com.example.evmate

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.example.evmate.databinding.ActivityLoginBinding

class LoginActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLoginBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.tvSignupLink.setOnClickListener {
            startActivity(Intent(this, SignupActivity::class.java))
        }

        binding.btnLogin.setOnClickListener {
            val email = binding.tiEmail.editText?.text?.toString().orEmpty()
            val pass = binding.tiPassword.editText?.text?.toString().orEmpty()
            if (email.isNotBlank() && pass.length >= 4) {
                startActivity(Intent(this, DashboardActivity::class.java))
                finish()
            } else {
                Toast.makeText(this, "Enter valid credentials", Toast.LENGTH_SHORT).show()
            }
        }

        val watcher = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                updateBtn()
            }
            override fun afterTextChanged(s: Editable?) {}
        }
        binding.tiEmail.editText?.addTextChangedListener(watcher)
        binding.tiPassword.editText?.addTextChangedListener(watcher)
        updateBtn()
    }

    private fun updateBtn() {
        val enabled = binding.tiEmail.editText?.text?.isNotEmpty() == true &&
                binding.tiPassword.editText?.text?.length ?: 0 >= 4
        binding.btnLogin.isEnabled = enabled
    }
}