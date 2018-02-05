package tdt4140.gr1800.app.core;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.stream.Collectors;

import tdt4140.gr1800.app.doc.DocumentStorageImpl;
import tdt4140.gr1800.app.doc.IDocumentImporter;
import tdt4140.gr1800.app.doc.IDocumentLoader;
import tdt4140.gr1800.app.doc.IDocumentPersistence;
import tdt4140.gr1800.app.doc.IDocumentStorage;
import tdt4140.gr1800.app.gpx.GpxDocumentConverter;
import tdt4140.gr1800.app.json.GeoLocationsJsonPersistence;

public class App {

	private GeoLocationsPersistence geoLocationsLoader = new GeoLocationsJsonPersistence();
	
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
		public Collection<GeoLocations> loadDocument(File documentLocation) throws Exception {
			return geoLocationsLoader.loadLocations(new FileInputStream(documentLocation));
		}

		@Override
		public void saveDocument(Collection<GeoLocations> document, File documentLocation) throws Exception {
			geoLocationsLoader.saveLocations(document, new FileOutputStream(documentLocation));
		}
	};

	private DocumentStorageImpl<Collection<GeoLocations>, File> documentStorage = new DocumentStorageImpl<Collection<GeoLocations>, File>() {

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

		public Collection<GeoLocations> loadDocument(File documentLocation) throws Exception {
			return documentPersistence.loadDocument(documentLocation);
		}
		
		public void saveDocument(Collection<GeoLocations> document, File documentLocation) throws Exception {
			documentPersistence.saveDocument(document, documentLocation);
		}
		
		public Collection<IDocumentImporter<File>> getDocumentImporters() {
			return documentLoaders.stream().map(loader -> new IDocumentImporter<File>() {
				@Override
				public void importDocument(File file) throws IOException {
					try {
						setDocumentAndLocation(loader.loadDocument(file), null);
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

	private Collection<IDocumentLoader<Collection<GeoLocations>, File>> documentLoaders = Arrays.asList(
		new IDocumentLoader<Collection<GeoLocations>, File>() {
			private GpxDocumentConverter gpxConverter = new GpxDocumentConverter();
			@Override
			public Collection<GeoLocations> loadDocument(File documentLocation) throws Exception {
				return gpxConverter.loadDocument(documentLocation);
			}
		}
	);
	
	public Iterable<IDocumentLoader<Collection<GeoLocations>, File>> getDocumentLoaders() {
		return documentLoaders;
	}
}
