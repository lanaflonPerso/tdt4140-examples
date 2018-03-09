package tdt4140.gr1800.app.core;

import static org.junit.Assert.assertTrue;

import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class GeoLocationsOwnerTest {

	private GeoLocationsOwner owner;
	
	@Before
	public void setUp() {
		this.owner = new GeoLocationsOwner();
	}
	
	private static <T> void check(Iterable<T> iterable, T... ts) {
		check(iterable, false, ts);
	}

	private static <T> void check(Iterable<T> iterable, boolean anyOrder, T... ts) {
		Collection<T> all = (anyOrder ? Arrays.asList(ts) : null);
		int num = 0;
		for (Iterator<T> it = iterable.iterator(); it.hasNext(); num++) {
			Assert.assertTrue(num < ts.length);
			T next = it.next();
			if (anyOrder) {
				assertTrue(all.contains(next));
			} else {
				Assert.assertEquals(ts[num], next);
			}
		}
		Assert.assertTrue(num == ts.length);
	}

	private static int size(Iterable<?> iterable) {
		int size = 0;
		for (Iterator<?> it = iterable.iterator(); it.hasNext(); it.next()) {
			size++;
		}
		return size;
	}

	@Test
	public void testGetGeoLocationNames() {
		Assert.assertEquals(0, size(owner.getGeoLocationsNames()));
		owner.addGeolocations(new GeoLocations("test1"));
		check(owner.getGeoLocationsNames(), "test1");
		owner.addGeolocations(new GeoLocations("test2"));
		check(owner.getGeoLocationsNames(), "test1", "test2");
	}
	
	@Test
	public void testHasGeoLocations() {
		Assert.assertTrue(owner.hasGeoLocations());
		Assert.assertFalse(owner.hasGeoLocations("test1"));
		owner.addGeolocations(new GeoLocations("test1"));
		Assert.assertTrue(owner.hasGeoLocations());
		Assert.assertTrue(owner.hasGeoLocations("test1"));
		owner.addGeolocations(new GeoLocations("test2"));
		Assert.assertTrue(owner.hasGeoLocations());
		Assert.assertTrue(owner.hasGeoLocations("test1"));
		Assert.assertTrue(owner.hasGeoLocations("test2"));
		Assert.assertTrue(owner.hasGeoLocations("test1", "test2"));
	}
	
	@Test
	public void testGetGeoLocations1() {
		Assert.assertNull(owner.getGeoLocations("test1"));
		GeoLocations geoLocations11 = new GeoLocations("test1");
		owner.addGeolocations(geoLocations11);
		Assert.assertSame(geoLocations11, owner.getGeoLocations("test1"));

		GeoLocations geoLocations2 = new GeoLocations("test2");
		owner.addGeolocations(geoLocations2);
		Assert.assertSame(geoLocations11, owner.getGeoLocations("test1"));
		Assert.assertSame(geoLocations2, owner.getGeoLocations("test2"));

		GeoLocations geoLocations12 = new GeoLocations("test1");
		owner.addGeolocations(geoLocations12);
		Assert.assertNotNull(owner.getGeoLocations("test1"));
		Assert.assertSame(geoLocations2, owner.getGeoLocations("test2"));

	}

	@Test
	public void testGetGeoLocationsN() {
		GeoLocations geoLocations11 = new GeoLocations("test1");
		owner.addGeolocations(geoLocations11);
		Assert.assertEquals(0, owner.getGeoLocations().size());
		check(owner.getGeoLocations(new String[]{"test1"}), geoLocations11);
		check(owner.getGeoLocations((String[]) null), geoLocations11);
		
		GeoLocations geoLocations2 = new GeoLocations("test2");
		owner.addGeolocations(geoLocations2);
		Assert.assertEquals(0, owner.getGeoLocations().size());
		check(owner.getGeoLocations(new String[]{"test1"}), geoLocations11);
		check(owner.getGeoLocations(new String[]{"test2"}), geoLocations2);
		check(owner.getGeoLocations((String[]) null), true, geoLocations11, geoLocations2);
		
		GeoLocations geoLocations12 = new GeoLocations("test1");
		owner.addGeolocations(geoLocations12);
		Assert.assertEquals(0, owner.getGeoLocations().size());
		check(owner.getGeoLocations(new String[]{"test1"}), true, geoLocations11, geoLocations12);
		Assert.assertEquals(1, owner.getGeoLocations(new String[]{"test2"}).size());
		check(owner.getGeoLocations((String[]) null), true, geoLocations11, geoLocations2, geoLocations12);
	}
	
	@Test
	public void testRemoveGeoLocationsN() {
		GeoLocations geoLocations11 = new GeoLocations("test1"), geoLocations2 = new GeoLocations("test2"), geoLocations12 = new GeoLocations("test1");
		owner.addGeolocations(geoLocations11);
		owner.addGeolocations(geoLocations2);
		owner.addGeolocations(geoLocations12);
		Assert.assertEquals(3, owner.getGeoLocations((String[]) null).size());
		owner.removeGeolocations("test1");
		check(owner.getGeoLocations((String[]) null), true, geoLocations2);
		owner.removeGeolocations("test2");
		Assert.assertEquals(0, owner.getGeoLocations((String[]) null).size());
	}
	
	@Test
	public void testRemoveGeoLocations1() {
		GeoLocations geoLocations11 = new GeoLocations("test1"), geoLocations2 = new GeoLocations("test2"), geoLocations12 = new GeoLocations("test1");
		owner.addGeolocations(geoLocations11);
		owner.addGeolocations(geoLocations2);
		owner.addGeolocations(geoLocations12);
		Assert.assertEquals(3, owner.getGeoLocations((String[]) null).size());
		owner.removeGeolocations(geoLocations11);
		check(owner.getGeoLocations((String[]) null), true, geoLocations12, geoLocations2);
		owner.removeGeolocations(geoLocations2);
		check(owner.getGeoLocations((String[]) null), geoLocations12);
		owner.removeGeolocations(geoLocations12);
		Assert.assertEquals(0, owner.getGeoLocations((String[]) null).size());
	}
}
