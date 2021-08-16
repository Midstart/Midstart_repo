package com.example.midstart;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

public class KuppermanActivity extends AppCompatActivity {

    ImageView back_btn;
    Button res_btn;
    int questionNum=11; //질문 갯수
    public static int score=0; //검사 결과 점수

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_kupperman);
        back_btn=(ImageView) findViewById(R.id.ces_back_btn);
        res_btn=findViewById(R.id.k_res);

        back_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                resetScore();
                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(intent);
            }
        });

        //질문 리스트 11개
        String [] quesList={
                "안면홍조(얼굴 화끈거림) 증상이 있다.",
                "발한(등 뒤로 땀이 흐름) 증상이 있다.",
                "불면증이 있다.",
                "신경질이 자주 난다.",
                "우울하다.",
                "어지러움을 느낀다.",
                "피로감을 느낀다.",
                "관절통과 근육통이 있다.",
                "두통이 있다.",
                "가슴 두근거림 증상이 있다.",
                "질 건조, 분비물 감소 증상이 있다.",

        };

        //score=0;
        //루프를 돌면서 질문들을 각 번호에 맞게 quesList에 있는 애들로 setText 해준다.
        for(int i=1;i<=questionNum;i++){
            String name="t"+String.valueOf(i); //해당되는 질문 번호를 찾아서
            //그 하위에 있는 textview와 radio_group를 찾는다.
            int resID = getResources().getIdentifier(name, "id", "com.example.midstart");
            View questSetView = (View)findViewById(resID);

            TextView opt1 = (TextView)questSetView.findViewById(R.id.A);
            TextView opt2 = (TextView)questSetView.findViewById(R.id.B);
            TextView opt3 = (TextView)questSetView.findViewById(R.id.C);
            TextView opt4 = (TextView)questSetView.findViewById(R.id.D);
            opt1.setText("없음");
            opt2.setText("약간");
            opt3.setText("보통");
            opt4.setText("심함");


            TextView question = (TextView)questSetView.findViewById(R.id.ques);
            TextView quesNum=(TextView)questSetView.findViewById(R.id.questionNum);
            // 기존에 씌여져 있던 텍스트(질문)을 변경한다.
            if(i<=9){
                quesNum.setText("0"+String.valueOf(i));
            }
            else{
                quesNum.setText(String.valueOf(i));
            }
            question.setText(quesList[i-1]);

            int scoreList [];

            if(i==1){
                scoreList= new int[]{0, 4, 8, 12};
            }
            else if(i==2||i==3||i==4){
                scoreList= new int[]{0, 2, 4, 6};
            }
            else{
                scoreList= new int[]{0, 1, 2, 3};
            }

            //그 질문의 점수 계산
            RadioGroup radioGroup = questSetView.findViewById(R.id.ques_radio_group);
            final int[] tempscore = new int[1];
            radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(RadioGroup group, int checkedId) {
                    switch (checkedId){
                        case R.id.A:  //1일 이하
                            tempscore[0] =scoreList[0];
                            break;
                        case R.id.B:  //1-2일
                            tempscore[0] +=scoreList[1];
                            break;
                        case R.id.C:  //3-4일
                            tempscore[0] +=scoreList[2];
                            break;
                        case R.id.D: //5-7일
                            tempscore[0] +=scoreList[3];
                            break;
                    }
                    score+=tempscore[0];
                }

            });


        }

        //결과 확인 액티비티로 이동
        res_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), KresActivity.class);
                startActivity(intent);
                //Toast.makeText(getApplicationContext(),"sss",Toast.LENGTH_SHORT).show();

            }
        });








    }

    public static String getScore(){
        return String.valueOf(score)+"점";
    }
    public static void resetScore(){
        score=0;
    }
    public static String getResult1(){
        if(score<=10){
            return "양호한 상태입니다.";
        }
        else if(score<=16){
            return "보통입니다.";
        }
        else{
            return "관리가 필요한 상태입니다.";
        }
    }
    public static String getResult2(){
        if(score<=10){
            return "쿠퍼만 갱년기 지수가 10점 이하로, 몸 상태는 매우 좋습니다! 하지만 미래를 위해 관리를 시작하는 것이 좋습니다.";
        }
        else if(score<=16){
            return "매우 좋은 상태는 아니며 관리가 필요한 상황입니다. 식습관을 고치고 규칙적인 운동을 시작하는 것이 좋습니다.";
        }
        else {
            return "전문의와의 상담이 필요합니다. 정밀 검사를 통해 정확한 진단을 받고, 치료 방법에 대해서도 전문의와 상의하는 것이 좋습니다.";
        }

    }



}