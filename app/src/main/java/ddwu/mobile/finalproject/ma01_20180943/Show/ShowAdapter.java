package ddwu.mobile.finalproject.ma01_20180943.Show;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

import ddwu.mobile.finalproject.ma01_20180943.Manager.DBManager;
import ddwu.mobile.finalproject.ma01_20180943.Manager.ImageFileManager;
import ddwu.mobile.finalproject.ma01_20180943.Manager.NetworkManager;
import ddwu.mobile.finalproject.ma01_20180943.R;

public class ShowAdapter extends BaseAdapter {

    public static final String TAG = "ShowAdapter";

    private LayoutInflater inflater;
    private Context context;
    private int layout;
    private ArrayList<ShowDto> list;
    private NetworkManager networkManager = null;
    private ImageFileManager imageFileManager = null;
    private DBManager dbManager = null;

    public ShowAdapter(Context context, int resource, ArrayList<ShowDto> list) {
        this.context = context;
        this.layout = resource;
        this.list = list;
        imageFileManager = new ImageFileManager(context);
        networkManager = new NetworkManager(context);
        dbManager = new DBManager(context);
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int i) {
        return list.get(i);
    }

    @Override
    public long getItemId(int i) {
        return list.get(i).get_id();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = convertView;
        ViewHolder viewHolder = null;

        if (view == null) {
            view = inflater.inflate(layout, parent, false);
            viewHolder = new ViewHolder();
            viewHolder.tvTitle = view.findViewById(R.id.tv_title);
            viewHolder.tvArea = view.findViewById(R.id.tv_area);
            viewHolder.tvPlace = view.findViewById(R.id.tv_place);
            viewHolder.tvRealmName = view.findViewById(R.id.tv_realmName);
            viewHolder.ivImage = view.findViewById(R.id.imageView);
            viewHolder.btnBookMark = view.findViewById(R.id.btn_bookmark);
            view.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) view.getTag();
        }

        ShowDto dto = list.get(position);

        viewHolder.tvTitle.setText(dto.getTitle(false));
        viewHolder.tvArea.setText("(" + dto.getArea() + ")");
        viewHolder.tvPlace.setText(dto.getPlace());
        viewHolder.tvRealmName.setText(dto.getRealmName());

        viewHolder.btnBookMark.setFocusable(false);
        if (dbManager.existingBookmark(dto.getSeq())) {
            viewHolder.btnBookMark.setText("즐겨찾기 해제");

            viewHolder.btnBookMark.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    boolean result = dbManager.removeBookmark(dto.getSeq());
                    if (result) {
                        Toast.makeText(context, "즐겨찾기 해제 성공", Toast.LENGTH_SHORT).show();
                        notifyDataSetChanged();
                    } else {
                        Toast.makeText(context, "즐겨찾기 해제 실패", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        } else {
            viewHolder.btnBookMark.setText("즐겨찾기");
            viewHolder.btnBookMark.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    boolean result = dbManager.addBookmark(dto);
                    if (result) {
                        Toast.makeText(context, "즐겨찾기 추가 성공", Toast.LENGTH_SHORT).show();
                        notifyDataSetChanged();
                    } else {
                        Toast.makeText(context, "즐겨찾기 추가 실패", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }

        if (dto.getImageLink() == null) {
            viewHolder.ivImage.setImageResource(R.mipmap.ic_launcher);
            return view;
        }

        Bitmap savedBitmap = imageFileManager.getBitmapFromTemporary(dto.getImageLink());

        if (savedBitmap != null) {
            viewHolder.ivImage.setImageBitmap(savedBitmap);
            Log.d(TAG, "Image loading from file");
        }
        else {
            viewHolder.ivImage.setImageResource(R.mipmap.ic_launcher);
            new GetImageAsyncTask(viewHolder).execute(dto.getImageLink());
            Log.d(TAG, "Image loading from network");
        }

        return view;
    }

    public void setList(ArrayList<ShowDto> list) {
        this.list = list;
        notifyDataSetChanged();
    }

    static class ViewHolder {
        public TextView tvTitle = null;
        public TextView tvArea = null;
        public TextView tvPlace = null;
        public TextView tvRealmName = null;
        public ImageView ivImage = null;
        public Button btnBookMark = null;
    }

    class GetImageAsyncTask extends AsyncTask<String, Void, Bitmap> {

        ViewHolder viewHolder;
        String imageAddress;

        public GetImageAsyncTask(ViewHolder holder) {
            viewHolder = holder;
        }

        @Override
        protected Bitmap doInBackground(String... params) {
            imageAddress = params[0];
            Bitmap result = null;

            result = (Bitmap) networkManager.download(imageAddress, true);

            return result;
        }

        @Override
        protected void onPostExecute(Bitmap bitmap) {
            if (bitmap != null) {
                viewHolder.ivImage.setImageBitmap(bitmap);
                imageFileManager.saveBitmapToTemporary(bitmap, imageAddress);
            }

        }

    }

}
