package com.example.firebasegroupapp9

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.auth.FirebaseAuth

class ForgotPasswordActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_forgot_password_activity)

        val txtEmail: TextInputEditText = findViewById(R.id.txtEmail)
        val btnResetPassword: Button = findViewById(R.id.btnResetPassword)
        val txtLogin: TextView = findViewById(R.id.txtLogin)
        val txtRegister: TextView = findViewById(R.id.txtRegister)

        txtLogin.setOnClickListener {
            startActivity(Intent(this@ForgotPasswordActivity, LoginActivity::class.java))
        }

        txtRegister.setOnClickListener {
            startActivity(Intent(this@ForgotPasswordActivity, RegisterActivity::class.java))
        }

        btnResetPassword.setOnClickListener {
            val email = txtEmail.text.toString().trim()
            if (email.isEmpty()) {
                txtEmail.error = "Email is required."
            } else {
                FirebaseAuth.getInstance().sendPasswordResetEmail(txtEmail.text.toString())
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            Toast.makeText(this, "Password Reset Email Sent", Toast.LENGTH_LONG)
                                .show()
                            startActivity(
                                Intent(
                                    this@ForgotPasswordActivity,
                                    LoginActivity::class.java
                                )
                            )
                        } else {
                            Toast.makeText(this, "Failed to send Email", Toast.LENGTH_LONG).show()
                        }
                    }
            }
        }
    }
}