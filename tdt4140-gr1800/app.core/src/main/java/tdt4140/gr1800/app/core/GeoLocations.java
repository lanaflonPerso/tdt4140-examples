package tdt4140.gr1800.app.core;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

public class GeoLocations implements Iterable<GeoLocated>, Tagged {

	private String name;
	
	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	private Collection<GeoLocated> locations = new ArrayList<GeoLocated>();
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
	
	public Comparator<? super GeoLocated> closestComparator(GeoLocated latLong) {
		return (geoLoc1, geoLoc2) -> (int) Math.signum(latLong.distance(geoLoc1) - latLong.distance(geoLoc2));
	}

	public Collection<GeoLocated> findLocationsNearby(GeoLocated geoLoc, double distance) {
		return locations.stream()
				.filter(geoLoc2 -> distance == 0.0 ? geoLoc2.equalsLatLong(geoLoc) : geoLoc.distance(geoLoc2) <= distance)
				.sorted(closestComparator(geoLoc))
				.collect(Collectors.toList());
	}

	public GeoLocated findNearestLocation(GeoLocated geoLoc) {
		Optional<GeoLocated> min = locations.stream()
				.min(closestComparator(geoLoc));
		return min.isPresent() ? min.get() : null;
	}

	//
	
	public void addLocation(LatLong geoLoc) {
		locations.add(geoLoc);
	}

	public void removeLocations(GeoLocated geoLoc, double distance) {
		Iterator<GeoLocated> it = locations.iterator();
		while (it.hasNext()) {
			GeoLocated geoLoc2 = it.next();
			if (distance == 0.0 ? geoLoc2.equalsLatLong(geoLoc) : geoLoc.getLatLong().distance(geoLoc2.getLatLong()) <= distance) {
				it.remove();
			}
		}
	}

	public int size() {
		return locations.size();
	}
	
	@Override
	public Iterator<GeoLocated> iterator() {
		return locations.iterator();
	}
	
	//
	
	private Set<String> tags = null;

	@Override
	public boolean hasTags(String... tags) {
		return this.tags != null && this.tags.containsAll(Arrays.asList(tags));
	}
	
	public void addTags(String... tags) {
		if (this.tags == null) {
			this.tags = new HashSet<>();
		}
		this.tags.addAll(Arrays.asList(tags));
	}
	
	public void removeTags(String... tags) {
		if (this.tags != null) {
			this.tags.removeAll(Arrays.asList(tags));
		}
	}
}
