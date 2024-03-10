package com.example.firebasegroupapp9

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import android.widget.Toolbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
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
        val txtProvince: EditText = findViewById(R.id.txtProvince)
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
            val province = txtProvince.text.toString()
            val country = txtCountry.text.toString()
            val nameOnCard = txtNameOnCard.text.toString()
            val cardNumber = txtCardNumber.text.toString()
            val validity = txtValidity.text.toString()
            val cvv = txtCvv.text.toString()

            if (validateName(firstname) && validateName(lastName) && validateEmail(email) && validatePhoneNumber(phoneNumber) && validateStreetAddress(address) && validateCanadianPostalCode(postalCode) && validateCanadianAddress(city) && validateCanadianAddress(province) && validateCountry(country) && validateName(nameOnCard) && validateCardNumber(cardNumber) && validateCVV(cvv)) {
                val firebaseUser = FirebaseAuth.getInstance().currentUser?.uid


                if (firebaseUser != null) {
                    val databaseReference: DatabaseReference =
                        FirebaseDatabase.getInstance().reference.child("orders").child(firebaseUser)
                    val userData = HashMap<String, Any>()
                    userData["firstName"] = firstname
                    userData["lastName"] = lastName
                    userData["email"] = email
                    userData["phoneNumber"] = phoneNumber
                    userData["address"] = address
                    userData["postalCode"] = postalCode
                    userData["city"] = city
                    userData["province"] = province
                    userData["country"] = country
                    userData["nameOnCard"] = nameOnCard
                    userData["cardNumber"] = cardNumber
                    userData["validity"] = validity
                    userData["cvv"] = cvv

                    databaseReference.setValue(userData)
                        .addOnSuccessListener {
                            Toast.makeText(
                                this,
                                "Your Order is successfully placed",
                                Toast.LENGTH_SHORT
                            ).show()

                            val cartReference: DatabaseReference = FirebaseDatabase.getInstance().reference.child("cart").child(firebaseUser)
                            cartReference.addListenerForSingleValueEvent(object :
                                ValueEventListener {
                                override fun onDataChange(dataSnapshot: DataSnapshot) {
                                    for (cartSnapshot in dataSnapshot.children) {
                                        val cartItem = cartSnapshot.getValue(Cart::class.java)
                                        cartItem?.let {
                                            databaseReference.push().setValue(it)
                                        }
                                    }
                                    dataSnapshot.ref.removeValue()
                                }
                                override fun onCancelled(databaseError: DatabaseError) {
                                    Log.e("CheckoutActivity", "onCancelled", databaseError.toException())
                                }

                            })
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
        val trimmedName = name.trim()
        if (trimmedName.isEmpty()) {
            Toast.makeText(this, "Please enter your name", Toast.LENGTH_SHORT).show()
            return false
        }
        if (!trimmedName.matches(Regex("^[a-zA-Z]+(?: [a-zA-Z]+)*\$"))) {
            Toast.makeText(this, "$name is INVALID!! Please enter a valid name", Toast.LENGTH_SHORT).show()
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
        val phonePattern = "^\\+1\\d{10}\$"
        val pattern = Pattern.compile(phonePattern)
        val matcher = pattern.matcher(phoneNumber)
        if (matcher.matches()) {
            return matcher.matches()
        } else {
            Toast.makeText(this, "Please enter a valid Canadian phone number in the format +19999999999", Toast.LENGTH_SHORT).show()
            return false
        }
    }
    private fun validateStreetAddress(streetAddress: String): Boolean {
        val trimmedStreetAddress = streetAddress.trim()
        if (trimmedStreetAddress.isEmpty()) {
            Toast.makeText(this, "Please enter a valid Address", Toast.LENGTH_SHORT).show()
            return false
        }
        if (!trimmedStreetAddress.matches(Regex("^[a-zA-Z0-9]+(?: [a-zA-Z0-9]+)*\$"))) {
            Toast.makeText(this, "$streetAddress is INVALID!! Please enter a valid address", Toast.LENGTH_SHORT).show()
            return false
        }
        return true
    }
    private fun validateCanadianAddress(address: String): Boolean {
        val trimmedAddress = address.trim()
        if (trimmedAddress.isEmpty()) {
            Toast.makeText(this, "Please enter a valid City and Province", Toast.LENGTH_SHORT).show()
            return false
        }
        if (!trimmedAddress.matches(Regex("^[a-zA-Z]+(?: [a-zA-Z]+)*\$"))) {
            Toast.makeText(this, "$address is INVALID!! Please enter a valid City and Province", Toast.LENGTH_SHORT).show()
            return false
        }
        return true
    }

    private fun validateCanadianPostalCode(postalCode: String): Boolean {
        val canadianPostalCodePattern = "^[A-Za-z]\\d[A-Za-z]\\s?\\d[A-Za-z]\\d$"
        val pattern = Pattern.compile(canadianPostalCodePattern)
        val matcher = pattern.matcher(postalCode.trim())
        if(matcher.matches()){
            return matcher.matches()
        } else {
            Toast.makeText(this, "Please enter a valid Canadian postal code", Toast.LENGTH_SHORT).show()
            return false
        }
    }
    private fun validateCountry(country: String): Boolean {
        val trimmedCountry = country.trim()
        if (trimmedCountry.equals("canada", ignoreCase = true)){
            return true
        } else {
            Toast.makeText(this, "Country INVALID!! Services are limited to Canada at the moment", Toast.LENGTH_SHORT).show()
            return false
        }
    }
    private fun validateCardNumber(cardNumber: String): Boolean {
        val cardNumberRegex = "^(\\d{4}[- ]){3}\\d{4}|\\d{16}$"
        if(cardNumber.trim().matches(Regex(cardNumberRegex))){
            return true
        } else {
            Toast.makeText(this, "INVALID card number", Toast.LENGTH_SHORT).show()
            return false
        }
    }

    private fun validateCVV(cvv: String): Boolean {
        val cvvRegex = "^\\d{3,4}$"
        if(cvv.trim().matches(Regex(cvvRegex))){
            return true
        } else {
            Toast.makeText(this, "INVALID cvv detected", Toast.LENGTH_SHORT).show()
            return false
        }
    }


}