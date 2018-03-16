package tdt4140.gr1800.app.db;

import java.sql.Connection;
import java.sql.Date;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Time;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.Collection;

import tdt4140.gr1800.app.core.GeoLocation;
import tdt4140.gr1800.app.core.GeoLocations;
import tdt4140.gr1800.app.core.Person;

public class DbAccessImpl extends AbstractDbAccessImpl {

	private final DbAccessHelper helper;
	
	private DbAccessImpl(DbAccessHelper helper) {
		this.helper = helper;
	}

	public DbAccessImpl(Connection dbConnection) {
		this(new DbAccessHelper(dbConnection));
	}

	public DbAccessImpl(String connectionUri, String user, String pass) throws SQLException {
		this(DriverManager.getConnection(connectionUri, user, pass));
	}

	public DbAccessImpl(String connectionUri) throws SQLException {
		this(connectionUri, "SA", "");
	}
	
	//
	
	@Override
	public synchronized Person createPerson(String name, String email) {
		Person person = super.createPerson(name, email);
		int id = helper.executeDbInsertGettingIdentity(String.format("INSERT INTO person (name, email) VALUES ('%s', '%s')", name, email));
		personIds.set(person, id);
		return person;
	}

	protected Person createPerson(int id, String name, String email) {
		Person person = new Person();
		person.setName(name);
		person.setEmail(email);
		personIds.remove(id);
		personIds.set(person, id);
		return person;
	}

	@Override
	public Collection<Person> getAllPersons(boolean refresh) {
		if (refresh) {
			personIds.clear();
			ResultSet result = helper.executeQuery("SELECT id, name, email FROM person");
			try {
				while (result.next()) {
					int id = result.getInt(1);
					String name = result.getString(2), email = result.getString(3);
					createPerson(id, name, email);
				}
			} catch (SQLException e) {
				helper.throwException(e);
			}
		}
		return super.getAllPersons(false);
	}

	@Override
	public Person getPerson(int id, boolean refresh) {
		Person person = null;
		if (! refresh) {
			person = super.getPerson(id, false);
		}
		if (person == null || refresh) {
			ResultSet result = helper.executeQuery("SELECT id, name, email FROM person WHERE id = ?", id);
			try {
				if (result.next()) {
					String name = result.getString(2), email = result.getString(3);
					person = createPerson(id, name, email);
				}
			} catch (SQLException e) {
				helper.throwException(e);
			}
		}
		return person;
	}

	@Override
	public Person getPersonByName(String name, boolean refresh) {
		Person person = null;
		if (! refresh) {
			person = super.getPersonByName(name, false);
		}
		if (person == null || refresh) {
			ResultSet result = helper.executeQuery("SELECT id, name, email FROM person WHERE name = ?", name);
			try {
				if (result.next()) {
					int id = result.getInt(1);
					String dbName = result.getString(2), email = result.getString(3);
					person = createPerson(id, dbName, email);
				}
			} catch (SQLException e) {
				helper.throwException(e);
			}
		}
		return person;
	}

	@Override
	public Person getPersonByEmail(String email, boolean refresh) {
		Person person = null;
		if (! refresh) {
			person = super.getPersonByEmail(email, false);
		}
		if (person == null || refresh) {
			ResultSet result = helper.executeQuery("SELECT id, name, email FROM person WHERE email = ?", email);
			try {
				if (result.next()) {
					int id = result.getInt(1);
					String name = result.getString(2), dbEmail = result.getString(3);
					person = createPerson(id, name, dbEmail);
				}
			} catch (SQLException e) {
				helper.throwException(e);
			}
		}
		return person;
	}
	
	@Override
	public synchronized void updatePersonData(Person person) {
		helper.executeDbStatement("UPDATE person SET name = ?, email = ? WHERE id = ?", person.getName(), person.getEmail(), getId(person));
	}

	@Override
	public synchronized void deletePerson(Person person) {
		int id = getId(person);
		super.deletePerson(person);
		helper.executeDbStatement("DELETE FROM person WHERE id = ?", id);
		// not needed with ON DELETE CASCADE set on foreign keys
//		helper.executeDbStatement("DELETE FROM geoLocations WHERE ownerId = ?", person.getId());
//		helper.executeDbStatement("DELETE FROM geoLocation WHERE ownerId = ?", person.getId());
	}
	
	//

	@Override
	public GeoLocations createGeoLocations(Person owner) {
		GeoLocations geoLocations = super.createGeoLocations(owner);
		int id = helper.executeDbInsertGettingIdentity(String.format("INSERT INTO geoLocations (ownerId) VALUES ('%s')", getId(owner)));
		geoLocationsIds.set(geoLocations, id);
		return geoLocations;
	}

	private static enum TagOwnerType {
		GLS, // GeoLocations,
		GL1,  // GeoLocation,
	}

	@Override
	public Collection<GeoLocations> getGeoLocations(Person owner, boolean refresh) {
		Collection<GeoLocations> existingGeoLocations = super.getGeoLocations(owner, false);
		if (refresh || existingGeoLocations.isEmpty()) {
			owner.removeGeolocations((String[]) null);
			geoLocationsIds.removeAll(existingGeoLocations);
			existingGeoLocations.clear();
			int ownerId = getId(owner);
			ResultSet result = helper.executeQuery("SELECT id, path, name, description, date, time, zone FROM geoLocations WHERE ownerId = ?", ownerId);
			try {
				while (result.next()) {
					int id = result.getInt(1);
					boolean path = result.getBoolean(2);
					String name = result.getString(3), description = result.getString(4);
					GeoLocations geoLocations = new GeoLocations(owner);
					owner.addGeolocations(geoLocations);
					geoLocations.setPath(path);
					geoLocations.setName(name);
					geoLocations.setDescription(description);
					Date date = result.getDate(5);
					Time time = result.getTime(6);
					String zone = result.getString(7);
					geoLocations.setDate(date != null ? date.toLocalDate() : null);
					geoLocations.setTime(time != null ? time.toLocalTime() : null);
					geoLocations.setZone(zone != null ? ZoneId.of(zone) : null);
					existingGeoLocations.add(geoLocations);
					geoLocationsIds.set(geoLocations, id);
					ResultSet tagResults = helper.executeQuery(String.format("SELECT tag FROM tag WHERE ownerId = ? AND ownerType = '%s'", TagOwnerType.GLS), id);
					while (tagResults.next()) {
						geoLocations.addTags(tagResults.getString(1));
					}
				}
			} catch (SQLException e) {
				helper.throwException(e);
			}
			ResultSet tagResult = helper.executeQuery(String.format("SELECT geoLocations.id, tag.tag FROM geoLocations, tag WHERE geoLocations.ownerId = ? AND tag.ownerId = geoLocations.id AND ownerType = '%s'", TagOwnerType.GLS), ownerId);
			try {
				while (tagResult.next()) {
					int geoLocationsId = tagResult.getInt(1);
					String tag = tagResult.getString(2);
					GeoLocations geoLocations = geoLocationsIds.get(geoLocationsId);
					if (geoLocations != null) {
						geoLocations.addTags(tag);
					}
				}
			} catch (SQLException e) {
				helper.throwException(e);
			}
		}
		return existingGeoLocations;
	}
	
	@Override
	public void updateGeoLocationsData(GeoLocations geoLocations) {
		boolean path = geoLocations.isPath();
		String name = geoLocations.getName(), desc = geoLocations.getDescription();
		LocalDate date = geoLocations.getDate();
		LocalTime time = geoLocations.getTime();
		String zone = (geoLocations.getZone() != null ? geoLocations.getZone().getId() : null);
		int ownerId = getId(geoLocations);
		helper.executeDbStatement("UPDATE geoLocations SET path = ?, name = ?, description = ?, date = ?, time = ?, zone = ? WHERE id = ?", path, name, desc, Date.valueOf(date), Time.valueOf(time), zone, ownerId);
		deleteTags(ownerId, TagOwnerType.GLS);
		String insertStatement = "INSERT INTO tag (ownerId, ownerType, tag) VALUES ";
		String[] tags = geoLocations.getTags();
		for (int i = 0; i < tags.length; i++) {
			if (i > 0) {
				insertStatement += ", ";				
			}
			insertStatement += String.format("(%s, '%s', '%s')", ownerId, TagOwnerType.GLS, tags[i]);
		}
		helper.executeDbStatement(insertStatement);
	}

	protected void deleteTags(int ownerId, TagOwnerType ownerType) {
		helper.executeDbStatement(String.format("DELETE FROM tag WHERE ownerId = ? AND ownerType = '%s'", ownerType), ownerId);
	}

	@Override
	public void deleteGeoLocations(GeoLocations geoLocations) {
		int ownerId = getId(geoLocations);
		super.deleteGeoLocations(geoLocations);
		helper.executeDbStatement("DELETE FROM geoLocations WHERE id = ?", ownerId);
		deleteTags(ownerId, TagOwnerType.GLS);
	}
	
	//

	@Override
	public void updateGeoLocationData(GeoLocations geoLocations, GeoLocation geoLocation) {
		throw new UnsupportedOperationException("NYI");
	}
}
