package com.teamnoname.streetartzone.StreetGroup;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.teamnoname.streetartzone.Data.GroupReviewData;
import com.teamnoname.streetartzone.Data.GroupReviewDataItem;
import com.teamnoname.streetartzone.R;

import java.util.ArrayList;

import io.realm.Realm;
import io.realm.RealmResults;

public class ReviewFragment extends Fragment {

    ArrayList<ReviewItem> arrayList_review;
    RecyclerView recyclerView_review;
    ReviewRecyclerViewAdapter reviewRecyclerViewAdapter;
    TextView tv_reviewCount;
    Button btn_writeReview;

    //DataBases
    RealmResults<GroupReviewData> realmResults;
    RealmResults<GroupReviewData> realmResults_total;
    int selectedSeq;
    Realm realm;

    //Firebase
    DatabaseReference dbReference;
    FirebaseDatabase firebaseDatabase;



    public static ReviewFragment newInstance(){
        Bundle args = new Bundle();

        ReviewFragment reviewFragment = new ReviewFragment();
        reviewFragment.setArguments(args);
        return reviewFragment;
    }

    public ReviewFragment() {
        // Required empty public constructor
        selectedSeq = StreetGroupDetailActivity.selectedSeq;

        realm = Realm.getDefaultInstance();
        realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                realmResults = realm.where(GroupReviewData.class).equalTo("seq",selectedSeq).findAll();
                realmResults_total = realm.where(GroupReviewData.class).findAll();
            }
        });

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_group_review,container,false);

        recyclerView_review = (RecyclerView) view.findViewById(R.id.fragment_group_review_recyclerView);
        tv_reviewCount = (TextView) view.findViewById(R.id.group_detail_review_count);
        btn_writeReview = (Button) view.findViewById(R.id.group_detail_review_write);

        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        if(realmResults!=null){
            String reviewCount = Integer.toString(realmResults.size());
            tv_reviewCount.setText(reviewCount);
        }

        btn_writeReview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent goWritingReview = new Intent(getActivity(),WriteReview.class);
                goWritingReview.putExtra("seq",selectedSeq);
                startActivity(goWritingReview);
            }
        });




    }

    @Override
    public void onResume() {
        super.onResume();

        setReviewData();

    }

    public void setReviewData(){

        realmResults_total = realm.where(GroupReviewData.class).findAll();

        firebaseDatabase =FirebaseDatabase.getInstance();
        dbReference = firebaseDatabase.getReference("groupdata/groupreview");

        dbReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull final DataSnapshot dataSnapshot) {

                if(realmResults_total.size()<dataSnapshot.getChildrenCount()){


                    realm.executeTransaction(new Realm.Transaction() {
                        @Override
                        public void execute(Realm realm) {
                            int reviewCount = 0;
                            for (DataSnapshot groupReviewDB : dataSnapshot.getChildren()) {

                                reviewCount += 1;

                                if (realmResults_total.size() < reviewCount) {

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
                    });

                }

                realmResults = realm.where(GroupReviewData.class).equalTo("seq",selectedSeq).findAll();
                arrayList_review= new ArrayList<>();

                for(int i=0; i<realmResults.size(); i++){
                    GroupReviewData tempData = realmResults.get(i);
                    arrayList_review.add(new ReviewItem(tempData.getWriter(),tempData.getDate(),tempData.getContents(),tempData.getscore()));
                }


                if(realmResults!=null){
                    String reviewCount = Integer.toString(realmResults.size());
                    tv_reviewCount.setText(reviewCount);
                }


                setReviewRecyclerView();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });






    }


    public void setReviewRecyclerView(){

        reviewRecyclerViewAdapter = new ReviewRecyclerViewAdapter(arrayList_review);
        recyclerView_review.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView_review.setAdapter(reviewRecyclerViewAdapter);



    }

}



class ReviewRecyclerViewAdapter extends RecyclerView.Adapter<ReviewRecyclerViewAdapter.ReviewViewHolder> {

    ArrayList<ReviewItem> arrayList_review;


    public ReviewRecyclerViewAdapter(ArrayList<ReviewItem> arrayList_review) {
        this.arrayList_review = arrayList_review;
    }

    @NonNull
    @Override
    public ReviewViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater
                .from(parent.getContext())
                .inflate(R.layout.streetgroup_detail_review_item, parent, false);


        return new ReviewViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull ReviewViewHolder holder, int position) {

        holder.tv_review_name.setText(arrayList_review.get(position).getReviewer());
        holder.tv_review_date.setText(arrayList_review.get(position).getReveiwDate());
        holder.tv_review_content.setText(arrayList_review.get(position).getReviewContent());


        double rating=arrayList_review.get(position).getReviewScore();

        if(rating==0){
            holder.imgV_star1.setImageResource(R.drawable.group_star_non);
        }else if(rating==0.5){
            holder.imgV_star1.setImageResource(R.drawable.group_star_half);
        }else{
            holder.imgV_star1.setImageResource(R.drawable.group_star_full);
        }


        if(rating <= 1){
            holder.imgV_star2.setImageResource(R.drawable.group_star_non);
        }else if(rating == 1.5){
            holder.imgV_star2.setImageResource(R.drawable.group_star_half);
        }else{
            holder.imgV_star2.setImageResource(R.drawable.group_star_full);
        }

        if(rating <= 2){
            holder.imgV_star3.setImageResource(R.drawable.group_star_non);
        }else if(rating == 2.5){
            holder.imgV_star3.setImageResource(R.drawable.group_star_half);
        }else{
            holder.imgV_star3.setImageResource(R.drawable.group_star_full);
        }

        if(rating <= 3){
            holder.imgV_star4.setImageResource(R.drawable.group_star_non);
        }else if(rating == 3.5){
            holder.imgV_star4.setImageResource(R.drawable.group_star_half);
        }else{
            holder.imgV_star4.setImageResource(R.drawable.group_star_full);
        }


        if(rating <= 4){
            holder.imgV_star5.setImageResource(R.drawable.group_star_non);
        }else if(rating == 4.5){
            holder.imgV_star5.setImageResource(R.drawable.group_star_half);
        }else{
            holder.imgV_star5.setImageResource(R.drawable.group_star_full);
        }





    }

    @Override
    public int getItemCount() {
        return (null == arrayList_review) ? 0 : arrayList_review.size();
    }

    public static class ReviewViewHolder extends RecyclerView.ViewHolder {

        TextView tv_review_name;
        TextView tv_review_date;
        TextView tv_review_content;
        ImageView imgV_review_img;
        ImageView imgV_star1;
        ImageView imgV_star2;
        ImageView imgV_star3;
        ImageView imgV_star4;
        ImageView imgV_star5;

        public ReviewViewHolder(View itemView) {
            super(itemView);

            tv_review_name = (TextView) itemView.findViewById(R.id.group_detail_review_name);
            tv_review_date = (TextView) itemView.findViewById(R.id.group_detail_review_date);
            tv_review_content = (TextView) itemView.findViewById(R.id.group_detail_review_content);
            imgV_review_img = (ImageView) itemView.findViewById(R.id.group_detail_review_img);
            imgV_star1 = (ImageView) itemView.findViewById(R.id.group_detail_review_star1);
            imgV_star2 = (ImageView) itemView.findViewById(R.id.group_detail_review_star2);
            imgV_star3 = (ImageView) itemView.findViewById(R.id.group_detail_review_star3);
            imgV_star4 = (ImageView) itemView.findViewById(R.id.group_detail_review_star4);
            imgV_star5 = (ImageView) itemView.findViewById(R.id.group_detail_review_star5);
        }
    }
}




class ReviewItem {

    String reviewer;
    String reveiwDate;
    String reviewContent;
    int reviewScore;

    public ReviewItem(String reviewer, String reveiwDate, String reviewContent, int reviewScore) {
        this.reviewer = reviewer;
        this.reveiwDate = reveiwDate;
        this.reviewContent = reviewContent;
        this.reviewScore = reviewScore;
    }

    public String getReviewer() {
        return reviewer;
    }

    public void setReviewer(String reviewer) {
        this.reviewer = reviewer;
    }

    public String getReveiwDate() {
        return reveiwDate;
    }

    public void setReveiwDate(String reveiwDate) {
        this.reveiwDate = reveiwDate;
    }

    public String getReviewContent() {
        return reviewContent;
    }

    public void setReviewContent(String reviewContent) {
        this.reviewContent = reviewContent;
    }

    public int getReviewScore() {
        return reviewScore;
    }

    public void setReviewScore(int reviewScore) {
        this.reviewScore = reviewScore;
    }
}