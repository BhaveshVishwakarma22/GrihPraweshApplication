package com.example.grihprawesh

import android.Manifest
import android.annotation.SuppressLint
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import com.bumptech.glide.Glide
import com.example.grihprawesh.apix.RetrofitServicexx
import com.example.grihprawesh.modelxx.FavListingResponseItem
import com.example.grihprawesh.modelxx.FavouriteLItem
import com.example.grihprawesh.modelxx.GeneralMessage
import com.example.grihprawesh.modelxx.HouseItemId
import com.example.grihprawesh.modelxx.HouseSellItem
import com.example.grihprawesh.modelxx.ListItem
import com.example.grihprawesh.modelxx.PlotIdReq
import com.example.grihprawesh.modelxx.PlotResponseXItem
import com.example.grihprawesh.modelxx.RequestUserIDFav
import com.example.grihprawesh.myadapters.RvItemAdapter
import com.google.android.material.button.MaterialButton
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.squareup.picasso.Picasso
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.PATCH
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

class PropertyDetailView : AppCompatActivity() {

    lateinit var pb_mat_btn_call:MaterialButton
    lateinit var pd_tv_basic_details:TextView
    lateinit var pd_tv_more_details:TextView
    lateinit var pd_tv_p_name:TextView
    lateinit var pd_iv:ImageView
    lateinit var pd_pb:ProgressBar
    lateinit var pb_mat_btn_delete: MaterialButton
    lateinit var pd_fav_ib_add:ImageButton
    lateinit var pd_fav_ib_remove:ImageButton




    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_property_detail_view)

        initUiElements()

        val list_item_id = intent.extras?.getString("prop_id")
        val item_prop_mode = intent.extras?.getString("prop_mode")
        val itemImage = intent.extras?.getString("prop_image")
        val itemName = intent.extras?.getString("prop_name")
        val callingAct = intent.extras?.getString("to_go_activity")
//        class com.example.grihprawesh.PropertyDetailView
//        class com.example.grihprawesh.MyPropertyDetailWithEditDelete
        Log.d("Poko2", list_item_id.toString())
        Log.d("Poko2", item_prop_mode.toString())
        Log.d("Poko2", itemImage.toString())
        Log.d("Poko2", callingAct.toString())

        checkCallPermission()

//        If Details accessed from my listing then visible delete button else visible call button
        if(callingAct.toString() == "class com.example.grihprawesh.MyPropertyDetailWithEditDelete"){
            Log.d("Poko2","Called from My listing")
            pb_mat_btn_delete.visibility = View.VISIBLE
            pb_mat_btn_call.visibility = View.GONE
        }else{
            Log.d("Poko2","Normal Call")
            pb_mat_btn_call.visibility = View.VISIBLE
            pb_mat_btn_delete.visibility = View.GONE
        }

        pd_tv_p_name.text = itemName


        Glide.with(this@PropertyDetailView)
            .load(itemImage)
            .into(pd_iv)

//        myUserId = "bV200222@gmail.com"

        val userId = LoginDataM.lid


        handleOnClickListeners(list_item_id.toString(), item_prop_mode.toString(), myUserId = userId)


//        Checking if Favourite
        val fav_item = RequestUserIDFav(userid = userId)
        CoroutineScope(Dispatchers.Main).launch {
            fetchMyListingDataNew(fav_item, list_item_id.toString())
        }

//        Converting List Item Id to HouseItemId
        val userx = HouseItemId(list_item_id.toString())
//        Fetching Data based on mode
        if(item_prop_mode == "plot"){
            CoroutineScope(Dispatchers.IO).async {
                val userp = PlotIdReq(list_item_id.toString())
                val a0 = async { fetchPlotById(userp) }
                try{
                    val i = a0.await()?.get(0)
                    Log.d("Poko2", i.toString())
                    i?.let {
                        pd_tv_basic_details.text = "Height: ${it.height}\n" +
                                "Width: ${it.width}\n" +
                                "Area: ${it.area}\n" +
                                "Cost: ${it.cost}\n" +
                                "Location: ${it.location}"

                        runOnUiThread {
                            pd_tv_more_details.visibility = View.GONE
                        }
                    }
                    a0.await()
                }catch (e:ArrayIndexOutOfBoundsException){
                    e.printStackTrace()
                    Log.d("Poko", "Error Fetching Data: " + e.message.toString())
                }
            }
        }else if(item_prop_mode == "rent"){
            CoroutineScope(Dispatchers.IO).async {

                val a0 = async { fetchHouseRentById(userx) }
                try{
                    val i = a0.await()?.get(0)
                    Log.d("Poko2", i.toString())
                    i?.let {
                        pd_tv_basic_details.text = "Height: ${it.height}\n" +
                                "Width: ${it.width}\n" +
                                "Area: ${it.area}\n" +
                                "Cost: ${it.cost}\n" +
                                "Location: ${it.location}"

                        pd_tv_more_details.text = "BHK: ${it.bhk}\n" +
                                "BR: ${it.br}\n" +
                                "Property Details: ${it.propDetails}\n" +
                                "Amenities: ${it.amenities}"
                    }
                    a0.await()
                }catch (e:ArrayIndexOutOfBoundsException){
                    e.printStackTrace()
                    Log.d("Poko", "Error Fetching Data: " + e.message.toString())
                }
            }
        }else if(item_prop_mode == "sell"){
            CoroutineScope(Dispatchers.IO).async {

                val a0 = async { fetchHouseSellById(userx) }
                try{
                    val i = a0.await()?.get(0)
                    Log.d("Poko2", i.toString())
                    i?.let {
                        pd_tv_basic_details.text = "Height: ${it.height}\n" +
                                "Width: ${it.width}\n" +
                                "Area: ${it.area}\n" +
                                "Cost: ${it.cost}\n" +
                                "Location: ${it.location}"

                        pd_tv_more_details.text = "BHK: ${it.bhk}\n" +
                                "BR: ${it.br}\n" +
                                "Property Details: ${it.propDetails}\n" +
                                "Amenities: ${it.amenities}"
                    }
                    a0.await()
                }catch (e:ArrayIndexOutOfBoundsException){
                    e.printStackTrace()
                    Log.d("Poko", "Error Fetching Data: " + e.message.toString())
                }
            }
        }

    }

    //    Fetch House Sale Data
    @SuppressLint("SetTextI18n")
    private suspend fun fetchHouseSellById(userId: HouseItemId): ArrayList<HouseSellItem>? {
        val ansx = CoroutineScope(Dispatchers.IO).async {
            val response = RetrofitServicexx.usersInstace.getHouseSellById(1, userId)
            if (response.isSuccessful) {
                Log.d("Poko", "Fetched House Sell Detail Data")
                runOnUiThread{
                    pd_pb.visibility = View.GONE
                }
            } else {
                Log.d("Poko", "Failed to fetch")
//                Toast.makeText(applicationContext, "Failed to fetch", Toast.LENGTH_SHORT).show()
            }
            response.body()
        }

        return ansx.await()
    }

    //    Fetch House Sale Data
    @SuppressLint("SetTextI18n")
    private suspend fun fetchHouseRentById(userId: HouseItemId): ArrayList<HouseSellItem>? {
        val ansx = CoroutineScope(Dispatchers.IO).async {
            val response = RetrofitServicexx.usersInstace.getHouseRentById(1, userId)
            if (response.isSuccessful) {
                Log.d("Poko", "Fetched House Rent Detail Data")
                runOnUiThread{
                    pd_pb.visibility = View.GONE
                }
            } else {
                Log.d("Poko", "Failed to fetch")
//                Toast.makeText(applicationContext, "Failed to fetch", Toast.LENGTH_SHORT).show()
            }
            response.body()
        }

        return ansx.await()
    }

    //    Fetch House Sale Data
    @SuppressLint("SetTextI18n")
    private suspend fun fetchPlotById(userId: PlotIdReq): ArrayList<PlotResponseXItem>? {
        val ansx = CoroutineScope(Dispatchers.IO).async {
            val response = RetrofitServicexx.usersInstace.getPlotById(1, userId)
            if (response.isSuccessful) {
                Log.d("Poko", "Fetched Plot Detail Data")
                runOnUiThread{
                    pd_pb.visibility = View.GONE
                }
            } else {
                Log.d("Poko", "Failed to fetch")
//                Toast.makeText(applicationContext, "Failed to fetch", Toast.LENGTH_SHORT).show()
            }
            response.body()
        }

        return ansx.await()
    }


    //    Checks Calling Permission
    private fun checkCallPermission() {
        if(ActivityCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.CALL_PHONE), 101)
        }
    }

//    Bind UI elements
    private fun initUiElements() {
        pb_mat_btn_call = findViewById(R.id.pb_mat_btn_call)
        pd_tv_more_details = findViewById(R.id.pd_tv_more_details)
        pd_tv_basic_details = findViewById(R.id.pd_tv_basic_details)
        pd_iv = findViewById(R.id.pd_iv_x)
        pd_tv_p_name = findViewById(R.id.pd_tv_p_name)
        pd_pb = findViewById(R.id.pd_pb)
        pb_mat_btn_delete = findViewById(R.id.pb_mat_btn_delete)
        pd_fav_ib_add = findViewById(R.id.pd_fav_ib_add)
        pd_fav_ib_remove = findViewById(R.id.pd_fav_ib_remove)
    }

//    Handle Click Events
    private fun handleOnClickListeners(list_item_id:String, prop_mode:String, myUserId:String) {
        pb_mat_btn_call.setOnClickListener {
//            Log.d("Poko", "Call Clicked")
            //Set Calling Number
            val pno = "+916267120992"


//            val callIntent = Intent(Intent.ACTION_CALL)
            val callIntent = Intent(Intent.ACTION_DIAL)
            callIntent.data = Uri.parse("tel:$pno")
            startActivity(callIntent)

        }

        pb_mat_btn_delete.setOnClickListener {
            Log.d("Poko", "Delete Clicked")
            showDialog("Delete", list_item_id, prop_mode)
            //Set Calling Number
        }

        pd_fav_ib_add.setOnClickListener {
            Log.d("Poko", "Adding to Favourites")
            val favItem = FavouriteLItem(listitemid = list_item_id, userid = myUserId)
            addFavourite(favItem)
        }

        pd_fav_ib_remove.setOnClickListener {
            Log.d("Poko", "Removing From Favorites")
            val favItem = FavouriteLItem(listitemid = list_item_id, userid = myUserId)
            deleteFavByLidAndUid(favItem)
        }

    }

//    Displays Confirmation dialog
    private fun showDialog(msg:String, id:String, prop_mode: String) {
        val dialog = MaterialAlertDialogBuilder(this)
            .setTitle("$msg Listing")
            .setMessage("Do you want to $msg this listing?")
            .setPositiveButton("OK") { dialogInterface: DialogInterface, _: Int ->
                // Handle positive button click

                if(msg == "Delete"){

                    if(prop_mode == "plot"){
//                When Mode is plot deleting plot item
                        val userx =  PlotIdReq(id)
                        deletePlot(userx)
                    }else if(prop_mode == "rent"){
//                When Mode is rent deleting rent item
                        val userx =  HouseItemId(id)
                        deleteHouseRent(userx)
                    }else if(prop_mode == "sell"){
//                When Mode is sell deleting sell item
                        val userx =  HouseItemId(id)
                        deleteHouseSell(userx)
                    }

                    TODO("Delete API For Listing")
                }
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


//    Deletion Api
    private fun deleteHouseRent(idItem: HouseItemId) {
    val call = RetrofitServicexx.usersInstace.deleteHouseRentById(idItem)
    call.enqueue(object : Callback<GeneralMessage> {
        override fun onResponse(
            call: Call<GeneralMessage>,
            response: Response<GeneralMessage>
        ) {
            if (response.isSuccessful) {
                Log.d("Poko", response.body().toString())
                Toast.makeText(this@PropertyDetailView, "House On Rent Successfully Deleted", Toast.LENGTH_SHORT).show()

            } else if (response.code() == 400) Log.d("Poko", "Failed to Delete")
        }

        override fun onFailure(call: Call<GeneralMessage>, t: Throwable) {
            Log.d("Poko", "Error in fetching response", t)
        }
    })
}

    private fun deleteHouseSell(idItem: HouseItemId) {
        val call = RetrofitServicexx.usersInstace.deleteHouseSellById(idItem)
        call.enqueue(object : Callback<GeneralMessage> {
            override fun onResponse(
                call: Call<GeneralMessage>,
                response: Response<GeneralMessage>
            ) {
                if (response.isSuccessful) {
                    Log.d("Poko", response.body().toString())
                    Toast.makeText(this@PropertyDetailView, "House on Sale Successfully Deleted", Toast.LENGTH_SHORT).show()
                } else if (response.code() == 400) Log.d("Poko", "Failed to Delete")
            }

            override fun onFailure(call: Call<GeneralMessage>, t: Throwable) {
                Log.d("Poko", "Error in fetching response", t)
            }
        })
    }

    private fun deletePlot(idItem: PlotIdReq) {
        val call = RetrofitServicexx.usersInstace.deletePlotById(idItem)
        call.enqueue(object : Callback<GeneralMessage> {
            override fun onResponse(
                call: Call<GeneralMessage>,
                response: Response<GeneralMessage>
            ) {
                if (response.isSuccessful) {
                    Log.d("Poko", response.body().toString())
                    Toast.makeText(this@PropertyDetailView, "Plot Successfully Deleted", Toast.LENGTH_SHORT).show()
                } else if (response.code() == 400) Log.d("Poko", "Failed to Delete")
            }

            override fun onFailure(call: Call<GeneralMessage>, t: Throwable) {
                Log.d("Poko", "Error in fetching response", t)
            }
        })
    }

//    Remove From Favourites Api
    private fun deleteFavByLidAndUid(favData: FavouriteLItem) {
        val call = RetrofitServicexx.usersInstace.deleteFavByUserIdAndLid(favData)
        call.enqueue(object : Callback<GeneralMessage> {
            override fun onResponse(
                call: Call<GeneralMessage>,
                response: Response<GeneralMessage>
            ) {
                if (response.isSuccessful) {
                    Log.d("Poko", response.body().toString())
                    pd_fav_ib_remove.visibility = View.GONE
                    pd_fav_ib_add.visibility = View.VISIBLE

                    Toast.makeText(this@PropertyDetailView, "Removed From Favorites", Toast.LENGTH_SHORT).show()
                } else if (response.code() == 400) Log.d("Poko", "Failed to Delete")
            }

            override fun onFailure(call: Call<GeneralMessage>, t: Throwable) {
                Log.d("Poko", "Error in fetching response", t)
            }
        })
    }

    //  Add to Favorites Api
    private fun addFavourite(favData: FavouriteLItem) {
    val call = RetrofitServicexx.usersInstace.postFavourite(favData)
    call.enqueue(object : Callback<FavouriteLItem> {
        override fun onResponse(
            call: Call<FavouriteLItem>,
            response: Response<FavouriteLItem>
        ) {
            if (response.isSuccessful) {
                Toast.makeText(this@PropertyDetailView, "Favorite item added", Toast.LENGTH_SHORT).show()
//                pd_fav_ib_add.setBackgroundResource(R.drawable.star_solid)
                pd_fav_ib_remove.visibility = View.VISIBLE
                pd_fav_ib_add.visibility = View.GONE
                Log.d("Poko", response.body().toString())
            } else if (response.code() == 400){
                Toast.makeText(this@PropertyDetailView, "Favorite Already Exist", Toast.LENGTH_SHORT).show()
                Log.d("Poko", "Favourite Already Exist")
            }
        }

        override fun onFailure(call: Call<FavouriteLItem>, t: Throwable) {
            Log.d("Poko", "Error in fetching response", t)
        }
    })
}

    //    Fetch House Sale Data     Favourites
    private suspend fun fetchMyListingDataNew(userId: RequestUserIDFav, item_id: String): Any {
        val ansx = CoroutineScope(Dispatchers.IO).async {
            val response = RetrofitServicexx.usersInstace.getFavouritesByUserId(userId)
            if (response.isSuccessful) {

                for (i in response.body()!!) {
                    if(i != null){
                        Log.d("Poko", i.toString())
                        val det = i.propDetails.toString().split('|');
//                    Log.d("Poko2", i.toString())
                        Log.d("Poko2", "PropID: \t"+ det[0])
//                  plot        sell        rent
                        runOnUiThread{
                            if(det[0] == item_id){
                                pd_fav_ib_remove.visibility = View.VISIBLE
                                pd_fav_ib_add.visibility = View.GONE
                            }
                        }
                    }
                }
            } else {
                Log.d("Poko", "Failed to fetch")
//                Toast.makeText(requireContext(), "Failed to fetch", Toast.LENGTH_SHORT).show()
            }
        }

        return ansx.await()
    }


}
