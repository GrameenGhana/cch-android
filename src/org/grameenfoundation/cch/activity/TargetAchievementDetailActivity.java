package org.grameenfoundation.cch.activity;

import java.util.ArrayList;

import org.digitalcampus.mobile.learningGF.R;
import org.digitalcampus.oppia.application.DbHelper;
import org.grameenfoundation.adapters.AchievementDetailsAdapter;
import org.grameenfoundation.adapters.TargetsAchievementAdapter;
import org.grameenfoundation.calendar.CalendarEvents;
import org.grameenfoundation.cch.model.CoverageTargetAchievementActivity;
import org.grameenfoundation.cch.model.EventTargetAchievementActivity;
import org.grameenfoundation.cch.model.EventTargets;
import org.grameenfoundation.cch.model.LearningTargetAchievementActivity;
import org.grameenfoundation.cch.model.LearningTargets;
import org.grameenfoundation.cch.model.MyCalendarEvents;
import org.grameenfoundation.cch.model.OtherTargetAchievementActivity;
import org.grameenfoundation.cch.model.TargetsForAchievements;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ExpandableListView;
import android.widget.ListView;
import android.widget.TextView;

public class TargetAchievementDetailActivity extends Activity {

	private ListView expandableListview;
	private Context mContext;
	private CalendarEvents c;
	 private int eventTargets;
	 private int coverageTargets;
	 private int otherTargets;
	 private int learningTargets;
	private ListAdapter adapter;
	private DbHelper db;
	private TextView textView_label;
	private TextView textView_number;
	private int month;
	private int year;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
	    super.onCreate(savedInstanceState);
	    setContentView(R.layout.activity_achievements_details);
	    expandableListview = (ListView) findViewById(R.id.expandableListView1);
	    		Bundle extras =getIntent().getExtras(); 
	            if (extras != null) {
	              month= extras.getInt("month");
	              year=extras.getInt("year");
	            }
	    mContext=TargetAchievementDetailActivity.this;
	    c= new CalendarEvents(mContext);
	    db=new DbHelper(TargetAchievementDetailActivity.this);
	    String[] groupItems={"Events","Coverage","Learning","Other"};
	    getActionBar().setDisplayShowHomeEnabled(false);
	    getActionBar().setTitle("Achievement Center");
	    getActionBar().setSubtitle("Achievement Details");
	    textView_label=(TextView) findViewById(R.id.textView_label);
	    textView_label.setText("Targets");
	    textView_number=(TextView) findViewById(R.id.textView_number);
	    eventTargets=db.getAllEventTargetsForAchievements(month+1,year);
	    coverageTargets=db.getAllCoverageTargetsForAchievements(month+1,year);
	    learningTargets=db.getAllLearningTargetsForAchievements(month+1, year);
	    otherTargets=db.getAllOtherTargetsForAchievements(month+1, year);
	    textView_number.setText(" ("+String.valueOf(eventTargets+coverageTargets+learningTargets+otherTargets)+" this month)");
	    adapter=new ListAdapter(mContext,groupItems);
	    expandableListview.setAdapter(adapter);
	    expandableListview.setOnItemClickListener(new OnItemClickListener(){

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				Intent intent;
				switch(position){
				case 0:
					intent=new Intent(mContext,EventTargetAchievementActivity.class);
					intent.putExtra("month", month);
					intent.putExtra("year", year);
					intent.putExtra("number", eventTargets);
					startActivity(intent);
					break;
				case 1:
					intent=new Intent(mContext,CoverageTargetAchievementActivity.class);
					intent.putExtra("month", month);
					intent.putExtra("year", year);
					intent.putExtra("number", coverageTargets);
					startActivity(intent);
					break;
				case 2:
					intent=new Intent(mContext,LearningTargetAchievementActivity.class);
					intent.putExtra("month", month);
					intent.putExtra("year", year);
					intent.putExtra("number", learningTargets);
					startActivity(intent);
					break;
					
				case 3:
					intent=new Intent(mContext,OtherTargetAchievementActivity.class);
					intent.putExtra("month", month);
					intent.putExtra("year", year);
					intent.putExtra("number", otherTargets);
					startActivity(intent);
					break;
				}
			}
	    	
	    });
	}
	class ListAdapter extends BaseAdapter{
		Context mContext;
		String[] listItems;
		 public LayoutInflater minflater;
		
		public ListAdapter(Context mContext,String[] listItems){
		this.mContext=mContext;
		this.listItems=listItems;
		 minflater = LayoutInflater.from(mContext);
		}
		@Override
		public int getCount() {
			return listItems.length;
		}

		@Override
		public Object getItem(int position) {
			return null;
		}

		@Override
		public long getItemId(int position) {
			return 0;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			if( convertView == null ){
				  convertView = minflater.inflate(R.layout.other_listview_single,parent, false);
			    }
			 TextView text=(TextView) convertView.findViewById(R.id.textView_otherCategory);
			 text.setText(listItems[position]);
			    return convertView;
		}
		
	}
}