package com.example.midstart;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

public class CardNewsActivity extends AppCompatActivity {

    ImageView cardnews1;
    ImageView cardnews2;
    ImageView back_btn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_card_news);

        cardnews1 = (ImageView) findViewById(R.id.news1);
        cardnews1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), CardNews1.class);
                startActivity(intent);
            }
        });

        cardnews2 = (ImageView) findViewById(R.id.news2);
        cardnews2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), CardNews2.class);
                startActivity(intent);
            }
        });

        back_btn  = (ImageView) findViewById(R.id.imageView2);
        back_btn.setOnClickListener(new View.OnClickListener() {
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
    }
}