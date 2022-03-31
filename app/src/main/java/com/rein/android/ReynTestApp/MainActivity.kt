package com.rein.android.ReynTestApp

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.rein.android.ReynTestApp.Fragments.StartFragment
import com.rein.android.ReynTestApp.databinding.ActivityMainBinding


class MainActivity : AppCompatActivity() {

    private lateinit var binding : ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater).also { setContentView(it.root) }


        if (savedInstanceState == null) {
            val fragment = StartFragment.newInstance()
            supportFragmentManager
                .beginTransaction()
                .add(R.id.fragmentContainer, fragment)
                .commit()
        }


    }




}