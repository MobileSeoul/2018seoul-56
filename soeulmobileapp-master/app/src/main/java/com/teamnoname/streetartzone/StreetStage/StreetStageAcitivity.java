package com.teamnoname.streetartzone.StreetStage;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.EditText;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.teamnoname.streetartzone.Data.StageInfo;
import com.teamnoname.streetartzone.R;

import java.util.ArrayList;
import java.util.HashMap;

import io.realm.Realm;
import io.realm.RealmResults;

public class StreetStageAcitivity extends AppCompatActivity implements DistrictAndStageListAdapter.ChangeMapImageListener {

    private String TAG = "## StreetStageAcitivity  ";
    private ExpandableListView exListV_stageInfo;

    private ImageView img_soeulMap;
    private ImageView img_districtMap;
    private ImageView img_xBtn;

    private String[] array_district;
    private String[] array_districtEng;
    private HashMap<String, ArrayList<StageInfo>> map_allStageInfo;

    private DistrictAndStageListAdapter adapter_stageInfoList;
    private int lastExpandedGroupPosition;
    private Realm realm;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_streetstage);
        map_allStageInfo = new HashMap<>();
        array_district = this.getResources().getStringArray(R.array.district);
        array_districtEng = this.getResources().getStringArray(R.array.districtEng);
        realm.init(this);
        realm = Realm.getDefaultInstance();
        initData();
        initView();
    }

    private void initView() {

        img_soeulMap = (ImageView) findViewById(R.id.streetstage_activity_img_soeulmap);
        img_districtMap = (ImageView) findViewById(R.id.streetstage_activity_img_district);
        exListV_stageInfo = (ExpandableListView) findViewById(R.id.streetstage_activity_list_districtinfo);
        img_xBtn = (ImageView) findViewById(R.id.streetstage_activity_img_xbtn);
        adapter_stageInfoList = new DistrictAndStageListAdapter(this, array_district, map_allStageInfo, this);
        exListV_stageInfo.setAdapter(adapter_stageInfoList);

        exListV_stageInfo.setOnGroupExpandListener(new ExpandableListView.OnGroupExpandListener() {
            @Override
            public void onGroupExpand(int groupPosition) {
                if (groupPosition != lastExpandedGroupPosition) {
                    exListV_stageInfo.collapseGroup(lastExpandedGroupPosition);
                }
                lastExpandedGroupPosition = groupPosition;
            }
        });

        img_xBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private void initData() {
        for (String district : array_district) {
            map_allStageInfo.put(district, getDataInRealm(district));
        }
    }

    private ArrayList<StageInfo> getDataInRealm(String district) {

        RealmResults<StageInfo> array_allStageInfo = realm.where(StageInfo.class).findAll();
        ArrayList<StageInfo> array_stageInfoOfDistrict = new ArrayList<>();

        for (StageInfo info : array_allStageInfo) {
            if (district.equals(info.getDistrict()))
                array_stageInfoOfDistrict.add(info);
        }

        return array_stageInfoOfDistrict;
    }


    @Override
    public void onChaneMapImage(int imageIndex) {
        img_districtMap.setImageResource(0);
        int imageId = getResources()
                .getIdentifier(
                        array_districtEng[imageIndex],
                        "drawable",
                        getPackageName()
                );

        Log.e(TAG, array_districtEng[imageIndex]);
        img_districtMap.setImageDrawable(getDrawable(imageId));
    }
}


class DistrictAndStageListAdapter extends BaseExpandableListAdapter {

    private AppCompatActivity context;
    private String[] array_districts;
    private HashMap<String, ArrayList<StageInfo>> map_stageInfo;
    private LayoutInflater inflater;
    private ChangeMapImageListener changeMapImageListener;
    private StageMapDialog beforeDialog;


    public DistrictAndStageListAdapter(AppCompatActivity context, String[] array_districts, HashMap<String, ArrayList<StageInfo>> map_stageInfo,
                                       ChangeMapImageListener changeMapImageListener) {
        this.context = context;
        this.array_districts = array_districts;
        this.map_stageInfo = map_stageInfo;
        this.changeMapImageListener = changeMapImageListener;
        this.inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

    }

    @Override
    public int getGroupCount() {
        return array_districts.length;
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        String district = array_districts[groupPosition];
        return map_stageInfo.get(district).size();
    }

    @Override
    public Object getGroup(int groupPosition) {
        return array_districts[groupPosition];
    }

    @Override
    public Object getChild(int groupPosition, int childPosition) {
        String district = array_districts[groupPosition];
        return map_stageInfo.get(district).get(childPosition);
    }

    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
        final GroupViewHolder groupViewHolder;
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.streetstage_district_item, parent, false);
            groupViewHolder = new GroupViewHolder();

            groupViewHolder.tv_district = (TextView) convertView.findViewById(R.id.district_item_tv_district);
            groupViewHolder.tv_numberOfStage = (TextView) convertView.findViewById(R.id.district_item_tv_numberofstage);
            groupViewHolder.view_selectdBar = (View) convertView.findViewById(R.id.district_item_view_bar);
            convertView.setTag(groupViewHolder);
        } else {
            groupViewHolder = (GroupViewHolder) convertView.getTag();
        }

        groupViewHolder.tv_district.setText(array_districts[groupPosition]);
        groupViewHolder.tv_numberOfStage.setText(map_stageInfo.get(array_districts[groupPosition]).size() + "개의 공연장");


        //펴짐 여부에 따른 바의 색변화 & 지도 변화
        if (isExpanded) {
            groupViewHolder.view_selectdBar.setBackgroundColor(Color.parseColor("#A789F5"));
            changeMapImageListener.onChaneMapImage(groupPosition);
        } else {
            groupViewHolder.view_selectdBar.setBackgroundColor(Color.parseColor("#DCCEFF"));
        }
        return convertView;
    }

    @Override
    public View getChildView(int groupPosition, final int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {

        final ArrayList<StageInfo> array_stageInfos = map_stageInfo.get(array_districts[groupPosition]);
        ChildViewHolder childViewHolder;

        if (convertView == null) {
            convertView = inflater.inflate(R.layout.streetstage_stageinfo_item, parent, false);
            childViewHolder = new ChildViewHolder();

            childViewHolder.tv_address = (TextView) convertView.findViewById(R.id.stageinfo_item_tv_address);
            childViewHolder.tv_placeName = (TextView) convertView.findViewById(R.id.stageinfo_item_tv_placename);
            childViewHolder.relative_background = (RelativeLayout) convertView.findViewById(R.id.stageinfo_item_rel_background);
            childViewHolder.view_line = (View) convertView.findViewById(R.id.stageinfo_item_line);
            convertView.setTag(childViewHolder);
        } else {
            childViewHolder = (ChildViewHolder) convertView.getTag();
        }

        childViewHolder.tv_address.setText(array_stageInfos.get(childPosition).getAddress());
        childViewHolder.tv_placeName.setText(array_stageInfos.get(childPosition).getPlaceName());

        //마지막 아이템 여부에 따른 뷰 설정.
        if (isLastChild) {
            childViewHolder.relative_background.setBackground(context.getDrawable(R.drawable.stage_last_item_background));
            convertView.setPadding(convertView.getPaddingLeft(), convertView.getPaddingTop(),
                    convertView.getPaddingRight(), 20);
            childViewHolder.view_line.setVisibility(View.INVISIBLE);
        } else {
            childViewHolder.relative_background.setBackground(context.getDrawable(R.drawable.stage_item_background));
            convertView.setPadding(convertView.getPaddingLeft(), convertView.getPaddingTop(),
                    convertView.getPaddingRight(), 0);
            childViewHolder.view_line.setVisibility(View.VISIBLE);
        }

        //아이템 클릭시 장소 다이얼로그 띄우기
        convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (beforeDialog != null)
                    beforeDialog.dismiss();

                StageMapDialog dialog_stageMap = StageMapDialog.newInstance(array_stageInfos.get(childPosition));
                dialog_stageMap.show(context.getSupportFragmentManager(), "DIALOG");

                beforeDialog = dialog_stageMap;
            }
        });
        return convertView;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return false;
    }

    interface ChangeMapImageListener {
        void onChaneMapImage(int imageIndex);
    }


    class GroupViewHolder {
        public TextView tv_district;
        public TextView tv_numberOfStage;
        private View view_selectdBar;
    }

    class ChildViewHolder {
        public TextView tv_placeName;
        public TextView tv_address;
        public RelativeLayout relative_background;
        public View view_line;
    }
}

