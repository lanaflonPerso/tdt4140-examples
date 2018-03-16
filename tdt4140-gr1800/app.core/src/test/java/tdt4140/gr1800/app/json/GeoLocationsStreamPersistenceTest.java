package tdt4140.gr1800.app.json;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import tdt4140.gr1800.app.core.GeoLocations;
import tdt4140.gr1800.app.core.GeoLocationsStreamPersistence;
import tdt4140.gr1800.app.core.GeoLocationsTest;
import tdt4140.gr1800.app.core.LatLong;

public class GeoLocationsStreamPersistenceTest {

	private GeoLocationsStreamPersistence persistence;

	@Before
	public void setUp() {
		persistence = new GeoLocationsPersistence();
	}

	@Test
	public void testLoadLocations() {
		Collection<GeoLocations> geoLocations = null;
		try {
			geoLocations = persistence.loadLocations(getClass().getResourceAsStream("geoLocations.json"));
		} catch (final Exception e) {
			Assert.fail(e.getMessage());
		}
		testGeoLocationsDotJson(geoLocations);
	}

	public static Collection<GeoLocations> createGeoLocationsDotJson() {
		return Arrays.asList(
				new GeoLocations("1", new LatLong(63, 10), new LatLong(63.1, 10.1)),
				new GeoLocations("2", new LatLong(64, 11), new LatLong(64.1, 11.1))
				);
	}

	public static void testGeoLocationsDotJson(final Collection<GeoLocations> geoLocations) {
		Assert.assertEquals(2, geoLocations.size());
		final Iterator<GeoLocations> it = geoLocations.iterator();
		GeoLocationsTest.assertGeoLocations(it.next(), new LatLong(63, 10), new LatLong(63.1, 10.1));
		GeoLocationsTest.assertGeoLocations(it.next(), new LatLong(64, 11), new LatLong(64.1, 11.1));
	}

	@Test
	public void testSaveLocations() {
		final Collection<GeoLocations> geoLocations = createGeoLocationsDotJson();
		final ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
		Collection<GeoLocations> geoLocations2 = null;
		try {
			persistence.saveLocations(geoLocations, outputStream);
			outputStream.close();
			geoLocations2 = persistence.loadLocations(new ByteArrayInputStream(outputStream.toByteArray()));
		} catch (final Exception e) {
			e.printStackTrace();
			Assert.fail(e.getMessage());
		}
		testGeoLocationsDotJson(geoLocations2);
	}
}
