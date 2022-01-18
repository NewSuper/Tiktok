package com.aitd.module_chat.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.aitd.module_chat.R
import com.aitd.module_chat.pojo.Account
import kotlinx.android.synthetic.main.item_userlist.view.*

class AccountAdapter(private val myDataSet: ArrayList<Account>) :
    RecyclerView.Adapter<AccountAdapter.MyViewHolder>() {

    class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.item_userlist, parent, false)
        return MyViewHolder(view)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.itemView.tvUserName.text = myDataSet[position].name
        holder.itemView.tvUserId.text = myDataSet[position].userId
        holder.itemView.setOnClickListener {
            mOnItemClickListener?.onClick(position)
        }
    }

    override fun getItemCount(): Int = myDataSet.size

    lateinit var mOnItemClickListener: OnItemClickListener

    interface OnItemClickListener {
        fun onClick(position: Int)
    }

    fun setOnItemClickListener(listener: OnItemClickListener) {
        mOnItemClickListener = listener
    }
}