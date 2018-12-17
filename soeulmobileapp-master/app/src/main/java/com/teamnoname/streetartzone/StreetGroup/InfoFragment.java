package com.teamnoname.streetartzone.StreetGroup;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.android.youtube.player.YouTubeInitializationResult;
import com.google.android.youtube.player.YouTubePlayer;
import com.google.android.youtube.player.YouTubePlayerSupportFragment;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.teamnoname.streetartzone.Data.GroupData;
import com.teamnoname.streetartzone.R;

import io.realm.Realm;

public class InfoFragment extends Fragment implements YouTubePlayer.OnInitializedListener {

    TextView tv_contents;
    ImageView imgV_main;
    YouTubePlayerSupportFragment youTubePlayerView;
    TextView tv_youtubeInfo;

    ImageView imgV_img1;
    ImageView imgV_img2;
    ImageView imgV_img3;
    
    ImageButton btn_ticket;

    TicketScheduleDialog dialog_ticketSchedule;

    int selectedSeq;
    Realm realm;

    //DataBases
    DatabaseReference dbReference;
    FirebaseDatabase db;
    GroupData groupData;
    String db_path="groupdata/groupimage/image/";
    String youtube_path="없음";
    GroupImageFirebaseData groupImageFirebaseData;

    public static InfoFragment newInstance(){
        Bundle args = new Bundle();

        InfoFragment infoFragment = new InfoFragment();
        infoFragment.setArguments(args);
        return infoFragment;
    }


    public InfoFragment() {
        // Required empty public constructor
        selectedSeq = StreetGroupDetailActivity.selectedSeq;
        db_path = db_path+selectedSeq;
        realm = Realm.getDefaultInstance();
        realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                groupData = realm.where(GroupData.class).equalTo("group_seq",selectedSeq).findFirst();

            }
        });
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_group_info, container, false);
        tv_contents = (TextView) view.findViewById(R.id.fragment_group_info_contents);
        imgV_main = (ImageView) view.findViewById(R.id.fragment_group_info_mainImg);
        tv_youtubeInfo = (TextView) view.findViewById(R.id.fragment_group_info_tv_youtube);

        imgV_img1 = (ImageView) view.findViewById(R.id.fragment_group_info_img1);
        imgV_img2 = (ImageView) view.findViewById(R.id.fragment_group_info_img2);
        imgV_img3 = (ImageView) view.findViewById(R.id.fragment_group_info_img3);
        
        btn_ticket = (ImageButton) view.findViewById(R.id.fragment_group_info_ticket);

        youTubePlayerView = (YouTubePlayerSupportFragment) getChildFragmentManager().findFragmentById(R.id.fragment_group_info_youtube);
        youTubePlayerView.initialize(youtube_path,this);

        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        db = FirebaseDatabase.getInstance();
        dbReference = db.getReference(db_path);
        groupImageFirebaseData = new GroupImageFirebaseData();

        youtube_path= groupData.getGroup_youtube();
        tv_contents.setText(groupData.getGroup_info());
        Glide.with(getActivity())
                .load(groupData.getGroup_titleImg())
                .into(imgV_main);

        imgV_img1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                goDetailImage(groupImageFirebaseData.getImg1());
            }
        });

        imgV_img2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                goDetailImage(groupImageFirebaseData.getImg2());

            }
        });

        imgV_img3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                goDetailImage(groupImageFirebaseData.getImg3());
            }
        });

        btn_ticket.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                dialog_ticketSchedule = new TicketScheduleDialog(getActivity());
                dialog_ticketSchedule.setImagePath(groupData.getGroup_titleImg());
                dialog_ticketSchedule.setCanceledOnTouchOutside(false);
                dialog_ticketSchedule.show();


            }
        });



        dbReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                //group review data
                for(DataSnapshot groupDetailDB : dataSnapshot.getChildren()){

                    switch (groupDetailDB.getKey().toString()){

                        case "img1" :

                            groupImageFirebaseData.setImg1(groupDetailDB.getValue().toString());
                            break;


                        case "img2" :
                            groupImageFirebaseData.setImg2(groupDetailDB.getValue().toString());

                            break;

                        case "img3" :
                            groupImageFirebaseData.setImg3(groupDetailDB.getValue().toString());
                            break;

                    }


                }

                if(!groupImageFirebaseData.getImg1().equals("없음")){
                    Glide.with(getActivity())
                            .load(groupImageFirebaseData.getImg1())
                            .into(imgV_img1);

                }

                if(!groupImageFirebaseData.getImg2().equals("없음")){
                    Glide.with(getActivity())
                            .load(groupImageFirebaseData.getImg2())
                            .into(imgV_img2);
                }

                if(!groupImageFirebaseData.getImg3().equals("없음")){
                    Glide.with(getActivity())
                            .load(groupImageFirebaseData.getImg3())
                            .into(imgV_img3);
                }







            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


    }

    public void goDetailImage(String imgPath){
        if(!imgPath.equals("없음")){
            Intent goDetailImg = new Intent(getActivity(),StreetGroupDetailImgActivity.class);
            goDetailImg.putExtra("path",imgPath);
            startActivity(goDetailImg);
        }

    }

    @Override
    public void onInitializationSuccess(YouTubePlayer.Provider provider, YouTubePlayer youTubePlayer, boolean b) {
        if(youtube_path == null || youtube_path.equals("없음")){

            if(tv_youtubeInfo.getVisibility()==View.GONE){
                tv_youtubeInfo.setVisibility(View.VISIBLE);
            }

        }else{

            youTubePlayer.cueVideo(youtube_path);
            if(tv_youtubeInfo.getVisibility()==View.VISIBLE){
                tv_youtubeInfo.setVisibility(View.GONE);
            }
        }

    }

    @Override
    public void onInitializationFailure(YouTubePlayer.Provider provider, YouTubeInitializationResult youTubeInitializationResult) {
        if(tv_youtubeInfo.getVisibility()==View.GONE){
            tv_youtubeInfo.setVisibility(View.VISIBLE);
        }
    }
}


class GroupImageFirebaseData{

    int seq;
    String img1;
    String img2;
    String img3;

    public GroupImageFirebaseData(int seq, String img1, String img2, String img3) {
        this.seq = seq;
        this.img1 = img1;
        this.img2 = img2;
        this.img3 = img3;
    }

    public GroupImageFirebaseData() {
    }

    public int getSeq() {
        return seq;
    }

    public void setSeq(int seq) {
        this.seq = seq;
    }

    public String getImg1() {
        return img1;
    }

    public void setImg1(String img1) {
        this.img1 = img1;
    }

    public String getImg2() {
        return img2;
    }

    public void setImg2(String img2) {
        this.img2 = img2;
    }

    public String getImg3() {
        return img3;
    }

    public void setImg3(String img3) {
        this.img3 = img3;
    }
}