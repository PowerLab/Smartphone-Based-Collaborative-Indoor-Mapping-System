package show;

import java.net.HttpURLConnection;
import java.net.URL;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.webkit.WebView;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.iknowway.R;

public class Show extends Activity {

	/** Called when the activity is first created. */
	private Spinner m_layer; // 층을 나타내기 위한 스피너 (콤보박스)
	private ArrayAdapter<CharSequence> m_adapterForSpinner;
	private ImageView map;
	private WebView webView;
	
	int getlayer = -999;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.show);
		
		getlayerphp();
		while(getlayer == -999){
			Log.d("yooooooo","yeeeeeeeh");
		}
		
		webView = (WebView) findViewById(R.id.webview);
		m_layer = (Spinner) findViewById(R.id.spinner1);
		m_adapterForSpinner = new ArrayAdapter<CharSequence>(this,
				android.R.layout.simple_spinner_item);
		m_adapterForSpinner
				.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		m_layer.setAdapter(m_adapterForSpinner);
		
	    Toast.makeText(getApplicationContext(), ""+getlayer, Toast.LENGTH_SHORT).show();
		
		for(int i = 1 ; i<=getlayer;i++){
			m_adapterForSpinner.add(""+i);
		}

		m_layer.setOnItemSelectedListener(new OnItemSelectedListener() {
			public void onItemSelected(AdapterView<?> parent, View view,
					int position, long id) {

				//Toast.makeText(getApplicationContext(),	parent.getItemAtPosition(position).toString(),Toast.LENGTH_SHORT).show();

				webView.getSettings().setJavaScriptEnabled(true);
				webView.getSettings().setLoadWithOverviewMode(true);
				webView.getSettings().setUseWideViewPort(true);

				webView.setScrollBarStyle(WebView.SCROLLBARS_OUTSIDE_OVERLAY);
				webView.setScrollbarFadingEnabled(false);

				// zoom 허용
				// webView.getSettings().setBuiltInZoomControls(true);
				// webView.getSettings().setSupportZoom(true);
				int temp = position+1;
				Log.d("select layer ---> ",""+temp);
				
				webView.loadUrl("http://edu.argos.or.kr/~nongyack/project/canvas_android.php?layer="+temp);
				/*
				 * if(parent.getItemAtPosition(position).toString().equals("1"))
				 * map.setImageResource(R.drawable.map1); else if
				 * (parent.getItemAtPosition(position).toString().equals("4"))
				 * map.setImageResource(R.drawable.map4);
				 */
			}

			@Override
			public void onNothingSelected(AdapterView<?> parent) {
				// TODO Auto-generated method stub

			}
		});

		// TODO Auto-generated method stub
	}

	private void addNewApinnerItem() {
		CharSequence textHolder = "1층";
		m_adapterForSpinner.add(textHolder);

	}

	public void getlayerphp(){
		
		Thread thread = new Thread(new Runnable(){
			@Override
			public void run() {
				// TODO Auto-generated method stub
				try 
		        {
				    URL text = new URL("http://edu.argos.or.kr/~nongyack/project/asdf.php");
				    //HttpURLConnection conn = (HttpURLConnection)text.openConnection();
				    //conn.connect();
				    text.openStream();
				    
				    String layerstring = XmlParser.getXmlData("layer.xml", "result");
				    getlayer = Integer.parseInt(layerstring);
				    Log.d("layerstring ---> ", layerstring);
				    //readStream(conn.getInputStream());
		        }
		        catch (Exception e) {
		        	getlayer = -999;
		        }
			}
		});			   
	    
		
		thread.start();
		
		
		thread.interrupt();
	}
	
	
/*
	private void readStream(InputStream in) {
		BufferedReader reader = null;
		try {
			reader = new BufferedReader(new InputStreamReader(in));
			String line = "";
			int n = 1;
			while ((line = reader.readLine()) != null) {
										
				Toast.makeText(getApplicationContext(), line,
						Toast.LENGTH_SHORT).show();
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (reader != null) {
				try {
					reader.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}
	*/
}
