package org.grameenfoundation.poc;

import org.digitalcampus.mobile.learningGF.R;
import org.digitalcampus.oppia.application.DbHelper;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

public class NoSeriousConditionsActivity extends Activity {

	Context mContext;
	private ListView listView_noConditions;
	private Button button_no;
	private DbHelper dbh;
	private Long start_time;
	private Long end_time;
	@Override
	public void onCreate(Bundle savedInstanceState) {
	    super.onCreate(savedInstanceState);
	    setContentView(R.layout.activity_no_serious_conditions);
	    getActionBar().setTitle("Point of Care");
	    getActionBar().setSubtitle("PNC Diagnostic: Other Serious Conditions");
	    mContext=NoSeriousConditionsActivity.this;
	    dbh=new DbHelper(mContext);
	    start_time=System.currentTimeMillis();
	    listView_noConditions=(ListView) findViewById(R.id.listView_noConditions);
	    String[] items={"Asymmetrical limb movement, one limb does not move",
	    				"Firm swelling/bump on one or both sides of head",
	    				"Bruises, swelling on buttocks "};
	    NoConditionsListAdapter adapter=new NoConditionsListAdapter(mContext,items);
	    button_no=(Button) findViewById(R.id.button_no);
	    button_no.setOnClickListener(new OnClickListener(){

			private Intent intent;

			@Override
			public void onClick(View v) {
				intent=new Intent(mContext,NoInjuriesActivity.class);
				startActivity(intent);
				
			}
	    	
	    });
	    listView_noConditions.setAdapter(adapter);
	    listView_noConditions.setOnItemClickListener(new OnItemClickListener(){

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				Intent intent;
				switch(position){
				case 0:
					intent=new Intent(mContext,TakeActionNoConditionsActivity.class);
					intent.putExtra("category","asymmetrical");
					startActivity(intent);
					break;
				case 1:
					intent=new Intent(mContext,TakeActionNoConditionsActivity.class);
					intent.putExtra("category","firm swelling");
					startActivity(intent);
					break;
				case 2:
					intent=new Intent(mContext,TakeActionNoConditionsActivity.class);
					intent.putExtra("category","firm swelling");
					startActivity(intent);
					break;
				
				}
				
			}
	    	
	    });
	}
	class NoConditionsListAdapter extends BaseAdapter{
		Context mContext;
		String[] listItems;
		 public LayoutInflater minflater;
		
		public NoConditionsListAdapter(Context mContext,String[] listItems){
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
				  convertView = minflater.inflate(R.layout.listview_text_single,parent, false);
			    }
			 TextView text=(TextView) convertView.findViewById(R.id.textView_listViewText);
			 text.setText(listItems[position]);
			    return convertView;
		}
		
	}
	public void onBackPressed()
	{
	    end_time=System.currentTimeMillis();
	    System.out.println("Start: " +start_time.toString()+"  "+"End: "+end_time.toString());
		dbh.insertCCHLog("Point of Care", "PNC Diagnostic Other Serious Conditions", start_time.toString(), end_time.toString());
		finish();
	}
}
