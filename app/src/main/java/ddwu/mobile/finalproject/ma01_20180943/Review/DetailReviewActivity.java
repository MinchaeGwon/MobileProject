package ddwu.mobile.finalproject.ma01_20180943.Review;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import ddwu.mobile.finalproject.ma01_20180943.Manager.DBManager;
import ddwu.mobile.finalproject.ma01_20180943.R;

public class DetailReviewActivity extends AppCompatActivity {

    public static final String TAG = "DetailReviewActivity";

    DBManager manager;

    EditText etTitle;
    EditText etContent;
    TextView tvName;

    ReviewDto dto;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_review);

        manager = new DBManager(this);
        dto = (ReviewDto) getIntent().getSerializableExtra("detailDto");

        etTitle = findViewById(R.id.et_detail_title);
        etContent = findViewById(R.id.et_detail_content);
        tvName = findViewById(R.id.tv_detail);

        etTitle.setText(dto.getTitle());
        etContent.setText(dto.getContent());
        tvName.setText("'" + dto.getTitle() + "' 리뷰");

    }

    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_detail_update:
                String title = etTitle.getText().toString();
                String content = etContent.getText().toString();
                tvName.setText("'" + dto.getTitle() + "' 리뷰");

                if (!title.isEmpty() && !content.isEmpty()) {
                    dto.setTitle(title);
                    dto.setContent(content);

                    if (manager.modifyReview(dto)) {
                        Toast.makeText(this, "수정 완료", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(this, "수정 실패", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    if (title.isEmpty()) {
                        Toast.makeText(this, "공연 제목을 입력하세요.", Toast.LENGTH_SHORT).show();
                    } else if (content.isEmpty()) {
                        Toast.makeText(this, "리뷰를 작성하세요.", Toast.LENGTH_SHORT).show();
                    }
                }
                break;
            case R.id.btn_detail_cancel:
                finish();
                break;
            case R.id.btn_review_share:
                Intent intent = new Intent(Intent.ACTION_SEND);
                intent.setType("text/plain");
                String review = etContent.getText().toString();
                intent.putExtra(Intent.EXTRA_TEXT, review);
                Intent share = Intent.createChooser(intent, "공유하기");
                startActivity(share);
                break;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.delete_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.item_delete:
                AlertDialog.Builder builder = new AlertDialog.Builder(DetailReviewActivity.this);
                builder.setTitle("리뷰 삭제")
                        .setMessage("리뷰를 삭제하시겠습니까?")
                        .setPositiveButton("삭제", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                if (manager.removeReview(dto.get_id())) {
                                    setResult(RESULT_OK);
                                    finish();
                                    //startActivity(new Intent(DetailReviewActivity.this, ListReviewActivity.class));
                                } else {
                                    Toast.makeText(DetailReviewActivity.this, "삭제 실패", Toast.LENGTH_SHORT).show();
                                }
                            }
                        })
                        .setNegativeButton("취소", null)
                        .setCancelable(false)
                        .show();

                break;
        }

        return true;
    }
}