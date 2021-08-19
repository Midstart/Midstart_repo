package com.example.midstart;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.SimpleItemAnimator;

import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
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
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class calendarActivity extends AppCompatActivity implements CalendarAdapter.OnItemListener, View.OnClickListener {

    private FirebaseAuth mFirebaseAuth; //파이어베이스 인증
    private DatabaseReference mDatabaseRef;  //실시간 데이터베이스
    private String uid;

    TextView text;
    Button btn;

    TextView monthYearText;
    RecyclerView calendarRecyclerView;
    Button btn_calendar;
    View calCell;


    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calendar);


        mFirebaseAuth = FirebaseAuth.getInstance();
        mDatabaseRef = FirebaseDatabase.getInstance().getReference("appname");
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser(); // 로그인한 유저의 정보 가져오기
        uid = user != null ? user.getUid() : null; // 로그인한 유저의 고유 uid 가져오기

/*
        text=findViewById(R.id.test);
        btn=findViewById(R.id.home);
        mDatabaseRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                    UserAccount value = snapshot.child("UserAccount").child(uid).getValue(UserAccount.class);
                    text.setText(value.getDiaryListAll());
                }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });



 */
        btn=findViewById(R.id.cal_home);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(intent);
            }
        });

        calendarRecyclerView = findViewById(R.id.calendarRecyclerView);
        calCell = findViewById(R.id.parentView);
        monthYearText = findViewById(R.id.monthYearTV);
        CalendarUtils.selectedDate = LocalDate.now();
        CalendarUtils.firstLoad = true;

        setMonthView();

        Button prev_btn = findViewById(R.id.previousmonth_btn);
        prev_btn.setOnClickListener(this);
        Button next_btn = findViewById(R.id.nextmonth_btn);
        next_btn.setOnClickListener(this);


    }

    CalendarAdapter calendarAdapter;
    RecyclerView.LayoutManager layoutManager;

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void setMonthView() {

        monthYearText.setText(monthYearFromDate(CalendarUtils.selectedDate));
        ArrayList<LocalDate> daysInMonth = daysInMonthArray(CalendarUtils.selectedDate);

        calendarAdapter = new CalendarAdapter(daysInMonth, this);
        if (calendarRecyclerView == null) {
            Toast.makeText(getApplicationContext(), "null", Toast.LENGTH_SHORT).show();
        }

        //수정
        layoutManager = new GridLayoutManager(getApplicationContext(), 7);


        RecyclerView.ItemAnimator animator = calendarRecyclerView.getItemAnimator();
        if (animator instanceof SimpleItemAnimator) {
            ((SimpleItemAnimator) animator).setSupportsChangeAnimations(false);
        }
        calendarAdapter.setHasStableIds(true); //깜빡임 없도록
        calendarRecyclerView.setLayoutManager(layoutManager);
        calendarRecyclerView.setAdapter(calendarAdapter);


    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public static ArrayList<LocalDate> daysInMonthArray(LocalDate date) {
        ArrayList<LocalDate> daysInMonthArray = new ArrayList<>();
        YearMonth yearMonth = YearMonth.from(date);

        int daysInMonth = yearMonth.lengthOfMonth();

        LocalDate firstOfMonth = CalendarUtils.selectedDate.withDayOfMonth(1);
        int dayOfWeek = firstOfMonth.getDayOfWeek().getValue();

        for (int i = 1; i <= 42; i++) {
            if (i <= dayOfWeek || i > daysInMonth + dayOfWeek)
                daysInMonthArray.add(null);
            else
                daysInMonthArray.add(LocalDate.of(CalendarUtils.selectedDate.getYear(), CalendarUtils.selectedDate.getMonth(), i - dayOfWeek));
        }
        return daysInMonthArray;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private String monthYearFromDate(LocalDate date) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMMM yyyy");
        return date.format(formatter);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public void previousMonthAction(View view) {
        CalendarUtils.selectedDate = CalendarUtils.selectedDate.minusMonths(1);
        setMonthView();
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public void nextMonthAction(View view) {
        CalendarUtils.selectedDate = CalendarUtils.selectedDate.plusMonths(1);
        setMonthView();
    }
    LocalDate tempDate;
    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onItemClick(int position, LocalDate date) {
        if (date != null) {

            CalendarUtils.selectedDate = date; //yyyy-MM-dd 형식
            CalendarUtils.firstLoad = false;

            //더블클릭한경우
            if(tempDate!=null){
                if(tempDate.equals(date)){
                    //Toast.makeText(getApplicationContext(),"더블클릭",Toast.LENGTH_SHORT).show();

                        mDatabaseRef.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                UserAccount value = snapshot.child("UserAccount").child(uid).getValue(UserAccount.class);
                                for(int i=0;i<value.getDiaryNum();i++){
                                    diary d=value.getcertainDiary(i);
                                    String dDate=d.getDate().substring(0,10);


                                    if(dDate.equals(date.toString())){
                                        String s=d.getDate()+"\n"+"질문내용\n"+d.getDiary();
                                        mOnPopupClick(s);


                                    }
                                }


                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });



                }
            }
            tempDate=CalendarUtils.selectedDate;

            //이걸 쓰면 기록한 날은 갱신 x 클릭한 날짜 테두리만 갱신할 수 있다. (깜빡임 해결!!)
            calendarAdapter.notifyDataSetChanged();



        }
    }

    //팝업 띄우는 함수
    public void mOnPopupClick(String s){
        //데이터 담아서 팝업(액티비티) 호출
        Intent intent = new Intent(this, PopupActivity.class);
        intent.putExtra("data",s);
        startActivityForResult(intent, 1);
    }







    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.previousmonth_btn:
                previousMonthAction(v);
                break;
            case R.id.nextmonth_btn:
                nextMonthAction(v);
                break;

        }


    }
}