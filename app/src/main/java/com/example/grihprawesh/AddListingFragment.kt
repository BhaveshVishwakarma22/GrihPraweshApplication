package com.example.grihprawesh

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.Toast
import androidx.cardview.widget.CardView
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.example.grihprawesh.apix.RetrofitServicexx
import com.example.grihprawesh.modelxx.FavouriteLItem
import com.example.grihprawesh.modelxx.GeneralMessage
import com.example.grihprawesh.modelxx.HouseSellItem
import com.example.grihprawesh.modelxx.PlotResponseXItem
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale


class AddListingFragment : Fragment() {

    var iv_mode_val = 0;
    lateinit var ib_sp:ImageButton; lateinit var ib_sh:ImageButton; lateinit var ib_rh:ImageButton
    lateinit var cl_sp:CardView;lateinit var cl_sh:CardView;lateinit var cl_rh:CardView
    lateinit var iv_sp:ImageView;lateinit var iv_sh:ImageView;lateinit var iv_rh:ImageView
    lateinit var ib_sp_adImg:ImageView;lateinit var ib_sh_adImg:ImageView;lateinit var ib_rh_adImg:ImageView

    lateinit var al_pb:ProgressBar

    lateinit var al_sh_cg_p_by:ChipGroup; lateinit var al_sh_cg_EntranceFacing:ChipGroup; lateinit var al_sh_cg_p_age:ChipGroup; lateinit var al_sh_cg_possesion:ChipGroup; lateinit var al_sh_cg_p_type:ChipGroup; lateinit var al_sh_cg_mp_type:ChipGroup; lateinit var unitChipGroup:ChipGroup; lateinit var al_sh_cg_Amenities:ChipGroup; lateinit var al_sh_cg_furnishing:ChipGroup;
//  Poko
    lateinit var al_sh_et_name:EditText; lateinit var al_sh_et_location:EditText; lateinit var al_sh_et_height:EditText; lateinit var al_sh_et_width:EditText; lateinit var al_sh_et_cost:EditText; lateinit var al_sh_et_area:EditText
    lateinit var al_sh_et_bhk:EditText; lateinit var al_sh_et_br:EditText;
    lateinit var al_btn_sh_add:Button; lateinit var al_btn_sp_add:Button; lateinit var al_btn_rh_add:Button;

//    Plot Input define
    lateinit var al_sp_et_name:EditText; lateinit var al_sp_et_height:EditText;lateinit var al_sp_et_width:EditText;lateinit var al_sp_et_area:EditText;lateinit var al_sp_et_cost:EditText;lateinit var al_sp_et_location:EditText;

//    House Rent Input Define
    lateinit var al_rh_et_name:EditText; lateinit var al_rh_et_width:EditText;lateinit var al_rh_et_area:EditText;lateinit var al_rh_et_cost:EditText;lateinit var al_rh_et_location:EditText;lateinit var al_rh_et_height:EditText;
    lateinit var al_rh_et_br:EditText; lateinit var al_rh_et_bhk:EditText;

    lateinit var al_rh_cg_mp_type:ChipGroup; lateinit var al_rh_cg_p_type:ChipGroup; lateinit var al_rh_cg_possesion:ChipGroup; lateinit var al_rh_cg_p_age:ChipGroup; lateinit var al_rh_cg_EntranceFacing:ChipGroup; lateinit var al_rh_cg_p_by:ChipGroup; lateinit var al_rh_cg_Amenities:ChipGroup; lateinit var al_rh_cg_furnishing:ChipGroup;


    //    Default Values
    var units = "ft"
    var propDetails="1 1 1 1 1 1 1"
    var amenities="1 1 1 1 1 1 1 1 1 1 1 1"
    var funishing = "1"
    var efacing = "1"


//    Firebase
    lateinit var imageUri: Uri
    lateinit var storgeRefrence: StorageReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val rv = inflater.inflate(R.layout.fragment_add_listing, container, false)

        initUIElements(rv)
        initUnitChip(rv)
        al_pb.visibility = View.GONE

        val userId = LoginDataM.lid

        addImageFun()

        handleOpenFromHomeFrag()

        openTabs()

//        myUserId = "bV200222@gmail.com"

        handleOnClickListeners(rv, myUserId = userId)

        return rv
    }

    private fun handleOnClickListeners(rv: View?, myUserId:String) {
        lateinit var name:String;
        lateinit var cost:String;
        lateinit var location:String;
        lateinit var widht:String;
        lateinit var height:String;
        lateinit var area:String;
        lateinit var imageName:String;


        al_sp_et_height.addTextChangedListener {
            try{
                val w = al_rh_et_width.text.toString().toInt()
                al_rh_et_area.setText((w * al_rh_et_height.text.toString().toInt()).toString())
            }catch (e:Exception){
                Log.d("Poko", "Error Computing Area")
            }
        }

//        Adding Plot
        al_sp_et_width.addTextChangedListener {
            try{
                val w = al_rh_et_width.text.toString().toInt()
                al_rh_et_area.setText((w * al_rh_et_height.text.toString().toInt()).toString())
            }catch (e:Exception){
                Log.d("Poko", "Error Computing Area\n${e.printStackTrace()}")
            }
        }

//        Add Plot Button
        al_btn_sp_add.setOnClickListener {
//            Making Progress bar visible
            al_pb.visibility = View.VISIBLE

//        Checking if any fields are empty ?
            if(al_sp_et_name.text.toString()==""
                || al_sp_et_height.text.toString()==""
                || al_sp_et_width.text.toString()==""
                || al_sp_et_area.text.toString() ==""
                || al_sp_et_cost.text.toString() ==""
                || al_sp_et_location.text.toString() == ""
                    ){
                Toast.makeText(context, "Empty Fields", Toast.LENGTH_SHORT).show()

            }else{
//                Fetching data from UI
                name = al_sp_et_name.text.toString()
                height = al_sp_et_height.text.toString()+"ft"
                widht = al_sp_et_width.text.toString()+"ft"
                area = al_sp_et_area.text.toString()+"sqft"
                cost = al_sp_et_cost.text.toString()
                location = al_sp_et_location.text.toString()
                imageName = name.lowercase().replace(" ", "")+height


                Log.d("Poko", "Plot:\n" +
                        "Name: ${name}\n" +
                        "Height: ${height}\n" +
                        "Width: ${widht}\n" +
                        "Area: $area\n" +
                        "Cost: $cost\n" +
                        "Location: $location\n" +
                        "Image Name: $imageName")

//              Checking If Image is picked
                if(iv_mode_val != 1){
                    Toast.makeText(context, "Image Not Picked", Toast.LENGTH_SHORT).show()
                }else{

                    uploadImageToFirebase(imageName, "plot")
                    val plotData = PlotResponseXItem(imageName, name, height, widht, area, imageName, cost, location)
                    addPlot(plotData)
//  Poko
                    val favItem = FavouriteLItem(listitemid = imageName, userid = myUserId)
                    addMyListing(favItem)
                }
            }

        }

//        Add House Sale Button
        al_btn_sh_add.setOnClickListener {
            var sbhk:String = "";
            var sbr:String = "";
            var spropDetails:String = ""
            var sAmenities:String = "";
//            Fetching Data of Selected Chips
//            Furnishing
            var ids: List<Int> = al_sh_cg_furnishing.getCheckedChipIds()
            for (id in ids) {
                val chip: Chip = al_sh_cg_furnishing.findViewById(id)
                spropDetails += chip.text.toString() + " "
            }
//          Main Property Type
            ids = al_sh_cg_mp_type.getCheckedChipIds()
            for (id in ids) {
                val chip: Chip = al_sh_cg_mp_type.findViewById(id)
                spropDetails += chip.text.toString() + " "
            }
//            Property Type
            ids = al_sh_cg_p_type.getCheckedChipIds()
            for (id in ids) {
                val chip: Chip = al_sh_cg_p_type.findViewById(id)
                spropDetails += chip.text.toString() + " "
            }
//            Property Age
            ids = al_sh_cg_p_age.getCheckedChipIds()
            for (id in ids) {
                val chip: Chip = al_sh_cg_p_age.findViewById(id)
                spropDetails += chip.text.toString() + " "
            }
//            Entrance Facing
            ids = al_sh_cg_EntranceFacing.getCheckedChipIds()
            for (id in ids) {
                val chip: Chip = al_sh_cg_EntranceFacing.findViewById(id)
                spropDetails += chip.text.toString() + " "
            }
//            Posted By
            ids = al_sh_cg_p_by.getCheckedChipIds()
            for (id in ids) {
                val chip: Chip = al_sh_cg_p_by.findViewById(id)
                spropDetails += chip.text.toString() + " "
            }
//            Possesion
            ids = al_sh_cg_possesion.getCheckedChipIds()
            for (id in ids) {
                val chip: Chip = al_sh_cg_possesion.findViewById(id)
                spropDetails += chip.text.toString()
            }
//            Amenities
            ids = al_sh_cg_Amenities.getCheckedChipIds()
            for (id in ids) {
                val chip: Chip = al_sh_cg_Amenities.findViewById(id)
                sAmenities += chip.text.toString() + " "
            }

//            ----------

            if(al_sh_et_name.text.toString()==""
                || al_sh_et_height.text.toString()==""
                || al_sh_et_width.text.toString()==""
                || al_sh_et_area.text.toString() ==""
                || al_sh_et_height.text.toString() ==""
                || al_sh_et_location.text.toString() == ""
                || al_sh_et_bhk.text.toString() == ""
                || al_sh_et_br.text.toString() == ""
            ){
                Toast.makeText(context, "Empty Fields", Toast.LENGTH_SHORT).show()

            }else{
//                Fetching data from UI
                name = al_sh_et_name.text.toString()
                height = al_sh_et_height.text.toString()+"ft"
                widht = al_sh_et_width.text.toString()+"ft"
                area = al_sh_et_area.text.toString()+"sqft"
                cost = al_sh_et_cost.text.toString()
                location = al_sh_et_location.text.toString()
                imageName = name.lowercase().replace(" ", "")+height
                sbhk = al_sh_et_bhk.text.toString()
                sbr = al_sh_et_br.text.toString()

                Log.d("Poko", "House Sale:\n" +
                        "Name: ${name}\n" +
                        "Height: ${height}\n" +
                        "Width: ${widht}\n" +
                        "Area: $area\n" +
                        "Cost: $cost\n" +
                        "BHK: $sbhk\n" +
                        "BR: $sbr\n" +
                        "Location: $location\n" +
                        "Image Name: $imageName\n" +
                        "Property Details: ${spropDetails}\n" +
                        "Amenities: ${sAmenities}")

//              If Image is picked
                if(iv_mode_val != 2){
                    Toast.makeText(context, "Image Not Picked", Toast.LENGTH_SHORT).show()
                }else{
                    Toast.makeText(context, "All Ready", Toast.LENGTH_SHORT).show()
//                    uploadImageToFirebase(imageName, "plot")
//                    val plotData = PlotResponseXItem(imageName, name, height, widht, area, imageName, cost, location)
//                    addPlot(plotData)


                    uploadImageToFirebase(imageName, "housesale")

                    val houseData = HouseSellItem(
                        imageName,
                        name,
                        height,
                        widht,
                        area,
                        imageName,
                        cost,
                        location,
                        sbhk,
                        sbr,
                        spropDetails,
                        sAmenities
                    )

//                    Adding to HouseSell table
                    addHouseSell(houseData)

//                    Adding to My Listing table
                    val favItem = FavouriteLItem(listitemid = imageName, userid = myUserId)
                    addMyListing(favItem)
                }
            }
        }

        al_btn_rh_add.setOnClickListener {
            var sbhk:String = "";
            var sbr:String = "";
            var spropDetails:String = ""
            var sAmenities:String = "";
//            Fetching Data of Selected Chips
//            Furnishing
            var ids: List<Int> = al_rh_cg_furnishing.getCheckedChipIds()
            for (id in ids) {
                val chip: Chip = al_rh_cg_furnishing.findViewById(id)
                spropDetails += chip.text.toString() + " "
            }
//          Main Property Type
            ids = al_rh_cg_mp_type.getCheckedChipIds()
            for (id in ids) {
                val chip: Chip = al_rh_cg_mp_type.findViewById(id)
                spropDetails += chip.text.toString() + " "
            }
//            Property Type
            ids = al_rh_cg_p_type.getCheckedChipIds()
            for (id in ids) {
                val chip: Chip = al_rh_cg_p_type.findViewById(id)
                spropDetails += chip.text.toString() + " "
            }
//            Property Age
            ids = al_rh_cg_p_age.getCheckedChipIds()
            for (id in ids) {
                val chip: Chip = al_rh_cg_p_age.findViewById(id)
                spropDetails += chip.text.toString() + " "
            }
//            Entrance Facing
            ids = al_rh_cg_EntranceFacing.getCheckedChipIds()
            for (id in ids) {
                val chip: Chip = al_rh_cg_EntranceFacing.findViewById(id)
                spropDetails += chip.text.toString() + " "
            }
//            Posted By
            ids = al_rh_cg_p_by.getCheckedChipIds()
            for (id in ids) {
                val chip: Chip = al_rh_cg_p_by.findViewById(id)
                spropDetails += chip.text.toString() + " "
            }
//            Possesion
            ids = al_rh_cg_possesion.getCheckedChipIds()
            for (id in ids) {
                val chip: Chip = al_rh_cg_possesion.findViewById(id)
                spropDetails += chip.text.toString()
            }
//            Amenities
            ids = al_rh_cg_Amenities.getCheckedChipIds()
            for (id in ids) {
                val chip: Chip = al_rh_cg_Amenities.findViewById(id)
                sAmenities += chip.text.toString() + " "
            }

//            ----------

            if(al_rh_et_name.text.toString()==""
                || al_rh_et_height.text.toString()==""
                || al_rh_et_width.text.toString()==""
                || al_rh_et_area.text.toString() ==""
                || al_rh_et_height.text.toString() ==""
                || al_rh_et_location.text.toString() == ""
                || al_rh_et_bhk.text.toString() == ""
                || al_rh_et_br.text.toString() == ""
            ){
                Toast.makeText(context, "Empty Fields", Toast.LENGTH_SHORT).show()

            }else{
//                Fetching data from UI
                name = al_rh_et_name.text.toString()
                height = al_rh_et_height.text.toString()+"ft"
                widht = al_rh_et_width.text.toString()+"ft"
                area = al_rh_et_area.text.toString()+"sqft"
                cost = al_rh_et_cost.text.toString()
                location = al_rh_et_location.text.toString()
                imageName = name.lowercase().replace(" ", "")+height
                sbhk = al_rh_et_bhk.text.toString()
                sbr = al_rh_et_br.text.toString()

                Log.d("Poko", "House Sale:\n" +
                        "Name: ${name}\n" +
                        "Height: ${height}\n" +
                        "Width: ${widht}\n" +
                        "Area: $area\n" +
                        "Cost: $cost\n" +
                        "BHK: $sbhk\n" +
                        "BR: $sbr\n" +
                        "Location: $location\n" +
                        "Image Name: $imageName\n" +
                        "Property Details: ${spropDetails}\n" +
                        "Amenities: ${sAmenities}")

//              If Image is picked
                if(iv_mode_val != 3){
                    Toast.makeText(context, "Image Not Picked", Toast.LENGTH_SHORT).show()
                }else{
                    Toast.makeText(context, "All Ready", Toast.LENGTH_SHORT).show()
//                    uploadImageToFirebase(imageName, "plot")
//                    val plotData = PlotResponseXItem(imageName, name, height, widht, area, imageName, cost, location)
//                    addPlot(plotData)


                    uploadImageToFirebase(imageName, "houserent")

                    val houseData = HouseSellItem(
                        imageName,
                        name,
                        height,
                        widht,
                        area,
                        imageName,
                        cost,
                        location,
                        sbhk,
                        sbr,
                        spropDetails,
                        sAmenities
                    )

//                    Adding to House Rent table
                    addHouseRent(houseData)

//                    Adding to My Listing Table
                    val favItem = FavouriteLItem(listitemid = imageName, userid = myUserId)
                    addMyListing(favItem)
                }
            }
        }

    }

    private fun initUnitChip(rv:View) {
        unitChipGroup = rv.findViewById(R.id.al_sh_cg_unit)                 //
        al_sh_cg_furnishing = rv.findViewById(R.id.al_sh_cg_furnishing)     //
        al_sh_cg_EntranceFacing = rv.findViewById(R.id.al_sh_cg_EntranceFacing) //
        al_sh_cg_p_by = rv.findViewById(R.id.al_sh_cg_p_by)
        al_sh_cg_p_age = rv.findViewById(R.id.al_sh_cg_p_age)
        al_sh_cg_possesion = rv.findViewById(R.id.al_sh_cg_possesion)
        al_sh_cg_p_type = rv.findViewById(R.id.al_sh_cg_p_type)
        al_sh_cg_mp_type = rv.findViewById(R.id.al_sh_cg_mp_type)
        al_sh_cg_Amenities = rv.findViewById(R.id.al_sh_cg_Amenities)       //


        unitChipGroup.setOnCheckedChangeListener { group, checkedId ->
            val chip = rv.findViewById<Chip>(checkedId)

            if(chip!=null) {
                Log.d("Poko", chip.text.toString())
                if(chip.text.toString() == "Foot"){
                    units = "ft";
                }else{
                    units = "mt";
                }
            }
        }

        al_sh_cg_Amenities.setOnCheckedChangeListener { group, checkedId ->
            val chip = rv.findViewById<Chip>(checkedId)
//            amenities="1 0 0 0 0 0 0 0 0 0 0"
            var i:Int = 0;
            var am = amenities.toCharArray()
            if(chip!=null) {
                Log.d("Poko", chip.text.toString())
                if(chip.text.toString() == "Gym"){
                    i = 0;
                }else if(chip.text.toString() == "Swimming-Pool"){
                    i = 2;
                }else if(chip.text.toString() == "Gas-Pipeline"){
                    i = 4;
                }else if(chip.text.toString() == "Visitor Parking"){
                    i = 6
                }else if(chip.text.toString() == "Power Backup"){
                    i = 8
                } else if(chip.text.toString() == "Security"){
                    i = 10
                }else if(chip.text.toString() == "Garden"){
                    i = 12
                }else if(chip.text.toString() == "24 Hours Water supply"){
                    i = 14
                }else if(chip.text.toString() == "CCTV"){
                    i = 16
                }else if(chip.text.toString() == "Reserved Parking"){
                    i = 18
                } else if(chip.text.toString() == "Lift"){
                    i = 20
                }
            }

            if(am.get(i) == '0'){
                am.set(i, '1')
            }else{
                am.set(i, '0')
            }
        }

        al_sh_cg_furnishing.setOnCheckedChangeListener { group, checkedId ->
            val chip = rv.findViewById<Chip>(checkedId)

            if(chip!=null) {
                Log.d("Poko", chip.text.toString())
                if(chip.text.toString() == "Fully Furnished"){
                    funishing = "1";
                }else if (chip.text.toString() == "Semi Furnished"){
                    funishing = "2";
                }else{
                    funishing = "3";
                }
            }
        }

        al_sh_cg_EntranceFacing.setOnCheckedChangeListener { group, checkedId ->
            val chip = rv.findViewById<Chip>(checkedId)

            if(chip!=null) {
                Log.d("Poko", chip.text.toString())
                if(chip.text.toString() == "North"){
                    efacing = "1";
                }else if (chip.text.toString() == "South"){
                    efacing = "2";
                }else if (chip.text.toString() == "East"){
                    efacing = "3";
                }else if (chip.text.toString() == "West"){
                    efacing = "4";
                }else if (chip.text.toString() == "North-East"){
                    efacing = "5";
                }else if (chip.text.toString() == "North-West"){
                    efacing = "6";
                }else if (chip.text.toString() == "South-East"){
                    efacing = "7";
                }else{
                    efacing = "0"
                }
            }
        }


    }


    //  Initializes UI Elements
    private fun initUIElements(rv:View) {
        al_btn_rh_add = rv. findViewById(R.id.al_btn_rh_add)
        al_btn_sp_add = rv. findViewById(R.id.al_btn_sp_add)
        al_btn_sh_add = rv. findViewById(R.id.al_btn_sh_add)
        ib_sp = rv.findViewById(R.id.al_ib_sellPlot)
        ib_sh = rv.findViewById(R.id.al_ib_sellHouse)
        ib_rh = rv.findViewById(R.id.al_ib_rentHouse)
        iv_sp = rv.findViewById(R.id.al_iv_sellPlot)
        iv_sh = rv.findViewById(R.id.al_iv_sellHouse)
        iv_rh = rv.findViewById(R.id.al_iv_putRent)
        cl_sp = rv.findViewById(R.id.al_sp_cl)
        cl_rh = rv.findViewById(R.id.al_pr_cl)
        cl_sh = rv.findViewById(R.id.al_sh_cl)

//        Progress Bar
        al_pb = rv.findViewById(R.id.al_pb)

        ib_sp_adImg = rv.findViewById(R.id.al_sp_ib_add_img)
        ib_sh_adImg = rv.findViewById(R.id.al_sh_ib_add_img)
        ib_rh_adImg = rv.findViewById(R.id.al_rh_ib_add_img)

        initUIInputElements(rv)

    }

    private fun initUIInputElements(rv: View) {
//        Plot
        al_sp_et_name = rv.findViewById(R.id.al_sp_et_name)
        al_sp_et_height = rv.findViewById(R.id.al_sp_et_height)
        al_sp_et_width = rv.findViewById(R.id.al_sp_et_width)
        al_sp_et_area = rv.findViewById(R.id.al_sp_et_area)
        al_sp_et_cost = rv.findViewById(R.id.al_sp_et_cost)
        al_sp_et_location = rv.findViewById(R.id.al_sp_et_location)

//        House Sale
        al_sh_et_name = rv.findViewById(R.id.al_sh_et_name)
        al_sh_et_height = rv.findViewById(R.id.al_sh_et_height)
        al_sh_et_width = rv.findViewById(R.id.al_sh_et_width)
        al_sh_et_cost = rv.findViewById(R.id.al_sh_et_cost)
        al_sh_et_area = rv.findViewById(R.id.al_sh_et_area)
        al_sh_et_location = rv.findViewById(R.id.al_sh_et_location)
        al_sh_et_bhk = rv.findViewById(R.id.al_sh_et_bhk)
        al_sh_et_br = rv.findViewById(R.id.al_sh_et_br)

//        House Rent
        al_rh_et_name = rv.findViewById(R.id.al_rh_et_name)
        al_rh_et_width = rv.findViewById(R.id.al_rh_et_width)
        al_rh_et_area = rv.findViewById(R.id.al_rh_et_area)
        al_rh_et_cost = rv.findViewById(R.id.al_rh_et_cost)
        al_rh_et_location = rv.findViewById(R.id.al_rh_et_location)
        al_rh_et_height = rv.findViewById(R.id.al_rh_et_height)
        al_rh_et_br = rv.findViewById(R.id.al_rh_et_br)
        al_rh_et_bhk = rv.findViewById(R.id.al_rh_et_bhk)
        al_rh_cg_mp_type = rv.findViewById(R.id.al_rh_cg_mp_type)
        al_rh_cg_p_type = rv.findViewById(R.id.al_rh_cg_p_type)
        al_rh_cg_possesion = rv.findViewById(R.id.al_rh_cg_possesion)
        al_rh_cg_p_age = rv.findViewById(R.id.al_rh_cg_p_age)
        al_rh_cg_EntranceFacing = rv.findViewById(R.id.al_rh_cg_EntranceFacing)
        al_rh_cg_p_by = rv.findViewById(R.id.al_rh_cg_p_by)
        al_rh_cg_Amenities = rv.findViewById(R.id.al_rh_cg_Amenities)
        al_rh_cg_furnishing = rv.findViewById(R.id.al_rh_cg_furnishing)


    }


    //  Handles option opened from Home Frag
    private fun handleOpenFromHomeFrag() {
        if(SelectedOption.sOption == 1){
            ib_sp.visibility = View.GONE
            cl_sp.visibility = View.VISIBLE
            ib_rh.visibility = View.VISIBLE
            cl_rh.visibility = View.GONE
            ib_sh.visibility = View.VISIBLE
            cl_sh.visibility = View.GONE
        }else if(SelectedOption.sOption == 2){
            ib_sp.visibility = View.VISIBLE
            cl_sp.visibility = View.GONE
            ib_rh.visibility = View.VISIBLE
            cl_rh.visibility = View.GONE
            ib_sh.visibility = View.GONE
            cl_sh.visibility = View.VISIBLE
        }else if(SelectedOption.sOption == 3){
            ib_sp.visibility = View.VISIBLE
            cl_sp.visibility = View.GONE
            ib_rh.visibility = View.GONE
            cl_rh.visibility = View.VISIBLE
            ib_sh.visibility = View.VISIBLE
            cl_sh.visibility = View.GONE
        }
        SelectedOption.sOption = 0
    }

    // Handles Opening and closing of tabs
    private fun openTabs(){
        ib_sp.setOnClickListener {
            ib_sp.visibility = View.GONE
            cl_sp.visibility = View.VISIBLE
            ib_rh.visibility = View.VISIBLE
            cl_rh.visibility = View.GONE
            ib_sh.visibility = View.VISIBLE
            cl_sh.visibility = View.GONE
        }
        ib_sh.setOnClickListener {
            ib_sp.visibility = View.VISIBLE
            cl_sp.visibility = View.GONE
            ib_rh.visibility = View.VISIBLE
            cl_rh.visibility = View.GONE
            ib_sh.visibility = View.GONE
            cl_sh.visibility = View.VISIBLE
        }
        ib_rh.setOnClickListener {
            ib_sp.visibility = View.VISIBLE
            cl_sp.visibility = View.GONE
            ib_rh.visibility = View.GONE
            cl_rh.visibility = View.VISIBLE
            ib_sh.visibility = View.VISIBLE
            cl_sh.visibility = View.GONE
        }

        iv_sp.setOnClickListener {
            ib_sp.visibility = View.VISIBLE
            cl_sp.visibility = View.GONE
            ib_rh.visibility = View.VISIBLE
            cl_rh.visibility = View.GONE
            ib_sh.visibility = View.VISIBLE
            cl_sh.visibility = View.GONE
        }
        iv_sh.setOnClickListener {
            ib_sp.visibility = View.VISIBLE
            cl_sp.visibility = View.GONE
            ib_rh.visibility = View.VISIBLE
            cl_rh.visibility = View.GONE
            ib_sh.visibility = View.VISIBLE
            cl_sh.visibility = View.GONE
        }
        iv_rh.setOnClickListener {
            ib_sp.visibility = View.VISIBLE
            cl_sp.visibility = View.GONE
            ib_rh.visibility = View.VISIBLE
            cl_rh.visibility = View.GONE
            ib_sh.visibility = View.VISIBLE
            cl_sh.visibility = View.GONE
        }
    }

    //  Handles Loading Image
    private fun uploadImage(image: ImageView) {
        val intent = Intent()
        intent.action = Intent.ACTION_GET_CONTENT
        intent.type = "image/*"
        startActivityForResult(intent, 1)
    }

    //  Handles Image Assignment on view
    private fun addImageFun() {
        ib_sp_adImg.setOnClickListener{ uploadImage(ib_sp_adImg); iv_mode_val = 1 }
        ib_rh_adImg.setOnClickListener { uploadImage(ib_rh_adImg); iv_mode_val = 3 }
        ib_sh_adImg.setOnClickListener{ uploadImage(ib_sh_adImg); iv_mode_val = 2 }
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if(requestCode == 1){
            if(iv_mode_val == 1){
                imageUri = data?.data!!

                ib_sp_adImg.setImageURI(data?.data)
                Glide.with(this.requireContext())
                    .load(data?.data)
                    .into(ib_sp_adImg)
            }else if(iv_mode_val == 2){
                imageUri = data?.data!!

                ib_sh_adImg.setImageURI(data?.data)
                Glide.with(this.requireContext())
                    .load(data?.data)
                    .into(ib_sh_adImg)
            }else if(iv_mode_val == 3){
                imageUri = data?.data!!

                ib_rh_adImg.setImageURI(data?.data)
                Glide.with(this.requireContext())
                    .load(data?.data)
                    .into(ib_rh_adImg)
            }
        }
    }


//    API Functions
    private fun addPlot(plotData: PlotResponseXItem) {
        val call = RetrofitServicexx.usersInstace.postPlot(plotData)
        call.enqueue(object : Callback<GeneralMessage> {
            override fun onResponse(
                call: Call<GeneralMessage>,
                response: Response<GeneralMessage>
            ) {
                if (response.isSuccessful) {
                    val resBody = response.body()
                    if (resBody != null) {
                        Log.d("Poko", resBody.toString())
                    } else {
                        Log.d("Poko", "Unable to add Plot for sale")
                    }
                } else {
                    Log.d(
                        "Poko",
                        "Response Code: " + response.code().toString() + "\nError Fetching Response"
                    )
                }
                //            Making Progress bar gone
                al_pb.visibility = View.GONE
            }

            override fun onFailure(call: Call<GeneralMessage>, t: Throwable) {
                Log.d("Poko", "Error in fetching response", t)
                //            Making Progress bar gone
                al_pb.visibility = View.GONE
            }
        })
    }

    private fun addHouseSell(houseData: HouseSellItem) {
        val call = RetrofitServicexx.usersInstace.postHouseSell(houseData)
        call.enqueue(object : Callback<GeneralMessage> {
            override fun onResponse(
                call: Call<GeneralMessage>,
                response: Response<GeneralMessage>
            ) {
                if (response.isSuccessful) {
                    val resBody = response.body()
                    if (resBody != null) {
                        Log.d("Poko", resBody.toString())
                    } else {
                        Log.d("Poko", "Unable to add House for sale")
                    }
                } else {
                    Log.d(
                        "Poko",
                        "Response Code: " + response.code().toString() + "\nError Fetching Response"
                    )
                }
            }

            override fun onFailure(call: Call<GeneralMessage>, t: Throwable) {
                Log.d("Poko", "Error in fetching response", t)
            }
        })
    }

    private fun addHouseRent(houseData: HouseSellItem) {
        val call = RetrofitServicexx.usersInstace.postHouseRent(houseData)
        call.enqueue(object : Callback<GeneralMessage> {
            override fun onResponse(
                call: Call<GeneralMessage>,
                response: Response<GeneralMessage>
            ) {
                if (response.isSuccessful) {
                    val resBody = response.body()
                    if (resBody != null) {
                        Log.d("Poko", resBody.toString())
                    } else {
                        Log.d("Poko", "Unable to add House for Rent")
                    }
                } else {
                    Log.d(
                        "Poko",
                        "Response Code: " + response.code().toString() + "\nError Fetching Response"
                    )
                }
            }

            override fun onFailure(call: Call<GeneralMessage>, t: Throwable) {
                Log.d("Poko", "Error in fetching response", t)
            }
        })
    }

    private fun uploadImageToFirebase(name: String, urlLocation:String) {
        storgeRefrence = FirebaseStorage.getInstance().reference

        val formatter = SimpleDateFormat("yyyy_MM__dd_HH__mm__ss", Locale.CHINA)
        val date = Date()
        val fileName = formatter.format(date)
        storgeRefrence = FirebaseStorage.getInstance().getReference("$urlLocation/$name.jpg")


        storgeRefrence.putFile(imageUri)
            .addOnSuccessListener {
                Toast.makeText(
                    context,
                    "Image Uploaded Successfully",
                    Toast.LENGTH_SHORT
                ).show()
            }
            .addOnFailureListener {
                Toast.makeText(context, "Image Upload Failed", Toast.LENGTH_SHORT)
                    .show()
            }
    }

//  Add To My listing
    private fun addMyListing(favData: FavouriteLItem) {
        val call = RetrofitServicexx.usersInstace.postMyListing(favData)
        call.enqueue(object : Callback<FavouriteLItem> {
            override fun onResponse(
                call: Call<FavouriteLItem>,
                response: Response<FavouriteLItem>
            ) {
                if (response.isSuccessful) {
                    Log.d("Poko", response.body().toString())
                } else if (response.code() == 400) Log.d("Poko", "Listing Already Exist")
            }

            override fun onFailure(call: Call<FavouriteLItem>, t: Throwable) {
                Log.d("Poko", "Error in fetching response", t)
            }
        })
    }
}