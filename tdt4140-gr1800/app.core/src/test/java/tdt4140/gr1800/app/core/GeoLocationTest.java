package tdt4140.gr1800.app.core;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class GeoLocationTest extends TimedTaggedTest {

	private GeoLocation geoLocation;
	
	@Before
	public void setUp() {
		setUp(geoLocation = new GeoLocation());
	}
	
	@Test
	public void testSetLatLong() {
		LatLong latLong = new LatLong(63, 10);
		geoLocation.setLatLong(latLong);
		Assert.assertEquals(latLong, geoLocation.getLatLong());
	}
}
