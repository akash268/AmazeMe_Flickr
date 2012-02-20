package i.amaze.u;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.DatePicker;

public class AmazeMe_FlickrActivity extends Activity {
    private DatePicker picker;

	/** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.amaze_me);
        picker = (DatePicker) findViewById(R.id.datePicker1);        
        findViewById(R.id.button1).setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				
				String date=picker.getYear()+"-"+String.format("%02d", picker.getMonth())+"-"+ String.format("%02d", picker.getDayOfMonth());
				Intent intent =new Intent(getApplicationContext(),ShowMe.class);
				intent.putExtra("date", date);
				startActivity(intent);
			}
		});        
    }
}