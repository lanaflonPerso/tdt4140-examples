package tdt4140.gr18nn.web.server;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.util.Collections;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.databind.node.NumericNode;

import tdt4140.gr18nn.app.core.LatLong;
import tdt4140.gr18nn.app.json.LatLongDeserializer;
import tdt4140.gr18nn.app.json.LatLongSerializer;

public class LatLongServletIT {

	private ObjectMapper objectMapper;

	final String baseUrl = "http://localhost:8080/latLong/";

	@Before
	public void setUp() {
		objectMapper = new ObjectMapper();
		final SimpleModule module = new SimpleModule();
		module.addSerializer(LatLong.class, new LatLongSerializer());
		module.addDeserializer(LatLong.class, new LatLongDeserializer());
		objectMapper.registerModule(module);
	}

	@Test
	public void test() throws Exception {
		// POST, i.e. add
		final LatLong latLong = new LatLong(63, 11);
		final URL postUrl = new URL(baseUrl);
		final HttpURLConnection postCon = ((HttpURLConnection) postUrl.openConnection());
		postCon.setRequestMethod("POST");
		postCon.setDoInput(true);
		doJsonOutput(postCon, Collections.singleton(latLong));
		testNumContent(postCon, 0);
		// GET
		testGet(0, latLong);
		// PUT, i.e. set
		final LatLong altLatLong = new LatLong(63, 11);
		final URL putUrl = new URL(baseUrl + "0");
		final HttpURLConnection putCon = ((HttpURLConnection) putUrl.openConnection());
		putCon.setRequestMethod("PUT");
		putCon.setDoInput(true);
		doJsonOutput(putCon, altLatLong);
		testNumContent(putCon, 0);
		// GET
		testGet(0, altLatLong);
		// DELETE, i.e. remove
		final URL deleteUrl = new URL(baseUrl + "0");
		final HttpURLConnection deleteCon = ((HttpURLConnection) deleteUrl.openConnection());
		deleteCon.setRequestMethod("DELETE");
		deleteCon.setDoInput(true);
		testJsonContent(deleteCon, altLatLong);
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
		final URL getUrl = new URL(baseUrl + num);
		final HttpURLConnection getCon = ((HttpURLConnection) getUrl.openConnection());
		getCon.setRequestMethod("GET");
		getCon.setDoInput(true);
		getCon.connect();
		testJsonContent(getCon, latLong);
	}

	protected void testNumContent(final HttpURLConnection con, final int num) throws IOException {
		try (InputStream conIn = con.getInputStream()) {
			final JsonNode jsonTree = objectMapper.readTree(conIn);
			Assert.assertTrue(jsonTree instanceof NumericNode);
			Assert.assertEquals(num, ((NumericNode) jsonTree).asInt());
		}
	}

	protected void testJsonContent(final HttpURLConnection getCon, final LatLong latLong)
			throws IOException, JsonParseException, JsonMappingException {
		try (InputStream conIn = getCon.getInputStream()) {
			final LatLong getLatLong = objectMapper.readValue(conIn, LatLong.class);
			Assert.assertEquals(latLong, getLatLong);
		}
	}
}
