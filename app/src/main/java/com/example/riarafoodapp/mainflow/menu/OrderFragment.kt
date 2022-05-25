package com.kevin.cakeCity.ui.main.fragments

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import com.example.riarafoodapp.R
import com.example.riarafoodapp.databinding.FragmentOrderBinding


class OrderFragment : Fragment(R.layout.fragment_order) {

    private lateinit var binding: FragmentOrderBinding

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentOrderBinding.bind(view)

        binding.callBtn.setOnClickListener {
            val dialIntent = Intent(Intent.ACTION_DIAL)
            dialIntent.data = Uri.parse("tel:" + "254779292074")
            startActivity(dialIntent)
        }
    }
}






















