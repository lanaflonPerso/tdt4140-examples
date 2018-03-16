package tdt4140.gr1800.app.db;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Collection;

import tdt4140.gr1800.app.core.GeoLocated;
import tdt4140.gr1800.app.core.GeoLocation;
import tdt4140.gr1800.app.core.GeoLocations;
import tdt4140.gr1800.app.core.Person;

public abstract class AbstractDbAccessImpl implements IDbAccess {

	protected IdMap<Person> personIds = new IdMap<Person>();
	protected IdMap<GeoLocations> geoLocationsIds = new IdMap<GeoLocations>();
	protected IdMap<GeoLocation> geoLocationIds = new IdMap<GeoLocation>();

	public IdProvider<Person> getPersonIdProvider() {
		return personIds;
	}

	public int getId(final Person person) {
		return personIds.getId(person);
	}

	public int getId(final GeoLocations geoLocations) {
		return geoLocationsIds.getId(geoLocations);
	}

	public int getId(final GeoLocation geoLocation) {
		return geoLocationIds.getId(geoLocation);
	}

	@Override
	public Person createPerson(final String name, final String email) {
		final Person person = new Person();
		person.setName(name);
		person.setEmail(email);
		return person;
	}

	@Override
	public GeoLocations createGeoLocations(final Person owner) {
		final GeoLocations geoLocations = new GeoLocations(owner);
		owner.addGeolocations(geoLocations);
		return geoLocations;
	}

	@Override
	public GeoLocation addGeoLocation(final GeoLocations geoLocations, final GeoLocated geoLoc, final int elevation, final LocalTime time) {
		final GeoLocation geoLocation = new GeoLocation();
		geoLocation.setLatLong(geoLoc.getLatLong());
		geoLocation.setElevation(elevation);
		geoLocation.setTime(time);
		geoLocations.addLocation(geoLoc);
		return geoLocation;
	}

	@Override
	public Collection<Person> getAllPersons(final boolean refresh) {
		return new ArrayList<Person>(personIds.get());
	}

	@Override
	public Person getPerson(final int id, final boolean refresh) {
		final Person person = personIds.get(id);
		return person;
	}

	@Override
	public Person getPersonByName(final String name, final boolean refresh) {
		for (final Person person : personIds.get()) {
			if (name == person.getName() || (name != null && name.equals(person.getName()))) {
				return person;
			}
		}
		return null;
	}

	@Override
	public Person getPersonByEmail(final String email, final boolean refresh) {
		for (final Person person : personIds.get()) {
			if (email == person.getEmail() || (email != null && email.equals(person.getEmail()))) {
				return person;
			}
		}
		return null;
	}

	@Override
	public Collection<GeoLocations> getGeoLocations(final Person owner, final boolean refresh) {
		return owner.getGeoLocations((String[]) null);
	}

	@Override
	public void deletePerson(final Person person) {
		personIds.remove(person);
	}

	@Override
	public void deleteGeoLocations(final GeoLocations geoLocations) {
		geoLocations.getOwner().removeGeolocations(geoLocations);
		geoLocationsIds.remove(geoLocations);
	}

	@Override
	public void deleteGeoLocation(final GeoLocations geoLocations, final GeoLocation geoLocation) {
		geoLocationIds.remove(geoLocation);
	}
}
