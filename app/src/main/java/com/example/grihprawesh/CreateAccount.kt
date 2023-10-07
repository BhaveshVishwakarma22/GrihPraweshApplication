package com.example.grihprawesh

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.EditText
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.Toast
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions
import com.example.grihprawesh.apix.RetrofitServicexx
import com.example.grihprawesh.modelxx.UserPostReq
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class CreateAccount : AppCompatActivity() {

    lateinit var add_image: ImageView
    lateinit var ca_ibtn_join: ImageButton

    lateinit var ca_et_fname: EditText;
    lateinit var ca_et_lname: EditText;
    lateinit var ca_et_email: EditText;
    lateinit var ca_et_pass: EditText;
    lateinit var ca_et_pno: EditText;

    lateinit var fname: String;
    lateinit var lname: String;
    lateinit var pass: String;
    lateinit var nemail: String;
    lateinit var pno: String

    lateinit var imageUri: Uri
    lateinit var storgeRefrence: StorageReference


    @SuppressLint("UseCompatLoadingForDrawables")
    val circletOptions = RequestOptions()
        .centerCrop()
        .circleCrop()
        .placeholder(R.drawable.add_image_btn)
        .error(R.drawable.add_image_btn)
        .diskCacheStrategy(DiskCacheStrategy.RESOURCE)


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_account)

//        Binding UI elements to Object
        add_image = findViewById(R.id.ca_iv_add_image)
        ca_ibtn_join = findViewById(R.id.ca_ibtn_join)
        ca_et_pno = findViewById(R.id.ca_et_pno)
        ca_et_fname = findViewById(R.id.ca_et_fname)
        ca_et_lname = findViewById(R.id.ca_et_lname)
        ca_et_email = findViewById(R.id.ca_et_email)
        ca_et_pass = findViewById(R.id.ca_et_pass)

        var imagePicked = false


        add_image.setOnClickListener {
            uploadImage(add_image)
            imagePicked = true
        }

        ca_ibtn_join.setOnClickListener {
            fname = ca_et_fname.text.toString()
            lname = ca_et_lname.text.toString()
            pass = ca_et_pass.text.toString()
            pno = ca_et_pno.text.toString()
            nemail = ca_et_email.text.toString()

//            Checking if any fields are empty
            if (fname == "" || lname == "" || pass == "" || nemail == "" || pno == "") {
                Toast.makeText(applicationContext, "Empty Fields", Toast.LENGTH_SHORT).show()
            } else {
//                If not empty then post
                val userPostData = UserPostReq(
                    fname = fname,
                    lname = lname,
                    email = nemail,
                    pass = pass,
                    phone = pno,
                    userPhoto = pno
                )
//                val userPostData = UserPostReq("Anay", "Mishra", "donz@gmail.com", "1234", "1472585369", "123.jpg")

                if(!imagePicked){
                    Toast.makeText(this@CreateAccount, "Image Not Picked", Toast.LENGTH_SHORT).show()
                }else{
                    postUserx(userPostData)
                    uploadImageToFirebase(pno)
//                    fetchImageFromFirebase(add_image, "user1234")
                }
            }

            /*
            val intent = Intent(this, Test::class.java)
            startActivity(intent)
            finish()
             */
        }
    }

    private fun fetchImageFromFirebase(imageViewx: ImageView, name:String) {
//        Create Reference of storage
        val storage = FirebaseStorage.getInstance()
        val storageRef: StorageReference = storage.reference

//        Create Reference of image
        val imagesRef: StorageReference = storageRef.child("user/$name")
//        Fetch Url of image
        imagesRef.downloadUrl.addOnSuccessListener { uri ->
            val imageUrl = uri.toString()
//            Log.d("Poko", imageUrl)
//            Load image from url
            Glide.with(this@CreateAccount)
                .load(imageUrl)
                .into(imageViewx)
            // Now you can use the imageUrl to load the image using Glide or any other image loading library
        }.addOnFailureListener { exception ->
            // Handle any errors
            Log.d("Poko", "Failed: ${exception.message}")
        }

    }

    private fun getAllImagesFromFirebase() {
        val storage = FirebaseStorage.getInstance()
        val storageRef: StorageReference = storage.reference
        val imagesRef: StorageReference = storageRef.child("user/user1234")

        imagesRef.listAll()
            .addOnSuccessListener { listResult ->
                for (item in listResult.items) {
//                    val b = BitmapFactory.decodeFile(localFile.absolutePath)
//                    add_image.setImageBitmap(b)
                    item.downloadUrl.addOnSuccessListener { uri ->
                        val imageUrl = uri.toString()
                        Log.d("Poko", imageUrl)
                        Glide.with(this@CreateAccount)
                            .load(imageUrl)
                            .into(add_image)
                        // Now you can use the imageUrl to load the image using Glide or any other image loading library
                    }.addOnFailureListener { exception ->
                        // Handle any errors
                        Log.d("Poko", "Failed: ${exception.message}")
                    }
//                     Fetch and display each image using Glide
//                    Log.d("Poko", item.name.toString())
                }
            }
            .addOnFailureListener { exception ->
                // Handle any errors
            }
    }

    private fun postUserx(userPostData: UserPostReq) {
//        val str = "{\"fname\":\"${userPostData.fname}\",\"lname\":\"${userPostData.lname}\",\"email\":\"${userPostData.email}\",\"pass\":\"${userPostData.pass}\",\"phone\":\"${userPostData.phone}\",\"userPhoto\":\"${userPostData.userPhoto}\"}"
        val call = RetrofitServicexx.usersInstace.postUser(userPostData)
        call.enqueue(object : Callback<UserPostReq> {
            override fun onResponse(call: Call<UserPostReq>, response: Response<UserPostReq>) {
                if(response.code() == 201){
                    Log.d("Poko", "User Created Successfully")
                    val d = response.body()
                    Log.d("Poko", d.toString())
                    Toast.makeText(this@CreateAccount, "User Created!", Toast.LENGTH_SHORT).show()
                }else if (response.code() == 400){
                    Log.d("Poko", "User Already Exists")
                    Toast.makeText(this@CreateAccount, "User Already Exists!", Toast.LENGTH_SHORT).show()
                }
                else Log.d("Poko", "Error creating user")
            }

            override fun onFailure(call: Call<UserPostReq>, t: Throwable) {
                Log.d("Poko", "Error in fetching response", t)
            }

        })
    }

    private fun uploadImage(image: ImageView) {
        val intent = Intent()
        intent.action = Intent.ACTION_GET_CONTENT
        intent.type = "image/*"
        startActivityForResult(intent, 1)
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

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == 1) {
            imageUri = data?.data!!
            add_image.setImageURI(data?.data)
            Glide.with(applicationContext)
                .load(data?.data)
                .apply(this.circletOptions)
                .into(add_image)
        }
    }
}