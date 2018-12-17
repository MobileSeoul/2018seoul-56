package com.teamnoname.streetartzone.StreetGroup;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.NotificationCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.teamnoname.streetartzone.Data.GroupData;
import com.teamnoname.streetartzone.Data.UserBookMarkGroup;
import com.teamnoname.streetartzone.R;
import com.teamnoname.streetartzone.SoeulApp;

import java.util.Date;

import io.realm.Realm;
import io.realm.RealmResults;

public class StreetGroupDetailActivity extends AppCompatActivity {

    //뷰
    FragmentAdapter frag_adapter;
    ViewPager viewPager;
    TabLayout tabLayout;
    ImageButton btn_back;
    ImageButton btn_bookmark;
    TextView tv_title;
    public static String groupName;

    //DB
    Realm realm;
    RealmResults<GroupData> groupData;

    public static int selectedSeq;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_streetgroup_detail);

        Intent intent = getIntent();
        selectedSeq = intent.getExtras().getInt("seq");

        frag_adapter = new FragmentAdapter(getSupportFragmentManager());
        viewPager = (ViewPager) findViewById(R.id.group_detail_viewpager);
        viewPager.setAdapter(frag_adapter);
        tabLayout = (TabLayout) findViewById(R.id.group_detail_tab);
        tabLayout.setupWithViewPager(viewPager);
        tv_title = (TextView) findViewById(R.id.group_detail_title);

        btn_back = (ImageButton) findViewById(R.id.group_detail_btn_back);
        btn_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                StreetGroupDetailActivity.this.finish();
            }
        });


        realm = Realm.getDefaultInstance();
        realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                groupData = realm.where(GroupData.class).equalTo("group_seq", selectedSeq).findAll();
                tv_title.setText(groupData.get(0).getGroup_name());
                groupName = groupData.get(0).getGroup_name();

            }
        });

        //북마크 관련
        btn_bookmark = (ImageButton) findViewById(R.id.group_detail_img_bookmark);

        if(realm.where(UserBookMarkGroup.class).equalTo("groupSeq", selectedSeq).findFirst()!=null)
            btn_bookmark.setImageDrawable(getDrawable(R.drawable.full_bookmark));
        else
            btn_bookmark.setImageDrawable(getDrawable(R.drawable.bookmark_icon));
        btn_bookmark.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btn_bookmark.setImageDrawable(getDrawable(R.drawable.full_bookmark));

                if (realm.where(UserBookMarkGroup.class).equalTo("groupSeq", selectedSeq).findFirst() == null) {
                    //즐찾 테이블에 해당 그룹 시퀀스가 없으면 저장.
                    realm.executeTransaction(new Realm.Transaction() {
                        @Override
                        public void execute(Realm realm) {
                            long now = System.currentTimeMillis();
                            UserBookMarkGroup group = new UserBookMarkGroup();
                            group.setGroupSeq(selectedSeq);
                            group.setAddDate(new Date(now));
                            realm.copyToRealm(group);
                        }
                    });
                } else {
                    //있으면 추가된 팀이라고 알림
                    Toast.makeText(StreetGroupDetailActivity.this, "이미 북마크에 추가된 공연단 입니다.", Toast.LENGTH_SHORT).show();
                    return;
                }

                GroupData bookMarkGroup = realm.where(GroupData.class)
                        .equalTo("group_seq", selectedSeq).findFirst();

                //노티피케이션 띄우기 : 오레오이상 버전에서는 반드시 channel사용

                Bitmap largeIcon = BitmapFactory.decodeResource(getResources(), R.drawable.soeul_app_icon);
                NotificationManager manager =
                        (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
                NotificationCompat.Builder builder;

                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                    builder = new NotificationCompat.Builder(StreetGroupDetailActivity.this, SoeulApp.getChannelID());
                } else {
                    builder = new NotificationCompat.Builder(StreetGroupDetailActivity.this);
                }

                Intent dummyIntent = new Intent();
                PendingIntent pIntent = PendingIntent.getBroadcast(StreetGroupDetailActivity.this,
                        0,
                        dummyIntent,
                        PendingIntent.FLAG_UPDATE_CURRENT);
                builder
                        .setSmallIcon(R.drawable.soeul_app_icon)
                        .setContentText("서울 버스커")
                        .setContentText(bookMarkGroup.getGroup_name() +
                                " 팀이 북마크에 추가 되었습니다.")
                        .setDefaults(Notification.DEFAULT_ALL)
                        .setAutoCancel(true)
                        .setOnlyAlertOnce(true)
                        .setContentIntent(pIntent)
                        .setLargeIcon(largeIcon)
                        .setPriority(Notification.PRIORITY_HIGH);


                manager.notify(selectedSeq, builder.build());


            }
        });
        //북마크 관련 끝
    }
}


class FragmentAdapter extends FragmentPagerAdapter {

    int fragmentPageNumber = 3;

    public FragmentAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        //페이지 호출
        switch (position) {

            case 0:
                return InfoFragment.newInstance();

            case 1:
                return ScheduleFragment.newInstance();

            case 2:
                return ReviewFragment.newInstance();

        }

        return null;
    }

    @Override
    public int getCount() {
        return fragmentPageNumber;
    }


    @Override
    public CharSequence getPageTitle(int position) {

        switch (position) {
            case 0:
                return "소개";

            case 1:
                return "공연 일정";

            case 2:
                return "공연 후기";

        }

        return null;

    }


}
