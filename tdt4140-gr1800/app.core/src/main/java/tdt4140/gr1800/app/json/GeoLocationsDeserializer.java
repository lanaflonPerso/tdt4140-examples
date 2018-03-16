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

public class GeoLocationsDeserializer extends StdDeserializer<GeoLocations> {

	public GeoLocationsDeserializer() {
		super(GeoLocations.class);
	}

	@Override
	public GeoLocations deserialize(final JsonParser jsonParser, final DeserializationContext deserContext) throws IOException, JsonProcessingException {
		final JsonNode jsonNode = jsonParser.getCodec().readTree(jsonParser);
		return deserialize(jsonNode, deserContext);
	}

	private final GeoLocationDeserializer geoLocationDeserializer = new GeoLocationDeserializer();

	GeoLocations deserialize(final JsonNode jsonNode, final DeserializationContext deserContext) throws JsonProcessingException {
		if (jsonNode instanceof ObjectNode) {
			final ObjectNode objectNode = (ObjectNode) jsonNode;
			final String name = objectNode.get(GeoLocationsSerializer.NAME_FIELD_NAME).asText();
			final GeoLocations geoLocations = new GeoLocations(name);
			final JsonNode pathNode = objectNode.get(GeoLocationsSerializer.PATH_FIELD_NAME);
			geoLocations.setPath(pathNode != null && pathNode.asBoolean(false));
			final JsonNode locationsNode = objectNode.get(GeoLocationsSerializer.LOCATIONS_FIELD_NAME);
			if (locationsNode instanceof ArrayNode) {
				for (final JsonNode locationNode : (ArrayNode) locationsNode) {
					final GeoLocation geoLocation = geoLocationDeserializer.deserialize(locationNode);
					geoLocations.addLocation(geoLocation);
				}
			}
			return geoLocations;
		}
		return null;
	}
}
