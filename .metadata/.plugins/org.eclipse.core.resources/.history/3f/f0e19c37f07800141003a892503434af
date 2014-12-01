package liberum.cibum.frooder;

import com.google.android.gms.maps.GoogleMap;
import com.parse.Parse;
import com.parse.ParseException;
import com.parse.ParseGeoPoint;
import com.parse.ParseInstallation;
import com.parse.ParsePush;
import com.parse.PushService;
import com.parse.SaveCallback;

import android.app.Application;
import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;

public class FrooderApplication extends Application {
	private static final int TWO_MINUTES = 1000 * 60 * 2;
    private static FrooderApplication me;
    private GoogleMap theMap;
    private Location bestLocation;
    private ParseGeoPoint parseUserLocation;
     

    @Override
    public void onCreate() {        
        super.onCreate();
        me = this ;
        
        Parse.initialize(FrooderApplication.getInstance(), "peOCyFSug2utyLbNmeoCmqXXL38hp2B1epY0UBOV", "H6gL0iFmKk7jCDT9danWB8zuMm5BpoPvkOvSNwkh");
        // Also in this method, specify a default Activity to handle push notifications
        PushService.setDefaultPushCallback(FrooderApplication.getInstance(), FoodListingListActivity.class);
        

       // ParseAnalytics.trackAppOpened(getIntent());

        // If you would like all objects to be private by default, remove this line.
        ParsePush.subscribeInBackground("", new SaveCallback() {
        	  @Override
        	  public void done(ParseException e) {
        	    if (e == null) {
        	      Log.d("com.parse.push", "successfully subscribed to the broadcast channel.");
        	    } else {
        	      Log.e("com.parse.push", "failed to subscribe for push", e);
        	    }
        	  } 
        	}); 
        ParseInstallation.getCurrentInstallation().saveInBackground();

        
        
        
     // Acquire a reference to the system Location Manager
        LocationManager locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        bestLocation = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
        // Define a listener that responds to location updates
        LocationListener locationListener = new LocationListener() {
            public void onLocationChanged(Location location) {
              // Called when a new location is found by the network location provider.
              //makeUseOfNewLocation(location);
            	Log.e("location", "new user location update: " + location.getLatitude() + " " + location.getLongitude());
          	  	if (isBetterLocation(location, bestLocation)) {
          	  		bestLocation = location;
          	  		
	          	  	if (parseUserLocation == null) {
	        			parseUserLocation = new ParseGeoPoint(location.getLatitude(), 
	        												  location.getLongitude());
	        		} else {
	        			parseUserLocation.setLatitude(location.getLatitude());
	        			parseUserLocation.setLongitude(location.getLongitude());
	        		}
          	  	}
          	  	
            }

            public void onStatusChanged(String provider, int status, Bundle extras) {}

            public void onProviderEnabled(String provider) {}

            public void onProviderDisabled(String provider) {}
          };

        // Register the listener with the Location Manager to receive location updates
        locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, locationListener);
        

    }
    public static FrooderApplication getInstance() {
         return me;
    }
    
	public GoogleMap getMap() {
		return theMap;
	}
	public void setMap(GoogleMap theMap) {
		this.theMap = theMap;
	}
	public Location getLocation() {
		return bestLocation;
	}
	public void setLocation(Location bestLocation) {
		this.bestLocation = bestLocation;
		Log.e("application", "set location success");
	}
	public ParseGeoPoint getParseLocation() {
		return parseUserLocation;
	}
	public void setParseLocation(ParseGeoPoint bestParseLocation) {
		this.parseUserLocation = bestParseLocation;
		Log.e("application", "set Parse location success");
	}
	
	 /** Determines whether one Location reading is better than the current Location fix
	  * @param location  The new Location that you want to evaluate
	  * @param currentBestLocation  The current Location fix, to which you want to compare the new one
	  */
	protected boolean isBetterLocation(Location location, Location currentBestLocation) {
	    if (currentBestLocation == null) {
	        // A new location is always better than no location
	        return true;
	    }

	    // Check whether the new location fix is newer or older
	    long timeDelta = location.getTime() - currentBestLocation.getTime();
	    boolean isSignificantlyNewer = timeDelta > TWO_MINUTES;
	    boolean isSignificantlyOlder = timeDelta < -TWO_MINUTES;
	    boolean isNewer = timeDelta > 0;

	    // If it's been more than two minutes since the current location, use the new location
	    // because the user has likely moved
	    if (isSignificantlyNewer) {
	        return true;
	    // If the new location is more than two minutes older, it must be worse
	    } else if (isSignificantlyOlder) {
	        return false;
	    }

	    // Check whether the new location fix is more or less accurate
	    int accuracyDelta = (int) (location.getAccuracy() - currentBestLocation.getAccuracy());
	    boolean isLessAccurate = accuracyDelta > 0;
	    boolean isMoreAccurate = accuracyDelta < 0;
	    boolean isSignificantlyLessAccurate = accuracyDelta > 200;

	    // Check if the old and new location are from the same provider
	    boolean isFromSameProvider = isSameProvider(location.getProvider(),
	            currentBestLocation.getProvider());

	    // Determine location quality using a combination of timeliness and accuracy
	    if (isMoreAccurate) {
	        return true;
	    } else if (isNewer && !isLessAccurate) {
	        return true;
	    } else if (isNewer && !isSignificantlyLessAccurate && isFromSameProvider) {
	        return true;
	    }
	    return false;
	}

	/** Checks whether two providers are the same */
	private boolean isSameProvider(String provider1, String provider2) {
	    if (provider1 == null) {
	      return provider2 == null;
	    }
	    return provider1.equals(provider2);
	}
}
