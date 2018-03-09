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
import tdt4140.gr1800.app.core.GeoLocationsStreamPersistence;
import tdt4140.gr1800.app.json.GeoLocationsJsonPersistence;

public class GeoServlet extends HttpServlet {

	private GeoLocationsStreamPersistence persistence = new GeoLocationsJsonPersistence();

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
	
	// REST URL structure, according to https://blog.mwaysolutions.com/2014/06/05/10-best-practices-for-better-restful-api/
	// persons/<id>/geoLocations/<num>/geoLocations/<num>

	// GET variants
	// persons: Get all Person objects. Do we allow that? Should we return a list of <id> values or all the person entities (with some subset of properties) 
	// persons/<id>: Get a specific Person object
	// persons/name or email: Get a specific Person object, with the provided name or email (with a '@')
	// persons/<id>/geoLocations: Get all the GeoLocations objects, with (some subset of) properties
	// persons/<id>/geoLocations/<num>: Get a specific GeoLocations object
	// persons/<id>/geoLocations/<num>/geoLocations: Get all GeoLocation objects, with (some subset of) properties
	// persons/<id>/geoLocations/<num>/geoLocations/<num>: Get a specific GeoLocations object
	
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

	// POST variants
	// persons: Create a new Person object, with properties in the payload
	// persons/<id>: Not allowed
    // persons/<id>/geoLocations: Create a new GeoLocations object, with properties in the payload
	// persons/<id>/geoLocations/<num>: Not allowed
	// persons/<id>/geoLocations/<num>/geoLocations: Create a new GeoLocation object, with properties in the payload
	// persons/<id>/geoLocations/<num>/geoLocations/<num>: Not allowed

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
    		super.doPost(req, resp);
    }
    
	// PUT variants
	// persons: Not allowed
	// persons/<id>: Update specific Person object
    // persons/<id>/geoLocations: Not allowed
	// persons/<id>/geoLocations/<num>: Update specific GeoLocations object
	// persons/<id>/geoLocations/<num>/geoLocations: Not allowed
	// persons/<id>/geoLocations/<num>/geoLocations/<num>: Update specific GeoLocations object

    @Override
    protected void doPut(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
    		// TODO Auto-generated method stub
    		super.doPut(req, resp);
    }

    // DELETE variants
    // persons: Not allowed
    // persons/<id>: Delete specific Person object
    // persons/<id>/geoLocations: Delete all GeoLocations objects?
    // persons/<id>/geoLocations/<num>: Delete specific GeoLocations object
    // persons/<id>/geoLocations/<num>/geoLocations: Delete all GeoLocation objects?
    // persons/<id>/geoLocations/<num>/geoLocations/<num>: Delete specific GeoLocation object

    @Override
    protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
    		// TODO Auto-generated method stub
    		super.doDelete(req, resp);
    }
}
