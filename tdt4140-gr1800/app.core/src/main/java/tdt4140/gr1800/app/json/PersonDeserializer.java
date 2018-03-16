package tdt4140.gr1800.app.json;

import java.io.IOException;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import tdt4140.gr1800.app.core.GeoLocations;
import tdt4140.gr1800.app.core.Person;

public class PersonDeserializer extends StdDeserializer<Person> {

	public PersonDeserializer() {
		super(Person.class);
	}

	@Override
	public Person deserialize(final JsonParser jsonParser, final DeserializationContext deserContext) throws IOException, JsonProcessingException {
		final JsonNode jsonNode = jsonParser.getCodec().readTree(jsonParser);
		return deserialize(jsonNode, deserContext);
	}

	private final GeoLocationsDeserializer geoLocationsDeserializer = new GeoLocationsDeserializer();

	Person deserialize(final JsonNode jsonNode, final DeserializationContext deserContext) throws JsonProcessingException {
		if (jsonNode instanceof ObjectNode) {
			final ObjectNode objectNode = (ObjectNode) jsonNode;
			final Person person = new Person();
			if (objectNode.has(GeoLocationsSerializer.NAME_FIELD_NAME)) {
				final String name = objectNode.get(GeoLocationsSerializer.NAME_FIELD_NAME).asText();
				person.setName(name);
			}
			if (objectNode.has(GeoLocationsSerializer.LOCATIONS_FIELD_NAME)) {
				final JsonNode locationsNode = objectNode.get(GeoLocationsSerializer.LOCATIONS_FIELD_NAME);
				if (locationsNode instanceof ArrayNode) {
					for (final JsonNode childNode : ((ArrayNode) locationsNode)) {
						final GeoLocations geoLocations = geoLocationsDeserializer.deserialize(childNode, deserContext);
						person.addGeolocations(geoLocations);
					}
				}
			}
			return person;
		}
		return null;
	}
}
