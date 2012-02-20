package i.amaze.u;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;

import org.apache.http.client.ClientProtocolException;

import utils.NetworkUtils;
import utils.touch.PinchZoom;
import android.app.Activity;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Parcelable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import beans.DownloadedPhoto;
import beans.FlickrBundle;
import beans.Photo;
import beans.PhotoPack;
import exceptions.InvalidDateFormat;

public class ShowMe extends Activity {
	ProgressBar progressBar;
	private TextView msgTV;
	private FlickrBundle flickrBundle;
	private ArrayList<DownloadedPhoto> photosDownloaded = new ArrayList<DownloadedPhoto>();
	private ViewPager flickrPager;
	private String date;

	private File sdCard = Environment.getExternalStorageDirectory();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.show_me);
		msgTV = (TextView) findViewById(R.id.msgTV);
		Bundle bundle = getIntent().getExtras();
		if (bundle == null || bundle.getString("date") == null) {
			msgTV.setText("No date selected!");
			return;
		}
		progressBar = (ProgressBar) findViewById(R.id.progressBar1);
		date = bundle.getString("date");
		FetchPhotoList fetch = new FetchPhotoList();
		fetch.execute(date);
		flickrPager = (ViewPager) findViewById(R.id.awesomepager);
		flickrPager.setAdapter(new MyPagerAdapter());
	}
	class MyPagerAdapter extends PagerAdapter {	

		@Override
		public void destroyItem(View arg0, int arg1, Object arg2) {
			unbindDrawables((View) arg2);
			((ViewPager) arg0).removeView((FrameLayout) arg2);
			
		}

		@Override
		public int getItemPosition(Object object) {
			return POSITION_NONE;
		}
		private void unbindDrawables(View view) {
	        if (view.getBackground() != null) {
	        view.getBackground().setCallback(null);
	        }
	        if (view instanceof ViewGroup) {
	            for (int i = 0; i < ((ViewGroup) view).getChildCount(); i++) {
	            unbindDrawables(((ViewGroup) view).getChildAt(i));
	            }
	        ((ViewGroup) view).removeAllViews();
	        }
	    }
		@Override
		public void finishUpdate(View arg0) {
			// TODO Auto-generated method stub

		}

		@Override
		public int getCount() {
			/*if (flickrBundle != null) {
				return flickrBundle.getPhotos().getPerpage();
			}*/
			return 5;
		}

		@Override
		public Object instantiateItem(View arg0, int arg1) {
			LayoutInflater inflater = getLayoutInflater();
			FrameLayout fl = /*(RelativeLayout) arg0
					.findViewById(R.id.rl_page);
			if (arg1 < photosDownloaded.size() - 1 && rl!=null)
				return rl;
			rl = */(FrameLayout) inflater.inflate(R.layout.photo_page, null);

			if (photosDownloaded.size() > arg1) {
				DownloadedPhoto currentPhoto = photosDownloaded.get(arg1);
				fl.findViewById(R.id.progressBar1).setVisibility(View.GONE);
				fl.findViewById(R.id.progressBar2).setVisibility(View.GONE);
				ImageView iv = (ImageView) fl.findViewById(R.id.imageView1);
				/*iv.setImageBitmap(BitmapFactory
						.decodeFile(sdCard.getAbsolutePath() + "/amazeme/"+date+"/"+currentPhoto.getFilePath()));*/
				try {
					iv.setImageBitmap(BitmapFactory.decodeStream(getApplicationContext().openFileInput(currentPhoto.getFilePath())));
				} catch (FileNotFoundException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				iv.setOnTouchListener(new PinchZoom(iv));
				TextView tv = (TextView) fl.findViewById(R.id.textView1);
				tv.setText(currentPhoto.getPhoto().getTitle());
			}
			((ViewPager) arg0).addView(fl);
			return fl;
		}

		@Override
		public boolean isViewFromObject(View arg0, Object arg1) {
			return arg0 == (FrameLayout) arg1;
		}

		@Override
		public void restoreState(Parcelable arg0, ClassLoader arg1) {
			// TODO Auto-generated method stub

		}

		@Override
		public Parcelable saveState() {
			
			return null;
		}

		@Override
		public void startUpdate(View arg0) {
			// TODO Auto-generated method stub

		}

	}

	class FetchPhotoList extends AsyncTask<String, Void, FlickrBundle> {
		String error = null;

		@Override
		protected void onPreExecute() {
			// TODO Auto-generated method stub
			super.onPreExecute();
			showProgressBar();
		}

		@Override
		protected FlickrBundle doInBackground(String... params) {
			NetworkUtils utils = new NetworkUtils(getApplicationContext());
			FlickrBundle bundle = null;
			try {
				bundle = utils.fetchInterestingList(params[0]);
			} catch (InvalidDateFormat e) {
				error = e.getMessage();
			} catch (Exception e) {
				Log.e("amaze", "Something bad happened:" + e.getMessage());
			}
			return bundle;
		}

		@Override
		protected void onPostExecute(FlickrBundle result) {
			// TODO Auto-generated method stub
			super.onPostExecute(result);
			hideProgressBar();
			if (error != null) {
				msgTV.setText(error);
			} else {
				if (result != null) {
					if (result.getStat().equalsIgnoreCase("ok")) {
						msgTV.setVisibility(View.GONE);
						flickrBundle = result;
						Log.e("amaze", "photo pack page size: "
								+ result.getPhotos().getPerpage());
						Log.e("amaze", "photo list size: "
								+ result.getPhotos().getPhoto().size());
						FetchPhotos fetchPhotos = new FetchPhotos();
						fetchPhotos.execute(result);
					} else {
						msgTV.setText("Couldn't retrieve photos at the moment\n Please try again later!");
					}
				}
			}
		}

	}

	public void showProgressBar() {
		progressBar.setVisibility(View.VISIBLE);
	}

	public void hideProgressBar() {
		progressBar.setVisibility(View.GONE);
	}

	class FetchPhotos extends AsyncTask<FlickrBundle, DownloadedPhoto, Void> {

		@Override
		protected Void doInBackground(FlickrBundle... params) {
			FlickrBundle bundle = params[0];
			PhotoPack pack = bundle.getPhotos();
			ArrayList<Photo> photoList = pack.getPhoto();
			int noOfPhotos = pack.getPerpage();
			for (int i = 0; i < noOfPhotos; i++) {
				String photoName = null;
				Photo toDownload = photoList.get(i);
				/*File file=new File(sdCard.getAbsolutePath()+"/"+date+"/image_"+i);
				if(file.exists()){
					publishProgress(new DownloadedPhoto(toDownload, "image_"+i));
					return null;
				}*/
				try {
					NetworkUtils networkUtils = new NetworkUtils(
							getApplicationContext());
					photoName = networkUtils.downloadPhoto(
							toDownload.generateUrl(),date, i);
				} catch (ClientProtocolException e) {
					Log.e("amaze", e.getMessage());
					e.printStackTrace();
				} catch (IOException e) {
					Log.e("amaze", "msg:" + e.getMessage());
					e.printStackTrace();
				}
				publishProgress(new DownloadedPhoto(toDownload, photoName));
			}
			return null;
		}

		@Override
		protected void onProgressUpdate(DownloadedPhoto... values) {
			// TODO Auto-generated method stub
			super.onProgressUpdate(values);
			photosDownloaded.add(values[0]);
			
			flickrPager.getAdapter().notifyDataSetChanged();
			Log.e("amaze", "Photo downloaded: " + values[0].getFilePath());
		}
	}

}
