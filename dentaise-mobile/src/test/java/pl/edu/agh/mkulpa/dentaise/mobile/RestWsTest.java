package pl.edu.agh.mkulpa.dentaise.mobile;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.CookieStore;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.protocol.ClientContext;
import org.apache.http.cookie.Cookie;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HttpContext;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import android.net.http.AndroidHttpClient;

@RunWith(JUnit4.class)
public class RestWsTest {

	@Test
	@Ignore
	public void testRestWs() {
		AndroidHttpClient httpClient = AndroidHttpClient.newInstance("dentaiseHttpClient");
	    CookieStore cookieStore = new BasicCookieStore();
	    HttpContext context = new BasicHttpContext();
	    context.setAttribute(ClientContext.COOKIE_STORE, cookieStore);
	    
		try {
			HttpConnectionParams.setSoTimeout(httpClient.getParams(), 30000);
			HttpPost httpPost = new HttpPost("http://127.0.0.1:9000/login");

			List<NameValuePair> nvps = new ArrayList<NameValuePair>();
			nvps.add(new BasicNameValuePair("username", "admin"));
			nvps.add(new BasicNameValuePair("password", "dupa123"));
			httpPost.setEntity(new UrlEncodedFormEntity(nvps, "UTF-8"));

			HttpResponse response = httpClient.execute(httpPost, context);
			Assert.assertTrue(response.getStatusLine().getStatusCode() == 200);
			
			System.out.println("Post logon cookies:");
			List<Cookie> cookies = cookieStore.getCookies();
			if (cookies.isEmpty()) {
				System.out.println("None");
			} else {
				for (int i = 0; i < cookies.size(); i++) {
					System.out.println("- " + cookies.get(i).toString());
				}
			}
			
			HttpGet httpGet = new HttpGet("http://127.0.0.1:9000/");
			response = httpClient.execute(httpGet);
			System.out.println(response.toString());
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			httpClient.getConnectionManager().shutdown();
		}
	}

}