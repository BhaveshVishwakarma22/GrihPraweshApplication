package com.example.grihprawesh.apix

import com.example.grihprawesh.modelxx.FavListingResponseItem
import com.example.grihprawesh.modelxx.FavouriteLItem
import com.example.grihprawesh.modelxx.GeneralMessage
import com.example.grihprawesh.modelxx.HouseItemId
import com.example.grihprawesh.modelxx.HouseSellItem
import com.example.grihprawesh.modelxx.PlotIdReq
import com.example.grihprawesh.modelxx.PlotResponseXItem
import com.example.grihprawesh.modelxx.RequestUserIDFav
import com.example.grihprawesh.modelxx.ResponseDC
import com.example.grihprawesh.modelxx.ResponseDCItem
import com.example.grihprawesh.modelxx.UserEmailPost
import com.example.grihprawesh.modelxx.UserPostReq
import com.example.grihprawesh.utilx.Constants.Companion.BASE_URL
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.HTTP
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

interface RetrofitInterfacesXX {

    //    User call Functions
    @GET("api/user/")
    fun getUsers():Call<List<ResponseDCItem>>

    @POST("api/user/")
    fun postUser(@Body post:UserPostReq):Call<UserPostReq>

    @PUT("api/user/{id}")
    fun updateUserById(@Path("id") d:Int, @Body post:UserPostReq):Call<GeneralMessage>

    @PUT("api/user/email/{id}")
    fun updateUserEmail(@Path("id") d:Int, @Body post:UserPostReq):Call<GeneralMessage>

    @PATCH("api/user/{id}")
    fun getItemFromEmail(@Path("id") d:Int, @Body post:UserEmailPost):Call<List<ResponseDCItem>>

    //    @DELETE("api/user/{id}")
    @HTTP(method = "DELETE", path = "api/user/{id}", hasBody = true)
    fun deleteUser(@Path("id") d:Int, @Body post:UserEmailPost):Call<GeneralMessage>


    //    Favourites Table Call Functions
    @GET("api/fav")
    fun getAllFavorites():Call<ArrayList<FavouriteLItem>>

    @HTTP(method = "PATCH", path = "api/fav/{id}", hasBody = true)
    fun getFavoritesById(@Path("id") d:Int, @Body post:RequestUserIDFav):Call<ArrayList<FavouriteLItem>>

    @POST("api/fav/")
    fun postFavourite(@Body post:FavouriteLItem):Call<FavouriteLItem>

    @HTTP(method = "DELETE", path = "api/fav/all", hasBody = true)
    fun deleteAllFavByUserId(@Body post:RequestUserIDFav):Call<GeneralMessage>

    @HTTP(method = "DELETE", path = "api/fav/", hasBody = true)
    fun deleteFavByUserIdAndLid(@Body post:FavouriteLItem):Call<GeneralMessage>


    //    User Listing Table Call Functions
    @GET("api/listing")
    fun getAllMyListing():Call<ArrayList<FavouriteLItem>>

    @HTTP(method = "PATCH", path = "api/listing/{id}", hasBody = true)
    fun getMyListingById(@Path("id") d:Int, @Body post:RequestUserIDFav):Call<ArrayList<FavouriteLItem>>

    @POST("api/listing/")
    fun postMyListing(@Body post:FavouriteLItem):Call<FavouriteLItem>

    @HTTP(method = "DELETE", path = "api/listing/all", hasBody = true)
    fun deleteAllMyListingByUserId(@Body post:RequestUserIDFav):Call<GeneralMessage>

    @HTTP(method = "DELETE", path = "api/listing/", hasBody = true)
    fun deleteMyListingByUserIdAndLid(@Body post:FavouriteLItem):Call<GeneralMessage>


    //    Plot Table Call Function
    @GET("api/plot")
    suspend fun getAllPlot():Response<ArrayList<PlotResponseXItem>>

    @PATCH("api/plot/{id}")
    suspend fun getPlotById(@Path("id") d:Int, @Body post:PlotIdReq):Response<ArrayList<PlotResponseXItem>>

    @POST("api/plot")
    fun postPlot(@Body post:PlotResponseXItem):Call<GeneralMessage>

    @PUT("api/plot")
    fun updatePlot(@Body post:PlotResponseXItem):Call<GeneralMessage>

    @HTTP(method = "DELETE", path = "api/plot/", hasBody = true)
    fun deletePlotById(@Body post:PlotIdReq):Call<GeneralMessage>


    //    House Sell Call Function
    @GET("api/house_sell")
    suspend fun getAllHouseSell():Response<ArrayList<HouseSellItem>>

    @PATCH("api/house_sell/{id}")
    suspend fun getHouseSellById(@Path("id") d:Int, @Body post:HouseItemId):Response<ArrayList<HouseSellItem>>

    @POST("api/house_sell")
    fun postHouseSell(@Body post:HouseSellItem):Call<GeneralMessage>

    @PUT("api/house_sell")
    fun updateHouseSell(@Body post:HouseSellItem):Call<GeneralMessage>

    @HTTP(method = "DELETE", path = "api/house_sell/", hasBody = true)
    fun deleteHouseSellById(@Body post:HouseItemId):Call<GeneralMessage>


    //    House Rent Call Function
    @GET("api/house_rent")
    suspend fun getAllHouseRent():Response<ArrayList<HouseSellItem>>

    @PATCH("api/house_rent/{id}")
    suspend fun getHouseRentById(@Path("id") d:Int, @Body post:HouseItemId):Response<ArrayList<HouseSellItem>>

    @POST("api/house_rent")
    fun postHouseRent(@Body post:HouseSellItem):Call<GeneralMessage>

    @PUT("api/house_rent")
    fun updateHouseRent(@Body post:HouseSellItem):Call<GeneralMessage>

    @HTTP(method = "DELETE", path = "api/house_rent/", hasBody = true)
    fun deleteHouseRentById(@Body post:HouseItemId):Call<GeneralMessage>

    //    Favourites
    @PATCH("api/fav/item/x")
    suspend fun getFavouritesByUserId(@Body post:RequestUserIDFav):Response<ArrayList<FavListingResponseItem>>

    //    User Listing
    @PATCH("api/listing/item/x")
    suspend fun getUserListingByUserId(@Body post:RequestUserIDFav):Response<ArrayList<FavListingResponseItem>>


    @GET("api/house_sell/item/{location}")
    suspend fun getPropertiesByLocation(@Path("location") d:String):Response<ArrayList<FavListingResponseItem>>

}

object RetrofitServicexx{
    val usersInstace:RetrofitInterfacesXX
    init{
        val retrofit = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        usersInstace = retrofit.create(RetrofitInterfacesXX::class.java)
    }
}