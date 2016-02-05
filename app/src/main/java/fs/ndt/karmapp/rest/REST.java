package fs.ndt.karmapp.rest;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import java.io.UnsupportedEncodingException;

import cz.msebera.android.httpclient.HttpEntity;
import cz.msebera.android.httpclient.entity.StringEntity;
import cz.msebera.android.httpclient.message.BasicHeader;
import cz.msebera.android.httpclient.protocol.HTTP;

/**
 * Created by tiocansino on 29/1/16.
 */
public class REST {
    private static final String BASE_URL = "http://karmapp.getsandbox.com";

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

    public static void post(String url, RequestParams params, AsyncHttpResponseHandler responseHandler) {
        client.post(getAbsoluteUrl(url), params, responseHandler);
    }

    private static String getAbsoluteUrl(String relativeUrl) {
        return BASE_URL + relativeUrl;
    }
}
