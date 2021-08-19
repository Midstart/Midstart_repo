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

    TextView question;// 질문
    TextView hinttxt;

    ImageView diaryBack_btn; //뒤로가기 버튼

    TextView level; //일기레벨
    TextView diaryNum; //일기갯수
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

        level=findViewById(R.id.diaryLevel);
        diaryNum=findViewById(R.id.diaryNum);

        mDatabaseRef= FirebaseDatabase.getInstance().getReference("appname");
        user = FirebaseAuth.getInstance().getCurrentUser(); // 로그인한 유저의 정보 가져오기
        uid = user != null ? user.getUid() : null; // 로그인한 유저의 고유 uid 가져오기checkdiaryDate


        checkTodoDiary();


        diaryBack_btn  = (ImageView) findViewById(R.id.imageView2);
        diaryBack_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                    startActivity(intent);
                }catch(Exception e){
                    Log.v("testerr",e.getMessage());
                }
            }
        });
        EditText mEtdiary=findViewById(R.id.diaryInput);
        input_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String diaryContent = mEtdiary.getText().toString();
                if(diaryContent.equals(null) || diaryContent.equals("")) {
                    addDiary("질문에 답변을 하지 않았습니다.");
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
        //질문 리스트 16개
        String [] quesList={
                "Q. 오늘 하루중 기억에 남기고 싶은 것은?",
                "Q. 죽기 전에 꼭 하고 싶은 일이 있다면?",
                "Q. 나에게 주고 싶은 선물이 있다면?",
                "Q. 나의 신조는 무엇인가?",
                "Q.닮고 싶은 문학작품 속 주인공은?.",
                "Q. _____은 재미있다.",
                "Q. 여름이 좋은가, 겨울이 좋은가?",
                "Q. 내 묘비에 남기고 싶은 말은?",
                "Q. 현실에 안주하고 싶은가, 흥분되는 도전을 원하는가?",
                "Q. 자신의 몸에 대해 어떻게 생각하는가?",
                "Q. 내가 한 거짓말 중에 가장 큰 파장을 불러일으켰던 것은?",
                "Q. 최근에 기분 좋은 대화를 나눈 사람의 이름을 적어보자.",
                "Q. 지금 사랑하고 있는가?",
                "Q. 오늘 있었던 일 중에서 지우고 싶은 기억이 있다면?",
                "Q. 집이란 무엇이라고 생각하는가?",
                "Q. 오늘 하루 슬퍼할 일이 있었는가?",

        };

        String currentDate = new SimpleDateFormat("yyyy.MM.dd", Locale.getDefault()).format(new Date());
        date = (TextView) findViewById(R.id.date);
        date.setText(currentDate);
        int tempDate=Integer.parseInt(currentDate.substring(8,10))%16;
        question = (TextView) findViewById(R.id.question_today);
        question.setText(quesList[tempDate]);





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
                int num=name.getDiaryNum()-1;
                String n= String.valueOf(num);
                diaryNum.setText("지금까지 "+n+"개의 일기를 작성했습니다." );
                if(num==0){
                    level.setText("00");
                }
                else if(num==1){
                    level.setText("01");
                }
                else if(num==2){
                    level.setText("02");
                }
                else{
                    level.setText("03");
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