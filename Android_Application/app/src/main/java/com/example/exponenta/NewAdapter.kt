package com.example.exponenta

import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter

var listArray = arrayListOf<String>("Image Classification", "Image Detection")

class NewAdapter(fragment: FragmentActivity): FragmentStateAdapter(fragment) {
    override fun getItemCount(): Int = 2

    override fun createFragment(position: Int): Fragment {
        val fragment = NewFragment()
        fragment.arguments = Bundle().apply {
            putString(ARG_OBJECT, position + 1)
        }
        return fragment
    }

    private fun putString(argObject: String, i: Int) {
    }
}
