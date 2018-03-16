package tdt4140.gr1800.app.json;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.Collection;
import java.util.List;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;

import tdt4140.gr1800.app.core.GeoLocation;
import tdt4140.gr1800.app.core.GeoLocations;
import tdt4140.gr1800.app.core.GeoLocationsStreamPersistence;
import tdt4140.gr1800.app.core.LatLong;
import tdt4140.gr1800.app.core.Person;

public class GeoLocationsPersistence implements GeoLocationsStreamPersistence {

	private final ObjectMapper objectMapper;

	public GeoLocationsPersistence() {
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
	}

	@Override
	public Collection<GeoLocations> loadLocations(final InputStream inputStream) throws Exception {
		return objectMapper.readValue(inputStream, objectMapper.getTypeFactory().constructCollectionType(List.class, GeoLocations.class));
	}

	@Override
	public void saveLocations(final Collection<GeoLocations> geoLocations, final OutputStream outputStream) throws Exception {
		objectMapper.writeValue(outputStream, geoLocations);
	}
}
