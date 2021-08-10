package com.example.midstart;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

public class CardNewsActivity extends AppCompatActivity {

    ImageView cardnews1;

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
    }
}