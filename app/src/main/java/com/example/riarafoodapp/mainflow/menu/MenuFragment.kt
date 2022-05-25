package com.example.riarafoodapp.mainflow.menu

import android.content.ContentValues.TAG
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.example.riarafoodapp.R
import com.example.riarafoodapp.data.Cart
import com.example.riarafoodapp.data.Food
import com.example.riarafoodapp.databinding.FragmentMenuBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class MenuFragment : Fragment() {

    private val database = FirebaseDatabase.getInstance()
    private lateinit var adapter: FoodAdapter
    private var mAuth: FirebaseAuth? = null


    private val binding: FragmentMenuBinding by lazy {
        FragmentMenuBinding.inflate(layoutInflater)
    }

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

        adapter = FoodAdapter { food -> }

        binding.menuRecyclerView.adapter = adapter
        binding.menuRecyclerView.layoutManager = GridLayoutManager(context, 2)

        binding.cardView.setOnClickListener {
            findNavController().navigate(R.id.cartFragment)
        }

        binding.proceedToCheckOut.setOnClickListener {
            findNavController().navigate(R.id.cartFragment)
        }

        addFoodMenu()
        getFoodMenus()
        filterFood()
        updateCartDetails()
        getCartDetails()
    }

    private fun filterFood() {

        binding.multiSearchView.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                adapter.filter.filter(s)
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                adapter.filter.filter(s)
            }
        })
    }

    private fun getFoodMenus() {
        val myRef = database.getReference("menu")


        // My top posts by number of stars
        myRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val list: MutableList<Food> = mutableListOf()

                // TODO: handle the post
                val children = dataSnapshot.children
                children.forEach {
                    it.getValue(Food::class.java)?.let {
                        list.add(it)
                    }
                }

                adapter.photosList = list
                adapter.submitList(list)
            }

            override fun onCancelled(databaseError: DatabaseError) {
                Log.w(TAG, "loadPost:onCancelled", databaseError.toException())
            }
        })

    }

    private fun addFoodMenu() {
        val myRef = database.getReference("menu")
        val pushedPostRef: DatabaseReference = myRef.push()
        pushedPostRef.setValue(Food(pushedPostRef.key.toString(), "chicken desc", "eee", ""))
    }

    private fun updateCartDetails() {
        val currentUser = FirebaseAuth.getInstance().currentUser
        val myRef = database.getReference("cart")
        val pushedPostRef: DatabaseReference = myRef.child(currentUser!!.uid)
        pushedPostRef.setValue(Cart("5", "400"))
    }

    private fun getCartDetails() {

        val currentUser = FirebaseAuth.getInstance().currentUser
        val myRef = database.getReference("cart")
        val pushedPostRef: DatabaseReference = myRef.child(currentUser!!.uid)

        val postListener = object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                // Get Post object and use the values to update the UI
                val post = dataSnapshot.getValue(Cart::class.java)
                if (post != null) {
                    binding.pricing.text =
                        "You have ${post.items} Items worth Kes ${post.total} added to cart"
                } else {
                    binding.cardView.isVisible = false
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {
                // Getting Post failed, log a message
                Log.w(TAG, "loadPost:onCancelled", databaseError.toException())
            }
        }

        pushedPostRef.addValueEventListener(postListener)

    }

}