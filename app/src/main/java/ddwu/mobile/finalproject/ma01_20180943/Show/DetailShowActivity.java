package ddwu.mobile.finalproject.ma01_20180943.Show;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlarmManager;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.Calendar;

import ddwu.mobile.finalproject.ma01_20180943.Bookmark.BookmarkActivity;
import ddwu.mobile.finalproject.ma01_20180943.Manager.DBManager;
import ddwu.mobile.finalproject.ma01_20180943.Manager.ImageFileManager;
import ddwu.mobile.finalproject.ma01_20180943.Manager.NetworkManager;
import ddwu.mobile.finalproject.ma01_20180943.R;
import ddwu.mobile.finalproject.ma01_20180943.Review.AddReviewActivity;
import ddwu.mobile.finalproject.ma01_20180943.Review.ListReviewActivity;

public class DetailShowActivity extends AppCompatActivity {

    public static final String TAG = "DetailShowActivity";

    TextView title;
    TextView place;
    TextView area;
    TextView startDate;
    TextView endDate;
    TextView price;
    TextView link;
    TextView phone;
    TextView placeAddr;
    TextView placeUrl;
    ImageView image;
    Button btn_bookmark;

    DBManager dbManager;
    NetworkManager networkManager;
    ImageFileManager imgFileManager;
    ShowXmlParser parser;
    ShowDto detail;

    PendingIntent pendingIntent = null;
    AlarmManager alarmManager = null;

    private GoogleMap mGoogleMap;
    private Marker centerMarker;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_show);

        parser = new ShowXmlParser();
        networkManager = new NetworkManager(this);
        imgFileManager = new ImageFileManager(this);
        dbManager = new DBManager(this);

        alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        createNotificationChannel();

        MapFragment mapFragment = (MapFragment) getFragmentManager().findFragmentById(R.id.detail_map);
        mapFragment.getMapAsync(mapReadyCallBack);

        title = findViewById(R.id.tv_detailTitle);
        place = findViewById(R.id.tv_detailPlace);
        area = findViewById(R.id.tv_detailArea);
        startDate = findViewById(R.id.tv_detailStart);
        endDate = findViewById(R.id.tv_detailEnd);
        price = findViewById(R.id.tv_detailPrice);
        link = findViewById(R.id.tv_detailLink); //예매처
        phone = findViewById(R.id.tv_detailPhone);
        placeAddr = findViewById(R.id.tv_detailAddr);
        placeUrl = findViewById(R.id.tv_detailURL); //공연장소 URL
        image = findViewById(R.id.imageView2);

        detail = (ShowDto) getIntent().getSerializableExtra("detailDto");

        btn_bookmark = findViewById(R.id.btn_add_bookmark);
        if (dbManager.existingBookmark(detail.getSeq())) {
            btn_bookmark.setText("즐겨찾기 해제");
        }

        tvSetText();

        // 예매처 URL 클릭시 해당 페이지로 이동
        link.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_VIEW);
                String url = link.getText().toString().substring(5);
                intent.setData(Uri.parse(url));
                startActivity(intent);
            }
        });

        // 공연 장소 URL 클릭시 해당 페이지로 이동
        placeUrl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_VIEW);
                String url = placeUrl.getText().toString().substring(9);
                intent.setData(Uri.parse(url));
                startActivity(intent);
            }
        });

    }

    public void tvSetText() {
        title.setText(detail.getTitle(false));
        place.setText("장소: " + detail.getPlace());
        area.setText("(" + detail.getArea() + ")");

        String s = detail.getStartDate();
        String startD = s.substring(0, 4) + "." + s.substring(4, 6) + "." + s.substring(6, 8);
        startDate.setText("시작일: " + startD);

        String e = detail.getEndDate();
        String endD = e.substring(0, 4) + "." + e.substring(4, 6) + "." + e.substring(6, 8);
        endDate.setText("종료일: " + endD);

        price.setText("티켓 가격: " + detail.getPrice());

        link.setText("예매처: " + detail.getTicketLink());
        String ticketLink = link.getText().toString();
        SpannableString spannableStr1 = new SpannableString(ticketLink);
        String word = ticketLink.substring(5);
        int start = ticketLink.indexOf(word);
        int end = start + word.length();
        spannableStr1.setSpan(new ForegroundColorSpan(Color.parseColor("#013ADF")),
                start, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        link.setText(spannableStr1);

        phone.setText("문의 번호: " + detail.getPhone());

        if (detail.getLatitude() != null && detail.getLongitude() != null) {
            placeAddr.setText("'" + detail.getPlace() + "' 주소: " + detail.getPlaceAddr());
        } else {
            placeAddr.setText("'" + detail.getPlace() +"' 주소: 정보 없음");
        }

        placeUrl.setText("공연장 URL: " + detail.getPlaceUrl());
        String pUrl = placeUrl.getText().toString();
        SpannableString spannableStr2 = new SpannableString(pUrl);
        word = pUrl.substring(9);
        start = pUrl.indexOf(word);
        end = start + word.length();
        spannableStr2.setSpan(new ForegroundColorSpan(Color.parseColor("#013ADF")),
                start, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        placeUrl.setText(spannableStr2);

        Bitmap savedBitmap = imgFileManager.getBitmapFromTemporary(detail.getImageLink());
        if (savedBitmap != null)
            image.setImageBitmap(savedBitmap);
    }

    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_add_review:
                Intent intent = new Intent(this, AddReviewActivity.class);
                intent.putExtra("title", detail.getTitle(false));
                startActivity(intent);
                break;
            case R.id.btn_add_bookmark:
                if (btn_bookmark.getText().equals("즐겨찾기")) {
                    boolean result = dbManager.addBookmark(detail);
                    if (result) {
                        Toast.makeText(this, "즐겨찾기 추가 성공", Toast.LENGTH_SHORT).show();
                        btn_bookmark.setText("즐겨찾기 해제");
                    } else {
                        Toast.makeText(this, "즐겨찾기 추가 실패", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    boolean result = dbManager.removeBookmark(detail.getSeq());
                    if (result) {
                        Toast.makeText(this, "즐겨찾기 해제 성공", Toast.LENGTH_SHORT).show();
                        btn_bookmark.setText("즐겨찾기");
                    } else {
                        Toast.makeText(this, "즐겨찾기 해제 실패", Toast.LENGTH_SHORT).show();
                    }
                }

                break;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.alarm_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        Intent intent = null;

        switch (item.getItemId()) {
            case R.id.item_alarm:
                intent = new Intent(this, MyBroadcastReceiver.class);
                intent.putExtra("title", detail.getTitle(false));

                //공연 종료일 알람
                pendingIntent = PendingIntent.getBroadcast(this, 100, intent, PendingIntent.FLAG_UPDATE_CURRENT);
                Calendar end = Calendar.getInstance();
                end.setTimeInMillis(System.currentTimeMillis());

                String e = detail.getEndDate();
                int endYear = Integer.parseInt(e.substring(0, 4));
                int endMonth = Integer.parseInt(e.substring(4, 6));
                int endDay = Integer.parseInt(e.substring(6, 8));
                end.set(endYear, endMonth-1, endDay, 0, 0, 0);
//                end.set(endYear, endMonth-1, 23, 0, 49, 0);

                alarmManager.set(AlarmManager.RTC, end.getTimeInMillis(), pendingIntent);

                Toast.makeText(this, "공연 종료일 알림 설정", Toast.LENGTH_SHORT).show();

                break;
            case R.id.alarm_bookmark:
                intent = new Intent(this, BookmarkActivity.class);
                startActivity(intent);
                break;
            case R.id.alarm_review:
                intent = new Intent(this, ListReviewActivity.class);
                startActivity(intent);
                break;
        }

        return true;
    }

    // notification 채널 만드는 코드
    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = getString(R.string.channel_name);
            String description = getString(R.string.channel_description);
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(getString(R.string.CHANNEL_ID), name, importance);
            channel.setDescription(description);

            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }

    OnMapReadyCallback mapReadyCallBack = new OnMapReadyCallback() {
        @Override
        public void onMapReady(GoogleMap googleMap) {
            mGoogleMap = googleMap;

            LatLng detailLoc;
            if (detail.getLatitude() != null && detail.getLongitude() != null) {
                detailLoc = new LatLng(detail.getLatitude(), detail.getLongitude());
                mGoogleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(detailLoc, 16));

                MarkerOptions options = new MarkerOptions();
                options.position(detailLoc);
                options.title(detail.getTitle(false));
                options.snippet(detail.getPlaceAddr());
                options.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE));

                centerMarker = mGoogleMap.addMarker(options);
            } else {
                // 공연장의 위도, 경도 정보가 없을 경우 서울의 중심 좌표가 중심이 됨
                detailLoc = new LatLng(37.564214, 127.001699);
                mGoogleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(detailLoc, 13));
            }

        }
    };

}