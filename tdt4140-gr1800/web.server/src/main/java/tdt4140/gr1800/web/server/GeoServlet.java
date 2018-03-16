package tdt4140.gr1800.web.server;

import java.io.IOException;
import java.io.OutputStream;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.Iterator;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;

import tdt4140.gr1800.app.core.GeoLocation;
import tdt4140.gr1800.app.core.GeoLocations;
import tdt4140.gr1800.app.core.LatLong;
import tdt4140.gr1800.app.core.Person;
import tdt4140.gr1800.app.db.DbAccessImpl;
import tdt4140.gr1800.app.db.IDbAccess;
import tdt4140.gr1800.app.json.GeoLocationDeserializer;
import tdt4140.gr1800.app.json.GeoLocationSerializer;
import tdt4140.gr1800.app.json.GeoLocationsDeserializer;
import tdt4140.gr1800.app.json.GeoLocationsSerializer;
import tdt4140.gr1800.app.json.LatLongDeserializer;
import tdt4140.gr1800.app.json.LatLongSerializer;
import tdt4140.gr1800.app.json.PersonDeserializer;
import tdt4140.gr1800.app.json.PersonSerializer;

public class GeoServlet extends HttpServlet {

	private IDbAccess dbAccess;
	private RestEntity<Void> rootEntity;
	private ObjectMapper objectMapper;

	@Override
	public void init() throws ServletException {
		String dbConnectionUrl = "jdbc:hsqldb:mem:" + getClass().getName();
		final String dbConnectionParam = getInitParameter("dbConnectionURL");
		if (dbConnectionParam != null) {
			dbConnectionUrl = dbConnectionParam;
		}
		try {
			final DbAccessImpl dbAccess = new DbAccessImpl(dbConnectionUrl);
			dbAccess.executeStatements("schema.sql", false);
			this.dbAccess = dbAccess;
		} catch (final SQLException e) {
			throw new ServletException("Error when initializing database connection (" + dbConnectionUrl + "): " + e, e);
		}

		rootEntity = new RestEntity<>();
		final RestEntity<Person> personEntity = new RestEntity<>();
		final RestEntity<GeoLocations> geoLocationsEntity = new RestEntity<>();
		final RestEntity<GeoLocation> geoLocationEntity = new RestEntity<>();

		final RestRelation<Void, Person> rootPersonRelation = new RootPersonsRelation("persons", personEntity);
		rootPersonRelation.setDbAccess(dbAccess);
		rootEntity.addRelation(rootPersonRelation);
		final RestRelation<Person, ?> personGeoLocationsRelation = new PersonGeoLocationsRelation("geoLocations", geoLocationsEntity);
		personGeoLocationsRelation.setDbAccess(dbAccess);
		personEntity.addRelation(personGeoLocationsRelation);
		final RestRelation<GeoLocations, ?> geoLocationsGeoLocationsRelation = new GeoLocationsGeoLocationsRelation("geoLocations", geoLocationEntity);
		geoLocationsGeoLocationsRelation.setDbAccess(dbAccess);
		geoLocationsEntity.addRelation(geoLocationsGeoLocationsRelation);

		objectMapper = new ObjectMapper();
		final SimpleModule module = new SimpleModule();
		module.addSerializer(new LatLongSerializer());
		module.addSerializer(new GeoLocationSerializer());
		module.addSerializer(new GeoLocationsSerializer());
		final PersonSerializer personSerializer = new PersonSerializer();
		personSerializer.setIdProvider(dbAccess.getPersonIdProvider());
		module.addSerializer(personSerializer);
		module.addDeserializer(LatLong.class, new LatLongDeserializer());
		module.addDeserializer(GeoLocation.class, new GeoLocationDeserializer());
		module.addDeserializer(GeoLocations.class, new GeoLocationsDeserializer());
		module.addDeserializer(Person.class, new PersonDeserializer());
		objectMapper.registerModule(module);
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
	// persons/<id>/geoLocations/<num>/geoLocations/<num>: Get a specific GeoLocations object

	@Override
	protected void doGet(final HttpServletRequest request, final HttpServletResponse response) throws ServletException, IOException {
		final HttpMethod method = new GetMethod(rootEntity);
		final Object result = method.doMethod(getPathSegments(request));
		if (result == null) {
			response.setStatus(HttpServletResponse.SC_NOT_FOUND);
		} else {
			respondWithJson(response, result);
		}
	}

	protected void respondWithJson(final HttpServletResponse response, final Object result) {
		response.setContentType("application/json");
		response.setStatus(HttpServletResponse.SC_OK);
		try (OutputStream output = response.getOutputStream()) {
			objectMapper.writerWithDefaultPrettyPrinter().writeValue(output, result);
		} catch (final Exception e) {
			response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
		}
	}

	protected Iterator<String> getPathSegments(final HttpServletRequest request) throws ServletException {
		final String path = request.getPathInfo();
		if (path == null) {
			throw new ServletException("Path cannot be empty");
		}
		final Iterator<String> segments = Arrays.asList(path.split("\\/")).iterator();
		return segments;
	}

	// POST variants
	// persons: Create a new Person object, with properties in the payload
	// persons/<id>: Not allowed
	// persons/<id>/geoLocations: Create a new GeoLocations object, with properties in the payload
	// persons/<id>/geoLocations/<num>: Not allowed
	// persons/<id>/geoLocations/<num>/geoLocations: Create a new GeoLocation object, with properties in the payload
	// persons/<id>/geoLocations/<num>/geoLocations/<num>: Not allowed

	@Override
	protected void doPost(final HttpServletRequest request, final HttpServletResponse response) throws ServletException, IOException {
		final HttpMethod method = new PostMethod(rootEntity);
		final JsonNode jsonNode = objectMapper.readTree(request.getInputStream());
		method.setPayload(jsonNode);
		final Object result = method.doMethod(getPathSegments(request));
		if (result == null) {
			response.setStatus(HttpServletResponse.SC_NOT_FOUND);
		} else {
			respondWithJson(response, result);
		}
	}

	// PUT variants
	// persons: Not allowed
	// persons/<id>: Update specific Person object
	// persons/<id>/geoLocations: Not allowed
	// persons/<id>/geoLocations/<num>: Update specific GeoLocations object
	// persons/<id>/geoLocations/<num>/geoLocations: Not allowed
	// persons/<id>/geoLocations/<num>/geoLocations/<num>: Update specific GeoLocations object

	@Override
	protected void doPut(final HttpServletRequest request, final HttpServletResponse response) throws ServletException, IOException {
		final HttpMethod method = new PutMethod(rootEntity);
		final JsonNode jsonNode = objectMapper.readTree(request.getInputStream());
		method.setPayload(jsonNode);
		final Object result = method.doMethod(getPathSegments(request));
		if (result == null) {
			response.setStatus(HttpServletResponse.SC_NOT_FOUND);
		} else {
			respondWithJson(response, result);
		}
	}

	// DELETE variants
	// persons: Not allowed
	// persons/<id>: Delete specific Person object
	// persons/<id>/geoLocations: Delete all GeoLocations objects?
	// persons/<id>/geoLocations/<num>: Delete specific GeoLocations object
	// persons/<id>/geoLocations/<num>/geoLocations: Delete all GeoLocation objects?
	// persons/<id>/geoLocations/<num>/geoLocations/<num>: Delete specific GeoLocation object

	@Override
	protected void doDelete(final HttpServletRequest request, final HttpServletResponse response) throws ServletException, IOException {
		final HttpMethod method = new DeleteMethod(rootEntity);
		final Object result = method.doMethod(getPathSegments(request));
		if (result == null) {
			response.setStatus(HttpServletResponse.SC_NOT_FOUND);
		} else {
			respondWithJson(response, result);
		}
	}
}
