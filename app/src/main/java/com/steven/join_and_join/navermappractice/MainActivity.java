package com.steven.join_and_join.navermappractice;

import android.Manifest;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.nhn.android.maps.NMapActivity;
import com.nhn.android.maps.NMapController;
import com.nhn.android.maps.NMapLocationManager;
import com.nhn.android.maps.NMapOverlayItem;
import com.nhn.android.maps.NMapProjection;
import com.nhn.android.maps.NMapView;
import com.nhn.android.maps.maplib.NGeoPoint;
import com.nhn.android.maps.nmapmodel.NMapError;
import com.nhn.android.maps.nmapmodel.NMapPlacemark;
import com.nhn.android.maps.overlay.NMapPOIdata;
import com.nhn.android.maps.overlay.NMapPOIitem;
import com.nhn.android.mapviewer.overlay.NMapMyLocationOverlay;
import com.nhn.android.mapviewer.overlay.NMapOverlayManager;

public class MainActivity extends NMapActivity {


    private String[] permission_list = {
            Manifest.permission.INTERNET,
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_WIFI_STATE,
            Manifest.permission.CHANGE_WIFI_STATE
    };

    private final String LOG_TAG = "NMapLog";

    private final String CLIENT_ID = "VssmOcriZJGm_dvVL3cI";
    private NMapView mMapView;
    private NMapLocationManager mMapLocationManager;
    private NMapController mMapController;
    private NMapProjection mMapProjection;
    private NMapPlacemark mMapPlaceMark;
    private NMapOverlayItem mMapOverlayItem;
    private NMapPOIitem mMapPOIitem;



    private NMapMyLocationOverlay mMyLocationOverlay; //지도 위에 현재 위치를 표시하는 오버레이 클래스이며 NMapOverlay 클래스를 상속한다.

    NMapViewerResourceProvider mMapViewerResourceProvider = null; //NMapViewerResourceProvider 클래스 상속
    NMapOverlayManager mMapOverlayManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mMapView = new NMapView(this);
//        setContentView(R.layout.activity_main);
        setContentView(mMapView);
        checkPermission();

        super.setMapDataProviderListener(myOnProviderListener);

        mMapView.setClientId(CLIENT_ID);
        mMapView.setClickable(true);
        mMapView.setEnabled(true);
        mMapView.setFocusable(true);
        mMapView.setFocusableInTouchMode(true);
        mMapView.requestFocus();

        mMapViewerResourceProvider = new NMapViewerResourceProvider(this);
        mMapOverlayManager = new NMapOverlayManager(this, mMapView, mMapViewerResourceProvider);

//        mMyLocationOverlay = new NMapMyLocationOverlay(this, mMapView, mMapLocationManager, null, mMapViewerResourceProvider);


        mMapController = mMapView.getMapController();
        mMapProjection = mMapView.getMapProjection();
        Log.d(LOG_TAG, "지도 중심 좌표(위도) : " + mMapController.getMapCenter().latitude);
        Log.d(LOG_TAG, "지도 중심 좌표(경도) : " + mMapController.getMapCenter().longitude);
        mMapController.setZoomLevel(13);

        mMapLocationManager = new NMapLocationManager(this);
        mMapLocationManager.setOnLocationChangeListener(myOnLocationChangeListener);
        mMapLocationManager.enableMyLocation(true);



        mMyLocationOverlay = mMapOverlayManager.createMyLocationOverlay(mMapLocationManager, null);
    }


    private NMapLocationManager.OnLocationChangeListener myOnLocationChangeListener = new NMapLocationManager.OnLocationChangeListener() {
        @Override
        public boolean onLocationChanged(NMapLocationManager nMapLocationManager, NGeoPoint nGeoPoint) {

            Log.d(LOG_TAG, "위치 변화가 있습니다!");

            Log.d(LOG_TAG, "현재 위치(위도) nMapLocationManager : " + nMapLocationManager.getMyLocation().latitude);
            Log.d(LOG_TAG, "현재 위치(경도) nMapLocationManager : " + nMapLocationManager.getMyLocation().longitude);
            Log.d(LOG_TAG, "현재 위치(위도) nGeoPoint : " + nGeoPoint.latitude);
            Log.d(LOG_TAG, "현재 위치(경도) nGeoPoint : " + nGeoPoint.longitude);
//            mMapController.setMapCenter(nGeoPoint, 10);
            mMapController.animateTo(nGeoPoint);

//            NMapPOIdata poiData = new NMapPOIdata(1, mMapViewerResourceProvider);
//            poiData.beginPOIdata(1);
//            poiData.addPOIitem(nGeoPoint.latitude, nGeoPoint.longitude, "나", NMapPOIflagType.FROM, 0);
//            poiData.endPOIdata();
//            mMapOverlayManager.createPOIdataOverlay(poiData, null);
//
//            Drawable drawable = getResources().getDrawable(R.drawable.marker);


//            mMapPOIitem = new NMapPOIitem(nGeoPoint, "나", drawable, "" ,0);
//            mMapPOIitem.setFloatingMode(NMapPOIitem.FLOATING_FIXED);
//            mMapOverlayItem = new NMapOverlayItem(nGeoPoint, "나", "범민", drawable);
//            mMapOverlayItem.setVisibility(NMapOverlayItem.VISIBLE);
//            Log.d(LOG_TAG, "마커의 위치(위도) : " + mMapOverlayItem.getPoint().latitude);
//            Log.d(LOG_TAG, "마커의 위치(경도) : " + mMapOverlayItem.getPoint().longitude);
//            Log.d(LOG_TAG, "마커의 표시 여부 : " + mMapOverlayItem.getVisibility());
            return true; // true면 계속 현재 위치 탐색
        }

        @Override
        public void onLocationUpdateTimeout(NMapLocationManager nMapLocationManager) {

            Log.d(LOG_TAG, "정해진 시간 내에 현재 위치 탐색을 실패했습니다!");
        }

        @Override
        public void onLocationUnavailableArea(NMapLocationManager nMapLocationManager, NGeoPoint nGeoPoint) {

            Log.d(LOG_TAG, "현재 위치가 지도 상에 표시할 수 있는 범위를 벗어났습니다!!");
        }
    };

    private final OnDataProviderListener myOnProviderListener = new OnDataProviderListener() {
        @Override
        public void onReverseGeocoderResponse(NMapPlacemark nMapPlacemark, NMapError nMapError) {
            if(nMapPlacemark != null) {
                Toast.makeText(MainActivity.this, "현재주소는 " + nMapPlacemark.toString(),Toast.LENGTH_LONG).show();
            }
        }
    };

    void checkPermission() {
        if(Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            return;
        }
        for(int i = 0; i < permission_list.length; i++) {
            int chk = checkCallingOrSelfPermission(permission_list[i]);
            if(chk == PackageManager.PERMISSION_DENIED) {
                requestPermissions(permission_list, 0);
                break;
            }
        }
    }
}
