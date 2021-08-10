package com.example.midstart;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.smarteist.autoimageslider.IndicatorView.animation.type.IndicatorAnimationType;
import com.smarteist.autoimageslider.SliderAnimations;
import com.smarteist.autoimageslider.SliderView;

import java.util.ArrayList;
import java.util.List;

public class CardNews1 extends AppCompatActivity {

    SliderView sliderView;
    int[] images = {R.drawable.news1_1,
            R.drawable.news1_2,
            R.drawable.news1_3,
            R.drawable.news1_4,
            R.drawable.news1_5,
            R.drawable.news1_6,
            R.drawable.news1_7,
            R.drawable.news1_8};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_card_news1);

        sliderView = findViewById(R.id.slider1);

        SliderAdapter sliderAdapter = new SliderAdapter(images);
        sliderView.setSliderAdapter(sliderAdapter);
        sliderView.setIndicatorAnimation(IndicatorAnimationType.WORM);
        sliderView.setSliderTransformAnimation(SliderAnimations.DEPTHTRANSFORMATION);
        sliderView.startAutoCycle();
    }
}