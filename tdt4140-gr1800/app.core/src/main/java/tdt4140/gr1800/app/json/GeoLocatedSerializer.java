package tdt4140.gr1800.app.json;

import java.io.IOException;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;

import tdt4140.gr1800.app.core.GeoLocated;

public class GeoLocatedSerializer<T extends GeoLocated> extends StdSerializer<T> {
	
	public static final String LONGITUDE_FIELD_NAME = "longitude";
	public static final String LATITUDE_FIELD_NAME = "latitude";

	public GeoLocatedSerializer(Class<T> clazz) {
		super(clazz);
	}

	protected void serialize(GeoLocated geoLocated, JsonGenerator jsonGen) throws IOException {
		jsonGen.writeFieldName(LATITUDE_FIELD_NAME);
		jsonGen.writeNumber(geoLocated.getLatitude());
		jsonGen.writeFieldName(LONGITUDE_FIELD_NAME);
		jsonGen.writeNumber(geoLocated.getLongitude());
	}

	@Override
	public void serialize(T value, JsonGenerator jsonGen, SerializerProvider provider) throws IOException {
		serialize(value, jsonGen);
	}
}
