package tdt4140.gr1800.app.core;

public interface GeoLocated {

	public LatLong getLatLong();
	
	default double getLatitude() {
		return getLatLong().latitude;
	}
	default double getLongitude() {
		return getLatLong().longitude;
	}
	default boolean equalsLatLong(GeoLocated geoLoc) {
		return getLatitude() == geoLoc.getLatitude() && getLongitude() == geoLoc.getLongitude();
	}
	default double distance(GeoLocated geoLoc) {
		return getLatLong().distance(geoLoc.getLatLong());
	}
}
