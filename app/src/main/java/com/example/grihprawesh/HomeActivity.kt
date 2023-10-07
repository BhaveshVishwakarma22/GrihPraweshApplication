package com.example.grihprawesh

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.FrameLayout
import androidx.fragment.app.Fragment
import com.example.grihprawesh.BottomNav.bottom_nav_bar
import com.example.grihprawesh.databinding.ActivityHomeBinding
import com.google.android.material.bottomnavigation.BottomNavigationView

object BottomNav{
    lateinit var bottom_nav_bar:BottomNavigationView
}

class HomeActivity : AppCompatActivity() {

    lateinit var recData:String

    lateinit var binding: ActivityHomeBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        bottom_nav_bar = findViewById(R.id.bnv_ha)

        replaceFragment(HomeFragment())

//        if(intent.hasExtra("data")){
//            recData = intent.getStringExtra("data").toString()
//        }


        bottom_nav_bar.setOnItemSelectedListener {

            when(it.itemId){
                R.id.bnv_home -> replaceFragment(HomeFragment())
                R.id.bnv_explore-> replaceFragment(ExploreFragment())
                R.id.bnv_myListing-> replaceFragment(MyListingFragement())
                R.id.bnv_addListing-> replaceFragment(AddListingFragment())
                R.id.bnv_myProfile-> replaceFragment(MyProfileFragment())

                else->{

                }
            }
            true
        }


    }

    private fun replaceFragment(fragment:Fragment){

        val fragmentManager = supportFragmentManager
        val fragemntTransaction = fragmentManager.beginTransaction()
        fragemntTransaction.replace(R.id.fl_home_activity, fragment)
        fragemntTransaction.commit()
    }
}