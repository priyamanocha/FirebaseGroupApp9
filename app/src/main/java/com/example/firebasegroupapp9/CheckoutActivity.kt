package com.example.firebasegroupapp9

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import android.widget.Toolbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import java.util.regex.Pattern

class CheckoutActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_checkout)
        val toolbar: androidx.appcompat.widget.Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)

        val txtTotal: TextView = findViewById(R.id.txtTotal)

        val txtFirstname: EditText = findViewById(R.id.txtFirstname)
        val txtLastName: EditText = findViewById(R.id.txtLastName)
        val txtEmail: EditText = findViewById(R.id.txtEmail)
        val txtPhoneNumber: EditText = findViewById(R.id.txtPhoneNumber)
        val txtAddress: EditText = findViewById(R.id.txtAddress)
        val txtPostalCode: EditText = findViewById(R.id.txtPostalCode)
        val txtCity: EditText = findViewById(R.id.txtCity)
        val txtState: EditText = findViewById(R.id.txtState)
        val txtCountry: EditText = findViewById(R.id.txtCountry)
        val txtNameOnCard: EditText = findViewById(R.id.txtNameOnCard)
        val txtCardNumber: EditText = findViewById(R.id.txtCardNumber)
        val txtValidity: EditText = findViewById(R.id.txtValidity)
        val txtCvv: EditText = findViewById(R.id.txtCvv)
        val btnCheckout: Button = findViewById(R.id.btnCheckout)

        btnCheckout.setOnClickListener {

            val firstname = txtFirstname.text.toString()
            val lastName = txtLastName.text.toString()
            val email = txtEmail.text.toString()
            val phoneNumber = txtPhoneNumber.text.toString()
            val address = txtAddress.text.toString()
            val postalCode = txtPostalCode.text.toString()
            val city = txtCity.text.toString()
            val state = txtState.text.toString()
            val country = txtCountry.text.toString()
            val nameOnCard = txtNameOnCard.text.toString()
            val cardNumber = txtCardNumber.text.toString()
            val validity = txtValidity.text.toString()
            val cvv = txtCvv.text.toString()

            if (validateName(firstname) && validateName(lastName) && validateName(nameOnCard) && validateEmail(email) && validatePhoneNumber(phoneNumber)) {
                val userId = FirebaseAuth.getInstance().currentUser?.uid

                if (userId != null) {
                    val userRef =
                        FirebaseDatabase.getInstance().reference.child("orders").child(userId)
                    val userData = HashMap<String, Any>()
                    userData["firstName"] = firstname
                    userData["lastName"] = lastName
                    userData["email"] = email
                    userData["phoneNumber"] = phoneNumber
                    userData["address"] = address
                    userData["postalCode"] = postalCode
                    userData["city"] = city
                    userData["state"] = state
                    userData["country"] = country
                    userData["nameOnCard"] = nameOnCard
                    userData["cardNumber"] = cardNumber
                    userData["validity"] = validity
                    userData["cvv"] = cvv

                    userRef.setValue(userData)
                        .addOnSuccessListener {
                            Toast.makeText(
                                this,
                                "Your Order is successfully placed",
                                Toast.LENGTH_SHORT
                            ).show()
                            // You can navigate to a success screen or perform other actions here
                        }
                        .addOnFailureListener { exception ->
                            Toast.makeText(
                                this,
                                "Failed to place order: ${exception.message}",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                } else {
                    Toast.makeText(this, "User not authenticated", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
    private fun validateName(name: String): Boolean {
        if (name.isEmpty()) {
            Toast.makeText(this, "Please enter your name", Toast.LENGTH_SHORT).show()
            return false
        }
        if (!name.matches(Regex("^[a-zA-Z]+\$"))) {
            Toast.makeText(this, "Please enter a valid name", Toast.LENGTH_SHORT).show()
            return false
        }
        return true
    }
    private fun validateEmail(email: String): Boolean {
        val emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+"
        val pattern = Pattern.compile(emailPattern)
        val matcher = pattern.matcher(email)
        if(matcher.matches()){
            return matcher.matches()
        }
        else{
            Toast.makeText(this, "Please enter a valid email", Toast.LENGTH_SHORT).show()
            return false
        }
    }
    private fun validatePhoneNumber(phoneNumber: String): Boolean {
        val phonePattern = "^\\+1\\s\\d{3}\\s\\d{3}\\s\\d{4}\$"
        val pattern = Pattern.compile(phonePattern)
        val matcher = pattern.matcher(phoneNumber)
        if (matcher.matches()) {
            return matcher.matches()
        } else {
            Toast.makeText(this, "Please enter a valid Canadian phone number in the format +1 999 999 9999", Toast.LENGTH_SHORT).show()
            return false
        }
    }
}