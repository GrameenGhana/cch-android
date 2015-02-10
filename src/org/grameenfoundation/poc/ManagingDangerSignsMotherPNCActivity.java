package org.grameenfoundation.poc;

import org.digitalcampus.mobile.learningGF.R;
import org.digitalcampus.oppia.application.DbHelper;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

public class ManagingDangerSignsMotherPNCActivity extends BaseActivity {

	private Button button_next;
	private Long start_time;
	private Long end_time;
	private DbHelper dbh;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
	    super.onCreate(savedInstanceState);
	    mContext=ManagingDangerSignsMotherPNCActivity.this;
	    setContentView(R.layout.activity_management_danger_signs);
	    getActionBar().setTitle("Point of Care");
	    getActionBar().setSubtitle("PNC Mother Diagnostic: Managing Danger Signs");
	    dbh=new DbHelper(ManagingDangerSignsMotherPNCActivity.this);
	    start_time=System.currentTimeMillis();
	    button_next=(Button) findViewById(R.id.button_next);
	    button_next.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
			Intent intent=new Intent(ManagingDangerSignsMotherPNCActivity.this,ManagingDangerSignsMotherPNCNextActivity.class);
			startActivity(intent);	
			overridePendingTransition(R.anim.slide_in_right, R.anim.slide_in_right);
			}
	    	
	    });
	}
	public void onBackPressed()
	{
		 end_time=System.currentTimeMillis();
		dbh.insertCCHLog("Point of Care", "PNC Mother Diagnostic: Managing Danger Signs", start_time.toString(), end_time.toString());
		finish();
	}
}
