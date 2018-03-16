package tdt4140.gr1800.app.db;

import java.sql.Connection;
import java.sql.Date;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Time;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.Collection;
import java.util.Scanner;

import tdt4140.gr1800.app.core.GeoLocation;
import tdt4140.gr1800.app.core.GeoLocations;
import tdt4140.gr1800.app.core.Person;

public class DbAccessImpl extends AbstractDbAccessImpl {

	private final DbAccessHelper helper;

	private DbAccessImpl(final DbAccessHelper helper) {
		this.helper = helper;
	}

	public DbAccessImpl(final Connection dbConnection) {
		this(new DbAccessHelper(dbConnection));
	}

	public DbAccessImpl(final String connectionUri, final String user, final String pass) throws SQLException {
		this(DriverManager.getConnection(connectionUri, user, pass));
	}

	public DbAccessImpl(final String connectionUri) throws SQLException {
		this(connectionUri, "SA", "");
	}

	//

	public void executeStatements(final String path, final boolean cleanUp) throws SQLException {
		final Statement dbStatement = helper.getDbConnection().createStatement();
		final StringBuilder buffer = new StringBuilder();
		try (Scanner scanner = new Scanner(getClass().getResourceAsStream(path))) {
			while (scanner.hasNextLine()) {
				final String line = scanner.nextLine();
				final int pos = line.indexOf(";");
				if (pos >= 0) {
					buffer.append(line.substring(0, pos + 1));
					final String sql = buffer.toString();
					buffer.setLength(0);
					if (pos < line.length()) {
						buffer.append(line.substring(pos + 1));
					}
					if (cleanUp || (! sql.startsWith("DROP"))) {
						dbStatement.execute(sql);
					}
				} else {
					buffer.append(line);
					buffer.append("\n");
				}
			}
		}
	}

	//

	@Override
	public synchronized Person createPerson(final String name, final String email) {
		final Person person = super.createPerson(name, email);
		final int id = helper.executeDbInsertGettingIdentity(String.format("INSERT INTO person (name, email) VALUES ('%s', '%s')", name, email));
		personIds.set(person, id);
		return person;
	}

	protected Person createPerson(final int id, final String name, final String email) {
		final Person person = new Person();
		person.setName(name);
		person.setEmail(email);
		personIds.remove(id);
		personIds.set(person, id);
		return person;
	}

	@Override
	public Collection<Person> getAllPersons(final boolean refresh) {
		if (refresh) {
			personIds.clear();
			final ResultSet result = helper.executeQuery("SELECT id, name, email FROM person");
			try {
				while (result.next()) {
					final int id = result.getInt(1);
					final String name = result.getString(2), email = result.getString(3);
					createPerson(id, name, email);
				}
			} catch (final SQLException e) {
				helper.throwException(e);
			}
		}
		return super.getAllPersons(false);
	}

	@Override
	public Person getPerson(final int id, final boolean refresh) {
		Person person = null;
		if (! refresh) {
			person = super.getPerson(id, false);
		}
		if (person == null || refresh) {
			final ResultSet result = helper.executeQuery("SELECT id, name, email FROM person WHERE id = ?", id);
			try {
				if (result.next()) {
					final String name = result.getString(2), email = result.getString(3);
					person = createPerson(id, name, email);
				}
			} catch (final SQLException e) {
				helper.throwException(e);
			}
		}
		return person;
	}

	@Override
	public Person getPersonByName(final String name, final boolean refresh) {
		Person person = null;
		if (! refresh) {
			person = super.getPersonByName(name, false);
		}
		if (person == null || refresh) {
			final ResultSet result = helper.executeQuery("SELECT id, name, email FROM person WHERE name = ?", name);
			try {
				if (result.next()) {
					final int id = result.getInt(1);
					final String dbName = result.getString(2), email = result.getString(3);
					person = createPerson(id, dbName, email);
				}
			} catch (final SQLException e) {
				helper.throwException(e);
			}
		}
		return person;
	}

	@Override
	public Person getPersonByEmail(final String email, final boolean refresh) {
		Person person = null;
		if (! refresh) {
			person = super.getPersonByEmail(email, false);
		}
		if (person == null || refresh) {
			final ResultSet result = helper.executeQuery("SELECT id, name, email FROM person WHERE email = ?", email);
			try {
				if (result.next()) {
					final int id = result.getInt(1);
					final String name = result.getString(2), dbEmail = result.getString(3);
					person = createPerson(id, name, dbEmail);
				}
			} catch (final SQLException e) {
				helper.throwException(e);
			}
		}
		return person;
	}

	@Override
	public synchronized void updatePersonData(final Person person) {
		helper.executeDbStatement("UPDATE person SET name = ?, email = ? WHERE id = ?", person.getName(), person.getEmail(), getId(person));
	}

	@Override
	public synchronized void deletePerson(final Person person) {
		final int id = getId(person);
		super.deletePerson(person);
		helper.executeDbStatement("DELETE FROM person WHERE id = ?", id);
		// not needed with ON DELETE CASCADE set on foreign keys
		//		helper.executeDbStatement("DELETE FROM geoLocations WHERE ownerId = ?", person.getId());
		//		helper.executeDbStatement("DELETE FROM geoLocation WHERE ownerId = ?", person.getId());
	}

	//

	@Override
	public GeoLocations createGeoLocations(final Person owner) {
		final GeoLocations geoLocations = super.createGeoLocations(owner);
		final int id = helper.executeDbInsertGettingIdentity(String.format("INSERT INTO geoLocations (ownerId) VALUES ('%s')", getId(owner)));
		geoLocationsIds.set(geoLocations, id);
		return geoLocations;
	}

	private static enum TagOwnerType {
		GLS, // GeoLocations,
		GL1,  // GeoLocation,
	}

	@Override
	public Collection<GeoLocations> getGeoLocations(final Person owner, final boolean refresh) {
		final Collection<GeoLocations> existingGeoLocations = super.getGeoLocations(owner, false);
		if (refresh || existingGeoLocations.isEmpty()) {
			owner.removeGeolocations((String[]) null);
			geoLocationsIds.removeAll(existingGeoLocations);
			existingGeoLocations.clear();
			final int ownerId = getId(owner);
			final ResultSet result = helper.executeQuery("SELECT id, path, name, description, date, time, zone FROM geoLocations WHERE ownerId = ?", ownerId);
			try {
				while (result.next()) {
					final int id = result.getInt(1);
					final boolean path = result.getBoolean(2);
					final String name = result.getString(3), description = result.getString(4);
					final GeoLocations geoLocations = new GeoLocations(owner);
					owner.addGeolocations(geoLocations);
					geoLocations.setPath(path);
					geoLocations.setName(name);
					geoLocations.setDescription(description);
					final Date date = result.getDate(5);
					final Time time = result.getTime(6);
					final String zone = result.getString(7);
					geoLocations.setDate(date != null ? date.toLocalDate() : null);
					geoLocations.setTime(time != null ? time.toLocalTime() : null);
					geoLocations.setZone(zone != null ? ZoneId.of(zone) : null);
					existingGeoLocations.add(geoLocations);
					geoLocationsIds.set(geoLocations, id);
					final ResultSet tagResults = helper.executeQuery(String.format("SELECT tag FROM tag WHERE ownerId = ? AND ownerType = '%s'", TagOwnerType.GLS), id);
					while (tagResults.next()) {
						geoLocations.addTags(tagResults.getString(1));
					}
				}
			} catch (final SQLException e) {
				helper.throwException(e);
			}
			final ResultSet tagResult = helper.executeQuery(String.format("SELECT geoLocations.id, tag.tag FROM geoLocations, tag WHERE geoLocations.ownerId = ? AND tag.ownerId = geoLocations.id AND ownerType = '%s'", TagOwnerType.GLS), ownerId);
			try {
				while (tagResult.next()) {
					final int geoLocationsId = tagResult.getInt(1);
					final String tag = tagResult.getString(2);
					final GeoLocations geoLocations = geoLocationsIds.get(geoLocationsId);
					if (geoLocations != null) {
						geoLocations.addTags(tag);
					}
				}
			} catch (final SQLException e) {
				helper.throwException(e);
			}
		}
		return existingGeoLocations;
	}

	@Override
	public void updateGeoLocationsData(final GeoLocations geoLocations) {
		final boolean path = geoLocations.isPath();
		final String name = geoLocations.getName(), desc = geoLocations.getDescription();
		final LocalDate date = geoLocations.getDate();
		final LocalTime time = geoLocations.getTime();
		final String zone = (geoLocations.getZone() != null ? geoLocations.getZone().getId() : null);
		final int ownerId = getId(geoLocations);
		helper.executeDbStatement("UPDATE geoLocations SET path = ?, name = ?, description = ?, date = ?, time = ?, zone = ? WHERE id = ?", path, name, desc, Date.valueOf(date), Time.valueOf(time), zone, ownerId);
		deleteTags(ownerId, TagOwnerType.GLS);
		String insertStatement = "INSERT INTO tag (ownerId, ownerType, tag) VALUES ";
		final String[] tags = geoLocations.getTags();
		for (int i = 0; i < tags.length; i++) {
			if (i > 0) {
				insertStatement += ", ";
			}
			insertStatement += String.format("(%s, '%s', '%s')", ownerId, TagOwnerType.GLS, tags[i]);
		}
		helper.executeDbStatement(insertStatement);
	}

	protected void deleteTags(final int ownerId, final TagOwnerType ownerType) {
		helper.executeDbStatement(String.format("DELETE FROM tag WHERE ownerId = ? AND ownerType = '%s'", ownerType), ownerId);
	}

	@Override
	public void deleteGeoLocations(final GeoLocations geoLocations) {
		final int ownerId = getId(geoLocations);
		super.deleteGeoLocations(geoLocations);
		helper.executeDbStatement("DELETE FROM geoLocations WHERE id = ?", ownerId);
		deleteTags(ownerId, TagOwnerType.GLS);
	}

	//

	@Override
	public void updateGeoLocationData(final GeoLocations geoLocations, final GeoLocation geoLocation) {
		throw new UnsupportedOperationException("NYI");
	}
}
