package com.teamnoname.streetartzone.StreetGroup;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.teamnoname.streetartzone.BuildConfig;
import com.teamnoname.streetartzone.Data.TicketData;
import com.teamnoname.streetartzone.R;

import java.io.File;

import io.realm.Realm;

public class TicketImage extends AppCompatActivity {

    ImageView imgV_ticketImage;
    LinearLayout linearLayout_topMenu;
    ImageButton btn_back;
    Button btn_share;
    Button btn_delete;

    String coverPath;
    String filePath;
    boolean isListing=false;

    Realm realm;

    public TicketImage() {
        realm = Realm.getDefaultInstance();
    }


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_ticket);

        Intent intent = getIntent();
        filePath = intent.getExtras().getString("filePath");
        coverPath = intent.getExtras().getString("coverPath");
        isListing = intent.getBooleanExtra("isListing",false);


        imgV_ticketImage = (ImageView) findViewById(R.id.ticket_final_image);
        linearLayout_topMenu = (LinearLayout) findViewById(R.id.ticket_final_top);
        btn_back = (ImageButton) findViewById(R.id.ticket_final_back);
        btn_share = (Button) findViewById(R.id.ticket_final_share);
        btn_delete = (Button) findViewById(R.id.ticket_final_delete);

        Glide.with(TicketImage.this)
                .load(filePath)
                .listener(new RequestListener<Drawable>() {
                    @Override
                    public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                        if(!isListing){
                            shareTicket();
                        }

                        return false;
                    }
                })
                .into(imgV_ticketImage);

        btn_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        btn_share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                shareTicket();

            }
        });

        btn_delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                //삭제 진행 다이얼로그 띄우기
                AlertDialog.Builder builder = new AlertDialog.Builder(TicketImage.this);
                builder.setMessage("티켓을 삭제합니다.")
                        .setPositiveButton("삭제", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                                File file = new File(filePath);

                                if (file.exists()) {
                                    //파일 존재 확인
                                    if (file.delete()) {

                                        refreshGallery(file);

                                        realm.executeTransaction(new Realm.Transaction() {
                                            @Override
                                            public void execute(Realm realm) {
                                                TicketData ticketData = realm.where(TicketData.class).equalTo("ticketPath",filePath).findFirst();
                                                if(ticketData!=null){
                                                    ticketData.deleteFromRealm();
                                                }

                                            }
                                        });

                                        TicketImage.this.finish();

                                    }
                                } else {
                                    Log.v("TicketImage", "티켓 삭제 에러 : 파일이 존재하지 않음");
                                }
                            }
                        })
                        .setNegativeButton("취소", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        });

                //다이얼로그 생성
                AlertDialog shareDialog = builder.create();
                shareDialog.show();


            }
        });

        imgV_ticketImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(linearLayout_topMenu.getVisibility()==View.VISIBLE){
                    linearLayout_topMenu.setVisibility(View.GONE);

                } else if(linearLayout_topMenu.getVisibility()==View.GONE){
                    linearLayout_topMenu.setVisibility(View.VISIBLE);
                }
            }
        });

        if(!isListing){
            saveTicket();
        }



    }

    private void saveTicket(){
        realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                TicketData ticketData = new TicketData();
                ticketData.setTicketPath(filePath);
                ticketData.setCoverPath(coverPath);
                realm.copyToRealm(ticketData);
            }
        });
    }

    private void refreshGallery(File file) {

        Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        mediaScanIntent.setData(Uri.fromFile(file));
        TicketImage.this.sendBroadcast(mediaScanIntent);

    }

    private void shareTicket(){

        //보고 있는 이미지 파일 선택
        File shareFile = new File(filePath);

        if (shareFile.exists()) {
            //파일 존재 여부 검사

            Uri uri = FileProvider.getUriForFile(TicketImage.this, BuildConfig.APPLICATION_ID + ".provider",shareFile);

            Intent shareIntent = new Intent();
            shareIntent.setAction(Intent.ACTION_SEND); //전송 메소드 호출
            shareIntent.putExtra(Intent.EXTRA_STREAM, uri); //사진 uri 저장
            shareIntent.setType("image/*"); //이미지 공유 Type 지정

            TicketImage.this.startActivity(shareIntent.createChooser(shareIntent, "초대장 보내기")); //공유할 어플 선택

        }
    }
}
