package com.teamnoname.streetartzone;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.view.PagerAdapter;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.facebook.stetho.Stetho;
import com.teamnoname.streetartzone.Schedule.Schedule;
import com.teamnoname.streetartzone.StreetGroup.StreetGroupAcitivty;
import com.teamnoname.streetartzone.StreetGroup.UserBookmarkGroupsActivity;
import com.teamnoname.streetartzone.StreetStage.NearStageActivity;
import com.teamnoname.streetartzone.StreetStage.StreetStageAcitivity;
import com.teamnoname.streetartzone.Ticket.TicketListActivity;

import java.util.ArrayList;

import cn.trinea.android.view.autoscrollviewpager.AutoScrollViewPager;

public class MainActivity extends AppCompatActivity {

    AutoScrollViewPager autoScrollViewPager;
    MainBannerAdapter mainBannerAdapter;
    ArrayList<Integer> arrayList_banner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Stetho.initializeWithDefaults(MainActivity.this);

        setBannerData();
        setBannerViewPager();


    }

    public void setBannerData() {
        arrayList_banner = new ArrayList<>();
        arrayList_banner.add(R.drawable.home_banner1);
        arrayList_banner.add(R.drawable.banner2);
        arrayList_banner.add(R.drawable.home_banner3);
    }


    public void setBannerViewPager(){
        autoScrollViewPager = (AutoScrollViewPager) findViewById(R.id.main_bannerViewPager);
        mainBannerAdapter = new MainBannerAdapter(arrayList_banner,MainActivity.this);
        autoScrollViewPager.setAdapter(mainBannerAdapter);
        autoScrollViewPager.setInterval(3000);
        autoScrollViewPager.startAutoScroll();
    }


    public void setMainBtn(View view) {

        switch (view.getId()){

            case R.id.main_btn_group :
                //공연팀 버튼
                startActivity(new Intent(MainActivity.this, StreetGroupAcitivty.class));

                break;

            case R.id.main_btn_schedule :
                //공연일정 버튼
                startActivity(new Intent(MainActivity.this,Schedule.class));
                break;


            case R.id.main_btn_stage :
                //공연 장소 버튼
                startActivity(new Intent(MainActivity.this, StreetStageAcitivity.class));


                break;

            case R.id.main_btn_ticket :
                //내티켓 버튼
                startActivity(new Intent(MainActivity.this, TicketListActivity.class));

                break;


            case R.id.main_bookmark_but :
                //북마크
                startActivity(new Intent(MainActivity.this, UserBookmarkGroupsActivity.class));
                break;


            case R.id.main_near_stage_btn:
                //근처 공연장
                startActivity(new Intent(MainActivity.this, NearStageActivity.class));
                break;

        }

    }
}

class MainBannerAdapter extends PagerAdapter {

    ArrayList<Integer> arrayList_banner;
    Context context;

    public MainBannerAdapter(ArrayList<Integer> arrayList_banner, Context context) {
        this.arrayList_banner = arrayList_banner;
        this.context = context;

    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {

        LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = layoutInflater.inflate(R.layout.main_banner_item,null);
        ImageView imgV_banner = (ImageView) view.findViewById(R.id.main_banner_img);
        Glide.with(context).load(arrayList_banner.get(position)).into(imgV_banner);
        container.addView(view);

        return view;
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        container.removeView((View)object);
    }

    @Override
    public int getCount() {
        return arrayList_banner.size();
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return view==object;
    }
}

