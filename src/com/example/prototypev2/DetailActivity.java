package com.example.prototypev2;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.util.EntityUtils;

import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Point;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import com.example.domin.DisplayItem;
import com.google.android.maps.GeoPoint;
import com.google.android.maps.ItemizedOverlay;
import com.google.android.maps.MapActivity;
import com.google.android.maps.MapController;
import com.google.android.maps.MapView;
import com.google.android.maps.Overlay;
import com.google.android.maps.OverlayItem;
import com.google.android.maps.Projection;

public class DetailActivity extends MapActivity {
	private DisplayItem deal;

	private MapView mapView;
	private MapController controller;
	
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.detial);
		
	    mapView = (MapView) findViewById(R.id.mapview);
	    mapView.setBuiltInZoomControls(true);
	    
	    controller = mapView.getController();
	    controller.setZoom(16);
	    
	    Intent handler = getIntent();
	    Bundle data = handler.getExtras();
	    
	    deal = (DisplayItem) data.getSerializable("data");
	    TextView tv = (TextView) findViewById(R.id.text);
	    tv.append(deal.getTitle());
//	    tv.append(Integer.toString((int)(Double.parseDouble(deal.getLatitude())*1000000)));
//	    tv.append(deal.getLatitude());
//	    tv.append(deal.getLongitude());
//	    tv.append(deal.getDistance());

	    drawRoute();
	}

	protected boolean isRouteDisplayed() {
		return false;
	}
	
	public void drawRoute(){		
		String url = "http://maps.google.com/maps/api/directions/xml?origin=53.343903,-6.254806" +
				"&destination=" + deal.getLatitude() + "," + deal.getLongitude() + "&sensor=false&mode=walking";
		
		HttpGet get = new HttpGet(url);
		String strResult = "";
		try {
			HttpParams httpParameters = new BasicHttpParams();
			HttpConnectionParams.setConnectionTimeout(httpParameters, 3000);
			HttpClient httpClient = new DefaultHttpClient(httpParameters); 
			
			HttpResponse httpResponse = null;
			httpResponse = httpClient.execute(get);
			
			if (httpResponse.getStatusLine().getStatusCode() == 200){
				strResult = EntityUtils.toString(httpResponse.getEntity());
			}
		} catch (Exception e) {
			return;
		}
		
		if (-1 == strResult.indexOf("<status>OK</status>")){
			Toast.makeText(this, "Error!", Toast.LENGTH_SHORT).show();
			this.finish();
			return;
		}
		
		int pos = strResult.indexOf("<overview_polyline>");
		pos = strResult.indexOf("<points>", pos + 1);
		int pos2 = strResult.indexOf("</points>", pos);
		strResult = strResult.substring(pos + 8, pos2);
		
		List<GeoPoint> points = decodePoly(strResult);
		
	    Drawable drawer = getResources().getDrawable(R.drawable.blue_pin);
		PinOverlay routeOverlay = new PinOverlay(drawer, points);
		List<Overlay> overlays = mapView.getOverlays();
		
		GeoPoint start = new GeoPoint(53343903, -6254806);
		
		GeoPoint end = new GeoPoint((int)(Double.parseDouble(deal.getLatitude())*1000000), (int)(Double.parseDouble(deal.getLongitude())*1000000));
		
		routeOverlay.addItem(start, "Start", "0");
		routeOverlay.addItem(end, "End", "0");
		
		overlays.add(routeOverlay);
		
		if (points.size() >= 2){
			controller.animateTo(points.get(0));
		}
		 
		mapView.invalidate();
	}
	
	public List<GeoPoint> decodePoly(String encoded) {

	    List<GeoPoint> poly = new ArrayList<GeoPoint>();
	    int index = 0, len = encoded.length();
	    int lat = 0, lng = 0;

	    while (index < len) {
	        int b, shift = 0, result = 0;
	        do {
	            b = encoded.charAt(index++) - 63;
	            result |= (b & 0x1f) << shift;
	            shift += 5;
	        } while (b >= 0x20);
	        int dlat = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
	        lat += dlat;

	        shift = 0;
	        result = 0;
	        do {
	            b = encoded.charAt(index++) - 63;
	            result |= (b & 0x1f) << shift;
	            shift += 5;
	        } while (b >= 0x20);
	        int dlng = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
	        lng += dlng;

	        GeoPoint p = new GeoPoint((int) (((double) lat / 1E5) * 1E6),
	             (int) (((double) lng / 1E5) * 1E6));
	        poly.add(p);
	    }

	    return poly;
	}
	
	private class PinOverlay extends ItemizedOverlay<OverlayItem> {
		private ArrayList<OverlayItem> overlayItemList;
		private Drawable marker;
		private List<GeoPoint> points;

		public PinOverlay(Drawable marker, List<GeoPoint> points) {
			super(boundCenterBottom(marker));
			this.overlayItemList = new ArrayList<OverlayItem>();
			this.marker = marker;
			this.points = points;
			
			populate();
		}

		public void addItem(GeoPoint p, String title, String snippet) {
			OverlayItem newItem = new OverlayItem(p, title, snippet);
			overlayItemList.add(newItem);
			populate();
		}

		protected OverlayItem createItem(int i) {
		    return overlayItemList.get(i);
		}

		public int size() {
		    return overlayItemList.size();
		}


		public void draw(Canvas canvas, MapView mapView, boolean shadow) {
		    super.draw(canvas, mapView, shadow);

		    
	        Paint paint = new Paint();   
	        paint.setColor(Color.BLUE);   
	        paint.setDither(true);   
	        paint.setStyle(Paint.Style.STROKE);   
	        paint.setStrokeJoin(Paint.Join.ROUND);   
	        paint.setStrokeCap(Paint.Cap.ROUND);   
	        paint.setStrokeWidth(5);   
	        Projection projection = mapView.getProjection();
	        Path path = new Path();
	        List<Point> pointss = new ArrayList<Point>();
	        
	        for(int i = 0; i < points.size(); i++) {
	       	 Point point = new Point();
	       	 projection.toPixels(points.get(i), point);
	       	 pointss.add(point);
	        }
	        
	        path.moveTo(pointss.get(0).x, pointss.get(0).y);  
	        for(int i = 1; i < points.size(); i++) {
	       	 path.lineTo(pointss.get(i).x, pointss.get(i).y);
	        }

	        canvas.drawPath(path, paint);
	        
		    boundCenterBottom(marker);
		}
	}

}
