package pl.edu.agh.mkulpa.dentaise.mobile.rest;

import android.net.http.AndroidHttpClient;
import android.util.Log;

import org.apache.http.Header;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.client.protocol.ClientContext;
import org.apache.http.cookie.Cookie;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.protocol.BasicHttpContext;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by byyyk on 05.08.14.
 */
public class AppRepository {
    private final String TAG = AppRepository.class.getSimpleName();
    private AndroidHttpClient httpClient;
    private String baseUrl;
    private BasicHttpContext context;
    private BasicCookieStore cookieStore;
    private String username;
    private String password;

    public AppRepository(String baseUrl, String username, String password) {
        httpClient = AndroidHttpClient.newInstance("dentaiseHttpClient");
        cookieStore = new BasicCookieStore();
        context = new BasicHttpContext();
        context.setAttribute(ClientContext.COOKIE_STORE, cookieStore);
        reconfigure(baseUrl, username, password);
    }

    public void reconfigure(String baseUrl, String username, String password) {
        this.baseUrl = baseUrl;
        this.username = username;
        this.password = password;
        cookieStore.clear();
    }

    public String getBaseUrl() {
        return baseUrl;
    }

    public void print(HttpResponse response) {
        StringBuilder builder = new StringBuilder();
        builder.append("Status: " + response.getStatusLine().getStatusCode() + " " + response.getStatusLine().getReasonPhrase() + "\n");
        Header[] headers = response.getAllHeaders();
        for (Header header : headers) {
            builder.append(header.getName() + ": " + header.getValue() + "\n");
        }
        Log.d(PatientRepository.class.getSimpleName(), builder.toString());
    }

    public void login() throws AuthenticationFailedException, IOException {
        logout();

        Log.i(PatientRepository.class.getSimpleName(), "Logging in...");
        HttpConnectionParams.setSoTimeout(httpClient.getParams(), 10000);
        HttpPost httpPost = new HttpPost(baseUrl + "/login");
        httpPost.setHeader("Accept", "application/json");

        List<NameValuePair> entity = new ArrayList<NameValuePair>();
        entity.add(new BasicNameValuePair("username", username));
        entity.add(new BasicNameValuePair("password", password));
        httpPost.setEntity(new UrlEncodedFormEntity(entity, "UTF-8"));

        HttpResponse response = httpClient.execute(httpPost, context);
        print(response);
        if (unauthorized(response)) {
            throw new AuthenticationFailedException("Could not login, probably wrong user or password");
        }

        StringBuilder cookieStringBuilder = new StringBuilder();
        List<Cookie> cookies = cookieStore.getCookies();
        if (cookies.isEmpty()) {
            cookieStringBuilder.append("None");
        } else {
            for (int i = 0; i < cookies.size(); i++) {
                cookieStringBuilder.append("[" + cookies.get(i).toString() + "]");
            }
        }
        Log.i(TAG, "Post logon cookies: " + cookieStringBuilder.toString());
    }

    public HttpResponse execute(HttpUriRequest request) throws IOException, AuthenticationFailedException {
        HttpResponse response;
        boolean loggedInBeforehand = false;
        if (notLoggedIn()) {
            Log.i(TAG, "User is not logged in");
            login();
            loggedInBeforehand = true;
        }

        Log.i(TAG, "Trying to call " + request.getMethod() + " " + request.getURI() + " ...");
        request.setHeader("Accept", "application/json");
        response = httpClient.execute(request, context);
        print(response);

        boolean definitelyUnauthorized = false;
        if (unauthorized(response)) {
            Log.i(TAG, "Unauthorized!");
            if (!loggedInBeforehand) {
                // maybe session expired, retry
                login();
                Log.i(TAG, "Retrying...");
                response = httpClient.execute(request, context);
                print(response);
                if (unauthorized(response)) {
                    definitelyUnauthorized = true;
                }
            } else {
                definitelyUnauthorized = true;
            }
        }

        if (definitelyUnauthorized) {
            throw new AuthenticationFailedException("Could not access requested resource, authentication error");
        }
        return response;
    }

    private boolean unauthorized(HttpResponse response) {
        return response.getStatusLine().getStatusCode() == 401;
    }

    private boolean notLoggedIn() {
        return cookieStore.getCookies().size() == 0;
    }

    public void logout() {
        //TODO: logout via ws?
        cookieStore.clear();
    }

    public void close() {
        httpClient.getConnectionManager().shutdown();
    }
}
