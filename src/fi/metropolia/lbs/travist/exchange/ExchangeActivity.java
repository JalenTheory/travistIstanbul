package fi.metropolia.lbs.travist.exchange;

import java.io.IOException;
import java.io.StringWriter;
import java.net.URL;
import java.util.ArrayList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.apache.http.client.ClientProtocolException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import fi.metropolia.lbs.travist.CheckInternetConnectivity;

import travist.pack.R;
import android.app.Activity;
import android.os.Bundle;
import android.os.StrictMode;
import android.util.Log;
import android.widget.ListView;
import android.widget.TextView;

public class ExchangeActivity extends Activity {
	ExchangeAdapter adapter;
	CheckInternetConnectivity checkInternet = new CheckInternetConnectivity();
	ExchangeFetchXML fetchXML = new ExchangeFetchXML();
	String currency_rate[] = new String[9];
	
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.exchange_rates);
		
		//This bypasses the policy that doesn't allow users to run network operations in main thread
		StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
	    StrictMode.setThreadPolicy(policy);
		
		if (checkInternet.isInternetAvailable()) {
			Log.d("Haetaa xml:stä uusimmat tiedot", "Jihuu");
			try {
				currency_rate= fetchXML.getRates();
				ArrayList<ExchangeItem> items = new ArrayList<ExchangeItem>();
				
				items.add(new ExchangeItem(R.drawable.currency_british_icon, "British Pound", currency_rate[1]));
				items.add(new ExchangeItem(R.drawable.currency_indian_icon, "Indian Rupee", currency_rate[2]));
				items.add(new ExchangeItem(R.drawable.currency_dollar_icon, "Australian Dollar", currency_rate[3]));
				items.add(new ExchangeItem(R.drawable.currency_dollar_icon, "Canadian Dollar", currency_rate[4]));
				items.add(new ExchangeItem(R.drawable.currency_swiss_icon, "Swiss Franc", currency_rate[5]));
				items.add(new ExchangeItem(R.drawable.currency_chinese_icon, "Chinese Yuan Renminbi", currency_rate[6]));
				items.add(new ExchangeItem(R.drawable.currency_malaysian_icon, "Malaysian Ringgit", currency_rate[7]));
				items.add(new ExchangeItem(R.drawable.currency_dollar_icon, "New Zealand Dollar", currency_rate[8]));
				
				adapter = new ExchangeAdapter(this, items);
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
			Log.d("Luetaa tekstitiedostosta", "Vanhaa paskaa");
			
		}
		
		//ListView listView = (ListView) findViewById (R.id.exchange_rates_list);
		//listView.setAdapter(adapter);
		
		
	}
}