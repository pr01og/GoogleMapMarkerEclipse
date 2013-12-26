package com.probojnik.googlemapmarker;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

public class MarkerBean {
	private Marker marker;

	public MarkerBean(){
	}
	
	public void addMarker(GoogleMap map, LatLng latLng, String title, String snippet){
		marker = map.addMarker(new MarkerOptions()
		.position(latLng)
		.title(title)
		.snippet(snippet)
		.icon( BitmapDescriptorFactory.fromResource(R.drawable.ic_launcher) )
		.alpha(0.7f)
        .rotation(90.0f) ); 
	}
	
	public void updMarker(String title, String snippet){
    	marker.setTitle(title);
    	marker.setSnippet(snippet);	
    	marker.hideInfoWindow();
    	marker.showInfoWindow();
	}
	
	public void delMarker(){
    	marker.remove();
	}
	
	public Marker getMarker() {
		return marker;
	}
	
}