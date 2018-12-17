package com.teamnoname.streetartzone.StreetGroup;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.teamnoname.streetartzone.R;

public class StreetGroupDetailImgActivity extends AppCompatActivity {

    ImageView imgV;
    ImageButton btn_back;
    String imagePath;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_streetgroup_detail_img);

        imgV = (ImageView) findViewById(R.id.group_detail_img);
        btn_back = (ImageButton) findViewById(R.id.group_detail_img_back);

        btn_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        Intent intent = getIntent();
        imagePath = intent.getExtras().getString("path");
        Glide.with(StreetGroupDetailImgActivity.this).load(imagePath).into(imgV);


    }
}
