package com.teamnoname.streetartzone.StreetGroup;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.teamnoname.streetartzone.R;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class WriteReview extends AppCompatActivity {

    ImageButton btn_back;
    TextView btn_finish;
    EditText edit_review;
    EditText edit_writer;
    RatingBar ratingBar;

    String myReview;
    String writer;
    int ratingScore;
    int selectedSeq;

    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;

    boolean isDialog=false;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_streetgroup_write_review);

        Intent intent = getIntent();
        selectedSeq = intent.getIntExtra("seq", 0);

        btn_back = (ImageButton) findViewById(R.id.group_write_review_back);
        btn_finish = (TextView) findViewById(R.id.group_write_review_finish);
        edit_review = (EditText) findViewById(R.id.group_write_review_edit);
        ratingBar = (RatingBar) findViewById(R.id.group_write_review_rating);
        edit_writer = (EditText) findViewById(R.id.group_write_review_writer);

        btn_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });


        btn_finish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                        WriteReview.this);

                alertDialogBuilder.setTitle("공연후기를 등록하시겠습니까?");


                alertDialogBuilder
                        .setCancelable(false)
                        .setPositiveButton("등록",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(
                                            DialogInterface dialog, int id) {

                                        ratingScore = (int) ratingBar.getRating();
                                        myReview = edit_review.getText().toString();
                                        writer = edit_writer.getText().toString();

                                        if(writer.equals("")){
                                            Toast.makeText(WriteReview.this, "작성자를 입력해주세요", Toast.LENGTH_SHORT).show();
                                        }else if(myReview.equals("")){
                                            Toast.makeText(WriteReview.this, "후기를 입력해주세요", Toast.LENGTH_SHORT).show();
                                        }else{
                                            registerReview(selectedSeq, ratingScore, myReview, writer);
                                        }



                                    }
                                })
                        .setNegativeButton("취소",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(
                                            DialogInterface dialog, int id) {

                                        dialog.cancel();
                                        isDialog=false;
                                    }
                                });



                        if(!isDialog){
                            alertDialogBuilder.show();
                        }
                        isDialog=true;





            }
        });

    }

    public void registerReview(int seq, int score, String contents, String name) {
        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference("groupdata/groupreview");
        DatabaseReference review = databaseReference.push();
        review.child("writer").setValue(name);
        review.child("seq").setValue(seq);
        review.child("score").setValue(score);
        review.child("contents").setValue(contents);

        SimpleDateFormat formatter = new SimpleDateFormat("yyyy.MM.dd", Locale.KOREA);
        Date date = new Date();
        String currentDate = formatter.format(date);

        review.child("date").setValue(currentDate);

        WriteReview.this.finish();


    }
}
