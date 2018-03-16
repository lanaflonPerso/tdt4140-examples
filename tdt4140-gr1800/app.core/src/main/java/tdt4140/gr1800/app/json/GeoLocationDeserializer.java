package tdt4140.gr1800.app.json;

import java.io.IOException;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import tdt4140.gr1800.app.core.GeoLocation;
import tdt4140.gr1800.app.core.GeoLocations;

public class GeoLocationDeserializer extends StdDeserializer<GeoLocation> {

	public GeoLocationDeserializer() {
		super(GeoLocations.class);
	}

	@Override
	public GeoLocation deserialize(final JsonParser jsonParser, final DeserializationContext deserContext) throws IOException, JsonProcessingException {
		final JsonNode jsonNode = jsonParser.getCodec().readTree(jsonParser);
		return deserialize(jsonNode);
	}

	private final TimedTaggedImplDeserializer timedTaggedImplDeserializer = new TimedTaggedImplDeserializer();
	private final LatLongDeserializer latLongDeserializer = new LatLongDeserializer();

	GeoLocation deserialize(final JsonNode jsonNode) throws JsonProcessingException {
		if (jsonNode instanceof ObjectNode) {
			final ObjectNode objectNode = (ObjectNode) jsonNode;
			final GeoLocation geoLocation = new GeoLocation();
			if (objectNode.has(GeoLocationsSerializer.NAME_FIELD_NAME)) {
				final String name = objectNode.get(GeoLocationsSerializer.NAME_FIELD_NAME).asText();
				geoLocation.setName(name);
			}
			if (objectNode.has(GeoLocationsSerializer.DESCRIPTION_FIELD_NAME)) {
				final String description = objectNode.get(GeoLocationsSerializer.DESCRIPTION_FIELD_NAME).asText();
				geoLocation.setDescription(description);
			}
			if (objectNode.has("location")) {
				final JsonNode locationNode = objectNode.get("location");
				geoLocation.setLatLong(latLongDeserializer.deserialize(locationNode));
			} else {
				geoLocation.setLatLong(latLongDeserializer.deserialize(objectNode));
			}
			if (objectNode.has(GeoLocationSerializer.ELEVATION_FIELD_NAME)) {
				final int elevation = objectNode.get(GeoLocationSerializer.ELEVATION_FIELD_NAME).asInt();
				geoLocation.setElevation(elevation);
			}
			timedTaggedImplDeserializer.deserialize(jsonNode, geoLocation);
			return geoLocation;
		} else if (jsonNode instanceof ArrayNode) {
			final GeoLocation geoLocation = new GeoLocation();
			geoLocation.setLatLong(latLongDeserializer.deserialize(jsonNode));
			return geoLocation;
		}
		return null;
	}
}
