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

import travist.pack.R;
import android.app.Activity;
import android.os.Bundle;
import android.os.StrictMode;
import android.util.Log;
import android.widget.ListView;

public class ExchangeActivity extends Activity {
	ExchangeAdapter adapter;
	
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.exchange_rates);
		
		//This bypasses the policy that doesn't allow users to run network operations in main thread
		StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
	    StrictMode.setThreadPolicy(policy);
		
		try {
			adapter = new ExchangeAdapter(this, getRates());
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
		
		ListView listView = (ListView) findViewById (R.id.exchange_rates_list);
		listView.setAdapter(adapter);
		
		
	}
	
	private ArrayList<ExchangeItem> getRates() throws ClientProtocolException, IOException, IllegalStateException, SAXException, ParserConfigurationException {
		String[] currency_rate = null;
		try 
	    {

	        URL url = new URL("http://www.ecb.europa.eu/stats/eurofxref/eurofxref-daily.xml");

	        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
	        DocumentBuilder db = dbf.newDocumentBuilder();
	        Document doc = db.parse(new InputSource(url.openStream()));
	        doc.getDocumentElement().normalize();
	        NodeList nodeList = doc.getElementsByTagName("Cube");
	        Node[] node = new Node[9];
	        node[0] = nodeList.item(2);
	        node[1] = nodeList.item(7);
	        node[2] = nodeList.item(25);
	        node[3] = nodeList.item(18);
	        node[4] = nodeList.item(20);
	        node[5] = nodeList.item(13);
	        node[6] = nodeList.item(21);
	        node[7] = nodeList.item(28);
	        node[8] = nodeList.item(29);
	        //Node node = nodeList.item(2);
	        currency_rate = new String[9];
	        for (int i = 0; i < 9; i++) {
	        	currency_rate[i] = nodeToString(node[i]);
	        	currency_rate[i] = currency_rate[i].substring(currency_rate[i].lastIndexOf("=") + 1 );
	        	currency_rate[i] = currency_rate[i].substring(1, currency_rate[i].length() - 3);
	        }		
	    } 
	    catch (Exception e) 
	    {
	        System.out.println("XML Parsing Exception = " + e);
	    }
		ArrayList<ExchangeItem> items = new ArrayList<ExchangeItem>();
		items.add(new ExchangeItem(R.drawable.currency_dollar_icon, "US Dollar", currency_rate[0]));
		items.add(new ExchangeItem(R.drawable.currency_british_icon, "British Pound", currency_rate[1]));
		items.add(new ExchangeItem(R.drawable.currency_indian_icon, "Indian Rupee", currency_rate[2]));
		items.add(new ExchangeItem(R.drawable.currency_dollar_icon, "Australian Dollar", currency_rate[3]));
		items.add(new ExchangeItem(R.drawable.currency_dollar_icon, "Canadian Dollar", currency_rate[4]));
		items.add(new ExchangeItem(R.drawable.currency_swiss_icon, "Swiss Franc", currency_rate[5]));
		items.add(new ExchangeItem(R.drawable.currency_chinese_icon, "Chinese Yuan Renminbi", currency_rate[6]));
		items.add(new ExchangeItem(R.drawable.currency_malaysian_icon, "Malaysian Ringgit", currency_rate[7]));
		items.add(new ExchangeItem(R.drawable.currency_dollar_icon, "New Zealand Dollar", currency_rate[8]));
		return items;
	}
	
	private String nodeToString(Node node) {
		  StringWriter sw = new StringWriter();
		  try {
		    Transformer t = TransformerFactory.newInstance().newTransformer();
		    t.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
		    t.transform(new DOMSource(node), new StreamResult(sw));
		  }
		  catch (TransformerException te) {
		    System.out.println("nodeToString Transformer Exception");
		  }
		  return sw.toString();
		}
}
