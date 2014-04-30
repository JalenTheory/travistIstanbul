package fi.metropolia.lbs.travist.exchange;

import java.io.BufferedReader;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StringWriter;
import java.net.URL;

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
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import android.content.Context;
import android.os.StrictMode;
import android.util.Log;

public class ExchangeFetchXML {

	public String[] getRates() throws ClientProtocolException, IOException, IllegalStateException, SAXException, ParserConfigurationException {
		String[] currency_rate = new String[10];
		String updated_date;
		String updated[] = new String[3];
		
		//This bypasses the policy that doesn't allow users to run network operations in main thread
		StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
	    StrictMode.setThreadPolicy(policy);
		
		try 
	    {

	        URL url = new URL("http://www.ecb.europa.eu/stats/eurofxref/eurofxref-daily.xml");
	        Log.d("LUETAA", "XML");

	        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
	        DocumentBuilder db = dbf.newDocumentBuilder();
	        Document doc = db.parse(new InputSource(url.openStream()));
	        doc.getDocumentElement().normalize();
	        NodeList nodeList = doc.getElementsByTagName("Cube");
	        Node dateNode = nodeList.item(1);
	        updated_date = nodeToString(dateNode);
	        updated_date = updated_date.substring(0, updated_date.indexOf('>'));
	        updated = updated_date.split("-", 3);
	        updated[0] = updated[0].substring(12);
	        updated[2] = updated[2].substring(0, updated[2].length() - 1);
	        updated_date = updated[2] + '.' + updated[1] + '.' + updated[0];

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

	        currency_rate[0] = updated_date;
	        for (int i = 1; i < 10; i++) {
	        	currency_rate[i] = nodeToString(node[i-1]);
	        	currency_rate[i] = currency_rate[i].substring(currency_rate[i].lastIndexOf("=") + 1 );
	        	currency_rate[i] = currency_rate[i].substring(1, currency_rate[i].length() - 3);
	        	
	        }		
	    } 
	    catch (Exception e) 
	    {
	        System.out.println("XML Parsing Exception = " + e);
	    }
		
		return currency_rate;
		
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
	
	public void saveXMLToFile(String[] exchange_rate, Context context) throws IOException {
		
		    FileOutputStream fos = context.openFileOutput("exchange_rates.txt", context.MODE_PRIVATE);
		    
		    fos.write("".getBytes());
		    
		    for (String string : exchange_rate) {  
		    	string = string + "\r\n";
			    fos.write(string.getBytes());
			}
		    fos.close();

	}
	
	public String[] readXMLFromFile (Context context) {
		String rates[] = new String[10];
		int i = 0;
		
		try {
		    BufferedReader inputReader = new BufferedReader(new InputStreamReader(
		    		context.openFileInput("exchange_rates.txt")));
		    String stringFromFile;		    
		    while ((stringFromFile = inputReader.readLine()) != null) {
		        rates[i] = stringFromFile;
		        i++;
		    }
		    inputReader.close();
		} catch (IOException e) {
		    e.printStackTrace();
		}	
		
		return rates;
	}
}