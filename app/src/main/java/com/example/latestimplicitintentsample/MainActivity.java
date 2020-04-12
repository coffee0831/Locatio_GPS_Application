package com.example.latestimplicitintentsample;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

public class MainActivity extends AppCompatActivity {
    //緯度のフィールド
    private double _latitude = 0;
    //経度のフィールド
    private double _longitude = 0;
    /*GPSから取得した緯度と経度を表示するTextViewを追加し、
    * 現在地の緯度と経度で書き換える*/
    //緯度を表示するTextViewフィールド
    private TextView _tvLatitude;
    //経度を表示するTextViewフィールド
    private TextView _tvLongitude;

    //メンバクラスとしてリスナクラスを設定
    private class GPSLocationListener implements LocationListener { //2-1
        @Override
        //引き数「location」には、プロバイダから取得した位置情報が格納される
        public void onLocationChanged(Location location) {
            //引き数のLocationオブジェクトから緯度を取得。
            _latitude = location.getLatitude();//2-2
            //引き数のLocationオブジェクトから経度を取得。
            _longitude = location.getLongitude();
            //取得した緯度をTextViewに表示
            // _latitudeの値も変更される
            _tvLatitude.setText(Double.toString(_latitude));
            //取得した経度をTextViewに表示
            //_tvLongitudeの値も変更される
            _tvLongitude.setText(Double.toString(_longitude));
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {
        }//2-4

        @Override
        public void onProviderEnabled(String provider) {
        }

        @Override
        public void onProviderDisabled(String provider) {
        }
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //緯度と経度を表示するTextViewフィールドの中身を取得
        _tvLatitude = findViewById(R.id.tvLatitude);//設定した0
        _tvLongitude = findViewById(R.id.tvLongitude);//設定した0
        //位置情報を利用するために、LocationManagerオブジェクトを取得
        LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);//1
        //位置情報が更新された際のリスナオブジェクトを生成
        GPSLocationListener locationListener = new GPSLocationListener();//2
        //ACCESS_FINE_LOCATIONの許可が下りていないなら
        //「checkSelfPermission()メソッドパーミッションを表す数値を返す」
        if (ActivityCompat.checkSelfPermission(MainActivity.this,Manifest.permission.ACCESS_FINE_LOCATION)!=PackageManager.PERMISSION_GRANTED) {
           //ACCESS_FINE_LOCATIONの許可を求めるダイアログを表示。その際にリクエストコードは1000に設定。
            String[] permission={Manifest.permission.ACCESS_FINE_LOCATION};
            ActivityCompat.requestPermissions(MainActivity.this,permission,1000);
            //onCreate()メソッドを終了
            return;
        }
        //位置情報の追跡を開始
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);//3
    }

    @Override //ユーザが「許可」あるいは「許可しない」を選択
    public void onRequestPermissionsResult(int requestCode,String[] permissions,int[] grantResults){
        //ACCESS_FINE_LOCATIONに対するパーミッションダイアログでかつ許可を選択したら
        if(requestCode==1000&&grantResults[0]==PackageManager.PERMISSION_GRANTED){ //許可されていれば「PERMISSION_GRANTED」、許可されていなければ「PERMISION_DENIED」
            //LocationManagerオブジェクトを取得
            LocationManager locationManager=(LocationManager)getSystemService(LOCATION_SERVICE);
            //位置情報が更新された際のリスナオブジェクトを生成
            GPSLocationListener locationListener=new GPSLocationListener();
            //再度ACCESS_FINE_LOCATIONの許可が下りていないかどうかのチェックをし、降りていないなら処理を中止
            if (ActivityCompat.checkSelfPermission(MainActivity.this,Manifest.permission.ACCESS_FINE_LOCATION)!=PackageManager.PERMISSION_GRANTED){
                return;
            }
            //位置情報の追跡を開始
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);
        }

    }

    public void onMapSearchButtonClick(View view){
       //入力欄に入力されたキーワード文字列を取得
        EditText etSearchWord=findViewById(R.id.etSearchWord);
        String searchWord =etSearchWord.getText().toString();
        try{
            //入力されたキーワードをURLエンコード
            searchWord= URLEncoder.encode(searchWord,"UTF-8");//1
            //マップアプリと連携するURI文字列を生成
            String uriStr="geo:0,0?q"+searchWord;//2
            //URI文字列からURIオブジェクトを生成
            Uri uri=Uri.parse(uriStr);//3
            //インテントオブジェクトを生成
            //引き数にアクションとURIを使ってアクションを暗黙的に指定
            //アクティビティの種類をインテントのクラスの定数フィールドを指定
            Intent intent=new Intent(Intent.ACTION_VIEW,uri);
            //アクティビティを起動
            startActivity(intent);
        }catch(UnsupportedEncodingException ex){
            Log.e("MainActivity","検索キーワード変換失敗",ex);
        }

    }
    public void onMapShowCurrentButtonClick(View view){
        //フィールドの緯度と経度の値をもとにマップアプリと連携するURI文字列を生成
        String uriStr="geo:"+_latitude+","+_longitude;
        //URI文字列からURIオブジェクトを生成
        Uri uri=Uri.parse(uriStr);
        //インテントオブジェクトを生成
        Intent intent=new Intent(Intent.ACTION_VIEW,uri);
        //アクティビティを起動
        startActivity(intent);


    }

}
