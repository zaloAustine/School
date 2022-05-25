package com.example.riarafoodapp.mainflow.menu

import android.content.ContentValues
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.SpinnerAdapter
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.riarafoodapp.R
import com.example.riarafoodapp.data.Food
import com.example.riarafoodapp.data.Order
import com.example.riarafoodapp.data.showAlertDialog
import com.example.riarafoodapp.databinding.FragmentDescBinding
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class DescFragment : Fragment(R.layout.fragment_desc) {
    val types = arrayOf("2/3", "Finance", "G4", "2/5")

    private lateinit var binding: FragmentDescBinding
    private val database = FirebaseDatabase.getInstance()
    private var mAuth: FirebaseAuth? = null
    private var location: String = "2/3"

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentDescBinding.bind(view)
        binding.spinner.adapter = activity?.let {
            ArrayAdapter(
                it,
                androidx.appcompat.R.layout.support_simple_spinner_dropdown_item,
                types
            )
        } as SpinnerAdapter

        binding.spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(parent: AdapterView<*>?) {
                Snackbar.make(
                    binding.root,
                    "Error",
                    Snackbar.LENGTH_LONG
                ).show()
            }

            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                location = types[position]
            }
        }

        binding.button2.setOnClickListener {
            if (getNameAndValidate().first) {
                context?.showAlertDialog("Place order", "Order", "Do you wan to place this order") {
                    //create order
                    placeOrder()
                    //navigate
                    findNavController().navigate(R.id.orderFragment)
                }
            }
        }

        mAuth = FirebaseAuth.getInstance()
    }

    private fun getNameAndValidate(): Pair<Boolean, String> {
        val name = binding.nameInput.text.toString()
        if (name.isEmpty()) {
            Toast.makeText(context, "Enter A valid Name", Toast.LENGTH_LONG).show()
            return Pair(false, "")
        }
        return Pair(true, name)
    }

    private fun placeOrder() {
        val currentUser = FirebaseAuth.getInstance().currentUser
        val myRef = database.getReference("cartItems").child(currentUser!!.uid)

        // My top posts by number of stars
        myRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val list: MutableList<Food> = mutableListOf()

                // TODO: handle the post
                val children = dataSnapshot.children
                children.forEach {
                    val cartItems = it.getValue(Food::class.java)
                    cartItems?.let {
                        list.add(it)
                    }
                }

                if (list.isNotEmpty()) {
                    val total = list.sumOf { it.price.toInt() }
                    val order =
                        Order(
                            foods = list,
                            total = total.toString(),
                            name = getNameAndValidate().second,
                            location = location
                        )
                    sendOrder(order)
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {
                Log.w(ContentValues.TAG, "loadPost:onCancelled", databaseError.toException())
            }
        })
    }

    private fun sendOrder(order: Order) {
        val currentUser = FirebaseAuth.getInstance().currentUser
        val myRef = database.getReference("Orders").child(currentUser!!.uid)
        val ref = myRef.push()
        ref.setValue(order)
        clearCart()
    }

    private fun clearCart() {
        val currentUser = FirebaseAuth.getInstance().currentUser
        val myRef = database.getReference("cartItems")
        val pushedPostRef = myRef.child(currentUser!!.uid)

        pushedPostRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                for (appleSnapshot in dataSnapshot.children) {
                    appleSnapshot.ref.removeValue()
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {
                Log.e(ContentValues.TAG, "onCancelled", databaseError.toException())
            }
        })

        pushedPostRef.removeValue()
    }
}
