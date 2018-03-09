package tdt4140.gr1800.app.core;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

public class GeoLocationsOwner {

	private Collection<GeoLocations> geoLocations = null;

	public Iterable<String> getGeoLocationsNames() {
		Collection<String> names = new ArrayList<String>(geoLocations != null ? geoLocations.size() : 0);
		if (geoLocations != null) {
			for (GeoLocations geoLocations : geoLocations) {
				names.add(geoLocations.getName());
			}
		}
		return names;
	}

	public boolean hasGeoLocations(String... names) {
		if (geoLocations != null) {
			outer: for (String name : names) {
				for (GeoLocations geoLocations : geoLocations) {
					if (name.equals(geoLocations.getName())) {
						continue outer;
					}
				}
				return false;
			}
			return true;
		}
		return names.length == 0;
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

	public Collection<GeoLocations> getGeoLocations(String... names) {
		Collection<GeoLocations> result = new ArrayList<GeoLocations>();
		if (geoLocations != null) {
			if (names != null) {
				for (GeoLocations geoLocations : geoLocations) {
					for (String name : names) {
						if (name.equals(geoLocations.getName())) {
							result.add(geoLocations);
						}
					}
				}
			} else {
				result.addAll(geoLocations);
			}
		}
		return result;
	}

	//

	public void removeGeolocations(String... names) {
		if (geoLocations != null) {
			Iterator<GeoLocations> it = geoLocations.iterator();
			while (it.hasNext()) {
				GeoLocations next = it.next();
				if (names != null) {
					for (String name : names) {
						if (name.equals(next.getName())) {
							it.remove();
							break;
						}
					}
				} else {
					it.remove();				
				}
			}
		}
	}

	public void addGeolocations(GeoLocations geoLocations) {
		if (this.geoLocations == null) {
			this.geoLocations = new ArrayList<>();
		}
		if (! this.geoLocations.contains(geoLocations)) {
			this.geoLocations.add(geoLocations);
		}
	}

	public void removeGeolocations(GeoLocations geoLocations) {
		if (this.geoLocations != null) {
			this.geoLocations.remove(geoLocations);
		}
	}
}
