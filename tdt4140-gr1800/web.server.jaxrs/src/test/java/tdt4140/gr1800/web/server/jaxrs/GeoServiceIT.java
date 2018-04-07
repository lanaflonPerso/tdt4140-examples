package tdt4140.gr1800.web.server.jaxrs;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.NumericNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.databind.node.ValueNode;

import tdt4140.gr1800.app.core.Person;

public class GeoServiceIT {

	private ObjectMapper objectMapper;

	@Before
	public void setUp() {
		objectMapper = new GeoObjectMapperProvider().getContext(getClass());
	}

	private HttpURLConnection createUrlConnection(final String path) {
		try {
			return (HttpURLConnection) (new URL("http://localhost:8080/jax-rs/geo/" + path).openConnection());
		} catch (final MalformedURLException e) {
			throw new RuntimeException(e);
		} catch (final IOException e) {
			throw new RuntimeException(e);
		}
	}

	protected Collection<Person> getPersons(final ArrayNode arrayNode) throws JsonProcessingException {
		final Collection<Person> persons = new ArrayList<Person>();
		for (final JsonNode childNode : arrayNode) {
			persons.add(objectMapper.treeToValue(childNode, Person.class));
		}
		return persons;
	}

	protected int testPerson(final InputStream input, final Person person) throws Exception {
		final JsonNode jsonTree = objectMapper.readTree(input);
		Assert.assertTrue(jsonTree instanceof ObjectNode);
		Assert.assertTrue(((ObjectNode) jsonTree).has("id"));
		final JsonNode idNode = ((ObjectNode) jsonTree).get("id");
		Assert.assertTrue(idNode instanceof ValueNode);
		final int id = (idNode instanceof NumericNode ? ((NumericNode) idNode).asInt() : Integer.valueOf(idNode.asText()));
		Assert.assertTrue(isPerson(person, objectMapper.treeToValue(jsonTree, Person.class)));
		return id;
	}

	protected void testGetPerson(final int id, final Person person) throws Exception {
		final HttpURLConnection con = createUrlConnection("persons/" + id);
		con.setRequestMethod("GET");
		con.setDoInput(true);
		try (InputStream conIn = con.getInputStream()) {
			testPerson(conIn, person);
		}
	}

	protected boolean isPerson(final Person person, final Person conPerson) {
		return person.getName().equals(conPerson.getName()) && person.getEmail().equals(conPerson.getEmail());
	}

	protected void testGetPersons(final Person... persons) throws Exception {
		final HttpURLConnection con = createUrlConnection("persons");
		con.setRequestMethod("GET");
		con.setDoInput(true);
		try (InputStream conIn = con.getInputStream()) {
			final JsonNode jsonTree = objectMapper.readTree(conIn);
			Assert.assertTrue(jsonTree instanceof ArrayNode);
			final Collection<Person> conPersons = getPersons((ArrayNode) jsonTree);
			outer:  for (final Person person : persons) {
				for (final Person conPerson : conPersons) {
					if (isPerson(person, conPerson)) {
						continue outer;
					}
				}
				Assert.fail();
			}
		}
	}

	@Test
	public void testCreatePersonGetPerson() throws Exception {
		final Person person = new Person();
		person.setName("Hallvard");
		person.setEmail("hal@ntnu.no");
		final HttpURLConnection con = createUrlConnection("persons");
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
			final int id = testPerson(conIn, person);
			testGetPerson(id, person);
		}
	}

	@Test
	public void testCreatePersonGetPersons() throws Exception {
		final Person person = new Person();
		person.setName("Hallvard");
		person.setEmail("hallvard.traetteberg@gmail.com");
		final HttpURLConnection con = createUrlConnection("persons");
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
			testGetPersons(person);
		}
	}
}
