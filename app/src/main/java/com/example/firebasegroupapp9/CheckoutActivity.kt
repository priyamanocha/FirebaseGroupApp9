package com.example.firebasegroupapp9

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log

import android.view.Menu
import android.view.MenuItem
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.widget.Toolbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import java.time.LocalDate
import java.time.LocalTime

import java.util.Calendar
import java.util.regex.Pattern
import kotlin.random.Random

class CheckoutActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_checkout)

        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)


        val intent = intent
        val totalAmount = intent.getStringExtra("TOTAL_AMOUNT")
        val txtTotal: TextView = findViewById(R.id.txtTotal)
        txtTotal.text = "Total Amount: $totalAmount"


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


            if (validateName(firstname, txtFirstname)
                && validateName(lastName, txtLastName)
                && validateEmail(email, txtEmail)
                && validatePhoneNumber(phoneNumber, txtPhoneNumber)
                && validateStreetAddress(address, txtAddress)
                && validateCanadianPostalCode(postalCode, txtPostalCode)
                && validateCanadianAddress(city, txtCity)
                && validateCanadianAddress(province, txtProvince)
                && validateCountry(country, txtCountry)
                && validateName(nameOnCard, txtNameOnCard)
                && validateCardNumber(cardNumber, txtCardNumber)
                && validateExpiryDate(validity, txtValidity)
                && validateCVV(cvv, txtCvv)
            ) {
                val firebaseUser = FirebaseAuth.getInstance().currentUser?.uid
                if (firebaseUser != null) {
                    val databaseReference: DatabaseReference =
                        FirebaseDatabase.getInstance().reference.child("orders").child(firebaseUser)
                    val orderId = generateOrderNumber()

                    val orderInfo = HashMap<String, Any>()
                    orderInfo["id"] = orderId
                    orderInfo["firstName"] = firstname
                    orderInfo["lastName"] = lastName
                    orderInfo["email"] = email
                    orderInfo["phoneNumber"] = phoneNumber
                    orderInfo["address"] = address
                    orderInfo["postalCode"] = postalCode
                    orderInfo["city"] = city
                    orderInfo["province"] = province
                    orderInfo["country"] = country
                    orderInfo["nameOnCard"] = nameOnCard
                    orderInfo["cardNumber"] = cardNumber
                    orderInfo["validity"] = validity
                    orderInfo["cvv"] = cvv
                    orderInfo["orderDate"] = LocalDate.now().toString()
                    orderInfo["orderTime"] = LocalTime.now().toString()
                    val totalAmount = txtTotal.text.toString()
                    val amtparts = totalAmount.split(" ")
                    val amtString = amtparts.last()
                    orderInfo["totalAmount"] = amtString


                    databaseReference.child(orderId.toString()).child("orderinfo")
                        .setValue(orderInfo)

                        .addOnSuccessListener {
                            Toast.makeText(
                                this,
                                "Your Order is successfully placed",
                                Toast.LENGTH_SHORT
                            ).show()

                            val cartReference =
                                FirebaseDatabase.getInstance().reference.child("cart")
                                    .child(firebaseUser)
                            cartReference.addListenerForSingleValueEvent(object :
                                ValueEventListener {
                                override fun onDataChange(dataSnapshot: DataSnapshot) {
                                    for (cartSnapshot in dataSnapshot.children) {
                                        val cartItem = cartSnapshot.getValue(Cart::class.java)
                                        cartItem?.let {
                                            databaseReference.child(orderId.toString())
                                                .child("products").push()
                                                .setValue(it)
                                        }
                                    }
                                    dataSnapshot.ref.removeValue()
                                }

                                override fun onCancelled(databaseError: DatabaseError) {
                                    Log.e(
                                        "CheckoutActivity",
                                        "onCancelled",
                                        databaseError.toException()
                                    )
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
                    val intent = Intent(this@CheckoutActivity, OrderConfirmedActivity::class.java)
                    intent.putExtra("ORDER_ID", orderId.toString())
                    startActivity(intent)
                } else {
                    Toast.makeText(this, "User not authenticated", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun generateOrderNumber(): Int {
        return Random.nextInt(10000000, 99999999 + 1)
    }

    private fun validateName(name: String, editText: EditText): Boolean {
        val trimmedName = name.trim()
        if (trimmedName.isEmpty()) {
            editText.error = "Please enter your name"
            return false
        }
        if (!trimmedName.matches(Regex("^[a-zA-Z]+(?: [a-zA-Z]+)*\$"))) {
            editText.error = "\"$name is INVALID!! Please enter a valid name"
            return false
        }
        return true
    }

    private fun validateEmail(email: String, txtEmail: EditText): Boolean {
        val emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+"
        val pattern = Pattern.compile(emailPattern)
        val matcher = pattern.matcher(email)
        if (matcher.matches()) {
            return matcher.matches()
        } else {
            txtEmail.error = "Please enter a valid email";
            return false
        }
    }

    private fun validateExpiryDate(expiryDate: String, txtValidity: EditText): Boolean {
        if (expiryDate.length == 4) {
            val enteredMonth = expiryDate.substring(0, 2).toIntOrNull()
            val enteredYear = expiryDate.substring(2, 4).toIntOrNull()

            if (enteredMonth != null && enteredYear != null) {
                val currentMonth = Calendar.getInstance().get(Calendar.MONTH) + 1
                val currentYear = Calendar.getInstance().get(Calendar.YEAR) % 100

                if (enteredYear > currentYear || (enteredYear == currentYear && enteredMonth >= currentMonth)) {
                    return true
                } else {
                    txtValidity.error = "Enter valid expiry date"
                    return false
                }
            }
        }
        // If entered date format is incorrect
        txtValidity.error = "Invalid expiry date format"
        return false
    }

    private fun validatePhoneNumber(phoneNumber: String, txtPhoneNumber: EditText): Boolean {
        val phonePattern = "^\\+1\\d{10}\$"
        val pattern = Pattern.compile(phonePattern)
        val matcher = pattern.matcher(phoneNumber)
        if (matcher.matches()) {
            return matcher.matches()
        } else {
            txtPhoneNumber.error =
                "Please enter a valid Canadian phone number in the format +19999999999"
            return false
        }
    }

    private fun validateStreetAddress(streetAddress: String, txtAddress: EditText): Boolean {
        val trimmedStreetAddress = streetAddress.trim()
        if (trimmedStreetAddress.isEmpty()) {
            txtAddress.error = "Please enter a valid Address"
            return false
        }
        if (!trimmedStreetAddress.matches(Regex("^[a-zA-Z0-9]+(?: [a-zA-Z0-9]+)*\$"))) {
            txtAddress.error = "$streetAddress is INVALID!! Please enter a valid address"
            return false
        }
        return true
    }

    private fun validateCanadianAddress(address: String, editText: EditText): Boolean {
        val trimmedAddress = address.trim()
        if (trimmedAddress.isEmpty()) {
            editText.error = "Please enter valid city or province"
            return false
        }
        if (!trimmedAddress.matches(Regex("^[a-zA-Z]+(?: [a-zA-Z]+)*\$"))) {
            editText.error = "$address is INVALID!! Please enter a valid City and Province"
            return false
        }
        return true
    }

    private fun validateCanadianPostalCode(postalCode: String, txtPostalCode: EditText): Boolean {
        val canadianPostalCodePattern = "^[A-Za-z]\\d[A-Za-z]\\s?\\d[A-Za-z]\\d$"
        val pattern = Pattern.compile(canadianPostalCodePattern)
        val matcher = pattern.matcher(postalCode.trim())
        if (matcher.matches()) {
            return matcher.matches()
        } else {
            txtPostalCode.error = "Please enter a valid Canadian postal code"
            return false
        }
    }

    private fun validateCountry(country: String, txtCountry: EditText): Boolean {
        val trimmedCountry = country.trim()
        if (trimmedCountry.equals("canada", ignoreCase = true)) {
            return true
        } else {
            txtCountry.error = "Country INVALID!! Services are limited to Canada at the moment"
            return false
        }
    }

    private fun validateCardNumber(cardNumber: String, txtCardNumber: EditText): Boolean {
        val cardNumberRegex = "^(\\d{4}[- ]){3}\\d{4}|\\d{16}$"
        if (cardNumber.trim().matches(Regex(cardNumberRegex))) {
            return true
        } else {
            txtCardNumber.error = "INVALID card number"
            return false
        }
    }

    private fun validateCVV(cvv: String, txtCvv: EditText): Boolean {
        val cvvRegex = "^\\d{3,4}$"
        if (cvv.trim().matches(Regex(cvvRegex))) {
            return true
        } else {
            txtCvv.error = "INVALID cvv detected"
            return false
        }
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