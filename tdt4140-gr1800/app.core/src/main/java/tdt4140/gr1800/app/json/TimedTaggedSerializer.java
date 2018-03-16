package tdt4140.gr1800.app.json;

import java.io.IOException;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;

import tdt4140.gr1800.app.core.TimedTaggedImpl;

public class TimedTaggedSerializer<T extends TimedTaggedImpl> extends StdSerializer<T> {
	
	public static final String DATE_FIELD_NAME = "date";
	public static final String TIME_FIELD_NAME = "time";
	public static final String ZONE_FIELD_NAME = "zone";
	public static final String TAGS_FIELD_NAME = "tags";

	public TimedTaggedSerializer(Class<T> clazz) {
		super(clazz);
	}

	protected void serialize(TimedTaggedImpl timedTagged, JsonGenerator jsonGen) throws IOException {
		if (timedTagged.getDate() != null) {
			jsonGen.writeFieldName(DATE_FIELD_NAME);
			jsonGen.writeString(timedTagged.getDate().toString());
		}
		if (timedTagged.getTime() != null) {
			jsonGen.writeFieldName(TIME_FIELD_NAME);
			jsonGen.writeString(timedTagged.getTime().toString());
		}
		String[] tags = timedTagged.getTags();
		if (tags != null && tags.length > 0) {
			jsonGen.writeFieldName(TAGS_FIELD_NAME);
			jsonGen.writeStartArray();
			for (int i = 0; i < tags.length; i++) {
				jsonGen.writeString(tags[i]);
			}
			jsonGen.writeEndArray();
		}
	}

	@Override
	public void serialize(T value, JsonGenerator jsonGen, SerializerProvider provider) throws IOException {
		serialize(value, jsonGen);
	}
}
