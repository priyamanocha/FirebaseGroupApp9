package com.example.firebasegroupapp9

import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.firebase.ui.database.FirebaseRecyclerAdapter
import com.firebase.ui.database.FirebaseRecyclerOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference

class ProductAdapter(options: FirebaseRecyclerOptions<Product>) :
    FirebaseRecyclerAdapter<Product, ProductAdapter.MyViewHolder>(options) {
    class MyViewHolder(inflater: LayoutInflater, parent: ViewGroup) :
        RecyclerView.ViewHolder(inflater.inflate(R.layout.product_row_layout, parent, false)) {
        val imgProduct: ImageView = itemView.findViewById(R.id.imgProduct)
        val txtName: TextView = itemView.findViewById(R.id.txtName)
        val txtManufacturer: TextView = itemView.findViewById(R.id.txtManufacturer)
        val txtPrice: TextView = itemView.findViewById(R.id.txtPrice)
        val txtDescription: TextView = itemView.findViewById(R.id.txtDescription)
        val cardView: CardView = itemView.findViewById(R.id.cardView)
        val btnAddToCart: Button = itemView.findViewById(R.id.btnAddToCart)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return MyViewHolder(inflater, parent)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int, model: Product) {

        holder.txtName.text = model.name
        holder.txtManufacturer.text = model.manufacturer
        holder.txtPrice.text = model.price.toString()
        holder.txtDescription.text = model.description

        val storageRef: StorageReference =
            FirebaseStorage.getInstance().getReferenceFromUrl(model.url)
        Glide.with(holder.imgProduct.context).load(storageRef).into(holder.imgProduct)

        holder.cardView.setOnClickListener { view ->
            val intent = Intent(
                view.context,
                DetailActivity::class.java
            )
            intent.putExtra("productDetails", model)
            view.context.startActivity(intent)
        }


        val databaseReference: DatabaseReference =
            FirebaseDatabase.getInstance().getReference("cart")
        val firebaseUser = FirebaseAuth.getInstance().currentUser

        if (firebaseUser != null) {
            databaseReference.child(firebaseUser.uid).child(model.id)
                .addListenerForSingleValueEvent(object :
                    ValueEventListener {
                    override fun onDataChange(dataSnapshot: DataSnapshot) {
                        if (dataSnapshot.exists()) {
                            holder.btnAddToCart.text = "Added"
                        }
                    }

                    override fun onCancelled(error: DatabaseError) {
                        Log.e("Product Adapter", error.message)
                    }
                })
        }
        holder.btnAddToCart.setOnClickListener { view ->
            if (holder.btnAddToCart.text.equals("Add to Cart")) {
                if (firebaseUser != null) {
                    val cartItem = Cart(model.id, model.name, model.price, 1, model.url)
                    databaseReference.child(firebaseUser.uid).child(model.id).setValue(cartItem)
                        .addOnCompleteListener { task ->
                            if (task.isSuccessful) {
                                holder.btnAddToCart.text = "Added"
                                Toast.makeText(
                                    view.context,
                                    "Product Added To Cart",
                                    Toast.LENGTH_LONG
                                )
                                    .show()
                            }
                        }.addOnFailureListener {
                            Log.e("Product Adapter", it.localizedMessage.toString())
                            Toast.makeText(view.context, it.localizedMessage, Toast.LENGTH_LONG)
                                .show()
                        }

                }
            }
        }
    }
}