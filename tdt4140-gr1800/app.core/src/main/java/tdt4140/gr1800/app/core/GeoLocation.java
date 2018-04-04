package tdt4140.gr1800.app.core;

/**
 * Main implementation of GeoLocated. Combines a LatLong and elevation with generic values
 * like name, description, time (implements Timed) and tags (implements Tagged).
 * @author hal
 *
 */
public class GeoLocation extends TimedTaggedImpl implements GeoLocated, Timed, Tagged {

	private LatLong latLong;

	@Override
	public LatLong getLatLong() {
		return latLong;
	}

	public void setLatLong(final LatLong latLong) {
		this.latLong = latLong;
	}

	private int elevation;

	public int getElevation() {
		return elevation;
	}

	public void setElevation(final int elevation) {
		this.elevation = elevation;
	}

	private String name, description;

	public String getName() {
		return name;
	}

	public void setName(final String name) {
		this.name = name;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(final String description) {
		this.description = description;
	}
}
