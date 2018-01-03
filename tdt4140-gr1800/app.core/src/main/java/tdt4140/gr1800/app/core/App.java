package tdt4140.gr1800.app.core;

import java.net.URI;
import java.util.ArrayList;
import java.util.Collection;

import tdt4140.gr1800.app.json.GeoLocationsJsonPersistence;

public class App {

	private GeoLocationsPersistence geoLocationsLoader = new GeoLocationsJsonPersistence();
	
	public void loadGeoLocations(URI uri) throws Exception {
		geoLocations = geoLocationsLoader.loadLocations(uri.toURL().openStream());
	}

	private Collection<GeoLocations> geoLocations;

	public Iterable<String> getGeoLocationNames() {
		Collection<String> names = new ArrayList<String>(geoLocations.size());
		if (geoLocations != null) {
			for (GeoLocations geoLocations : geoLocations) {
				names.add(geoLocations.getName());
			}
		}
		return names;
	}
	
	public boolean hasGeoLocations(String name) {
		if (geoLocations != null) {
			for (GeoLocations geoLocations : geoLocations) {
				if (name.equals(geoLocations.getName())) {
					return true;
				}
			}
		}
		return false;
	}

	public GeoLocations getGeoLocations(String name) {
		if (geoLocations != null) {
			for (GeoLocations geoLocations : geoLocations) {
				if (name.equals(geoLocations.getName())) {
					return geoLocations;
				}
			}
		}
		return null;
	}
}
