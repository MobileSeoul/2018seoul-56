package com.teamnoname.streetartzone.Schedule;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.teamnoname.streetartzone.Data.Contest;
import com.teamnoname.streetartzone.Data.GroupData;
import com.teamnoname.streetartzone.Data.StageInfo;
import com.teamnoname.streetartzone.R;
import com.teamnoname.streetartzone.StreetGroup.StreetGroupDetailActivity;
import com.teamnoname.streetartzone.StreetStage.StageMapDialog;

import java.util.ArrayList;
import java.util.Calendar;

import io.realm.Realm;
import io.realm.RealmResults;


public class Schedule extends AppCompatActivity implements ClickListener {
    Calendar calendar;                                   //현재 날짜를 가져올 수 있는 클래스.
    int nowDate, nowMonth, nowYear;                        //현재 날짜.
    int selDate, selMonth, selYear;                        //사용자에 의해 선택된 날짜.
    ArrayList<Contestitem> contests = new ArrayList<>(); //공연시간 데이터를 담을 컨테이너.

    RecyclerView schedule_rv;
    RecyclerView date_rv;
    TextView showMonth;
    ImageView nextMonthBtn, prevMonthBtn;
    ImageView backBtn;
    MenuDialog dialog;

    //어댑터
    ScheduleRecyclerViewAdapter schedule_adapter;
    DateRecyclerViewAdapter date_adapter;
    Realm realm;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_schedule);
        realm = Realm.getDefaultInstance();


        //뷰 초기화.
        viewInit();
        //날짜 초기화.
        dateInit();
        //현재 날짜 상단과 좌측에 표기
        initNowMonthOnTop();
        //데이터 초기화 (데모)
        setDataSet();
        //공연 rv 설정
        setScheduleRecyclerView();
        //date rv 설정
        setDateRecyclerView();


    }

    private void initNowMonthOnTop() {
        showMonth.setText(nowYear + "년 " + nowMonth + "월");

    }

    private void dateInit() {
        //날짜 받아서 초기화
        calendar = Calendar.getInstance();
        nowYear = calendar.get(calendar.YEAR);
        nowMonth = calendar.get(calendar.MONTH);
        nowDate = calendar.get(calendar.DATE);

        //맨 처음 화면에서 선택 되는것은 현재 날짜
        selYear = nowYear;
        selMonth = nowMonth;
        selDate = nowDate;
    }


    //뷰 초기화
    private void viewInit() {
        //textView
        showMonth = (TextView) findViewById(R.id.schedule_activity_btn_nowmonth);
        //button
        nextMonthBtn = (ImageView) findViewById(R.id.schedule_activity_btn_nextmonth);
        prevMonthBtn = (ImageView) findViewById(R.id.schedule_activity_btn_prevmonth);
        backBtn = (ImageView) findViewById(R.id.backbtn);

        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        //이전 월 버튼 클릭
        prevMonthBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //1월이면 년도를 변경 후에 12월로 변경..
                if (selMonth == 1) {
                    selYear -= 1;
                    selMonth = 12;
                }
                //아니면 -1해줌
                else {
                    selMonth -= 1;
                }
                //변경된 월수에 맞춰 상단 날짜 변경.
                showMonth.setText(selYear + "년 " + selMonth + "월");
                changedMonth();

                //현재월인지 확인하기
                //현재월이면 오늘 날짜로 표시해주고 아니면 1일로 포커싱
                //해당 날짜로 리사이클러뷰 포커싱 하기.
                if (nowMonth == selMonth) {
                    selDate = nowDate;
                } else {
                    selDate = 1;
                }
                focusItemByDate(selDate);



            }
        });

        //다음 월 버튼 클릭.
        nextMonthBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //12월이면 년도를 변경 후에 11월로 변경..
                if (selMonth == 12) {
                    selYear += 1;
                    selMonth = 1;
                }
                //아니면 +1해줌
                else {
                    selMonth += 1;
                }
                //변경된 월수에 맞춰 상단 날짜 변경.
                showMonth.setText(selYear + "년 " + selMonth + "월");

                //월에 맞게 변경.
                changedMonth();

                //현재월인지 확인하기
                //현재월이면 오늘 날짜로 표시해주고 아니면 1일로 포커싱
                //해당 날짜로 리사이클러뷰 포커싱 하기.
                if (nowMonth == selMonth) {
                    selDate = nowDate;
                } else {
                    selDate = 1;
                }

                focusItemByDate(selDate);

            }
        });


    }

    public void changedMonth() {


        if (selMonth == nowMonth && selYear == nowYear) {
            //리스트뷰를 해당 월에 맞춰 변경해 줘야함.
            date_adapter.setCurrentMonth(nowMonth, nowDate);
            Log.i("Schedule", "날짜변경 : " + date_adapter.getSelected_month());
            //ArrayList안에 있는 데이터도 모두 현재월의 데이터로 변경해줘야함.
            //그 이후 스케쥴 노티
            schedule_adapter.notifyDataSetChanged();
        } else {
            //리스트뷰를 해당 월에 맞춰 변경해 줘야함.
            date_adapter.setSelected_month(selMonth);
            //ArrayList안에 있는 데이터도 모두 해당월의 데이터로 변경해줘야함.
            //그 이후 스케쥴 노티
            schedule_adapter.notifyDataSetChanged();

        }
        contests.clear();
        realm.beginTransaction();
        RealmResults<Contest> realmResults = realm.where(Contest.class).equalTo("month", selMonth + "").notEqualTo("time","~").findAllSorted("dateForSort");
        for (int i = 0; i < realmResults.size(); i++) {
            GroupData ro = realm.where(GroupData.class).equalTo("group_name", realmResults.get(i).getTeamname()).findFirst();
            String place = "[" + realmResults.get(i).getDistrict() + "]" + realmResults.get(i).getArea();
            int genre = 0;
            if (ro != null) {
                genre = getGenre(ro.getGroup_genre());
            }
            contests.add(new Contestitem(realmResults.get(i).getNum(), realmResults.get(i).getTeamname(), genre, realmResults.get(i).getDate(), realmResults.get(i).getTime(), place));
        }
        realm.commitTransaction();
        schedule_adapter.notifyDataSetChanged();
    }

    public int getGenre(String genre) {
        if (genre.equals("기악")) {
            return 1;
        } else if (genre.equals("기악")) {
            return 2;
        } else if (genre.equals("기악")) {
            return 3;
        } else {
            return 4;
        }

    }

    //현재 arraylist에 있는 date를 찾아서 몇번째 포지션에 있는지 확인하기
    public void focusItemByDate(int date) {
        Log.i("Schedule", "처음date : " + date);
        boolean isItem = false;
        for (int i = 0; i < contests.size(); i++) {

            String fullDate = contests.get(i).getDate();
            String[] devidedDate = fullDate.split("-");

            int thisdate = Integer.parseInt(devidedDate[2]);

            //현재 날짜와 동일한 애를 포커싱 해줌.
            if (thisdate == date) {
                schedule_rv.scrollToPosition(i);
                LinearLayoutManager lm;
                isItem = true;
                break;
            }
        }
        if (!isItem) {
            Toast.makeText(Schedule.this, date + "일에 공연이 없습니다.", Toast.LENGTH_SHORT).show();
        }
    }

    public void setScheduleRecyclerView() {
        schedule_rv = (RecyclerView) findViewById(R.id.schedule_rv_contests);
        schedule_adapter = new ScheduleRecyclerViewAdapter(contests, Schedule.this);
        schedule_rv.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        schedule_rv.setAdapter(schedule_adapter);
    }

    public void setDateRecyclerView() {
        date_rv = (RecyclerView) findViewById(R.id.schedule_rv_date);
        date_adapter = new DateRecyclerViewAdapter(selMonth, selDate, Schedule.this);
        LinearLayoutManager lm = new LinearLayoutManager(getApplicationContext());
        lm.setOrientation(LinearLayoutManager.HORIZONTAL);

        date_rv.setLayoutManager(lm);

        date_rv.setAdapter(date_adapter);
        date_rv.smoothScrollToPosition(nowDate + 1);

    }

    public void setDataSet() {

        realm.beginTransaction();
        RealmResults<Contest> realmResults = realm.where(Contest.class).equalTo("month", selMonth + "").notEqualTo("time","~").findAllSorted("date");
        for (int i = 0; i < realmResults.size(); i++) {
            GroupData ro = realm.where(GroupData.class).equalTo("group_name", realmResults.get(i).getTeamname()).findFirst();
            String place = "[" + realmResults.get(i).getDistrict() + "]" + realmResults.get(i).getArea();
            int genre = 0;
            if (ro != null) {
                genre = getGenre(ro.getGroup_genre());
            }

            contests.add(new Contestitem(realmResults.get(i).getNum(), realmResults.get(i).getTeamname(), genre, realmResults.get(i).getDate(), realmResults.get(i).getTime(), place));
        }
        realm.commitTransaction();
    }


    @Override
    public void setOnItemClickForDate(int selectedDate) {
        //날짜 구해서 해당 날짜로 아이템 포커싱
        focusItemByDate(selectedDate);

    }

    @Override
    public void setOnItemClickForSchedule(final int selectedPosition) {
        //아이템 클릭시,
        dialog = new MenuDialog(Schedule.this,
                //왼쪽 리스너
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //공연단 소개 페이지로 넘기기.
                        Intent intent = new Intent(Schedule.this, StreetGroupDetailActivity.class);

                        String teamname = contests.get(selectedPosition).getTeamName();

                        GroupData ro = realm.where(GroupData.class).equalTo("group_name", teamname).findFirst();
                        if(ro!=null){
                            int seq = ro.getGroup_seq();
                            //팀 소개를 보여줄 것이므로 팀넘버를 넘겨준다.
                            intent.putExtra("seq", seq);
                            startActivity(intent);
                            dialog.dismiss();

                        }else{
                            Toast.makeText(Schedule.this, "해당 공연팀은 정보가 없습니다.", Toast.LENGTH_SHORT).show();
                        }
                        
                    }
                },
                //오른쪽 리스너
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //맵으로 넘기기
                        Intent intent = new Intent();

                        //현재 arraylist contest에 있는 주소의 형태와 db에 있는 데이터의 형태가 다름.
                        //그러므로 num을 기준으로 realm으로 부터 공연장 명을 가져온 후에, 혜진씨가 만든 StageInfo 테이블에서 공연장 명을 가지고
                        //진짜 주소를 찾아서 넘겨줘야함.
                        int num = contests.get(selectedPosition).getContestnum();
                        realm.beginTransaction();
                        Contest contest = realm.where(Contest.class).equalTo("num", num).findFirst();

                        StageInfo stageInfo = realm.where(StageInfo.class).like("placeName", "*" + contest.getArea() + "*").findFirst();

                        //공연 위치를 보여줘야 하므로\ 공연 넘버를 넘겨줌
                        StageMapDialog dialog_stageMap = StageMapDialog.newInstance(stageInfo);
                        dialog_stageMap.show(getSupportFragmentManager(), "DIALOG");
                        realm.commitTransaction();
                        dialog.dismiss();
                    }
                });
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCanceledOnTouchOutside(true);
        dialog.show();
    }
}





