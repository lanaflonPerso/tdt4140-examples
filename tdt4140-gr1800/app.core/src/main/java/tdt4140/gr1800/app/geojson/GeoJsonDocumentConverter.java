package tdt4140.gr1800.app.geojson;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.stream.Collectors;

import org.geojson.GeoJsonObject;
import org.geojson.LngLatAlt;

import tdt4140.gr1800.app.core.GeoLocations;
import tdt4140.gr1800.app.core.LatLong;
import tdt4140.gr1800.app.doc.IDocumentLoader;

public class GeoJsonDocumentConverter implements IDocumentLoader<Collection<GeoLocations>> {

	private GeoJsonDocumentLoader geoJsonLoader = new GeoJsonDocumentLoader();
	
	@Override
	public Collection<GeoLocations> loadDocument(InputStream inputStream) throws Exception {
		GeoJsonObject geoJson = geoJsonLoader.loadDocument(inputStream);
		return convert(geoJson);
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
	
	public Collection<GeoLocations> convert(GeoJsonObject geoJson) throws Exception {
		Collection<GeoLocations> geoLocations = new ArrayList<GeoLocations>();
		return geoLocations;
	}

	private LatLong[] convert(Collection<LngLatAlt> points) {
		Collection<LatLong> latLongs = points.stream()
				.map(point -> convert(point))
				.collect(Collectors.toList());
		return latLongs.toArray(new LatLong[latLongs.size()]);
	}

	private LatLong convert(LngLatAlt point) {
		return new LatLong(point.getLatitude(), point.getLongitude());
	}
}
