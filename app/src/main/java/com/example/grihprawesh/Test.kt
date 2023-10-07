package com.example.grihprawesh

import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.grihprawesh.modelxx.ListItem
import com.example.grihprawesh.myadapters.RvItemAdapter
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup
import okhttp3.MediaType
import okhttp3.Request
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory


class Test : AppCompatActivity() {

    lateinit var recyclerViewGrid: RecyclerView

    lateinit var rv_item_list:ArrayList<ListItem>
    lateinit var rv_item_adapter:RvItemAdapter

    lateinit var recyclerViewLinear: RecyclerView
    lateinit var rv_item_list_lin:ArrayList<ListItem>
    lateinit var rv_item_adapter_lin:RvItemAdapter

    lateinit var chip_group:ChipGroup
    lateinit var prev_selectedChip:Chip

    lateinit var tv_test_retrofit:TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_test)

        initUIElements()


//        User{

//        Get All Users
//        getUsersData(retrofitBuilder)

//        Get Single User
//        val dEmail = "BV200222@gmail.co"
//        getSingleUser(retrofitBuilder, dEmail)

//        Post Create User
//        val userBody = UserPostBody("Rishabh", "Singh", "1234.jpg", "1234", "5123456780", "rSingh@gmail.com")
//        createUser(retrofitBuilder, userBody)

//        Put Update User
//        val userBody = UserPostBody("Rishabh", "Singh", "1234.jpg", "1234", "1112223334", "rSingh@gmail.com")
//        updateUser(retrofitBuilder, userBody)

//        Delete user
//        val dEmail = "rSingh@gmail.com"
//        deleteUser(retrofitBuilder, dEmail)

//        }

//        Plot{

//        Get All Plot
//        getAllPlot(retrofitBuilder)

//        Get Plot by ID
//        val id = "bv2002222@gmail.com_1"
//        getPlotById(retrofitBuilder, id)

//        Add Plot
//        val plotModelItem = PlotInputModel2("king", "500sqft", "100ft", "200ft", "Bhavesh's Plot", "500000", "yo.jpg", "yo")
//        addPlotMethod(retrofitBuilder, plotModelItem)

//        Update Plot
//        updatePlot
//        val plotModelItem = PlotInputModel("1", "500sqft", "100ft", "200ft", "Poko's Plot", "500000", "yo.jpg", "yo")
//        modifyPlot(retrofitBuilder, plotModelItem)
    }

    /*
    private fun modifyPlot(retrofitBuilder: UserxApi, plotModelItem: PlotInputModel) {
        val retrofitDataBody = retrofitBuilder.updatePlot(plotModelItem)
        retrofitDataBody.enqueue(object  : Callback<MessageBodyModel>{
            override fun onResponse(
                call: Call<MessageBodyModel>,
                response: Response<MessageBodyModel>
            ) {
                Toast.makeText(applicationContext, "Updated",Toast.LENGTH_SHORT).show()
            }

            override fun onFailure(call: Call<MessageBodyModel>, t: Throwable) {
                Toast.makeText(applicationContext,"Failed to update!",Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun addPlotMethod(retrofitBuilder: UserxApi, plotModelItem: PlotInputModel2) {
        val retrofitData = retrofitBuilder.addPlot(plotModelItem)

        retrofitData.enqueue(object : Callback<MessageBodyModel>{
            override fun onResponse(
                call: Call<MessageBodyModel>,
                response: Response<MessageBodyModel>
            ) {
                Toast.makeText(applicationContext,"UserCreated",Toast.LENGTH_SHORT).show()

            }
            override fun onFailure(call: Call<MessageBodyModel>, t: Throwable) {
                Toast.makeText(applicationContext,"Failed",Toast.LENGTH_SHORT).show()

            }
        })
    }

    private fun getPlotById(retrofitBuilder: UserxApi, id: String) {
        val pid = ListItemIdRequestModel(id)
        val retrofitSingleData = retrofitBuilder.getPlotbyId(pid)

        retrofitSingleData.enqueue(object : Callback<List<PlotModelItem>>{
            override fun onResponse(
                call: Call<List<PlotModelItem>>,
                response: Response<List<PlotModelItem>>
            ) {
                val responseBody = response.body()!!
                Log.d("Poko", responseBody.toString())
                val myString = StringBuilder()

                myString.append(responseBody.toString())

                tv_test_retrofit.text = myString;
            }

            override fun onFailure(call: Call<List<PlotModelItem>>, t: Throwable) {
                Log.d("Poko","Error")
            }
        })
    }

    private fun getAllPlot(retrofitBuilder: UserxApi) {

        val retrofitData = retrofitBuilder.getAllPlots()
        retrofitData.enqueue(object : Callback<List<PlotModelItem>?>{
            override fun onResponse(
                call: Call<List<PlotModelItem>?>,
                response: Response<List<PlotModelItem>?>
            ) {
                val responseBody = response.body()!!

                val myString = StringBuilder()

                for (userData in responseBody){
                    myString.append("Plot Area: ")
                    myString.append(userData.area)
                    myString.append("\n")
                    myString.append("Plot Name: ")
                    myString.append(userData.name)
                    myString.append("\n")
                    myString.append("Plot Height: ")
                    myString.append(userData.height)
                    myString.append("\n")
                    myString.append("Response Body: ")
                    myString.append(userData)
                    myString.append("\n")
                }

                tv_test_retrofit.text = myString;
            }

            override fun onFailure(call: Call<List<PlotModelItem>?>, t: Throwable) {
                TODO("Not yet implemented")
            }
        })
    }

    private fun deleteUser(retrofitBuilder: UserxApi, dEmail: String) {
        val deleteBody = EmailRequestBody(dEmail)
        val retrofitDataBody = retrofitBuilder.deleteUser(deleteBody)
        retrofitDataBody.enqueue(object  : Callback<MessageBodyModel>{
            override fun onResponse(
                call: Call<MessageBodyModel>,
                response: Response<MessageBodyModel>
            ) {
                Toast.makeText(applicationContext, "User Deleted",Toast.LENGTH_SHORT).show()
            }

            override fun onFailure(call: Call<MessageBodyModel>, t: Throwable) {
                Toast.makeText(applicationContext,"Failed to Delete user!",Toast.LENGTH_SHORT).show()
            }

        })
    }

    private fun updateUser(retrofitBuilder: UserxApi, userBody: UserPostBody) {
        val retrofitDataBody = retrofitBuilder.putUser(userBody)
        retrofitDataBody.enqueue(object  : Callback<MessageBodyModel>{
            override fun onResponse(
                call: Call<MessageBodyModel>,
                response: Response<MessageBodyModel>
            ) {
                Toast.makeText(applicationContext, "User Updated",Toast.LENGTH_SHORT).show()
            }
            override fun onFailure(call: Call<MessageBodyModel>, t: Throwable) {
                Toast.makeText(applicationContext,"Failed to update user!",Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun createUser(retrofitBuilder: UserxApi, userBody:UserPostBody) {
        val retrofitDataBody = retrofitBuilder.postUser(userBody)
        retrofitDataBody.enqueue(object : Callback<UserPostBody>{
            override fun onResponse(call: Call<UserPostBody>, response: Response<UserPostBody>) {
                Toast.makeText(applicationContext,"UserCreated",Toast.LENGTH_SHORT).show()

            }

            override fun onFailure(call: Call<UserPostBody>, t: Throwable) {
                Toast.makeText(applicationContext,"Failed to create user!",Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun getSingleUser(retrofitBuilder: UserxApi, dEmail:String) {
        val getSingleUserDataBody = EmailRequestBody(dEmail)
        val retrofitSingleData = retrofitBuilder.getSingleUsers(getSingleUserDataBody)

        retrofitSingleData.enqueue(object : Callback<List<UsersModelItem>>{
            override fun onResponse(
                call: Call<List<UsersModelItem>>,
                response: Response<List<UsersModelItem>>
            ) {
                val responseBody = response.body()!!
                Log.d("Poko", responseBody.toString())
                val myString = StringBuilder()

                for (userData in responseBody){
                    myString.append("User ID: ")
                    myString.append(userData.id)
                    myString.append("\n")
                    myString.append("User Name: ")
                    myString.append(userData.fname + " " + userData.lname)
                    myString.append("\n")
                    myString.append("User Email: ")
                    myString.append(userData.email)
                    myString.append("\n")
                    myString.append("User Pass: ")
                    myString.append(userData.pass)
                    myString.append("\n")
                    myString.append("User Phone: ")
                    myString.append(userData.phone)
                    myString.append("\n")
                }

                tv_test_retrofit.text = myString;
            }

            override fun onFailure(call: Call<List<UsersModelItem>>, t: Throwable) {
                Log.d("Poko","Error")
            }
        })
    }

    private fun getUsersData(retrofitBuilder: UserxApi) {
        val retrofitData = retrofitBuilder.getUsers()
        retrofitData.enqueue(object : Callback<List<UsersModelItem>?>{
            override fun onResponse(
                call: Call<List<UsersModelItem>?>,
                response: Response<List<UsersModelItem>?>
            ) {
                val responseBody = response.body()!!

                val myString = StringBuilder()

                for (userData in responseBody){
                    myString.append("User ID: ")
                    myString.append(userData.id)
                    myString.append("\n")
                    myString.append("User Name: ")
                    myString.append(userData.fname + " " + userData.lname)
                    myString.append("\n")
                    myString.append("User Email: ")
                    myString.append(userData.email)
                    myString.append("\n")
                    myString.append("User Pass: ")
                    myString.append(userData.pass)
                    myString.append("\n")
                    myString.append("User Phone: ")
                    myString.append(userData.phone)
                    myString.append("\n")
                }

                tv_test_retrofit.text = myString;
            }

            override fun onFailure(call: Call<List<UsersModelItem>?>, t: Throwable) {
                TODO("Not yet implemented")
            }
        })
    }

     */

    private fun initUIElements() {
        tv_test_retrofit = findViewById(R.id.tv_test_retrofit)
        initRvItemGrid()
        initRvItemLin()

        initChip()
    }

    private fun initChip() {
        chip_group = findViewById(R.id.chip_group)

        chip_group.setOnCheckedChangeListener { group, checkedId ->
            val chip = findViewById<Chip>(checkedId)

            if(chip!=null) {
                Log.d("Poko", chip.text.toString())

            }
        }
    }

    private fun initRvItemLin() {
        recyclerViewLinear = findViewById(R.id.rv_test_linear)
        recyclerViewLinear.setHasFixedSize(true)
        recyclerViewLinear.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        rv_item_list_lin = ArrayList()

        for(i in 0 until 40){
            rv_item_list_lin.add(ListItem(R.drawable.item_img_def.toString(),"Bhavesh's House$i", "bv200222_1|₹45,500,100\n2BHK", "buy", "New Rajendra Nagar"))
        }

        rv_item_adapter_lin = RvItemAdapter(rv_item_list_lin, applicationContext)
        recyclerViewLinear.adapter = rv_item_adapter_lin

    }



    private fun initRvItemGrid() {
        recyclerViewGrid = findViewById(R.id.rv_test_grid)
        recyclerViewGrid.setHasFixedSize(true)
        recyclerViewGrid.layoutManager = GridLayoutManager(this, 2, GridLayoutManager.VERTICAL, false)
        rv_item_list = ArrayList()

        for(i in 0 until 40){
            rv_item_list.add(ListItem(R.drawable.item_img_def.toString(),"Bhavesh's House$i", "bv200222_1|₹45,500,100\n2BHK", "buy", "New Rajendra Nagar"))
        }

        rv_item_adapter = RvItemAdapter(rv_item_list, applicationContext)
        recyclerViewGrid.adapter = rv_item_adapter

    }

    private fun getUri(res:Int): Uri {
        return Uri.parse("android.resource://com.example.grihprawesh/$res")
    }


}