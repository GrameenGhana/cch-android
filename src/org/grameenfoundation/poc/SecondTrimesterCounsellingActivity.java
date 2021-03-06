package org.grameenfoundation.poc;

import org.digitalcampus.mobile.learningGF.R;
import org.grameenfoundation.cch.model.WebAppInterface;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

public class SecondTrimesterCounsellingActivity extends Activity {

	private WebView myWebView;
	private static final String URL = "file:///android_asset/www/cch/modules/poc/checktrimester2.html";
	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
	    super.onCreate(savedInstanceState);
	    setContentView(R.layout.activity_second_trimester_activity);
	    getActionBar().setTitle("Point of Care");
	    getActionBar().setSubtitle("ANC Counselling");
	    myWebView = (WebView) findViewById(R.id.webView_secondTrimesterCounselling);	    	 
		myWebView.getSettings().setJavaScriptEnabled(true);
		myWebView.addJavascriptInterface(new WebAppInterface(this), "Android");
		myWebView.setLayerType(View.LAYER_TYPE_SOFTWARE, null);	 
		myWebView.loadUrl(URL);
		myWebView.setWebViewClient(new WebViewClient(){
				
			@Override
			     public void onReceivedError(WebView view, int errorCod,String description, String failingUrl) {
		            Toast.makeText(view.getContext(), description , Toast.LENGTH_LONG).show();
		         }
			    
			     
	});
	}

}
