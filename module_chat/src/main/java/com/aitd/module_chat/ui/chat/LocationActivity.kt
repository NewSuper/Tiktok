package com.aitd.module_chat.ui.chat

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Color
import android.os.Bundle
import android.os.Environment
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import com.aitd.library_common.base.BaseActivity
import com.aitd.module_chat.Message
import com.aitd.module_chat.R
import com.aitd.module_chat.adapter.LocationAdapter
import com.aitd.module_chat.lib.MessageCreator
import com.aitd.module_chat.lib.QXIMClient
import com.aitd.module_chat.pojo.LocationModel
import com.aitd.module_chat.utils.ToastUtil
import com.aitd.module_chat.utils.qlog.QLog
import com.amap.api.maps.AMap
import com.amap.api.maps.CameraUpdateFactory
import com.amap.api.maps.model.*
import com.amap.api.services.core.LatLonPoint
import com.amap.api.services.core.PoiItem
import com.amap.api.services.poisearch.PoiResult
import com.amap.api.services.poisearch.PoiSearch
import kotlinx.android.synthetic.main.imui_activity_location.*
import java.io.FileNotFoundException
import java.io.FileOutputStream
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*


class LocationActivity : BaseActivity() {

    private  val TAG = "LocationActivity"
    override fun getLayoutId() = R.layout.imui_activity_location
    private lateinit var mAMap: AMap

    private var locationData = mutableListOf<LocationModel>()
    private val adapter:LocationAdapter by lazy { LocationAdapter(R.layout.imui_item_location) }

    private var selectLocationData:LocationModel ?= null

    private val zoom = 18f
    private var targetId:String? = ""
    private var conversationType:String? = ""
    private var curLatLng: LatLng?= null
    private var curMarker: Marker?= null
    private var clickMarker: Marker?= null
    override fun init(saveInstanceState: Bundle?) {

    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initView()
        mapView.onCreate(savedInstanceState)
        targetId = intent.getStringExtra("targetId")
        conversationType = intent.getStringExtra("conversationType")
    }

    fun initView() {
        rvLocation.layoutManager = LinearLayoutManager(this)
        rvLocation.adapter = adapter
        adapter.setOnItemClickListener { adapter, view, position ->
            for(i in adapter.data.indices) {
                (adapter.data[i] as LocationModel).selected = i == position
            }
            selectLocationData = adapter.data[position] as LocationModel
            adapter.notifyDataSetChanged()
            val latLng = LatLng(selectLocationData!!.lat, selectLocationData!!.lng)
            clickMarker?.destroy()
            curMarker?.remove()
            clickMarker?.destroy()
            clickMarker?.remove()
            mAMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng,zoom),object : AMap.CancelableCallback {
                override fun onFinish() {
                    curMarker = mAMap.addMarker(MarkerOptions().position(latLng).icon(getPositionBitmap()))
                }

                override fun onCancel() {
                }
            })
        }
        tvCancel.setOnClickListener {
            finish()
        }
        tvSend.setOnClickListener {
            sendLocationMessage()
        }

        ivPositioning.setOnClickListener {
            curLatLng?.let {
                clickMarker?.destroy()
                curMarker?.remove()
                clickMarker?.destroy()
                clickMarker?.remove()
                mAMap.animateCamera(CameraUpdateFactory.newLatLngZoom(it,zoom),object : AMap.CancelableCallback {
                    override fun onFinish() {
                        curMarker = mAMap.addMarker(MarkerOptions().position(it).icon(getPositionBitmap()))
                        searchBound(it)
                    }

                    override fun onCancel() {
                    }
                })
            }
        }
        mAMap = mapView.map
        val myLocationStyle = MyLocationStyle()
        myLocationStyle.interval(2000)
        myLocationStyle.myLocationType(MyLocationStyle.LOCATION_TYPE_LOCATE)
        myLocationStyle.myLocationIcon(BitmapDescriptorFactory.fromResource(R.drawable.imui_location_circle))
        myLocationStyle.strokeColor(Color.TRANSPARENT)
        myLocationStyle.strokeWidth(0f)
        myLocationStyle.radiusFillColor(Color.TRANSPARENT)
        mAMap.myLocationStyle = myLocationStyle
        mAMap.uiSettings.isMyLocationButtonEnabled = false
        mAMap.uiSettings.isZoomControlsEnabled = false
        mAMap.isMyLocationEnabled = true
        mAMap.moveCamera(CameraUpdateFactory.zoomTo(zoom))
        mAMap.setOnMyLocationChangeListener {

            val latLng = LatLng(it.latitude, it.longitude)
            if (curLatLng == null) {
                curLatLng = latLng
            }
            clickMarker?.destroy()
            mAMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng,zoom),object : AMap.CancelableCallback {
                override fun onFinish() {
                    if (curMarker == null) {
                        curMarker =  mAMap.addMarker(MarkerOptions().position(latLng).icon(getPositionBitmap()))
                        searchBound(latLng)
                    }
                }

                override fun onCancel() {
                }

            })
            QLog.d(TAG,"setOnMyLocationChangeListener：${latLng.latitude},${latLng.longitude}")
        }

        mAMap.setOnCameraChangeListener(object : AMap.OnCameraChangeListener {
            override fun onCameraChangeFinish(cameraPosition: CameraPosition?) {
                if (cameraPosition?.target != null) {
                    val latLng = LatLng(cameraPosition?.target.latitude, cameraPosition?.target.longitude)
                    curMarker?.setPositionNotUpdate(latLng)
                    searchBound(latLng)
                }
            }

            override fun onCameraChange(cameraPosition: CameraPosition?) {
                clickMarker?.destroy()
                if (cameraPosition?.target != null && curLatLng != null) {
                    val latLng = LatLng(cameraPosition?.target.latitude, cameraPosition?.target.longitude)
                    if (curMarker == null) {
                        curMarker =  mAMap.addMarker(MarkerOptions().position(latLng).icon(getPositionBitmap()))
                    } else {
                        curMarker!!.setPositionNotUpdate(latLng)
                    }
                }
            }
        })

        mAMap.setOnMapClickListener {
            clickMarker?.destroy()
            clickMarker?.remove()
            clickMarker = null
            val latLng = LatLng(it.latitude, it.longitude)
            QLog.d(TAG,"setOnMapClickListener：${latLng.latitude},${latLng.longitude}")
            curMarker?.destroy()
            curMarker?.remove()
            curMarker = null
            mAMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng,zoom),object : AMap.CancelableCallback {
                override fun onFinish() {
                    clickMarker = mAMap.addMarker(MarkerOptions().position(latLng).icon(getPositionBitmap()))
                    searchBound(latLng)
                }

                override fun onCancel() {
                }

            })
        }
    }

    private fun getPositionBitmap() : BitmapDescriptor {
        return BitmapDescriptorFactory.fromResource(R.drawable.imui_location_position)
    }

    private fun searchBound(latLng: LatLng) {
        QLog.d(TAG,"searchBound latLng:${latLng.latitude},${latLng.longitude}")
        pbLocation.visibility = View.VISIBLE
        adapter.data.clear()
        adapter.notifyDataSetChanged()
        val query = PoiSearch.Query("", "", "")
        query.pageSize = 20
        query.pageNum = 1
        val posiSearch = PoiSearch(this@LocationActivity,query)
        posiSearch.setOnPoiSearchListener(object : PoiSearch.OnPoiSearchListener {
            override fun onPoiItemSearched(p0: PoiItem?, p1: Int) {

            }

            override fun onPoiSearched(p0: PoiResult?, p1: Int) {
                pbLocation.visibility = View.GONE
                QLog.d(TAG,"onPoiSearched pageCount:${p0?.pageCount}")
                if (p0 != null && p0.pois.size > 0) {
                    adapter.data.clear()
                    locationData.clear()
                    for (posiresult in p0.pois) {
                        val locationModel = LocationModel(posiresult.latLonPoint.latitude,posiresult.latLonPoint.longitude,
                            posiresult.title,posiresult.snippet,posiresult.adCode,false)
                        locationData.add(locationModel)
                        QLog.d(TAG,"posiresult:title:${posiresult.title},${posiresult.snippet}")
                    }
                    if (locationData.size > 0) {
                        selectLocationData = locationData[0]
                        locationData[0].selected = true
                    }
                    adapter.addData(locationData)
                }
            }
        })
        posiSearch.bound = PoiSearch.SearchBound(LatLonPoint(latLng.latitude,latLng.longitude), 500)
        posiSearch.searchPOIAsyn()
    }


    private fun sendLocationMessage() {
        selectLocationData?.apply {
            mAMap.getMapScreenShot(object : AMap.OnMapScreenShotListener {
                override fun onMapScreenShot(p0: Bitmap?) {

                }

                override fun onMapScreenShot(bitmap: Bitmap?, p1: Int) {
                    if (bitmap == null) {
                        ToastUtil.toast(this@LocationActivity, getString(R.string.qx_geo_screenshot_fail))
                        return
                    } else {
                        val sdf = SimpleDateFormat("yyyyMMddHHmmss")
                        val path = "${Environment.getExternalStorageDirectory()}/${sdf.format(Date())}"
                        try {
                            val fos = FileOutputStream(path)
                            val b = bitmap.compress(Bitmap.CompressFormat.PNG, 100, fos);
                            try {
                                fos.flush()
                            } catch (e: IOException) {
                                e.printStackTrace()
                            }
                        } catch (e: FileNotFoundException) {
                            e.printStackTrace()
                        }
                        if(path.isNullOrEmpty()) {
                            return
                        }
                        var message = createGeoMessage(path)
                        var intent = Intent()
                        intent.putExtra("geo", message)
                        setResult(RESULT_OK, intent)
                        finish()
                    }
                }
            })
        }
    }

    private fun createGeoMessage(path: String?) : Message {
        return MessageCreator.instance.createGeoMessage(conversationType!!,
            QXIMClient.instance.getCurUserId()!!, targetId!!, selectLocationData!!.title, selectLocationData!!.address, "",path!!,
            selectLocationData!!.lng.toFloat(), selectLocationData!!.lat.toFloat(), "")
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