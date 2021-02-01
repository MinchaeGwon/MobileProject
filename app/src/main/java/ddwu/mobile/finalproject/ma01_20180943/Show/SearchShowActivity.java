package ddwu.mobile.finalproject.ma01_20180943.Show;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;

import ddwu.mobile.finalproject.ma01_20180943.Bookmark.BookmarkActivity;
import ddwu.mobile.finalproject.ma01_20180943.Manager.ImageFileManager;
import ddwu.mobile.finalproject.ma01_20180943.Manager.NetworkManager;
import ddwu.mobile.finalproject.ma01_20180943.R;
import ddwu.mobile.finalproject.ma01_20180943.Review.ListReviewActivity;

public class SearchShowActivity extends AppCompatActivity {

    public static final String TAG = "SearchShowActivity";

    EditText etPlace;
    ListView lvList;
    String apiAddress;

    String query;

    ShowAdapter adapter;
    ArrayList<ShowDto> resultList;
    ShowXmlParser parser;
    NetworkManager networkManager;
    ImageFileManager imgFileManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_show);

        etPlace = findViewById(R.id.et_place);
        lvList = findViewById(R.id.lvList);

        resultList = new ArrayList();
        adapter = new ShowAdapter(this, R.layout.listview_show, resultList);
        lvList.setAdapter(adapter);

        apiAddress = getResources().getString(R.string.api_url);
        parser = new ShowXmlParser();
        networkManager = new NetworkManager(this);
        imgFileManager = new ImageFileManager(this);

        lvList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(SearchShowActivity.this, DetailShowActivity.class);
                ShowDto dto = resultList.get(position);
                intent.putExtra("detailDto", dto);
                startActivity(intent);
            }
        });

    }

    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_search:
                query = etPlace.getText().toString();

                try {
                    new NetworkAsyncTask().execute(apiAddress + URLEncoder.encode(query, "UTF-8"));
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }

                break;
            case R.id.btn_search_place:
                //검색된 공연들의 공연장 위치를 알려주는 액티비티로 넘어감
                if (resultList != null) {
                    Intent intent = new Intent(this, ShowMapActivity.class);
                    intent.putExtra("showList", resultList);
                    startActivity(intent);
                } else {
                    Toast.makeText(this, "공연을 먼저 검색하세요.", Toast.LENGTH_SHORT).show();
                }

                break;
        }
    }

    class NetworkAsyncTask extends AsyncTask<String, Integer, ArrayList<ShowDto>> {
        ProgressDialog progressDialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog = ProgressDialog.show(SearchShowActivity.this, "Wait", "Searching...");
        }

        @Override
        protected ArrayList<ShowDto> doInBackground(String... strings) {
            String address = strings[0];
            String result = null;

            result = (String) networkManager.download(address, false);
            if (result == null) {
                return null;
            }

            resultList = parser.placeParse(result);

            for (int i = 0; i < resultList.size(); i++) {
                apiAddress = getResources().getString(R.string.detail_api_url) + resultList.get(i).getSeq();
                result = (String) networkManager.download(apiAddress, false);
                if (result == null) {
                    return null;
                }

                parser.detailParse(result, resultList.get(i));
            }

            for (ShowDto dto : resultList) {
                Bitmap savedBitmap = imgFileManager.getBitmapFromTemporary(dto.getImageLink());

                if (savedBitmap == null) {
                    Bitmap bitmap = (Bitmap) networkManager.download(dto.getImageLink(), true);
                    if (bitmap != null) {
                        imgFileManager.saveBitmapToTemporary(bitmap, dto.getImageLink());
                    }
                }
            }
            return resultList;
        }

        @Override
        protected void onPostExecute(ArrayList<ShowDto> resultList) {
            if (!resultList.isEmpty()) {
                adapter.setList(resultList);
            } else {
                Toast.makeText(SearchShowActivity.this, "검색 결과가 없습니다.", Toast.LENGTH_SHORT).show();
            }
            progressDialog.dismiss();
        }
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
}