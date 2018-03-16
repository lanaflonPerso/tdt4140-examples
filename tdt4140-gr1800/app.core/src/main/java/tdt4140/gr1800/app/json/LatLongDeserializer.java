package tdt4140.gr1800.app.json;

import java.io.IOException;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import tdt4140.gr1800.app.core.LatLong;

public class LatLongDeserializer extends StdDeserializer<LatLong> {

	public LatLongDeserializer() {
		super(LatLong.class);
	}

	@Override
	public LatLong deserialize(final JsonParser jsonParser, final DeserializationContext deserContext) throws IOException, JsonProcessingException {
		final JsonNode jsonNode = jsonParser.getCodec().readTree(jsonParser);
		return deserialize(jsonNode);
	}

	public LatLong deserialize(final JsonNode jsonNode) throws JsonProcessingException {
		if (jsonNode instanceof ObjectNode) {
			final ObjectNode objectNode = (ObjectNode) jsonNode;
			final double latitude = objectNode.get(GeoLocatedSerializer.LATITUDE_FIELD_NAME).asDouble();
			final double longitude = objectNode.get(GeoLocatedSerializer.LONGITUDE_FIELD_NAME).asDouble();
			return new LatLong(latitude, longitude);
		} else if (jsonNode instanceof ArrayNode) {
			final ArrayNode locationArray = (ArrayNode) jsonNode;
			if (locationArray.size() == 2) {
				final double latitude = locationArray.get(0).asDouble();
				final double longitude = locationArray.get(1).asDouble();
				return new LatLong(latitude, longitude);
			}
		}
		return null;
	}
}
