package com.example.grihprawesh.modelxx


import android.net.Uri
import com.google.gson.annotations.SerializedName

// Recycler View
data class ListItem(
    var prop_image: String,
    val prop_name: String,
    val prop_details: String,
    val prop_mode: String,
    val prop_location: String
)


//Response

data class FavListingResponseItem(

    @field:SerializedName("prop_name")
    val propName: String? = null,

    @field:SerializedName("prop_image")
    val propImage: String? = null,

    @field:SerializedName("prop_details")
    val propDetails: String? = null,

    @field:SerializedName("prop_mode")
    val propMode: String? = null,

    @field:SerializedName("prop_location")
    val propLocation: String? = null
)


data class ResponseDC(

    @field:SerializedName("ResponseDC")
    val responseDC: List<ResponseDCItem?>? = null
)

data class ResponseDCItem(

    @field:SerializedName("fname")
    val fname: String? = null,

    @field:SerializedName("createdAt")
    val createdAt: String? = null,

    @field:SerializedName("lname")
    val lname: String? = null,

    @field:SerializedName("userPhoto")
    val userPhoto: String? = null,

    @field:SerializedName("pass")
    val pass: String? = null,

    @field:SerializedName("phone")
    val phone: String? = null,

    @field:SerializedName("id")
    val id: Int? = null,

    @field:SerializedName("email")
    val email: String? = null,

    @field:SerializedName("updatedAt")
    val updatedAt: String? = null
)

data class GeneralMessage(
    @field:SerializedName("message")
    val message: String? = null,
)

data class FavouriteLItem(

    @field:SerializedName("listitemid")
    val listitemid: String? = null,

    @field:SerializedName("userid")
    val userid: String? = null,
)

data class PlotResponseXItem(
    @field:SerializedName("id")
    val id: String? = null,

    @field:SerializedName("name")
    val name: String? = null,

    @field:SerializedName("height")
    val height: String? = null,

    @field:SerializedName("width")
    val width: String? = null,

    @field:SerializedName("area")
    val area: String? = null,

    @field:SerializedName("image")
    val image: String? = null,

    @field:SerializedName("cost")
    val cost: String? = null,

    @field:SerializedName("location")
    val location: String? = null,
)

data class HouseSellItem(
    @field:SerializedName("list_item_id")
    val listItemId: String? = null,

    @field:SerializedName("name")
    val name: String? = null,

    @field:SerializedName("height")
    val height: String? = null,

    @field:SerializedName("width")
    val width: String? = null,

    @field:SerializedName("area")
    val area: String? = null,

    @field:SerializedName("image")
    val image: String? = null,

    @field:SerializedName("cost")
    val cost: String? = null,

    @field:SerializedName("location")
    val location: String? = null,

    @field:SerializedName("bhk")
    val bhk: String? = null,

    @field:SerializedName("br")
    val br: String? = null,

    @field:SerializedName("prop_details")
    val propDetails: String? = null,

    @field:SerializedName("amenities")
    val amenities: String? = null
)

data class HouseItemId(
    @field:SerializedName("list_item_id")
    val listItemId: String? = null,
)


//Request
data class UserEmailPost(
    @field:SerializedName("email")
    val email: String,
)

data class UserPostReq(

    @field:SerializedName("fname")
    val fname: String? = null,

    @field:SerializedName("lname")
    val lname: String? = null,

    @field:SerializedName("email")
    val email: String? = null,

    @field:SerializedName("pass")
    val pass: String? = null,

    @field:SerializedName("phone")
    val phone: String? = null,

    @field:SerializedName("userPhoto")
    val userPhoto: String? = null,
)

data class RequestUserIDFav(

    @field:SerializedName("userid")
    val userid: String? = null,
)

data class PlotIdReq(
    @field:SerializedName("id")
    val id: String? = null,
)