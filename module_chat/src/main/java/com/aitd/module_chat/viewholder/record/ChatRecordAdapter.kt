package com.aitd.module_chat.viewholder.record

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.aitd.module_chat.Message
import com.aitd.module_chat.R
import com.aitd.module_chat.viewholder.RecordViewHolder

class ChatRecordAdapter(private val mDataSet: List<Message>) : RecyclerView.Adapter<RecordViewHolder>() {

    var mOnItemClickListener : OnItemClickListener? = null

    override fun onCreateViewHolder(parent: ViewGroup, position: Int): RecordViewHolder {
        var view = LayoutInflater.from(parent.context).inflate(R.layout.imui_item_message_record, parent, false)
        return RecordViewHolder(view)
    }

    override fun onBindViewHolder(holder: RecordViewHolder, position: Int) {
        var last = mDataSet[position]
        if (position > 0) {
            last = mDataSet[position - 1]
        }
        var data = mDataSet[position]
        holder.fill(data, last, position, mOnItemClickListener)
    }

    override fun getItemCount() = mDataSet.size

    fun setItemClickListener(listener : OnItemClickListener) {
        mOnItemClickListener = listener
    }

    interface OnItemClickListener {
        fun onClick(position: Int, v: View)
    }
}