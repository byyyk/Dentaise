package pl.edu.agh.mkulpa.dentaise.mobile.rest;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.CookieStore;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.protocol.ClientContext;
import org.apache.http.cookie.Cookie;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HTTP;
import org.apache.http.protocol.HttpContext;
import org.apache.http.util.EntityUtils;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.type.TypeReference;
import org.json.JSONArray;
import org.json.JSONException;

import android.net.http.AndroidHttpClient;
import android.util.JsonReader;
import android.util.Log;

public class RestClient {

    private static final AndroidHttpClient httpClient;
    private static final String baseUrl = "http://192.168.2.123:9000";
    private static final BasicHttpContext context;
    private static final BasicCookieStore cookieStore;

    static {
		httpClient = AndroidHttpClient.newInstance("dentaiseHttpClient");
	    cookieStore = new BasicCookieStore();
	    context = new BasicHttpContext();
	    context.setAttribute(ClientContext.COOKIE_STORE, cookieStore);
	    
		login();
	}

    public static void login() {
        try {
            logout();

            Log.i(RestClient.class.getSimpleName(), "Logging in...");
            HttpConnectionParams.setSoTimeout(httpClient.getParams(), 10000);
            HttpPost httpPost = new HttpPost(baseUrl + "/login");

            List<NameValuePair> nvps = new ArrayList<NameValuePair>();
            nvps.add(new BasicNameValuePair("username", "mckulpa"));
            nvps.add(new BasicNameValuePair("password", "s3cret"));
            httpPost.setEntity(new UrlEncodedFormEntity(nvps, "UTF-8"));

            HttpResponse response = httpClient.execute(httpPost, context);
            print("LOGIN", response);

            System.out.println("Post logon cookies:");
            List<Cookie> cookies = cookieStore.getCookies();
            if (cookies.isEmpty()) {
                System.out.println("None");
            } else {
                for (int i = 0; i < cookies.size(); i++) {
                    System.out.println("- " + cookies.get(i).toString());
                }
            }

            HttpGet httpGet = new HttpGet(baseUrl + "/");
            response = httpClient.execute(httpGet);
            print("TEST LOGIN", response);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void print(String prefix, HttpResponse response) {
        StringBuilder builder = new StringBuilder();
        builder.append(prefix + " ");
        builder.append("Status: " + response.getStatusLine().getStatusCode() + " " + response.getStatusLine().getReasonPhrase() + "\n");
        Header[] headers = response.getAllHeaders();
        for (Header header : headers) {
            builder.append(header.getName() + ": " + header.getValue() + "\n");
        }
        Log.d(RestClient.class.getSimpleName(), builder.toString());
    }

    private static void logout() {
        //TODO: logout via ws?
        cookieStore.clear();
    }


    public static Patient getPatient(long id) throws IOException, JSONException {
        HttpGet httpGet = new HttpGet(baseUrl + "/patients/" + id);
        httpGet.setHeader("Accept", "application/json");
        HttpResponse response = httpClient.execute(httpGet, context);
        ObjectMapper mapper = new ObjectMapper();
        Patient patient = mapper.readValue(response.getEntity().getContent(), Patient.class);
        return patient;
    }

    public static void savePatient(Patient patient) throws IOException, JSONException {
        HttpPost httpPost = new HttpPost(baseUrl + "/patients");
        httpPost.setHeader("Accept", "application/json");
        ObjectMapper mapper = new ObjectMapper();
        StringEntity entity = new StringEntity(mapper.writeValueAsString(patient), HTTP.UTF_8);
        entity.setContentType("application/json");
        httpPost.setEntity(entity);
        HttpResponse response = httpClient.execute(httpPost, context);
        print("SAVE", response);
    }

    public static List<Patient> listPatients(String query) throws IOException, JSONException {
        HttpGet httpGet = new HttpGet(baseUrl + "/patients" + (query != null ? "?query=" + URLEncoder.encode(query, "UTF-8") : ""));
        httpGet.setHeader("Accept", "application/json");
        HttpResponse response = httpClient.execute(httpGet, context);
        print("LIST", response);
        ObjectMapper mapper = new ObjectMapper();
        List<Patient> patients = mapper.readValue(response.getEntity().getContent(), new TypeReference<List<Patient>>() {});
        return patients;
    }

    public void close() {
        httpClient.getConnectionManager().shutdown();
    }

}
