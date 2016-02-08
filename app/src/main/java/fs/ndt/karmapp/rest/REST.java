package fs.ndt.karmapp.rest;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.util.Iterator;

import cz.msebera.android.httpclient.HttpEntity;
import cz.msebera.android.httpclient.entity.ContentType;
import cz.msebera.android.httpclient.entity.StringEntity;
import cz.msebera.android.httpclient.message.BasicHeader;
import cz.msebera.android.httpclient.protocol.HTTP;

/**
 * Created by tiocansino on 29/1/16.
 */
public class REST {
    //private static final String BASE_URL = "http://192.168.10.185:8080/api";
    //private static final String BASE_URL = "http://karmapp.getsandbox.com";
    private static final String BASE_URL = "http://192.168.10.208:8080/api";

    private static AsyncHttpClient client = new AsyncHttpClient();

    public static void get(String url, HttpEntity entity, AsyncHttpResponseHandler responseHandler) {
        StringEntity se = null;
        try {
            se = new StringEntity("");
            se.setContentType(new BasicHeader(HTTP.CONTENT_TYPE, "application/json"));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        client.get(null, getAbsoluteUrl(url), se, "application/json", responseHandler);
    }

    public static void post(String url, JSONObject body, AsyncHttpResponseHandler responseHandler) {
        RequestParams params = new RequestParams();
        try {
//            Iterator<String> itKeys = body.keys();
//            while (itKeys.hasNext()) {
//                String key = itKeys.next();
//                params.put(key, body.get(key));
//
//            }
            StringEntity entity = new StringEntity(body.toString(), ContentType.APPLICATION_JSON);
            entity.setContentType(new BasicHeader(HTTP.CONTENT_TYPE, "application/json"));
            entity.setContentEncoding("UTF-8");
            client.post(null, getAbsoluteUrl(url), entity, "application/json", responseHandler);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static String getAbsoluteUrl(String relativeUrl) {
        return BASE_URL + relativeUrl;
    }
}
