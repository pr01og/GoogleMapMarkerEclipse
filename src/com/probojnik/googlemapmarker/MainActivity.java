package com.probojnik.googlemapmarker;

import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.maps.*;
import com.google.android.gms.maps.GoogleMap.OnInfoWindowClickListener;
import com.google.android.gms.maps.model.*;

import android.app.SearchManager;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.view.ActionMode;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends ActionBarActivity implements LocationListener {
    GoogleMap map;
    List<MarkerBean> markers;
    ActionBar ab;
    ActionMode am;
    BaseAdapter adapter;
    FragmentManager fragmentManager;
    DBHelper dbHelper;
    
    private DrawerLayout mDrawerLayout;
    private ActionBarDrawerToggle mDrawerToggle;
    private ListView mDrawerList;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        dbHelper = new DBHelper(this);
        
        DisplayMetrics metrics  = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);
        
        ab = getSupportActionBar();
        ab.setDisplayHomeAsUpEnabled(true);
        ab.setHomeButtonEnabled(true);
        
        markers = new ArrayList<MarkerBean>();
        adapter = new MarkerBeanAdapter(this, markers);
        		
        
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mDrawerList = (ListView) findViewById(R.id.left_drawer);     
        mDrawerList.getLayoutParams().width = metrics.widthPixels * 2 / 3;
        mDrawerList.setAdapter( adapter );
        mDrawerList.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
			    MarkerBean mb = (MarkerBean) mDrawerList.getItemAtPosition(position);
			    goToMarker(mb.getMarker());
			}
		});
        
        mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout, R.drawable.ic_drawer, R.string.drawer_open, R.string.drawer_close){
            public void onDrawerClosed(View view) {
            	ab.setTitle(getTitle());
            	supportInvalidateOptionsMenu();
            }

            public void onDrawerOpened(View drawerView) {
            	ab.setTitle(getResources().getString(R.string.second_screen));
            	supportInvalidateOptionsMenu();
            }
        };
        mDrawerLayout.setDrawerListener(mDrawerToggle);
        
        int status = GooglePlayServicesUtil.isGooglePlayServicesAvailable(getBaseContext());
        if(status == ConnectionResult.SUCCESS){
	        fragmentManager = getSupportFragmentManager();  
	        SupportMapFragment mapFragment = (SupportMapFragment) fragmentManager.findFragmentById(R.id.map);  
	        map = mapFragment.getMap();
	        if(map != null){
	        	map.setMyLocationEnabled(true);
	            LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
	            Criteria criteria = new Criteria();
	            String provider = locationManager.getBestProvider(criteria, true);
	            Location location = locationManager.getLastKnownLocation(provider);
	            if (location != null)  onLocationChanged(location);
//	            locationManager.requestLocationUpdates(provider, 20000L,(float) 0, (android.location.LocationListener) this); API 8
	            map.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
	    			@Override
	    			public void onMapClick(LatLng point) {
	    				am = startSupportActionMode(new AMC(point));
	    			}
	    		});
	            map.setOnInfoWindowClickListener(new OnInfoWindowClickListener() {
	    			@Override
	    			public void onInfoWindowClick(Marker marker) {
	    	            showDialog(showDialogCreate(marker));
	    			}
	    		});
	        } else Log.e("log1", "map " + map);
        } else Log.e("log1", "status " + status);
        
    }
    
    @Override
    protected void onStart() {
    	super.onStart();
        revertState();
    }
    
    @Override
    protected void onStop() {
    	super.onStop();
    	retainState();
    }
    
    void retainState(){ // add Data to SQLite
    	SQLiteDatabase db = dbHelper.getWritableDatabase();  // query to the database on UI. this is very bad. I know
    	db.delete(DBHelper.DB_TABLE, null, null);
    	ContentValues cv = new ContentValues();
    	
    	Iterator<MarkerBean> iterator = markers.iterator();
		while (iterator.hasNext()) {
			MarkerBean marker = iterator.next();
			
			cv.put(DBHelper.Tables.LATITUDE.getTable(), marker.getMarker().getPosition().latitude);
            cv.put(DBHelper.Tables.LONGITUDE.getTable(), marker.getMarker().getPosition().longitude);
            cv.put(DBHelper.Tables.TITLE.getTable(), marker.getMarker().getTitle());
            cv.put(DBHelper.Tables.SNIPPET.getTable(), marker.getMarker().getSnippet());
            db.insert(DBHelper.DB_TABLE, null, cv);
		}

        dbHelper.close();
    }
    
    void insertDB(MarkerBean mb){
    	SQLiteDatabase db = dbHelper.getWritableDatabase();
    	ContentValues cv = new ContentValues();
    	cv.put(DBHelper.Tables.LATITUDE.getTable(), mb.getMarker().getPosition().latitude);
        cv.put(DBHelper.Tables.LONGITUDE.getTable(), mb.getMarker().getPosition().longitude);
        cv.put(DBHelper.Tables.TITLE.getTable(), mb.getMarker().getTitle());
        cv.put(DBHelper.Tables.SNIPPET.getTable(), mb.getMarker().getSnippet());
        cv.put(DBHelper.Tables.MID.getTable(), mb.getMarker().getId());
        db.insert(DBHelper.DB_TABLE, null, cv);
        dbHelper.close();
    }
    
    void updateDB(MarkerBean mb, String title, String snippet){
    	SQLiteDatabase db = dbHelper.getWritableDatabase();
    	ContentValues cv = new ContentValues();
        cv.put(DBHelper.Tables.TITLE.getTable(), title);
        cv.put(DBHelper.Tables.SNIPPET.getTable(), snippet);
    	db.update(DBHelper.DB_TABLE, cv, DBHelper.Tables.MID.getTable()+" = ?", new String[] {  mb.getMarker().getId() });
    	dbHelper.close();
    }
    
    void deleteDB(MarkerBean mb){
    	SQLiteDatabase db = dbHelper.getWritableDatabase();
    	db.delete(DBHelper.DB_TABLE, DBHelper.Tables.MID.getTable()+" = ?", new String[] {  mb.getMarker().getId() });
    	dbHelper.close();
    }
    
    void revertState(){ // get Data out of SQLite
    	map.clear();
    	markers.clear();
    	
    	SQLiteDatabase db = dbHelper.getWritableDatabase();
    	Cursor c = db.query(DBHelper.DB_TABLE, null, null, null, null, null, null);
    	
    	if (c.moveToFirst()) {
    		int latitude = c.getColumnIndex(DBHelper.Tables.LATITUDE.getTable());
    		int longitude = c.getColumnIndex(DBHelper.Tables.LONGITUDE.getTable());
    		int title = c.getColumnIndex(DBHelper.Tables.TITLE.getTable());
    		int snippet = c.getColumnIndex(DBHelper.Tables.SNIPPET.getTable());
    		
    		do {
				LatLng latLng = new LatLng(c.getDouble(latitude), c.getDouble(longitude));
				
				MarkerBean mb = new MarkerBean();
				mb.addMarker(map, latLng, c.getString(title), c.getString(snippet));
				markers.add(mb);
				
	        } while (c.moveToNext());

		}
    	dbHelper.close();
    }
    
    void goToMarker(Marker m){
        map.moveCamera( CameraUpdateFactory.newLatLngZoom(m.getPosition(), 16) );
        m.showInfoWindow();
        mDrawerLayout.closeDrawers();
    }
    
    class AMC implements ActionMode.Callback {
    	LatLng point;
    	
    	public AMC(LatLng point){
    		this.point = point;
    	}

	    public boolean onCreateActionMode(ActionMode mode, Menu menu) {
	      menu.add(0, 123, 0, getResources().getString(R.string.done));
	      return true;
	    }

	    public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
	      return false;
	    }

	    public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
	    	switch (item.getItemId()) {
			case 123:
				showDialog( showDialogAdd(point) );
				return true;
			default:
				return false;
			}
	    }

	    public void onDestroyActionMode(ActionMode mode) {
	      am = null;
	    }

	  }
    
    void dialogMethod(LatLng latLng, int index, byte b, String... markerData){
    	if(b == 1 && index < 0){ 											// add
    		MarkerBean mb = new MarkerBean();
    		mb.addMarker(map, latLng, markerData[0], markerData[1]);
    		markers.add(mb);
//    		insertDB(mb);
    	} else if(b == 2 && index > 0){ 									// update
    		MarkerBean mb = markers.get(index);
    		mb.updMarker(markerData[0], markerData[1]);
//    		updateDB(mb, markerData[0], markerData[1]);
    	} else if(b == 3 && index > 0) { 									// delete
    		MarkerBean mb = markers.get(index);
    		mb.delMarker();
    		markers.remove(index);
//    		deleteDB(mb);
    	}
    	adapter.notifyDataSetChanged();
    }
    
    int indexArray(Marker m){
    	for (int i = 0; i < markers.size(); i++){
    		MarkerBean mb =  markers.get(i);
    		if(m.getId().equals(markers.get(i).getMarker().getId())){
    			return i;
    		}
    	}
    	return -1;
    }
    
    Bundle showDialogAdd(LatLng latLng) {
    	Bundle args = new Bundle();
        args.putParcelable(MarkerBean.MB.LATLNG.v, latLng);
        return args;
    }
    
    Bundle showDialogCreate(Marker m) {
    	Bundle args = new Bundle();
    	
    	if(indexArray(m) < 0) showDialogAdd(m.getPosition());
    	MarkerBean mb = markers.get(indexArray(m));
    	
        args.putInt(MarkerBean.MB.INDEX.v, indexArray(m));
        args.putString(MarkerBean.MB.TITLE.v, mb.getMarker().getTitle());
        args.putString(MarkerBean.MB.SNIPPET.v, mb.getMarker().getSnippet());
        
        return args;
    }
    
    void showDialog(Bundle args) {
    	if(am != null) am.finish();
    	
        TitleFragment titleFragment = new TitleFragment();
        titleFragment.setArguments(args);
		titleFragment.show(fragmentManager, getResources().getString(R.string.title_fragment));    
	}
    
	@Override
	public void onLocationChanged(Location location) {
		map.clear();
        double latitude = location.getLatitude();
        double longitude = location.getLongitude();
        LatLng myPosition = new LatLng(latitude, longitude);
        
        map.moveCamera( CameraUpdateFactory.newLatLngZoom(myPosition, 16) );
	}
	
	@Override
	public boolean onPrepareOptionsMenu(Menu menu) {
        boolean drawerOpen = mDrawerLayout.isDrawerOpen(mDrawerList);
        return super.onPrepareOptionsMenu(menu);
    }
	
	@Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (mDrawerToggle.onOptionsItemSelected(item)) return true;
        else return super.onOptionsItemSelected(item);
    }
	
	@Override
	public void setTitle(CharSequence title) {
	    ab.setTitle(title);
	}
	
	@Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        mDrawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        mDrawerToggle.onConfigurationChanged(newConfig);
    }
    
}