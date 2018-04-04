package tdt4140.gr1800.app.doc;

/**
 * Listener interface for the (contents of) the (current) document of an IDocumentStorage, e.g.
 * when an **open** action is performed.
 * @author hal
 *
 * @param <D> the document type
 * @param <L> the location type
 */
public interface IDocumentListener<D, L> extends IDocumentStorageListener<L> {
	/**
	 * Notifies that the current document has changed.
	 * @param document the new document
	 * @param oldDocument the previous document
	 */
	public void documentChanged(D document, D oldDocument);
}
