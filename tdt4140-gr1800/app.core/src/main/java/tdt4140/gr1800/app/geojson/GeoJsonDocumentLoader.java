package tdt4140.gr1800.app.geojson;

import java.io.InputStream;

import org.geojson.GeoJsonObject;

import com.fasterxml.jackson.databind.ObjectMapper;

import tdt4140.gr1800.app.doc.IDocumentLoader;

public class GeoJsonDocumentLoader implements IDocumentLoader<GeoJsonObject> {

	@Override
	public GeoJsonObject loadDocument(InputStream inputStream) throws Exception {
		return new ObjectMapper().readValue(inputStream, GeoJsonObject.class);
	}
}
