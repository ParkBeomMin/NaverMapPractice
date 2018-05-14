package com.steven.join_and_join.navermappractice;

import android.Manifest;
import android.content.ContentValues;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
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
import com.nhn.android.mapviewer.overlay.NMapPOIdataOverlay;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;

public class MainActivity extends NMapActivity {


    private String[] permission_list = { // 접근 권한 허용 리스트
            Manifest.permission.INTERNET,
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_WIFI_STATE,
            Manifest.permission.CHANGE_WIFI_STATE
    };


    private TextView addressTv;
    private Button addressBtn;

    private final String LOG_TAG = "NMapLog";

    //네이버 애플리케이션 클라이언트 ID, SECRET
    private final String CLIENT_ID = "";
    private final String CLIENT_SECRET = "";

    private NMapView mMapView; // 지도 데이터 표시 클래스
    private NMapLocationManager mMapLocationManager; // 단말기의 현재 위치 탐색 기능을 사용하기 위한 클래스
    private NMapController mMapController; // 지도 상태 변경하고 컨트롤하기 위한 클래스

//    private NMapProjection mMapProjection;
//    private NMapPlacemark mMapPlaceMark;
//    private NMapOverlayItem mMapOverlayItem;
//    private NMapPOIitem mMapPOIitem;



    private NMapMyLocationOverlay mMyLocationOverlay; //지도 위에 현재 위치를 표시하는 오버레이 클래스이며 NMapOverlay 클래스를 상속한다.

    NMapViewerResourceProvider mMapViewerResourceProvider = null; // NMapViewerResourceProvider 클래스 상속
    NMapOverlayManager mMapOverlayManager; // 지도 위에 표시되는 오버레이 객체를 관리.

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // mapview 가져오기
        mMapView = (NMapView)findViewById(R.id.map_view);

        // 접근 권한 체크 함수 실행
        checkPermission();

        // 주소를 보여줄 Tv와 주소 선택 Btn 가져오기
        addressTv = (TextView)findViewById(R.id.addresss_tv);
        addressBtn = (Button)findViewById(R.id.address_btn);

        // 데이터 프로바이더 리스너 등록
        super.setMapDataProviderListener(myOnProviderListener);

        // NMapView 초기화 셋팅
        mMapView.setClientId(CLIENT_ID);
        mMapView.setClickable(true);
        mMapView.setEnabled(true);
        mMapView.setFocusable(true);
        mMapView.setFocusableInTouchMode(true);
        mMapView.requestFocus();

        // map 상태 변화 리스너 등록
        mMapView.setOnMapStateChangeListener(onMapViewStateChangeListener);

        // 리소스 프로바이더 생성
        mMapViewerResourceProvider = new NMapViewerResourceProvider(this);

        // 오버레이 매니저 생성
        mMapOverlayManager = new NMapOverlayManager(this, mMapView, mMapViewerResourceProvider);

        // 맵 컨트롤러 생성
        mMapController = mMapView.getMapController();

//        mMapProjection = mMapView.getMapProjection();

        Log.d(LOG_TAG, "지도 중심 좌표(위도) : " + mMapController.getMapCenter().latitude);
        Log.d(LOG_TAG, "지도 중심 좌표(경도) : " + mMapController.getMapCenter().longitude);

        // 맵 컨트롤러로 초기 줌 레벨 설정(0 ~ 15)
        mMapController.setZoomLevel(13);

        // 맵 로케이션 매니저 생성
        mMapLocationManager = new NMapLocationManager(this);

        // 단말기의 현재 위치 상태 변경 시 호출되는 리스너 등록
        mMapLocationManager.setOnLocationChangeListener(myOnLocationChangeListener);

        // 위치 탐색 시작
        mMapLocationManager.enableMyLocation(true);


        // myLocationOverlay 객체 생성
        mMyLocationOverlay = mMapOverlayManager.createMyLocationOverlay(mMapLocationManager, null); // 현재 위치 마커 찍는 메소드 호출하려면..

    }


    private NMapLocationManager.OnLocationChangeListener myOnLocationChangeListener = new NMapLocationManager.OnLocationChangeListener() {
        @Override
        public boolean onLocationChanged(NMapLocationManager nMapLocationManager, NGeoPoint nGeoPoint) { // 위치 변화가 있으면 호출 된다.

            Log.d(LOG_TAG, "위치 변화가 있습니다!");

            Log.d(LOG_TAG, "현재 위치(위도) nMapLocationManager : " + nMapLocationManager.getMyLocation().latitude);
            Log.d(LOG_TAG, "현재 위치(경도) nMapLocationManager : " + nMapLocationManager.getMyLocation().longitude);
            Log.d(LOG_TAG, "현재 위치(위도) nGeoPoint : " + nGeoPoint.latitude);
            Log.d(LOG_TAG, "현재 위치(경도) nGeoPoint : " + nGeoPoint.longitude);
            mMapController.animateTo(nGeoPoint);
            return false; // true면 계속 현재 위치 탐색
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
        public void onReverseGeocoderResponse(NMapPlacemark nMapPlacemark, NMapError nMapError) { // 좌표를 주소로 변환하는 메소드
            ReverseGeoCodeTask reverseGeoCodeTask = new ReverseGeoCodeTask(nMapPlacemark, null); // 번지수까지 나타나게 하기 위해
            reverseGeoCodeTask.execute();
//            addressTv.setText((nMapPlacemark != null) ? nMapPlacemark.toString() : null); // 동까지만 나옴.
        }
    };

    private final NMapView.OnMapStateChangeListener onMapViewStateChangeListener = new NMapView.OnMapStateChangeListener() {
        @Override
        public void onMapInitHandler(NMapView nMapView, NMapError nMapError) {

        }

        @Override
        public void onMapCenterChange(NMapView nMapView, final NGeoPoint nGeoPoint) { // 맵 중앙 변화가 있을 때마다 호출
            Log.i(LOG_TAG, "onMapCenterChange: center= " + nGeoPoint.toString());
            MainActivity.super.findPlacemarkAtLocation(nGeoPoint.getLongitude(), nGeoPoint.getLatitude()); //좌표를 주소로 변환하는 서버 API호출

            Drawable drawable = getResources().getDrawable(R.drawable.marker);

            mMapOverlayManager.clearOverlays();
            int markerId = NMapPOIflagType.PIN;
            NMapPOIdata poiData = new NMapPOIdata(1, mMapViewerResourceProvider);
            poiData.beginPOIdata(1);
            poiData.addPOIitem(nGeoPoint.getLongitude(), nGeoPoint.getLatitude(), "", markerId, 0);
            poiData.endPOIdata();
            mMapOverlayManager.createPOIdataOverlay(poiData, null);


            addressBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast.makeText(MainActivity.this, addressTv.getText().toString()+"위치로 선택되었습니다. (" + nGeoPoint.toString() + ")", Toast.LENGTH_LONG).show();
                }
            });
        }

        @Override
        public void onMapCenterChangeFine(NMapView nMapView) {

        }

        @Override
        public void onZoomLevelChange(NMapView nMapView, int i) {

        }

        @Override
        public void onAnimationStateChange(NMapView nMapView, int i, int i1) {

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


    public class ReverseGeoCodeTask extends AsyncTask<Void, Void, String>{

        private NMapPlacemark nMapPlacemark;
        private ContentValues values;

        public ReverseGeoCodeTask(NMapPlacemark nMapPlacemark, ContentValues values){
            this.nMapPlacemark = nMapPlacemark;
            this.values = values;
        }

        @Override
        protected String doInBackground(Void... voids) {
            String apiURL = "https://openapi.naver.com/v1/map/reversegeocode?query=" + nMapPlacemark.longitude + "," + nMapPlacemark.latitude;
            StringBuffer response = null;
            try {
                URL url = new URL(apiURL);
                HttpURLConnection con = (HttpURLConnection) url.openConnection();
                con.setRequestMethod("GET");
                con.setRequestProperty("X-Naver-Client-Id", CLIENT_ID);
                con.setRequestProperty("X-Naver-Client-Secret", CLIENT_SECRET);
                int responseCode = con.getResponseCode();
                BufferedReader br;
                if (responseCode == 200) {
                    br = new BufferedReader((new InputStreamReader(con.getInputStream())));
                } else {
                    br = new BufferedReader(new InputStreamReader(con.getErrorStream()));
                }

                String inputLine;
                response = new StringBuffer();
                while ((inputLine = br.readLine()) != null) {
                    response.append(inputLine);
                }
                br.close();
                Log.d(LOG_TAG, "response : " + response.toString());
            } catch (IOException e) {
                e.printStackTrace();
            }
            return response.toString();
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            try {
                JSONObject jsonObject = new JSONObject(s);

                JSONObject jsonObject1 = new JSONObject(jsonObject.getString("result"));
                JSONArray jsonArray = new JSONArray(jsonObject1.getString("items"));
                for(int i = 0; i < jsonArray.length(); i++) {
                    JSONObject jsonObject2 = jsonArray.getJSONObject(i);
                    addressTv.setText(jsonObject2.getString("address"));
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }
}
