package com.teamnoname.streetartzone.StreetGroup;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Display;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

import com.teamnoname.streetartzone.Data.Contest;
import com.teamnoname.streetartzone.Data.GroupData;
import com.teamnoname.streetartzone.R;

import java.util.ArrayList;

import io.realm.Realm;
import io.realm.RealmResults;

public class TicketScheduleDialog extends Dialog implements ScheduleRecyclerViewAdapter.ScheduleItemClickListener {

    ArrayList<ScheduleItem> arrayList_schedule = new ArrayList<>();
    ArrayList<Integer> arrayList_scheduleCategory = new ArrayList<>();
    RecyclerView recyclerView_schedule;
    ScheduleRecyclerViewAdapter scheduleRecyclerViewAdapter;
    Realm realm;
    Context context;
    TextView tv_selectedInfo;

    TicketDialog dialog_ticket;
    String imagePath;

    public String getImagePath() {
        return imagePath;
    }

    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }

    public TicketScheduleDialog(@NonNull Context context) {
        super(context);
        realm = Realm.getDefaultInstance();
        this.context = context;

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        //다이얼로그 설정
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.fragment_group_schedule);
        WindowManager.LayoutParams lpWindow = new WindowManager.LayoutParams();
        lpWindow.flags = WindowManager.LayoutParams.FLAG_DIM_BEHIND;
        lpWindow.dimAmount = 0.8f;

        Display display = ((WindowManager) context.getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();
        lpWindow.height = (int) (display.getHeight() * 0.8);//다이얼로그 크기 설정

        getWindow().setAttributes(lpWindow);


        recyclerView_schedule = (RecyclerView) findViewById(R.id.group_schedule_recyclerView);
        tv_selectedInfo = (TextView) findViewById(R.id.group_schedule_select);

        if (tv_selectedInfo.getVisibility() == View.GONE) {
            tv_selectedInfo.setVisibility(View.VISIBLE);
        }

        setScheduleData();
        setScheduleRecyclerView();
    }

    public void setScheduleData() {
        GroupData groupData = realm.where(GroupData.class).equalTo("group_seq", StreetGroupDetailActivity.selectedSeq).findFirst();
        RealmResults<Contest> contest = realm.where(Contest.class).equalTo("teamname", groupData.getGroup_name()).findAllSorted("date");

        for (int i = 0; i < contest.size(); i++) {
            String fulldate = contest.get(i).getDate();
            String fulltime = contest.get(i).getTime();
            String[] devider = fulldate.split("-");
            String year = devider[0];
            String date = devider[2];
            String[] devider2 = fulltime.split("~");
            String starttime = devider2[0];
            String endtime = devider2[1];
            String month = contest.get(i).getMonth();
            String place = contest.get(i).getDistrict() + " | " + contest.get(i).getArea();
            arrayList_schedule.add(new ScheduleItem(date, place, starttime, endtime, year, month));

            if (i > 0) {
                if (!arrayList_schedule.get(i).getMonth().equals(arrayList_schedule.get(i - 1).getMonth()))
                    arrayList_scheduleCategory.add(i);
            }

        }


        if (arrayList_schedule.size() == 0) {
            Toast.makeText(context, "공연일정이 없습니다.", Toast.LENGTH_SHORT).show();
        }


    }


    public void setScheduleRecyclerView() {

        scheduleRecyclerViewAdapter = new ScheduleRecyclerViewAdapter(arrayList_schedule, arrayList_scheduleCategory);
        scheduleRecyclerViewAdapter.setSelectMode(true);
        scheduleRecyclerViewAdapter.setScheduleItemClickListener(TicketScheduleDialog.this);
        recyclerView_schedule.setLayoutManager(new LinearLayoutManager(context));
        recyclerView_schedule.setAdapter(scheduleRecyclerViewAdapter);

    }

    @Override
    public void setOnitemClick(String month, String day, String place, String start, String end) {

        dialog_ticket = new TicketDialog(context);
        dialog_ticket.setMonth(month);
        dialog_ticket.setDay(day);
        dialog_ticket.setPlace(place);
        dialog_ticket.setStart(start);
        dialog_ticket.setEnd(end);
        dialog_ticket.setImagePath(imagePath);
        dialog_ticket.setCanceledOnTouchOutside(false);
        dialog_ticket.show();
        TicketScheduleDialog.this.dismiss();
    }
}
