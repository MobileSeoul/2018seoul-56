package com.teamnoname.streetartzone.Schedule;

import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.teamnoname.streetartzone.R;


//날짜 어댑터.
public class DateRecyclerViewAdapter extends RecyclerView.Adapter<DateRecyclerViewAdapter.DateViewHolder>{
    int selected_month,selected_date ;
    ClickListener clickListener;


    public DateRecyclerViewAdapter(int selected_month, int selected_date,ClickListener clickListener) {
        this.selected_month = selected_month;
        this.selected_date = selected_date;
        this.clickListener = clickListener;
    }

    public int getSelected_month() {
        return selected_month;
    }


    //날짜와 동일하지 않은 월을 클릭시.
    public void setSelected_month(int selected_month) {
        this.selected_month = selected_month;
        this.selected_date = 1;
        //개수를 변경해줌
        getItemCount();
        //1일로 날짜 변경
        notifyDataSetChanged();
    }
    //날짜와 동일한 월을 클릭시 오늘 날짜로 보여주기
    public void setCurrentMonth(int nowMonth,int nowDate){
        selected_date = nowDate;
        this.selected_month = nowMonth;
        //개수 변경 알리기
        getItemCount();
        //현재 날짜로 변경.
        notifyDataSetChanged();

    }

    public int getSelected_date() {
        return selected_date;
    }

    public void setSelected_date(int selected_date) {
        this.selected_date = selected_date;
    }

    //해당 월의 마지막 날짜를 찾아주는 함수.
    int LastDate(int month){
        if(month==2){
            return 28;
        }else if(month==4||month==6||month==9||month==11){
            return 30;
        }else{
            return 31;
        }
    }

    @NonNull
    @Override
    public DateViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater
                .from(parent.getContext())
                .inflate(R.layout.schedule_date_item,parent,false);


        return new DateViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull final DateViewHolder holder, final int position) {

        //Text에 값 초기화
        holder.date.setText(position+1+"");
        if(position+1==selected_date) {
            //현재 날짜는 그린색으로 표시
            holder.date.setTextColor(Color.WHITE);
        }else{
            holder.date.setTextColor(Color.parseColor("#D8D8D8"));
        }
        holder.getItemView().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
             holder.date.setTextColor(Color.parseColor("#D8D8D8"));
             selected_date=position+1;
            notifyDataSetChanged();
            clickListener.setOnItemClickForDate(selected_date);



            }
        });



    }

    @Override
    public int getItemCount() {

//        Log.i("DateRecyclerViewAdapter","찍어낼 개수 변경");
        //해당 월에 숫자만큼 찍어내기
        return LastDate(selected_month);
    }

    public  class DateViewHolder extends RecyclerView.ViewHolder {

        View itemView;
        TextView date ;


        public DateViewHolder(View itemView) {
            super(itemView);
            this.itemView = itemView;
            date = (TextView)itemView.findViewById(R.id.schedule_date_txt);

        }
        public View getItemView(){return this.itemView;}
    }
}