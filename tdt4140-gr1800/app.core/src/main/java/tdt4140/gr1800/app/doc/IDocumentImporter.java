package tdt4140.gr1800.app.doc;

import java.io.IOException;
import java.io.InputStream;

/**
 * An interface with a method for importing domain data from a location.
 * The main use is supporting an **import** action in a **File** menu.
 * @author hal
 *
 */
public interface IDocumentImporter {
	/**
	 * Loads a document from the input stream and sets it as the current document.
	 * @param inputStream
	 * @throws IOException
	 */
	public void importDocument(InputStream inputStream) throws IOException;
}
