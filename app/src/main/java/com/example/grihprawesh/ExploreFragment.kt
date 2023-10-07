package com.example.grihprawesh

import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.grihprawesh.apix.RetrofitServicexx
import com.example.grihprawesh.modelxx.HouseSellItem
import com.example.grihprawesh.modelxx.ListItem
import com.example.grihprawesh.modelxx.RequestUserIDFav
import com.example.grihprawesh.myadapters.RvItemAdapter
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

class ExploreFragment : Fragment() {

//    RV House Sale
    lateinit var rv_house_for_sale: RecyclerView
    lateinit var rv_house_sale_list:ArrayList<ListItem>
    lateinit var rv_house_sale_adapter: RvItemAdapter
//    RV Plot Sale
    lateinit var rv_plot_sale: RecyclerView
    lateinit var rv_plot_sale_list:ArrayList<ListItem>
    lateinit var rv_plot_sale_adapter: RvItemAdapter
//    RV House Rent
    lateinit var rv_house_rent: RecyclerView
    lateinit var rv_house_rent_list:ArrayList<ListItem>
    lateinit var rv_house_rent_adapter: RvItemAdapter
//    RV Search Result
    lateinit var rv_search_res: RecyclerView
    lateinit var rv_search_res_list:ArrayList<ListItem>
    lateinit var rv_search_res_adapter: RvItemAdapter
//    Search
    lateinit var ib_search:ImageButton; lateinit var ef_et_search:EditText
    lateinit var ef_tv_loc:TextView

    lateinit var ll_search_res:LinearLayout
    lateinit var ll_seqarch_res_text:LinearLayout
    lateinit var ll_explore_def:LinearLayout
//    Progress Bar
    lateinit var pb_house_sale:ProgressBar
    lateinit var pb_plot_sale:ProgressBar
    lateinit var pb_house_rent:ProgressBar
    lateinit var ef_pb_search_res_list:ProgressBar



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val rv = inflater.inflate(R.layout.fragment_explore, container, false)

//        Initialize UI components
        initUiElements(rv)

//        OnClick Listeners
        handleOnClickListeners(rv)

//        Progress Bar Visibility On
        activity?.runOnUiThread{
            pb_house_sale.visibility = View.VISIBLE
            pb_plot_sale.visibility = View.VISIBLE
            pb_house_rent.visibility = View.VISIBLE
        }

//        Fetching Data from House Sale, Plot, and House Rent
        fetchAndBindHouseSale()
        fetchAndBindPlotSale()
        fetchAndBindHouseRent()

        return rv
    }


//    -----------------------Fetch And Fill Functions

//    Search
//    Fetch and Bind images and Data
    private fun fetchAndBindSearchResults(location:String) {

        CoroutineScope(Dispatchers.IO).launch {
            val a = CoroutineScope(Dispatchers.IO).async {
//                Fetching Data
                val a0 = async { fetchSearchResultsData(location) }
                a0.await()
//                Fetching Images
                val a2 = fetchSearchResultsImages(a0.await())
                a2
            }

            val listX = a.await()
            activity?.runOnUiThread {

                Log.d("Poko", "List: $listX")
//                Passing data to recycler view
                rv_search_res_adapter = RvItemAdapter(listX, context)
                rv_search_res.adapter = rv_search_res_adapter

//                After fetching and binding removing progressbar
                ef_pb_search_res_list.visibility = View.GONE
            }
        }
    }

//    Fetch Search Result Data
    private suspend fun fetchSearchResultsData(location:String):ArrayList<ListItem> {
        val ansx = CoroutineScope(Dispatchers.IO).async {
            val response = RetrofitServicexx.usersInstace.getPropertiesByLocation(location)
            if (response.isSuccessful) {
                for (i in response.body()!!) {
                    if(i != null){

//                        Destructuring the property details Data
                        val prop_det = i.propDetails.toString().split('|')

//                        Creating ListItem from fetched Data
                        val a = ListItem(
                            prop_image = i.propImage.toString(),
                            prop_name = i.propName.toString(),
                            prop_mode = i.propMode.toString(),
                            prop_details = "${prop_det[0]}|${prop_det[1]}\n${prop_det[2]}",
                            prop_location = i.propLocation.toString()
                        )

//                        Adding to list
                        rv_search_res_list.add(a)
                    }
                }
            } else {
//                If Failed
                Log.d("Poko", "Failed to fetch")
            }
            rv_search_res_list
        }

        return ansx.await()
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
    private suspend fun fetchSearchResultsImages(rvListItemList: ArrayList<ListItem>): ArrayList<ListItem> {
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

//    ---------------------------
//    House Sale
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
                rv_house_sale_adapter = RvItemAdapter(listX, context)
                rv_house_for_sale.adapter = rv_house_sale_adapter

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

                    rv_house_sale_list.add(a)
                }
            } else {
                Log.d("Poko", "Failed to fetch")
//                Toast.makeText(requireContext(), "Failed to fetch", Toast.LENGTH_SHORT).show()
            }
            rv_house_sale_list
        }

        return ansx.await()
    }

//    ---------------------------
//  Plot Sale
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
                rv_plot_sale_adapter = RvItemAdapter(listX, context)
                rv_plot_sale.adapter = rv_plot_sale_adapter

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

                    rv_plot_sale_list.add(a)
                }
            } else {
                Log.d("Poko", "Failed to fetch")
//                Toast.makeText(requireContext(), "Failed to fetch", Toast.LENGTH_SHORT).show()
            }
            rv_plot_sale_list
        }

        return ansx.await()
    }

//    ---------------------------
//    House Rent
    //    Function to bind and fetch for House Sale
    private fun fetchAndBindHouseRent() {

        CoroutineScope(Dispatchers.IO).launch {
            val a = CoroutineScope(Dispatchers.IO).async {
                val a1 = async { fetchHouseRentData() }
                a1.await()
                val a2 = fetchHouseRentImage(a1.await())
                a2
            }

            val listX = a.await()
            activity?.runOnUiThread {
                Log.d("Poko", "List: $listX")
//                Passing data to recycler view
                rv_house_rent_adapter = RvItemAdapter(listX, context)
                rv_house_rent.adapter = rv_house_rent_adapter

//                After fetching and binding removing progressbar
                pb_house_rent.visibility = View.GONE
            }
        }
    }

    //    Fetch images from For house sale list
    private suspend fun fetchHouseRentImage(rvListItemList: ArrayList<ListItem>): ArrayList<ListItem> {
//        Create Reference of storage
        val storage = FirebaseStorage.getInstance()
        val storageRef: StorageReference = storage.reference
        val rvListItemNewList = ArrayList<ListItem>()

        val deferredList = CoroutineScope(Dispatchers.IO).async {
            rvListItemList.map { i ->

                suspendCoroutine<ListItem?> { continuation ->
                    val imagesRef: StorageReference =
                        storageRef.child("houserent/${i.prop_image}.jpg")

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

    //    Fetch House Rent Data
    private suspend fun fetchHouseRentData():ArrayList<ListItem> {
        val ansx = CoroutineScope(Dispatchers.IO).async {
            val response = RetrofitServicexx.usersInstace.getAllHouseRent()
            if (response.isSuccessful) {
                for (i in response.body()!!) {
//                    Log.d("Poko", i.toString())

                    val a = ListItem(
                        prop_image = i.image.toString(),
                        prop_name = i.name.toString(),
                        prop_mode = "rent",
                        prop_details = "${i.listItemId}|₹${i.cost}\n${i.bhk}BHK",
                        prop_location = i.location.toString()
                    )
                    Log.d("Poko", a.toString())

                    rv_house_rent_list.add(a)
                }
            } else {
                Log.d("Poko", "Failed to fetch")
//                Toast.makeText(requireContext(), "Failed to fetch", Toast.LENGTH_SHORT).show()
            }
            rv_house_rent_list
        }

        return ansx.await()
    }

//--------------------------------
//    Initialize UI Elements
    private fun initUiElements(rv: View) {
//        Simple UI Elements
        ib_search = rv.findViewById(R.id.ef_ib_search)
        ll_explore_def = rv.findViewById(R.id.ll_ef_explore_def)
        ll_search_res = rv.findViewById(R.id.ll_ef_search_res)
        ll_seqarch_res_text = rv.findViewById(R.id.ll_ef_search_res_text)
        ef_et_search = rv.findViewById(R.id.ef_et_search)
        ef_pb_search_res_list = rv.findViewById(R.id.ef_pb_search_res_list)
        ef_tv_loc = rv.findViewById(R.id.ef_tv_loc)

//        Recycler Views
        pb_house_sale = rv.findViewById(R.id.ef_pb_house_sale_list)
        pb_house_rent = rv.findViewById(R.id.ef_pb_house_rent_list)
        pb_plot_sale = rv.findViewById(R.id.ef_pb_plot_sale_list)

//        Making Export Default Linear Layout Visible
        ll_explore_def.visibility = View.VISIBLE
        ll_search_res.visibility = View.GONE

//        Initializing ArrayLists for RV
        rv_house_sale_list = ArrayList()
        rv_plot_sale_list = ArrayList()
        rv_house_rent_list = ArrayList()

//        Setting up RV
        rv_house_for_sale = rv.findViewById(R.id.rv_ef_house_for_sale)
        rv_house_for_sale.setHasFixedSize(true)
        rv_house_for_sale.layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)

        rv_plot_sale = rv.findViewById(R.id.rv_ef_plot_for_sale)
        rv_plot_sale.setHasFixedSize(true)
        rv_plot_sale.layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)

        rv_house_rent = rv.findViewById(R.id.rv_ef_house_for_rent)
        rv_house_rent.setHasFixedSize(true)
        rv_house_rent.layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)


//        Handle Searches From Home Fragment
        intiSearchFromHomeFrag(rv)

    }

//    Search From Home Fragment
    private fun intiSearchFromHomeFrag(rv: View) {
//    If searched from Home Fragment
        if(SelectedOption.searchedFrom == 1){
//            If Searched Text is not empty?
            if(SelectedOption.searchedText != ""){
//                UI text view
                ef_tv_loc.text = SelectedOption.searchedText
//              Set ET text
                ef_et_search.setText(SelectedOption.searchedText)
                SelectedOption.searchedFrom = 0
//                Toggling searched view visibility
                handleSearchedViewVisibility(rv, SelectedOption.searchedText)
            }
        }

    }

// Visibility of searched icons
    private fun handleSearchedViewVisibility(rv: View, searchText: String) {
        ll_explore_def.visibility = View.GONE

        initRvSearchResGrid(rv, searchText)

        ll_seqarch_res_text.visibility = View.VISIBLE
        ll_search_res.visibility = View.VISIBLE
    }

//    Handles on Click events
    private fun handleOnClickListeners(rv:View) {
//        Search Button onClick
        ib_search.setOnClickListener {
            if(ef_et_search.text.toString() != ""){
//                UI Text
                ef_tv_loc.text = ef_et_search.text.toString()

//                Search from Explore Fragment
                SelectedOption.searchedFrom = 0
                handleSearchedViewVisibility(rv, ef_et_search.text.toString())
            }
        }
    }

//    Initialize Search Result RV
    private fun initRvSearchResGrid(rv:View, searchText:String) {
//      Initializing Search Result RV
        rv_search_res = rv.findViewById(R.id.ef_rv_search_res_grid)
        rv_search_res.setHasFixedSize(true)
        rv_search_res.layoutManager = GridLayoutManager(context, 2, GridLayoutManager.VERTICAL, false)
        rv_search_res_list = ArrayList()

//      Fetching and Binding Search Results
        fetchAndBindSearchResults(searchText)
    }

}