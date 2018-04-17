package tdt4140.gr18nn.web.server;

import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;

import tdt4140.gr18nn.app.core.LatLong;
import tdt4140.gr18nn.app.json.LatLongDeserializer;
import tdt4140.gr18nn.app.json.LatLongSerializer;

public class LatLongServlet extends HttpServlet {

	private ObjectMapper objectMapper;

	@Override
	public void init() throws ServletException {
		objectMapper = new ObjectMapper();
		final SimpleModule module = new SimpleModule();
		module.addSerializer(LatLong.class, new LatLongSerializer());
		module.addDeserializer(LatLong.class, new LatLongDeserializer());
		objectMapper.registerModule(module);
	}

	private final List<LatLong> geoLocations = new ArrayList<>();
	// About REST APIs: https://blog.mwaysolutions.com/2014/06/05/10-best-practices-for-better-restful-api/

	@Override
	protected void doGet(final HttpServletRequest request, final HttpServletResponse response) throws ServletException, IOException {
		int geoLocNum = parseNum(request.getPathInfo());
		if (geoLocNum < 0) {
			geoLocNum = geoLocations.size() + geoLocNum;
		}
		if (geoLocNum >= 0 && geoLocNum < geoLocations.size()) {
			respondWithJson(response, geoLocations.get(geoLocNum));
		} else {
			response.setStatus(HttpServletResponse.SC_NOT_FOUND);
		}
	}

	protected int parseNum(String path) throws ServletException {
		if (path.startsWith("/")) {
			path = path.substring(1);
		}
		try {
			return Integer.valueOf(path);
		} catch (final NumberFormatException e) {
		}
		throw new ServletException("Illegal reference to geo-location: " + path);
	}

	protected void respondWithJson(final HttpServletResponse response, final Object result) {
		response.setContentType("application/json");
		try (OutputStream output = response.getOutputStream()) {
			response.setStatus(HttpServletResponse.SC_OK);
			objectMapper.writerWithDefaultPrettyPrinter().writeValue(output, result);
		} catch (final Exception e) {
			response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
		}
	}

	@Override
	protected void doPost(final HttpServletRequest request, final HttpServletResponse response) throws ServletException, IOException {
		final Collection<LatLong> latLongs = objectMapper.readValue(request.getInputStream(), objectMapper.getTypeFactory().constructParametricType(Collection.class, LatLong.class));
		final int geoLocNum = geoLocations.size();
		geoLocations.addAll(latLongs);
		respondWithJson(response, geoLocNum);
	}

	@Override
	protected void doPut(final HttpServletRequest request, final HttpServletResponse response) throws ServletException, IOException {
		int geoLocNum = parseNum(request.getPathInfo());
		if (geoLocNum < 0) {
			geoLocNum = geoLocations.size() + geoLocNum;
		}
		final LatLong latLong = objectMapper.readValue(request.getInputStream(), LatLong.class);
		if (geoLocNum >= 0 && geoLocNum < geoLocations.size()) {
			geoLocations.set(geoLocNum, latLong);
			respondWithJson(response, geoLocNum);
		} else {
			response.setStatus(HttpServletResponse.SC_NOT_FOUND);
		}
	}

	@Override
	protected void doDelete(final HttpServletRequest request, final HttpServletResponse response) throws ServletException, IOException {
		int geoLocNum = parseNum(request.getPathInfo());
		if (geoLocNum < 0) {
			geoLocNum = geoLocations.size() + geoLocNum;
		}
		if (geoLocNum >= 0 && geoLocNum < geoLocations.size()) {
			final LatLong latLong = geoLocations.remove(geoLocNum);
			respondWithJson(response, latLong);
		} else {
			response.setStatus(HttpServletResponse.SC_NOT_FOUND);
		}
	}
}
