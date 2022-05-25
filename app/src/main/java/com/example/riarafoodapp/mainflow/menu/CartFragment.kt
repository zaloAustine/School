package com.example.riarafoodapp.mainflow.menu

import android.content.ContentValues
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.GridLayoutManager
import com.example.riarafoodapp.data.Food
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

        }

        binding.menuRecyclerView.adapter = adapter
        binding.menuRecyclerView.layoutManager = GridLayoutManager(context, 2)

        addToCart()
        getCartFoods()

    }

    private fun getCartFoods() {
        val currentUser = FirebaseAuth.getInstance().currentUser
        val myRef = database.getReference("cartItems")
        val pushedPostRef = myRef.child(currentUser!!.uid)

        // My top posts by number of stars
        pushedPostRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val list: MutableList<Food> = mutableListOf()

                // TODO: handle the post
                val children = dataSnapshot.children
                children.forEach {
                    Log.e("cart",it.getValue(String::class.java)!!)

//                    it.getValue(Food::class.java)?.let {
//                        list.add(it)
//                    }
                }

                adapter.photosList = list
                adapter.submitList(list)
            }

            override fun onCancelled(databaseError: DatabaseError) {
                Log.w(ContentValues.TAG, "loadPost:onCancelled", databaseError.toException())
            }
        })
    }

    private fun addToCart() {
        val currentUser = FirebaseAuth.getInstance().currentUser
        val myRef = database.getReference("cartItems")
        val pushedPostRef: DatabaseReference = myRef.child(currentUser!!.uid)
        pushedPostRef.setValue(Food(pushedPostRef.key.toString(), "cow desc", "eee", ""))
    }

}