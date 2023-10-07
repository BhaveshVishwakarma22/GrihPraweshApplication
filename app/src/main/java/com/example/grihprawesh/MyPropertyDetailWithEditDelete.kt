package com.example.grihprawesh

import android.Manifest
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.core.app.ActivityCompat
import com.google.android.material.button.MaterialButton
import com.google.android.material.dialog.MaterialAlertDialogBuilder

class MyPropertyDetailWithEditDelete : AppCompatActivity() {

    lateinit var mpb_mat_btn_delete: MaterialButton; lateinit var mpb_mat_btn_edit: MaterialButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_my_property_detail_with_edit_delete)

        initUiElements()

        handleOnClickListners()
    }


    private fun initUiElements() {
        mpb_mat_btn_delete = findViewById(R.id.mpb_mat_btn_delete)
        mpb_mat_btn_edit = findViewById(R.id.mpb_mat_btn_edit)

    }


    private fun handleOnClickListners() {
        mpb_mat_btn_delete.setOnClickListener {
            Log.d("Poko", "Delete Clicked")
            showDialog()
            //Set Calling Number
        }

        mpb_mat_btn_edit.setOnClickListener {
            Log.d("Poko", "Edit Clicked")
        }
    }

    private fun showDialog() {
        val dialog = MaterialAlertDialogBuilder(this)
            .setTitle("Delete Listing")
            .setMessage("Do you want to delete this listing?")
            .setPositiveButton("OK") { dialogInterface: DialogInterface, _: Int ->
                // Handle positive button click
                dialogInterface.dismiss()
                finish()
            }
            .setNegativeButton("Cancel") { dialogInterface: DialogInterface, _: Int ->
                // Handle negative button click
                dialogInterface.dismiss()
            }
            .setCancelable(true)
            .create()

        dialog.show()
    }
}