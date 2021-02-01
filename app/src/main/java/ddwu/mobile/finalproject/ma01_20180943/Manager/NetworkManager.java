package ddwu.mobile.finalproject.ma01_20180943.Manager;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import javax.net.ssl.HttpsURLConnection;

public class NetworkManager {
    public static final String TAG = "NetworkManager";

    private Context context;

    public NetworkManager(Context context) {
        this.context = context;
    }

//    downloadContents() 와 downloadImage()를 하나의 메소드로 구현할 경우
//    사용 시 매개변수로 String or Image 를 구분, 반환 값은 타입캐스팅 필요
    public Object download(String address, boolean isImage) {
        HttpURLConnection conn = null;
        InputStream stream = null;
        Object result = null;

        try {
            URL url = new URL(address);
            conn = (HttpURLConnection)url.openConnection();
            stream = getNetworkConnection(conn);
            if (isImage) {
                result = readStreamToBitmap(stream);
            } else {
                result = readStreamToString(stream);
            }
            if (stream != null) stream.close();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (conn != null) conn.disconnect();
        }

        return result;
    }

    /* InputStream을 전달받아 비트맵으로 변환 후 반환 */
    private Bitmap readStreamToBitmap(InputStream stream) {
        return BitmapFactory.decodeStream(stream);
    }


    /* InputStream을 전달받아 문자열로 변환 후 반환 */
    private String readStreamToString(InputStream stream){
        StringBuilder result = new StringBuilder();

        try {
            InputStreamReader inputStreamReader = new InputStreamReader(stream);
            BufferedReader bufferedReader = new BufferedReader(inputStreamReader);

            String readLine = bufferedReader.readLine();

            while (readLine != null) {
                result.append(readLine + "\n");
                readLine = bufferedReader.readLine();
            }

            bufferedReader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return result.toString();
    }


    /* 네트워크 환경 조사 */
    private boolean isOnline() {
        ConnectivityManager connMgr = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        return (networkInfo != null && networkInfo.isConnected());
    }


    /* URLConnection 을 전달받아 연결정보 설정 후 연결, 연결 후 수신한 InputStream 반환 */
    private InputStream getNetworkConnection(HttpURLConnection conn) throws Exception {
        conn.setReadTimeout(3000);
        conn.setConnectTimeout(3000);
        conn.setRequestMethod("GET");
        conn.setDoInput(true);

        int responseCode = conn.getResponseCode();

        if (responseCode == HttpURLConnection.HTTP_OK) {
            return conn.getInputStream();
        } else if (responseCode == HttpURLConnection.HTTP_MOVED_TEMP) {
            String redirectionUrl = conn.getHeaderField("Location");
            URL url = new URL(redirectionUrl);
            HttpURLConnection conn2 = (HttpURLConnection)url.openConnection();
            conn2.setReadTimeout(3000);
            conn2.setConnectTimeout(3000);
            conn2.setRequestMethod("GET");
            conn2.setDoInput(true);
            conn2.connect();

            return conn2.getInputStream();
        } else {
            throw new IOException("HTTP error code: " + responseCode);
        }
    }


}
