package ddwu.mobile.finalproject.ma01_20180943.Manager;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.util.ArrayList;

import ddwu.mobile.finalproject.ma01_20180943.Review.ReviewDto;
import ddwu.mobile.finalproject.ma01_20180943.Show.ShowDto;

public class DBManager {

    public static final String TAG = "DBManager";

    DBHelper helper;
    Cursor cursor;

    public DBManager(Context context) {
        helper = new DBHelper(context);
    }

    //리뷰 관련
    public Cursor getAllReview() {
        SQLiteDatabase db = helper.getReadableDatabase();
        return db.rawQuery("select * from " + DBHelper.REVIEW_TABLE, null);
    }

    public boolean addReview(ReviewDto newDto) {
        SQLiteDatabase db = helper.getWritableDatabase();
        ContentValues row = new ContentValues();

        row.put(DBHelper.REVIEW_TITLE, newDto.getTitle());
        row.put(DBHelper.REVIEW_CONTENT, newDto.getContent());

        long result = db.insert(DBHelper.REVIEW_TABLE, null, row);
        helper.close();

        if (result > 0) return true;
        return false;
    }

    public ReviewDto getReviewById(long id) {
        ReviewDto dto = new ReviewDto();
        SQLiteDatabase db = helper.getReadableDatabase();
        String selection = DBHelper.REVIEW_ID + "=?";
        String[] selectArgs = new String[] {String.valueOf(id)};

        cursor = db.query(DBHelper.REVIEW_TABLE, null, selection, selectArgs, null, null, null);

        while (cursor.moveToNext()) {
            dto.set_id(cursor.getLong(cursor.getColumnIndex(DBHelper.REVIEW_ID)));
            dto.setTitle(cursor.getString(cursor.getColumnIndex(DBHelper.REVIEW_TITLE)));
            dto.setContent(cursor.getString(cursor.getColumnIndex(DBHelper.REVIEW_CONTENT)));
        }
        cursor.close();
        helper.close();

        return dto;
    }

    public boolean modifyReview(ReviewDto dto) {
        SQLiteDatabase db = helper.getWritableDatabase();
        ContentValues row = new ContentValues();

        row.put(DBHelper.REVIEW_TITLE, dto.getTitle());
        row.put(DBHelper.REVIEW_CONTENT, dto.getContent());

        String whereClause = DBHelper.REVIEW_ID + "=?";
        String[] whereArgs = new String[] {String.valueOf(dto.get_id())};

        int result = db.update(DBHelper.REVIEW_TABLE, row, whereClause, whereArgs);
        helper.close();

        if (result > 0) return true;
        return false;
    }

    public boolean removeReview(long id) {
        SQLiteDatabase db = helper.getWritableDatabase();

        String whereClause = DBHelper.REVIEW_ID + "=?";
        String[] whereArgs = new String[] {String.valueOf(id)};

        int result = db.delete(DBHelper.REVIEW_TABLE, whereClause, whereArgs);
        helper.close();

        if (result > 0) return true;
        return false;
    }

    //즐겨찾기 관련
    public ArrayList<ShowDto> getAllBookmarkList() {
        SQLiteDatabase db = helper.getReadableDatabase();
        cursor = db.rawQuery("select * from " + DBHelper.BOOKMARK_TABLE, null);

        ArrayList<ShowDto> list = new ArrayList<>();

        while (cursor.moveToNext()) {
            ShowDto dto = new ShowDto();
            dto.set_id(cursor.getLong(cursor.getColumnIndex(DBHelper.BOOKMARK_ID)));
            dto.setSeq(cursor.getString(cursor.getColumnIndex(DBHelper.BOOKMARK_SEQ)));
            dto.setTitle(cursor.getString(cursor.getColumnIndex(DBHelper.BOOKMARK_TITLE)));
            dto.setArea(cursor.getString(cursor.getColumnIndex(DBHelper.BOOKMARK_AREA)));
            dto.setPlace(cursor.getString(cursor.getColumnIndex(DBHelper.BOOKMARK_PLACE)));
            dto.setImageLink(cursor.getString(cursor.getColumnIndex(DBHelper.BOOKMARK_IMAGE)));
            dto.setRealmName(cursor.getString(cursor.getColumnIndex(DBHelper.BOOKMARK_REALMNAME)));
            dto.setStartDate(cursor.getString(cursor.getColumnIndex(DBHelper.BOOKMARK_START)));
            dto.setEndDate(cursor.getString(cursor.getColumnIndex(DBHelper.BOOKMARK_END)));
            dto.setPlaceUrl(cursor.getString(cursor.getColumnIndex(DBHelper.BOOKMARK_PLACE_URL)));
            dto.setPrice(cursor.getString(cursor.getColumnIndex(DBHelper.BOOKMARK_PRICE)));
            dto.setPhone(cursor.getString(cursor.getColumnIndex(DBHelper.BOOKMARK_PHONE)));
            dto.setTicketLink(cursor.getString(cursor.getColumnIndex(DBHelper.BOOKMARK_TICKET_LINK)));
            dto.setPlaceAddr(cursor.getString(cursor.getColumnIndex(DBHelper.BOOKMARK_PLACE_ADDR)));
            dto.setLatitude(cursor.getDouble(cursor.getColumnIndex(DBHelper.BOOKMARK_LATITUDE)));
            dto.setLongitude(cursor.getDouble(cursor.getColumnIndex(DBHelper.BOOKMARK_LONGITUDE)));
            list.add(dto);
        }
        cursor.close();
        helper.close();

        return list;
    }

    // 즐겨찾기 추가
    public boolean addBookmark(ShowDto dto) {
        SQLiteDatabase db = helper.getWritableDatabase();
        ContentValues row = new ContentValues();

        row.put(DBHelper.BOOKMARK_SEQ, dto.getSeq());
        row.put(DBHelper.BOOKMARK_TITLE, dto.getTitle(true));
        row.put(DBHelper.BOOKMARK_AREA, dto.getArea());
        row.put(DBHelper.BOOKMARK_PLACE, dto.getPlace());
        row.put(DBHelper.BOOKMARK_IMAGE, dto.getImageLink());
        row.put(DBHelper.BOOKMARK_REALMNAME, dto.getRealmName());
        row.put(DBHelper.BOOKMARK_START, dto.getStartDate());
        row.put(DBHelper.BOOKMARK_END, dto.getEndDate());
        row.put(DBHelper.BOOKMARK_LATITUDE, dto.getLatitude());
        row.put(DBHelper.BOOKMARK_LONGITUDE, dto.getLongitude());
        row.put(DBHelper.BOOKMARK_PLACE_URL, dto.getPlaceUrl());
        row.put(DBHelper.BOOKMARK_PLACE_ADDR, dto.getPlaceAddr());
        row.put(DBHelper.BOOKMARK_PRICE, dto.getPrice());
        row.put(DBHelper.BOOKMARK_PHONE, dto.getPhone());
        row.put(DBHelper.BOOKMARK_TICKET_LINK, dto.getTicketLink());

        long result = db.insert(DBHelper.BOOKMARK_TABLE, null, row);
        helper.close();

        if (result > 0) return true;
        return false;
    }

    public boolean existingBookmark(String seq) {
        SQLiteDatabase db = helper.getReadableDatabase();

        String selection = DBHelper.BOOKMARK_SEQ + "=?";
        String[] selectArgs = new String[] {seq};

        cursor = db.query(DBHelper.BOOKMARK_TABLE, null, selection, selectArgs, null, null, null);

        if (cursor.moveToNext()) {
            return true;
        } else {
            cursor.close();
            helper.close();
            return false;
        }
    }

    // 즐겨찾기 삭제
    public boolean removeBookmark(long id) {
        SQLiteDatabase db = helper.getWritableDatabase();

        String whereClause = DBHelper.BOOKMARK_ID + "=?";
        String[] whereArgs = new String[] {String.valueOf(id)};

        int result = db.delete(DBHelper.BOOKMARK_TABLE, whereClause, whereArgs);
        helper.close();

        if (result > 0) return true;
        return false;
    }

    public boolean removeBookmark(String seq) {
        SQLiteDatabase db = helper.getWritableDatabase();

        String whereClause = DBHelper.BOOKMARK_SEQ + "=?";
        String[] whereArgs = new String[] {String.valueOf(seq)};

        int result = db.delete(DBHelper.BOOKMARK_TABLE, whereClause, whereArgs);
        helper.close();

        if (result > 0) return true;
        return false;
    }

    public ShowDto getBookmarkById(long id) {
        ShowDto dto = new ShowDto();
        SQLiteDatabase db = helper.getReadableDatabase();
        String selection = DBHelper.BOOKMARK_ID + "=?";
        String[] selectArgs = new String[] {String.valueOf(id)};

        cursor = db.query(DBHelper.BOOKMARK_TABLE, null, selection, selectArgs, null, null, null);

        if (cursor.moveToNext()) {
            dto.set_id(cursor.getLong(cursor.getColumnIndex(DBHelper.BOOKMARK_ID)));
            dto.setSeq(cursor.getString(cursor.getColumnIndex(DBHelper.BOOKMARK_SEQ)));
            dto.setTitle(cursor.getString(cursor.getColumnIndex(DBHelper.BOOKMARK_TITLE)));
            dto.setArea(cursor.getString(cursor.getColumnIndex(DBHelper.BOOKMARK_AREA)));
            dto.setPlace(cursor.getString(cursor.getColumnIndex(DBHelper.BOOKMARK_PLACE)));
            dto.setImageLink(cursor.getString(cursor.getColumnIndex(DBHelper.BOOKMARK_IMAGE)));
            dto.setRealmName(cursor.getString(cursor.getColumnIndex(DBHelper.BOOKMARK_REALMNAME)));
            dto.setStartDate(cursor.getString(cursor.getColumnIndex(DBHelper.BOOKMARK_START)));
            dto.setEndDate(cursor.getString(cursor.getColumnIndex(DBHelper.BOOKMARK_END)));
            dto.setLatitude(cursor.getDouble(cursor.getColumnIndex(DBHelper.BOOKMARK_LATITUDE)));
            dto.setLongitude(cursor.getDouble(cursor.getColumnIndex(DBHelper.BOOKMARK_LONGITUDE)));
            dto.setPlaceUrl(cursor.getString(cursor.getColumnIndex(DBHelper.BOOKMARK_PLACE_URL)));
            dto.setPlaceAddr(cursor.getString(cursor.getColumnIndex(DBHelper.BOOKMARK_PLACE_ADDR)));
            dto.setPrice(cursor.getString(cursor.getColumnIndex(DBHelper.BOOKMARK_PRICE)));
            dto.setPhone(cursor.getString(cursor.getColumnIndex(DBHelper.BOOKMARK_PHONE)));
            dto.setTicketLink(cursor.getString(cursor.getColumnIndex(DBHelper.BOOKMARK_TICKET_LINK)));
        }
        cursor.close();
        helper.close();

        return dto;
    }

}
