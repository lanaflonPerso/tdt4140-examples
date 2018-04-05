package tdt4140.gr1800.app.db;

import java.time.LocalTime;
import java.util.Collection;

import tdt4140.gr1800.app.core.GeoLocated;
import tdt4140.gr1800.app.core.GeoLocation;
import tdt4140.gr1800.app.core.GeoLocations;
import tdt4140.gr1800.app.core.Person;

/*
 * class diagram:
 *
 * @startuml
 * class GeoLocationsOwner {
 * }
 *
 * class Person {
 * 	String name
 * 	String email
 * }
 *
 * class TimedTaggedImpl {
 *  LocalDate date
 *  LocalTime time
 *  ZoneId zone
 *  String[] tags
 * }
 *
 * GeoLocationsOwner <|-- Person
 *
 * GeoLocationsOwner *-- GeoLocations: owner
 *
 * class GeoLocations {
 * 	String name
 * 	String description
 *  LocalDate date
 *  LocalTime time
 *  ZoneId zone
 *  String[] tags
 * }
 *
 * GeoLocations --|> TimedTaggedImpl
 *
 * class GeoLocation {
 * 	int elevation
 * 	LocalTime time
 * 	String name
 * 	String description
 * }

 * GeoLocation --|> TimedTaggedImpl
 *
 * GeoLocations *-- GeoLocation
 * GeoLocation *-- LatLong
 * @enduml
 */

/*
 * ER diagram interface for our domain:
 *
 * @startuml
 * entity person {
 * * id INTEGER GENERATED
 * *	 name varchar(80)
 * *	 email varchar(80)
 * }
 *
 * entity geoLocations {
 * * id INTEGER GENERATED
 * 	path boolean
 * 	name varchar(80)
 *  description varchar(200)
 *  date date
 *  time time
 *  zone varchar(20)
 * }
 *
 * person --{ geoLocations: ownerId
 *
 * entity geoLocation {
 * * id INTEGER GENERATED
 * 	name varchar(80)
 *  description varchar(200)
 *  * latitude decimal
 *  * longitude decimal
 *  elevation int
 *  date date
 *  time time
 *  zone varchar(20)
 * }
 *
 * geoLocations --{ geoLocation: ownerId
 *
 * entity tags {
 *  * ownerType char(3)
 *  * tag varchar(15)
 * }
 *
 * geoLocations --{ tags: ownerId
 * geoLocation --{ tags: ownerId
 *
 * @enduml
 */
public interface IDbAccess {

	public IdProvider<Person> getPersonIdProvider();

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
	public void updateGeoLocationData(GeoLocations geoLocations, GeoLocation geoLocation);

	// Delete
	public void deletePerson(Person person);
	public void deleteGeoLocations(GeoLocations geoLocations);
	public void deleteGeoLocation(GeoLocations geoLocations, GeoLocation geoLocation);
}
