package tdt4140.gr1800.app.json;

import java.io.IOException;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;

import tdt4140.gr1800.app.core.GeoLocations;
import tdt4140.gr1800.app.core.Person;

public class PersonSerializer extends IdSerializer<Person> {

	public static final String ID_FIELD_NAME = "id";
	public static final String NAME_FIELD_NAME = "name";
	public static final String EMAIL_FIELD_NAME = "email";
	public static final String GEOLOCATIONS_FIELD_NAME = "geoLocations";

	public PersonSerializer() {
		super(Person.class);
	}

	@Override
	public void serialize(final Person person, final JsonGenerator jsonGen, final SerializerProvider serProvider) throws IOException {
		jsonGen.writeStartObject();
		serializeId(person, jsonGen);
		jsonGen.writeFieldName(NAME_FIELD_NAME);
		jsonGen.writeString(person.getName());
		if (person.getEmail() != null) {
			jsonGen.writeFieldName(EMAIL_FIELD_NAME);
			jsonGen.writeString(person.getEmail());
		}
		jsonGen.writeFieldName(GEOLOCATIONS_FIELD_NAME);
		jsonGen.writeStartArray();
		for (final GeoLocations geoLocations : person.getGeoLocations((String[]) null)) {
			jsonGen.writeObject(geoLocations);
		}
		jsonGen.writeEndArray();
		jsonGen.writeEndObject();
	}
}
