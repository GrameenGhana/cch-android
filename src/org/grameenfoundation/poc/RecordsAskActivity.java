package org.grameenfoundation.poc;

import org.digitalcampus.mobile.learningGF.R;
import org.digitalcampus.oppia.application.DbHelper;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

public class RecordsAskActivity extends Activity {

	private Button button_next;
	private Context mContext;
	private DbHelper dbh;
	private Long start_time;
	private Long end_time;  

	@Override
	public void onCreate(Bundle savedInstanceState) {
	    super.onCreate(savedInstanceState);
	    setContentView(R.layout.activity_records_ask);
	    mContext=RecordsAskActivity.this;
	    dbh=new DbHelper(mContext);
	    start_time=System.currentTimeMillis();
	    getActionBar().setTitle("Point of Care");
	    getActionBar().setSubtitle("PNC Diagnostic: Records & History");
	  	button_next=(Button) findViewById(R.id.button_next);
	  	button_next.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				Intent intent=new Intent(mContext,RecordsCheckActivity.class);
				startActivity(intent);
				
			}
	  		
	  	});
	}
	public void onBackPressed()
	{
	    end_time=System.currentTimeMillis();
	    System.out.println("Start: " +start_time.toString()+"  "+"End: "+end_time.toString());
		dbh.insertCCHLog("Point of Care", "PNC Diagnostic: Records & History", start_time.toString(), end_time.toString());
		finish();
	}
}
