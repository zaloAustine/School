package com.example.riarafoodapp.mainflow.menu

import android.content.ContentValues
import android.content.ContentValues.TAG
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.example.riarafoodapp.R
import com.example.riarafoodapp.data.Cart
import com.example.riarafoodapp.data.Food
import com.example.riarafoodapp.data.showAlertDialog
import com.example.riarafoodapp.databinding.FragmentCartBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class CartFragment : Fragment() {

    private val binding: FragmentCartBinding by lazy {
        FragmentCartBinding.inflate(layoutInflater)
    }

    private val database = FirebaseDatabase.getInstance()
    private lateinit var adapter: CartAdapter
    private var mAuth: FirebaseAuth? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        mAuth = FirebaseAuth.getInstance()

        adapter = CartAdapter { food ->
            removeFromCart(food.desc)
        }

        binding.menuRecyclerView.adapter = adapter
        binding.menuRecyclerView.layoutManager = GridLayoutManager(context, 2)

        binding.placeOrderBtn.setOnClickListener {
            findNavController().navigate(R.id.descFragment)
        }

        getCartFoods()
    }

    private fun getCartFoods() {
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
                    updateCartDetails(count = list.size.toString(), total = total.toString())
                    binding.amount.text = "Cart Total Kes $total"
                } else {
                    updateCartDetails("0", total = "0")
                }

                adapter.photosList = list
                adapter.submitList(list)
            }

            override fun onCancelled(databaseError: DatabaseError) {
                Log.w(ContentValues.TAG, "loadPost:onCancelled", databaseError.toException())
            }
        })
    }

    private fun removeFromCart(desc: String) {
        val currentUser = FirebaseAuth.getInstance().currentUser
        val myRef = database.getReference("cartItems")
        val pushedPostRef: DatabaseReference = myRef.child(currentUser!!.uid)

        val applesQuery = pushedPostRef.orderByChild("desc").equalTo(desc)

        applesQuery.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                for (appleSnapshot in dataSnapshot.children) {
                    appleSnapshot.ref.removeValue()
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {
                Log.e(TAG, "onCancelled", databaseError.toException())
            }
        })

        pushedPostRef.removeValue()
    }

    private fun updateCartDetails(count: String, total: String) {
        val currentUser = FirebaseAuth.getInstance().currentUser
        val myRef = database.getReference("cart")
        val pushedPostRef: DatabaseReference = myRef.child(currentUser!!.uid)
        pushedPostRef.setValue(Cart(count, total))
    }

}