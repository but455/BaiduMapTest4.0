package com.example.baidumaptest;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.SDKInitializer;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BaiduMap.OnMapClickListener;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.MapPoi;
import com.baidu.mapapi.map.MapStatus;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.search.geocode.GeoCodeResult;
import com.baidu.mapapi.search.geocode.GeoCoder;
import com.baidu.mapapi.search.geocode.OnGetGeoCoderResultListener;
import com.baidu.mapapi.search.geocode.ReverseGeoCodeOption;
import com.baidu.mapapi.search.geocode.ReverseGeoCodeResult;

public class BaiduTestActivity extends Activity {
	private MapView mMapView = null;
	private BaiduMap mBaiduMap;
	private BitmapDescriptor bitmap;
	private String address = "";
	private double mCurrentLat = 0.0;
	private double mCurrentLon = 0.0;
	public MyLocationListenner myListener = new MyLocationListenner();
	private LocationClient mLocClient;
	private TextView city_tv, address_tv;
	private Button dw_bt;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// 在使用SDK各组件之前初始化context信息，传入ApplicationContext
		// 注意该方法要再setContentView方法之前实现
		SDKInitializer.initialize(getApplicationContext());
		setContentView(R.layout.activity_bdtest);
		city_tv = (TextView) findViewById(R.id.city_tv);
		address_tv = (TextView) findViewById(R.id.address_tv);
		dw_bt=(Button)findViewById(R.id.dw_bt);
		// 获取地图控件引用
		mMapView = (MapView) findViewById(R.id.bmapView);
		mBaiduMap = mMapView.getMap();
		// 设置是否显示比例尺控件
		mMapView.showScaleControl(false);
		// 设置是否显示缩放控件
		mMapView.showZoomControls(true);
		// 设置是否显示定位按钮
		// 删除百度地图LoGo
		mMapView.removeViewAt(1);

		// 设置marker图标
		bitmap = BitmapDescriptorFactory
				.fromResource(R.drawable.ssdk_oks_ptr_ptr);
		mBaiduMap.setOnMapClickListener(new OnMapClickListener() {

			@Override
			public boolean onMapPoiClick(MapPoi arg0) {
				// TODO Auto-generated method stub
				return false;
			}

			// 此方法就是点击地图监听
			@Override
			public void onMapClick(LatLng latLng) {
				// 获取经纬度
				mCurrentLat = latLng.latitude;
				mCurrentLon = latLng.longitude;

				// 先清除图层
				mBaiduMap.clear();
				// 定义Maker坐标点
				LatLng point = new LatLng(mCurrentLat, mCurrentLon);
				// 构建MarkerOption，用于在地图上添加Marker
				MarkerOptions options = new MarkerOptions().position(point)
						.icon(bitmap);
				// 在地图上添加Marker，并显示
				mBaiduMap.addOverlay(options);
				// 实例化一个地理编码查询对象
				GeoCoder geoCoder = GeoCoder.newInstance();
				// 设置反地理编码位置坐标
				ReverseGeoCodeOption op = new ReverseGeoCodeOption();
				op.location(latLng);
				// 发起反地理编码请求(经纬度->地址信息)
				geoCoder.reverseGeoCode(op);
				geoCoder.setOnGetGeoCodeResultListener(new OnGetGeoCoderResultListener() {

					@Override
					public void onGetReverseGeoCodeResult(
							ReverseGeoCodeResult arg0) {
						// 获取点击的坐标地址
						address = arg0.getAddress();
						city_tv.setText(arg0.getAddressDetail().city);
						address_tv.setText(address);

					}

					@Override
					public void onGetGeoCodeResult(GeoCodeResult arg0) {
					}
				});
			}
		});

		// 开启定位图层
		mBaiduMap.setMyLocationEnabled(true);
		// 定位初始化
		mLocClient = new LocationClient(this);
		mLocClient.registerLocationListener(myListener);
		LocationClientOption option = new LocationClientOption();
		option.setOpenGps(true); // 打开gps
		option.setCoorType("bd09ll"); // 设置坐标类型
		option.setScanSpan(1000);
		mLocClient.setLocOption(option);
		mLocClient.start();
		
		//点击定位按钮重新定位
		dw_bt.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				mBaiduMap.setMyLocationEnabled(true);
				mLocClient.start();
			}
				
		});
	}

	/**
	 * 定位SDK监听函数
	 */
	public class MyLocationListenner implements BDLocationListener {

		@Override
		public void onReceiveLocation(BDLocation location) {
			// TODO Auto-generated method stub
			// map view 销毁后不在处理新接收的位置
			if (location == null || mMapView == null) {
				return;

			}
			// 获取经纬度
			mCurrentLat = location.getLatitude();
			mCurrentLon = location.getLongitude();

			// 先清除图层
			mBaiduMap.clear();
			// 定义Maker坐标点
			LatLng point = new LatLng(mCurrentLat, mCurrentLon);
			// 构建MarkerOption，用于在地图上添加Marker
			MarkerOptions options = new MarkerOptions().position(point).icon(
					bitmap);
			// 在地图上添加Marker，并显示
			mBaiduMap.addOverlay(options);

			LatLng ll = new LatLng(location.getLatitude(),
					location.getLongitude());
			MapStatus.Builder builder = new MapStatus.Builder();
			builder.target(ll).zoom(18.0f);
			mBaiduMap.animateMapStatus(MapStatusUpdateFactory
					.newMapStatus(builder.build()));

			// 实例化一个地理编码查询对象
			GeoCoder geoCoder = GeoCoder.newInstance();
			// 设置反地理编码位置坐标
			ReverseGeoCodeOption op = new ReverseGeoCodeOption();
			op.location(point);
			// 发起反地理编码请求(经纬度->地址信息)
			geoCoder.reverseGeoCode(op);
			geoCoder.setOnGetGeoCodeResultListener(new OnGetGeoCoderResultListener() {

				@Override
				public void onGetReverseGeoCodeResult(ReverseGeoCodeResult arg0) {
					// 获取点击的坐标地址
					address = arg0.getAddress();
					city_tv.setText(arg0.getAddressDetail().city);
					address_tv.setText(address);
					// 定位成功后就停止
				
					mBaiduMap.setMyLocationEnabled(false);
					if (mLocClient.isStarted()) {
						mLocClient.stop();
					};
				}

				@Override
				public void onGetGeoCodeResult(GeoCodeResult arg0) {
				}
			});

		}

	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		mLocClient.stop();
		// 在activity执行onDestroy时执行mMapView.onDestroy()，实现地图生命周期管理
		mMapView.onDestroy();
	}

	@Override
	protected void onResume() {
		super.onResume();
		// 在activity执行onResume时执行mMapView. onResume ()，实现地图生命周期管理
		mMapView.onResume();
	}

	@Override
	protected void onPause() {
		super.onPause();
		// 在activity执行onPause时执行mMapView. onPause ()，实现地图生命周期管理
		mMapView.onPause();
	}
}