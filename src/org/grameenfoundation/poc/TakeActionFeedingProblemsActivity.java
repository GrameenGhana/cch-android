package org.grameenfoundation.poc;

import org.digitalcampus.mobile.learningGF.R;
import org.digitalcampus.oppia.application.DbHelper;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;

public class TakeActionFeedingProblemsActivity extends BaseActivity {

	private String take_action_category;
	private Long start_time;
	private Long end_time;
	private DbHelper dbh;
	
	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
	    super.onCreate(savedInstanceState);
	    getActionBar().setTitle("Point of Care");
	    getActionBar().setSubtitle("PNC Diagnostic: Feeding Problems");
	    dbh=new DbHelper(TakeActionFeedingProblemsActivity.this);
	    start_time=System.currentTimeMillis();
	   // listView_takeAction=(ListView) findViewById(R.id.listView_takeAction);
	  //  textView_takeAction=(TextView) findViewById(R.id.textView_takeActionCategory);
	    Bundle extras = getIntent().getExtras(); 
        if (extras != null) {
          take_action_category= extras.getString("value");
        }
        if(take_action_category.equals("not_attached")){
        	   setContentView(R.layout.activity_poor_attachement);
        	   TextView click_here=(TextView) findViewById(R.id.textView_clickHere);
      		   click_here.setOnClickListener(new OnClickListener(){

      			@Override
      			public void onClick(View v) {
      				Intent intent=new Intent(TakeActionFeedingProblemsActivity.this,InfantFeedingNextActivity.class);
      				intent.putExtra("value", "breast_attachement");
      				startActivity(intent);
      				overridePendingTransition(R.anim.slide_in_right, R.anim.slide_in_right);
      			}
      			   
      		   });
      		 TextView click_here_too=(TextView) findViewById(R.id.textView_clickHereToo);
      		click_here_too.setOnClickListener(new OnClickListener(){

    			@Override
    			public void onClick(View v) {
    				Intent intent=new Intent(TakeActionFeedingProblemsActivity.this,ExpressBreastmilkActivity.class);
    				intent.putExtra("value", "breast_attachement");
    				startActivity(intent);
    				overridePendingTransition(R.anim.slide_in_right, R.anim.slide_in_right);
    			}
    			   
    		   });
      		
      		TextView click_here_three=(TextView) findViewById(R.id.textView_clickHereThree);
      		click_here_three.setOnClickListener(new OnClickListener(){

    			@Override
    			public void onClick(View v) {
    				Intent intent=new Intent(TakeActionFeedingProblemsActivity.this,HomeCareForInfantActivity.class);
    				intent.putExtra("value", "exclusive_breastfeeding");
    				startActivity(intent);
    				overridePendingTransition(R.anim.slide_in_right, R.anim.slide_in_right);
    			}
    			   
    		   });
        }else if(take_action_category.equals("not_suckling")){
     	   setContentView(R.layout.activity_poor_attachement);
     	  TextView click_here=(TextView) findViewById(R.id.textView_clickHere);
 		   click_here.setOnClickListener(new OnClickListener(){

 			@Override
 			public void onClick(View v) {
 				Intent intent=new Intent(TakeActionFeedingProblemsActivity.this,InfantFeedingNextActivity.class);
 				intent.putExtra("value", "breast_attachement");
 				startActivity(intent);
 				overridePendingTransition(R.anim.slide_in_right, R.anim.slide_in_right);
 			}
 			   
 		   });
 		 TextView click_here_too=(TextView) findViewById(R.id.textView_clickHereToo);
 		click_here_too.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				Intent intent=new Intent(TakeActionFeedingProblemsActivity.this,ExpressBreastmilkActivity.class);
				intent.putExtra("value", "breast_attachement");
				startActivity(intent);
				overridePendingTransition(R.anim.slide_in_right, R.anim.slide_in_right);
			}
			   
		   });
 		
 		TextView click_here_three=(TextView) findViewById(R.id.textView_clickHereThree);
 		click_here_three.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				Intent intent=new Intent(TakeActionFeedingProblemsActivity.this,HomeCareForInfantActivity.class);
				intent.putExtra("value", "exclusive_breastfeeding");
				startActivity(intent);
				overridePendingTransition(R.anim.slide_in_right, R.anim.slide_in_right);
			}
			   
		   });
     }else if(take_action_category.equals("breastfeeding")){
   	   setContentView(R.layout.activity_breastfeeding);
   	 TextView click_here=(TextView) findViewById(R.id.textView_clickHere);
	   click_here.setOnClickListener(new OnClickListener(){

		@Override
		public void onClick(View v) {
			Intent intent=new Intent(TakeActionFeedingProblemsActivity.this,ExclusiveBreastFeedingActivity.class);
			intent.putExtra("value", "breast_attachement");
			startActivity(intent);
			overridePendingTransition(R.anim.slide_in_right, R.anim.slide_in_right);
		}
		   
	   });
	 TextView click_here_too=(TextView) findViewById(R.id.textView_clickHereToo);
	click_here_too.setOnClickListener(new OnClickListener(){

		@Override
		public void onClick(View v) {
			Intent intent=new Intent(TakeActionFeedingProblemsActivity.this,InfantFeedingNextActivity.class);
			intent.putExtra("value", "feeding_frequency");
			startActivity(intent);
			overridePendingTransition(R.anim.slide_in_right, R.anim.slide_in_right);
		}
		   
	   });
	
	TextView click_here_three=(TextView) findViewById(R.id.textView_clickHereThree);
	click_here_three.setOnClickListener(new OnClickListener(){

		@Override
		public void onClick(View v) {
			Intent intent=new Intent(TakeActionFeedingProblemsActivity.this,HomeCareForInfantActivity.class);
			intent.putExtra("value", "exclusive_breastfeeding");
			startActivity(intent);
			overridePendingTransition(R.anim.slide_in_right, R.anim.slide_in_right);
		}
		   
	   });
     }else if(take_action_category.equals("receive_food")){
   	   setContentView(R.layout.activity_receive_other_foods);
    	 TextView click_here=(TextView) findViewById(R.id.textView_clickHere);
  	   click_here.setOnClickListener(new OnClickListener(){

  		@Override
  		public void onClick(View v) {
  			Intent intent=new Intent(TakeActionFeedingProblemsActivity.this,InfantFeedingNextActivity.class);
  			intent.putExtra("value", "feeding_frequency");
  			startActivity(intent);
  			overridePendingTransition(R.anim.slide_in_right, R.anim.slide_in_right);
  		}
  		   
  	   });
  	 TextView click_here_too=(TextView) findViewById(R.id.textView_clickHereToo);
  	click_here_too.setOnClickListener(new OnClickListener(){

  		@Override
  		public void onClick(View v) {
  			Intent intent=new Intent(TakeActionFeedingProblemsActivity.this,ExpressBreastmilkActivity.class);
  			intent.putExtra("value", "feeding_frequency");
  			startActivity(intent);
  			overridePendingTransition(R.anim.slide_in_right, R.anim.slide_in_right);
  		}
  		   
  	   });
  	
  	TextView click_here_three=(TextView) findViewById(R.id.textView_clickHereThree);
  	click_here_three.setOnClickListener(new OnClickListener(){

  		@Override
  		public void onClick(View v) {
  			Intent intent=new Intent(TakeActionFeedingProblemsActivity.this,HomeCareForInfantActivity.class);
  			intent.putExtra("value", "exclusive_breastfeeding");
  			startActivity(intent);
  			overridePendingTransition(R.anim.slide_in_right, R.anim.slide_in_right);
  		}
  		   
  	   });
     }else if(take_action_category.equals("not_breastfeeding")){
     	   setContentView(R.layout.activity_not_breastfeeding);
     	   
     	  TextView click_here=(TextView) findViewById(R.id.textView_clickHere);
     	   click_here.setOnClickListener(new OnClickListener(){

     		@Override
     		public void onClick(View v) {
     			Intent intent=new Intent(TakeActionFeedingProblemsActivity.this,ExpressBreastmilkActivity.class);
     			intent.putExtra("value", "feeding_frequency");
     			startActivity(intent);
     			overridePendingTransition(R.anim.slide_in_right, R.anim.slide_in_right);
     		}
     		   
     	   });
     	 TextView click_here_too=(TextView) findViewById(R.id.textView_clickHereToo);
     	click_here_too.setOnClickListener(new OnClickListener(){

     		@Override
     		public void onClick(View v) {
     			Intent intent=new Intent(TakeActionFeedingProblemsActivity.this,HomeCareForInfantActivity.class);
     			intent.putExtra("value", "feeding_frequency");
     			startActivity(intent);
     			overridePendingTransition(R.anim.slide_in_right, R.anim.slide_in_right);
     		}
     		   
     	   });
       }else if(take_action_category.equals("low_weight_for_age")){
     	   setContentView(R.layout.activity_low_birth_weight_for_age);
     	   	 TextView click_here=(TextView) findViewById(R.id.textView_clickHere);
     	  	   click_here.setOnClickListener(new OnClickListener(){

     	  		@Override
     	  		public void onClick(View v) {
     	  			Intent intent=new Intent(TakeActionFeedingProblemsActivity.this,InfantFeedingNextActivity.class);
     	  			intent.putExtra("value", "low_birth_weight");
     	  			startActivity(intent);
     	  			overridePendingTransition(R.anim.slide_in_right, R.anim.slide_in_right);
     	  		}
     	  		   
     	  	   });
     	  	 TextView click_here_too=(TextView) findViewById(R.id.textView_clickHereToo);
     	  	click_here_too.setOnClickListener(new OnClickListener(){

     	  		@Override
     	  		public void onClick(View v) {
     	  			Intent intent=new Intent(TakeActionFeedingProblemsActivity.this,ExpressBreastmilkActivity.class);
     	  			intent.putExtra("value", "feeding_frequency");
     	  			startActivity(intent);
     	  			overridePendingTransition(R.anim.slide_in_right, R.anim.slide_in_right);
     	  		}
     	  		   
     	  	   });
     	  	
     	  	TextView click_here_three=(TextView) findViewById(R.id.textView_clickHereThree);
     	  	click_here_three.setOnClickListener(new OnClickListener(){

     	  		@Override
     	  		public void onClick(View v) {
     	  			Intent intent=new Intent(TakeActionFeedingProblemsActivity.this,KangarooCareActivity.class);
     	  			startActivity(intent);
     	  			overridePendingTransition(R.anim.slide_in_right, R.anim.slide_in_right);
     	  		}
     	  		   
     	  	   });
     	  	TextView click_here_four=(TextView) findViewById(R.id.textView_clickHereFour);
     	  	click_here_four.setOnClickListener(new OnClickListener(){

     	  		@Override
     	  		public void onClick(View v) {
     	  			Intent intent=new Intent(TakeActionFeedingProblemsActivity.this,HomeCareForInfantActivity.class);
     	  			startActivity(intent);
     	  			overridePendingTransition(R.anim.slide_in_right, R.anim.slide_in_right);
     	  		}
     	  		   
     	  	   });
       }else if(take_action_category.equals("thrush")){
     	   setContentView(R.layout.activity_thrush);
     	   	 TextView click_here=(TextView) findViewById(R.id.textView_clickHere);
   	  	   click_here.setOnClickListener(new OnClickListener(){

   	  		@Override
   	  		public void onClick(View v) {
   	  			Intent intent=new Intent(TakeActionFeedingProblemsActivity.this,TreatingLocationInfectionActivity.class);
   	  			intent.putExtra("value", "low_birth_weight");
   	  			startActivity(intent);
   	  		overridePendingTransition(R.anim.slide_in_right, R.anim.slide_in_right);
   	  		}
   	  		   
   	  	   });
   	  	 TextView click_here_too=(TextView) findViewById(R.id.textView_clickHereToo);
   	  	click_here_too.setOnClickListener(new OnClickListener(){

   	  		@Override
   	  		public void onClick(View v) {
   	  			Intent intent=new Intent(TakeActionFeedingProblemsActivity.this,HomeCareForInfantActivity.class);
   	  			intent.putExtra("value", "feeding_frequency");
   	  			startActivity(intent);
   	  		overridePendingTransition(R.anim.slide_in_right, R.anim.slide_in_right);
   	  		}
   	  		   
   	  	   });
       }else if(take_action_category.equals("no_feeding_problems")){
     	   setContentView(R.layout.activity_no_feeding_problems);
     	  TextView click_here=(TextView) findViewById(R.id.textView_clickHere);
  	  	   click_here.setOnClickListener(new OnClickListener(){

  	  		@Override
  	  		public void onClick(View v) {
  	  			Intent intent=new Intent(TakeActionFeedingProblemsActivity.this,HomeCareForInfantActivity.class);
  	  			intent.putExtra("value", "low_birth_weight");
  	  			startActivity(intent);
  	  		overridePendingTransition(R.anim.slide_in_right, R.anim.slide_in_right);
  	  		}
  	  		   
  	  	   });
  	  	 TextView click_here_too=(TextView) findViewById(R.id.textView_clickHereToo);
  	  	click_here_too.setOnClickListener(new OnClickListener(){

  	  		@Override
  	  		public void onClick(View v) {
  	  			Intent intent=new Intent(TakeActionFeedingProblemsActivity.this,PostnatalCareCounsellingTopicsActivity.class);
  	  			intent.putExtra("value", "feeding_frequency");
  	  			startActivity(intent);
  	  		overridePendingTransition(R.anim.slide_in_right, R.anim.slide_in_right);
  	  		}
  	  		   
  	  	   });
       }
	
	
	}
	public void onBackPressed()
	{
		 end_time=System.currentTimeMillis();
		dbh.insertCCHLog("Point of Care", "PNC Diagnostic: Feeding Problems", start_time.toString(), end_time.toString());
		finish();
	}
}
