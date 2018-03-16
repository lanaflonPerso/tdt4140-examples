package tdt4140.gr1800.web.server;

import java.util.Collection;

import com.fasterxml.jackson.databind.JsonNode;

import tdt4140.gr1800.app.core.GeoLocation;
import tdt4140.gr1800.app.core.GeoLocations;

public class GeoLocationsGeoLocationsRelation extends RestRelation<GeoLocations, GeoLocation> {

	public GeoLocationsGeoLocationsRelation(String name, RestEntity<GeoLocation> relatedEntity) {
		super(name, relatedEntity);
	}

	@Override
	public Collection<GeoLocation> getRelated(GeoLocations entity) {
		throw new UnsupportedOperationException("NYI");
	}

	@Override
	public GeoLocation createRelated(GeoLocations owner, JsonNode payload) {
		throw new UnsupportedOperationException("NYI");
	}

	@Override
	public void updateRelated(GeoLocations owner, GeoLocation related, JsonNode payload) {
		getDbAccess().updateGeoLocationData(owner, related);
	}

	@Override
	public void deleteRelated(GeoLocations owner, GeoLocation related) {
		getDbAccess().deleteGeoLocation(owner, related);
	}
}
