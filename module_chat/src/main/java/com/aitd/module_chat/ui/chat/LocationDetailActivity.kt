package com.aitd.module_chat.ui.chat

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import com.aitd.library_common.base.BaseActivity
import com.aitd.library_common.utils.MapNavigationUtils
import com.aitd.library_common.utils.StringUtil
import com.aitd.module_chat.GeoMessage
import com.aitd.module_chat.Message
import com.aitd.module_chat.R
import com.aitd.module_chat.view.CustomizeDialogs
import com.aitd.module_chat.view.bottom.BottomMenuDialog
import com.aitd.module_chat.view.bottom.BottomMenuItem
import com.amap.api.maps.AMap
import com.amap.api.maps.CameraUpdateFactory
import com.amap.api.maps.model.*
import kotlinx.android.synthetic.main.imui_activity_location_detail.*
import java.util.ArrayList

class LocationDetailActivity :BaseActivity(){

    companion object{
        fun startActivity(context: Context,message: Message){
            val intent = Intent(context,LocationDetailActivity::class.java)
            intent.putExtra("message",message)
            context.startActivity(intent)
        }
    }
    override fun getLayoutId(): Int = R.layout.imui_activity_location_detail

    private var message:Message? =null
    private lateinit var mAMap: AMap
    private var geoMessage: GeoMessage? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mapView.onCreate(savedInstanceState)
    }
    override fun init(saveInstanceState: Bundle?) {


        message = intent.getParcelableExtra("message")
        message?.let {
            geoMessage = it.messageContent as GeoMessage
            showMap()
            Log.e("LocationDetailActivity", "$geoMessage")
        }

        tvBack.setOnClickListener {
            finish()
        }
        mAMap = mapView.map
        val myLocationStyle = MyLocationStyle()
        myLocationStyle.showMyLocation(false)
        mAMap.myLocationStyle = myLocationStyle
        mAMap.uiSettings.isZoomControlsEnabled = false
        mAMap.setOnMarkerClickListener {
            if (it.isInfoWindowShown) {
                it.hideInfoWindow()
            } else {
                it.showInfoWindow()
            }
            return@setOnMarkerClickListener true
        }
        mAMap.setInfoWindowAdapter(object : AMap.InfoWindowAdapter {
            override fun getInfoContents(marker: Marker): View? {
                return null
            }

            override fun getInfoWindow(marker: Marker): View {
                val view: View = View.inflate(this@LocationDetailActivity, R.layout.imui_item_location_info, null)
                val titleTv = view.findViewById(R.id.tvLocationInfoTitle) as TextView
                val addressTv = view.findViewById(R.id.tvLocationInfoAddress) as TextView
                val navigationBtn = view.findViewById<LinearLayout>(R.id.map_navigation_layout);
                titleTv.text = getMapTitle()
                addressTv.text = getMapAddress()
                navigationBtn.setOnClickListener(View.OnClickListener {
                    //点击使用第三方app导航
                    showOperaDialog()
                })
                return view
            }

        })
    }
    fun showOperaDialog() {
        //检查安装的导航软件列表
        val menuItemList: MutableList<BottomMenuItem> = ArrayList<BottomMenuItem>()
        var mapList = MapNavigationUtils.getInstalledMapApp(this@LocationDetailActivity);
        if (mapList != null && mapList.size > 0) {
            for ((index, name) in mapList.withIndex()) {
                val itemId = mapList.get(index).mapId;
                val itemName = mapList.get(index).mapName;
                menuItemList.add(BottomMenuItem(itemId, itemName))
            }
            val bottomMenuDialog = BottomMenuDialog()
            bottomMenuDialog.setBottomMenuList(this, menuItemList)
            bottomMenuDialog.setOnMenuItemClickListener { itemId, ob ->
                when (itemId) {
                    1 -> {
                        //谷歌地图
                        MapNavigationUtils.gotoGoogleMap(this@LocationDetailActivity, geoMessage?.lat.toString(), geoMessage?.lon.toString())
                    }
                    2 -> {
                        //高德地图
                        MapNavigationUtils.gotoGaoDe(this@LocationDetailActivity, geoMessage?.lat.toString(), geoMessage?.lon.toString(), geoMessage?.address)
                    }

                    3 -> {
                        //百度地图
                        MapNavigationUtils.goToBaiduActivity(this@LocationDetailActivity, geoMessage?.address, geoMessage?.lat.toString(), geoMessage?.lon.toString())
                    }
                    4 -> {
                        //腾讯地图
                        MapNavigationUtils.gotoTengxun(this@LocationDetailActivity, geoMessage?.address, geoMessage?.lat.toString(), geoMessage?.lon.toString())
                    }
                }
            }
            bottomMenuDialog.show(supportFragmentManager, BottomMenuDialog::class.java.getSimpleName())
        } else {
            showNoInstallDialog(this@LocationDetailActivity, StringUtil.getResourceStr(this,R.string.dialog_content), StringUtil.getResourceStr(this,R.string.dialog_know));
        }
    }

    /**
     * 没有安装导航软件的提示框
     */
    fun showNoInstallDialog(mContext: Context, message: String, leftButtonText: String) {
        val customizeDialogs = CustomizeDialogs(mContext)
        customizeDialogs.setMessage(message)
        customizeDialogs.setMessageAlignCenter()
        customizeDialogs.setCanceledOnTouchOutside(false)
        customizeDialogs.setSingleButton(leftButtonText, CustomizeDialogs.IDialogsCallBack { buttonType, thisDialogs ->
            when (buttonType) {
                CustomizeDialogs.ButtonType.leftButton -> {
                    customizeDialogs.dismiss()
                }
            }
        })
        customizeDialogs.show()
    }

    fun showMap() {
        val latLng = LatLng(getLat(), getLng())
        mAMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 18f))
        addMarkersToMap(latLng)
    }

    open fun getMapTitle(): String {
        if (geoMessage == null) {
            return ""
        }
        return geoMessage!!.title
    }

    open fun getMapAddress(): String {
        if (geoMessage == null) {
            return ""
        }
        return geoMessage!!.address
    }

    open fun getLng(): Double {
        return geoMessage!!.lon.toDouble();
    }

    open fun getLat(): Double {
        return geoMessage!!.lat.toDouble();
    }

    private fun addMarkersToMap(latlng: LatLng) {
        if (mAMap != null) {

            val circleBitmap = BitmapDescriptorFactory.fromResource(R.drawable.imui_location_circle)
            val circleOption = MarkerOptions()
                .position(latlng)
                .anchor(0f, 0.6f)
                .draggable(true)
                .icon(circleBitmap)
            mAMap.addMarker(circleOption)
            circleBitmap.recycle()

            val bitmap = BitmapDescriptorFactory.fromResource(R.drawable.imui_location_position)
            var markerOption = MarkerOptions()
                .position(latlng)
                .anchor(0f, 1f)
                .draggable(true)
                .icon(bitmap)
            val marker = mAMap.addMarker(markerOption)
            marker.showInfoWindow()


            bitmap.recycle()
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        mapView.onSaveInstanceState(outState)
    }

    override fun onResume() {
        super.onResume()
        mapView.onResume()
    }

    override fun onPause() {
        super.onPause()
        mapView.onPause()
    }

    override fun onDestroy() {
        super.onDestroy()
        mapView.onDestroy()
    }
}