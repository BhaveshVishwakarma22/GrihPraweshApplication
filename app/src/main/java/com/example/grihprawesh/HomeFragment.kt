package com.example.grihprawesh

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageButton
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.grihprawesh.apix.RetrofitServicexx
import com.example.grihprawesh.modelxx.HouseSellItem
import com.example.grihprawesh.modelxx.ListItem
import com.example.grihprawesh.modelxx.PlotResponseXItem
import com.example.grihprawesh.myadapters.RvItemAdapter
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine
import kotlin.time.measureTime

object SelectedOption {
    var sOption = 0
    var searchedText: String = ""
    var searchedFrom = 0
}

class HomeFragment : Fragment() {

    lateinit var rv_look_house: RecyclerView
    lateinit var rv_loik_house_list: ArrayList<ListItem>
    lateinit var rv_look_house_adapter: RvItemAdapter

    lateinit var rv_look_plot: RecyclerView
    lateinit var rv_loik_plot_list: ArrayList<ListItem>
    lateinit var rv_look_plot_adapter: RvItemAdapter

    lateinit var ib_hf_sell_a_plot: ImageButton;
    lateinit var ib_hf_sell_a_house: ImageButton;
    lateinit var ib_hf_put_house_on_rent: ImageButton;
    lateinit var hf_ibtn_search: ImageButton;
    lateinit var hf_et_search: EditText

    lateinit var hf_hi_name: TextView

    lateinit var pb_house_sale:ProgressBar
    lateinit var pb_plot_sale:ProgressBar

    @SuppressLint("SetTextI18n")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        val rv = inflater.inflate(R.layout.fragment_home, container, false)

        initUiElements(rv)

        if (LoginDataM.lemail != null) {
            hf_hi_name.text = "Hi ${LoginDataM.lfname}!"
        }

        Log.d("Poko2", "From Home Fragment: ${LoginDataM.lid};${LoginDataM.lfname};${LoginDataM.llname};${LoginDataM.lemail};${LoginDataM.pass};${LoginDataM.lphone};${LoginDataM.limage}")


        rv_look_house.layoutManager =
            LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
        rv_look_house.setHasFixedSize(true)

//        Setting Progressbar visible when fetching
        activity?.runOnUiThread{
            pb_house_sale.visibility = View.VISIBLE
            pb_plot_sale.visibility = View.VISIBLE
        }

//        Fetching HouseSaleList data and filling it in Recycler View
        fetchAndBindHouseSale()
//        Fetching PlotSaleList data and filling it in Recycler View
        fetchAndBindPlotSale()

        handleOnClickListeners()

        return rv
    }

    //    Initialize all UI elements
    private fun initUiElements(rv: View) {
        ib_hf_sell_a_plot = rv.findViewById(R.id.ib_hf_sell_a_plot)
        ib_hf_sell_a_house = rv.findViewById(R.id.ib_hf_sell_a_house)
        ib_hf_put_house_on_rent = rv.findViewById(R.id.ib_hf_put_house_on_rent)
        hf_hi_name = rv.findViewById(R.id.hf_hi_name)

        hf_ibtn_search = rv.findViewById(R.id.hf_ibtn_search)
        hf_et_search = rv.findViewById(R.id.hf_et_search)

        rv_look_house = rv.findViewById(R.id.rv_hf_look_house)
        rv_look_plot = rv.findViewById(R.id.rv_hf_look_plot)

        pb_house_sale = rv.findViewById(R.id.hf_pb_house_sale_list)
        pb_plot_sale = rv.findViewById(R.id.hf_pb_plot_list)

        rv_loik_house_list = ArrayList()
        rv_loik_plot_list = ArrayList()
    }

    private suspend fun networkCall1(): String {
        delay(2000L)
        return "Answer1"
    }

    private suspend fun networkCall2(): String {
        delay(2000L)
        return "Answer1"
    }

//    Function to bind and fetch for House Sale
    private fun fetchAndBindHouseSale() {

        CoroutineScope(Dispatchers.IO).launch {
            val a = CoroutineScope(Dispatchers.IO).async {
                val a1 = async { fetchHouseSaleData() }
                a1.await()
                val a2 = fetchHouseSaleImage(a1.await())
                a2
            }

            val listX = a.await()
            activity?.runOnUiThread {
                Log.d("Poko", "List: $listX")
//                Passing data to recycler view
                rv_look_house_adapter = RvItemAdapter(listX, context)
                rv_look_house.adapter = rv_look_house_adapter

//                After fetching and binding removing progressbar
                pb_house_sale.visibility = View.GONE
            }
        }
    }

//    Fetch images from For house sale list
    private suspend fun fetchHouseSaleImage(rvListItemList: ArrayList<ListItem>): ArrayList<ListItem> {
//        Create Reference of storage
        val storage = FirebaseStorage.getInstance()
        val storageRef: StorageReference = storage.reference
        val rvListItemNewList = ArrayList<ListItem>()

        val deferredList = CoroutineScope(Dispatchers.IO).async {
            rvListItemList.map { i ->

                suspendCoroutine<ListItem?> { continuation ->
                    val imagesRef: StorageReference =
                        storageRef.child("housesale/${i.prop_image}.jpg")

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
                        Log.d("Poko", "Failed: ${exception.message}")
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

//    Fetch House Sale Data
    private suspend fun fetchHouseSaleData():ArrayList<ListItem> {
        val ansx = CoroutineScope(Dispatchers.IO).async {
            val response = RetrofitServicexx.usersInstace.getAllHouseSell()
            if (response.isSuccessful) {
                for (i in response.body()!!) {
//                    Log.d("Poko", i.toString())

                    val a = ListItem(
                        prop_image = i.image.toString(),
                        prop_name = i.name.toString(),
                        prop_mode = "sell",
                        prop_details = "${i.listItemId}|₹${i.cost}\n${i.bhk}BHK",
                        prop_location = i.location.toString()
                    )
                    Log.d("Poko", a.toString())

                    rv_loik_house_list.add(a)
                }
            } else {
                Log.d("Poko", "Failed to fetch")
//                Toast.makeText(requireContext(), "Failed to fetch", Toast.LENGTH_SHORT).show()
            }
            rv_loik_house_list
        }

        return ansx.await()
    }


//    Function to bind and fetch for plot Sale
    private fun fetchAndBindPlotSale() {

        CoroutineScope(Dispatchers.IO).launch {
            val a = CoroutineScope(Dispatchers.IO).async {
                val a1 = async { fetchPlotData() }
                a1.await()
                val a2 = fetchPlotImage(a1.await())
                a2
            }

            val listX = a.await()
            activity?.runOnUiThread {
                Log.d("Poko", "List: $listX")
//                Passing data to recycler view
                rv_look_plot_adapter = RvItemAdapter(listX, context)
                rv_look_plot.adapter = rv_look_plot_adapter

//                After fetching and binding removing progressbar
                pb_plot_sale.visibility = View.GONE
            }
        }
    }

//    Fetch images from For plot sale list
    private suspend fun fetchPlotImage(rvListItemList: ArrayList<ListItem>): ArrayList<ListItem> {
//        Create Reference of storage
        val storage = FirebaseStorage.getInstance()
        val storageRef: StorageReference = storage.reference
        val rvListItemNewList = ArrayList<ListItem>()

        val deferredList = CoroutineScope(Dispatchers.IO).async {
            rvListItemList.map { i ->

                suspendCoroutine<ListItem?> { continuation ->
                    val imagesRef: StorageReference =
                        storageRef.child("plot/${i.prop_image}.jpg")

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
                        Log.d("Poko", "Failed: ${exception.message}")
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

//    Fetch Plot Sale Data
    private suspend fun fetchPlotData():ArrayList<ListItem> {
        val ansx = CoroutineScope(Dispatchers.IO).async {
            val response = RetrofitServicexx.usersInstace.getAllPlot()
            if (response.isSuccessful) {
                for (i in response.body()!!) {
//                    Log.d("Poko", i.toString())

                    val a = ListItem(
                        prop_image = i.image.toString(),
                        prop_name = i.name.toString(),
                        prop_mode = "plot",
                        prop_details = "${i.id}|₹${i.cost}\n${i.area}",
                        prop_location = i.location.toString()
                    )
                    Log.d("Poko", a.toString())

                    rv_loik_plot_list.add(a)
                }
            } else {
                Log.d("Poko", "Failed to fetch")
//                Toast.makeText(requireContext(), "Failed to fetch", Toast.LENGTH_SHORT).show()
            }
            rv_loik_plot_list
        }

        return ansx.await()
    }


//    Handle on click event on UI components
    private fun handleOnClickListeners() {

        //Handles Post New Property section Buttons
        ib_hf_sell_a_plot.setOnClickListener {
            SelectedOption.sOption = 1
            BottomNav.bottom_nav_bar.selectedItemId = R.id.bnv_addListing
        }

        ib_hf_sell_a_house.setOnClickListener {
            SelectedOption.sOption = 2
            BottomNav.bottom_nav_bar.selectedItemId = R.id.bnv_addListing
        }

        ib_hf_put_house_on_rent.setOnClickListener {
            SelectedOption.sOption = 3
            BottomNav.bottom_nav_bar.selectedItemId = R.id.bnv_addListing
        }

        hf_ibtn_search.setOnClickListener {
            if (hf_et_search.text.toString() != "") {
                SelectedOption.searchedText = hf_et_search.text.toString()
                SelectedOption.searchedFrom = 1
                BottomNav.bottom_nav_bar.selectedItemId = R.id.bnv_explore
            }
        }
    }

}