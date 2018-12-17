package com.teamnoname.streetartzone.Ticket;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.teamnoname.streetartzone.Data.TicketData;
import com.teamnoname.streetartzone.R;
import com.teamnoname.streetartzone.StreetGroup.TicketImage;
import com.teamnoname.streetartzone.Util.GlideApp;

import java.util.ArrayList;

import io.realm.Realm;
import io.realm.RealmResults;

public class TicketListActivity extends AppCompatActivity implements MyPagerAdapter.ItemClickListener {

    private ViewPager viewpagerTop, viewPagerBackground;
    public static final int ADAPTER_TYPE_TOP = 1;
    public static final int ADAPTER_TYPE_BOTTOM = 2;

    ArrayList<TicketData> arrayList_ticket;
    Realm realm;
    RealmResults<TicketData> realmResults_ticket;

    ImageButton btn_back;
    TextView tv_noDataMsg;


    public TicketListActivity() {
        realm = Realm.getDefaultInstance();
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ticketlist);

        btn_back = (ImageButton) findViewById(R.id.ticket_list_back);
        tv_noDataMsg = (TextView) findViewById(R.id.ticket_list_noDataMsg);

        btn_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });


        init();

    }

    @Override
    protected void onResume() {
        super.onResume();
        setData();
    }

    public void setData(){
        arrayList_ticket= new ArrayList<>();

        realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                realmResults_ticket = realm.where(TicketData.class).findAll();

                for(int i=0; i<realmResults_ticket.size() ; i++){
                    arrayList_ticket.add(realmResults_ticket.get(i));
                }

                if(arrayList_ticket.size()==0){
                    if(tv_noDataMsg.getVisibility()==View.GONE){
                        tv_noDataMsg.setVisibility(View.VISIBLE);
                    }
                }else{
                    setupViewPager();
                }



            }
        });

    }

    /**
     * Initialize all required variables
     */
    private void init() {
        viewpagerTop = (ViewPager) findViewById(R.id.viewpagerTop);
        viewPagerBackground = (ViewPager) findViewById(R.id.viewPagerbackground);

        viewpagerTop.setClipChildren(false);
        viewpagerTop.setPageMargin(getResources().getDimensionPixelOffset(R.dimen.pager_margin));
        viewpagerTop.setOffscreenPageLimit(3);
        viewpagerTop.setPageTransformer(false, new CarouselEffectTransformer(this)); // Set transformer
    }

    /**
     * Setup viewpager and it's events
     */
    private void setupViewPager() {
        // Set Top ViewPager Adapter
        MyPagerAdapter adapter = new MyPagerAdapter(this, arrayList_ticket, ADAPTER_TYPE_TOP);
        adapter.setItemClickListener(TicketListActivity.this);
        viewpagerTop.setAdapter(adapter);


        // Set Background ViewPager Adapter
        MyPagerAdapter adapterBackground = new MyPagerAdapter(this, arrayList_ticket, ADAPTER_TYPE_BOTTOM);
        viewPagerBackground.setAdapter(adapterBackground);


        viewpagerTop.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {

            private int index = 0;

            @Override
            public void onPageSelected(int position) {
                index = position;

            }

            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                int width = viewPagerBackground.getWidth();
                viewPagerBackground.scrollTo((int) (width * position + width * positionOffset), 0);
            }

            @Override
            public void onPageScrollStateChanged(int state) {
                if (state == ViewPager.SCROLL_STATE_IDLE) {
                    viewPagerBackground.setCurrentItem(index);
                }

            }
        });
    }

    @Override
    public void onItemClick(int position) {
        Intent intent = new Intent(TicketListActivity.this,TicketImage.class);
        intent.putExtra("filePath",arrayList_ticket.get(position).getTicketPath());     //티켓 이미지
        intent.putExtra("coverPath",arrayList_ticket.get(position).getCoverPath());     //티켓 배경 이미지
        intent.putExtra("isListing",true);                                         //접근 구분
        startActivity(intent);
    }
}


class MyPagerAdapter extends PagerAdapter {

    Context context;
    ArrayList<TicketData> arrayList_ticket;
    int adapterType;
    ItemClickListener itemClickListener;

    public void setItemClickListener(ItemClickListener itemClickListener) {
        this.itemClickListener = itemClickListener;
    }

    public MyPagerAdapter(Context context, ArrayList<TicketData> arrayList_ticket, int adapterType) {
        this.context = context;
        this.arrayList_ticket = arrayList_ticket;
        this.adapterType = adapterType;
    }

    @Override
    public Object instantiateItem(ViewGroup container, final int position) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_cover, null);
        try {

            LinearLayout linMain = (LinearLayout) view.findViewById(R.id.linMain);
            ImageView imageCover = (ImageView) view.findViewById(R.id.imageCover);
            linMain.setTag(position);

            switch (adapterType)
            {
                case TicketListActivity.ADAPTER_TYPE_TOP:
                    GlideApp.with(context)
                            .load(arrayList_ticket.get(position).getTicketPath())
                            .into(imageCover);

                    linMain.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            itemClickListener.onItemClick(position);
                        }
                    });

                    break;
                case TicketListActivity.ADAPTER_TYPE_BOTTOM:
                    linMain.setBackgroundResource(0);
                    GlideApp.with(context)
                            .load(arrayList_ticket.get(position).getCoverPath())
                            .into(imageCover);

                    break;
            }




            container.addView(view);

        } catch (Exception e) {
            e.printStackTrace();
        }

        return view;
    }

    public interface ItemClickListener{
        void onItemClick(int position);
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((View) object);
    }

    @Override
    public int getCount() {
        return arrayList_ticket.size();
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return (view == object);
    }

}



class CarouselEffectTransformer implements ViewPager.PageTransformer {

    private int maxTranslateOffsetX;
    private ViewPager viewPager;

    public CarouselEffectTransformer(Context context) {
        this.maxTranslateOffsetX = dp2px(context, 180);
    }

    public void transformPage(View view, float position) {
        if (viewPager == null) {
            viewPager = (ViewPager) view.getParent();
        }

        int leftInScreen = view.getLeft() - viewPager.getScrollX();
        int centerXInViewPager = leftInScreen + view.getMeasuredWidth() / 2;
        int offsetX = centerXInViewPager - viewPager.getMeasuredWidth() / 2;
        float offsetRate = (float) offsetX * 0.38f / viewPager.getMeasuredWidth();
        float scaleFactor = 1 - Math.abs(offsetRate);

        if (scaleFactor > 0) {
            view.setScaleX(scaleFactor);
            view.setScaleY(scaleFactor);
            view.setTranslationX(-maxTranslateOffsetX * offsetRate);
        }
        ViewCompat.setElevation(view, scaleFactor);

    }

    /**
     * Dp to pixel conversion
     */
    private int dp2px(Context context, float dipValue) {
        float m = context.getResources().getDisplayMetrics().density;
        return (int) (dipValue * m + 0.5f);
    }

}



