package com.aitd.module_chat.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.aitd.module_chat.R
import com.aitd.module_chat.pojo.Account
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestOptions
import kotlinx.android.synthetic.main.imui_item_friend.view.*

class FriendAdapter(private val myDataSet: List<Account>) : RecyclerView.Adapter<FriendAdapter.MyViewHolder>() {

    lateinit var mOnItemClickListener: OnItemClickListener

    class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.imui_item_friend, parent, false)
        return MyViewHolder(view)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.itemView.tv_title_bar_name.text = myDataSet[position].name
        holder.itemView.tv_friend_id.text = myDataSet[position].userId
        Glide.with(holder.itemView.context).load(myDataSet[position].icon)
            .apply(
                RequestOptions.bitmapTransform(
                RoundedCorners(
                    10
                )
            )).into(holder.itemView.iv_text_avatar)

        holder.itemView.setOnClickListener {
            mOnItemClickListener?.onClick(position)
        }
    }

    override fun getItemCount() = myDataSet.size

    fun setOnItemClickListener(listener: OnItemClickListener) {
        mOnItemClickListener = listener
    }

    interface OnItemClickListener {
        fun onClick(position: Int)
    }
}