package com.example.iknowway;

import register.Register;
import show.Show;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Rect;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.view.Menu;
import android.view.MotionEvent;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.nhn.android.maps.NMapActivity;
import com.nhn.android.maps.NMapController;
import com.nhn.android.maps.NMapOverlay;
import com.nhn.android.maps.NMapOverlayItem;
import com.nhn.android.maps.NMapView;
import com.nhn.android.maps.NMapView.OnMapStateChangeListener;
import com.nhn.android.maps.NMapView.OnMapViewTouchEventListener;
import com.nhn.android.maps.maplib.NGeoPoint;
import com.nhn.android.maps.nmapmodel.NMapError;
import com.nhn.android.maps.overlay.NMapPOIdata;
import com.nhn.android.maps.overlay.NMapPOIitem;
import com.nhn.android.mapviewer.overlay.NMapCalloutOverlay;
import com.nhn.android.mapviewer.overlay.NMapOverlayManager;
import com.nhn.android.mapviewer.overlay.NMapOverlayManager.OnCalloutOverlayListener;
import com.nhn.android.mapviewer.overlay.NMapPOIdataOverlay;
import com.nhn.android.mapviewer.overlay.NMapPOIdataOverlay.OnStateChangeListener;

public class MainActivity extends NMapActivity implements
		OnMapStateChangeListener, OnMapViewTouchEventListener,
		OnCalloutOverlayListener, OnStateChangeListener, LocationListener {

	public static final String API_KEY = "7f8b48f1917b0bfa16d9140ffe8776ca";
	public static final int ACTIVITY_SHOW = 0;
	public static final int ACTIVITY_REGISTER = 0;
	
	NMapView mMapView = null;
	NMapController mMapController = null;
	//LinearLayout MapContainer;
	//Button button_alertDialog;

//	OnStateChangeListener onPOIdataStateChangeListener = null;
	//NMapViewerResourceProvider mMapViewerResourceProvider = null;
	NMapOverlayManager mOverlayManager;
//	OnMapViewTouchEventListener ChangeListener = null;
	//NMapPOIdata poiData;
	//NMapPOIdata NMapPOIdatapoiData;

	//private PopupWindow pw;
	//private View pv;	

	int Mark_count = 1;
	//int markerId = NMapPOIflagType.PIN;

	Intent register_intent;
	Intent show_intent;
	
	// LinearLayout LLayout;
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		register_intent = new Intent(this, Register.class);
		show_intent = new Intent(this, Show.class);
		
		/*********************변수선언****************/
		LinearLayout MapContainer;
		
		/************************** 초기화 시작 **************************/

		// 네이버 지도를 넣기위한 LinearLayout 컴포넌트
		MapContainer = (LinearLayout) findViewById(R.id.MapContainer);
		// 네이버 지도 객체
		mMapView = new NMapView(this);
		// 지도 객체 컨트롤러.
		mMapController = mMapView.getMapController();
		// ApiKEY
		mMapView.setApiKey(API_KEY);
		// 네이버 지도 객체를 보여줌
		MapContainer.addView(mMapView);
		// 지도 터치가능
		mMapView.setClickable(true);
		// 지도 상태변경 이벤트 연결
		mMapView.setOnMapStateChangeListener(this);
		// 지도 터치 이벤트 연결
		mMapView.setOnMapViewTouchEventListener(this);
		/************************** 초기화 끝 **************************/

		LocationManager manager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
		//Location location = manager	.getLastKnownLocation(LocationManager.GPS_PROVIDER);

		manager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 10000, 0,
				(LocationListener) this);

		mMapView.setOnMapViewTouchEventListener(this);

	}

	private void SetMarker(final double latitude, final double longitude) {
		/***********************변수선언*************************/
		OnStateChangeListener onPOIdataStateChangeListener = null;
		NMapViewerResourceProvider mMapViewerResourceProvider = null;
		int markerId = NMapPOIflagType.PIN;
		
		/************************** 오버레이 설정 **************************/
		// 오버레이 표시를 위한 리소스 객체 할당.
		mMapViewerResourceProvider = new NMapViewerResourceProvider(this);
		// 오버레이 관리자 추가
		mOverlayManager = new NMapOverlayManager(this, mMapView,
				mMapViewerResourceProvider);
		/************************** 오버레이 설정 끝 **************************/

		// mOverlayManager.setOnCalloutOverlayListener(this); // 오버레이 리스너 등록
		NMapPOIdata poiData = new NMapPOIdata(Mark_count,
				mMapViewerResourceProvider);
		poiData.beginPOIdata(Mark_count);
		poiData.addPOIitem(latitude, longitude, "Yoooo", markerId, 0);
		poiData.endPOIdata();

		// 위치 데이터를 사용하여 오버래이 생성
		NMapPOIdataOverlay poiDataOverlay = mOverlayManager
				.createPOIdataOverlay(poiData, null);

		// 위치 재설정. 마커를 중심으로 두도록
		// poiDataOverlay.showAllPOIdata(0);
		poiDataOverlay.setOnStateChangeListener(onPOIdataStateChangeListener = new NMapPOIdataOverlay.OnStateChangeListener() {

			@Override
			public void onCalloutClick(NMapPOIdataOverlay poiDataOverlay, NMapPOIitem item) {


			}

			@Override
			public void onFocusChanged(NMapPOIdataOverlay poiDataOverlay, NMapPOIitem item) {
				if(item!=null)
					MakerClicked(latitude,longitude);

			}
		});


		mOverlayManager.setOnCalloutOverlayListener(this);
		// ////////////////////////////////////////////////////////마커의 위치를 서버에
		// 전송해야 할듯.

		Mark_count++;

		//

	}

	/*
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}*/

	public void onLongPress(NMapView arg0, MotionEvent arg1) {
		// TODO Auto-generated method stub

	}

	public void onLongPressCanceled(NMapView arg0) {
		// TODO Auto-generated method stub

	}

	public void onScroll(NMapView arg0, MotionEvent arg1, MotionEvent arg2) {
		// TODO Auto-generated method stub

	}

	public void onSingleTapUp(NMapView map, MotionEvent me) {
		// TODO Auto-generated method stub
		NGeoPoint touchGP = map.getMapProjection().fromPixels((int) me.getX(),
				(int) me.getY());

		SetMarker(touchGP.longitude, touchGP.latitude);
	}

	public void onTouchDown(NMapView mapView, MotionEvent me) {
		// TODO Auto-generated method stub

	}

	public void onTouchUp(NMapView map, MotionEvent me) {
		// TODO Auto-generated method stub

	}

	public void onAnimationStateChange(NMapView arg0, int arg1, int arg2) {
		// TODO Auto-generated method stub

	}

	public void onMapCenterChange(NMapView arg0, NGeoPoint arg1) {
		// TODO Auto-generated method stub

	}

	public void onMapCenterChangeFine(NMapView arg0) {
		// TODO Auto-generated method stub

	}

	public void onMapInitHandler(NMapView mapview, NMapError errorInfo) {
		// TODO Auto-generated method stub

		if (errorInfo == null) {
			mMapController
			.setMapCenter(new NGeoPoint(127.34397, 36.366596), 14);
		} else {
			android.util.Log.e("NMAP",
					"onMapInitHandler: error=" + errorInfo.toString());

		}

	}

	public void onZoomLevelChange(NMapView arg0, int arg1) {
		// TODO Auto-generated method stub

	}

	
	@Override
	public NMapCalloutOverlay onCreateCalloutOverlay(NMapOverlay overlay,
			NMapOverlayItem item, Rect rect) {
		// TODO Auto-generated method stub

		return null;
	}

	@Override
	public void onLocationChanged(Location arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onProviderDisabled(String arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onProviderEnabled(String arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onStatusChanged(String arg0, int arg1, Bundle arg2) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onCalloutClick(NMapPOIdataOverlay arg0, NMapPOIitem arg1) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onFocusChanged(NMapPOIdataOverlay overlay, NMapPOIitem item) {
		// TODO Auto-generated method stub

		if(item!=null){

		}
		else{

		}
	}

	public void MakerClicked(final double lat, final double log) {

		AlertDialog.Builder builder = new AlertDialog.Builder(this);

		builder.setTitle("충남대학교 공대5호관");
		builder.setMessage("하고 싶은 행동을 선택해주세요.");
		builder.setNeutralButton("등록", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {

				//Toast.makeText(getApplicationContext(), "x : "+lat+"\ny : "+log, Toast.LENGTH_LONG).show();
				register_intent.putExtra("x", lat); //등록화면으로 가져갈 값
				register_intent.putExtra("y", log);
				
				startActivityForResult(register_intent,ACTIVITY_REGISTER);
			}
		});
		builder.setNegativeButton("보기", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				show_intent.putExtra("x", lat);
				show_intent.putExtra("y", log);
				
				startActivityForResult(show_intent, ACTIVITY_SHOW);
			}
		});
		builder.setPositiveButton("취소", new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {

			}
		});
		builder.show();



	}

}
