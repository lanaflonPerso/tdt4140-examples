package tdt4140.gr1800.app.db;

import java.time.LocalTime;
import java.util.Collection;

import tdt4140.gr1800.app.core.GeoLocated;
import tdt4140.gr1800.app.core.GeoLocation;
import tdt4140.gr1800.app.core.GeoLocations;
import tdt4140.gr1800.app.core.GeoLocationsOwner;
import tdt4140.gr1800.app.core.Person;

/*
 * CRUD interface for our domain:
 * 
 * @startuml
 * class GeoLocationsOwner {
 * 	String id
 * }
 * class Person {
 * 	String name
 * 	String email
 * }
 * GeoLocationsOwner <|-- Person
 * class GeoLocations {
 * 	String name
 * 	String description
 * }
 * GeoLocationsOwner *-- GeoLocations: owner
 * class GeoLocation {
 * 	int elevation
 * 	LocalTime time
 * 	String name
 * 	String description
 * }
 * GeoLocations *-- GeoLocation
 * GeoLocation *-- LatLong
 * @enduml
 */
public interface IDbAccess {
	
	// Create
	public Person createPerson(String name, String email);
	public GeoLocations createGeoLocations(Person owner);
	public GeoLocation addGeoLocation(GeoLocations geoLocations, GeoLocated geoLoc, int elevation, LocalTime time);

	// Read
	public Collection<Person> getAllPersons(boolean refresh);
	public Person getPerson(int id, boolean refresh);
	public Person getPersonByName(String name, boolean refresh);
	public Person getPersonByEmail(String email, boolean refresh);
	
	public Collection<GeoLocations> getGeoLocations(Person owner, boolean refresh);
	
	// Update
	public void updatePersonData(Person person);
	public void updateGeoLocationsData(GeoLocations geoLocations);
	public void updateGeoLocationData(GeoLocations geoLocations, GeoLocations geoLocation);
	
	// Delete
	public void deletePerson(Person person);
	public void deleteGeoLocations(GeoLocations geoLocations);
	public void deleteGeoLocation(GeoLocations geoLocations, GeoLocation geoLocation);
}
