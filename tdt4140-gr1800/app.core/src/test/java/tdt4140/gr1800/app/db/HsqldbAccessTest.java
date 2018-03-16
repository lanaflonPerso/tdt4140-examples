package tdt4140.gr1800.app.db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Collection;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import tdt4140.gr1800.app.core.GeoLocations;
import tdt4140.gr1800.app.core.Person;

public class HsqldbAccessTest {

	private Connection dbCon;
	private AbstractDbAccessImpl dbAccess;
	private static int testNum = 0;

	@Before
	public void setUp() throws Exception {
		Class.forName("org.hsqldb.jdbc.JDBCDriver");
		dbCon = DriverManager.getConnection("jdbc:hsqldb:mem:" + HsqldbAccessTest.class.getName() + testNum, "SA", "");
		final DbAccessImpl dbAccess = new DbAccessImpl(dbCon);
		dbAccess.executeStatements("schema.sql", false);
		this.dbAccess = dbAccess;
		testNum++;
	}

	@After
	public void tearDown() {
		if (dbCon != null) {
			try {
				dbCon.close();
			} catch (final SQLException e) {
			}
		}
	}

	protected void checkPersonData(final Person hal, final Person dbHal) {
		Assert.assertNotNull(dbHal);
		Assert.assertEquals(hal.getName(), dbHal.getName());
		Assert.assertEquals(hal.getEmail(), dbHal.getEmail());
	}

	@Test
	public void testCreatePersonGetAllPersons() {
		final Person hal = dbAccess.createPerson("hal", "hal@ntnu.no");
		final Collection<Person> persons = dbAccess.getAllPersons(false);
		Assert.assertEquals(1, persons.size());
		checkPersonData(hal, persons.iterator().next());
		dbAccess.personIds.clear();
		final Collection<Person> dbPersons = dbAccess.getAllPersons(true);
		Assert.assertEquals(1, dbPersons.size());
		checkPersonData(hal, dbPersons.iterator().next());
	}

	@Test
	public void testCreatePersonGetPerson() {
		final Person hal = dbAccess.createPerson("hal", "hal@ntnu.no");
		final int id = dbAccess.getId(hal);
		final Person dbHal = dbAccess.getPerson(id, true);
		checkPersonData(hal, dbHal);
		Assert.assertSame(dbHal, dbAccess.getPerson(id, false));
	}

	@Test
	public void testCreatePersonGetPersonByName() {
		final Person hal = dbAccess.createPerson("hal", "hal@ntnu.no");
		final Person dbHal = dbAccess.getPersonByName(hal.getName(), true);
		checkPersonData(hal, dbHal);
	}

	@Test
	public void testCreatePersonGetPersonByEmail() {
		final Person hal = dbAccess.createPerson("hal", "hal@ntnu.no");
		final Person dbHal = dbAccess.getPersonByEmail(hal.getEmail(), true);
		checkPersonData(hal, dbHal);
	}

	@Test
	public void testCreatePersonUpdatePerson() {
		final Person hal = dbAccess.createPerson("hal", "hal@ntnu.no");
		final int id = dbAccess.getId(hal);
		hal.setName("Hallvard");
		hal.setEmail("hallvard.traetteberg@gmail.com");
		dbAccess.updatePersonData(hal);
		final Person dbHal = dbAccess.getPerson(id, true);
		checkPersonData(hal, dbHal);
	}

	@Test
	public void testCreatePersonDeletePerson() {
		final Person hal = dbAccess.createPerson("hal", "hal@ntnu.no");
		dbAccess.deletePerson(hal);
		final Collection<Person> persons = dbAccess.getAllPersons(true);
		Assert.assertEquals(0, persons.size());
	}

	@Test
	public void testCreatePersonCreateGeoLocationsGetGeoLocations() {
		final Person hal = dbAccess.createPerson("hal", "hal@ntnu.no");
		// no GeoLocations so far
		Assert.assertEquals(0, dbAccess.getGeoLocations(hal, false).size());
		final GeoLocations geoLocations1 = dbAccess.createGeoLocations(hal);
		// check we have one locally
		Assert.assertEquals(1, hal.getGeoLocations((String[]) null).size());
		final Collection<GeoLocations> geoLocations = dbAccess.getGeoLocations(hal, false);
		Assert.assertEquals(1, hal.getGeoLocations((String[]) null).size());
		Assert.assertEquals(1, geoLocations.size());
		Assert.assertSame(geoLocations1, geoLocations.iterator().next());
		dbAccess.geoLocationsIds.clear();
		// check we have one in the db
		final Collection<GeoLocations> dbGeoLocations = dbAccess.getGeoLocations(hal, true);
		Assert.assertEquals(1, hal.getGeoLocations((String[]) null).size());
		Assert.assertEquals(1, dbGeoLocations.size());
	}

	@Test
	public void testCreatePersonCreateUpdateGeoLocationsGetGeoLocations() {
		final Person hal = dbAccess.createPerson("hal", "hal@ntnu.no");
		final GeoLocations geoLocations1 = dbAccess.createGeoLocations(hal);
		geoLocations1.setName("geoLocs1");
		geoLocations1.setDescription("my first geo-location");
		geoLocations1.setDate(LocalDate.of(2018, 3, 14));
		geoLocations1.setTime(LocalTime.of(11, 14));
		geoLocations1.setZone("Europe/Oslo");
		final String[] tags = {"tag1", "tag2"};
		geoLocations1.addTags(tags);
		dbAccess.updateGeoLocationsData(geoLocations1);
		dbAccess.geoLocationsIds.clear();
		// check the db
		final Collection<GeoLocations> dbGeoLocations = dbAccess.getGeoLocations(hal, true);
		Assert.assertEquals(1, hal.getGeoLocations((String[]) null).size());
		Assert.assertEquals(1, dbGeoLocations.size());
		final GeoLocations dbGeoLocations1 = dbGeoLocations.iterator().next();
		Assert.assertEquals(geoLocations1.getName(), dbGeoLocations1.getName());
		Assert.assertEquals(geoLocations1.getDescription(), dbGeoLocations1.getDescription());
		Assert.assertEquals(geoLocations1.getDate(), dbGeoLocations1.getDate());
		Assert.assertEquals(geoLocations1.getTime(), dbGeoLocations1.getTime());
		Assert.assertEquals(geoLocations1.getZone(), dbGeoLocations1.getZone());
		Assert.assertEquals(2, dbGeoLocations1.getTags().length);
		Assert.assertTrue(dbGeoLocations1.hasTags(tags));
	}

	@Test
	public void testCreatePersonCreateGeoLocationsDeleteGeoLocations() {
		final Person hal = dbAccess.createPerson("hal", "hal@ntnu.no");
		final GeoLocations geoLocations = dbAccess.createGeoLocations(hal);
		final Collection<GeoLocations> dbGeoLocations = dbAccess.getGeoLocations(hal, true);
		Assert.assertEquals(1, dbGeoLocations.size());
		dbAccess.deleteGeoLocations(geoLocations);
	}
}
