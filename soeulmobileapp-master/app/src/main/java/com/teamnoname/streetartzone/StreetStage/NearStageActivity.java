package com.teamnoname.streetartzone.StreetStage;

import android.Manifest;
import android.app.AlertDialog;
import android.app.FragmentManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.teamnoname.streetartzone.Data.StageInfo;
import com.teamnoname.streetartzone.R;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

import io.realm.Realm;
import io.realm.RealmResults;


public class NearStageActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap map;
    private LocationManager locationManager;
    private LocationListener locationListener;
    private LatLng userLocation;
    private Realm realm;
    private RealmResults<StageInfo> result_StageInfo;
    private RealmResults<StageInfo> result_StageInfoDistrict;

    private ImageView img_backBtn;
    private boolean showDialogTag = true;
    private AlertDialog dialog_gpsSetting;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_near_stage);

        realm = Realm.getDefaultInstance();
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.near_stage_map);
        mapFragment.getMapAsync(this);

        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        img_backBtn = (ImageView) findViewById(R.id.near_stage_img_backbtn);
        img_backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        result_StageInfo = realm.where(StageInfo.class).findAll();

    }

    @Override
    protected void onStart() {
        super.onStart();
        if (dialog_gpsSetting != null) dialog_gpsSetting.dismiss();
        getLocation();

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        map = googleMap;


        LatLng soeul = getLatLotFromAddress("대한민국 서울특별시");
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(soeul, 13f));
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            Toast.makeText(this, "위치 서비스 사용 권한을 설정해 주세요.", Toast.LENGTH_SHORT).show();
            return;
        }
        map.setMyLocationEnabled(true);


    }

    private void getLocation() {
        if (ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            Toast.makeText(this, "위치 서비스 사용 권한을 설정해 주세요.", Toast.LENGTH_SHORT).show();
            return;
        }
        //map.setMyLocationEnabled(true);
        locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                double longitude = location.getLongitude(); //경도
                double latitude = location.getLatitude();   //위도

                userLocation = new LatLng(latitude, longitude);
                map.moveCamera(CameraUpdateFactory.newLatLngZoom(userLocation, 13f));
                String address = getAddressFromLatLng();
                //String address = "대한민국 서울특별시 마포구";
                if (address == null) {
                    Toast.makeText(NearStageActivity.this, "GPS 신호가 약합니다. 잠시후 다시 시도해 주세요", Toast.LENGTH_SHORT).show();
                    return;
                }
                String district = address.split(" ")[2];
                result_StageInfoDistrict = getDistrictStageData(district);

                if (result_StageInfoDistrict.size() > 0) {
                    addMapMarker(result_StageInfoDistrict.size());
                } else {
                    Toast.makeText(NearStageActivity.this, "주변에 공연장이 없습니다.", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {

            }

            @Override
            public void onProviderEnabled(String provider) {

            }

            @Override
            public void onProviderDisabled(String provider) {
                //Toast.makeText(NearStageActivity.this,"GPS 신호가 약합니다. 잠시후 다시 시도해 주세요",Toast.LENGTH_SHORT).show();

            }
        };


        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,
                100,
                500,
                locationListener);

        locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER,
                100,
                500,
                locationListener);

    }

    private RealmResults<StageInfo> getDistrictStageData(String district) {
        return result_StageInfo.where().equalTo("district", district).findAll();
    }

    //위도,경도로부터 주소추출
    private String getAddressFromLatLng() {
        Geocoder geocoder = new Geocoder(this, Locale.KOREA);
        List<Address> addresses;
        String address = null;

        try {
            addresses = geocoder.getFromLocation(userLocation.latitude, userLocation.longitude, 1);
            address = addresses.get(0).getAddressLine(0).toString();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return address;
    }

    //주소로부터 위도, 경도 추출
    private LatLng getLatLotFromAddress(String address) {
        Geocoder geocoder = new Geocoder(this);
        LatLng getLatLng = null;
        try {
            Address location = geocoder.getFromLocationName(address, 1).get(0);
            if (location != null) {
                getLatLng = new LatLng(location.getLatitude(), location.getLongitude());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return getLatLng;
    }

    private void addMapMarker(int size) {
        LatLng latLng = null;
        for (int i = 0; i < size; i++) {
            StageInfo info = result_StageInfoDistrict.get(i);
            latLng = getLatLotFromAddress(info.getAddress());
            MarkerOptions options = new MarkerOptions()
                    .position(latLng)
                    .title(info.getAddress());
            map.addMarker(options);
        }


    }

    void checkGPS() {
        if (locationManager != null && showDialogTag) {
            if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                showDialogTag = false;
                View customView = getLayoutInflater().inflate(R.layout.dialog_gps_setting, null);
                Button btn_setting = (Button) customView.findViewById(R.id.gps_setting_btn_gosetting);
                dialog_gpsSetting = new AlertDialog.Builder(this)
                        .setView(customView)
                        .setOnDismissListener(new DialogInterface.OnDismissListener() {
                            @Override
                            public void onDismiss(DialogInterface dialog) {
                                if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER))
                                    Toast.makeText(NearStageActivity.this, "GPS기능이 꺼져 있어 주변 공연장 정보를 받아 올 수 없습니다.", Toast.LENGTH_SHORT).show();
                            }
                        })
                        .show();
                btn_setting.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                        intent.addCategory(Intent.CATEGORY_DEFAULT);
                        startActivity(intent);
                        //dialog_gpsSetting.dismiss();
                    }
                });
            }
        }
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (hasFocus) checkGPS();
    }

    @Override
    protected void onPause() {
        super.onPause();

        if (locationManager != null && locationListener != null)
            locationManager.removeUpdates(locationListener);
    }
}
