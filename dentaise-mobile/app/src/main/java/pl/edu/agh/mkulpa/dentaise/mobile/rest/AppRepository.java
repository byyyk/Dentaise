package pl.edu.agh.mkulpa.dentaise.mobile.rest;

import android.net.http.AndroidHttpClient;
import android.util.Log;

import org.apache.http.Header;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.client.protocol.ClientContext;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.cookie.Cookie;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.protocol.BasicHttpContext;

import java.io.IOException;
import java.io.InputStream;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;
import java.util.ArrayList;
import java.util.List;


/**
 * Created by byyyk on 05.08.14.
 */
public class AppRepository {
    private final String TAG = AppRepository.class.getSimpleName();
    private AndroidHttpClient httpClient;
    private String baseUrl;
    private BasicHttpContext httpContext;
    private BasicCookieStore cookieStore;
    private String username;
    private String password;
    private String serverAddress;

    public AppRepository(String serverAddress, String username, String password) {
        httpClient = AndroidHttpClient.newInstance("dentaiseHttpClient");
        cookieStore = new BasicCookieStore();
        httpContext = new BasicHttpContext();
        httpContext.setAttribute(ClientContext.COOKIE_STORE, cookieStore);
        loadCertificate();
        setServerAddress(serverAddress);
        setUsername(username);
        setPassword(password);
    }

    private void loadCertificate() {
        try {
            KeyStore keyStore = null;
            keyStore = KeyStore.getInstance(KeyStore.getDefaultType());

            String keystoreLocation = "res/raw/generated.keystore";
            InputStream certStream = this.getClass().getClassLoader().getResourceAsStream(keystoreLocation);
            try {
                keyStore.load(certStream, null);

            } finally {
                certStream.close();
            }

            SSLSocketFactory socketFactory = new SSLSocketFactory(keyStore);
            Scheme sch = new Scheme("https", socketFactory, 443);
            httpClient.getConnectionManager().getSchemeRegistry().register(sch);
        } catch (CertificateException e) {
            Log.e(TAG, null, e);
        } catch (NoSuchAlgorithmException e) {
            Log.e(TAG, null, e);
        } catch (IOException e) {
            Log.e(TAG, null, e);
        } catch (KeyStoreException e) {
            Log.e(TAG, null, e);
        } catch (UnrecoverableKeyException e) {
            Log.e(TAG, null, e);
        } catch (KeyManagementException e) {
            Log.e(TAG, null, e);
        }
    }

    public void reconfigure() {
        Log.i(TAG, "reconfiguring for " + username + ":" + password + "@" + serverAddress);
        cookieStore.clear();
    }

    String getBaseUrl() {
        return baseUrl;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setServerAddress(String serverAddress) {
        this.serverAddress = serverAddress;
        if (!serverAddress.startsWith("https://") || !serverAddress.startsWith("http://")) {
            this.baseUrl = "https://" + serverAddress;
        } else {
            this.baseUrl = serverAddress;
        }
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

        HttpResponse response = httpClient.execute(httpPost, httpContext);
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
        if (serverAddress == null || serverAddress.isEmpty()) {
            throw new IOException("serverAddress not specified!");
        }
        if (username == null || username.isEmpty()) {
            throw new IOException("username not specified!");
        }
        if (password == null || password.isEmpty()) {
            throw new IOException("password not specified!");
        }

        HttpResponse response;
        boolean loggedInBeforehand = false;
        if (notLoggedIn()) {
            Log.i(TAG, "User is not logged in");
            login();
            loggedInBeforehand = true;
        }

        Log.i(TAG, "Trying to call " + request.getMethod() + " " + request.getURI() + " ...");
        request.setHeader("Accept", "application/json");
        response = httpClient.execute(request, httpContext);
        print(response);

        boolean definitelyUnauthorized = false;
        if (unauthorized(response)) {
            Log.i(TAG, "Unauthorized!");
            if (!loggedInBeforehand) {
                // maybe session expired, retry
                login();
                Log.i(TAG, "Retrying...");
                response = httpClient.execute(request, httpContext);
                print(response);
                if (unauthorized(response)) {
                    definitelyUnauthorized = true;
                }
            } else {
                definitelyUnauthorized = true;
            }
        }
        if (notOk(response)) {
            throw new IOException("Server did not respond with 200 OK");
        }
        if (definitelyUnauthorized) {
            throw new AuthenticationFailedException("Could not access requested resource, authentication error");
        }
        return response;
    }

    private boolean notOk(HttpResponse response) {
        return response.getStatusLine().getStatusCode() != 200;
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
