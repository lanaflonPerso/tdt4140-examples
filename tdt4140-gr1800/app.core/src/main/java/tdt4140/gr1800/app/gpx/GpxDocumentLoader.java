package tdt4140.gr1800.app.gpx;

import java.io.InputStream;

import io.jenetics.jpx.GPX;
import tdt4140.gr1800.app.doc.IDocumentLoader;

public class GpxDocumentLoader implements IDocumentLoader<GPX> {

	@Override
	public GPX loadDocument(InputStream inputStream) throws Exception {
		return GPX.read(inputStream, true);
	}
}
