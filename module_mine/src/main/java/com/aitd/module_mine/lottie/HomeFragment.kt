package com.aitd.module_mine.lottie

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.aitd.library_common.base.BaseFragment
import com.aitd.module_mine.R
import com.aitd.module_mine.adapter.BaseExpandableRecyclerViewAdapter
import kotlinx.android.synthetic.main.fragment_home.*
import java.util.*

class HomeFragment : BaseFragment() {

    private var mAdapter: MainExpandableAdapter? = null
    override fun init(saveInstanceState: Bundle?) {

        val groupBeans = initGroupData()
        rvList.layoutManager = LinearLayoutManager(mActivity)
        mAdapter = MainExpandableAdapter(groupBeans!!)

        mAdapter!!.setListener(object :
            BaseExpandableRecyclerViewAdapter.ExpandableRecyclerViewOnClickListener<GroupBean?, ChildBean?> {
            override fun onGroupLongClicked(groupItem: GroupBean?): Boolean {
                return false
            }

            override fun onInterceptGroupExpandEvent(
                groupItem: GroupBean?,
                isExpand: Boolean
            ): Boolean {
                return false
            }

            override fun onGroupClicked(groupItem: GroupBean?) {
                mAdapter!!.setSelectedChildBean(groupItem!!)
            }


            override fun onChildClicked(groupItem: GroupBean?, childItem: ChildBean?) {
                val intent = Intent(mActivity, childItem?.getTargetClass())
                intent.putExtra("TITLE", childItem?.mName)
                intent.putExtra("TYPE", childItem?.mType)
                mActivity?.startActivity(intent)
            }

        })
        rvList.adapter = mAdapter
    }

    override fun getLayoutId(): Int = R.layout.fragment_home


    private fun initGroupData(): List<GroupBean>? {
        val groupList: MutableList<GroupBean> = ArrayList()
        val videoConnectChildList: MutableList<ChildBean> = ArrayList()
        videoConnectChildList.add(
            ChildBean(
                "仿京东金融的数值滚动尺",
                R.mipmap.roulette_daohang,
                1,
                TestActivity::class.java
            )
        )
        videoConnectChildList.add(
            ChildBean(
                "仿京",
                R.mipmap.roulette_daohang,
                1,
                TestActivity::class.java
            )
        )
        return groupList
    }


    private class MainExpandableAdapter(private val mListGroupBean: List<GroupBean>) :
        BaseExpandableRecyclerViewAdapter<GroupBean?, ChildBean?, GroupVH?, ChildVH?>() {
        private var mGroupBean: GroupBean? = null
        fun setSelectedChildBean(groupBean: GroupBean) {
            val isExpand: Boolean = isExpand(groupBean)
            if (mGroupBean != null) {
                val lastVH: GroupVH? = getGroupViewHolder(mGroupBean)
                mGroupBean = if (!isExpand) groupBean else null
                if (lastVH != null) {
                    notifyItemChanged(lastVH.adapterPosition)
                }
            } else {
                mGroupBean = if (!isExpand) groupBean else null
            }
            if (mGroupBean != null) {
                val currentVH: GroupVH? = getGroupViewHolder(mGroupBean)
                if (currentVH != null) {
                    notifyItemChanged(currentVH.adapterPosition)
                }
            }
        }

        override fun getGroupCount(): Int {
            return mListGroupBean.size
        }

        override fun getGroupItem(groupIndex: Int): GroupBean {
            return mListGroupBean[groupIndex]
        }

        override fun onCreateGroupViewHolder(parent: ViewGroup, groupViewType: Int): GroupVH {
            return GroupVH(
                LayoutInflater.from(parent.context)
                    .inflate(R.layout.module_entry_item, parent, false)
            )
        }


        override fun onCreateChildViewHolder(parent: ViewGroup, childViewType: Int): ChildVH {
            return ChildVH(
                LayoutInflater.from(parent.context)
                    .inflate(R.layout.module_entry_child_item, parent, false)
            )
        }


        override fun onBindGroupViewHolder(
            holder: GroupVH?,
            groupBean: GroupBean?,
            isExpand: Boolean
        ) {
            if (holder != null) {
                if (groupBean != null) {
                    holder.textView.text = groupBean.mName
                }
            }
            groupBean?.mIconId?.let { holder?.ivLogo?.setImageResource(it) }
            if (mGroupBean === groupBean) {
                holder?.itemView?.setBackgroundResource(R.color.main_item_selected_color)
            } else {
                holder?.itemView?.setBackgroundResource(R.color.main_item_unselected_color)
            }
        }

        override fun onBindChildViewHolder(
            holder: ChildVH?,
            groupBean: GroupBean?,
            childBean: ChildBean?
        ) {
            if (holder != null) {
                if (childBean != null) {
                    holder.textView.text = childBean.getName()
                }
            }
            if (groupBean != null) {
                if (groupBean.mChildList.indexOf(childBean) == groupBean.mChildList.size - 1) { //说明是最后一个
                    if (holder != null) {
                        holder.divideView.visibility = View.GONE
                    }
                } else {
                    if (holder != null) {
                        holder.divideView.visibility = View.VISIBLE
                    }
                }
            }
        }
    }


    class GroupVH internal constructor(itemView: View) :
        BaseExpandableRecyclerViewAdapter.BaseGroupViewHolder(itemView) {
        var ivLogo: ImageView = itemView.findViewById<View>(R.id.icon_iv) as ImageView
        var textView: TextView = itemView.findViewById<View>(R.id.name_tv) as TextView
        protected override fun onExpandStatusChanged(
            relatedAdapter: RecyclerView.Adapter<*>?,
            isExpanding: Boolean
        ) {
        }

    }

    class ChildVH internal constructor(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var textView: TextView = itemView.findViewById<View>(R.id.name_tv) as TextView
        var divideView: View = itemView.findViewById(R.id.item_view_divide)

    }

    private class GroupBean(
        val mName: String,
        val mIconId: Int,
        val mChildList: List<ChildBean>
    ) :
        BaseExpandableRecyclerViewAdapter.BaseGroupBean<ChildBean?> {
        override fun getChildCount(): Int {
            return mChildList.size
        }

        override fun getChildAt(index: Int): ChildBean? {
            return if (mChildList.size <= index) null else mChildList[index]
        }

        override fun isExpandable(): Boolean {
            return childCount > 0
        }

        fun getName(): String {
            return mName
        }

        fun getChildList(): List<ChildBean> {
            return mChildList
        }

        fun getIconId(): Int {
            return mIconId
        }
    }

    private class ChildBean(
        var mName: String,
        var mIconId: Int,
        var mType: Int,
        var mTargetClass: Class<*>
    ) {
        fun getName(): String {
            return mName
        }

        fun getIconId(): Int {
            return mIconId
        }

        fun getTargetClass(): Class<*> {
            return mTargetClass
        }
    }
}