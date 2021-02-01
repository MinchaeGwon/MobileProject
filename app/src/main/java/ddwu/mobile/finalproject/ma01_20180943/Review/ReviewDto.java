package ddwu.mobile.finalproject.ma01_20180943.Review;

import java.io.Serializable;

public class ReviewDto implements Serializable {
    private long _id;
    private String title;
    private String content;

    public long get_id() {
        return _id;
    }

    public void set_id(long _id) {
        this._id = _id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}
