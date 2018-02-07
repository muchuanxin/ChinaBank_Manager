package com.xd.aselab.chinabankmanager.marketingGuide;

import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;
import com.amap.api.maps.AMap;
import com.amap.api.maps.CameraUpdateFactory;
import com.amap.api.maps.LocationSource;
import com.amap.api.maps.MapView;
import com.amap.api.maps.UiSettings;
import com.amap.api.maps.model.BitmapDescriptorFactory;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.Marker;
import com.amap.api.maps.model.MarkerOptions;
import com.amap.api.services.core.LatLonPoint;
import com.amap.api.services.geocoder.GeocodeAddress;
import com.amap.api.services.geocoder.GeocodeQuery;
import com.amap.api.services.geocoder.GeocodeResult;
import com.amap.api.services.geocoder.GeocodeSearch;
import com.amap.api.services.geocoder.RegeocodeResult;
import com.amap.api.services.route.RouteSearch;
import com.xd.aselab.chinabankmanager.R;
import com.xd.aselab.chinabankmanager.kafenqi.KWorkerManage.model.WorkerVO;
import com.xd.aselab.chinabankmanager.util.AMapUtil;
import com.xd.aselab.chinabankmanager.util.ConnectUtil;
import com.xd.aselab.chinabankmanager.util.DensityUtil;
import com.xd.aselab.chinabankmanager.util.NoScrollListView;
import com.xd.aselab.chinabankmanager.util.PostParameter;
import com.xd.aselab.chinabankmanager.util.RequestDialog;
import com.xd.aselab.chinabankmanager.util.SharePreferenceUtil;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;

public class MarketingGuideNew extends AppCompatActivity implements LocationSource,AMapLocationListener,
        GeocodeSearch.OnGeocodeSearchListener {

    private RelativeLayout back;
    private RequestDialog requestDialog;
    private PopupWindow popupWindow;

    private MapView mapView;
    private AMap aMap;
    private AMapLocationClient locationClient = null;
    private AMapLocationClientOption locationOption = null;
    private LocationSource.OnLocationChangedListener mListener;
    private GeocodeSearch geocoderSearch;
    private AMapLocation amapLocation;
    private int size;
    private int count;
    private String action;

    private final int GREEN_YELLOW_DIVIDE_LINE = 1;//0~GREEN_YELLOW_DIVIDE_LINE-1为绿
    private final int YELLOW_RED_DIVIDE_LINE = 4;//GREEN_YELLOW_DIVIDE_LINE~YELLOW_RED_DIVIDE_LINE-1为黄
    //>=YELLOW_RED_DIVIDE_LINE为红
    private final int STEP = 150;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_marketing_guide_new);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        }
        requestDialog = new RequestDialog();
        //requestDialog.showProgressDialog(MarketingGuide.this,"正在定位，请稍后");

        back = (RelativeLayout) findViewById(R.id.act_market_guide_back_btn);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        /*
         * 设置离线地图存储目录，在下载离线地图或初始化地图设置;
         * 使用过程中可自行设置, 若自行设置了离线地图存储的路径，
         * 则需要在离线地图下载和使用地图页面都进行路径设置
         * */
        //Demo中为了其他界面可以使用下载的离线地图，使用默认位置存储，屏蔽了自定义设置
        //MapsInitializer.sdcardDir =OffLineMapUtils.getSdCacheDir(this);
        mapView = (MapView) findViewById(R.id.act_market_guide_map);
        mapView.onCreate(savedInstanceState);// 此方法必须重写

        /**
         * 初始化AMap对象
         */
        if (aMap == null) {
            aMap = mapView.getMap();
            // 设置点击marker事件监听器
            aMap.setOnMarkerClickListener(new AMap.OnMarkerClickListener() {
                @Override
                public boolean onMarkerClick(Marker marker) {
                    //Toast.makeText(MarketingGuide.this,"点击了---"+marker.getSnippet(),Toast.LENGTH_SHORT).show();
                    showPopWindow(marker.getTitle(), marker.getSnippet());
                    return true;
                }
            });
            UiSettings mUiSettings = aMap.getUiSettings();
            mUiSettings.setZoomControlsEnabled(false);//设置地图默认的缩放按钮是否显示
            mUiSettings.setCompassEnabled(false);//设置地图默认的指南针是否显示
            mUiSettings.setMyLocationButtonEnabled(true);//设置地图默认的定位按钮是否显示
        }

        geocoderSearch = new GeocodeSearch(this);
        geocoderSearch.setOnGeocodeSearchListener(this);

        aMap.setLocationSource(this);// 设置定位监听
        aMap.setMyLocationEnabled(true);// 设置为true表示显示定位层并可触发定位，false表示隐藏定位层并不可触发定位，默认是false
        // 设置定位的类型为定位模式，参见类AMap。
        aMap.setMyLocationType(AMap.LOCATION_TYPE_LOCATE);

    }

    /**
     * 在地图上添加marker
     */
    private void addMarkersToMap(LatLng latLng,String title, String snippet, int number) {
        MarkerOptions markerOptions = new MarkerOptions()
                .position(latLng)
                .title(title)
                .snippet(snippet)
                .draggable(false);
        if (number<GREEN_YELLOW_DIVIDE_LINE){
            markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN));
        }
        else if (number<YELLOW_RED_DIVIDE_LINE){
            markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_YELLOW));
        }
        else {
            markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));
        }

        aMap.addMarker(markerOptions);
    }

    /**
     * 方法必须重写
     */
    @Override
    protected void onResume() {
        super.onResume();
        mapView.onResume();
    }

    /**
     * 方法必须重写
     */
    @Override
    protected void onPause() {
        super.onPause();
        mapView.onPause();
        deactivate();
    }

    /**
     * 激活定位
     */
    @Override
    public void activate(OnLocationChangedListener listener) {
        mListener = listener;
        if (locationClient == null) {
            locationClient = new AMapLocationClient(this);
            locationOption = new AMapLocationClientOption();
            // 设置定位模式为低功耗模式
            locationOption.setLocationMode(AMapLocationClientOption.AMapLocationMode.Battery_Saving);
            //设置为单次定位
            locationOption.setOnceLocation(true);
            //设置定位监听
            locationClient.setLocationListener(this);
            // 设置是否需要显示地址信息
            locationOption.setNeedAddress(true);
            // 设置是否开启缓存
            locationOption.setLocationCacheEnable(true);
            //设置是否等待设备wifi刷新，如果设置为true,会自动变为单次定位，持续定位时不要使用
            locationOption.setOnceLocationLatest(true);
            /**
             *  设置发送定位请求的时间间隔,最小值为1000，如果小于1000，按照1000算
             *  只有持续定位设置定位间隔才有效，单次定位无效
             */
            locationOption.setInterval(1000);
            //设置定位参数
            locationClient.setLocationOption(locationOption);
            // 此方法为每隔固定时间会发起一次定位请求，为了减少电量消耗或网络流量消耗，
            // 注意设置合适的定位时间的间隔（最小间隔支持为2000ms），并且在合适时间调用stopLocation()方法来取消定位请求
            // 在定位结束后，在合适的生命周期调用onDestroy()方法
            // 在单次定位情况下，定位无论成功与否，都无需调用stopLocation()方法移除请求，定位sdk内部会移除

            AMapLocation location = locationClient.getLastKnownLocation();//获取最后一次定位
            if (location!=null){
                this.amapLocation = location;
                mListener.onLocationChanged(amapLocation);// 显示系统小蓝点
                aMap.moveCamera(CameraUpdateFactory.zoomTo(14));
                //requestDialog.showProgressDialog(MarketingGuide.this,"正在搜索，请稍后");
                //showProgressDialog("正在搜索，请稍后");
            }
            //定位与添加marker为异步，顺序为 调用最后一次定位->加marker->新定位
            sendMsg();

            locationClient.startLocation();
        }
    }

    /**
     * 停止定位
     */
    @Override
    public void deactivate() {
        mListener = null;
        if (locationClient != null) {
            locationClient.stopLocation();
            locationClient.onDestroy();
        }
        locationClient = null;
    }

    /**
     * 方法必须重写
     */
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mapView.onSaveInstanceState(outState);
    }

    /**
     * 方法必须重写
     */
    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (null != locationClient) {
            /**
             * 如果AMapLocationClient是在当前Activity实例化的，
             * 在Activity的onDestroy中一定要执行AMapLocationClient的onDestroy
             */
            locationClient.onDestroy();
            locationClient = null;
            locationOption = null;
        }
        mapView.onDestroy();
    }

    /**
     * 定位成功后回调函数
     */
    @Override
    public void onLocationChanged(AMapLocation amapLocation) {
        if (mListener != null && amapLocation != null) {
            //requestDialog.dissmissProgressDialog();
            if ( amapLocation.getErrorCode() == 0) {
                this.amapLocation = amapLocation;
                mListener.onLocationChanged(amapLocation);// 显示系统小蓝点
                aMap.moveCamera(CameraUpdateFactory.zoomTo(14));
                //requestDialog.showProgressDialog(MarketingGuide.this,"正在搜索，请稍后");
                // showProgressDialog("正在搜索，请稍后");
                //sendMsg();
            } else {
                Toast.makeText(MarketingGuideNew.this, "定位失败，请点击右上角的定位按钮重新定位", Toast.LENGTH_SHORT).show();
                String errText = "定位失败," + amapLocation.getErrorCode()+ ": " + amapLocation.getErrorInfo();
                Log.e("AmapErr",errText);
            }
        }
    }

    private void sendMsg(){
        new Thread(){
            @Override
            public void run() {
                super.run();
                PostParameter[] params = new PostParameter[1];
                params[0] = new PostParameter("market", "market");
                String reCode = ConnectUtil.httpRequest(ConnectUtil.GetMarketingGuide, params, ConnectUtil.POST);
                try {
                    if (reCode!=null){
                        Log.e("www", "reCode:"+reCode);
                        JSONObject json = new JSONObject(reCode);
                        String status = json.getString("status");
                        if ("false".equals(status)) {
                            Toast.makeText(MarketingGuideNew.this, json.getString("message"), Toast.LENGTH_SHORT).show();
                        } else if ("true".equals(status)) {
                            JSONArray jsonArray = json.getJSONArray("4sshop_list");
                            if (jsonArray.length()>0){
                                size = jsonArray.length();
                                Log.e("size",""+size);
                                for (int i=0; i<size; i+=2){
                                    JSONObject one_shop = (JSONObject) jsonArray.get(i);
                                    String shop_name = one_shop.getString("4sshop_name");
                                    String shop_addr = one_shop.getString("4sshop_addr");
                                    JSONArray jsonArray_worker = one_shop.getJSONArray("worker_list");
                                    LatLng latLng = new LatLng(one_shop.getDouble("latitude"), one_shop.getDouble("longitude"));
                                    addMarkersToMap(latLng, shop_name, jsonArray_worker.toString(), jsonArray_worker.length());
                                    action = "mark";
                                }
                            }
                            else {
                                Log.e("CBNetwork_Activity", "list长度为0");
                            }
                        } else {
                            Log.e("CBNetwork_Activity", MarketingGuideNew.this.getResources().getString(R.string.status_exception));
                        }
                    }
                    else {
                        Toast.makeText(MarketingGuideNew.this, MarketingGuideNew.this.getResources().getString(R.string.network_connect_exception), Toast.LENGTH_SHORT).show();
                        Log.e("CBNetwork_Activity", "reCode为空");
                    }
                }
                catch (Exception e){
                    e.printStackTrace();
                }
            }
        }.start();
    }


    /**
     * 逆地理编码回调
     */
    @Override
    public void onRegeocodeSearched(RegeocodeResult regeocodeResult, int i) {

    }

    /**
     * 地理编码查询回调
     */
    @Override
    public void onGeocodeSearched(GeocodeResult geocodeResult, int rCode) {

    }

    private void showPopWindow(String title, String data){
        View root = LayoutInflater.from(MarketingGuideNew.this).inflate(R.layout.join_worker_pop_window,null);

        TextView name_txt = (TextView) root.findViewById(R.id.join_worker_pop_shop_name);
        name_txt.setText(title);

        NoScrollListView worker_list_view = (NoScrollListView) root.findViewById(R.id.join_worker_pop_worker_list);
        NoScrollListView manager_list_view = (NoScrollListView) root.findViewById(R.id.join_worker_pop_manager_list);
        List<Map<String,String>> worker_list = new ArrayList<>();
        List<Map<String,String>> manager_list = new ArrayList<>();
        try {
            JSONArray jsonArray = new JSONArray(data);
            Log.e("jsonArray.length()",""+jsonArray.length());
            for (int i=0; i<jsonArray.length(); i++){
                JSONObject temp = jsonArray.getJSONObject(i);

                Map<String,String> worker_map = new HashMap<>();
                worker_map.put("worker_name",temp.getString("worker_name"));
                worker_list.add(worker_map);

                Map<String,String> manager_map = new HashMap<>();
                manager_map.put("manager_name",temp.getString("manager_name"));
                manager_list.add(manager_map);

                Log.e("worker_name",""+temp.getString("worker_name"));
                Log.e("manager_name",""+temp.getString("manager_name"));
            }
        }
        catch (Exception e){
            e.printStackTrace();
        }
        Log.e("worker_list.length()",""+worker_list.size());
        Log.e("manager_list.length()",""+manager_list.size());
        // 1个长度为135dp 一行list没有超过图片大小
        // 2个长度为150dp 两行超过了
        // 5个长度为210dp
        // 一行为20dp
        int height = 135;
        if (worker_list.size()>1){
            height = (worker_list.size()-2)*20+150;
        }
        /*popupWindow = new PopupWindow(root,
                DensityUtil.dip2px(MarketingGuide.this, 340),
                DensityUtil.dip2px(MarketingGuide.this, height));*/
        popupWindow = new PopupWindow(root, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT, true);
        // 设置popWindow弹出窗体可点击，这句话必须添加，并且是true
        popupWindow.setFocusable(true);
        // 实例化一个ColorDrawable颜色为半透明
        //ColorDrawable dw = new ColorDrawable(0xb0000000);
        // popWindow出来的时候设置背景透明
        //findViewById(R.id.activity_marketing_guide).setBackground(new ColorDrawable(0xb0000000));
        // 不设置这句话，popWindow无法消失（点击旁边、返回键都没用）
        popupWindow.setBackgroundDrawable(new BitmapDrawable());
        // 设置popWindow的显示和消失动画
        popupWindow.setAnimationStyle(R.style.mypopwindow_anim_style);
        // 在中间显示
        popupWindow.showAtLocation(findViewById(R.id.activity_marketing_guide), Gravity.BOTTOM, 0, 0);

        //popWindow消失监听方法
        popupWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                //findViewById(R.id.activity_marketing_guide).setBackground(new BitmapDrawable());
            }
        });

        TextView ok_btn = (TextView) root.findViewById(R.id.join_worker_pop_ok_btn);
        ok_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popupWindow.dismiss();
            }
        });

        SimpleAdapter worker_adapter = new SimpleAdapter(MarketingGuideNew.this, worker_list,
                R.layout.list_view_marketing_guide_pop_worker, new String[]{"worker_name"},
                new int[]{R.id.list_view_marketing_guide_pop_worker_name});
        worker_list_view.setAdapter(worker_adapter);

        SimpleAdapter manager_adapter = new SimpleAdapter(MarketingGuideNew.this, manager_list,
                R.layout.list_view_marketing_guide_pop_manager, new String[]{"manager_name"},
                new int[]{R.id.list_view_marketing_guide_pop_manager_name});
        manager_list_view.setAdapter(manager_adapter);
    }

}
