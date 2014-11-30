package liberum.cibum.frooder;

import java.util.ArrayList;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.parse.ParseGeoPoint;
import com.parse.ParseObject;

import android.app.Activity;
import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.SeekBar;

public class AddFoodActivity extends Activity {

	MapView mapView;
	GoogleMap map;      
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_add_food);
		// Gets the MapView from the XML layout and creates it
        mapView = (MapView) findViewById(R.id.mapview_add);
        mapView.onCreate(savedInstanceState);
        // Gets to GoogleMap from the MapView and does initialization stuff
        map = mapView.getMap();
        map.getUiSettings().setMyLocationButtonEnabled(false);   
        map.setMyLocationEnabled(false);
         
        MapsInitializer.initialize(this);
        
        Location userLocation = FrooderApplication.getInstance().getLocation();
        LatLng userLatLng;
        if (userLocation != null)
        	userLatLng = new LatLng(userLocation.getLatitude(), userLocation.getLongitude());    
        else
        	userLatLng = new LatLng(40, -74); //TODO: change to best guess
        CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(userLatLng, 17);
        map.animateCamera(cameraUpdate);
        
       // Marker foodMarker = map.addMarker(new MarkerOptions().position(userLatLng).draggable(true));
  

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.settings, menu);
		return true;
	}
  
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
	public void saveFood(View view) {
		
		String food = ((EditText) findViewById(R.id.editText1)).getText().toString();
		((EditText) findViewById(R.id.editText1)).setText("");
		String where = ((EditText) findViewById(R.id.editText2)).getText().toString();
		((EditText) findViewById(R.id.editText2)).setText("");
		SeekBar quantity = ((SeekBar) findViewById(R.id.seekBar1));
		double max = quantity.getMax();
		double current = quantity.getProgress();
		int relativeQuantity = (int) (10.0 * current / max);
		
		ParseObject foodListing = new ParseObject("FoodListing");
		foodListing.put("foodType", food);
		foodListing.put("whereText", where);
		foodListing.put("relativeQuantity", relativeQuantity);
		ArrayList<String> channels = new ArrayList<String>();
		channels.add("Princeton");
		foodListing.put("relevantChannels", channels);
//		Location currentLocation = FrooderApplication.getInstance().getLocation();
//		if (currentLocation != null) {
//			ParseGeoPoint parseLocation = new ParseGeoPoint(currentLocation.getLatitude(), currentLocation.getLongitude());
//			foodListing.put("foodLocation", parseLocation);
//		}
		LatLng mappedFoodLocation = map.getCameraPosition().target;
		ParseGeoPoint parseFoodLocation = new ParseGeoPoint(mappedFoodLocation.latitude, mappedFoodLocation.longitude);
		foodListing.put("foodLocation", parseFoodLocation);
		foodListing.saveInBackground();
	}
	
	@Override
	public void onResume() {
		mapView.onResume();
		super.onResume();
	}
	@Override
	public void onDestroy() {
		super.onDestroy();
		mapView.onDestroy();
	}
	@Override
	public void onLowMemory() {
		super.onLowMemory();
		mapView.onLowMemory();
	} 
}
	