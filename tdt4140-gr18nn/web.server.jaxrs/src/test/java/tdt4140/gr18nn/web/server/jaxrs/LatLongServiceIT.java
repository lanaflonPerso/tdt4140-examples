package tdt4140.gr18nn.web.server.jaxrs;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.util.Collections;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import tdt4140.gr18nn.app.core.LatLong;

public class LatLongServiceIT {

	private ObjectMapper objectMapper;
	private Client client;

	@Before
	public void setUp() {
		objectMapper = new LatLongObjectMapperProvider().getContext(getClass());
		client = ClientBuilder.newClient();
	}

	@After
	public void tearDown() {
		client.close();
	}

	final String baseUrl = "http://localhost:8080/jax-rs/latLong/";

	@Test
	public void test() throws Exception {
		// POST, i.e. add
		final LatLong latLong = new LatLong(63, 11);
		final Response postResponse = client.target(baseUrl)
				.request("application/json; charset=UTF-8")
				.post(Entity.entity(objectMapper.writeValueAsString(Collections.singleton(latLong)), MediaType.APPLICATION_JSON));
		final String postResponseEntity = postResponse.readEntity(String.class);
		final Integer postNum = objectMapper.readValue(postResponseEntity, Integer.class);
		Assert.assertEquals(0, postNum.intValue());
		// GET
		testGet(0, latLong);
		// PUT, i.e. set
		final LatLong altLatLong = new LatLong(63, 11);

		final Response putResponse = client.target(baseUrl + "0")
				.request("application/json; charset=UTF-8")
				.put(Entity.entity(objectMapper.writeValueAsString(latLong), MediaType.APPLICATION_JSON));
		final Integer putNum = objectMapper.readValue(putResponse.readEntity(String.class), Integer.class);
		Assert.assertEquals(0, putNum.intValue());
		// GET
		testGet(0, altLatLong);
		// DELETE, i.e. remove
		final Response deleteResponse = client.target(baseUrl + "0")
				.request("application/json; charset=UTF-8")
				.delete();
		testContent(deleteResponse.readEntity(String.class), altLatLong);
	}

	protected void doJsonOutput(final HttpURLConnection postCon, final Object content)
			throws IOException, JsonGenerationException, JsonMappingException {
		postCon.setDoOutput(true);
		final ByteArrayOutputStream out = new ByteArrayOutputStream();
		objectMapper.writeValue(out, content);
		out.close();
		final byte[] postBytes = out.toByteArray();
		postCon.setFixedLengthStreamingMode(postBytes.length);
		postCon.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
		postCon.connect();
		try (OutputStream conOut = postCon.getOutputStream()) {
			conOut.write(postBytes);
		}
	}

	protected void testGet(final int num, final LatLong latLong)
			throws MalformedURLException, IOException, ProtocolException, JsonParseException, JsonMappingException {
		// GET
		final Response response = client.target(baseUrl + num)
				.request("application/json; charset=UTF-8")
				.get();
		testContent(response.readEntity(String.class), latLong);
	}

	protected void testContent(final String content, final LatLong latLong)
			throws IOException, JsonParseException, JsonMappingException {
		final LatLong getLatLong = objectMapper.readValue(content, LatLong.class);
		Assert.assertEquals(latLong, getLatLong);
	}
}
