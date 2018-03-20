/* 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package info.guardianproject.net.http;


import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;

import android.util.Log;

/*
 * General HTTP handler class
 */
public class HttpManager {
	
	private final static String TAG = "HttpManager";
	
	private final static String POST_MIME_TYPE = "application/x-www-form-urlencoded";
	
	public static String doGet (String serviceEndpoint, Properties props) throws Exception
	{

		HttpClient httpClient = new SocksHttpClient();

		StringBuilder uriBuilder = new StringBuilder(serviceEndpoint);
	
		StringBuffer sbResponse = new StringBuffer ();
		
		Enumeration<Object> enumProps = props.keys();
		String key, value = null;
		
		uriBuilder.append('?');
		
		while (enumProps.hasMoreElements())
		{
			key = (String)enumProps.nextElement();
			value = (String)props.get(key);
			uriBuilder.append(key);
			uriBuilder.append('=');
			uriBuilder.append(java.net.URLEncoder.encode(value));
			uriBuilder.append('&');
			
		}
		

		HttpGet request = new HttpGet(uriBuilder.toString());
		HttpResponse response = httpClient.execute(request);

		int status = response.getStatusLine().getStatusCode();

		// we assume that the response body contains the error message
		if (status != HttpStatus.SC_OK) {
			ByteArrayOutputStream ostream = new ByteArrayOutputStream();
			response.getEntity().writeTo(ostream);
			Log.e("HTTP CLIENT", ostream.toString());
			
			return null;
			
		} else {
			InputStream content = response.getEntity().getContent();
			// <consume response>
			
			BufferedReader reader = new BufferedReader (new InputStreamReader(content));
			String line;
			
			while ((line = reader.readLine())!=null)
				sbResponse.append(line);
			
			content.close(); // this will also close the connection
		}
		
		return sbResponse.toString();
		
	}
	
	public static String doPost (String serviceEndpoint, Properties props) throws Exception
	{

		DefaultHttpClient httpClient = new SocksHttpClient();
		
		HttpPost request = new HttpPost(serviceEndpoint);
		HttpResponse response = null;
		HttpEntity entity = null;
		  
		StringBuffer sbResponse = new StringBuffer ();
		
		Enumeration<Object> enumProps = props.keys();
		String key, value = null;
		
		 List <NameValuePair> nvps = new ArrayList <NameValuePair>();
		
		while (enumProps.hasMoreElements())
		{
			key = (String)enumProps.nextElement();
			value = (String)props.get(key);
			nvps.add(new BasicNameValuePair(key, value));
			
			Log.i(TAG,"adding nvp:" + key + "=" + value);
		}
		
		UrlEncodedFormEntity uf = new UrlEncodedFormEntity(nvps, HTTP.UTF_8);
		
		Log.i(TAG, uf.toString());
		
		request.setEntity(uf);
         
		request.setHeader("Content-Type", POST_MIME_TYPE);
		
		Log.i(TAG, "http post request: " + request.toString());
		
         // Post, check and show the result (not really spectacular, but works):
         response = httpClient.execute(request);
         entity = response.getEntity(); 
         
		int status = response.getStatusLine().getStatusCode();

		// we assume that the response body contains the error message
		if (status != HttpStatus.SC_OK) {
			ByteArrayOutputStream ostream = new ByteArrayOutputStream();
			entity.writeTo(ostream);
			
			Log.e(TAG, " error status code="+status);
			Log.e(TAG, ostream.toString());
				
			return null;
		} else {
			InputStream content = response.getEntity().getContent();
			// <consume response>
			
			BufferedReader reader = new BufferedReader (new InputStreamReader(content));
			String line;
			
			while ((line = reader.readLine())!=null)
				sbResponse.append(line);
			
			content.close(); // this will also close the connection
			
			return sbResponse.toString();
		}
		
		
		
	}
	
	
	public static String uploadFile (String serviceEndpoint, Properties properties, String fileParam, String file) throws Exception
	{


		HttpClient httpClient = new DefaultHttpClient();

		HttpPost request = new HttpPost(serviceEndpoint);
		MultipartEntity entity = new MultipartEntity();
			
		Iterator<Map.Entry<Object,Object>> i = properties.entrySet().iterator();
		while (i.hasNext())
		{
		       Map.Entry<Object,Object> entry = (Map.Entry<Object,Object>) i.next();
		       String key = (String) entry.getKey();
		       String val = (String) entry.getValue();
		       entity.addPart(key, new StringBody(val));
		     
		 }
		File upload = new File(file);
		Log.i("httpman","upload file (" + upload.getAbsolutePath() + ") size=" + upload.length());
		
		entity.addPart(fileParam, new FileBody(upload));
		request.setEntity(entity);
		
		
		HttpResponse response = httpClient.execute(request);
		int status = response.getStatusLine().getStatusCode();

		if (status != HttpStatus.SC_OK) {
	
		} else {
			
		}

		return response.toString();
		
	}
	
	
}
