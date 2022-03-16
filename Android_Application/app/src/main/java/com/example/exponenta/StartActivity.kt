package com.example.exponenta

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.widget.ViewPager2
import com.example.exponenta.databinding.ActivityStartBinding



class StartActivity : FragmentActivity() {

    private lateinit var adapter: NewAdapter
    private lateinit var viewPager: ViewPager2

    lateinit var startBindingClass: ActivityStartBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        startBindingClass = ActivityStartBinding.inflate(layoutInflater)
        setContentView(startBindingClass.root)

        adapter = NewAdapter(this)
        startBindingClass.pager.adapter = adapter

        startBindingClass.btSkip.setOnClickListener {
            var intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }
    }
}