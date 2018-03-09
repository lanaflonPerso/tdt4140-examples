package tdt4140.gr1800.app.core;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.Iterator;
import java.util.Optional;
import java.util.stream.Collectors;

public class GeoLocations extends TimedTaggedImpl implements Iterable<GeoLocated>, Tagged, Timed {

	private final GeoLocationsOwner owner;
	
	public GeoLocations(GeoLocationsOwner owner) {
		this.owner = owner;
	}

	public GeoLocationsOwner getOwner() {
		return owner;
	}
	
	private String name;
	
	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
	}

	private String description;

	public String getDescription() {
		return description;
	}
	
	public void setDescription(String description) {
		this.description = description;
	}

	private Collection<GeoLocated> locations = new ArrayList<GeoLocated>();
	private boolean path = false;
	
	public GeoLocations(GeoLocated...geoLocs) {
		this((GeoLocationsOwner) null);
		for (int i = 0; i < geoLocs.length; i++) {
			addLocation(geoLocs[i]);
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
	
	public void addLocation(GeoLocated geoLoc) {
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
}
