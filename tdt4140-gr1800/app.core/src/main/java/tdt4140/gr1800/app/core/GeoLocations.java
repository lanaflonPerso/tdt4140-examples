package tdt4140.gr1800.app.core;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.Iterator;
import java.util.Optional;
import java.util.stream.Collectors;

public class GeoLocations implements Iterable<LatLong> {

	private String name;
	
	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	private Collection<LatLong> locations = new ArrayList<LatLong>();
	private boolean path = false;
	
	public GeoLocations(LatLong...latLongs) {
		for (int i = 0; i < latLongs.length; i++) {
			addLocation(latLongs[i]);
		}
	}
	
	public GeoLocations(String name, LatLong...latLongs) {
		this(latLongs);
		setName(name);
	}
	
	public boolean isPath() {
		return path;
	}
	
	public void setPath(boolean path) {
		this.path = path;
	}
	
	public Comparator<? super LatLong> latLongComparator(LatLong latLong) {
		return (latLong1, latLong2) -> (int) Math.signum(latLong.distance(latLong1) - latLong.distance(latLong2));
	}

	public Collection<LatLong> findLocationsNearby(LatLong latLong, double distance) {
		return locations.stream()
				.filter(latLong2 -> latLong.distance(latLong2) <= distance)
				.sorted(latLongComparator(latLong))
				.collect(Collectors.toList());
	}

	public LatLong findNearestLocation(LatLong latLong) {
		Optional<LatLong> min = locations.stream()
				.min(latLongComparator(latLong));
		return min.isPresent() ? min.get() : null;
	}

	//
	
	public void addLocation(LatLong latLong) {
		if (! locations.contains(latLong)) {
			locations.add(latLong);
		}
	}

	public void removeLocations(LatLong latLong, double distance) {
		Iterator<LatLong> it = locations.iterator();
		while (it.hasNext()) {
			LatLong latLong2 = it.next();
			if (latLong.distance(latLong2) <= distance) {
				it.remove();
			}
		}
	}

	public int size() {
		return locations.size();
	}
	
	@Override
	public Iterator<LatLong> iterator() {
		return locations.iterator();
	}
}
