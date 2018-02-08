package tdt4140.gr1800.app.core;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.stream.Collectors;

import tdt4140.gr1800.app.doc.AbstractDocumentStorageImpl;
import tdt4140.gr1800.app.doc.IDocumentImporter;
import tdt4140.gr1800.app.doc.IDocumentLoader;
import tdt4140.gr1800.app.doc.IDocumentPersistence;
import tdt4140.gr1800.app.doc.IDocumentStorage;
import tdt4140.gr1800.app.gpx.GpxDocumentConverter;
import tdt4140.gr1800.app.json.GeoLocationsJsonPersistence;

public class App {

	private GeoLocationsPersistence geoLocationsPersistence = new GeoLocationsJsonPersistence();
	
	private Collection<GeoLocations> geoLocations = null;

	public Iterable<String> getGeoLocationNames() {
		Collection<String> names = new ArrayList<String>(geoLocations != null ? geoLocations.size() : 0);
		if (geoLocations != null) {
			for (GeoLocations geoLocations : geoLocations) {
				names.add(geoLocations.getName());
			}
		}
		return names;
	}

	public boolean hasGeoLocations(String name) {
		if (geoLocations != null) {
			for (GeoLocations geoLocations : geoLocations) {
				if (name.equals(geoLocations.getName())) {
					return true;
				}
			}
		}
		return false;
	}

	public GeoLocations getGeoLocations(String name) {
		if (geoLocations != null) {
			for (GeoLocations geoLocations : geoLocations) {
				if (name.equals(geoLocations.getName())) {
					return geoLocations;
				}
			}
		}
		return null;
	}

	public Collection<GeoLocations> getGeoLocations(String... names) {
		Collection<GeoLocations> result = new ArrayList<GeoLocations>();
		if (geoLocations != null) {
			if (names != null) {
				for (String name : names) {
					GeoLocations gl = getGeoLocations(name);
					if (gl != null) {
						result.add(gl);
					}
				}
			} else {
				result.addAll(geoLocations);
			}
		}
		return result;
	}

	// 

	private IDocumentPersistence<Collection<GeoLocations>, File> documentPersistence = new IDocumentPersistence<Collection<GeoLocations>, File>() {
		
		@Override
		public Collection<GeoLocations> loadDocument(InputStream inputStream) throws Exception {
			return geoLocationsPersistence.loadLocations(inputStream);
		}

		@Override
		public void saveDocument(Collection<GeoLocations> document, File documentLocation) throws Exception {
			try (OutputStream output = new FileOutputStream(documentLocation)) {
				geoLocationsPersistence.saveLocations(document, output);
			}
		}
	};

	private AbstractDocumentStorageImpl<Collection<GeoLocations>, File> documentStorage = new AbstractDocumentStorageImpl<Collection<GeoLocations>, File>() {

		@Override
		protected Collection<GeoLocations> getDocument() {
			return geoLocations;
		}

		@Override
		protected void setDocument(Collection<GeoLocations> document) {
			Collection<GeoLocations> oldDocument = getDocument();
			App.this.geoLocations = document;
			fireDocumentChanged(oldDocument);
		}

		@Override
		protected Collection<GeoLocations> createDocument() {
			return new ArrayList<GeoLocations>();
		}

		protected InputStream toInputStream(File storage) throws IOException {
			return new FileInputStream(storage);
		}
		
		public Collection<GeoLocations> loadDocument(InputStream inputStream) throws Exception {
			return documentPersistence.loadDocument(inputStream);
		}
		
		public void saveDocument(Collection<GeoLocations> document, File documentLocation) throws Exception {
			documentPersistence.saveDocument(document, documentLocation);
		}
		
		public Collection<IDocumentImporter> getDocumentImporters() {
			return documentLoaders.stream().map(loader -> new IDocumentImporter() {
				@Override
				public void importDocument(InputStream inputStream) throws IOException {
					try {
						setDocumentAndLocation(loader.loadDocument(inputStream), null);
					} catch (Exception e) {
						throw new IOException(e);
					} 
				}
			}).collect(Collectors.toList());
		}
	};

	public IDocumentStorage<File> getDocumentStorage() {
		return documentStorage;
	}

	private Collection<IDocumentLoader<Collection<GeoLocations>>> documentLoaders = Arrays.asList(
		new IDocumentLoader<Collection<GeoLocations>>() {
			private GpxDocumentConverter gpxConverter = new GpxDocumentConverter();
			@Override
			public Collection<GeoLocations> loadDocument(InputStream inputStream) throws Exception {
				return gpxConverter.loadDocument(inputStream);
			}
		}
	);
	
	public Iterable<IDocumentLoader<Collection<GeoLocations>>> getDocumentLoaders() {
		return documentLoaders;
	}
}
