package tdt4140.gr1800.web.server.jaxrs;

import java.util.Collection;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.ext.ContextResolver;
import javax.ws.rs.ext.Providers;

import com.fasterxml.jackson.databind.ObjectMapper;

import tdt4140.gr1800.app.core.GeoLocations;
import tdt4140.gr1800.app.core.Person;
import tdt4140.gr1800.app.db.IDbAccess;

@Path("geo")
public class GeoService {

	@Inject
	IDbAccess dbAccess;

	protected IDbAccess getDbAccess() {
		return dbAccess;
	}

	@Context
	private Providers providers;

	@PostConstruct
	protected void ensureIdProvider() {
		final ContextResolver<ObjectMapper> resolver = providers.getContextResolver(ObjectMapper.class, MediaType.APPLICATION_JSON_TYPE);
		if (resolver instanceof GeoObjectMapperProvider) {
			((GeoObjectMapperProvider) resolver).setPersonIdProvider(getDbAccess().getPersonIdProvider());
		}
	}

	// REST URL structure, according to https://blog.mwaysolutions.com/2014/06/05/10-best-practices-for-better-restful-api/
	// persons/<id>/geoLocations/<num>/geoLocations/<num>

	// GET variants
	// persons: Get all Person objects. Do we allow that? Should we return a list of <id> values or all the person entities (with some subset of properties)
	// persons/<id>: Get a specific Person object
	// persons/name or email: Get a specific Person object, with the provided name or email (with a '@')
	// persons/<id>/geoLocations: Get all the GeoLocations objects, with (some subset of) properties
	// persons/<id>/geoLocations/<num>: Get a specific GeoLocations object
	// persons/<id>/geoLocations/<num>/geoLocations: Get all GeoLocation objects, with (some subset of) properties
	// persons/<id>/geoLocations/<num>/geoLocations/<num>: Get a specific GeoLocation object

	@GET
	@Path("/persons")
	@Produces(MediaType.APPLICATION_JSON)
	public Collection<Person> getPersons() {
		//		System.out.println("getPersons");
		return getDbAccess().getAllPersons(false);
	}

	@GET
	@Path("/persons/{id}")
	@Produces(MediaType.APPLICATION_JSON)
	public Person getPerson(@PathParam("id") final String idOrKey) {
		//		System.out.println("getPerson: " + idOrKey);
		Person person = null;
		try {
			final Integer num = Integer.valueOf(idOrKey);
			person = getDbAccess().getPerson(num, false);
		} catch (final NumberFormatException e) {
			person = (idOrKey.contains("@") ? getDbAccess().getPersonByEmail(idOrKey, false) : getDbAccess().getPersonByName(idOrKey, false));
		}
		return person;
	}

	@GET
	@Path("/persons/{id}/geoLocations")
	@Produces(MediaType.APPLICATION_JSON)
	public Collection<GeoLocations> getGeoLocations(@PathParam("id") final String idOrKey) throws RuntimeException {
		//		System.out.println("getGeoLocations: " + idOrKey);
		final Person person = getDbAccess().getPerson(Integer.valueOf(idOrKey), false);
		return getDbAccess().getGeoLocations(person, false);
	}

	@GET
	@Path("/persons/{id}/geoLocations/{num}")
	@Produces(MediaType.APPLICATION_JSON)
	public GeoLocations getGeoLocation(@PathParam("id") final int id, @PathParam("num") final int num) throws RuntimeException {
		//		System.out.println("getGeoLocation: " + id + "," + num);
		final Person person = getDbAccess().getPerson(id, false);
		final int geoNum = 0;
		for (final GeoLocations geoLocations : getDbAccess().getGeoLocations(person, false)) {
			if (geoNum == num) {
				return geoLocations;
			}
		}
		return null;
	}

	// POST variants
	// persons: Create a new Person object, with properties in the payload
	// persons/<id>: Not allowed
	// persons/<id>/geoLocations: Create a new GeoLocations object, with properties in the payload
	// persons/<id>/geoLocations/<num>: Not allowed
	// persons/<id>/geoLocations/<num>/geoLocations: Create a new GeoLocation object, with properties in the payload
	// persons/<id>/geoLocations/<num>/geoLocations/<num>: Not allowed

	@POST
	@Path("/persons")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Person createPerson(final Person person, @Context final Providers providers) {
		final Person dbPerson = getDbAccess().createPerson(person.getName(), person.getEmail());
		return dbPerson;
	}


	// PUT variants
	// persons: Not allowed
	// persons/<id>: Update specific Person object
	// persons/<id>/geoLocations: Not allowed
	// persons/<id>/geoLocations/<num>: Update specific GeoLocations object
	// persons/<id>/geoLocations/<num>/geoLocations: Not allowed
	// persons/<id>/geoLocations/<num>/geoLocations/<num>: Update specific GeoLocation object

	// DELETE variants
	// persons: Not allowed
	// persons/<id>: Delete specific Person object
	// persons/<id>/geoLocations: Delete all GeoLocations objects?
	// persons/<id>/geoLocations/<num>: Delete specific GeoLocations object
	// persons/<id>/geoLocations/<num>/geoLocations: Delete all GeoLocation objects?
	// persons/<id>/geoLocations/<num>/geoLocations/<num>: Delete specific GeoLocation object
}
