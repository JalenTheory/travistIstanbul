package fi.metropolia.lbs.travist.exchange;

import java.io.IOException;
import java.util.ArrayList;

import javax.xml.parsers.ParserConfigurationException;

import org.apache.http.client.ClientProtocolException;
import org.xml.sax.SAXException;

import travist.pack.R;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.StrictMode;
import android.widget.ListView;
import android.widget.TextView;
import fi.metropolia.lbs.travist.CheckInternetConnectivity;
import fi.metropolia.lbs.travist.TravistIstanbulActivity;

public class ExchangeActivity extends Activity {
	ExchangeAdapter adapter;
	CheckInternetConnectivity checkInternet = new CheckInternetConnectivity();
	ExchangeFetchXML XMLOperations = new ExchangeFetchXML();
	String currency_rate[] = new String[10];
	TextView updated_text;
	
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.exchange_rates);
		//This bypasses the policy that doesn't allow users to run network operations in main thread
		StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
	    StrictMode.setThreadPolicy(policy);
	    
	    updated_text = (TextView) findViewById (R.id.currency_updated_text);
		
		if (checkInternet.isInternetAvailable(getBaseContext())) {
			try {
				currency_rate= XMLOperations.getRates();
				XMLOperations.saveXMLToFile(currency_rate, getBaseContext());
				ArrayList<ExchangeItem> items = new ArrayList<ExchangeItem>();
				
				updated_text.setText("Updated: " + currency_rate[0]);
				items.add(new ExchangeItem(R.drawable.currency_dollar_icon, "United States Dollar", currency_rate[1]));
				items.add(new ExchangeItem(R.drawable.currency_british_icon, "British Pound", currency_rate[2]));
				items.add(new ExchangeItem(R.drawable.currency_indian_icon, "Indian Rupee", currency_rate[3]));
				items.add(new ExchangeItem(R.drawable.currency_dollar_icon, "Australian Dollar", currency_rate[4]));
				items.add(new ExchangeItem(R.drawable.currency_dollar_icon, "Canadian Dollar", currency_rate[5]));
				items.add(new ExchangeItem(R.drawable.currency_swiss_icon, "Swiss Franc", currency_rate[6]));
				items.add(new ExchangeItem(R.drawable.currency_chinese_icon, "Chinese Yuan Renminbi", currency_rate[7]));
				items.add(new ExchangeItem(R.drawable.currency_malaysian_icon, "Malaysian Ringgit", currency_rate[8]));
				items.add(new ExchangeItem(R.drawable.currency_dollar_icon, "New Zealand Dollar", currency_rate[9]));
				
				adapter = new ExchangeAdapter(this, items);
				adapter.notifyDataSetChanged();
			} catch (ClientProtocolException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IllegalStateException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (SAXException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (ParserConfigurationException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		else {
			currency_rate = XMLOperations.readXMLFromFile(getBaseContext());
			if (currency_rate[0] == null && currency_rate[1] == null) { 
				//If there's nothing on the "database", it'll use these hardcoded rates from 28.04.2014
				updated_text.setText("Updated: 28.04.2014");
				
				ArrayList<ExchangeItem> items = new ArrayList<ExchangeItem>();
				
				items.add(new ExchangeItem(R.drawable.currency_dollar_icon, "United States Dollar", "1.3861"));
				items.add(new ExchangeItem(R.drawable.currency_british_icon, "British Pound", "0.82280"));
				items.add(new ExchangeItem(R.drawable.currency_indian_icon, "Indian Rupee", "84.0392"));
				items.add(new ExchangeItem(R.drawable.currency_dollar_icon, "Australian Dollar", "1.4934"));
				items.add(new ExchangeItem(R.drawable.currency_dollar_icon, "Canadian Dollar", "1.5280"));
				items.add(new ExchangeItem(R.drawable.currency_chinese_icon, "Chinese Yuan Renminbi", "8.6689"));
				items.add(new ExchangeItem(R.drawable.currency_malaysian_icon, "Malaysian Ringgit", "4.5303"));
				items.add(new ExchangeItem(R.drawable.currency_dollar_icon, "New Zealand Dollar", "1.6220"));
				
				adapter = new ExchangeAdapter(this, items);
				adapter.notifyDataSetChanged();
			}
			else {
				//If there's been an internet connection at some point during the exchange rates-lifetime, the
				//rates from that time will be read from a text-file in here
				ArrayList<ExchangeItem> items = new ArrayList<ExchangeItem>();
				
				updated_text.setText("Updated: " + currency_rate[0]);

				items.add(new ExchangeItem(R.drawable.currency_dollar_icon, "United States Dollar", currency_rate[1]));
				items.add(new ExchangeItem(R.drawable.currency_british_icon, "British Pound", currency_rate[2]));
				items.add(new ExchangeItem(R.drawable.currency_indian_icon, "Indian Rupee", currency_rate[3]));
				items.add(new ExchangeItem(R.drawable.currency_dollar_icon, "Australian Dollar", currency_rate[4]));
				items.add(new ExchangeItem(R.drawable.currency_dollar_icon, "Canadian Dollar", currency_rate[5]));
				items.add(new ExchangeItem(R.drawable.currency_chinese_icon, "Chinese Yuan Renminbi", currency_rate[7]));
				items.add(new ExchangeItem(R.drawable.currency_malaysian_icon, "Malaysian Ringgit", currency_rate[8]));
				items.add(new ExchangeItem(R.drawable.currency_dollar_icon, "New Zealand Dollar", currency_rate[9]));
				
				adapter = new ExchangeAdapter(this, items);
				adapter.notifyDataSetChanged();
			}
			
			
			
		}
		
		ListView listView = (ListView) findViewById (R.id.exchange_rates_list);
		listView.setAdapter(adapter);
		
	}
	
	public void onBackPressed() {  
	    //do whatever you want the 'Back' button to do  
	    //as an example the 'Back' button is set to start a new Activity named 'NewActivity'  
	    startActivity(new Intent(this, TravistIstanbulActivity.class));  

	    return;  
	}
}