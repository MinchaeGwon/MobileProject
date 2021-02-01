package ddwu.mobile.finalproject.ma01_20180943.Review;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.CursorAdapter;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.Toast;

import ddwu.mobile.finalproject.ma01_20180943.Bookmark.BookmarkActivity;
import ddwu.mobile.finalproject.ma01_20180943.Manager.DBHelper;
import ddwu.mobile.finalproject.ma01_20180943.Manager.DBManager;
import ddwu.mobile.finalproject.ma01_20180943.R;

public class ListReviewActivity extends AppCompatActivity {

    public static final String TAG = "ListReviewActivity";

    ListView listView = null;
    Cursor cursor;
    SimpleCursorAdapter adapter;

    DBManager manager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_review);

        manager = new DBManager(this);
        listView = findViewById(R.id.review_lvList);

        adapter = new SimpleCursorAdapter(this, android.R.layout.simple_list_item_1, null,
                new String[] {DBHelper.REVIEW_TITLE},
                new int[] {android.R.id.text1},
                CursorAdapter.FLAG_REGISTER_CONTENT_OBSERVER);

        listView.setAdapter(adapter);

        //리뷰 상세보기(수정도 가능한 액티비티)
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                ReviewDto dto = manager.getReviewById(id);

                Intent intent = new Intent(ListReviewActivity.this, DetailReviewActivity.class);
                intent.putExtra("detailDto", dto);
                startActivity(intent);
            }
        });

        //롱클릭 -> 리뷰 삭제
        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                final long tabledId = id;

                AlertDialog.Builder builder = new AlertDialog.Builder(ListReviewActivity.this);
                builder.setTitle("리뷰 삭제")
                        .setMessage("리뷰를 삭제하시겠습니까?")
                        .setPositiveButton("삭제", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                if (manager.removeReview(tabledId)) {
                                    Toast.makeText(ListReviewActivity.this, "리뷰 삭제 완료", Toast.LENGTH_SHORT).show();
                                    cursor = manager.getAllReview();
                                    adapter.changeCursor(cursor);
                                }
                            }
                        })
                        .setNegativeButton("취소", null)
                        .setCancelable(false)
                        .show();

                return true;
            }
        });

    }

    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_list_add_review:
                startActivity(new Intent(this, AddReviewActivity.class));
                break;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        cursor = manager.getAllReview();
        adapter.changeCursor(cursor);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if (cursor != null) cursor.close();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.bookmark_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        Intent intent = null;

        switch (item.getItemId()) {
            case R.id.bookmark_alone:
                intent = new Intent(this, BookmarkActivity.class);
                break;
        }
        if (intent != null) startActivity(intent);

        return true;
    }
}