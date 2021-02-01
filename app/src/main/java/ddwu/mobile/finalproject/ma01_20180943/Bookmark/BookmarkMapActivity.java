package ddwu.mobile.finalproject.ma01_20180943.Bookmark;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;

import ddwu.mobile.finalproject.ma01_20180943.R;
import ddwu.mobile.finalproject.ma01_20180943.Review.ListReviewActivity;
import ddwu.mobile.finalproject.ma01_20180943.Show.ShowDto;

public class BookmarkMapActivity extends AppCompatActivity {

    ArrayList<ShowDto> list;

    private GoogleMap mGoogleMap;
    private Marker centerMarker;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bookmark_map);

        list = (ArrayList<ShowDto>) getIntent().getSerializableExtra("bookmarkList");

        MapFragment mapFragment = (MapFragment) getFragmentManager().findFragmentById(R.id.bookmark_map);
        mapFragment.getMapAsync(mapReadyCallBack);

    }

    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_bmBack:
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
        getMenuInflater().inflate(R.menu.review_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        Intent intent = null;

        switch (item.getItemId()) {
            case R.id.review_alone:
                intent = new Intent(this, ListReviewActivity.class);
                break;
        }
        if (intent != null) startActivity(intent);

        return true;
    }
}