package ddwu.mobile.finalproject.ma01_20180943.Manager;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DBHelper extends SQLiteOpenHelper {

    private final static String DB_NAME = "show_db";

    //리뷰 관련 테이블
    public final static String REVIEW_TABLE = "review_table";
    public final static String REVIEW_ID = "_id";
    public final static String REVIEW_TITLE = "review_title";
    public final static String REVIEW_CONTENT = "review_content";

    //즐겨찾기 관련 테이블
    public final static String BOOKMARK_TABLE = "bookmark_table";
    public final static String BOOKMARK_ID = "_id";
    public final static String BOOKMARK_SEQ = "bookmark_seq";
    public final static String BOOKMARK_TITLE = "bookmark_title";
    public final static String BOOKMARK_AREA = "bookmark_area";
    public final static String BOOKMARK_PLACE = "bookmark_place";
    public final static String BOOKMARK_IMAGE = "bookmark_imageLink";
    public final static String BOOKMARK_START = "bookmark_start";
    public final static String BOOKMARK_END = "bookmark_end";
    public final static String BOOKMARK_REALMNAME = "bookmark_realmname";
    //여기부터
    public final static String BOOKMARK_PLACE_URL = "bookmark_place_url";
    public final static String BOOKMARK_PLACE_ADDR = "bookmark_place_addr";
    public final static String BOOKMARK_PRICE = "bookmark_price";
    public final static String BOOKMARK_TICKET_LINK = "bookmark_ticket_link";
    public final static String BOOKMARK_PHONE = "bookmark_phone";
    //여기까지는 삭제해도될듯
    public final static String BOOKMARK_LATITUDE = "bookmark_latitude";
    public final static String BOOKMARK_LONGITUDE = "bookmark_longitude";

    public DBHelper(Context context) {
        super(context, DB_NAME, null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("create table " + REVIEW_TABLE + " ( " + REVIEW_ID + " integer primary key autoincrement,"
                + REVIEW_TITLE + " TEXT, " + REVIEW_CONTENT + " TEXT);");

        db.execSQL("create table " + BOOKMARK_TABLE + " ( " + BOOKMARK_ID + " integer primary key autoincrement,"
                + BOOKMARK_SEQ + " TEXT, " + BOOKMARK_TITLE + " TEXT, " + BOOKMARK_AREA + " TEXT, "
                + BOOKMARK_PLACE + " TEXT, " + BOOKMARK_IMAGE + " TEXT, " + BOOKMARK_START + " TEXT, "
                + BOOKMARK_END + " TEXT, " + BOOKMARK_PLACE_URL + " TEXT, " + BOOKMARK_PLACE_ADDR + " TEXT, "
                + BOOKMARK_REALMNAME + " TEXT, " + BOOKMARK_PRICE + " TEXT, " + BOOKMARK_TICKET_LINK + " TEXT, "
                + BOOKMARK_PHONE + " TEXT, " + BOOKMARK_LATITUDE + " DOUBLE, " + BOOKMARK_LONGITUDE + " DOUBLE);");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("drop table " + REVIEW_TABLE);
        db.execSQL("drop table " + BOOKMARK_TABLE);
        onCreate(db);
    }
}
