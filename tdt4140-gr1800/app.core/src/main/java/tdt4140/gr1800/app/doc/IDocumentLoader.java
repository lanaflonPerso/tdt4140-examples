package tdt4140.gr1800.app.doc;

import java.io.InputStream;

/**
 * An interface with a method for loading and returning a document (domain data container) from an InputStream.
 * This allows various ways of loading or importing domain data, with different sources and formats.
 * @author hal
 *
 * @param <D> the document type
 */
public interface IDocumentLoader<D> {
	/**
	 * Loads and returns a new document from an InputStream
	 * @param inputStream
	 * @return
	 * @throws Exception
	 */
	public D loadDocument(InputStream inputStream) throws Exception;
}
