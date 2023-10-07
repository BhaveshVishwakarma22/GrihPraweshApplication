package com.example.grihprawesh

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.ImageButton
import androidx.appcompat.app.AppCompatActivity


class AuthorDetails : AppCompatActivity() {

    lateinit var ad_linkind_btn:ImageButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_author_details)

        ad_linkind_btn = findViewById(R.id.ad_linkind_btn)

//        Open LinkedIn OnClick LinkedIn Button
        ad_linkind_btn.setOnClickListener {
            intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://www.linkedin.com/in/bhavesh-vishwakarma-a29080175/"))
            startActivity(intent);
        }
    }
}