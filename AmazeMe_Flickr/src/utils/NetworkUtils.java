package utils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import org.apache.commons.io.IOUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;

import android.content.Context;
import android.os.Environment;
import beans.FlickrBundle;

import com.google.gson.Gson;

import exceptions.InvalidDateFormat;

public class NetworkUtils {
	public static final String url_part1="http://api.flickr.com/services/rest/?method=flickr.interestingness.getList&api_key=254169c337f3e8b405ad1fdd6a22ca70" +
			"&format=json&date=";
	private Context context;
public NetworkUtils(Context context) {
	this.context=context;
}
	public FlickrBundle fetchInterestingList(String date) throws ClientProtocolException, IOException, InvalidDateFormat{
		if(checkFormat(date)){
			HttpEntity entity = hitUrl(url_part1+date);
			String jsonResponse=IOUtils.toString(entity.getContent());
			String bundleStr=jsonResponse.substring(14, jsonResponse.length()-1);			
			Gson gson=new Gson();
			FlickrBundle bundle= gson.fromJson(bundleStr, FlickrBundle.class);
			return bundle;			
		}else{
			throw new InvalidDateFormat();
		}
		
	}
	private HttpEntity hitUrl(String url) throws IOException,
			ClientProtocolException {
		int TIMEOUT_MILLISEC = 15000;  // = 15 seconds
		HttpParams httpParams = new BasicHttpParams();
		HttpConnectionParams.setConnectionTimeout(httpParams, TIMEOUT_MILLISEC);
		HttpConnectionParams.setSoTimeout(httpParams, TIMEOUT_MILLISEC);
		HttpClient client=new DefaultHttpClient(httpParams);
		HttpGet getRequest=new HttpGet(url);
		HttpResponse response=client.execute(getRequest);
		HttpEntity entity= response.getEntity();
		return entity;
	}
	private boolean checkFormat(String date) {
		if (date.matches("\\d{4}-\\d{2}-\\d{2}")) {
		    return true;
		}
		return false;
	}
	public String downloadPhoto(String url,String date, int i) throws ClientProtocolException, IOException{		
		String imageName="img_"+i+".jpg";
		/*File sdCard = Environment.getExternalStorageDirectory();
		File dir = new File (sdCard.getAbsolutePath() + "/amazeme/"+date);
		dir.mkdirs();
		File file = new File(dir, imageName);
		FileOutputStream f = new FileOutputStream(file);*/
		FileOutputStream fos=context.openFileOutput(imageName, Context.MODE_PRIVATE);;
		fos.write(IOUtils.toByteArray(hitUrl(url).getContent()));
		return imageName;
	}
}
