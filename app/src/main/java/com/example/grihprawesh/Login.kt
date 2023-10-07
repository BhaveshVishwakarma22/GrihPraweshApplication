package com.example.grihprawesh

import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.EditText
import android.widget.ImageButton
import android.widget.ProgressBar
import android.widget.Toast
import com.bumptech.glide.Glide
import com.example.grihprawesh.apix.RetrofitInterfacesXX
import com.example.grihprawesh.apix.RetrofitServicexx
import com.example.grihprawesh.modelxx.ListItem
import com.example.grihprawesh.modelxx.ResponseDCItem
import com.example.grihprawesh.modelxx.UserEmailPost
import com.google.android.material.button.MaterialButton
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.lang.NullPointerException
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

object LoginDataM{
    var lfname:String ="Bravo"
    var lemail:String ="bv200222@gmail.com"
    var llname:String ="Vishwakarma"
    var limage:String ="user1234"
    var lphone:String ="1593572846"
    var lid:String ="56"
    var pass:String ="1234"
    var limageName:String = "userphoto.jpg"
    var l_state:Boolean = false
}

class Login : AppCompatActivity() {

    lateinit var lg_ibtn_login:ImageButton
    lateinit var lg_ibtn_create_acc:ImageButton
    lateinit var lg_mbtn_info:MaterialButton

    lateinit var lg_pb:ProgressBar

    lateinit var lg_et_email:EditText; lateinit var lg_et_pno:EditText; lateinit var lg_et_pass:EditText;


    lateinit var  npno:String; lateinit var  nemail:String;lateinit var  npass:String;


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)


        initUIElements()

        handleOnClickListners()
    }

    private fun initUIElements() {
        lg_ibtn_login = findViewById(R.id.lg_ibtn_login)
        lg_ibtn_create_acc = findViewById(R.id.lg_ibtn_create_acc)
        lg_mbtn_info = findViewById(R.id.lg_mbtn_info)
        lg_et_email = findViewById(R.id.lg_et_email)
        lg_et_pno = findViewById(R.id.lg_et_pno)
        lg_et_pass = findViewById(R.id.lg_et_pass)
        lg_pb = findViewById(R.id.lg_pb)
    }

    @SuppressLint("SuspiciousIndentation")
    private fun handleOnClickListners() {
        lg_ibtn_create_acc.setOnClickListener {
            val intent = Intent(this, CreateAccount::class.java)
            startActivity(intent)
        }

        lg_ibtn_login.setOnClickListener {


            npno = lg_et_pno.text.toString()
            nemail = lg_et_email.text.toString()
            npass = lg_et_pass.text.toString()

            if(npass == "" || nemail == "" || npno == ""){
                Toast.makeText(applicationContext, "Empty Fields", Toast.LENGTH_SHORT).show()
            }else{

                lg_pb.visibility = View.VISIBLE
//                getUsersData()
                val userEmailPost = UserEmailPost(nemail)

//                Get User Details and Login
                getUserFromEmail(userEmailPost)
            }
        }

        lg_mbtn_info.setOnClickListener {
            val intent = Intent(this, AuthorDetails::class.java)
            startActivity(intent)
        }
    }


//    Fetching From Email
    private fun getUserFromEmail(postData: UserEmailPost) {
        val call = RetrofitServicexx.usersInstace.getItemFromEmail(1, postData)
        call.enqueue(object : Callback<List<ResponseDCItem>> {
            override fun onResponse(
                call: Call<List<ResponseDCItem>>,
                response: Response<List<ResponseDCItem>>
            ) {
                val users = response.body()
                if (response.code() == 200) {
                    if (users != null) {
                        Log.d("Poko2", users.toString())

                        for(i in users){
                            if(i.phone == npno){
                                if(i.pass == npass){
//                                    Login Successfully
                                    Log.d("Poko2", "Everything Matched")
                                    getImageUrl(i)

                                }else{
                                    Toast.makeText(applicationContext, "Invalid Password", Toast.LENGTH_SHORT).show()
                                }
                            }else{
                                Toast.makeText(applicationContext, "Invalid Phone Number", Toast.LENGTH_SHORT).show()
                            }
                        }
                    }
                } else {
                    Log.d("Poko", "Error code: " + response.code().toString())
                    Toast.makeText(applicationContext, "Invalid Email", Toast.LENGTH_SHORT).show()
                }

                lg_pb.visibility = View.GONE
            }

            override fun onFailure(call: Call<List<ResponseDCItem>>, t: Throwable) {
                Log.d("Poko", "Error in fetching response", t)
                lg_pb.visibility = View.GONE

            }

        })
    }


    private fun getImageUrl(i: ResponseDCItem){
//        Create Reference of storage
        val storage = FirebaseStorage.getInstance()
        val storageRef: StorageReference = storage.reference
//        val imageLocation = "user/${i.userPhoto.toString().substring(0, i.userPhoto.toString().length-4)}"
        val imageLocation = "user/${i.userPhoto.toString()}"

        val imagesRef: StorageReference =
            storageRef.child(imageLocation)

        imagesRef.downloadUrl.addOnSuccessListener { uri ->
            val imageUrl = uri.toString()
            Log.d("Poko", imageUrl)

//                                    Storing Logged in data
            LoginDataM.lid = i.id.toString()
            LoginDataM.lfname = i.fname.toString()
            LoginDataM.llname = i.lname.toString()
            LoginDataM.lemail = i.email.toString()
            LoginDataM.pass = i.pass.toString()
            LoginDataM.lphone = i.phone.toString()
            LoginDataM.limage = imageUrl
            LoginDataM.limageName = i.userPhoto.toString()

            Toast.makeText(applicationContext, "Successfully Logged In", Toast.LENGTH_SHORT).show()
            Log.d("Poko2", "Image: $imageUrl")

//                                    Launching HomeActivity
            val intent = Intent(this@Login, HomeActivity::class.java)


            intent.putExtra("data", "${LoginDataM.lid};${LoginDataM.lfname};${LoginDataM.llname};${LoginDataM.lemail};${LoginDataM.pass};${LoginDataM.lphone};${LoginDataM.limage}")
            startActivity(intent)

        }.addOnFailureListener { exception ->
            Log.d("Poko", "Failed: ${exception.message}")
        }
    }

}
