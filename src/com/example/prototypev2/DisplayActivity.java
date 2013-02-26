package com.example.prototypev2;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.xmlpull.v1.XmlPullParser;

import com.example.domin.DisplayItem;

import android.os.Bundle;
import android.util.Xml;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemSelectedListener;
import android.app.Activity;
import android.app.ListActivity;
import android.content.Intent;

public class DisplayActivity extends ListActivity {
	private Button refreshBtn;
	
	private String latitude = "53.343903";
	private String longitude = "-6.254806";
	private String range ="1";
	
	private HttpClient httpclient;
	private HttpGet httpGet;
	private HttpResponse response;
	
	private List<DisplayItem> deals;
	
	private static final String[] sort = {"Name","Distance","Price"};
	private static final String[] category = {"Food","Drink","Meal"};
	
	private Spinner sortSpinner;
	private Spinner categorySpinner;
	
	private ArrayAdapter<String> sortAdapter;
	private ArrayAdapter<String> categoryAdapter;
	
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.deal_list);
		
		refreshBtn = (Button) findViewById(R.id.refreshBtn);
		sortSpinner = (Spinner) findViewById(R.id.sortSpinner);
		categorySpinner = (Spinner) findViewById(R.id.categorySpinner);
		
		sortAdapter = new ArrayAdapter<String>(this,android.R.layout.simple_spinner_item,sort);
		categoryAdapter = new ArrayAdapter<String>(this,android.R.layout.simple_spinner_item,category);
		
		sortAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		categoryAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		
		sortSpinner.setAdapter(sortAdapter);
		categorySpinner.setAdapter(categoryAdapter);
		
		sortSpinner.setOnItemSelectedListener(new SpinnerSelectedListener());
		categorySpinner.setOnItemSelectedListener(new SpinnerSelectedListener());
	
        refreshBtn.setOnClickListener(new OnClickListener() {
				public void onClick(View v) {
					getDealList();
				}
			}     		
        );
	}

	public void getDealList() {        
        httpclient = new DefaultHttpClient();
        
		String URI = "http://dealsensor.cloudfoundry.com/REST/getDealByRange2?latitude=";      
        URI+= latitude + "&longitude=" + longitude + "&range=" + range + "&unit=\'K\'";
        httpGet = new HttpGet(URI);        
        
        try {
            response = httpclient.execute(httpGet);
            if(response != null) {
                InputStream inputstream = response.getEntity().getContent();
                
                parseXML(inputstream);
                
                createList();
            } else {
            	
            }
        } catch (ClientProtocolException e) {
        } catch (IOException e) {
        } catch (Exception e) {
        }
	}
	
    private void parseXML(InputStream is) {
    	XmlPullParser parser = Xml.newPullParser();
    	
    	try {
            // auto-detect the encoding from the stream
            parser.setInput(is, null);
            int eventType = parser.getEventType();
            DisplayItem deal = null;            
            boolean done = false;
            
            while (eventType != XmlPullParser.END_DOCUMENT && !done) {
                String name = null;
                
                switch (eventType) {
                    case XmlPullParser.START_DOCUMENT:
                        deals = new ArrayList<DisplayItem>();
                        break;
                        
                    case XmlPullParser.START_TAG:
                        name = parser.getName();
                        
                        if (name.equalsIgnoreCase("deal")){
                            deal = new DisplayItem();
                            
                            deal.setId(Integer.parseInt(parser.getAttributeValue(0)));
                        } else if (deal != null){
                        	if (name.equalsIgnoreCase("title")){
                            	deal.setTitle(parser.nextText());
                            } else if (name.equalsIgnoreCase("location")) {
                            	for(int i = 0; i < parser.getAttributeCount(); i++) {
                            		if(parser.getAttributeName(i) == "longitude") {
                            			deal.setLongitude(parser.getAttributeValue(i));
                            		} else if(parser.getAttributeName(i) == "latitude") {
                            			deal.setLatitude(parser.getAttributeValue(i));
                            		} else if(parser.getAttributeName(i) == "range") {
                            			deal.setDistance(
                            					Integer.toString(((int) (Double.parseDouble(parser.getAttributeValue(i)) * 1000))));
                            		}
                            	}
//                            	deal.setLongitude(parser.getAttributeValue(1));
//                            	deal.setLatitude(parser.getAttributeValue(2));
                            }
                        }
                        break;
                        
                    case XmlPullParser.END_TAG:
                        name = parser.getName();
                        if (name.equalsIgnoreCase("deal") && deal != null){
                            deals.add(deal);
                        } else if (name.equalsIgnoreCase("deals")){
                            done = true;
                        }
                        break;
                }
                
                eventType = parser.next();
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
    
    public void createList() {
    	DealAdapter listAdapter = new DealAdapter(this);
        
        setListAdapter(listAdapter);
    }
    
    
    
	protected void onListItemClick(ListView l, View v, int position, long id) {
		super.onListItemClick(l, v, position, id);
		
//		Toast.makeText(getApplicationContext(), Integer.toString(position), Toast.LENGTH_SHORT).show();
		
		Intent showDetail = new Intent(DisplayActivity.this, DetailActivity.class);
		Bundle dealData = new Bundle();
		dealData.putSerializable("data", deals.get(position));
		
		showDetail.putExtras(dealData);
		startActivity(showDetail);   
	}

	private class DealAdapter extends ArrayAdapter {
    	Activity context;

		public DealAdapter(Activity context) {
			super(context, R.layout.row, deals);
			this.context = context;
		}

		public View getView(int position, View convertView, ViewGroup parent) {
			LayoutInflater inflater = context.getLayoutInflater();
			
			View row = inflater.inflate(R.layout.row, null);
			TextView title = (TextView) row.findViewById(R.id.rowTitle);
			ImageView icon = (ImageView) row.findViewById(R.id.icon);
			
			title.setText(deals.get(position).getTitle());
			icon.setImageResource(R.drawable.ic_launcher);
			return (row);
		}
    }
	
	private class SpinnerSelectedListener implements OnItemSelectedListener{

		public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2,
				long arg3) {
		}

		public void onNothingSelected(AdapterView<?> arg0) {
		}
	}

}

