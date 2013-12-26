package com.probojnik.googlemapmarker;

import java.util.Map;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;

class MarkerAdapter extends BaseAdapter {
	Map<LatLng, Marker> mData;
	Marker mMarker;
	private LayoutInflater mInflater;
	MainActivity ctx;
	
    public MarkerAdapter(MainActivity ctx, Map<LatLng, Marker> markers) {
    	this.ctx = ctx;
    	this.mData = markers;
        mInflater = (LayoutInflater)ctx.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }
    
	@Override
	public int getCount() {
		return mData.size();
	}
	
    @Override
    public Marker getItem(int position) {
    	LatLng k = (LatLng) mData.keySet().toArray()[position];
    	Marker v = mData.get(k);
    	return v;
    }

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		mMarker = getItem(position);
		View view = mInflater.inflate(android.R.layout.simple_list_item_1, parent, false);
		TextView textView = (TextView) view.findViewById(android.R.id.text1);
		textView.setText( mMarker.getTitle() + " " + mMarker.getSnippet() );
		textView.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				ctx.goToMarker(mMarker);				
			}
		});
		return view;
	}

}