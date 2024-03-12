package com.example.firebasegroupapp9

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.TextView

class Orderconfirmed : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_orderconfirmed)

        val intent = intent
        val o_id = intent.getStringExtra("ORDER_ID")
        val orderID: TextView = findViewById(R.id.orderID)
        orderID.text = "$o_id"

        val orderbtn: Button = findViewById(R.id.orderbtn)

        orderbtn.setOnClickListener{
            val intent = Intent(this@Orderconfirmed, ProductActivity::class.java)
            startActivity(intent)
        }

    }
}