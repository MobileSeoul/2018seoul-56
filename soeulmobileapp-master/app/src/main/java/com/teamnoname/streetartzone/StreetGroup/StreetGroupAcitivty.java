package com.teamnoname.streetartzone.StreetGroup;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.teamnoname.streetartzone.Data.GroupData;
import com.teamnoname.streetartzone.Data.GroupReviewData;
import com.teamnoname.streetartzone.R;

import java.util.ArrayList;

import io.realm.Realm;
import io.realm.RealmResults;

public class StreetGroupAcitivty extends AppCompatActivity implements StreetGroupRecyclerViewAdapter.RecyclerViewItemClickListener {

    //ListView
    RecyclerView recyclerView_group;
    StreetGroupRecyclerViewAdapter recyclerViewAdapter;
    ArrayList<StreetGroupItem> arrayList_groupInfo;
    ArrayList<StreetGroupItem> arrayList_groupinfo_instrument;
    ArrayList<StreetGroupItem> arrayList_groupinfo_performance;
    ArrayList<StreetGroupItem> arrayList_groupinfo_tradition;
    ArrayList<StreetGroupItem> arrayList_groupinfo_music;

    String selectedCategory = "전체";

    //UI View
    ArrayList<TextView> arrayList_bar;
    TextView bar_category_all;
    TextView bar_category_instrument;
    TextView bar_category_performance;
    TextView bar_category_tradition;
    TextView bar_category_music;

    ImageButton btn_search;
    ImageButton btn_back;


    //DataBases
    ArrayList<Integer> arrayList_ReviewCount;
    ArrayList<Integer> arrayList_ReviewScore;
    Realm realm;
    RealmResults<GroupData> realmResults_group;
    RealmResults<GroupReviewData> realmResults_groupReview;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_streetgroup);


        init();


    }

    @Override
    protected void onResume() {

        super.onResume();
        setGroupData();
    }

    public void init() {
        recyclerView_group = (RecyclerView) findViewById(R.id.group_recyclerView);
        bar_category_all = (TextView) findViewById(R.id.group_bar_all);
        bar_category_instrument = (TextView) findViewById(R.id.group_bar_instrument);
        bar_category_performance = (TextView) findViewById(R.id.group_bar_performance);
        bar_category_tradition = (TextView) findViewById(R.id.group_bar_tradition);
        bar_category_music = (TextView) findViewById(R.id.group_bar_music);

        btn_search = (ImageButton) findViewById(R.id.group_main_btn_search);
        btn_search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showSearchDialog();
            }
        });

        btn_back = (ImageButton) findViewById(R.id.group_main_btn_back);

        btn_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                StreetGroupAcitivty.this.finish();
            }
        });


        arrayList_bar = new ArrayList<>();
        arrayList_bar.add(bar_category_all);
        arrayList_bar.add(bar_category_instrument);
        arrayList_bar.add(bar_category_performance);
        arrayList_bar.add(bar_category_tradition);
        arrayList_bar.add(bar_category_music);

        setCategoryBar(0);


    }

    public void showSearchDialog() {
        final EditText edit_search = new EditText(this);

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("거리공연단 검색");
        builder.setView(edit_search);
        builder.setPositiveButton("검색",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        String searchStr = edit_search.getText().toString();

                        boolean isFinding = false;
                        for (int i = 0; i < arrayList_groupInfo.size(); i++) {
                            if (arrayList_groupInfo.get(i).getGroup_name().equalsIgnoreCase(searchStr)) {
                                ArrayList<StreetGroupItem> arrayList_temp = new ArrayList<>();
                                arrayList_temp.add(new StreetGroupItem(
                                        arrayList_groupInfo.get(i).getGroup_seq(),
                                        arrayList_groupInfo.get(i).getGroup_img(),
                                        arrayList_groupInfo.get(i).getGroup_name(),
                                        arrayList_groupInfo.get(i).getGroup_category(),
                                        arrayList_groupInfo.get(i).getGroup_detail(),
                                        arrayList_groupInfo.get(i).getGroup_score(),
                                        arrayList_groupInfo.get(i).getGroup_reviewNumber()

                                ));

                                recyclerViewAdapter = new StreetGroupRecyclerViewAdapter(StreetGroupAcitivty.this, arrayList_temp, StreetGroupAcitivty.this);
                                recyclerView_group.setLayoutManager(new LinearLayoutManager(StreetGroupAcitivty.this));
                                recyclerView_group.setAdapter(recyclerViewAdapter);

                                isFinding = true;
                                break;
                            }
                        }

                        if (!isFinding) {
                            Toast.makeText(StreetGroupAcitivty.this, "검색결과가 없습니다.", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
        builder.setNegativeButton("취소",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });
        builder.show();
    }


    public void setGroupData() {

        //database
        realm = Realm.getDefaultInstance();
        realmResults_group = realm.where(GroupData.class).findAll();
        realmResults_groupReview = realm.where(GroupReviewData.class).findAll();

        arrayList_ReviewCount = new ArrayList<>();
        arrayList_ReviewScore = new ArrayList<>();

        arrayList_groupInfo = new ArrayList<>();
        arrayList_groupinfo_instrument = new ArrayList<>();
        arrayList_groupinfo_performance = new ArrayList<>();
        arrayList_groupinfo_tradition = new ArrayList<>();
        arrayList_groupinfo_music = new ArrayList<>();

        //리뷰 데이터 초기화
        for(int i=0; i<realmResults_group.size(); i++){
            arrayList_ReviewCount.add(0);
            arrayList_ReviewScore.add(0);
        }

        //리뷰 개수 및 평가점수
        for (int i = 0; i < realmResults_groupReview.size(); i++) {
            GroupReviewData groupReviewData = realmResults_groupReview.get(i);
            Log.v("리뷰확인1",""+groupReviewData.getSeq());
            int tempReviewCount = arrayList_ReviewCount.get(groupReviewData.getSeq());
            arrayList_ReviewCount.set(groupReviewData.getSeq(),tempReviewCount+1);
            Log.v("리뷰확인2",""+tempReviewCount);


            int temReviewScore  = arrayList_ReviewScore.get(groupReviewData.getSeq());
            arrayList_ReviewScore.set(groupReviewData.getSeq(),temReviewScore+groupReviewData.getscore());

            Log.v("리뷰확인3",""+temReviewScore);

        }

        for (int i = 0; i < realmResults_group.size(); i++) {
            GroupData groupData = realmResults_group.get(i);
            arrayList_groupInfo.add(new StreetGroupItem(
                    groupData.getGroup_seq(),
                    groupData.getGroup_titleImg(),
                    groupData.getGroup_name(),
                    groupData.getGroup_genre(),
                    groupData.getGroup_info(),
                    arrayList_ReviewScore.get(i),
                    arrayList_ReviewCount.get(i)
            ));

            if (groupData.getGroup_genre().equals("기악")) {
                arrayList_groupinfo_instrument.add(new StreetGroupItem(
                        groupData.getGroup_seq(),
                        groupData.getGroup_titleImg(),
                        groupData.getGroup_name(),
                        groupData.getGroup_genre(),
                        groupData.getGroup_info(),
                        arrayList_ReviewScore.get(i),
                        arrayList_ReviewCount.get(i)
                ));

            } else if (groupData.getGroup_genre().equals("퍼포먼스")) {
                arrayList_groupinfo_performance.add(new StreetGroupItem(
                        groupData.getGroup_seq(),
                        groupData.getGroup_titleImg(),
                        groupData.getGroup_name(),
                        groupData.getGroup_genre(),
                        groupData.getGroup_info(),
                        arrayList_ReviewScore.get(i),
                        arrayList_ReviewCount.get(i)
                ));

            } else if (groupData.getGroup_genre().equals("전통")) {
                arrayList_groupinfo_tradition.add(new StreetGroupItem(
                        groupData.getGroup_seq(),
                        groupData.getGroup_titleImg(),
                        groupData.getGroup_name(),
                        groupData.getGroup_genre(),
                        groupData.getGroup_info(),
                        arrayList_ReviewScore.get(i),
                        arrayList_ReviewCount.get(i)
                ));


            } else if (groupData.getGroup_genre().equals("음악")) {
                arrayList_groupinfo_music.add(new StreetGroupItem(
                        groupData.getGroup_seq(),
                        groupData.getGroup_titleImg(),
                        groupData.getGroup_name(),
                        groupData.getGroup_genre(),
                        groupData.getGroup_info(),
                        arrayList_ReviewScore.get(i),
                        arrayList_ReviewCount.get(i)
                ));

            }

        }

        setRecyclerViewNotify(selectedCategory);

    }

    public void setGroupRecyclerView() {


        recyclerViewAdapter = new StreetGroupRecyclerViewAdapter(StreetGroupAcitivty.this, arrayList_groupInfo, StreetGroupAcitivty.this);
        recyclerView_group.setLayoutManager(new LinearLayoutManager(StreetGroupAcitivty.this));
        recyclerView_group.setAdapter(recyclerViewAdapter);


    }


    @Override
    public void setOnItemClick(int selectedPosition) {

        Intent intent = new Intent(StreetGroupAcitivty.this, StreetGroupDetailActivity.class);
        intent.putExtra("seq", selectedPosition);
        startActivity(intent);


    }

    public void setCategoryBar(int order) {
        //전체 , 기악, 퍼포먼스, 전통, 음악 순
        for (int i = 0; i < 5; i++) {

            if (i == order) {
                if (arrayList_bar.get(i).getVisibility() == View.INVISIBLE) {
                    arrayList_bar.get(i).setVisibility(View.VISIBLE);
                }

            } else {
                if (arrayList_bar.get(i).getVisibility() == View.VISIBLE) {
                    arrayList_bar.get(i).setVisibility(View.INVISIBLE);
                }

            }
        }


    }

    public void setRecyclerViewNotify(String category) {

        switch (category) {
            case "전체":
                recyclerViewAdapter = new StreetGroupRecyclerViewAdapter(StreetGroupAcitivty.this, arrayList_groupInfo, StreetGroupAcitivty.this);
                recyclerView_group.setLayoutManager(new LinearLayoutManager(StreetGroupAcitivty.this));
                recyclerView_group.setAdapter(recyclerViewAdapter);


                break;

            case "기악":
                recyclerViewAdapter = new StreetGroupRecyclerViewAdapter(StreetGroupAcitivty.this, arrayList_groupinfo_instrument, StreetGroupAcitivty.this);
                recyclerView_group.setLayoutManager(new LinearLayoutManager(StreetGroupAcitivty.this));
                recyclerView_group.setAdapter(recyclerViewAdapter);


                break;
            case "퍼포먼스":
                recyclerViewAdapter = new StreetGroupRecyclerViewAdapter(StreetGroupAcitivty.this, arrayList_groupinfo_performance, StreetGroupAcitivty.this);
                recyclerView_group.setLayoutManager(new LinearLayoutManager(StreetGroupAcitivty.this));
                recyclerView_group.setAdapter(recyclerViewAdapter);


                break;
            case "전통":
                recyclerViewAdapter = new StreetGroupRecyclerViewAdapter(StreetGroupAcitivty.this, arrayList_groupinfo_tradition, StreetGroupAcitivty.this);
                recyclerView_group.setLayoutManager(new LinearLayoutManager(StreetGroupAcitivty.this));
                recyclerView_group.setAdapter(recyclerViewAdapter);


                break;

            case "음악":
                recyclerViewAdapter = new StreetGroupRecyclerViewAdapter(StreetGroupAcitivty.this, arrayList_groupinfo_music, StreetGroupAcitivty.this);
                recyclerView_group.setLayoutManager(new LinearLayoutManager(StreetGroupAcitivty.this));
                recyclerView_group.setAdapter(recyclerViewAdapter);


                break;

        }


    }

    //카레고리 클릭
    public void setCategoryClick(View view) {

        switch (view.getId()) {
            case R.id.group_btn_all:
                setCategoryBar(0);
                selectedCategory = "전체";
                setRecyclerViewNotify(selectedCategory);
                break;

            case R.id.group_btn_instrument:
                setCategoryBar(1);
                selectedCategory = "기악";
                setRecyclerViewNotify(selectedCategory);
                break;


            case R.id.group_btn_performance:
                setCategoryBar(2);
                selectedCategory = "퍼포먼스";
                setRecyclerViewNotify(selectedCategory);
                break;

            case R.id.group_btn_tradition:
                setCategoryBar(3);
                selectedCategory = "전통";
                setRecyclerViewNotify(selectedCategory);
                break;

            case R.id.group_btn_music:
                setCategoryBar(4);
                selectedCategory = "음악";
                setRecyclerViewNotify(selectedCategory);

                break;

        }
    }
}


class GroupFirebaseData {


    String name;
    String genre;
    String intro;
    String image;
    String youtube;

    public GroupFirebaseData() {
    }

    public GroupFirebaseData(String name, String genre, String intro, String image, String youtube) {
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


class GroupReviewFirebaseData {

    int score;
    String writer;
    String date;
    String contents;
    int seq;

    public GroupReviewFirebaseData() {
    }


    public String getWriter() {
        return writer;
    }

    public void setWriter(String writer) {
        this.writer = writer;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getContents() {
        return contents;
    }

    public void setContents(String contents) {
        this.contents = contents;
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public int getSeq() {
        return seq;
    }

    public void setSeq(int seq) {
        this.seq = seq;
    }
}