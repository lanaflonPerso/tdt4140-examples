package tdt4140.gr1800.app.json;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import com.fasterxml.jackson.databind.node.ObjectNode;

import tdt4140.gr1800.app.core.TimedTaggedImpl;

public class TimedTaggedImplDeserializer extends StdDeserializer<TimedTaggedImpl> {

	public TimedTaggedImplDeserializer() {
		super(TimedTaggedImpl.class);
	}

	@Override
	public TimedTaggedImpl deserialize(final JsonParser jsonParser, final DeserializationContext deserContext) throws IOException, JsonProcessingException {
		final JsonNode jsonNode = jsonParser.getCodec().readTree(jsonParser);
		return deserialize(jsonNode, new TimedTaggedImpl());
	}

	public TimedTaggedImpl deserialize(final JsonNode jsonNode, final TimedTaggedImpl timedTagged) throws JsonProcessingException {
		if (jsonNode instanceof ObjectNode) {
			final ObjectNode objectNode = (ObjectNode) jsonNode;
			if (objectNode.has(TimedTaggedSerializer.DATE_FIELD_NAME)) {
				timedTagged.setDate(LocalDate.parse(objectNode.get(TimedTaggedSerializer.DATE_FIELD_NAME).asText()));
			}
			if (objectNode.has(TimedTaggedSerializer.TIME_FIELD_NAME)) {
				timedTagged.setTime(LocalTime.parse(objectNode.get(TimedTaggedSerializer.TIME_FIELD_NAME).asText()));
			}
			if (objectNode.has(TimedTaggedSerializer.ZONE_FIELD_NAME)) {
				timedTagged.setZone(ZoneId.of(objectNode.get(TimedTaggedSerializer.ZONE_FIELD_NAME).asText()));
			}
		}
		return timedTagged;
	}
}
