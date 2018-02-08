package tdt4140.gr1800.web.server;

import java.io.IOException;
import java.io.OutputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.stream.Collectors;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import io.jenetics.jpx.Person;
import tdt4140.gr1800.app.core.GeoLocations;
import tdt4140.gr1800.app.core.GeoLocationsPersistence;
import tdt4140.gr1800.app.json.GeoLocationsJsonPersistence;

public class GeoServlet extends HttpServlet {

	private GeoLocationsPersistence persistence = new GeoLocationsJsonPersistence();

	private Collection<GeoLocations> allGeoLocations = new ArrayList<GeoLocations>();
	
	@Override
	public void init() throws ServletException {
		String dataLocationsProp = System.getProperty("data.locations");
		if (dataLocationsProp != null) {
			String[] dataLocations = dataLocationsProp.split(",");
			for (int i = 0; i < dataLocations.length; i++) {
				try {
					Collection<GeoLocations> geoLocations = persistence.loadLocations(new URL(dataLocations[i]).openStream());
					allGeoLocations.addAll(geoLocations);
				} catch (Exception e) {
					System.err.println(e.getMessage());
				}
			}
		}
		super.init();
	}
	
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
    		String path = request.getPathInfo();
    		Collection<GeoLocations> geoLocations = null;
    		if (path != null) {
    			if (path.startsWith("/")) {
    				path = path.substring(1);
    			}
    			String[] segments = path.split("\\/");
    			if (segments.length == 1) {
    				geoLocations = allGeoLocations.stream().filter(geoLocation -> segments[0].equals(geoLocation.getName())).collect(Collectors.toList());
    			}
    		} else {
    			geoLocations = allGeoLocations;
    		}
    		if (geoLocations == null) {
			response.setStatus(HttpServletResponse.SC_NOT_FOUND);
    		} else {
    			response.setContentType("application/json");
    			response.setStatus(HttpServletResponse.SC_OK);
    			try (OutputStream output = response.getOutputStream()) {
				persistence.saveLocations(geoLocations, output);
			} catch (Exception e) {
				response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
			}
    		}
    }
}
