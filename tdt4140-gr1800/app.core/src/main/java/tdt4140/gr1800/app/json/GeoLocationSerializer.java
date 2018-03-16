package tdt4140.gr1800.app.json;

import java.io.IOException;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;

import tdt4140.gr1800.app.core.GeoLocation;

public class GeoLocationSerializer extends TimedTaggedSerializer<GeoLocation> {
	
	public static final String ELEVATION_FIELD_NAME = "elevation";

	public GeoLocationSerializer() {
		super(GeoLocation.class);
	}
	
	private GeoLocatedSerializer<GeoLocation> geoLocatedSerializer = new GeoLocatedSerializer<>(GeoLocation.class);

	@Override
	public void serialize(GeoLocation geoLocation, JsonGenerator jsonGen, SerializerProvider serProvider) throws IOException {
		jsonGen.writeStartObject();
		super.serialize(geoLocation, jsonGen);
		geoLocatedSerializer.serialize(geoLocation, jsonGen);
		if (geoLocation.getElevation() != 0) {
			jsonGen.writeFieldName(ELEVATION_FIELD_NAME);
			jsonGen.writeNumber(geoLocation.getElevation());
		}
		if (geoLocation.getName() != null) {
			jsonGen.writeFieldName(GeoLocationsSerializer.NAME_FIELD_NAME);
			jsonGen.writeString(geoLocation.getName());
		}
		if (geoLocation.getDescription() != null) {
			jsonGen.writeFieldName(GeoLocationsSerializer.DESCRIPTION_FIELD_NAME);
			jsonGen.writeString(geoLocation.getDescription());
		}
		jsonGen.writeEndObject();
	}
}
