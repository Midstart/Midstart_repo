package com.example.midstart;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
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
import android.view.View;
import android.widget.Button;
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

    Button cardNews;  //카드뉴스
    Button cesTest; //우울증검사

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

                UserAccount value =  snapshot.child("UserAccount").child(uid).getValue(UserAccount.class);
                name.setText(value.getName()+" 님 안녕하세요!\n오늘 기분은 어떠신가요?");


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
        cardNews = (Button) findViewById(R.id.btn_cardnews);
        cardNews.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), CardNewsActivity.class);
                startActivity(intent);
            }
        });

        //우울증 검사로 이동
        cesTest= (Button) findViewById(R.id.btn_ces_d);
        cesTest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), CESActivity.class);
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
        stepSinceReboot.setText("STEPS: "+String.valueOf((int)event.values[0]));
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
        //int resourceID=getResources().getIdentifier(weather.getMicon(),"drawable",getPackageName());
        //mweatherIcon.setImageResource(resourceID);
    }

}