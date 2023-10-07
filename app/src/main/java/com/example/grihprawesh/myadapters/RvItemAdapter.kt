package com.example.grihprawesh.myadapters

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.grihprawesh.PropertyDetailView
import com.example.grihprawesh.R
import com.example.grihprawesh.modelxx.HouseSellItem
import com.example.grihprawesh.modelxx.ListItem
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.squareup.picasso.Picasso
import kotlin.random.Random

class RvItemAdapter(private val rv_list_item:ArrayList<ListItem>, private val con: Context?, private val activityToGo: Class<out Activity> = PropertyDetailView::class.java):
    RecyclerView.Adapter<RvItemAdapter.RvItemViewHolder>() {

    lateinit var mcontext:Context

    class RvItemViewHolder(itemView: View):RecyclerView.ViewHolder(itemView) {
        val tv_prop_own_name:TextView = itemView.findViewById(R.id.tv_prop_own_name)
        val tv_prop_details:TextView = itemView.findViewById(R.id.tv_prop_details)
        val tv_prop_mode:TextView = itemView.findViewById(R.id.tv_prop_mode)
        val tv_prop_loc:TextView = itemView.findViewById(R.id.tv_prop_loc)
        val iv_prop_image:ImageView = itemView.findViewById(R.id.iv_prop_image)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RvItemViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.rv_item, parent, false)
        mcontext = parent.context

        return RvItemViewHolder(view)

    }

    override fun onBindViewHolder(holder: RvItemViewHolder, position: Int) {

        val item = rv_list_item[position]
        if(item.prop_image.length>80){
            Glide.with(mcontext)
                .load(item.prop_image)
                .into(holder.iv_prop_image)
        }else{
            val imUri = getUri(R.drawable.item_img_def)
            Glide.with(mcontext)
                .load(imUri)
                .into(holder.iv_prop_image)
        }



        holder.tv_prop_own_name.text = item.prop_name
        val id = item.prop_details.split("|")
        holder.tv_prop_details.text = id[1]
        holder.tv_prop_loc.text = item.prop_location
        holder.tv_prop_mode.text = item.prop_mode
        holder.iv_prop_image.setOnClickListener {
//            val i = Intent(con, activityToGo)

            val i = Intent(con, PropertyDetailView::class.java)

            i.putExtra("prop_id", id[0])
            i.putExtra("prop_name", item.prop_name)
            i.putExtra("prop_mode", item.prop_mode)
            i.putExtra("prop_image", item.prop_image)
            i.putExtra("to_go_activity", activityToGo.toString())
//            Log.d("Poko2", "Activity_to_go: " + activityToGo.toString())

//            Log.d("Poko", id[0])
            con?.startActivity(i)
        }

    }

    override fun getItemCount(): Int {
        return rv_list_item.size
    }

    private fun getUri(res:Int): Uri {
        return Uri.parse("android.resource://com.example.grihprawesh/$res")
    }

}
