package org.grameenfoundation.poc;

import org.digitalcampus.mobile.learningGF.R;
import org.digitalcampus.oppia.application.DbHelper;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

public class ExamineThePatientActivity extends Activity {

	private ListView listView_ask;
	private ListView listView_look;
	private ListView listView_check;
	private Context mContext;
	private Button button_next;
	private DbHelper dbh;
	private Long start_time;
	private Long end_time;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
	    super.onCreate(savedInstanceState);
	    setContentView(R.layout.activity_examine_patient);
	    mContext=ExamineThePatientActivity.this;
	    dbh=new DbHelper(mContext);
	    start_time=System.currentTimeMillis();
	    getActionBar().setTitle("Point of Care");
	    getActionBar().setSubtitle("ANC Diagnostic: Danger Signs");
	    listView_ask=(ListView) findViewById(R.id.listView_ask);
	    listView_look=(ListView) findViewById(R.id.listView_look);
	    listView_check=(ListView) findViewById(R.id.listView_check);
	    String[] ask_items={"Severe headache","Severe Abdominal Pains","Excessive vomiting",
	    					"Blurred vision","Bleeding","Offensive/discoloured vaginal discharge",
	    					"Fever","Difficulty breathing","Epigastric pain","Foetal movements"};
	    String[] look_items={"Examine conjunctiva, tongue, palms, and nail beds for palor",
	    					"Oedaema of the feet, hands face, ankles","Bleeding",
	    					"Jaundice","Signs of shock","Offensive vaginal discharge"};
	    String[] check_items={"Blood pressure, if possible","Temperature","Pulse"};
	    
	    ListAdapter adapter1=new ListAdapter(mContext,ask_items);
	    ListAdapter adapter2=new ListAdapter(mContext,look_items);
	    ListAdapter adapter3=new ListAdapter(mContext,check_items);
	    listView_ask.setAdapter(adapter1);
	    /*
	    listView_ask.setOnTouchListener(new ListView.OnTouchListener() {
	        @Override
	        public boolean onTouch(View v, MotionEvent event) {
	            int action = event.getAction();
	            switch (action) {
	            case MotionEvent.ACTION_DOWN:
	                // Disallow ScrollView to intercept touch events.
	                v.getParent().requestDisallowInterceptTouchEvent(true);
	                break;

	            case MotionEvent.ACTION_UP:
	                // Allow ScrollView to intercept touch events.
	                v.getParent().requestDisallowInterceptTouchEvent(false);
	                break;
	            }

	            // Handle ListView touch events.
	            v.onTouchEvent(event);
	            return true;
	        }
	    });
	    */
	    listView_look.setAdapter(adapter2);
	    /*
	    listView_look.setOnTouchListener(new ListView.OnTouchListener() {
	        @Override
	        public boolean onTouch(View v, MotionEvent event) {
	            int action = event.getAction();
	            switch (action) {
	            case MotionEvent.ACTION_DOWN:
	                // Disallow ScrollView to intercept touch events.
	                v.getParent().requestDisallowInterceptTouchEvent(true);
	                break;

	            case MotionEvent.ACTION_UP:
	                // Allow ScrollView to intercept touch events.
	                v.getParent().requestDisallowInterceptTouchEvent(false);
	                break;
	            }

	            // Handle ListView touch events.
	            v.onTouchEvent(event);
	            return true;
	        }
	    });
	    */
	    listView_check.setAdapter(adapter3);
	    /*
	    listView_check.setOnTouchListener(new ListView.OnTouchListener() {
	        @Override
	        public boolean onTouch(View v, MotionEvent event) {
	            int action = event.getAction();
	            switch (action) {
	            case MotionEvent.ACTION_DOWN:
	                // Disallow ScrollView to intercept touch events.
	                v.getParent().requestDisallowInterceptTouchEvent(true);
	                break;

	            case MotionEvent.ACTION_UP:
	                // Allow ScrollView to intercept touch events.
	                v.getParent().requestDisallowInterceptTouchEvent(false);
	                break;
	            }

	            // Handle ListView touch events.
	            v.onTouchEvent(event);
	            return true;
	        }
	    });
	    */
	    button_next=(Button) findViewById(R.id.button_next);
	    button_next.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
			Intent intent=new Intent(mContext,AskHerActivity.class);
			startActivity(intent);
				
			}
	    	
	    });
	}

	class ListAdapter extends BaseAdapter{
		Context mContext;
		String[] items;
		 public LayoutInflater minflater;
		public ListAdapter(Context mContext,String[] items){
			this.mContext=mContext;
			this.items=items;
			 minflater = LayoutInflater.from(mContext);
		}
		@Override
		public int getCount() {
			return items.length;
		}

		@Override
		public Object getItem(int position) {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public long getItemId(int position) {
			// TODO Auto-generated method stub
			return 0;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			if( convertView == null ){
			      
				  convertView = minflater.inflate(R.layout.listview_single,parent, false);
			    }
			 TextView text=(TextView) convertView.findViewById(R.id.textView_textSingle);
			 text.setText(items[position]);
			 if(position%2==0){
				 convertView.setBackgroundColor(getResources().getColor(R.color.BackgroundGrey));
			 }else{
				 convertView.setBackgroundColor(getResources().getColor(R.color.White));
			 }
			    return convertView;
		}
		
	}
	public void onBackPressed()
	{
	    end_time=System.currentTimeMillis();
	    System.out.println("Start: " +start_time.toString()+"  "+"End: "+end_time.toString());
		dbh.insertCCHLog("Point of Care", "ANC Diagnostic Managing Danger Signs", start_time.toString(), end_time.toString());
		finish();
	}
}
