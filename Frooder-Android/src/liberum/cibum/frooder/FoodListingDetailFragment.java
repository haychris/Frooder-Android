package liberum.cibum.frooder;

import java.util.List;

import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.parse.FindCallback;
import com.parse.GetCallback;
import com.parse.ParseGeoPoint;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseException;


import android.location.Location;
import android.os.Bundle;
import android.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import liberum.cibum.frooder.dummy.DummyContent;
import liberum.cibum.frooder.dummy.FoodListingAdapter;
  
/**
 * A fragment representing a single Food Listing detail screen.
 * This fragment is either contained in a {@link FoodListingListActivity}
 * in two-pane mode (on tablets) or a {@link FoodListingDetailActivity}
 * on handsets.
 */
public class FoodListingDetailFragment extends Fragment {
    /**
     * The fragment argument representing the item ID that this fragment
     * represents.
     */
    public static final String ARG_ITEM_ID = "item_id";
    private static final int MAX_ZOOM_LEVEL = 20;

    /**
     * The dummy content this fragment is presenting.
     */
    private ParseObject mItem;
	MapView mapView;
	GoogleMap map; 
	boolean setFoodMarker = false;
	View cardView;
    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public FoodListingDetailFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments().containsKey(ARG_ITEM_ID)) {
//            // Load the dummy content specified by the fragment
//            // arguments. In a real-world scenario, use a Loader
//            // to load content from a content provider.
//            mItem = DummyContent.ITEM_MAP.get(getArguments().getString(ARG_ITEM_ID));
        	 ParseQuery<ParseObject> query = ParseQuery.getQuery("FoodListing");
             query.getInBackground(getArguments().getString(ARG_ITEM_ID), new GetCallback<ParseObject>() {
               public void done(ParseObject object, ParseException e) {
                 if (e == null) {
                	 mItem = object;
                	 //((TextView) getView().findViewById(R.id.foodlisting_detail)).setText(mItem.getString("foodType"));
                	 //View card = getView().findViewById(R.id.card_main_detail);
         	         ParseGeoPoint parseUserLocation = FrooderApplication.getInstance().getParseLocation();
         	         if (cardView != null) {
                		 Log.e("card detail", "filling card from onCreate()");
                		 FoodListingAdapter.fillCard(cardView, object, parseUserLocation);
                	 } else {
                		 Log.e("card detail", "did not fill card from onCreate()");
                	 }
                	 ParseGeoPoint foodLocation = mItem.getParseGeoPoint("foodLocation");
                	 if (map != null && !setFoodMarker) {
                		 setFoodMarker = true;
                		 map.addMarker(new MarkerOptions()
                		    .position(new LatLng(foodLocation.getLatitude(), foodLocation.getLongitude()))
                		    .title(mItem.getString("foodType")));
                		 
                		 
                		Location userLocation = FrooderApplication.getInstance().getLocation();
            	        LatLng userLatLng;
            	        if (userLocation == null) {
            	        	userLatLng = new LatLng(40, -74);
            	        	Log.e("updating map", "location null, using hardcoded default");
            	        } else {
            	        	userLatLng = new LatLng(userLocation.getLatitude(), userLocation.getLongitude());
            	        }
            	
      
        	   		 	LatLng medianLatLng = new LatLng((foodLocation.getLatitude() + userLatLng.latitude) / 2, 
        	   		 									 (foodLocation.getLongitude() + userLatLng.longitude) / 2);
        		        int zoomLevel = calculateZoomLevel(mapView.getWidth(), 
        		        				(int) (foodLocation.distanceInKilometersTo(parseUserLocation) * 1000.0));
        		        CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(medianLatLng, zoomLevel);
        		        map.animateCamera(cameraUpdate);
                	        
                	 }
                 } else {
                   // something went wrong
                 }
               }
             }); 
        }
       
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_foodlisting_detail, container, false);

        
        
        // Gets the MapView from the XML layout and creates it
        mapView = (MapView) container.findViewById(R.id.mapview);
        mapView.onCreate(savedInstanceState);
        // Gets to GoogleMap from the MapView and does initialization stuff
        map = mapView.getMap();
        map.getUiSettings().setMyLocationButtonEnabled(false);
        map.setMyLocationEnabled(true);
         
        MapsInitializer.initialize(this.getActivity());
        // Updates the location and zoom of the MapView
        Location userLocation = FrooderApplication.getInstance().getLocation();
        ParseGeoPoint parseUserLocation = FrooderApplication.getInstance().getParseLocation();
	    cardView = rootView.findViewById(R.id.card_main_detail);
		if (mItem != null) {
			Log.e("food detail", "Filling card from getView()");
			FoodListingAdapter.fillCard(cardView, mItem, parseUserLocation);
		} else {
			Log.e("food detail", "did not fill card in getView()");
		}
        LatLng userLatLng;
        if (userLocation == null) {
        	userLatLng = new LatLng(40, -74);
        	Log.e("updating map", "location null, using hardcoded default");
        } else {
        	userLatLng = new LatLng(userLocation.getLatitude(), userLocation.getLongitude());
        }
        if (mItem != null && !setFoodMarker) {
        	setFoodMarker = true;
   		 	ParseGeoPoint foodLocation = mItem.getParseGeoPoint("foodLocation");
   		 	map.addMarker(new MarkerOptions()
   		 		.position(new LatLng(foodLocation.getLatitude(), foodLocation.getLongitude()))
   		 		.title(mItem.getString("foodType")));
   	 	
   		 	LatLng medianLatLng = new LatLng((foodLocation.getLatitude() + userLatLng.latitude) / 2, 
   		 									 (foodLocation.getLongitude() + userLatLng.longitude) / 2);
	        int zoomLevel = calculateZoomLevel(mapView.getWidth(), 
	        				(int) (foodLocation.distanceInKilometersTo(parseUserLocation) * 1000.0));
	        CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(medianLatLng, zoomLevel);
	        map.animateCamera(cameraUpdate);
        } else {
        	CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(userLatLng, 15);
        	map.animateCamera(cameraUpdate);
        }
 

        

        return rootView;
    }
    private int calculateZoomLevel(int screenWidth, int distanceShown) {
    	//TODO: properly fix this
    	if (screenWidth == 0) return 15;
        double equatorLength = 40075004; // in meters
        double widthInPixels = screenWidth;
        double metersPerPixel = equatorLength / 256;
        int zoomLevel = 1;
        while ((metersPerPixel * widthInPixels) > distanceShown) {
            metersPerPixel /= 2;
            ++zoomLevel;
        }
        Log.e("map", "distance = " + distanceShown);
        Log.e("map", "screen width = " + screenWidth); 
        Log.e("map", "zoom level = "+zoomLevel);
        if (zoomLevel > MAX_ZOOM_LEVEL)
        	return MAX_ZOOM_LEVEL;
        return zoomLevel;
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
