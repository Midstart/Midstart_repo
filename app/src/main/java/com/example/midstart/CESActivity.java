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

public class CESActivity extends AppCompatActivity {

    ImageView back_btn;

    Button res_btn;
    int questionNum=20; //질문 갯수
    public static int score=0; //검사 결과 점수
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cesactivity);
        back_btn=(ImageView) findViewById(R.id.ces_back_btn);
        res_btn=findViewById(R.id.ces_res);

        back_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(intent);
            }
        });

        //질문 리스트 20개
        String [] quesList={
                "평소에는 아무렇지도 않은 일들이 괴롭고 귀찮게 느껴졌다.",
                "먹고 싶지 않고 식욕이 없다.",
                "어느 누가 도와준다고 하더라도 나의 울적한 기분을 떨쳐 버릴 수 없을 것 같다.",
                "무슨 일을 하든 정신을 집중하기 힘들었다.",
                "비교적 잘 지냈다.",
                "상당히 우울했다.",
                "모든 일들이 힘들게 느껴졌다.",
                "앞일이 암담하게 느껴졌다.",
                "지금까지의 내 인생을 실패작이라는 생각이 들었다.",
                "적어도 보통 사람들만큼의 능력은 있다고 생각한다.",
                "잠을 설쳤다.(잠을 잘 이루지 못했다.)",
                "두려움을 느꼈다.",
                "평소에 비해 말수가 적었다.",
                "세상에 홀로 있는 듯한 외로움을 느꼈다.",
                "큰 불만 없이 생활했다.",
                "사람들이 나에게 차갑게 대하는 것 같았다.",
                "갑자기 울음이 나왔다.",
                "마음이 슬펐다.",
                "사람들이 나를 싫어하는 것 같았다.",
                "도무지 뭘 해나갈 엄두가 나지 않았다."
        };

        //score=0;
        //루프를 돌면서 질문들을 각 번호에 맞게 quesList에 있는 애들로 setText 해준다.
        for(int i=1;i<=questionNum;i++){
            String name="q"+String.valueOf(i); //해당되는 질문 번호를 찾아서
            //그 하위에 있는 textview와 radio_group를 찾는다.
            int resID = getResources().getIdentifier(name, "id", "com.example.midstart");
            View questSetView = (View)findViewById(resID);
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
            //5번,10번,15번 질문은 점수 매기는 기준이 거꾸로
            if(i==5||i==10||i==15){
                scoreList= new int[]{3, 2, 1, 0};
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
                Intent intent = new Intent(getApplicationContext(), CESresActivity.class);
                startActivity(intent);
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
        if(score<=15){
            return "건강한 상태입니다.";
        }
        else if(score<=20){
            return "약간 우울한 상태입니다.";
        }
        else if(score<=24){
            return "다소 우울한 상태입니다.";
        }
        else{
            return "매우 우울한 상태입니다.";
        }
    }
    public static String getResult2(){
        if(score<=15){
            return "우울증 검진 결과가 15점 이하로 정신이 아주 건강한 상태라고 볼 수 있습니다!앞으로도 지금처럼만 관리해주세요~*^^*";
        }
        else if(score<=20){
            return "우울증 검진 결과가 16~20점 사이로, 약간의 스트레스 관리가 필요합니다. 좋아하는 일을 하거나 맛있는 걸 먹으며 기분을 환기해보는 것은 어떨까요?";
        }
        else if(score<=24){
           return "우울증 검진 결과가 21~24점 사이로, 주의가 필요한 수준입니다. 2주 이상 상태가 지속된다면 전문의와의 상담이 권장됩니다.";
        }
        else{
            return "우울증 검진 결과가 25점 이상으로, 정신건강의학과 전문의 상담과 도움이 필요합니다. 우울증 치료에 중요한 것은 본인의 의지입니다. 전문기관의 도움을 받는 것을 두려워하지 마세요!";
        }
    }

}