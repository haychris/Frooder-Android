package liberum.cibum.frooder;

import java.util.List;

import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.model.LatLng;
import com.parse.FindCallback;
import com.parse.GetCallback;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseException;


import android.os.Bundle;
import android.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import liberum.cibum.frooder.dummy.DummyContent;
  
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

    /**
     * The dummy content this fragment is presenting.
     */
    private ParseObject mItem;
	MapView mapView;
	GoogleMap map; 
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
                   // object will be your game score
                	 mItem = object;
                	 ((TextView) getView().findViewById(R.id.foodlisting_detail)).setText(mItem.getString("foodType"));
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
        CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(new LatLng(40.4315248, -74.660736), 15);
        map.animateCamera(cameraUpdate); 
        // Show the dummy content as text in a TextView.
//        if (mItem != null) {
//            ((TextView) rootView.findViewById(R.id.foodlisting_detail)).setText(mItem.content);
//        }

        return rootView;
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
