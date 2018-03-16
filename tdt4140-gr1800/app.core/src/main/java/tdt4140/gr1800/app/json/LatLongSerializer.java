package tdt4140.gr1800.app.json;

import java.io.IOException;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;

import tdt4140.gr1800.app.core.LatLong;

public class LatLongSerializer extends GeoLocatedSerializer<LatLong> {

	public LatLongSerializer() {
		super(LatLong.class);
	}

	@Override
	public void serialize(LatLong geoLocated, JsonGenerator jsonGen, SerializerProvider serProvider) throws IOException {
		jsonGen.writeStartObject();
		super.serialize(geoLocated, jsonGen);
		jsonGen.writeEndObject();
	}
}
