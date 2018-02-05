package tdt4140.gr1800.app.core;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.stream.Collectors;

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

	public void setGeoLocations(Collection<GeoLocations> geoLocations) {
		this.geoLocations = new ArrayList<>(geoLocations);
		fireGeoLocationsUpdated(null);
	}

	public void addGeoLocations(GeoLocations geoLocations) {
		if (hasGeoLocations(geoLocations.getName())) {
			throw new IllegalArgumentException("Duplicate geo-locations name: " + geoLocations.getName());
		}
		this.geoLocations.add(geoLocations);
		fireGeoLocationsUpdated(geoLocations);
	}
	
	public void removeGeoLocations(GeoLocations geoLocations) {
		this.geoLocations.remove(geoLocations);
		fireGeoLocationsUpdated(geoLocations);
	}

	private Collection<IGeoLocationsListener> geoLocationsListeners = new ArrayList<>();
	
	public void addGeoLocationsListener(IGeoLocationsListener listener) {
		geoLocationsListeners.add(listener);
	}

	public void removeGeoLocationsListener(IGeoLocationsListener listener) {
		geoLocationsListeners.remove(listener);
	}

	protected void fireGeoLocationsUpdated(GeoLocations geoLocations) {
		for (IGeoLocationsListener listener : geoLocationsListeners) {
			listener.geoLocationsUpdated(geoLocations);
		}
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
			setGeoLocations(document);
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
