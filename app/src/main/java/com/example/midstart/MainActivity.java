package com.example.midstart;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONObject;

public class MainActivity extends AppCompatActivity implements SensorEventListener {

    private FirebaseAuth mFirebaseAuth; //파이어베이스 인증
    private DatabaseReference mDatabaseRef;  //실시간 데이터베이스

    private SensorManager sensorManager;
    private Sensor sensor;
    private boolean isSensor;   // 센서 사용 가능한지
    private TextView stepSinceReboot;   // 걸음수
    int stepNow;

    // 날씨, 위치 관련
    final String APP_ID = "ac5471e3caa6df5bb40fbe111f57c735";
    final String WEATHER_URL = "https://api.openweathermap.org/data/2.5/weather";
    final long MIN_TIME = 5000; // 5sec
    final float MIN_DISTANCE = 1000;
    final int REQUEST_CODE = 101;


    TextView weatherState, Temperature;
    ImageView mweatherIcon;
    LocationManager mLocationManager;
    LocationListener mLocationListner;

    ImageView cardNews;  //카드뉴스
    ImageView cesTest; //우울증검사
    ImageView KTest; //갱년기검사
    ImageView diary; //일기


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mFirebaseAuth= FirebaseAuth.getInstance();
        mDatabaseRef= FirebaseDatabase.getInstance().getReference("appname");
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser(); // 로그인한 유저의 정보 가져오기
        String uid = user != null ? user.getUid() : null; // 로그인한 유저의 고유 uid 가져오기
      
        TextView name=findViewById(R.id.settingName);

        weatherState = (TextView) findViewById(R.id.weatherCondition); // 날씨
        Temperature = (TextView) findViewById(R.id.temperature); // 온도
        mweatherIcon = (ImageView) findViewById(R.id.weatherIcon); // 날씨 아이콘
        mLocationManager = (LocationManager)this.getSystemService(Context.LOCATION_SERVICE);
        boolean isGPSEnabled = mLocationManager.isProviderEnabled(LocationManager.GPS_PROVIDER); // gps 가능 여부부        boolean isNetworkEnabled = mLocationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER); // 네트워크 가능 여부

        //상단에 로그인한 유저의 닉네임, 이메일 표시
        mDatabaseRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                try {
                    UserAccount value = snapshot.child("UserAccount").child(uid).getValue(UserAccount.class);
                    name.setText(value.getName() + " 님 안녕하세요!\n오늘도 즐거운 하루를 시작해봐요.");
                }catch(Exception e){
                    Log.v("testerr",e.getMessage());
                }




            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        stepSinceReboot = (TextView) findViewById(R.id.totalSteps);
        sensorManager = (SensorManager) this.getSystemService(Context.SENSOR_SERVICE);
        if(sensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER)!=null){
            sensor = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER);
            isSensor = true;
        }
        else{
            isSensor = false;
        }

        //카드뉴스로 이동
        cardNews = (ImageView) findViewById(R.id.thumbnail2);
        cardNews.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), CardNewsActivity.class);
                startActivity(intent);
            }
        });

        //우울증 검사로 이동
        cesTest= (ImageView) findViewById(R.id.thumbnail3);
        cesTest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), CESActivity.class);
                startActivity(intent);
            }
        });

        // 채팅 화면으로 이동
        ImageView chat = (ImageView) findViewById(R.id.btn_chat_trans);
        chat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), ChatBotActivity.class);
                startActivity(intent);
            }
        });

        //갱년기 검사로 이동
        KTest= (ImageView) findViewById(R.id.thumbnail4);
        KTest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), KuppermanActivity.class);
                startActivity(intent);
            }
        });

        //일기로 이동
        //cardView11
        diary= (ImageView) findViewById(R.id.thumbnail5);
        diary.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), DiaryActivity.class);
                startActivity(intent);
            }
        });








    }

    @Override
    protected void onResume() {
        super.onResume();
        if(isSensor){
            sensorManager.registerListener(this,sensor,SensorManager.SENSOR_DELAY_NORMAL);
        }
        Intent mIntent = getIntent();
        String city = mIntent.getStringExtra("City");
        if (city != null) {
            getWeatherForNewCity(city);
        } else {
            getWeatherForCurrentLocation();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if(isSensor){
            sensorManager.unregisterListener(this);
        }
        if(mLocationManager!=null)
        {
            mLocationManager.removeUpdates(mLocationListner);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        sensorManager = null;
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        stepSinceReboot.setText(String.valueOf((int)event.values[0]));
        stepNow = (int)event.values[0];
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    private void getWeatherForCurrentLocation() {

        mLocationManager = (LocationManager)getSystemService(Context.LOCATION_SERVICE);

        mLocationListner = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {

                String Latitude = String.valueOf(location.getLatitude());
                String Longitude = String.valueOf(location.getLongitude());

                RequestParams params = new RequestParams();
                params.put("lat", Latitude);
                params.put("lon", Longitude);
                params.put("appid", APP_ID);
                letsdoSomeNetworking(params);

            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {

            }

            @Override
            public void onProviderEnabled(String provider) {

            }

            @Override
            public void onProviderDisabled(String provider) {
                //not able to get location
                Toast.makeText(MainActivity.this,"Not able to get location", Toast.LENGTH_SHORT).show();
            }
        };


        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.ACCESS_FINE_LOCATION},REQUEST_CODE);
            return;
        }
        // mLocationManager.requestLocationUpdates(Location_Provider, MIN_TIME, MIN_DISTANCE, mLocationListner);
        mLocationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, MIN_TIME, MIN_DISTANCE, mLocationListner);
        mLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, MIN_TIME, MIN_DISTANCE, mLocationListner);

    }

    private void getWeatherForNewCity(String city) {
        RequestParams params = new RequestParams();
        params.put("q", city);
        params.put("appid", APP_ID);
        letsdoSomeNetworking(params);
    }
    private  void letsdoSomeNetworking(RequestParams params)
    {
        AsyncHttpClient client = new AsyncHttpClient();
        client.get(WEATHER_URL,params,new JsonHttpResponseHandler()
        {
            @Override
            public void onSuccess(int statusCode, cz.msebera.android.httpclient.Header[] headers, JSONObject response) {
                //super.onSuccess(statusCode, headers, response);
                // Toast.makeText(MainActivity.this,"정보를 성공적으로 불러왔습니다.",Toast.LENGTH_SHORT).show();
                weatherData weatherD=weatherData.fromJson(response);
                updateUI(weatherD);
            }

            @Override
            public void onFailure(int statusCode, cz.msebera.android.httpclient.Header[] headers, Throwable throwable, JSONObject errorResponse) {
                //super.onFailure(statusCode, headers, throwable, errorResponse);
            }
        });
    }
    private  void updateUI(weatherData weather){
        Temperature.setText(weather.getmTemperature());
        weatherState.setText(weather.getmWeatherType());
        int resourceID=getResources().getIdentifier(weather.getMicon(),"drawable",getPackageName());
        mweatherIcon.setImageResource(resourceID);

        TextView pedometerTxt = findViewById(R.id.pedometerTxt);    // 현재 날씨와 걸음수에 따른 조언

        int temp_now = weather.getTempforTip(); // 현재 온도
        String tmp_w = weather.getMicon();  // 날씨 상태
        int weather_state=0; // 0 좋음 1 흐림 2 산책할 수 없는 날씨
        if(tmp_w=="sunny")
            weather_state=0;
        else if(tmp_w=="cloudy"||tmp_w=="fog"||tmp_w=="overcast")
            weather_state=1;
        else
            weather_state=2;

        if(stepNow<4000){
            if(weather_state==0&&temp_now<30&&temp_now>0){
                pedometerTxt.setText("산책하기에 딱 좋은 날씨예요!\n");
            }
            else if(weather_state==1&&temp_now<30&&temp_now>0){
                pedometerTxt.setText("조금 흐리긴 하지만 산책하기에 좋은 날씨예요!\n잠깐이라도 나가서 걸으며 기분전환해봐요~");
            }
            else if(weather_state==2&&temp_now<30&&temp_now>0){
                pedometerTxt.setText("날씨가 좋지 않네요.\n요가 등 실내 운동을 해보는건 어떨까요?");
            }
            else if(temp_now>=30){
                pedometerTxt.setText("오늘은 날씨가 덥네요.\n폭염 주의하시고 실내에서 활동해봐요!");
            }
            else if(temp_now<=0){
                pedometerTxt.setText("날씨가 너무 추워요!\n감기 조심하시고 실내에서 활동해봐요!");
            }
        }
        else if(stepNow>=4000){
            if(temp_now<30&&temp_now>0){
                pedometerTxt.setText("아주 좋아요!\n규칙적인 유산소 운동은 갱년기 증상 완화에 도움이 됩니다.");
            }
            else if(temp_now>=30){
                pedometerTxt.setText("규칙적인 생활 아주 좋아요!\n날씨가 더운데 건강 조심하세요!");
            }
            else if(temp_now<=0){
                pedometerTxt.setText("규칙적인 생활 아주 좋아요!\n날씨가 추운데 감기 조심하세요!");
            }
            
        }



    }

}