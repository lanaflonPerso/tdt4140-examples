package tdt4140.gr18nn.web.server.jaxrs;

import java.util.List;

import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import tdt4140.gr18nn.app.core.LatLong;

@Path("latLong")
public class LatLongService {

	@Inject
	LatLongApp latLongApp;

	protected List<LatLong> getLatLongs() {
		return latLongApp.geoLocations;
	}

	@GET
	@Path("/{num}")
	@Produces(MediaType.APPLICATION_JSON)
	public LatLong getLatLong(@PathParam("num") int geoLocNum) {
		if (geoLocNum < 0) {
			geoLocNum = getLatLongs().size() + geoLocNum;
		}
		return getLatLongs().get(geoLocNum);
	}

	@POST
	@Path("/")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public int addLatLongs(final List<LatLong> latLongs) {
		final int geoLocNum = getLatLongs().size();
		getLatLongs().addAll(latLongs);
		return geoLocNum;
	}

	@PUT
	@Path("/{num}")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public int setLatLong(final LatLong latLong, @PathParam("num") int geoLocNum) {
		if (geoLocNum < 0) {
			geoLocNum = getLatLongs().size() + geoLocNum;
		}
		getLatLongs().set(geoLocNum, latLong);
		return geoLocNum;
	}

	@DELETE
	@Path("/{num}")
	@Produces(MediaType.APPLICATION_JSON)
	public LatLong deleteLatLong(@PathParam("num") int geoLocNum) {
		if (geoLocNum < 0) {
			geoLocNum = getLatLongs().size() + geoLocNum;
		}
		final LatLong latLong = getLatLongs().remove(geoLocNum);
		return latLong;
	}
}
