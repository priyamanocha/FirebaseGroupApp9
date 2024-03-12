package com.example.firebasegroupapp9

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.widget.Toolbar
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference

class DetailActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail)

        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)

        val product = intent.getSerializableExtra("productDetails") as Product

        val txtName: TextView = findViewById(R.id.txtName)
        txtName.text = product.name

        val txtDescription: TextView = findViewById(R.id.txtDescription)
        txtDescription.text = product.description

        val txtPrice: TextView = findViewById(R.id.txtPrice)
        txtPrice.text = "Price: $ " + product.price

        val txtManufacturer: TextView = findViewById(R.id.txtManufacturer)
        txtManufacturer.text = "Sold By: " + product.manufacturer

        val txtSize: TextView = findViewById(R.id.txtSize)
        txtSize.text = "Size: " + product.size

        val txtFullDescription: TextView = findViewById(R.id.txtFullDescription)
        txtFullDescription.text = product.fullDescription

        val btnAddToCart: Button = findViewById(R.id.btnAddToCart)
        val databaseReference: DatabaseReference =
            FirebaseDatabase.getInstance().getReference("cart")
        val firebaseUser = FirebaseAuth.getInstance().currentUser

        if (firebaseUser != null) {
            databaseReference.child(firebaseUser.uid).child(product.id)
                .addListenerForSingleValueEvent(object :
                    ValueEventListener {
                    override fun onDataChange(dataSnapshot: DataSnapshot) {
                        if (dataSnapshot.exists()) {
                            btnAddToCart.text = "Added"
                        }
                    }

                    override fun onCancelled(error: DatabaseError) {
                        Log.e("Detail Activity", error.message)
                    }
                })
        }
        btnAddToCart.setOnClickListener {
            if (firebaseUser != null) {
                if (btnAddToCart.text.equals("Add to Cart")) {
                    val cartItem = Cart(product.id, product.name, product.price, 1, product.url)
                    databaseReference.child(firebaseUser.uid).child(product.id).setValue(cartItem)
                        .addOnCompleteListener { task ->
                            if (task.isSuccessful) {
                                btnAddToCart.text = "Added"
                                Toast.makeText(this, "Product Added To Cart", Toast.LENGTH_LONG)
                                    .show()
                            }
                        }.addOnFailureListener {
                            Log.e("Detail Activity", it.localizedMessage.toString())
                            Toast.makeText(this, it.localizedMessage, Toast.LENGTH_LONG).show()
                        }

                }
            }
        }
        val storageRef: StorageReference =
            FirebaseStorage.getInstance().getReferenceFromUrl(product.url)
        Glide.with(this).load(storageRef).into(findViewById(R.id.imgProduct))
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.nav_logout -> {
                Toast.makeText(this, "User Logged Out", Toast.LENGTH_LONG).show()
                FirebaseAuth.getInstance().signOut()
                val homeIntent = Intent(this, MainActivity::class.java)
                startActivity(homeIntent)
                finish()
            }

            R.id.nav_product -> {
                val mainIntent = Intent(this, ProductActivity::class.java)
                startActivity(mainIntent)
            }

            R.id.nav_cart -> {
                val cartIntent = Intent(this, CartActivity::class.java)
                startActivity(cartIntent)
            }
        }
        return true
    }
}