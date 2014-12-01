package liberum.cibum.frooder;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.parse.FindCallback;
import com.parse.GetCallback;
import com.parse.Parse;
import com.parse.ParseException;
import com.parse.ParseGeoPoint;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import android.R.drawable;
import android.app.Activity;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.app.ListFragment;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;


import liberum.cibum.frooder.dummy.DummyContent;
import liberum.cibum.frooder.dummy.FoodListingAdapter;

/**
 * A list fragment representing a list of Food Listings. This fragment
 * also supports tablet devices by allowing list items to be given an
 * 'activated' state upon selection. This helps indicate which item is
 * currently being viewed in a {@link FoodListingDetailFragment}.
 * <p>
 * Activities containing this fragment MUST implement the {@link Callbacks}
 * interface.
 */
public class FoodListingListFragment extends ListFragment {
    public FoodListingAdapter mListAdapter;
	public ArrayList<ParseObject> foodItemList;
    public Map<String, ParseObject> foodItemMap;
	private static final int TWO_MINUTES = 1000 * 60 * 2;
	public Location currentLocation;


    /**
     * The serialization (saved instance state) Bundle key representing the
     * activated item position. Only used on tablets.
     */
    private static final String STATE_ACTIVATED_POSITION = "activated_position";

    /**
     * The fragment's current callback object, which is notified of list item
     * clicks.
     */
    private Callbacks mCallbacks = sDummyCallbacks;

    /**
     * The current activated item position. Only used on tablets.
     */
    private int mActivatedPosition = ListView.INVALID_POSITION;
    

    /**
     * A callback interface that all activities containing this fragment must
     * implement. This mechanism allows activities to be notified of item
     * selections.
     */
    public interface Callbacks {
        /**
         * Callback for when an item has been selected.
         */
        public void onItemSelected(String id);
    }

    /**
     * A dummy implementation of the {@link Callbacks} interface that does
     * nothing. Used only when this fragment is not attached to an activity.
     */
    private static Callbacks sDummyCallbacks = new Callbacks() {
        @Override
        public void onItemSelected(String id) {
        }
    };
    
    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public FoodListingListFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //Parse.initialize(getActivity(), "peOCyFSug2utyLbNmeoCmqXXL38hp2B1epY0UBOV", "H6gL0iFmKk7jCDT9danWB8zuMm5BpoPvkOvSNwkh");

     // Acquire a reference to the system Location Manager
        LocationManager locationManager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);
        currentLocation = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
        Location curBestLoc = FrooderApplication.getInstance().getLocation();
        if (curBestLoc == null)
        	FrooderApplication.getInstance().setLocation(currentLocation);
         
        
        
        
        foodItemList = new ArrayList<ParseObject>();
        foodItemMap = new HashMap<String, ParseObject>();


        
        ParseQuery<ParseObject> query = ParseQuery.getQuery("FoodListing");
        query.whereNotEqualTo("foodType", "poop");
        query.getFirstInBackground(new GetCallback<ParseObject>() {
        	  public void done(ParseObject object, ParseException e) {
        	    if (object == null) {
        	      Log.d("score", "The getFirst request failed.");
        	    } else {
        	    	if (!foodItemMap.containsKey(object.getObjectId())) {
	        	      foodItemList.add(object);
	        	      foodItemMap.put(object.getObjectId(), object);
	        	      mListAdapter.notifyDataSetChanged();
	        	      Log.d("score", "Retrieved the object.");
        	    	}
        	    }   
        	  }
        	});
        ParseQuery<ParseObject> queryPrinceton = ParseQuery.getQuery("FoodListing");
        queryPrinceton.whereExists("relevantChannels");
        queryPrinceton.findInBackground(new FindCallback<ParseObject>() {
            public void done(List<ParseObject> foodList, ParseException e) {
                if (e == null) {
                    Log.d("score", "Retrieved " + foodList.size() + " scores");
                    for (ParseObject po : foodList) {
                    	if (!foodItemMap.containsKey(po.getObjectId())) {
                    		foodItemList.add(po);
                  	      	foodItemMap.put(po.getObjectId(), po);
                    	}
                    }
                    sortByBestChance();
          	      	mListAdapter.notifyDataSetChanged();
                } else {
                    Log.d("score", "Error: " + e.getMessage());
                }
            }
        });
        
        ParseObject test = new ParseObject("FoodListing");
        test.put("foodType", "Pizza");
        test.put("relativeQuantity", 3);
        test.put("whereText", "Forbes A132");
        test.put("foodLocation", new ParseGeoPoint(40.3415388, -74.6607197));
        foodItemList.add(test);
        
        ParseObject test2 = new ParseObject("FoodListing");
        test2.put("foodType", "Chicken");
        test2.put("relativeQuantity", 7);
        test2.put("whereText", "Frist");
        test2.put("foodLocation", new ParseGeoPoint(40.346738, -74.655126));
        foodItemList.add(test2);
        
        ParseObject test3 = new ParseObject("FoodListing");
        test3.put("foodType", "Sandwiches");
        test3.put("relativeQuantity", 1);
        test3.put("whereText", "London");
        test3.put("foodLocation", new ParseGeoPoint(51.507351, -0.127758));
        foodItemList.add(test3);
        
        mListAdapter = new FoodListingAdapter(getActivity(), R.layout.food_card, foodItemList);
        if (currentLocation != null) 
        	mListAdapter.setUserLocation(currentLocation);
        setListAdapter(mListAdapter);  
        //sortByDateCreated();
        //mListAdapter.sort(comparator);
      
        
    }
    
    public void sortByNewestCreated() {
    	if (foodItemList.isEmpty())
    		Log.e("list", "list empty");
    	else Log.e("list", "" + foodItemList.size());
    	Collections.sort(foodItemList, new Comparator<ParseObject>(){
    	    public int compare(ParseObject o1, ParseObject o2) 
    	    {
    	       Date o1Creation = o1.getCreatedAt();
    	       Date o2Creation = o2.getCreatedAt();
    	       if (o1 != null && o1Creation == null)
    	    	   o1Creation = new Date(0);
    	       if (o2 != null && o2Creation == null)
    	    	   o2Creation = new Date(0);
    	       Log.e("date sort", "" + ((o1Creation != null) ? o1Creation.toString() : "null") + " and " + ((o2Creation != null) ? o2Creation.toString() : "null"));
    	       return o2Creation.compareTo(o1Creation);
    	    }
    	});
    	mListAdapter.notifyDataSetChanged();

    }
    public void sortByClosest() {
    	if (foodItemList.isEmpty())
    		Log.e("list", "list empty");
    	else Log.e("list", "" + foodItemList.size());
    	Collections.sort(foodItemList, new Comparator<ParseObject>(){
    	    public int compare(ParseObject o1, ParseObject o2) 
    	    {
    	    	//ParseGeoPoint userLocationParse = FrooderApplication.getInstance().getParseLocation();
    	    	Location userLocation = FrooderApplication.getInstance().getLocation();
    	    	ParseGeoPoint userLocationParse = new ParseGeoPoint(userLocation.getLatitude(), userLocation.getLongitude());
    	    	ParseGeoPoint foodLocation1 = o1.getParseGeoPoint("foodLocation");
    	    	ParseGeoPoint foodLocation2 = o2.getParseGeoPoint("foodLocation");
    			double foodDist1 = -1;
    			double foodDist2 = -1;
    			if (userLocationParse == null)
    				Log.e("location sort", "userLocation null");
    			if (foodLocation1 != null && userLocationParse != null) {
    				foodDist1 = foodLocation1.distanceInKilometersTo(userLocationParse);
    			}
    			if (foodLocation2 != null && userLocationParse != null) {
    				foodDist2 = foodLocation2.distanceInKilometersTo(userLocationParse);
    			}
    			Log.e("location sort", "" + foodDist1 + " and " + foodDist2);
    			return ((Double) foodDist1).compareTo((Double) foodDist2);
    	    }
    	});
    	mListAdapter.notifyDataSetChanged();

    }
    
    public void sortByBestChance() { 
    	if (foodItemList.isEmpty())
    		Log.e("list", "list empty");
    	else Log.e("list", "" + foodItemList.size());
    	Collections.sort(foodItemList, new Comparator<ParseObject>(){
    	    public int compare(ParseObject o1, ParseObject o2) 
    	    {
    	    	Location userLocation = FrooderApplication.getInstance().getLocation();
    	    	ParseGeoPoint userLocationParse = new ParseGeoPoint(userLocation.getLatitude(), userLocation.getLongitude());
    	    	ParseGeoPoint foodLocation1 = o1.getParseGeoPoint("foodLocation");
    	    	ParseGeoPoint foodLocation2 = o2.getParseGeoPoint("foodLocation");
    			double foodDist1 = -1;
    			double foodDist2 = -1;
    			if (userLocationParse == null)
    				Log.e("location sort", "userLocation null");
    			if (foodLocation1 != null && userLocationParse != null) {
    				foodDist1 = foodLocation1.distanceInKilometersTo(userLocationParse);
    			}
    			if (foodLocation2 != null && userLocationParse != null) {
    				foodDist2 = foodLocation2.distanceInKilometersTo(userLocationParse);
    			}
    			Date createDate1 = o1.getCreatedAt();
    			Date createDate2 = o2.getCreatedAt();
    			if (createDate1 == null)
    				createDate1 = new Date(0);
    			if (createDate2 == null)
    				createDate2 = new Date(0);
    			Long elapsedTime1 = ((System.currentTimeMillis() - createDate1.getTime()) / 1000);
    			Long elapsedTime2 = ((System.currentTimeMillis() - createDate2.getTime()) / 1000);
    			
    			
    			double percentChance1 = 9 * Math.log(((foodDist1 != -1) ? foodDist1 / FoodListingAdapter.MOVEMENT_SPEED + elapsedTime1 : elapsedTime1));
    	    	double percentChance2 = 9 * Math.log(((foodDist2 != -1) ? foodDist2 / FoodListingAdapter.MOVEMENT_SPEED + elapsedTime2 : elapsedTime2));

    	    	return ((Double) percentChance1).compareTo((Double) percentChance2);
    	    }
    	});
    	mListAdapter.notifyDataSetChanged();

    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        
        this.getListView().setDivider(getResources().getDrawable(android.R.color.transparent));
        this.getListView().setDividerHeight(15);
        this.getListView().setPadding(0, 15, 0, 0);
        // Restore the previously serialized activated item position.
        if (savedInstanceState != null
                && savedInstanceState.containsKey(STATE_ACTIVATED_POSITION)) {
            setActivatedPosition(savedInstanceState.getInt(STATE_ACTIVATED_POSITION));
        }
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        // Activities containing this fragment must implement its callbacks.
        if (!(activity instanceof Callbacks)) {
            throw new IllegalStateException("Activity must implement fragment's callbacks.");
        }

        mCallbacks = (Callbacks) activity;
    }

    @Override
    public void onDetach() {
        super.onDetach();

        // Reset the active callbacks interface to the dummy implementation.
        mCallbacks = sDummyCallbacks;
    }

    @Override
    public void onListItemClick(ListView listView, View view, int position, long id) {
        super.onListItemClick(listView, view, position, id);

        // Notify the active callbacks interface (the activity, if the
        // fragment is attached to one) that an item has been selected.
        
        //TODO: REPLACE
        //mCallbacks.onItemSelected(DummyContent.ITEMS.get(position).id);
        mCallbacks.onItemSelected(foodItemList.get(position).getObjectId());
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if (mActivatedPosition != ListView.INVALID_POSITION) {
            // Serialize and persist the activated item position.
            outState.putInt(STATE_ACTIVATED_POSITION, mActivatedPosition);
        }
    }

    /**
     * Turns on activate-on-click mode. When this mode is on, list items will be
     * given the 'activated' state when touched.
     */
    public void setActivateOnItemClick(boolean activateOnItemClick) {
        // When setting CHOICE_MODE_SINGLE, ListView will automatically
        // give items the 'activated' state when touched.
        getListView().setChoiceMode(activateOnItemClick
                ? ListView.CHOICE_MODE_SINGLE
                : ListView.CHOICE_MODE_NONE);
    }

    private void setActivatedPosition(int position) {
        if (position == ListView.INVALID_POSITION) {
            getListView().setItemChecked(mActivatedPosition, false);
        } else {
            getListView().setItemChecked(position, true);
        }

        mActivatedPosition = position;
    }
//    /** Determines whether one Location reading is better than the current Location fix
//	  * @param location  The new Location that you want to evaluate
//	  * @param currentBestLocation  The current Location fix, to which you want to compare the new one
//	  */
//	protected boolean isBetterLocation(Location location, Location currentBestLocation) {
//	    if (currentBestLocation == null) {
//	        // A new location is always better than no location
//	        return true;
//	    }
//
//	    // Check whether the new location fix is newer or older
//	    long timeDelta = location.getTime() - currentBestLocation.getTime();
//	    boolean isSignificantlyNewer = timeDelta > TWO_MINUTES;
//	    boolean isSignificantlyOlder = timeDelta < -TWO_MINUTES;
//	    boolean isNewer = timeDelta > 0;
//
//	    // If it's been more than two minutes since the current location, use the new location
//	    // because the user has likely moved
//	    if (isSignificantlyNewer) {
//	        return true;
//	    // If the new location is more than two minutes older, it must be worse
//	    } else if (isSignificantlyOlder) {
//	        return false;
//	    }
//
//	    // Check whether the new location fix is more or less accurate
//	    int accuracyDelta = (int) (location.getAccuracy() - currentBestLocation.getAccuracy());
//	    boolean isLessAccurate = accuracyDelta > 0;
//	    boolean isMoreAccurate = accuracyDelta < 0;
//	    boolean isSignificantlyLessAccurate = accuracyDelta > 200;
//
//	    // Check if the old and new location are from the same provider
//	    boolean isFromSameProvider = isSameProvider(location.getProvider(),
//	            currentBestLocation.getProvider());
//
//	    // Determine location quality using a combination of timeliness and accuracy
//	    if (isMoreAccurate) {
//	        return true;
//	    } else if (isNewer && !isLessAccurate) {
//	        return true;
//	    } else if (isNewer && !isSignificantlyLessAccurate && isFromSameProvider) {
//	        return true;
//	    }
//	    return false;
//	}
//
//	/** Checks whether two providers are the same */
//	private boolean isSameProvider(String provider1, String provider2) {
//	    if (provider1 == null) {
//	      return provider2 == null;
//	    }
//	    return provider1.equals(provider2);
//	}
}
