package ddwu.mobile.finalproject.ma01_20180943.Main;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.ResultReceiver;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;

import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.net.FetchPlaceRequest;
import com.google.android.libraries.places.api.net.FetchPlaceResponse;
import com.google.android.libraries.places.api.net.PlacesClient;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import ddwu.mobile.finalproject.ma01_20180943.Bookmark.BookmarkActivity;
import ddwu.mobile.finalproject.ma01_20180943.R;
import ddwu.mobile.finalproject.ma01_20180943.Review.ListReviewActivity;
import noman.googleplaces.NRPlaces;
import noman.googleplaces.PlaceType;
import noman.googleplaces.PlacesException;
import noman.googleplaces.PlacesListener;

public class MainMapActivity extends AppCompatActivity implements OnMapReadyCallback {

    final static String TAG = "MainMapActivity";
    final static int PERMISSION_REQ_CODE = 100;

    private GoogleMap mGoogleMap;
    private MarkerOptions markerOptions;
    private Marker centerMarker;
    Location currentLoc;

    private PlacesClient placesClient;

    private LatLngResultReceiver latLngResultReceiver;
    private AddressResultReceiver addressResultReceiver;

    EditText etSearch;
    LatLng searchLatlng;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_map);

        mapLoad();

        Places.initialize(getApplicationContext(), getResources().getString(R.string.google_api_key));
        placesClient = Places.createClient(this);

        latLngResultReceiver = new LatLngResultReceiver(new Handler());
        addressResultReceiver = new AddressResultReceiver(new Handler());

        etSearch = findViewById(R.id.et_search_place);

    }

    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_home:
                finish();
                break;
            case R.id.btn_map_search:
                mGoogleMap.clear();
                startLatLngService();
                break;
        }
    }

    private void startLatLngService() {
        String address = etSearch.getText().toString();
        Intent intent = new Intent(this, FetchLatLngIntentService.class);
        intent.putExtra(Constants.RECEIVER, latLngResultReceiver);
        intent.putExtra(Constants.ADDRESS_DATA_EXTRA, address);
        startService(intent);
    }

    /* 주소 → 위도/경도 변환 ResultReceiver */
    class LatLngResultReceiver extends ResultReceiver {
        public LatLngResultReceiver(Handler handler) {
            super(handler);
        }

        @Override
        protected void onReceiveResult(int resultCode, Bundle resultData) {
            Log.d(TAG, "지오 코딩 결과 부분");
            Double lat = null;
            Double lng = null;
            ArrayList<LatLng> latLngList = null;

            if (resultCode == Constants.SUCCESS_RESULT) {
                if (resultData == null) return;
                latLngList = (ArrayList<LatLng>) resultData.getSerializable(Constants.RESULT_DATA_KEY);
                if (latLngList == null) {
                    Toast.makeText(MainMapActivity.this, "검색 결과가 없습니다.", Toast.LENGTH_SHORT).show();
                } else {
                    searchLatlng = latLngList.get(0);
                    lat = searchLatlng.latitude;
                    lng = searchLatlng.longitude;

                    Intent intent = new Intent(MainMapActivity.this, FetchAddressIntentService.class);
                    intent.putExtra(Constants.RECEIVER, addressResultReceiver);
                    intent.putExtra(Constants.LAT_DATA_EXTRA, lat);
                    intent.putExtra(Constants.LNG_DATA_EXTRA, lng);
                    startService(intent);
                }
            } else {
                Toast.makeText(MainMapActivity.this, "검색 결과가 없습니다.", Toast.LENGTH_SHORT).show();
            }
        }
    }

    /* 위도/경도 → 주소 변환 ResultReceiver */
    class AddressResultReceiver extends ResultReceiver {
        public AddressResultReceiver(Handler handler) {
            super(handler);
        }

        @Override
        protected void onReceiveResult(int resultCode, Bundle resultData) {

            String addressOutput = null;

            if (resultCode == Constants.SUCCESS_RESULT) {
                if (resultData == null) return;
                addressOutput = resultData.getString(Constants.RESULT_DATA_KEY);
                if (addressOutput == null) addressOutput = "";

                markerOptions.title(etSearch.getText().toString());
                markerOptions.snippet(addressOutput);
                markerOptions.position(searchLatlng);
                markerOptions.icon(BitmapDescriptorFactory.defaultMarker(
                        BitmapDescriptorFactory.HUE_BLUE
                ));
                centerMarker = mGoogleMap.addMarker(markerOptions);

                mGoogleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(searchLatlng, 15));
            } else {
                Toast.makeText(MainMapActivity.this, "검색 결과가 없습니다.", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void searchStart(String type) {
        new NRPlaces.Builder().listener(placesListener)
                .key(getResources().getString(R.string.google_api_key))
                .latlng(currentLoc.getLatitude(), currentLoc.getLongitude())
                .radius(1000)
                .type(type)
                .build()
                .execute();
    }

    private void getPlaceDetail(final String placeId) {
        List<Place.Field> placeFields = Arrays.asList(Place.Field.ID, Place.Field.NAME,
                Place.Field.PHONE_NUMBER, Place.Field.ADDRESS);

        final FetchPlaceRequest request = FetchPlaceRequest.builder(placeId, placeFields).build();

        placesClient.fetchPlace(request).addOnSuccessListener(new OnSuccessListener<FetchPlaceResponse>() {
            @Override
            public void onSuccess(FetchPlaceResponse response) {
                Place place = response.getPlace();

                String phone = place.getPhoneNumber();
                if (phone == null) {
                    phone = "정보 없음";
                }

                AlertDialog.Builder builder = new AlertDialog.Builder(MainMapActivity.this);
                builder.setTitle(place.getName())
                        .setMessage("전화번호: " + phone + "\n주소: " + place.getAddress())
                        .setPositiveButton("확인", null)
                        .show();

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                if (e instanceof ApiException) {
                    ApiException apiException = (ApiException) e;
                    int statusCode=  apiException.getStatusCode();
                    Log.e(TAG, "Place not found: " + statusCode +  " " + e.getMessage());
                }
            }
        });

    }

    PlacesListener placesListener = new PlacesListener() {
        @Override
        public void onPlacesSuccess(final List<noman.googleplaces.Place> places) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    for (noman.googleplaces.Place place : places) {
                        markerOptions.title(place.getName());
                        markerOptions.position(new LatLng(place.getLatitude(), place.getLongitude()));
                        markerOptions.icon(BitmapDescriptorFactory.defaultMarker(
                                BitmapDescriptorFactory.HUE_BLUE
                        ));
                        Marker newMarker = mGoogleMap.addMarker(markerOptions);
                        newMarker.setTag(place.getPlaceId());
                    }
                }
            });
        }

        @Override
        public void onPlacesFailure(PlacesException e) {}

        @Override
        public void onPlacesStart() {}

        @Override
        public void onPlacesFinished() {}
    };

    /*구글맵을 멤버변수로 로딩*/
    private void mapLoad() {
        MapFragment mapFragment = (MapFragment) getFragmentManager().findFragmentById(R.id.main_map);
        mapFragment.getMapAsync(this);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mGoogleMap = googleMap;

        LatLng loc = new LatLng(37.564214, 127.001699);
        mGoogleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(loc, 14));

        markerOptions = new MarkerOptions();

        if (checkPermission()) {
            mGoogleMap.setMyLocationEnabled(true);
        }

        mGoogleMap.setOnMyLocationButtonClickListener(new GoogleMap.OnMyLocationButtonClickListener() {
            @Override
            public boolean onMyLocationButtonClick() {
                mGoogleMap.clear();
                currentLoc = mGoogleMap.getMyLocation();

                // 공연이나 전시를 할 수 있는 박물관, 경기장을 보여줌
                searchStart(PlaceType.MUSEUM);
                searchStart(PlaceType.STADIUM);

                return false;
            }
        });

        mGoogleMap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
            @Override
            public void onInfoWindowClick(Marker marker) {
                if (marker.getTag().toString() != null) {
                    getPlaceDetail(marker.getTag().toString());
                }
            }
        });

    }

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

    private boolean checkPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION)
                    != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this,
                        new String[] {Manifest.permission.ACCESS_FINE_LOCATION},
                        PERMISSION_REQ_CODE);
                return false;
            }
        }
        return true;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if(requestCode == PERMISSION_REQ_CODE) {
            if(grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // 퍼미션을 획득하였을 경우 맵 로딩 실행
                mapLoad();
            } else {
                // 퍼미션 미획득 시 액티비티 종료
                Toast.makeText(this, "앱 실행을 위해 권한 허용이 필요함", Toast.LENGTH_SHORT).show();
                finish();
            }
        }
    }

}