package com.example.firebasegroupapp9

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.widget.Toolbar
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.firebase.ui.database.FirebaseRecyclerOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class CartActivity : AppCompatActivity() {
    private var adapter: CartAdapter? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_cart)

        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)

        val query = FirebaseDatabase.getInstance().reference.child("cart").child(
            FirebaseAuth.getInstance().currentUser?.uid.toString()
        )

        val txtEmptyCart: TextView = findViewById(R.id.txtEmptyCart)
        val btnCheckout: Button = findViewById(R.id.btnCheckout)
        val options =
            FirebaseRecyclerOptions.Builder<Cart>().setQuery(query, Cart::class.java)
                .build()
        adapter = CartAdapter(options)
        val rView: RecyclerView = findViewById(R.id.recView)
        rView.layoutManager = LinearLayoutManager(this)
        rView.adapter = adapter

        query.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                if (dataSnapshot.exists()) {
                    txtEmptyCart.visibility = View.GONE;
                    btnCheckout.visibility = View.VISIBLE;
                } else {
                    txtEmptyCart.visibility = View.VISIBLE;
                    btnCheckout.visibility = View.GONE;
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {
                // Handle error
            }
        })


        val gotoCheckout: Button = findViewById(R.id.btnCheckout)
        gotoCheckout.setOnClickListener {
            startActivity(Intent(this@CartActivity, CheckoutActivity::class.java))
        }
    }

    override fun onStart() {
        super.onStart()
        adapter?.startListening()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.nav_logout) {
            Toast.makeText(this, "User Logged Out", Toast.LENGTH_LONG).show()
            FirebaseAuth.getInstance().signOut()
            val homeIntent = Intent(this, MainActivity::class.java)
            startActivity(homeIntent)
            finish()
        } else if (item.itemId == R.id.nav_home) {
            val mainIntent = Intent(this, ProductActivity::class.java)
            startActivity(mainIntent)
        } else if (item.itemId == R.id.nav_cart) {
            val cartIntent = Intent(this, CartActivity::class.java)
            startActivity(cartIntent)
        }
        return true
    }
}