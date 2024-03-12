package com.example.firebasegroupapp9

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.TextView

class OrderConfirmedActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_order_confirmed)

        val intent = intent
        val orderID: TextView = findViewById(R.id.orderID)
        orderID.text = "Order Id:" + intent.getStringExtra("ORDER_ID")

        val btnGoToProducts: Button = findViewById(R.id.btnProducts)

        btnGoToProducts.setOnClickListener{
            val intent = Intent(this@OrderConfirmedActivity, ProductActivity::class.java)
            startActivity(intent)
        }

    }
}