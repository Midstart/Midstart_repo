package com.example.midstart;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.smarteist.autoimageslider.IndicatorView.animation.type.IndicatorAnimationType;
import com.smarteist.autoimageslider.SliderAnimations;
import com.smarteist.autoimageslider.SliderView;

import java.util.ArrayList;
import java.util.List;

public class CardNews2 extends AppCompatActivity {

    SliderView sliderView;
    int[] images = {R.drawable.news3_1,
            R.drawable.news3_2,
            R.drawable.news3_3,
            R.drawable.news3_4,
            R.drawable.news3_5,};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_card_news2);

        sliderView = findViewById(R.id.slider2);

        SliderAdapter sliderAdapter = new SliderAdapter(images,2);
        sliderView.setSliderAdapter(sliderAdapter);
        sliderView.setIndicatorAnimation(IndicatorAnimationType.WORM);
        sliderView.setSliderTransformAnimation(SliderAnimations.DEPTHTRANSFORMATION);
        sliderView.startAutoCycle();
    }
}