package com.example.midstart;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class DiaryActivity extends AppCompatActivity {

    Button input_btn; //일기쓰고 누르는 버튼
    ImageView cal_btn; //달력으로 이동
    Button inputAgain;//일기는 하루에 한번씩만 작성 가능. 오늘 일기 지우고 새로쓰는 버튼
    TextView date; // 오늘 날짜
    TextView hinttxt;
    private String uid;
    private DatabaseReference mDatabaseRef;  //실시간 데이터베이스
    private FirebaseUser user;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_diary);

        input_btn=(Button)findViewById(R.id.diaryBtn);
        cal_btn=(ImageView)findViewById(R.id.calendar_btn);
        inputAgain=(Button)findViewById(R.id.removediaryBtn);
        hinttxt = (TextView)findViewById(R.id.hinttxt);

        mDatabaseRef= FirebaseDatabase.getInstance().getReference("appname");
        user = FirebaseAuth.getInstance().getCurrentUser(); // 로그인한 유저의 정보 가져오기
        uid = user != null ? user.getUid() : null; // 로그인한 유저의 고유 uid 가져오기checkdiaryDate


        checkTodoDiary();

        EditText mEtdiary=findViewById(R.id.diaryInput);
        input_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String diaryContent = mEtdiary.getText().toString();
                if(diaryContent.equals(null) || diaryContent.equals("")) {
                    addDiary("일기 내용이 없삼..");
                }
                else{
                    addDiary(diaryContent);
                }

                input_btn.setText("오늘의 일기 저장하기");
                inputAgain.setText("오늘 일기 삭제하고 다시 쓰기.");
                hinttxt.setVisibility(View.VISIBLE);
                input_btn.setBackgroundResource(R.drawable.button_design_2);
                inputAgain.setBackgroundResource(R.drawable.button_design);
                input_btn.setEnabled(false);
                inputAgain.setEnabled(true);

            }
        });

        inputAgain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteDiary();
                input_btn.setText("오늘의 일기 저장하기");
                inputAgain.setText("오늘 일기 삭제하고 다시 쓰기.");
                hinttxt.setVisibility(View.GONE);
                input_btn.setBackgroundResource(R.drawable.button_design);
                inputAgain.setBackgroundResource(R.drawable.button_design_2);
                inputAgain.setEnabled(false);
                input_btn.setEnabled(true);

            }
        });


        cal_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    Intent intent = new Intent(getApplicationContext(), calendarActivity.class);
                    startActivity(intent);
                }catch(Exception e){
                    Log.v("testerr",e.getMessage());
                }
            }
        });


        String currentDate = new SimpleDateFormat("yyyy.MM.dd", Locale.getDefault()).format(new Date());
        date = (TextView) findViewById(R.id.date);
        date.setText(currentDate);



    }


    //오늘일기를 썼는지 체크하고 그에따라 버튼 비활성화 시키는 함수
    public boolean checkTodoDiary(){
        long now = System.currentTimeMillis();
        Date mDate = new Date(now);

        SimpleDateFormat simpleDate = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
        String getTime = simpleDate.format(mDate);

        final boolean[] check = new boolean[1];


        mDatabaseRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {


                UserAccount name =  snapshot.child("UserAccount").child(uid).getValue(UserAccount.class);
                if(name.checkdiaryDate(getTime)){
                    input_btn.setText("오늘의 일기 저장하기");
                    inputAgain.setText("오늘 일기 삭제하고 다시 쓰기.");
                    hinttxt.setVisibility(View.GONE);
                    input_btn.setBackgroundResource(R.drawable.button_design);
                    inputAgain.setBackgroundResource(R.drawable.button_design_2);
                    inputAgain.setEnabled(false);
                }
                else {
                    input_btn.setText("오늘의 일기 저장하기");
                    inputAgain.setText("오늘 일기 삭제하고 다시 쓰기.");
                    hinttxt.setVisibility(View.VISIBLE);
                    input_btn.setBackgroundResource(R.drawable.button_design_2);
                    inputAgain.setBackgroundResource(R.drawable.button_design);
                    input_btn.setEnabled(false);

                }


            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        // if(check[0]) Toast.makeText(getApplicationContext(),"트루-중복아님",Toast.LENGTH_SHORT).show();
        // else Toast.makeText(getApplicationContext(),"false-중복임",Toast.LENGTH_SHORT).show();

        return check[0];
    }
    //일기 추가 함수
    public void addDiary(String content){
        long now = System.currentTimeMillis();
        Date mDate = new Date(now);

        SimpleDateFormat simpleDate = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
        String getTime = simpleDate.format(mDate);


        mDatabaseRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {


                UserAccount name =  snapshot.child("UserAccount").child(uid).getValue(UserAccount.class);
                diary newD=new diary(getTime,content);
                name.addDiary(newD);
                mDatabaseRef.child("UserAccount").child(uid).setValue(name);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


    }

    //가장 최신 일기 삭제 함수
    public void deleteDiary(){
        long now = System.currentTimeMillis();
        Date mDate = new Date(now);

        SimpleDateFormat simpleDate = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
        String getTime = simpleDate.format(mDate);



        mDatabaseRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {


                UserAccount name =  snapshot.child("UserAccount").child(uid).getValue(UserAccount.class);
                name.removeTodayDiary();
                mDatabaseRef.child("UserAccount").child(uid).setValue(name);


            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


    }
}