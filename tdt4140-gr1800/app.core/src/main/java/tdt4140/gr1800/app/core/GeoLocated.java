package tdt4140.gr1800.app.core;

/**
 * Interface for geo-located data, i.e. classes that can return a corresponding LatLong object.
 * @author hal
 *
 */
public interface GeoLocated {

	/**
	 * @return the corresponding LatLong object
	 */
	public LatLong getLatLong();

	/**
	 * @return the corresponding latitude
	 */
	default double getLatitude() {
		return getLatLong().latitude;
	}

	/**
	 * @return the corresponding longitude
	 */
	default double getLongitude() {
		return getLatLong().longitude;
	}

	/**
	 * Checks that the latitudes and longitudes are the same
	 * @param geoLoc
	 * @return true if the latitudes and longitudes are the same, false otherwise
	 */
	default boolean equalsLatLong(final GeoLocated geoLoc) {
		return getLatitude() == geoLoc.getLatitude() && getLongitude() == geoLoc.getLongitude();
	}

	/**
	 * @param geoLoc
	 * @return the distance between the LatLong of this GeoLocated and the one provided
	 */
	default double distance(final GeoLocated geoLoc) {
		return getLatLong().distance(geoLoc.getLatLong());
	}
}
