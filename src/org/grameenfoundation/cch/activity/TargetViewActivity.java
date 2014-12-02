package org.grameenfoundation.cch.activity;


import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;

import org.digitalcampus.mobile.learningGF.R;
import org.digitalcampus.oppia.activity.CoverageTargetsDetailActivity.DatePickerFragment;
import org.digitalcampus.oppia.activity.CoverageTargetsDetailActivity.DatePickerFragment2;
import org.digitalcampus.oppia.application.DbHelper;
import org.digitalcampus.oppia.service.TrackerService;
import org.grameenfoundation.adapters.CoverageListAdapter;
import org.grameenfoundation.adapters.EventBaseAdapter;
import org.grameenfoundation.adapters.LearningBaseAdapter;
import org.grameenfoundation.adapters.OtherBaseAdapter;
import org.grameenfoundation.adapters.TargetsViewCursorAdapter;
import org.grameenfoundation.cch.activity.HomeActivity;

import com.actionbarsherlock.app.SherlockFragmentActivity;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.ContextMenu;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.RadioButton;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.ExpandableListView;
import android.widget.RadioGroup;
import android.widget.ExpandableListView.ExpandableListContextMenuInfo;
import android.widget.ExpandableListView.OnChildClickListener;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.view.ActionMode;
import android.widget.Toast;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.AdapterView.OnItemClickListener;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.ActionBar.Tab;
import com.actionbarsherlock.app.SherlockFragmentActivity;


public class TargetViewActivity extends SherlockFragmentActivity implements ActionBar.TabListener, OnSharedPreferenceChangeListener{
	 private DbHelper dbh;
	SectionsPagerAdapter mSectionsPagerAdapter;
	public static String current_month;
	
	private static final String EVENT_PLANNER_ID = "Event Planner";
	public static final String TAG = TargetViewActivity.class.getSimpleName();
	public static String month_passed = null;
    /**
     * The {@link ViewPager} that will host the section contents.
     */
    ViewPager mViewPager;
	private SharedPreferences prefs;
	private static DbHelper db;
	private long todayEventId;
	private long thisMonthEventId;
	private long thisWeekEventId;
	private long midYearEventId;
	private long thisQuarterEventId;
	private long thisYearEventId;
	private long todayCoverageId;
	private long thisWeekCoverageId;
	private long thisMonthCoverageId;
	private long midYearCoverageId;
	private long thisQuarterCoverageId;
	private long thisYearCoverageId;
	private long todayLearningId;
	private long thisWeekLearningId;
	private long thisMonthLearningId;
	private long midYearLearningId;
	private long thisQuarterLearningId;
	private long thisYearLearningId;
	private long todayOtherId;
	private long thisWeekOtherName;
	private long thisWeekOtherId;
	private long thisMonthOtherId;
	private long midYearOtherId;
	private long thisQuarterOtherId;
	private long thisYearOtherId;
	 int counter3;
	 int counter;
	 int counter2;
	 int counter4;
	@Override
	public void onCreate(Bundle savedInstanceState) {
	    super.onCreate(savedInstanceState);
	    setContentView(R.layout.activity_new_event_planner);
	    
	   // final PagerTabStrip pagerTabStrip = (PagerTabStrip) findViewById(R.id.pager_header);
       // pagerTabStrip.setDrawFullUnderline(true);
       // pagerTabStrip.setTabIndicatorColor(Color.rgb(83,171,32));
        dbh = new DbHelper(TargetViewActivity.this);
      
        final ActionBar actionBar =getSupportActionBar();
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
        actionBar.setTitle("Planner");
        actionBar.setSubtitle("Target Setting");
        actionBar.setDisplayHomeAsUpEnabled(true);
		actionBar.setHomeButtonEnabled(true);
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.pager);
        
        mViewPager.setAdapter(mSectionsPagerAdapter);
        mViewPager.setOffscreenPageLimit(3);
        // When swiping between different sections, select the corresponding
        // tab. We can also use ActionBar.Tab#select() to do this if we have
        // a reference to the Tab.
        
        mViewPager
        .setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
                @Override
                public void onPageSelected(int position) {
                        actionBar.setSelectedNavigationItem(position);
                }
        });

        // For each of the sections in the app, add a tab to the action bar.
        
        for (int i = 0; i < mSectionsPagerAdapter.getCount(); i++) {
                // Create a tab with text corresponding to the page title defined by
                // the adapter. Also specify this Activity object, which implements
                // the TabListener interface, as the callback (listener) for when
                // this tab is selected.
        	
                actionBar.addTab(actionBar.newTab()
                                .setText(mSectionsPagerAdapter.getPageTitle(i))
                                .setTabListener(this));
        }
       
       
        prefs = PreferenceManager.getDefaultSharedPreferences(this);
		prefs.registerOnSharedPreferenceChangeListener(this);
		PreferenceManager.setDefaultValues(this, R.xml.prefs, false);
		
		Intent service = new Intent(this, TrackerService.class);
		Bundle tb = new Bundle();
		tb.putBoolean("backgroundData", true);
		service.putExtras(tb);
		this.startService(service);
        // Create the adapter that will return a fragment for each of the four
        // primary sections of the app.
		
		 	
        
}
	 public class SectionsPagerAdapter extends FragmentPagerAdapter {

         public SectionsPagerAdapter(FragmentManager fm) {
                 super(fm);
         }

         @Override
         public Fragment getItem(int position) {
                 Fragment fragment = null;
                 if(position==0 ){
                        fragment= new EventsActivity();
                 }else if(position==1){
                	 fragment= new CoverageActivity();
                 }else if(position==2){
                	// fragment= new LearningActivity();
                	 fragment= new OtherActivity();
                 }
                 return fragment;
         }

         @Override
         public int getCount() {
                 return 3;
         }

         @Override
         public CharSequence getPageTitle(int position) {
                 Locale l = Locale.getDefault();
                 db=new DbHelper(TargetViewActivity.this);
     		    todayEventId=db.getEventIdCount("Daily");
     		    thisMonthEventId=db.getEventIdCount("Monthly");
     		    thisWeekEventId=db.getEventIdCount("Weekly");
     		    midYearEventId=db.getEventIdCount("Mid-year");
     		    thisQuarterEventId=db.getEventIdCount("Quarterly");
     		    thisYearEventId=db.getEventIdCount("Annually");
     		     int event_number1=(int)todayEventId;
     			 int event_number2=(int)thisMonthEventId;
     			 int event_number3=(int)thisWeekEventId;
     			 int event_number4=(int)midYearEventId;
     			 int event_number5=(int)thisQuarterEventId;
     			 int event_number6=(int)thisYearEventId;
     	
     			counter=event_number1+event_number2+event_number3+event_number4+event_number5+event_number6;
     			
     			todayCoverageId=db.getCoverageIdCount("Daily");
     			thisWeekCoverageId=db.getCoverageIdCount("Weekly");
     			thisMonthCoverageId=db.getCoverageIdCount("Monthly");
     			midYearCoverageId=db.getCoverageIdCount("Mid-year");
     			thisQuarterCoverageId=db.getCoverageIdCount("Quarterly");
     			thisYearCoverageId=db.getCoverageIdCount("Annually");
     			
     			     int coverage_number1=(int)todayCoverageId;
     				 int coverage_number2=(int)thisWeekCoverageId;
     				 int coverage_number3=(int)thisMonthCoverageId;
     				 int coverage_number4=(int)midYearCoverageId;
     				 int coverage_number5=(int)thisQuarterCoverageId;
     				 int coverage_number6=(int)thisYearCoverageId;
     				
     				counter2=coverage_number1+coverage_number2+coverage_number3+coverage_number4+coverage_number5+coverage_number6;
     			
     			todayLearningId=db.getLearningIdCount("Daily");
     			thisWeekLearningId=db.getLearningIdCount("Weekly");
     			thisMonthLearningId=db.getLearningIdCount("Monthly");
     			midYearLearningId=db.getLearningIdCount("Mid-year");
     			thisQuarterLearningId=db.getLearningIdCount("Quarterly");
     			thisYearLearningId=db.getLearningIdCount("Annually");
     			
     			 int learning_number1=(int)todayLearningId;
     			 int learning_number2=(int)thisWeekLearningId;
     			 int learning_number3=(int)thisMonthLearningId;
     			 int learning_number4=(int)midYearLearningId;
     			 int learning_number5=(int)thisQuarterLearningId;
     			 int learning_number6=(int)thisYearLearningId;
     			counter3=learning_number1+
     					learning_number2+
     					learning_number3+
     					learning_number4+
     					learning_number5+
     					learning_number6;
     			 todayOtherId=db.getOtherIdCount("Daily");
     			 thisWeekOtherId=db.getOtherIdCount("Weekly");
     			 thisMonthOtherId=db.getOtherIdCount("Monthly");
     			 midYearOtherId=db.getOtherIdCount("Mid-year");
     			 thisQuarterOtherId=db.getOtherIdCount("Quarterly");
     			 thisYearOtherId=db.getOtherIdCount("Annually");
     			 
     			 int other_number1=(int)todayOtherId;
     			 int other_number2=(int)thisWeekOtherId;
     			 int other_number3=(int)thisMonthOtherId;
     			 int other_number4=(int)midYearOtherId;
     			 int other_number5=(int)thisQuarterOtherId;
     			 int other_number6=(int)thisYearOtherId;
     		
     			counter4=other_number1+
     					other_number2+
     					other_number3+
     					other_number4+
     					other_number5+
     					other_number6;
                 switch (position) {
                         case 0:
                                 return "EVENTS"+" ("+String.valueOf(counter)+")";
                         case 1:
                                 return "COVERAGE"+" ("+String.valueOf(counter2)+")";
                         case 2: 
                        	 return "OTHER"+" ("+String.valueOf(counter4)+")";
                    	 	//	return "LEARNING"+" ("+String.valueOf(counter3)+")";
                         //case 3:
                        		//return "OTHER"+" ("+String.valueOf(counter4)+")";
                 
                 }
                 return null;
         }
 }
		
	 public static class EventsActivity extends Fragment implements OnChildClickListener{

			private Context mContext;															
			private ExpandableListView listView_events;
			private View rootView;
			
			 public EventsActivity(){

            }
			 public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
				 rootView=inflater.inflate(R.layout.activity_events,null,false);
			    mContext=getActivity().getApplicationContext();
			    //TypefaceUtil.overrideFont(mContext, "SERIF", "fonts/Roboto-Thin.ttf");
			    db=new DbHelper(getActivity());
			    listView_events=(ExpandableListView) rootView.findViewById(R.id.expandableListView1);
			  
			    new Handler().post(new Runnable() {
		            private TargetsViewCursorAdapter customAdapter;

					@Override
		            public void run() {
		                customAdapter = new TargetsViewCursorAdapter(db.getAllForEvents(), getActivity());
		                listView_events.setAdapter(customAdapter);
		            }
		        });	
			return rootView;
				   
			}
			 
		
			@Override
			public boolean onChildClick(ExpandableListView parent, View v,
					int groupPosition, int childPosition, final long id) {
					return true;
		}
			
	 }
	 public static class CoverageActivity extends Fragment implements OnChildClickListener{

			private Context mContext;
			private View rootView;
			private ExpandableListView listView_coverage;		
			
			 public CoverageActivity(){

         }
			 public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
				 rootView=inflater.inflate(R.layout.activity_coverage,null,false);
			    mContext=getActivity().getApplicationContext();
			    db=new DbHelper(getActivity());
			    listView_coverage=(ExpandableListView) rootView.findViewById(R.id.expandableListView1);
				  
			    new Handler().post(new Runnable() {
		            private TargetsViewCursorAdapter customAdapter;

					@Override
		            public void run() {
		                customAdapter = new TargetsViewCursorAdapter(db.getAllForCoverage(), getActivity());
		                listView_coverage.setAdapter(customAdapter);
		            }
		        });	
			   
			   
			return rootView;
				   
			}
			@Override
			public boolean onChildClick(ExpandableListView parent, View v,
					int groupPosition, int childPosition, final long id) {
				
				return true;
			}
			
	 }
	 
	 public static class LearningActivity extends Fragment implements OnChildClickListener{

			private Context mContext;
			private View rootView;															
			
			
			 
			 public LearningActivity(){

      }
			 public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
				rootView=inflater.inflate(R.layout.activity_learning,null,false);
			    mContext=getActivity().getApplicationContext();
			    db=new DbHelper(getActivity());
			    
	    	
			return rootView;
				   
			 }			
				@Override
				public boolean onChildClick(ExpandableListView parent, View v,
						int groupPosition, int childPosition, long id) {
					
					return true;
				}

		
	 }
	 
	 public static class OtherActivity extends Fragment implements OnChildClickListener{

			private Context mContext;
			private View rootView;
			private ExpandableListView listView_other;															
			
			 public OtherActivity(){

   }
			 public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
				 rootView=inflater.inflate(R.layout.activity_other,null,false);
			    mContext=getActivity().getApplicationContext();
			    db=new DbHelper(getActivity());
			    listView_other=(ExpandableListView) rootView.findViewById(R.id.expandableListView_other);
			    new Handler().post(new Runnable() {
		            private TargetsViewCursorAdapter customAdapter;

					@Override
		            public void run() {
		                customAdapter = new TargetsViewCursorAdapter(db.getAllForOther(), getActivity());
		                listView_other.setAdapter(customAdapter);
		            }
		        });	
			return rootView;
				   
			}
			@Override
			public boolean onChildClick(ExpandableListView parent, View v,
					int groupPosition, int childPosition, long id) {
				
				return true;
			}
				
			}
	 
	 @Override
		public boolean onKeyDown(int keyCode, KeyEvent event) {
			if ((keyCode == KeyEvent.KEYCODE_BACK)) {
				TargetViewActivity.this.finish();
				
			} 
			
		    return true; 
		}
	  public void saveToLog(Long starttime) 
		{
		  Long endtime = System.currentTimeMillis();  
		  dbh.insertCCHLog(EVENT_PLANNER_ID, "Target Setting", starttime.toString(), endtime.toString());	
		}
	 public void onDestroy(){
		 super.onDestroy();
		 Long starttime=System.currentTimeMillis();  
		 saveToLog(starttime); 
	 }


	public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
		Log.d(TAG, key + " changed");
		if(key.equalsIgnoreCase(getString(R.string.prefs_server))){
			Editor editor = sharedPreferences.edit();
			if(!sharedPreferences.getString(getString(R.string.prefs_server), "").endsWith("/")){
				String newServer = sharedPreferences.getString(getString(R.string.prefs_server), "").trim()+"/";
				editor.putString(getString(R.string.prefs_server), newServer);
		    	editor.commit();
			}
		}
		if(key.equalsIgnoreCase(getString(R.string.prefs_points)) || key.equalsIgnoreCase(getString(R.string.prefs_badges))){
			supportInvalidateOptionsMenu();
		}
	}

	@Override
	public void onTabSelected(Tab tab, FragmentTransaction ft) {
		  mViewPager.setCurrentItem(tab.getPosition());
		
	}

	@Override
	public void onTabUnselected(Tab tab, FragmentTransaction ft) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onTabReselected(Tab tab, FragmentTransaction ft) {
		// TODO Auto-generated method stub
		
	}

}