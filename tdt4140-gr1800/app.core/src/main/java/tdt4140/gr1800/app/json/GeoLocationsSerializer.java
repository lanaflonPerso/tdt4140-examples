package tdt4140.gr1800.app.json;

import java.io.IOException;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;

import tdt4140.gr1800.app.core.GeoLocated;
import tdt4140.gr1800.app.core.GeoLocations;

public class GeoLocationsSerializer extends StdSerializer<GeoLocations> {
	
	public static final String LOCATIONS_FIELD_NAME = "locations";
	public static final String PATH_FIELD_NAME = "path";
	public static final String NAME_FIELD_NAME = "name";
	public static final String DESCRIPTION_FIELD_NAME = "description";

	public GeoLocationsSerializer() {
		super(GeoLocations.class);
	}

	private TimedTaggedSerializer<GeoLocations> timedTaggedSerializer = new TimedTaggedSerializer<>(GeoLocations.class);
	
	@Override
	public void serialize(GeoLocations geoLocations, JsonGenerator jsonGen, SerializerProvider serProvider) throws IOException {
		jsonGen.writeStartObject();
		jsonGen.writeFieldName(NAME_FIELD_NAME);
		jsonGen.writeString(geoLocations.getName());
		if (geoLocations.getDescription() != null) {
			jsonGen.writeFieldName(DESCRIPTION_FIELD_NAME);
			jsonGen.writeString(geoLocations.getDescription());
		}
		jsonGen.writeFieldName(PATH_FIELD_NAME);
		jsonGen.writeBoolean(geoLocations.isPath());
		jsonGen.writeFieldName(LOCATIONS_FIELD_NAME);
		jsonGen.writeStartArray();
		for (GeoLocated geoLoc : geoLocations) {
			jsonGen.writeObject(geoLoc);
		}
		jsonGen.writeEndArray();
		timedTaggedSerializer.serialize(geoLocations, jsonGen);
		jsonGen.writeEndObject();
	}
}
