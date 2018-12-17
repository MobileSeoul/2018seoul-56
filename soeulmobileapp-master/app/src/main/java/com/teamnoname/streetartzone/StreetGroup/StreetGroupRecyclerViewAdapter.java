package com.teamnoname.streetartzone.StreetGroup;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.teamnoname.streetartzone.R;

import java.util.ArrayList;

public class StreetGroupRecyclerViewAdapter extends RecyclerView.Adapter<StreetGroupRecyclerViewAdapter.RecyclerViewHolder> {

    private static String TAG="StreetGroupRecyclerViewAdapter";
    private ArrayList<StreetGroupItem> arrayList_item;
    private RecyclerViewItemClickListener recyclerViewItemClickListener;
    Context context;


    public StreetGroupRecyclerViewAdapter(Context context, ArrayList<StreetGroupItem> arrayList_item, RecyclerViewItemClickListener recyclerViewItemClickListener) {
        this.context=context;
        this.arrayList_item = arrayList_item;
        this.recyclerViewItemClickListener=recyclerViewItemClickListener;
    }


    @NonNull
    @Override
    public StreetGroupRecyclerViewAdapter.RecyclerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        //아이템 뷰 생성
        View itemView = LayoutInflater
                .from(parent.getContext())
                .inflate(R.layout.streetgroup_item, parent, false);


        return new RecyclerViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull final RecyclerViewHolder holder, final int position) {

        Glide.with(context)
                .load(arrayList_item.get(position).getGroup_img())
                .into(holder.imgV_titleImg);


        holder.tv_groupName.setText(arrayList_item.get(position).getGroup_name());
        holder.tv_groupCategory.setText(arrayList_item.get(position).getGroup_category());
        holder.tv_groupDetail.setText(arrayList_item.get(position).getGroup_detail());

        int score =0;
        if(arrayList_item.get(position).getGroup_reviewNumber()>0){
            score =arrayList_item.get(position).getGroup_score()/arrayList_item.get(position).getGroup_reviewNumber();
        }

        holder.tv_groupScore.setText(Integer.toString(score));
        holder.tv_groupReviewNumber.setText("공연후기 "+Integer.toString(arrayList_item.get(position).getGroup_reviewNumber()));

        double rating=0;
        if(arrayList_item.get(position).getGroup_reviewNumber()>0){
            rating = arrayList_item.get(position).getGroup_score()/(double) arrayList_item.get(position).getGroup_reviewNumber();
        }



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




        holder.getItemView().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                recyclerViewItemClickListener.setOnItemClick(arrayList_item.get(holder.getAdapterPosition()).getGroup_seq());
            }
        });

    }

    @Override
    public int getItemCount() {
        return (null == arrayList_item) ? 0 : arrayList_item.size();
    }

    public interface RecyclerViewItemClickListener{
        void setOnItemClick(int selectedPosition);
    }


    //공연팀 리사이클러뷰 뷰홀더
    public static class RecyclerViewHolder extends RecyclerView.ViewHolder {


        View itemView;
        ImageView imgV_titleImg;
        TextView tv_groupName;
        TextView tv_groupCategory;
        TextView tv_groupDetail;
        LinearLayout btn_groupDetailMore;
        ImageView imgV_star1;
        ImageView imgV_star2;
        ImageView imgV_star3;
        ImageView imgV_star4;
        ImageView imgV_star5;
        TextView tv_groupScore;
        TextView tv_groupReviewNumber;
        CardView cardView_group;



        public RecyclerViewHolder(View itemView) {
            super(itemView);
            this.itemView = itemView;
            cardView_group = (CardView) itemView.findViewById(R.id.group_item_cardview);
            imgV_titleImg = (ImageView) itemView.findViewById(R.id.group_item_img);
            tv_groupName = (TextView) itemView.findViewById(R.id.group_item_name);
            tv_groupCategory = (TextView) itemView.findViewById(R.id.group_item_category);
            tv_groupDetail = (TextView) itemView.findViewById(R.id.group_item_detail);
            imgV_star1 = (ImageView) itemView.findViewById(R.id.group_item_star1);
            imgV_star2 = (ImageView) itemView.findViewById(R.id.group_item_star2);
            imgV_star3 = (ImageView) itemView.findViewById(R.id.group_item_star3);
            imgV_star4 = (ImageView) itemView.findViewById(R.id.group_item_star4);
            imgV_star5 = (ImageView) itemView.findViewById(R.id.group_item_star5);
            tv_groupScore = (TextView) itemView.findViewById(R.id.group_item_score);
            tv_groupReviewNumber = (TextView) itemView.findViewById(R.id.group_item_reviewNumber);


        }

        public View getItemView(){return this.itemView;}
    }

}


//공연팀 리사이클러뷰 아이템 클래스
class StreetGroupItem {

    private int group_seq;
    private String group_img;
    private String group_name;
    private String group_category;
    private String group_detail;
    private int group_score;
    private int group_reviewNumber;


    public StreetGroupItem(int group_seq, String group_img, String group_name, String group_category, String group_detail, int group_score, int group_reviewNumber) {
        this.group_seq = group_seq;
        this.group_img = group_img;
        this.group_name = group_name;
        this.group_category = group_category;
        this.group_detail = group_detail;
        this.group_score = group_score;
        this.group_reviewNumber = group_reviewNumber;
    }

    public int getGroup_seq() {
        return group_seq;
    }

    public void setGroup_seq(int group_seq) {
        this.group_seq = group_seq;
    }

    public String getGroup_img() {
        return group_img;
    }

    public void setGroup_img(String group_img) {
        this.group_img = group_img;
    }

    public String getGroup_name() {
        return group_name;
    }

    public void setGroup_name(String group_name) {
        this.group_name = group_name;
    }

    public String getGroup_category() {
        return group_category;
    }

    public void setGroup_category(String group_category) {
        this.group_category = group_category;
    }

    public String getGroup_detail() {
        return group_detail;
    }

    public void setGroup_detail(String group_detail) {
        this.group_detail = group_detail;
    }

    public int getGroup_score() {
        return group_score;
    }

    public void setGroup_score(int group_score) {
        this.group_score = group_score;
    }

    public int getGroup_reviewNumber() {
        return group_reviewNumber;
    }

    public void setGroup_reviewNumber(int group_reviewNumber) {
        this.group_reviewNumber = group_reviewNumber;
    }
}