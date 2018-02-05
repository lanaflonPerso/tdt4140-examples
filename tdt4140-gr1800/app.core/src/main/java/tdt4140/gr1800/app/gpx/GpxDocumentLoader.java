package tdt4140.gr1800.app.gpx;

import java.io.InputStream;
import java.net.URL;

import io.jenetics.jpx.GPX;
import tdt4140.gr1800.app.core.IDocumentLoader;

public class GpxDocumentLoader implements IDocumentLoader<GPX, URL> {
	
	public GPX loadDocument(InputStream inputStream) throws Exception {
		return GPX.read(inputStream, true);
	}

	@Override
	public GPX loadDocument(URL documentLocation) throws Exception {
		return loadDocument(documentLocation.openStream());
	}
}
