package ddwu.mobile.finalproject.ma01_20180943.Show;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;

import ddwu.mobile.finalproject.ma01_20180943.Bookmark.BookmarkActivity;
import ddwu.mobile.finalproject.ma01_20180943.R;
import ddwu.mobile.finalproject.ma01_20180943.Review.ListReviewActivity;

public class ShowMapActivity extends AppCompatActivity {

    TextView mapTitle;
    ArrayList<ShowDto> list;

    private GoogleMap mGoogleMap;
    private Marker centerMarker;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_map);

        mapTitle = findViewById(R.id.tv_show_map);
        list = (ArrayList<ShowDto>) getIntent().getSerializableExtra("showList");

        mapTitle.setText(list.get(0).getArea() + "지역 공연장");

        MapFragment mapFragment = (MapFragment) getFragmentManager().findFragmentById(R.id.show_map);
        mapFragment.getMapAsync(mapReadyCallBack);

    }

    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_show_map:
                finish();
                break;
        }
    }

    OnMapReadyCallback mapReadyCallBack = new OnMapReadyCallback() {
        @Override
        public void onMapReady(GoogleMap googleMap) {
            mGoogleMap = googleMap;

            // 맨 처음에 중심은 첫번째 객체의 위치가 중심
            LatLng first = new LatLng(list.get(0).getLatitude(), list.get(0).getLongitude());
            mGoogleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(first, 12));

            for (int i = 0; i < list.size(); i++) {
                ShowDto show = list.get(i);

                if (show.getLatitude() != null && show.getLongitude() != null) {
                    LatLng loc = new LatLng(show.getLatitude(), show.getLongitude());

                    MarkerOptions options = new MarkerOptions();
                    options.position(loc);
                    options.title(show.getTitle(false));
                    options.snippet(show.getPlaceAddr());
                    options.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE));

                    centerMarker = mGoogleMap.addMarker(options);
                }
            }

            mGoogleMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
                @Override
                public boolean onMarkerClick(Marker marker) {
                    mGoogleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(marker.getPosition(), 16));
                    marker.showInfoWindow();
                    return true;
                }
            });

        }
    };

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        Intent intent = null;

        switch (item.getItemId()) {
            case R.id.item_bookmark:
                intent = new Intent(this, BookmarkActivity.class);
                break;
            case R.id.item_review:
                intent = new Intent(this, ListReviewActivity.class);
                break;
        }
        if (intent != null) startActivity(intent);

        return true;
    }
}