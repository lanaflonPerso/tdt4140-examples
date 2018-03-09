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

	public int getId(Person person) {
		return personIds.getId(person);
	}

	public int getId(GeoLocations geoLocations) {
		return geoLocationsIds.getId(geoLocations);
	}

	public int getId(GeoLocation geoLocation) {
		return geoLocationIds.getId(geoLocation);
	}
	
	@Override
	public Person createPerson(String name, String email) {
		Person person = new Person();
		person.setName(name);
		person.setEmail(email);
		return person;
	}

	@Override
	public GeoLocations createGeoLocations(Person owner) {
		GeoLocations geoLocations = new GeoLocations(owner);
		owner.addGeolocations(geoLocations);
		return geoLocations;
	}

	@Override
	public GeoLocation addGeoLocation(GeoLocations geoLocations, GeoLocated geoLoc, int elevation, LocalTime time) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Collection<Person> getAllPersons(boolean refresh) {
		return new ArrayList<Person>(personIds.get());
	}

	@Override
	public Person getPerson(int id, boolean refresh) {
		Person person = personIds.get(id);
		return person;
	}

	@Override
	public Person getPersonByName(String name, boolean refresh) {
		for (Person person : personIds.get()) {
			if (name == person.getName() || (name != null && name.equals(person.getName()))) {
				return person;
			}
		}
		return null;
	}

	@Override
	public Person getPersonByEmail(String email, boolean refresh) {
		for (Person person : personIds.get()) {
			if (email == person.getEmail() || (email != null && email.equals(person.getEmail()))) {
				return person;
			}
		}
		return null;
	}

	@Override
	public Collection<GeoLocations> getGeoLocations(Person owner, boolean refresh) {
		Collection<GeoLocations> result = new ArrayList<>();
		for (GeoLocations geoLocations : geoLocationsIds.get()) {
			if (geoLocations.getOwner() == owner) {
				result.add(geoLocations);
			}
		}
		return result;
	}

	@Override
	public void updateGeoLocationsData(GeoLocations geoLocations) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void updateGeoLocationData(GeoLocations geoLocations, GeoLocations geoLocation) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void deletePerson(Person person) {
		personIds.remove(person);
	}

	@Override
	public void deleteGeoLocations(GeoLocations geoLocations) {
		geoLocations.getOwner().removeGeolocations(geoLocations);
		geoLocationsIds.remove(geoLocations);
	}

	@Override
	public void deleteGeoLocation(GeoLocations geoLocations, GeoLocation geoLocation) {
		geoLocationIds.remove(geoLocation);
	}
}
