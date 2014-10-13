package org.digitalcampus.oppia.activity;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.digitalcampus.mobile.learningGF.R;
import org.digitalcampus.oppia.application.DbHelper;
import org.grameenfoundation.adapters.CoverageListAdapter;
import org.grameenfoundation.adapters.EventBaseAdapter;
import org.grameenfoundation.adapters.EventsDetailPagerAdapter;
import org.grameenfoundation.adapters.MainScreenBaseAdapter;
import org.grameenfoundation.calendar.CalendarEvents;
import org.grameenfoundation.cch.activity.PDFActivity;
import org.grameenfoundation.cch.model.WebAppInterface;
import org.grameenfoundation.chnonthego.NewEventPlannerActivity.CoverageActivity;
import org.grameenfoundation.chnonthego.NewEventPlannerActivity.EventsActivity;
import org.grameenfoundation.chnonthego.NewEventPlannerActivity.LearningActivity;
import org.grameenfoundation.chnonthego.NewEventPlannerActivity.OtherActivity;
import org.grameenfoundation.chnonthego.NewEventPlannerActivity.SectionsPagerAdapter;
import org.grameenfoundation.database.CHNDatabaseHandler;
import org.grameenfoundation.poc.PointOfCareActivity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.text.format.Time;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class MainScreenActivity extends FragmentActivity implements OnItemClickListener {

	private ListView main_menu_listview;
	private static Context mContext;
	private static TextView status;


	SectionsPagerAdapter mSectionsPagerAdapter;
    ViewPager mViewPager;
	private DbHelper dbh;
	// MODULE IDs
		private static final String EVENT_PLANNER_ID      = "Event Planner";
		private static final String STAYING_WELL_ID       = "Staying Well";
		private static final String POINT_OF_CARE_ID      = "Point of Care";
		private static final String LEARNING_CENTER_ID    = "Learning Center";
		private static final String ACHIEVEMENT_CENTER_ID = "Achievement Center";
  

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
	    super.onCreate(savedInstanceState);
	    setContentView(R.layout.activity_main_screen);
	    mContext=MainScreenActivity.this;
	    getActionBar().setDisplayShowHomeEnabled(false);
	    getActionBar().setTitle("Welcome");
	   // TypefaceUtil.overrideFont(mContext, "SERIF", "fonts/Roboto-Thin.ttf");
	    main_menu_listview=(ListView) findViewById(R.id.listView_mainScreenMenu);
	    String[] categories={"Planner",
	    		"Point of Care",
	    		"Learning Center",
	    		"Achievements",
	    		"Staying Well"};
	    
	    int[] images={R.drawable.ic_calendar,
	    			  R.drawable.ic_point_of_care,
	    			  R.drawable.ic_learning_center,
	    			  R.drawable.ic_achievement,
	    			  R.drawable.ic_staying_well};
	    MainScreenBaseAdapter adapter=new MainScreenBaseAdapter(mContext,categories,images);
	    main_menu_listview.setAdapter(adapter);				
	    main_menu_listview.setOnItemClickListener(this);
		dbh = new DbHelper(getApplicationContext());

	    
	    mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.pager2);
        
        mViewPager.setAdapter(mSectionsPagerAdapter);
        mViewPager.setOffscreenPageLimit(2);
	  
	}
	public class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
                super(fm);
        }

        @Override
        public Fragment getItem(int position) {
                Fragment fragment = null;
                if(position==0 ){
                	 fragment= new EventsSummary();   
                }else if(position==1){
                	 fragment= new EventsDetails();   
                }
               	
                return fragment;
        }

        @Override
        public int getCount() {
                return 2;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return "OBJECT " + (position + 1);
        }
}
	
	 public class EventsSummary extends Fragment {
		 View rootView;
		// private SharedPreferences loginPref;
		 private String name;
		 //private ArrayList<String> eventsNumber;
		 private CHNDatabaseHandler db;
		private TextView event_number;
		int month;
		String month_text;
		 CalendarEvents c;
		 public ArrayList<String> EventTypeToday;
		private SharedPreferences prefs;
		 public EventsSummary(){
			 
		 }
		 
		 public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
			 	rootView=inflater.inflate(R.layout.events_pager_layout,null,false);
			 	prefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
			 	//loginPref=getApplicationContext().getSharedPreferences("loginPrefs", MODE_WORLD_READABLE);
			    name=prefs.getString(getString(R.string.prefs_username), "name");
			    db=new CHNDatabaseHandler(getActivity());
			    status=(TextView) rootView.findViewById(R.id.textView_status);
			    event_number=(TextView) rootView.findViewById(R.id.textView_eventsNumber);
			    Time time = new Time();
			    time.setToNow();
			    Calendar rightNow = Calendar.getInstance();
			    java.text.SimpleDateFormat df = new java.text.SimpleDateFormat("MMMM");
			   // month= rightNow.get(Calendar.MONTH)+1;
			   // month_text.equals("September");
			   // System.out.println(month_text);
			    c= new CalendarEvents(mContext);
			    EventTypeToday=c.getTodaysEventsType();
			    if(time.hour<12)
			    {
			    	System.out.println("name");
			    	  status.setText("Good morning, "+name+"!");
			    }else if(time.hour>12&& time.hour<17)
			    {
			    	 status.setText("Good afternoon, "+name+"!");
			    }else if(time.hour>17&& time.hour<20)
			    {
			    	 status.setText("Good evening, "+name+"!");
			    }else{
			    	 status.setText("Good day, "+name+"!");
			    }
			 //eventsNumber=db.getAllEventsForMonth("September");
			 if(EventTypeToday.size()==0){
				 event_number.setText("0"); 
			 }else {
				 event_number.setText(String.valueOf(EventTypeToday.size())); 
			 }
			 return rootView;
		 }
	 }
	 
	 public static class EventsDetails extends Fragment {
		 View rootView;
		 CalendarEvents c;
		 public ArrayList<String> EventTypeToday;
		 public ArrayList<String> EventTypeTime;
		 private CHNDatabaseHandler db;
		private TextView eventStatus;
		int month;
		String month_text;
		private ListView listView_details;
		 public EventsDetails(){
			 
		 }
		 
		 public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
			 	rootView=inflater.inflate(R.layout.events_detail_pager_layout,null,false);
			    db=new CHNDatabaseHandler(getActivity());
			    status=(TextView) rootView.findViewById(R.id.textView_status);
			    eventStatus=(TextView) rootView.findViewById(R.id.textView1);
			    listView_details=(ListView) rootView.findViewById(R.id.listView_eventsDetail);
			    Time time = new Time();
			    time.setToNow();
			    Calendar rightNow = Calendar.getInstance();
			    java.text.SimpleDateFormat df = new java.text.SimpleDateFormat("MMMM");
			   // month= rightNow.get(Calendar.MONTH)+1;
			    //month_text.equals(df.format(month));
			    c= new CalendarEvents(mContext);
			    EventTypeToday=c.getTodaysEventsType();
			    EventTypeTime=c.getTodaysEventsTime(false);
			 if(EventTypeToday.size()==0){
				 eventStatus.setText("No events planned for today!"); 
			 }else if(EventTypeToday.size()>0){
				 EventsDetailPagerAdapter adapter=new EventsDetailPagerAdapter(getActivity(),EventTypeToday,EventTypeTime);
			    	adapter.notifyDataSetChanged();
			    	listView_details.setAdapter(adapter);	 
			 }
			 return rootView;
		 }
	 }
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
	    MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.custom_action_bar, menu);
        return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		Intent intent;
		switch(position){
		case 0:
			intent=new Intent(mContext, EventPlannerOptionsActivity.class);
			startActivity(intent);
			break;
		case 1:
			intent=new Intent(mContext, PointOfCareActivity.class);
			startActivity(intent);
			break;
			
		case 2:
			intent = new Intent(getApplicationContext(), OppiaMobileActivity.class);
            startActivity(intent);	
			break;
		}
		
	}
	
	 

}
