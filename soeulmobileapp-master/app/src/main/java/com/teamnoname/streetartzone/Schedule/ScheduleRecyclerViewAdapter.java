package com.teamnoname.streetartzone.Schedule;

import android.graphics.Color;
import android.icu.util.Calendar;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.teamnoname.streetartzone.R;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Hashtable;

/**
 * Created by iyeonghan on 2018. 9. 12..
 */

public
//공연 어댑터
class ScheduleRecyclerViewAdapter extends RecyclerView.Adapter<ScheduleRecyclerViewAdapter.ScheduleViewHolder> {

    ArrayList<Contestitem> arrayListContests;
    ClickListener clickListener;
    ArrayList<Integer> dateCheck = new ArrayList<>();
    Hashtable<Integer,Integer> dateNotice = new Hashtable<>();
    public interface RecyclerViewItemClickListener{
        void setOnItemClick(int selectedPosition);
}

    public ScheduleRecyclerViewAdapter(ArrayList<Contestitem> arrayList,ClickListener clickListener) {
        this.arrayListContests = arrayList;
        this.clickListener = clickListener;
    }

    @NonNull
    @Override
    public ScheduleViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View itemView = LayoutInflater
                    .from(parent.getContext())
                    .inflate(R.layout.schedule_item_layout, parent, false);


        return new ScheduleViewHolder(itemView);
    }



    @Override
    public void onBindViewHolder(@NonNull ScheduleViewHolder holder, final int position) {

        String fullDate = arrayListContests.get(position).getDate();
        String[] devidedDate = fullDate.split("-");
        int year =Integer.parseInt(devidedDate[0]);
        int month = Integer.parseInt(devidedDate[1]);
        int date = Integer.parseInt(devidedDate[2]);

        String whatDate = null;
        try {
             whatDate = getDateDay(fullDate);
        } catch (Exception e) {
            Log.i("Adapter","요일얻기 에러");
            e.printStackTrace();
        }

        if(dateNotice.get(date)==null){
            dateNotice.put(date,position);
            holder.date.setText(date+"("+whatDate+")");
            holder.date.setVisibility(View.VISIBLE);

        }else if(dateNotice.get(date)==position){
            holder.date.setText(date+"("+whatDate+")");
            holder.date.setVisibility(View.VISIBLE);
        }else{
            holder.date.setVisibility(View.GONE);
        }


        if(arrayListContests.get(position).getTeamName().length()>10){
            holder.teamname.setTextSize(15);
        }else {
            holder.teamname.setTextSize(20);
        }
        if(arrayListContests.get(position).getPlace().length()>17){
            holder.place.setTextSize(11);
        }else{
            holder.place.setTextSize(12);
        }
        holder.teamname.setText(arrayListContests.get(position).getTeamName());
        holder.place.setText(arrayListContests.get(position).getPlace());
        holder.time.setText(arrayListContests.get(position).getTime());
        holder.getItemView().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clickListener.setOnItemClickForSchedule(position);
            }
        });

        //색깔 변동주기
        if(arrayListContests.get(position).getContestType()==1){//기악
        holder.type_light.setBackgroundColor(Color.parseColor("#C40000"));
        }else if(arrayListContests.get(position).getContestType()==2){//퍼포먼스
            holder.type_light.setBackgroundColor(Color.parseColor("#14C322"));
        }else if(arrayListContests.get(position).getContestType()==3){//전통
            holder.type_light.setBackgroundColor(Color.YELLOW);
        }else{  //4 : 음악.
            holder.type_light.setBackgroundColor(Color.parseColor("#0B427B"));


        }




    }

    @Override
    public int getItemCount() {
        return (null == arrayListContests) ? 0 : arrayListContests.size();
    }


    public  class ScheduleViewHolder extends RecyclerView.ViewHolder {

        View itemView;
        TextView teamname ;
        TextView place;
        TextView time;
        TextView date;
        LinearLayout type_light ;

        public ScheduleViewHolder(View itemView) {
            super(itemView);
            this.itemView = itemView;
            teamname = (TextView)itemView.findViewById(R.id.schedule_activity_txtv_teamname);
            place = (TextView)itemView.findViewById(R.id.schedule_activity_txtv_place);
            time = (TextView)itemView.findViewById(R.id.schedule_activity_txtv_time);
            type_light =(LinearLayout) itemView.findViewById(R.id.schedule_activity_layout_light);
            date = (TextView)itemView.findViewById(R.id.schedule_date_txtv);

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


//그냥 서버로 부터 데이터를 가져올 때 한번에 다 가져와서 저장한다.
class Contestitem{
    int contestnum;
    String teamName;
    int contestType; //1.기악 2.퍼포먼스 3. 전통 4. 음악
    String date;
    String time;
    String place;

    public Contestitem(int contestnum, String teamName, int contestType, String date, String time, String place) {
        this.contestnum = contestnum;
        this.teamName = teamName;
        this.contestType = contestType;
        this.date = date;
        this.time = time;
        this.place = place;
    }

    public int getContestnum() {
        return contestnum;
    }

    public void setContestnum(int contestnum) {
        this.contestnum = contestnum;
    }


    public String getTeamName() {
        return teamName;
    }

    public void setTeamName(String teamName) {
        this.teamName = teamName;
    }


    public int getContestType() {
        return contestType;
    }

    public void setContestType(int contestType) {
        this.contestType = contestType;
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

    public String getPlace() {
        return place;
    }

    public void setPlace(String place) {
        this.place = place;
    }

}
