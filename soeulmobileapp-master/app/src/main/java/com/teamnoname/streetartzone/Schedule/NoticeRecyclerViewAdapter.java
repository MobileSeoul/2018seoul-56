package com.teamnoname.streetartzone.Schedule;

import android.graphics.Color;
import android.icu.util.Calendar;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.teamnoname.streetartzone.R;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Hashtable;


public
//알림 어댑터
class NoticeRecyclerViewAdapter extends RecyclerView.Adapter<NoticeRecyclerViewAdapter.NoticeViewHolder> {

    ArrayList<Contestitem> arrayListContests;
    NoticeClickListener clickListener;
    Hashtable<Integer,Integer> dateNotice = new Hashtable<>();

    public NoticeRecyclerViewAdapter(ArrayList<Contestitem> arrayList,NoticeClickListener clickListener) {
        this.arrayListContests = arrayList;
        this.clickListener = clickListener;
    }

    @NonNull
    @Override
    public NoticeViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View itemView = LayoutInflater
                .from(parent.getContext())
                .inflate(R.layout.notice_item, parent, false);


        return new NoticeViewHolder(itemView);
    }



    @Override
    public void onBindViewHolder(@NonNull NoticeViewHolder holder, final int position) {
        Log.i("Adapter","onBindViewHolder");

        String fullDate = arrayListContests.get(position).getDate();
        String[] devidedDate = fullDate.split("-");
        int year =Integer.parseInt(devidedDate[0]);
        int month = Integer.parseInt(devidedDate[1]);
        int date = Integer.parseInt(devidedDate[2]);

        String whatDate = null;
        try {
            whatDate = getDateDay(fullDate);
        } catch (Exception e) {
            e.printStackTrace();
        }

        holder.teamname.setText(arrayListContests.get(position).getTeamName());
        holder.place.setText(date+"일 "+arrayListContests.get(position).getPlace());
        holder.time.setText(arrayListContests.get(position).getTime());
        holder.getItemView().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clickListener.setOnItemClickForNotice(position);
            }
        });

        //색깔 변동주기
        if(arrayListContests.get(position).getContestType()==1){//기악
            holder.type_light.setBackgroundColor(Color.RED);
        }else if(arrayListContests.get(position).getContestType()==2){//퍼포먼스
            holder.type_light.setBackgroundColor(Color.BLUE);
        }else if(arrayListContests.get(position).getContestType()==3){//전통
            holder.type_light.setBackgroundColor(Color.YELLOW);
        }else{  //4 : 음악.
            holder.type_light.setBackgroundColor(Color.GREEN);

        }

        //삭제 버튼 클릭할 경우.
        holder.delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clickListener.setOnItemClickForDeleteButton(position);
            }
        });



    }

    @Override
    public int getItemCount() {
        return (null == arrayListContests) ? 0 : arrayListContests.size();
    }


    public  class NoticeViewHolder extends RecyclerView.ViewHolder {

        View itemView;
        TextView teamname ;
        TextView place;
        TextView time;
        TextView date;
        LinearLayout type_light ;
        ImageView delete;

        public NoticeViewHolder(View itemView) {
            super(itemView);
            this.itemView = itemView;
            teamname = (TextView)itemView.findViewById(R.id.schedule_activity_txtv_teamname);
            place = (TextView)itemView.findViewById(R.id.schedule_activity_txtv_place);
            time = (TextView)itemView.findViewById(R.id.schedule_activity_txtv_time);
            type_light =(LinearLayout) itemView.findViewById(R.id.schedule_activity_layout_light);
            date = (TextView)itemView.findViewById(R.id.schedule_date_txtv);
            delete = (ImageView) itemView.findViewById(R.id.notice_delete_txt);

        }
        public View getItemView(){return this.itemView;}
    }


    //요일 구하는 메소드
    public String getDateDay(String date) throws Exception {


        String day = "" ;

        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd") ;
        Date nDate = dateFormat.parse(date) ;

        Calendar cal = Calendar.getInstance() ;
        cal.setTime(nDate);

        int dayNum = cal.get(Calendar.DAY_OF_WEEK) ;



        switch(dayNum){
            case 1:
                day = "일";
                break ;
            case 2:
                day = "월";
                break ;
            case 3:
                day = "화";
                break ;
            case 4:
                day = "수";
                break ;
            case 5:
                day = "목";
                break ;
            case 6:
                day = "금";
                break ;
            case 7:
                day = "토";
                break ;

        }



        return day ;
    }

}