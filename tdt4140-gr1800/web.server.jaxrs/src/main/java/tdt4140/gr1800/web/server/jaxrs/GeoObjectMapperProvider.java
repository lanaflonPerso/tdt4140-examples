package tdt4140.gr1800.web.server.jaxrs;

import javax.ws.rs.Consumes;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.ext.ContextResolver;
import javax.ws.rs.ext.Provider;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;

import tdt4140.gr1800.app.core.GeoLocation;
import tdt4140.gr1800.app.core.GeoLocations;
import tdt4140.gr1800.app.core.LatLong;
import tdt4140.gr1800.app.core.Person;
import tdt4140.gr1800.app.db.IdProvider;
import tdt4140.gr1800.app.json.GeoLocationDeserializer;
import tdt4140.gr1800.app.json.GeoLocationSerializer;
import tdt4140.gr1800.app.json.GeoLocationsDeserializer;
import tdt4140.gr1800.app.json.GeoLocationsSerializer;
import tdt4140.gr1800.app.json.LatLongDeserializer;
import tdt4140.gr1800.app.json.LatLongSerializer;
import tdt4140.gr1800.app.json.PersonDeserializer;
import tdt4140.gr1800.app.json.PersonSerializer;

@Provider
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class GeoObjectMapperProvider implements ContextResolver<ObjectMapper> {

	private final ObjectMapper objectMapper;
	private final PersonSerializer personSerializer;

	public GeoObjectMapperProvider() {
		personSerializer = new PersonSerializer();
		final SimpleModule module = new SimpleModule()
				.addSerializer(new LatLongSerializer())
				.addSerializer(new GeoLocationSerializer())
				.addSerializer(new GeoLocationsSerializer())
				.addSerializer(personSerializer)
				.addDeserializer(LatLong.class, new LatLongDeserializer())
				.addDeserializer(GeoLocation.class, new GeoLocationDeserializer())
				.addDeserializer(GeoLocations.class, new GeoLocationsDeserializer())
				.addDeserializer(Person.class, new PersonDeserializer());
		objectMapper = new ObjectMapper()
				.registerModule(module);
	}

	public void setPersonIdProvider(final IdProvider<Person> idProvider) {
		personSerializer.setIdProvider(idProvider);
	}

	@Override
	public ObjectMapper getContext(final Class<?> type) {
		return objectMapper;
	}
}
