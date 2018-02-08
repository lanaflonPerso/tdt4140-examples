package tdt4140.gr1800.web.server;

import java.io.InputStream;
import java.net.URL;
import java.util.Collection;
import java.util.Iterator;

import org.junit.Assert;
import org.junit.Test;

import tdt4140.gr1800.app.core.GeoLocated;
import tdt4140.gr1800.app.core.GeoLocations;
import tdt4140.gr1800.app.core.GeoLocationsPersistence;
import tdt4140.gr1800.app.core.LatLong;
import tdt4140.gr1800.app.json.GeoLocationsJsonPersistence;

public class GeoLocationsServerIT {

	private GeoLocationsPersistence persistence = new GeoLocationsJsonPersistence();
	
	private Collection<GeoLocations> get(String path, int size) throws Exception {
		URL url = new URL("http://localhost:8080/geo" + path);
		try (InputStream inputStream = url.openStream()) {
			Collection<GeoLocations> geoLocations = persistence.loadLocations(inputStream);
			Assert.assertEquals(size, geoLocations.size());
			return geoLocations;
		}
	}

	private LatLong[][] latLongs = {
		{ new LatLong(63,  10), new LatLong(63.1,  10.1)},
		{ new LatLong(64,  11), new LatLong(64.1,  11.1)}
	};
	
	@Test
	public void testGet() throws Exception {
		Collection<GeoLocations> geoLocations = get("", 2);
		Iterator<GeoLocations> it = geoLocations.iterator();
		checkGeoLocations(it.next(), "1", latLongs[0]);
		checkGeoLocations(it.next(), "2", latLongs[1]);

		Collection<GeoLocations> geoLocations1 = get("/1", 1);
		checkGeoLocations(geoLocations1.iterator().next(), "1", latLongs[0]);
		Collection<GeoLocations> geoLocations2 = get("/2", 1);
		checkGeoLocations(geoLocations2.iterator().next(), "2", latLongs[1]);
	}

	private void checkGeoLocations(GeoLocations geoLocations, String name, LatLong... latLongs) {
		Assert.assertEquals(name, geoLocations.getName());
		Iterator<GeoLocated> it = geoLocations.iterator();
		for (int i = 0; i < latLongs.length; i++) {
			Assert.assertTrue(it.hasNext());
			latLongs[i].equalsLatLong(it.next());
		}
		Assert.assertFalse(it.hasNext());
	}
}
