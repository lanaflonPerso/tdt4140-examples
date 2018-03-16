package tdt4140.gr1800.web.server;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.databind.node.ObjectNode;

import tdt4140.gr1800.app.core.GeoLocation;
import tdt4140.gr1800.app.core.GeoLocations;
import tdt4140.gr1800.app.core.LatLong;
import tdt4140.gr1800.app.core.Person;
import tdt4140.gr1800.app.json.GeoLocationDeserializer;
import tdt4140.gr1800.app.json.GeoLocationSerializer;
import tdt4140.gr1800.app.json.GeoLocationsDeserializer;
import tdt4140.gr1800.app.json.GeoLocationsSerializer;
import tdt4140.gr1800.app.json.LatLongDeserializer;
import tdt4140.gr1800.app.json.LatLongSerializer;
import tdt4140.gr1800.app.json.PersonDeserializer;
import tdt4140.gr1800.app.json.PersonSerializer;

public class GeoLocationsServerIT {

	private ObjectMapper objectMapper;

	@Before
	public void setUp() {
		objectMapper = new ObjectMapper();
		final SimpleModule module = new SimpleModule();
		module.addSerializer(new LatLongSerializer());
		module.addSerializer(new GeoLocationSerializer());
		module.addSerializer(new GeoLocationsSerializer());
		module.addSerializer(new PersonSerializer());
		module.addDeserializer(LatLong.class, new LatLongDeserializer());
		module.addDeserializer(GeoLocation.class, new GeoLocationDeserializer());
		module.addDeserializer(GeoLocations.class, new GeoLocationsDeserializer());
		module.addDeserializer(Person.class, new PersonDeserializer());
		objectMapper.registerModule(module);
	}

	@Test
	public void testPostPerson() throws Exception {
		final Person person = new Person();
		person.setName("Hallvard");
		person.setEmail("hal@ntnu.no");
		final URL url = new URL("http://localhost:8080/geo/persons");
		final HttpURLConnection con = ((HttpURLConnection) url.openConnection());
		con.setRequestMethod("POST");
		con.setDoOutput(true);
		con.setDoInput(true);
		final ByteArrayOutputStream out = new ByteArrayOutputStream();
		objectMapper.writeValue(out, person);
		out.close();
		final byte[] bytes = out.toByteArray();
		con.setFixedLengthStreamingMode(bytes.length);
		con.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
		con.connect();
		try (OutputStream conOut = con.getOutputStream()) {
			conOut.write(bytes);
		}
		try (InputStream conIn = con.getInputStream()) {
			final JsonNode jsonTree = objectMapper.readTree(conIn);
			//			System.out.println(jsonTree);
			Assert.assertTrue(jsonTree instanceof ObjectNode);
			Assert.assertTrue(((ObjectNode) jsonTree).has("id"));
			final Person conPerson = objectMapper.treeToValue(jsonTree, Person.class);
			Assert.assertEquals(person.getName(), conPerson.getName());
			Assert.assertEquals(person.getEmail(), conPerson.getEmail());
		}
	}
}
