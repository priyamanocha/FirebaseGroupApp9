package com.example.firebasegroupapp9

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val txtRegister: TextView = findViewById(R.id.txtRegister)
        val btnLogin: Button = findViewById(R.id.btnLogin)
        val txtForgotPassword: TextView = findViewById(R.id.txtForgotPassword)

        txtRegister.setOnClickListener {
            startActivity(Intent(this@MainActivity, RegisterActivity::class.java))
        }
        btnLogin.setOnClickListener {
            startActivity(Intent(this@MainActivity, LoginActivity::class.java))
        }
        txtForgotPassword.setOnClickListener {
            startActivity(Intent(this@MainActivity, ForgotPasswordActivity::class.java))
        }
    }

}