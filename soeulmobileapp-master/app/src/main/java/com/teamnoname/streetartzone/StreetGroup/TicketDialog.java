package com.teamnoname.streetartzone.StreetGroup;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.View;
import android.view.Window;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.Target;
import com.teamnoname.streetartzone.R;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;

public class TicketDialog extends Dialog {

    ImageView imgV_ticket;
    TextView tv_groupName;
    TextView tv_date;
    TextView tv_place;
    TextView tv_time;
    ImageView imgV_top;
    EditText edit_msg;
    ImageView imgV_msg;
    ImageView imgV_sending;
    LinearLayout linearLayout_ticket;


    String month;
    String day;
    String place;
    String start;
    String end;

    String imagePath;
    Context context;

    String ticketPath;

    boolean isComplete=false;
    

    public String getImagePath() {
        return imagePath;
    }

    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }

    public TicketDialog(@NonNull Context context) {
        super(context);
        this.context=context;


    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        setContentView(R.layout.dialog_ticket);


        imgV_ticket = (ImageView) findViewById(R.id.dialog_ticket_image);
        tv_groupName = (TextView) findViewById(R.id.dialog_ticket_groupName);
        tv_date = (TextView) findViewById(R.id.dialog_ticket_date);
        tv_place = (TextView) findViewById(R.id.dialog_ticket_place);
        tv_time = (TextView) findViewById(R.id.dialog_ticket_time);

        imgV_top = (ImageView) findViewById(R.id.ticket_top);
        edit_msg = (EditText) findViewById(R.id.ticket_edit);
        imgV_msg = (ImageView) findViewById(R.id.ticket_edit_background);


        imgV_sending = (ImageView) findViewById(R.id.ticket_sending);
        imgV_sending.setMinimumWidth(imgV_top.getWidth());

        linearLayout_ticket = (LinearLayout) findViewById(R.id.ticket_total);


        Glide.with(context)
                .load(imagePath)
                .apply(new RequestOptions().override(imgV_top.getWidth()))
                .listener(new RequestListener<Drawable>() {
                    @Override
                    public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                        tv_groupName.setText(StreetGroupDetailActivity.groupName);
                        tv_date.setText(getMonth()+"/"+getDay());
                        tv_place.setText(getPlace());
                        tv_time.setText(getStart()+" ~ "+getEnd());
                        edit_msg.setWidth(imgV_msg.getWidth());


                        return false;
                    }
                })
                .into(imgV_ticket);

        imgV_ticket.setColorFilter(R.color.dim);


        imgV_sending.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(!isComplete){
                    imgV_sending.setImageResource(R.drawable.ticket_bottom);
                    edit_msg.setCursorVisible(false);
                    if(edit_msg.getText().toString().equals("")){
                        edit_msg.setText("  ");
                    }

                    isComplete=true;
                    picCapture();
                }



            }
        });


    }

    public void picCapture(){


        linearLayout_ticket.buildDrawingCache();
        Bitmap captureView = linearLayout_ticket.getDrawingCache();

        FileOutputStream fos;
        String strFolderPath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/busker/ticket";

        File folder = new File(strFolderPath);
        if(!folder.exists()) {
            folder.mkdirs();
        }

        ticketPath = strFolderPath + "/" + System.currentTimeMillis() + ".png";
        File fileCacheItem = new File(ticketPath);



        try {
            fos = new FileOutputStream(fileCacheItem);
            captureView.compress(Bitmap.CompressFormat.PNG, 100, fos);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } finally {

            Intent intent = new Intent(context,TicketImage.class);
            intent.putExtra("filePath",ticketPath); //티켓 이미지
            intent.putExtra("coverPath",imagePath); //티켓 배경 이미지
            context.startActivity(intent);
            Toast.makeText(context, "초대장은 공연티켓 메뉴에서 확인하실 수 있습니다.", Toast.LENGTH_SHORT).show();
            TicketDialog.this.dismiss();
        }
    }



    public String getMonth() {
        return month;
    }

    public void setMonth(String month) {
        this.month = month;
    }

    public String getDay() {
        return day;
    }

    public void setDay(String day) {
        this.day = day;
    }

    public String getPlace() {
        return place;
    }

    public void setPlace(String place) {
        this.place = place;
    }

    public String getStart() {
        return start;
    }

    public void setStart(String start) {
        this.start = start;
    }

    public String getEnd() {
        return end;
    }

    public void setEnd(String end) {
        this.end = end;
    }
}
