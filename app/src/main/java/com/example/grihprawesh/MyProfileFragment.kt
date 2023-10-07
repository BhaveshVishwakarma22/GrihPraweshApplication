package com.example.grihprawesh

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions
import com.example.grihprawesh.apix.RetrofitServicexx
import com.example.grihprawesh.modelxx.FavouriteLItem
import com.example.grihprawesh.modelxx.ListItem
import com.example.grihprawesh.modelxx.RequestUserIDFav
import com.example.grihprawesh.myadapters.RvItemAdapter
import com.google.android.gms.tasks.OnFailureListener
import com.google.android.gms.tasks.OnSuccessListener
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.IOException
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

//
//object LoginData{
//
//    lateinit var fName: String
//    lateinit var lName: String
//    lateinit var pass: String
//    lateinit var email: String
//    lateinit var pno: String
//
//}

class MyProfileFragment : Fragment() {

    lateinit var rv_my_fav:RecyclerView
    lateinit var rv_my_fav_adapter:RvItemAdapter

    lateinit var rv_list_my_fav:ArrayList<ListItem>


    lateinit var ib_pf_edit:ImageButton

    lateinit var tv_pf_details:TextView; lateinit var tv_pf_name:TextView;
    lateinit var iv_pf_profile_pic:ImageView;

    lateinit var mp_pb_fav_list:ProgressBar
    lateinit var mp_tv_emptyFav:TextView




    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


    }

    @SuppressLint("SetTextI18n")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val rv = inflater.inflate(R.layout.fragment_my_profile, container, false)

        initUIElements(rv)

        LoginDataM.let {

            tv_pf_name.text = it.lfname +" "+ it.llname
            tv_pf_details.text = it.lphone+"\n" + it.lemail

            context?.let { itx ->
                Glide.with(itx)
                    .load(it.limage)
                    .into(iv_pf_profile_pic)
            }

        }

        mp_tv_emptyFav.visibility = View.GONE

        val userId = LoginDataM.lid

        initRvItemLinMyFav(rv, userId)

        ib_pf_edit.setOnClickListener {
            val intent = Intent(context, EditAccount::class.java)
            startActivity(intent)
        }

        return rv
    }

    private fun initUIElements(rv:View) {
        ib_pf_edit = rv.findViewById(R.id.ib_pf_edit)
        tv_pf_name = rv.findViewById(R.id.tv_pf_name)
        tv_pf_details = rv.findViewById(R.id.tv_pf_details)
        iv_pf_profile_pic = rv.findViewById(R.id.iv_pf_profile_pic)
        mp_pb_fav_list = rv.findViewById(R.id.mp_pb_fav_list)
        mp_tv_emptyFav = rv.findViewById(R.id.mp_tv_emptyFav)

    }

    private fun initRvItemLinMyFav(rv:View, userId:String) {
//        Setting up RV
        rv_my_fav = rv.findViewById(R.id.rv_pf_myFavorites)
        rv_my_fav.setHasFixedSize(true)
        rv_my_fav.layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
        rv_list_my_fav = ArrayList()

//        for(i in 0 until 40){
//            rv_list_my_fav.add(ListItem(R.drawable.item_img_def.toString(),"Bhavesh's House$i", "bv200222_1|₹45,500,100\n2BHK", "buy", "New Rajendra Nagar"))
//        }


//        Fetching And Binding Favorites
        val req = RequestUserIDFav(userId)
        getUserFromEmail(req)

//        fetchAndBindMyListing("bV200222@gmail.com")

//        "bV200222@gmail.com"
    }


    private fun fetchAndBindMyListing(userId:String) {

        CoroutineScope(Dispatchers.IO).launch {
            val a = CoroutineScope(Dispatchers.IO).async {

//                Object to store user id
                var userx = RequestUserIDFav(userId)
//                Fetching Data
                val a0 = async { fetchMyListingDataNew(userx) }
                a0.await()
//                Fetching Images
                val a2 = fetchMyListingImage(a0.await())
                a2
            }

            val listX = a.await()
            activity?.runOnUiThread {
                Log.d("Poko", "List: $listX")
//                Passing data to recycler view
//                rv_my_listing_adapter = RvItemAdapter(listX, context)
                rv_my_fav_adapter = RvItemAdapter(listX, context)

                rv_my_fav.adapter = rv_my_fav_adapter

//                After fetching and binding removing progressbar
                mp_pb_fav_list.visibility = View.GONE
            }
        }
    }

//    Returns Mode location from Listitem
    private fun myMode(i:ListItem):String{
        if(i.prop_mode == "plot"){
            return "plot"
        }else if(i.prop_mode == "rent"){
            return "houserent"
        }else {
            return "housesale"
        }
    }

    //    Fetch images from For from all list
    private suspend fun fetchMyListingImage(rvListItemList: ArrayList<ListItem>): ArrayList<ListItem> {
//        Create Reference of storage
        val storage = FirebaseStorage.getInstance()
        val storageRef: StorageReference = storage.reference
        val rvListItemNewList = ArrayList<ListItem>()

        val deferredList = CoroutineScope(Dispatchers.IO).async {
//                  plot        sell        rent
            rvListItemList.map { i ->
                val loc = async{ myMode(i) }

                Log.d("Poko", loc.await())

                val imagesRef: StorageReference = storageRef.child("${loc.await()}/${i.prop_image}.jpg")

                suspendCoroutine<ListItem?> { continuation ->

                    imagesRef.downloadUrl.addOnSuccessListener { uri ->
                        val imageUrl = uri.toString()

                        val newItem = ListItem(
                            prop_image = imageUrl,
                            prop_name = i.prop_name,
                            prop_mode = i.prop_mode,
                            prop_details = i.prop_details,
                            prop_location = i.prop_location
                        )

                        continuation.resume(newItem) // Resume coroutine with the captured item
                    }.addOnFailureListener { exception ->
                        Log.d("Poko", "Failed ${i.prop_name}: ${exception.message}")
                        continuation.resume(null) // Resume coroutine with null if failed
                    }
                }
            }
        }
        val resultList = deferredList.await().filterNotNull()
        rvListItemNewList.addAll(resultList)

//        Log.d("Poko",rvListItemNewArrayList.toString())
        return ArrayList(rvListItemNewList)
    }

//    Poko New

    //    Fetch House Sale Data     Favourites
    private suspend fun fetchMyListingDataNew(userId: RequestUserIDFav):ArrayList<ListItem> {
        val ansx = CoroutineScope(Dispatchers.IO).async {
            val response = RetrofitServicexx.usersInstace.getFavouritesByUserId(userId)
            if (response.isSuccessful) {

                for (i in response.body()!!) {
                    if(i != null){
                        Log.d("Poko", i.toString())
                        val det = i.propDetails.toString().split('|');
//                    Log.d("Poko2", i.toString())
                        Log.d("Poko2", "PropName: \t"+ i.propName.toString())
                        Log.d("Poko2", "PropImage: \t"+ i.propImage.toString())
                        Log.d("Poko2", "PropMode: \t"+ i.propMode.toString())
                        Log.d("Poko2", "PropLocation: \t"+ i.propLocation.toString())
                        Log.d("Poko2", "PropDetails: \t"+ i.propDetails.toString())
                        Log.d("Poko2", "PropID: \t"+ det[0])
                        Log.d("Poko2", "PropCost: \t"+ det[1])
                        Log.d("Poko2", "PropUT: \t"+ det[2])
//                  plot        sell        rent
                        val a = ListItem(
                            prop_image = i.propImage.toString(),
                            prop_name = i.propName.toString(),
                            prop_mode = i.propMode.toString(),
                            prop_details = "${det[0]}|${det[1]}\n${det[2]}",
                            prop_location = i.propLocation.toString()
                        )
                        Log.d("Poko", a.toString())

                        rv_list_my_fav.add(a)
                    }
                }
            } else {
                Log.d("Poko", "Failed to fetch")
//                Toast.makeText(requireContext(), "Failed to fetch", Toast.LENGTH_SHORT).show()
            }
            rv_list_my_fav
        }

        return ansx.await()
    }

    //    Fetch From User Id
    private fun getUserFromEmail(postData: RequestUserIDFav) {
        val call = RetrofitServicexx.usersInstace.getFavoritesById(1, postData)
        call.enqueue(object : Callback<ArrayList<FavouriteLItem>> {
            override fun onResponse(
                call: Call<ArrayList<FavouriteLItem>>,
                response: Response<ArrayList<FavouriteLItem>>
            ) {
                val resx = response.body()
                if (response.code() == 200) {
                    if (resx != null) {
                        Log.d("Poko2", resx.toString())
                        fetchAndBindMyListing(postData.userid.toString())
                    }
                } else if(response.code() == 400){
                    Log.d("Poko", "Error Fetching My Listing: " + response.body().toString())
                    mp_tv_emptyFav.visibility = View.VISIBLE
                    mp_pb_fav_list.visibility = View.GONE
                } else {
                    Log.d("Poko", "Error code: " + response.code().toString())
                }
            }

            override fun onFailure(call: Call<ArrayList<FavouriteLItem>>, t: Throwable) {
                Log.d("Poko", "Error in fetching response", t)
            }
        })
    }

}