package com.teamnoname.streetartzone;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.teamnoname.streetartzone.Data.Contest;
import com.teamnoname.streetartzone.Data.GroupData;
import com.teamnoname.streetartzone.Data.GroupReviewData;
import com.teamnoname.streetartzone.Data.GroupReviewDataItem;
import com.teamnoname.streetartzone.Data.StageInfo;

import io.realm.Realm;
import io.realm.RealmResults;
import io.realm.annotations.PrimaryKey;

public class SplashActivity extends AppCompatActivity {

    boolean isDataLoadingEnd = false;                             //데이터 로딩 완료 체크

    //Local database
    private Realm realm;
    RealmResults<GroupData> realmResults_GroupData;               //공연팀 데이터
    RealmResults<GroupReviewData> realmResults_GroupReviewData;   //공연팀 리뷰 데이터
    int reviewCount = 0;                                          //공연팀 리뷰 데이터 개수
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor preferenceEditor;

    //Firebase
    DatabaseReference dbReference;
    FirebaseDatabase firebaseDatabase;

    //Permission
    final int permissionRequestCodeForStorageW = 1000;
    final int permissionRequestCodeForStorageR = 1001;
    final int permissionRequestCodeForMap = 1002;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        realm = Realm.getDefaultInstance();
        sharedPreferences = getSharedPreferences("GET_STAGE", MODE_PRIVATE);
        preferenceEditor = sharedPreferences.edit();

        checkPermissions();

    }


    public boolean isNetworkConnected(){
        ConnectivityManager cm = (ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);

        return cm.getActiveNetworkInfo() != null;
    }


    private void checkPermissions() {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) { //API level 구분

            //마시멜로 이상인 경우

            if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                //위치정보 권한
                ActivityCompat.requestPermissions(SplashActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, permissionRequestCodeForMap);

            } else if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                //저장소 권한
                ActivityCompat.requestPermissions(SplashActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, permissionRequestCodeForStorageW);

            } else if(ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){
                //저장소 권한
                ActivityCompat.requestPermissions(SplashActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, permissionRequestCodeForStorageR);


            } else {
                //권한 모두 획득시

                setFirebaseData();

                Handler h = new Handler();
                h.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        goMainActivity();
                    }
                }, 3000);

            }

        } else {
            //마시멜로 미만 버전 ( 권한 요청 따로 구분 x)

            setFirebaseData();

            Handler h = new Handler();
            h.postDelayed(new Runnable() {
                @Override
                public void run() {
                    goMainActivity();
                }
            }, 3000);

        }

    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        switch (requestCode) {

            case permissionRequestCodeForMap:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    //권한 허용 (다시 권한 체크 로직 -> 모두 완료되면 다음 이벤트 실행)
                    checkPermissions();

                } else {

                    //권한 거절 시
                    finish();
                    Toast.makeText(this, "권한을 허용하셔야 서비스 이용이 가능합니다.", Toast.LENGTH_SHORT).show();
                }

                break;

            case permissionRequestCodeForStorageW:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    //권한 허용 (다시 권한 체크 로직 -> 모두 완료되면 다음 이벤트 실행)
                    checkPermissions();

                } else {

                    //권한 거절 시
                    finish();
                    Toast.makeText(this, "권한을 허용하셔야 서비스 이용이 가능합니다.", Toast.LENGTH_SHORT).show();
                }

                break;

            case permissionRequestCodeForStorageR:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    //권한 허용 (다시 권한 체크 로직 -> 모두 완료되면 다음 이벤트 실행)
                    checkPermissions();

                } else {

                    //권한 거절 시
                    finish();
                    Toast.makeText(this, "권한을 허용하셔야 서비스 이용이 가능합니다.", Toast.LENGTH_SHORT).show();
                }

                break;

        }

    }


    public boolean checkGroupData() {
        realmResults_GroupData = realm.where(GroupData.class).findAll();

        if (realmResults_GroupData == null || realmResults_GroupData.size() == 0) {

            return false;
        } else {
            return true;
        }
    }


    public void setFirebaseData() {

        firebaseDatabase = FirebaseDatabase.getInstance();
        dbReference = firebaseDatabase.getReference();

        dbReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull final DataSnapshot dataSnapshot) {

                realm.executeTransaction(new Realm.Transaction() {
                    @Override
                    public void execute(Realm realm) {

                        //거리 공연단 데이터
                        if (!checkGroupData()) {

                            int countSeq = 0;
                            for (DataSnapshot groupDB : dataSnapshot.child("groupdata/group").getChildren()) {
                                GroupDataItem groupDataItem = groupDB.getValue(GroupDataItem.class);
                                GroupData groupData = new GroupData();

                                groupData.setGroup_seq(countSeq);
                                groupData.setGroup_name(groupDataItem.getName());
                                groupData.setGroup_genre(groupDataItem.getGenre());
                                groupData.setGroup_titleImg(groupDataItem.getImage());
                                groupData.setGroup_info(groupDataItem.getIntro());
                                groupData.setGroup_youtube(groupDataItem.getYoutube());

                                realm.copyToRealm(groupData);

                                countSeq++;

                            }
                        }

                        realmResults_GroupReviewData = realm.where(GroupReviewData.class).findAll();


                        //거리 공연단 리뷰 데이터
                        if (realmResults_GroupReviewData.size() < dataSnapshot.child("groupdata/groupreview").getChildrenCount()) {
                            reviewCount = 0;
                            for (DataSnapshot groupReviewDB : dataSnapshot.child("groupdata/groupreview").getChildren()) {

                                reviewCount += 1;

                                if (realmResults_GroupReviewData.size() < reviewCount) {
                                    GroupReviewDataItem groupReviewDataItem = groupReviewDB.getValue(GroupReviewDataItem.class);
                                    GroupReviewData groupReviewData = new GroupReviewData();

                                    groupReviewData.setSeq(groupReviewDataItem.getSeq());
                                    groupReviewData.setscore(groupReviewDataItem.getScore());
                                    groupReviewData.setContents(groupReviewDataItem.getContents());
                                    groupReviewData.setWriter(groupReviewDataItem.getWriter());
                                    groupReviewData.setDate(groupReviewDataItem.getDate());

                                    realm.copyToRealm(groupReviewData);
                                }

                            }
                        }

                        //공연일정 데이터
                        if (!sharedPreferences.getBoolean("isContestData", false)) {

                            for (DataSnapshot contestDB : dataSnapshot.child("contestdata").getChildren()) {

                                ContestDataItem contestDataItem = contestDB.getValue(ContestDataItem.class);
                                String[] devider = contestDataItem.getDate().split("-");
                                int dateForSort= Integer.parseInt(devider[0]+devider[1]+devider[2]);
                                String month = devider[1];
                                Contest contest = new Contest(
                                        contestDataItem.getNum(),
                                        contestDataItem.getTeamname(),
                                        contestDataItem.getDistrict(),
                                        contestDataItem.getArea(),
                                        contestDataItem.getDate(),
                                        contestDataItem.getTime(),
                                        month,dateForSort);

                                realm.copyToRealm(contest);

                            }

                            preferenceEditor.putBoolean("isContestData", true);
                            preferenceEditor.commit();
                        }


                        //공연장소 데이터
                        if (!sharedPreferences.getBoolean("isStageData", false)) {

                            for (DataSnapshot stageDB : dataSnapshot.child("stagedata").getChildren()) {

                                StageInfoItem stageInfo = stageDB.getValue(StageInfoItem.class);

                                StageInfo info = new StageInfo();
                                info.setSeq(stageInfo.getSeq());
                                info.setAddress(stageInfo.getAddress());
                                info.setDistrict(stageInfo.getDistrict());
                                info.setPlaceName(stageInfo.getPlaceName());
                                realm.copyToRealm(info);
                            }

                            preferenceEditor.putBoolean("isStageData", true);
                            preferenceEditor.commit();
                        }

                        //데이터 로딩 완료 체크
                        isDataLoadingEnd = true;

                    }
                });


            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }


    public void goMainActivity() {

        if (isDataLoadingEnd) {
            Intent go_MainActivity = new Intent(SplashActivity.this, MainActivity.class);
            startActivity(go_MainActivity);
            finish();

        } else {

            Handler h = new Handler();
            h.postDelayed(new Runnable() {
                @Override
                public void run() {
                    if(isNetworkConnected()){
                        goMainActivity();
                    }else{
                        Toast.makeText(SplashActivity.this, "인터넷 상태를 확인해주세요", Toast.LENGTH_LONG).show();
                        finish();
                    }

                }
            }, 1500);
        }
    }
}



class GroupDataItem {

    String name;
    String genre;
    String intro;
    String image;
    String youtube;

    public GroupDataItem() {
    }

    public GroupDataItem(String name, String genre, String intro, String image, String youtube) {
        this.name = name;
        this.genre = genre;
        this.intro = intro;
        this.image = image;
        this.youtube = youtube;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getGenre() {
        return genre;
    }

    public void setGenre(String genre) {
        this.genre = genre;
    }

    public String getIntro() {
        return intro;
    }

    public void setIntro(String intro) {
        this.intro = intro;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getYoutube() {
        return youtube;
    }

    public void setYoutube(String youtube) {
        this.youtube = youtube;
    }
}


class ContestDataItem {
    @PrimaryKey
    int num;
    String teamname;
    String district;
    String area;
    String date;
    String time;

    public ContestDataItem() {
    }

    public int getNum() {
        return num;
    }

    public void setNum(int num) {
        this.num = num;
    }

    public String getTeamname() {
        return teamname;
    }

    public void setTeamname(String teamname) {
        this.teamname = teamname;
    }

    public String getDistrict() {
        return district;
    }

    public void setDistrict(String district) {
        this.district = district;
    }

    public String getArea() {
        return area;
    }

    public void setArea(String area) {
        this.area = area;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }
}

class StageInfoItem {

    @PrimaryKey
    int seq;
    String district;
    String address;
    String placeName;

    public StageInfoItem() {
    }

    public StageInfoItem(int seq, String district, String address, String placeName) {
        this.seq = seq;
        this.district = district;
        this.address = address;
        this.placeName = placeName;
    }

    public int getSeq() {
        return seq;
    }

    public void setSeq(int seq) {
        this.seq = seq;
    }

    public String getDistrict() {
        return district;
    }

    public void setDistrict(String district) {
        this.district = district;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getPlaceName() {
        return placeName;
    }

    public void setPlaceName(String placeName) {
        this.placeName = placeName;
    }
}