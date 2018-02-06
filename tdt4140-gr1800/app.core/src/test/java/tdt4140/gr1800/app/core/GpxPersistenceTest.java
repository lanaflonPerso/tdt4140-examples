package tdt4140.gr1800.app.core;

import java.util.Collection;
import java.util.Iterator;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import io.jenetics.jpx.GPX;
import tdt4140.gr1800.app.gpx.GpxDocumentConverter;
import tdt4140.gr1800.app.gpx.GpxDocumentLoader;

public class GpxPersistenceTest {

	private GpxDocumentLoader loader;
	private GpxDocumentConverter converter;
	
	@Before
	public void setUp() {
		loader = new GpxDocumentLoader();
		converter = new GpxDocumentConverter();
	}

	@Test
	public void testLoadDocument() {
		try {
			GPX gpx = loader.loadDocument(getClass().getResourceAsStream("sample1.gpx"));
			Assert.assertEquals(1, gpx.getTracks().size());
			Assert.assertEquals(1, gpx.getTracks().get(0).getSegments().size());
			Assert.assertEquals(3, gpx.getTracks().get(0).getSegments().get(0).getPoints().size());
		} catch (Exception e) {
			Assert.fail(e.getMessage());
		}
	}
	
	@Test
	public void testConvertSample1() {
		try {
			GPX gpx = loader.loadDocument(getClass().getResourceAsStream("sample1.gpx"));
			Collection<GeoLocations> geoLocations = converter.convert(gpx);
			Assert.assertEquals(1, geoLocations.size());
			GeoLocations track = geoLocations.iterator().next();
			Assert.assertEquals("Example GPX Document", track.getName());	
			Assert.assertEquals(3, track.size());
			Assert.assertTrue(track.isPath());
		} catch (Exception e) {
			System.err.println(e);
			Assert.fail(e.getMessage());
		}
	}
	
	@Test
	public void testConvertAchterbroekRoute() {
		try {
			GPX gpx = loader.loadDocument(getClass().getResourceAsStream("Achterbroek-route.gpx"));
			Collection<GeoLocations> geoLocations = converter.convert(gpx);
			Assert.assertEquals(2, geoLocations.size());
			Iterator<GeoLocations> iterator = geoLocations.iterator();
			GeoLocations route = iterator.next();
			Assert.assertEquals("Achterbroek naar De Maatjes   13 km RT", route.getName());	
			Assert.assertEquals(19, route.size());
			Assert.assertTrue(route.isPath());
			GeoLocations waypoints = iterator.next();
			Assert.assertEquals("Achterbroek naar De Maatjes   13 km RT", route.getName());	
			Assert.assertEquals(1, waypoints.size());
			Assert.assertFalse(waypoints.isPath());
		} catch (Exception e) {
			System.err.println(e);
			Assert.fail(e.getMessage());
		}
	}

	@Test
	public void testConvertAchterbroekTrack() {
		try {
			GPX gpx = loader.loadDocument(getClass().getResourceAsStream("Achterbroek-track.gpx"));
			Collection<GeoLocations> geoLocations = converter.convert(gpx);
			Assert.assertEquals(2, geoLocations.size());
			Iterator<GeoLocations> iterator = geoLocations.iterator();
			GeoLocations track = iterator.next();
			Assert.assertEquals("Achterbroek naar De Maatjes   13 km TR", track.getName());	
			Assert.assertEquals(26, track.size());
			Assert.assertTrue(track.isPath());
			GeoLocations waypoints = iterator.next();
			Assert.assertEquals(1, waypoints.size());
			Assert.assertFalse(waypoints.isPath());
		} catch (Exception e) {
			System.err.println(e);
			Assert.fail(e.getMessage());
		}
	}
}
