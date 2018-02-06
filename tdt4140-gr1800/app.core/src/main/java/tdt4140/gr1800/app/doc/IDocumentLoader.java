package tdt4140.gr1800.app.doc;

import java.io.InputStream;

public interface IDocumentLoader<D> {
	public D loadDocument(InputStream inputStream) throws Exception;
}
