package com.probojnik.googlemapmarker;

import java.sql.Array;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class MarkerBeanAdapter extends BaseAdapter{
	private MainActivity ctx;
	private List<MarkerBean> markers;
	private LayoutInflater mInflater;
	private MarkerBean markerBean;
	
	public MarkerBeanAdapter(MainActivity ctx, List<MarkerBean> markers) {
		this.ctx = ctx;
    	this.markers = markers;
    	mInflater = (LayoutInflater)ctx.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}

	@Override
	public int getCount() {
		return markers.size();
	}

	@Override
	public MarkerBean getItem(int position) {
		return markers.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		markerBean = getItem(position);
		View view = mInflater.inflate(android.R.layout.simple_list_item_1, parent, false);
		view.setTag(markerBean.getMarker().getId());
		
		TextView textView = (TextView) view.findViewById(android.R.id.text1);
		
		textView.setText( markerBean.getMarker().getTitle() + " " + markerBean.getMarker().getSnippet() );
		
		return view;
	}

}
