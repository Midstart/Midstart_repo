package com.example.midstart;
import android.annotation.SuppressLint;
import android.graphics.Color;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.time.LocalDate;
import java.util.ArrayList;



class CalendarAdapter extends RecyclerView.Adapter<CalendarViewHolder>
{
    private final ArrayList<LocalDate> days;
    private final OnItemListener onItemListener;


    @Override
    public long getItemId(int position) {
        return position;
    }
    public CalendarAdapter(ArrayList<LocalDate> days, OnItemListener onItemListener)
    {
        this.days = days;
        this.onItemListener = onItemListener;

    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @NonNull
    @Override
    public CalendarViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
    {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.calendar_cell, parent, false);
        ViewGroup.LayoutParams layoutParams = view.getLayoutParams();
        if(days.size() > 15) //month view
        {
            layoutParams.height = (int) (parent.getHeight() * 0.166666666);
        }

        else // week view
            layoutParams.height = (int) parent.getHeight();
        CalendarViewHolder holder=new CalendarViewHolder(view, onItemListener, days);


        return holder;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onBindViewHolder(@NonNull CalendarViewHolder holder, int position)
    {

        final LocalDate date = days.get(position);

        if(date == null)
            holder.dayOfMonth.setText("");
        else
        {
            holder.dayOfMonth.setText(String.valueOf(date.getDayOfMonth()));
            if(date.equals(CalendarUtils.selectedDate)){

                holder.parentView.setBackgroundResource(R.drawable.edge_cal); //선택 날짜 테두리 둘러지게..
            }
            else{ //선택날짜 아닌애들은 테두리 지우기
                holder.parentView.setBackgroundResource(0);
            }

            showWaterDate(holder,date); //물준날 체크

            //기록한 날 체크 테스트
            String testdate = "2021-07-10";
            if(testdate.equals(date.toString())){
                holder.calText1.setBackgroundColor(Color.parseColor("#FAECC5"));
                holder.calText1.setText("기록한 날");

            }


        }

    }



    //파이어베이스에서 기록한 날 읽고 색칠하기
    public void showWaterDate(@NonNull CalendarViewHolder holder,LocalDate date){
        /* -----------------------------------------파이어베이스---------------------------------------*/
        DatabaseReference mDatabaseRef= FirebaseDatabase.getInstance().getReference("appname");
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser(); // 로그인한 유저의 정보 가져오기
        String uid = user != null ? user.getUid() : null; // 로그인한 유저의 고유 uid 가져오기



        mDatabaseRef.addValueEventListener(new ValueEventListener() {

            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                UserAccount value =  snapshot.child("UserAccount").child(uid).getValue(UserAccount.class);


                for(int i=0;i<value.getDiaryNum();i++){
                    diary d=value.getcertainDiary(i);
                    String dDate=d.getDate().substring(0,10);


                    if(dDate.equals(date.toString())){

                        holder.calText1.setBackgroundColor(Color.parseColor("#E8D9FF"));
                        holder.calText1.setText("기록한 날");

                    }
                }


            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        /* ----------------------------------------파이어베이스----------------------------------------------*/
    }
    @Override
    public int getItemCount()
    {
        return days.size();
    }

    public interface  OnItemListener
    {
        void onItemClick(int position, LocalDate date);

    }
}