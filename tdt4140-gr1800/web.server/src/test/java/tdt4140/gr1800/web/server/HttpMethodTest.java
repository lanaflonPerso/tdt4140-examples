package tdt4140.gr1800.web.server;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;

import tdt4140.gr1800.app.core.GeoLocation;
import tdt4140.gr1800.app.core.GeoLocations;
import tdt4140.gr1800.app.core.LatLong;
import tdt4140.gr1800.app.core.Person;
import tdt4140.gr1800.app.db.AbstractDbAccessImpl;
import tdt4140.gr1800.app.db.IDbAccess;
import tdt4140.gr1800.app.json.GeoLocationDeserializer;
import tdt4140.gr1800.app.json.GeoLocationSerializer;
import tdt4140.gr1800.app.json.GeoLocationsDeserializer;
import tdt4140.gr1800.app.json.GeoLocationsSerializer;
import tdt4140.gr1800.app.json.LatLongDeserializer;
import tdt4140.gr1800.app.json.LatLongSerializer;
import tdt4140.gr1800.app.json.PersonDeserializer;
import tdt4140.gr1800.app.json.PersonSerializer;

public class HttpMethodTest {

	private IDbAccess dbAccess;
	private RestEntity<Void> rootEntity;

	private Map<String, Object> dbAccessRecord;
	private ObjectMapper objectMapper;

	@Before
	public void setUpEntities() {

		objectMapper = new ObjectMapper();
		final SimpleModule module = new SimpleModule();
		module.addSerializer(new LatLongSerializer());
		module.addSerializer(new GeoLocationSerializer());
		module.addSerializer(new GeoLocationsSerializer());
		module.addSerializer(new PersonSerializer());
		module.addDeserializer(LatLong.class, new LatLongDeserializer());
		module.addDeserializer(GeoLocation.class, new GeoLocationDeserializer());
		module.addDeserializer(GeoLocations.class, new GeoLocationsDeserializer());
		module.addDeserializer(Person.class, new PersonDeserializer());
		objectMapper.registerModule(module);

		dbAccessRecord = new HashMap<>();
		dbAccess = new AbstractDbAccessImpl() {

			@Override
			public Person createPerson(final String name, final String email) {
				final Person person = super.createPerson(name, email);
				personIds.set(person, personIds.size() + 1);
				dbAccessRecord.put("createPerson", "name=" + name + ";email=" + email);
				return person;
			}

			@Override
			public Collection<Person> getAllPersons(final boolean refresh) {
				final Collection<Person> persons = super.getAllPersons(refresh);
				dbAccessRecord.put("getAllPersons", refresh);
				return persons;
			}

			@Override
			public Person getPerson(final int id, final boolean refresh) {
				final Person person = super.getPerson(id, refresh);
				dbAccessRecord.put("getPerson", id);
				return person;
			}

			@Override
			public void updatePersonData(final Person person) {
				dbAccessRecord.put("updatePersonData", "name=" + person.getName() + ";email=" + person.getEmail());
			}

			@Override
			public void deletePerson(final Person person) {
				super.deletePerson(person);
				dbAccessRecord.put("deletePerson", person.getName());
			}

			//

			@Override
			public GeoLocations createGeoLocations(final Person owner) {
				final GeoLocations geoLocations = super.createGeoLocations(owner);
				geoLocationsIds.set(geoLocations, geoLocationsIds.size() + 1);
				dbAccessRecord.put("createGeoLocations", owner.getGeoLocations((String[]) null).size());
				return geoLocations;
			}

			@Override
			public Collection<GeoLocations> getGeoLocations(final Person owner, final boolean refresh) {
				final Collection<GeoLocations> geoLocations = super.getGeoLocations(owner, refresh);
				dbAccessRecord.put("getGeoLocations", refresh);
				return geoLocations;
			}

			@Override
			public void updateGeoLocationsData(final GeoLocations geoLocations) {
				dbAccessRecord.put("updateGeoLocationsData", geoLocations.getName());
			}

			@Override
			public void deleteGeoLocations(final GeoLocations geoLocations) {
				super.deleteGeoLocations(geoLocations);
				dbAccessRecord.put("deleteGeoLocations", geoLocations.getName());
			}

			@Override
			public void updateGeoLocationData(final GeoLocations geoLocations, final GeoLocation geoLocation) {
			}
		};

		rootEntity = new RestEntity<>();
		final RestEntity<Person> personEntity = new RestEntity<>();
		final RestEntity<GeoLocations> geoLocationsEntity = new RestEntity<>();
		final RestEntity<GeoLocation> geoLocationEntity = new RestEntity<>();

		final RestRelation<Void, Person> rootPersonRelation = new RootPersonsRelation("persons", personEntity);
		rootEntity.addRelation(rootPersonRelation);
		rootPersonRelation.setDbAccess(dbAccess);

		final RestRelation<Person, ?> personGeoLocationsRelation = new PersonGeoLocationsRelation("geoLocations", geoLocationsEntity);
		personGeoLocationsRelation.setDbAccess(dbAccess);
		personEntity.addRelation(personGeoLocationsRelation);

		final RestRelation<GeoLocations, ?> geoLocationsGeoLocationsRelation = new GeoLocationsGeoLocationsRelation("geoLocations", geoLocationEntity);
		geoLocationsGeoLocationsRelation.setDbAccess(dbAccess);
		geoLocationsEntity.addRelation(geoLocationsGeoLocationsRelation);
	}

	@Test
	// create Person
	public void testPostPerson() {
		final HttpMethod method = new PostMethod(rootEntity);
		method.setPayload("name" , "Hallvard", "email" , "hal@ntnu.no");
		final Object result = method.doMethod("persons");
		Assert.assertEquals("name=Hallvard;email=hal@ntnu.no", dbAccessRecord.get("createPerson"));
		Assert.assertTrue(result instanceof Person);
	}

	@Test
	// read Person
	public void testGetPerson() {
		final Person person = dbAccess.createPerson("Hallvard", "hal@ntnu.no");
		final HttpMethod method = new GetMethod(rootEntity);
		final Object result = method.doMethod("persons");
		Assert.assertEquals(false, dbAccessRecord.get("getAllPersons"));
		Assert.assertTrue(result instanceof Collection);
		final Collection<?> persons = (Collection<?>) result;
		Assert.assertEquals(1, persons.size());
		Assert.assertEquals(person, persons.iterator().next());
	}

	@Test
	// update Person
	public void testPutPerson() {
		final Person person = dbAccess.createPerson("Hallvard", "hal@ntnu.no");
		final HttpMethod method = new PutMethod(rootEntity);
		final int id = 1;
		method.setPayload("name" , "Hallvard Trætteberg", "email" , "hallvard.traetteberg@gmail.com");
		final Object result = method.doMethod("persons", String.valueOf(id));
		Assert.assertEquals("name=Hallvard Trætteberg;email=hallvard.traetteberg@gmail.com", dbAccessRecord.get("updatePersonData"));
		Assert.assertTrue(result instanceof Person);
		Assert.assertEquals("Hallvard Trætteberg", person.getName());
		Assert.assertEquals("hallvard.traetteberg@gmail.com", person.getEmail());
	}

	@Test
	// delete Person
	public void testDeletePerson() {
		dbAccess.createPerson("Hallvard", "hal@ntnu.no");
		final HttpMethod method = new DeleteMethod(rootEntity);
		method.doMethod("persons", "Hallvard");
		Assert.assertEquals("Hallvard", dbAccessRecord.get("deletePerson"));
		Assert.assertTrue(dbAccess.getAllPersons(false).isEmpty());
	}

	@Test
	// create GeoLocations
	public void testPostGeoLocations() {
		dbAccess.createPerson("Hallvard", "hal@ntnu.no");
		final HttpMethod method = new PostMethod(rootEntity);
		method.setPayload(
				"name" , "geoLocs1",
				"description" , "my first geo-location",
				"date", LocalDate.of(2018, 3, 14).toString(),
				"time", LocalTime.of(11, 14).toString(),
				"zone", "Europe/Oslo",
				"tags", "tag1,tag2"
				);
		final Object result = method.doMethod("persons", "Hallvard", "geoLocations");
		Assert.assertEquals(1, dbAccessRecord.get("createGeoLocations"));
		Assert.assertTrue(result instanceof GeoLocations);
		final GeoLocations geoLocations = (GeoLocations) result;
		Assert.assertEquals("geoLocs1", geoLocations.getName());
		Assert.assertEquals("my first geo-location", geoLocations.getDescription());
		Assert.assertEquals(LocalDate.of(2018, 3, 14), geoLocations.getDate());
		Assert.assertEquals(LocalTime.of(11, 14), geoLocations.getTime());
		Assert.assertEquals(ZoneId.of("Europe/Oslo"), geoLocations.getZone());
		Assert.assertEquals("tag1,tag2", geoLocations.getTags(null, ",", null));
	}

	@Test
	// read GeoLocations
	public void testGetGeoLocations() {
		final Person person = dbAccess.createPerson("Hallvard", "hal@ntnu.no");
		dbAccess.createGeoLocations(person);
		dbAccess.createGeoLocations(person);
		final HttpMethod method = new GetMethod(rootEntity);
		final Object result = method.doMethod("persons", "Hallvard", "geoLocations");
		Assert.assertEquals(false, dbAccessRecord.get("getGeoLocations"));
		Assert.assertTrue(result instanceof Collection<?>);
		Assert.assertEquals(2, ((Collection<?>) result).size());
	}

	@Test
	// read GeoLocations
	public void testGetGeoLocations1() {
		final Person person = dbAccess.createPerson("Hallvard", "hal@ntnu.no");
		final GeoLocations geoLocations1 = dbAccess.createGeoLocations(person);
		final GeoLocations geoLocations2 = dbAccess.createGeoLocations(person);
		final HttpMethod method = new GetMethod(rootEntity);
		final Object result0 = method.doMethod("persons", "Hallvard", "geoLocations", "0");
		Assert.assertEquals(false, dbAccessRecord.get("getGeoLocations"));
		Assert.assertSame(geoLocations1, result0);
		final Object result1 = method.doMethod("persons", "Hallvard", "geoLocations", "1");
		Assert.assertEquals(false, dbAccessRecord.get("getGeoLocations"));
		Assert.assertSame(geoLocations2, result1);
		final Object result2m = method.doMethod("persons", "Hallvard", "geoLocations", "-2");
		Assert.assertEquals(false, dbAccessRecord.get("getGeoLocations"));
		Assert.assertSame(geoLocations1, result2m);
		final Object result1m = method.doMethod("persons", "Hallvard", "geoLocations", "-1");
		Assert.assertEquals(false, dbAccessRecord.get("getGeoLocations"));
		Assert.assertSame(geoLocations2, result1m);
	}

	@Test
	// update GeoLocations
	public void testUpdateGeoLocations() {
		final Person person = dbAccess.createPerson("Hallvard", "hal@ntnu.no");
		final GeoLocations geoLocations1 = dbAccess.createGeoLocations(person);
		geoLocations1.setName("geoLocs1");
		geoLocations1.setDescription("my first geo-location");
		geoLocations1.setDate(LocalDate.of(2018, 3, 14));
		geoLocations1.setTime(LocalTime.of(11, 14));
		geoLocations1.setZone("Europe/Oslo");
		geoLocations1.setTags("tag1");
		final HttpMethod method = new PutMethod(rootEntity);
		method.setPayload(
				"name" , "geoLocs2",
				"description" , "my second geo-location",
				"date", LocalDate.of(2018, 3, 15).toString(),
				"time", LocalTime.of(11, 15).toString(),
				"zone", "Europe/Paris",
				"tags", "tag2"
				);
		method.doMethod("persons", "Hallvard", "geoLocations", "0");
		Assert.assertEquals("geoLocs2", dbAccessRecord.get("updateGeoLocationsData"));
		Assert.assertEquals("geoLocs2", geoLocations1.getName());
		Assert.assertEquals("my second geo-location", geoLocations1.getDescription());
		Assert.assertEquals(LocalDate.of(2018, 3, 15), geoLocations1.getDate());
		Assert.assertEquals(LocalTime.of(11, 15), geoLocations1.getTime());
		Assert.assertEquals(ZoneId.of("Europe/Paris"), geoLocations1.getZone());
		Assert.assertEquals("tag2", geoLocations1.getTags(null, ",", null));
	}


	@Test
	// delete Person
	public void testDeleteGeoLocations() {
		final Person person = dbAccess.createPerson("Hallvard", "hal@ntnu.no");
		final GeoLocations geoLocations1 = dbAccess.createGeoLocations(person);
		geoLocations1.setName("geoLoc1");
		final GeoLocations geoLocations2 = dbAccess.createGeoLocations(person);
		geoLocations2.setName("geoLoc2");
		final HttpMethod method = new DeleteMethod(rootEntity);
		method.doMethod("persons", "Hallvard", "geoLocations", "-2");
		Assert.assertEquals("geoLoc1", dbAccessRecord.get("deleteGeoLocations"));
		Assert.assertEquals(1, person.getGeoLocations((String[]) null).size());
		method.doMethod("persons", "Hallvard", "geoLocations", "0");
		Assert.assertEquals("geoLoc2", dbAccessRecord.get("deleteGeoLocations"));
		Assert.assertEquals(0, person.getGeoLocations((String[]) null).size());
	}
}
