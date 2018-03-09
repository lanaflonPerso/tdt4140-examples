package tdt4140.gr1800.app.db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Collection;
import java.util.Scanner;

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
		executeStatements("schema.sql");
		dbAccess = new DbAccessImpl(dbCon);
		testNum++;
	}
	
	@After
	public void tearDown() {
		if (dbCon != null) {
			try {
				dbCon.close();
			} catch (SQLException e) {
			}
		}
	}

	protected void executeStatements(String path) throws SQLException {
		Statement dbStatement = dbCon.createStatement();
		StringBuilder buffer = new StringBuilder();
		try (Scanner scanner = new Scanner(HsqldbAccessTest.class.getResourceAsStream(path))) {
			while (scanner.hasNextLine()) {
				String line = scanner.nextLine();
				int pos = line.indexOf(";");
				if (pos >= 0) {
					buffer.append(line.substring(0, pos + 1));
					String sql = buffer.toString();
					buffer.setLength(0);
					if (pos < line.length()) {
						buffer.append(line.substring(pos + 1));
					}
					if (! sql.startsWith("DROP")) {
						dbStatement.execute(sql);
					}
				} else {
					buffer.append(line);
					buffer.append("\n");
				}
			}
		}
	}
	
	protected void checkPersonData(Person hal, Person dbHal) {
		Assert.assertNotNull(dbHal);
		Assert.assertEquals(hal.getName(), dbHal.getName());
		Assert.assertEquals(hal.getEmail(), dbHal.getEmail());
	}

	@Test
	public void testCreatePersonGetAllPersons() {
		Person hal = dbAccess.createPerson("hal", "hal@ntnu.no");
		Collection<Person> persons = dbAccess.getAllPersons(false);
		Assert.assertEquals(1, persons.size());
		checkPersonData(hal, persons.iterator().next());
		dbAccess.personIds.clear();
		Collection<Person> dbPersons = dbAccess.getAllPersons(true);
		Assert.assertEquals(1, dbPersons.size());
		checkPersonData(hal, dbPersons.iterator().next());
	}

	@Test
	public void testCreatePersonGetPerson() {
		Person hal = dbAccess.createPerson("hal", "hal@ntnu.no");
		int id = dbAccess.getId(hal);
		Person dbHal = dbAccess.getPerson(id, true);
		checkPersonData(hal, dbHal);
		Assert.assertSame(dbHal, dbAccess.getPerson(id, false));
	}
	
	@Test
	public void testCreatePersonGetPersonByName() {
		Person hal = dbAccess.createPerson("hal", "hal@ntnu.no");
		Person dbHal = dbAccess.getPersonByName(hal.getName(), true);
		checkPersonData(hal, dbHal);
	}
	
	@Test
	public void testCreatePersonGetPersonByEmail() {
		Person hal = dbAccess.createPerson("hal", "hal@ntnu.no");
		Person dbHal = dbAccess.getPersonByEmail(hal.getEmail(), true);
		checkPersonData(hal, dbHal);
	}
	
	@Test
	public void testCreatePersonUpdatePerson() {
		Person hal = dbAccess.createPerson("hal", "hal@ntnu.no");
		int id = dbAccess.getId(hal);
		hal.setName("Hallvard");
		hal.setEmail("hallvard.traetteberg@gmail.com");
		dbAccess.updatePersonData(hal);
		Person dbHal = dbAccess.getPerson(id, true);
		checkPersonData(hal, dbHal);
	}

	@Test
	public void testCreatePersonDeletePerson() {
		Person hal = dbAccess.createPerson("hal", "hal@ntnu.no");
		dbAccess.deletePerson(hal);
		Collection<Person> persons = dbAccess.getAllPersons(true);
		Assert.assertEquals(0, persons.size());
	}
	
	@Test
	public void testCreatePersonCreateGeoLocationsGetGeoLocations() {
		Person hal = dbAccess.createPerson("hal", "hal@ntnu.no");
		// no GeoLocations so far
		Assert.assertEquals(0, dbAccess.getGeoLocations(hal, false).size());
		GeoLocations geoLocations1 = dbAccess.createGeoLocations(hal);
		// check we have one locally
		Assert.assertEquals(1, hal.getGeoLocations((String[]) null).size());
		Collection<GeoLocations> geoLocations = dbAccess.getGeoLocations(hal, false);
		Assert.assertEquals(1, hal.getGeoLocations((String[]) null).size());
		Assert.assertEquals(1, geoLocations.size());
		Assert.assertSame(geoLocations1, geoLocations.iterator().next());
		dbAccess.geoLocationsIds.clear();
		// check we have one in the db
		Collection<GeoLocations> dbGeoLocations = dbAccess.getGeoLocations(hal, true);
		Assert.assertEquals(1, hal.getGeoLocations((String[]) null).size()); 
		Assert.assertEquals(1, dbGeoLocations.size());
	}
	
	@Test
	public void testCreatePersonCreateUpdateGeoLocationsGetGeoLocations() {
		Person hal = dbAccess.createPerson("hal", "hal@ntnu.no");
		GeoLocations geoLocations1 = dbAccess.createGeoLocations(hal);
		geoLocations1.setName("geoLocs1");
		String[] tags = {"tag1", "tag2"}; 
		geoLocations1.addTags(tags);
		dbAccess.updateGeoLocationsData(geoLocations1);
		dbAccess.geoLocationsIds.clear();
		// check the db
		Collection<GeoLocations> dbGeoLocations = dbAccess.getGeoLocations(hal, true);
		Assert.assertEquals(1, hal.getGeoLocations((String[]) null).size()); 
		Assert.assertEquals(1, dbGeoLocations.size());
		GeoLocations dbGeoLocations1 = dbGeoLocations.iterator().next();
		Assert.assertEquals(2, dbGeoLocations1.getTags().length);
		Assert.assertTrue(dbGeoLocations1.hasTags(tags));
	}
	
	@Test
	public void testCreatePersonCreateGeoLocationsDeleteGeoLocations() {
		Person hal = dbAccess.createPerson("hal", "hal@ntnu.no");
		GeoLocations geoLocations = dbAccess.createGeoLocations(hal);
		Collection<GeoLocations> dbGeoLocations = dbAccess.getGeoLocations(hal, true);
		Assert.assertEquals(1, dbGeoLocations.size());		
		dbAccess.deleteGeoLocations(geoLocations);
	}
}
