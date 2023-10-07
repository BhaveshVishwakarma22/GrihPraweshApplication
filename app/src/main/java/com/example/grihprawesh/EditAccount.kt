package com.example.grihprawesh

import android.annotation.SuppressLint
import android.content.Intent
import android.media.Image
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.EditText
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.Toast
import androidx.core.net.toUri
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions
import com.example.grihprawesh.apix.RetrofitServicexx
import com.example.grihprawesh.modelxx.GeneralMessage
import com.example.grihprawesh.modelxx.UserPostReq
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.net.URI
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class EditAccount : AppCompatActivity() {

    lateinit var edit_image: ImageView
    lateinit var et_fname: EditText
    lateinit var et_lname: EditText
    lateinit var et_email: EditText
    lateinit var et_opass: EditText
    lateinit var et_npass: EditText
    lateinit var et_pno: EditText
    lateinit var ibtn_save: ImageButton

    lateinit var fname:String; lateinit var lname:String; lateinit var email:String;
    lateinit var pass1:String; lateinit var pass2:String; lateinit var pno:String;
    lateinit var img:URI;

    lateinit var imageUri: Uri
    lateinit var storgeRefrence: StorageReference

    var imagePicked = false

    @SuppressLint("UseCompatLoadingForDrawables")
    val circletOptions = RequestOptions()
        .centerCrop()
        .circleCrop()
        .placeholder(R.drawable.add_image_btn)
        .error(R.drawable.add_image_btn)
        .diskCacheStrategy(DiskCacheStrategy.RESOURCE)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_account)

        initUiElements()

        fillDefaults()


        ibtn_save.setOnClickListener {
            fname = et_fname.text.toString()
            lname = et_lname.text.toString()
            email = et_email.text.toString()
            pass1 = et_opass.text.toString()
            pass2 = et_npass.text.toString()
            pno = et_pno.text.toString()

            if (fname == "" || lname == "" || pass1 == "" || pass2 == "" || pno == "" || email == "") {
                Toast.makeText(applicationContext, "Empty Fields", Toast.LENGTH_SHORT).show()
            } else {
//                If not empty then post
                val userPostData = UserPostReq(
                    fname = fname,
                    lname = lname,
                    email = email,
                    pass = pass2,
                    phone = pno,
                    userPhoto = LoginDataM.limageName
                )

                val str = "{\"fname\":\"${userPostData.fname}\",\"lname\":\"${userPostData.lname}\",\"email\":\"${userPostData.email}\",\"pass\":\"${userPostData.pass}\",\"phone\":\"${userPostData.phone}\",\"userPhoto\":\"${userPostData.userPhoto}\"}"
                Log.d("Poko2", str)

                if(!imagePicked){
                    Toast.makeText(this@EditAccount, "Image Not Picked", Toast.LENGTH_SHORT).show()
                }else{
                    uploadImageToFirebase(LoginDataM.limageName)
                    Toast.makeText(this@EditAccount, "Image Uploaded", Toast.LENGTH_SHORT).show()

                }

                updateUserx(userPostData)

            }

        }


        edit_image.setOnClickListener {
            uploadImage(edit_image)
            imagePicked = true
        }
    }

    private fun uploadImage(image: ImageView) {
        val intent = Intent()
        intent.action = Intent.ACTION_GET_CONTENT
        intent.type = "image/*"
        startActivityForResult(intent, 1)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if(requestCode == 1){
            imageUri = data?.data!!
            edit_image.setImageURI(data?.data)
            Glide.with(applicationContext)
                .load(data?.data)
                .apply(this.circletOptions)
                .into(edit_image)
        }
    }

    private fun initUiElements() {
        edit_image = findViewById(R.id.ea_iv_add_image)
        et_fname = findViewById(R.id.ea_et_fname)
        et_lname = findViewById(R.id.ea_et_lname)
        et_email = findViewById(R.id.ea_et_email)
        et_opass = findViewById(R.id.ea_et_old_pass)
        et_npass = findViewById(R.id.ea_et_new_pass)
        et_pno = findViewById(R.id.ea_et_pno)
        ibtn_save = findViewById(R.id.ea_ibtn_save)
    }

    private fun fillDefaults(){
        et_fname.setText(LoginDataM.lfname)
        et_lname.setText(LoginDataM.llname)
        et_email.setText(LoginDataM.lemail)
        et_pno.setText(LoginDataM.lphone)
        Glide.with(this@EditAccount)
            .load(LoginDataM.limage)
            .apply(this.circletOptions)
            .into(edit_image)

    }

//  Api

    private fun deleteImageFromFirebase(name: String) {
        storgeRefrence = FirebaseStorage.getInstance().reference

        storgeRefrence = FirebaseStorage.getInstance().getReference("user/$name")


        storgeRefrence.delete()
            .addOnSuccessListener {
                Toast.makeText(
                    applicationContext,
                    "Image Deleted Successfully",
                    Toast.LENGTH_SHORT
                ).show()

//                Upload New Image
                uploadImageToFirebase(name)
            }
            .addOnFailureListener {
                Toast.makeText(applicationContext, "Image Upload Failed", Toast.LENGTH_SHORT)
                    .show()
            }
    }

    private fun uploadImageToFirebase(name: String) {
        storgeRefrence = FirebaseStorage.getInstance().reference

        val formatter = SimpleDateFormat("yyyy_MM__dd_HH__mm__ss", Locale.CHINA)
        val date = Date()
        val fileName = formatter.format(date)
        storgeRefrence = FirebaseStorage.getInstance().getReference("user/$name")


        storgeRefrence.putFile(imageUri)
            .addOnSuccessListener {
                Toast.makeText(
                    applicationContext,
                    "Image Uploaded Successfully",
                    Toast.LENGTH_SHORT
                ).show()
            }
            .addOnFailureListener {
                Toast.makeText(applicationContext, "Image Upload Failed", Toast.LENGTH_SHORT)
                    .show()
            }
    }

    private fun updateUserx(userPostData: UserPostReq) {
//        val str = "{\"fname\":\"${userPostData.fname}\",\"lname\":\"${userPostData.lname}\",\"email\":\"${userPostData.email}\",\"pass\":\"${userPostData.pass}\",\"phone\":\"${userPostData.phone}\",\"userPhoto\":\"${userPostData.userPhoto}\"}"
        val call = RetrofitServicexx.usersInstace.updateUserById(1, userPostData)
        call.enqueue(object : Callback<GeneralMessage> {
            override fun onResponse(call: Call<GeneralMessage>, response: Response<GeneralMessage>) {
                if(response.isSuccessful){
                    Log.d("Poko", "User Updated Successfully")
                    val d = response.body()
                    Log.d("Poko", d.toString())
                    Toast.makeText(this@EditAccount, "User Updated!", Toast.LENGTH_SHORT).show()
                }else if (response.code() == 400){
                    Log.d("Poko", "User Already Exists")
                    Toast.makeText(this@EditAccount, "Unable to Update user!", Toast.LENGTH_SHORT).show()
                }
                else Log.d("Poko", "Error updating user ${response.code()}\n${response.message().toString()}")
            }

            override fun onFailure(call: Call<GeneralMessage>, t: Throwable) {
                Log.d("Poko", "Error in fetching response", t)
            }

        })
    }
}