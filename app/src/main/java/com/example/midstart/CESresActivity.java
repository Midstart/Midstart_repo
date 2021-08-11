package com.example.midstart;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class CESresActivity extends AppCompatActivity {

    TextView scoreText;
    Button homeBtn;
    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cesres);

        homeBtn=findViewById(R.id.ces_res);
        //메인 액티비티로 이동
        homeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CESActivity.resetScore();
                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(intent);
            }
        });
        //점수와 피드백 세팅
        scoreText=findViewById(R.id.result_score);
        String score=CESActivity.getScore();
        scoreText.setText(score);

        TextView des1=(TextView)findViewById(R.id.result_des);
        TextView des2=findViewById(R.id.more_des);
        des1.setText(CESActivity.getResult1());
        des2.setText(CESActivity.getResult2());




    }
}