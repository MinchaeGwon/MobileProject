package ddwu.mobile.finalproject.ma01_20180943.Show;

import android.util.Log;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.StringReader;
import java.util.ArrayList;

public class ShowXmlParser {
    public static final String TAG = "ShowXmlParser";

    public enum TagType { NONE, SEQ, TITLE, AREA, PLACE, IMAGE, REALMNAME, START_DATE, END_DATE,
        PLACE_URL, ADDR, PRICE, LINK, PHONE, LATITUDE, LONGITUDE };

    final static String TAG_ITEM = "perforList";
    final static String TAG_SEQ = "seq";
    final static String TAG_TITLE = "title";
    final static String TAG_AREA = "area";
    final static String TAG_PLACE = "place";
    final static String TAG_IMAGE = "thumbnail";
    final static String TAG_REALMNAME = "realmName";
    final static String TAG_START_DATE = "startDate";
    final static String TAG_END_DATE = "endDate";
    final static String TAG_PLACE_URL = "placeUrl";
    final static String TAG_ADDR = "placeAddr";
    final static String TAG_PRICE = "price";
    final static String TAG_LINK = "url";
    final static String TAG_PHONE = "phone";
    final static String TAG_LATITUDE = "gpsY";
    final static String TAG_LONGITUDE = "gpsX";

    public ShowXmlParser() {
    }

    public ArrayList<ShowDto> placeParse(String xml) {
        ArrayList<ShowDto> resultList = new ArrayList();
        ShowDto dto = null;

        TagType tagType = TagType.NONE;

        try {
            XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
            XmlPullParser parser = factory.newPullParser();
            parser.setInput(new StringReader(xml));

            int eventType = parser.getEventType();
            while (eventType != XmlPullParser.END_DOCUMENT) {
                switch (eventType) {
                    case XmlPullParser.START_DOCUMENT:
                        break;
                    case XmlPullParser.END_DOCUMENT:
                        break;
                    case XmlPullParser.START_TAG:
                        if (parser.getName().equals(TAG_ITEM)) {
                            dto = new ShowDto();
                        } else if (parser.getName().equals(TAG_SEQ)) {
                            if (dto != null) tagType = TagType.SEQ;
                        } else if (parser.getName().equals(TAG_TITLE)) {
                            if (dto != null) tagType = TagType.TITLE;
                        } else if (parser.getName().equals(TAG_AREA)) {
                            if (dto != null) tagType = TagType.AREA;
                        } else if (parser.getName().equals(TAG_PLACE)) {
                            if (dto != null) tagType = TagType.PLACE;
                        } else if (parser.getName().equals(TAG_IMAGE)) {
                            if (dto != null) tagType = TagType.IMAGE;
                        } else if (parser.getName().equals(TAG_REALMNAME)) {
                            if (dto != null) tagType = TagType.REALMNAME;
                        } else if (parser.getName().equals(TAG_START_DATE)) {
                            if (dto != null) tagType = TagType.START_DATE;
                        } else if (parser.getName().equals(TAG_END_DATE)) {
                            if (dto != null) tagType = TagType.END_DATE;
                        } else if (parser.getName().equals(TAG_LATITUDE)) {
                            if (dto != null) tagType = TagType.LATITUDE;
                        } else if (parser.getName().equals(TAG_LONGITUDE)) {
                            if (dto != null) tagType = TagType.LONGITUDE;
                        }
                        break;
                    case  XmlPullParser.END_TAG:
                        if (parser.getName().equals(TAG_ITEM)) {
                            resultList.add(dto);
                            dto = null;
                        }
                        break;
                    case XmlPullParser.TEXT:
                        switch (tagType) {
                            case SEQ:
                                dto.setSeq(parser.getText());
                                break;
                            case TITLE:
                                dto.setTitle(parser.getText());
                                break;
                            case AREA:
                                dto.setArea(parser.getText());
                                break;
                            case PLACE:
                                dto.setPlace(parser.getText());
                                break;
                            case IMAGE:
                                dto.setImageLink(parser.getText());
                                break;
                            case REALMNAME:
                                dto.setRealmName(parser.getText());
                                break;
                            case START_DATE:
                                dto.setStartDate(parser.getText());
                                break;
                            case END_DATE:
                                dto.setEndDate(parser.getText());
                                break;
                            case LATITUDE:
                                dto.setLatitude(Double.valueOf(parser.getText()));
                                break;
                            case LONGITUDE:
                                dto.setLongitude(Double.valueOf(parser.getText()));
                                break;
                        }
                        tagType = TagType.NONE;
                        break;
                }
                eventType = parser.next();
            }


        } catch (Exception e) {
            e.printStackTrace();
        }


        return resultList;
    }

    public  ShowDto detailParse(String xml, ShowDto dto) {
        TagType tagType = TagType.NONE;

        try {
            XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
            XmlPullParser parser = factory.newPullParser();
            parser.setInput(new StringReader(xml));

            int eventType = parser.getEventType();
            while (eventType != XmlPullParser.END_DOCUMENT) {
                switch (eventType) {
                    case XmlPullParser.START_DOCUMENT:
                        break;
                    case XmlPullParser.END_DOCUMENT:
                        break;
                    case XmlPullParser.START_TAG:
                        if (parser.getName().equals(TAG_PRICE)) {
                            if (dto != null) tagType = TagType.PRICE;
                        } else if (parser.getName().equals(TAG_ADDR)) {
                            if (dto != null) tagType = TagType.ADDR;
                        } else if (parser.getName().equals(TAG_PLACE_URL)) {
                            if (dto != null) tagType = TagType.PLACE_URL;
                        } else if (parser.getName().equals(TAG_LINK)) {
                            if (dto != null) tagType = TagType.LINK;
                        } else if (parser.getName().equals(TAG_PHONE)) {
                            if (dto != null) tagType = TagType.PHONE;
                        }
                        break;
                    case  XmlPullParser.END_TAG:
                        break;
                    case XmlPullParser.TEXT:
                        switch (tagType) {
                            case PRICE:
                                dto.setPrice(parser.getText());
                                break;
                            case ADDR:
                                dto.setPlaceAddr(parser.getText());
                                break;
                            case PLACE_URL:
                                dto.setPlaceUrl(parser.getText());
                                break;
                            case LINK:
                                dto.setTicketLink(parser.getText());
                                break;
                            case PHONE:
                                dto.setPhone(parser.getText());
                                break;
                        }
                        tagType = TagType.NONE;
                        break;
                }
                eventType = parser.next();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return dto;
    }

}
