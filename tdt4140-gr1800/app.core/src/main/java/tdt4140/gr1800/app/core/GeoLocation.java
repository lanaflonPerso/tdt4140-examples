package tdt4140.gr1800.app.core;

public class GeoLocation extends TimedTaggedImpl implements GeoLocated, Timed, Tagged {

	private LatLong latLong;

	public LatLong getLatLong() {
		return latLong;
	}

	public void setLatLong(LatLong latLong) {
		this.latLong = latLong;
	}

	private int elevation;
	
	public int getElevation() {
		return elevation;
	}
	
	private String name, description;
	
	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public String getDescription() {
		return description;
	}
	
	public void setDescription(String description) {
		this.description = description;
	}
}
