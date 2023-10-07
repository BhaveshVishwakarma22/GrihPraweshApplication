package com.example.grihprawesh

import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.grihprawesh.apix.RetrofitServicexx
import com.example.grihprawesh.modelxx.FavouriteLItem
import com.example.grihprawesh.modelxx.ListItem
import com.example.grihprawesh.modelxx.RequestUserIDFav
import com.example.grihprawesh.modelxx.ResponseDC
import com.example.grihprawesh.modelxx.ResponseDCItem
import com.example.grihprawesh.modelxx.UserEmailPost
import com.example.grihprawesh.myadapters.RvItemAdapter
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.lang.Exception
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine


class MyListingFragement : Fragment() {

    lateinit var rv_mylisting_grid: RecyclerView

    lateinit var rv_my_listing_list:ArrayList<ListItem>
    lateinit var rv_my_listing_adapter: RvItemAdapter
    lateinit var ml_pb_fav_list:ProgressBar
    lateinit var ml_tv_emptyListing:TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val rv = inflater.inflate(R.layout.fragment_my_listing_fragement, container, false)


//        Connecting with UI Elements
        rv_mylisting_grid = rv.findViewById(R.id.rv_ml_myListing_grid)
        ml_pb_fav_list = rv.findViewById(R.id.ml_pb_fav_list)
        ml_tv_emptyListing = rv.findViewById(R.id.ml_tv_emptyListing)

        ml_tv_emptyListing.visibility = View.GONE

//        Setting up RV
        rv_mylisting_grid.setHasFixedSize(true)
        rv_mylisting_grid.layoutManager = GridLayoutManager(context, 2, GridLayoutManager.VERTICAL, false)
        rv_my_listing_list = ArrayList()

        var userId = "bV200222@gmail.com"
        userId = LoginDataM.lid


//        Fetching My Listing data and placing in Recycler View
        val req = RequestUserIDFav(userId)
        getUserFromEmail(req)


        return rv
    }

    private fun fetchAndBindMyListing(userId:String) {

        CoroutineScope(Dispatchers.IO).launch {
            val a = CoroutineScope(Dispatchers.IO).async {

                var userx = RequestUserIDFav(userId)

                val a1 = async { fetchMyListingDataNew2(userx) }
                a1.await()

                val a2 = fetchMyListingImage(a1.await())
                a2
            }

            val listX = a.await()
            activity?.runOnUiThread {
                Log.d("Poko", "List: $listX")
//                Passing data to recycler view
//                rv_my_listing_adapter = RvItemAdapter(listX, context)
                rv_my_listing_adapter = RvItemAdapter(listX, context, MyPropertyDetailWithEditDelete::class.java)

                rv_mylisting_grid.adapter = rv_my_listing_adapter

//                After fetching and binding removing progressbar
                ml_pb_fav_list.visibility = View.GONE
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


    //    Fetch User Listing Data
    private suspend fun fetchMyListingDataNew2(userId: RequestUserIDFav):ArrayList<ListItem> {
        val ansx = CoroutineScope(Dispatchers.IO).async {
            val response = RetrofitServicexx.usersInstace.getUserListingByUserId(userId)

            if (response.isSuccessful) {
                Log.d("Poko2", response.body().toString())
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

                        rv_my_listing_list.add(a)
                    }
                }
            } else {
                Log.d("Poko", "Failed to fetch")
//                Toast.makeText(requireContext(), "Failed to fetch", Toast.LENGTH_SHORT).show()
            }

            rv_my_listing_list
        }

        return ansx.await()
    }

//    Fetch From User Id
    private fun getUserFromEmail(postData: RequestUserIDFav) {
        val call = RetrofitServicexx.usersInstace.getMyListingById(1, postData)
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
                    ml_tv_emptyListing.visibility = View.VISIBLE
                    ml_pb_fav_list.visibility = View.GONE
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