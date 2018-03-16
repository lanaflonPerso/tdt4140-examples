package tdt4140.gr1800.app.gpx;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.stream.Collectors;

import io.jenetics.jpx.GPX;
import io.jenetics.jpx.Route;
import io.jenetics.jpx.Track;
import io.jenetics.jpx.TrackSegment;
import io.jenetics.jpx.WayPoint;
import tdt4140.gr1800.app.core.GeoLocations;
import tdt4140.gr1800.app.core.LatLong;
import tdt4140.gr1800.app.doc.IDocumentLoader;

public class GpxDocumentConverter implements IDocumentLoader<Collection<GeoLocations>> {

	private GpxDocumentLoader gpxLoader = new GpxDocumentLoader();
	
	@Override
	public Collection<GeoLocations> loadDocument(InputStream inputStream) throws Exception {
		GPX gpx = gpxLoader.loadDocument(inputStream);
		return convert(gpx);
	}

	private String trackNameFormat = "%s";
	private String trackCountFormat = "Track %s";
	private String trackSegmentFormat = "%s.%s";

	public void setTrackNameFormat(String trackNameFormat) {
		this.trackNameFormat = trackNameFormat;
	}
	
	public void setTrackCountFormat(String trackCountFormat) {
		this.trackCountFormat = trackCountFormat;
	}
	
	public void setTrackSegmentFormat(String trackSegmentFormat) {
		this.trackSegmentFormat = trackSegmentFormat;
	}

	private String routeNameFormat = "%s";
	private String routeCountFormat = "Route %s";
	
	public void setRouteNameFormat(String routeNameFormat) {
		this.routeNameFormat = routeNameFormat;
	}
	
	public void setRouteCountFormat(String routeCountFormat) {
		this.routeCountFormat = routeCountFormat;
	}

	public Collection<GeoLocations> convert(GPX gpx) throws Exception {
		Collection<GeoLocations> geoLocations = new ArrayList<GeoLocations>();
		int trackCount = 1;
		for (Track track : gpx.getTracks()) {
			String trackName = (track.getName().isPresent() ? String.format(trackNameFormat, track.getName().get()) : String.format(trackCountFormat, trackCount));
			int segmentCount = 1;
			for (TrackSegment segment : track) {
				boolean singleTrack = track.getSegments().size() <= 1;
				String name = (singleTrack ? trackName : String.format(trackSegmentFormat, trackName, segmentCount));
				GeoLocations gl = new GeoLocations(name, convert(segment.getPoints()));
				gl.setPath(true);
				gl.addTags(Track.class.getName().toLowerCase());
				geoLocations.add(gl);
			}
		}
		int routeCount = 1;
		for (Route route : gpx.getRoutes()) {
			String routeName = (route.getName().isPresent() ? String.format(routeNameFormat, route.getName().get()) : String.format(routeCountFormat, routeCount));
			GeoLocations gl = new GeoLocations(routeName, convert(route.getPoints()));
			gl.setPath(true);
			gl.addTags(Route.class.getName().toLowerCase());
			geoLocations.add(gl);
		}
		if (! gpx.getWayPoints().isEmpty()) {
			GeoLocations gl = new GeoLocations("Waypoints", convert(gpx.getWayPoints()));
			gl.addTags(WayPoint.class.getName().toLowerCase());
			geoLocations.add(gl);
		}
		return geoLocations;
	}

	private LatLong[] convert(Collection<WayPoint> points) {
		Collection<LatLong> latLongs = points.stream()
				.map(point -> convert(point))
				.collect(Collectors.toList());
		return latLongs.toArray(new LatLong[latLongs.size()]);
	}

	private LatLong convert(WayPoint point) {
		return new LatLong(point.getLatitude().doubleValue(), point.getLongitude().doubleValue());
	}	
}
