package tdt4140.gr1800.app.doc;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;

/**
 * Incomplete implementation of **IDocumentStorage**, to simplify implementing ones for specific document and location types.
 * The main missing methods are for getting and setting the current document, creating an empty one and
 * creating an **InputStream** from a location.
 * @author hal
 *
 * @param <D> the document type
 * @param <L> the location type
 */
public abstract class AbstractDocumentStorageImpl<D, L> implements IDocumentStorage<L>, IDocumentPersistence<D, L> {

	private L documentLocation;

	@Override
	public L getDocumentLocation() {
		return documentLocation;
	}

	@Override
	public void setDocumentLocation(final L documentLocation) {
		final L oldDocumentLocation = this.documentLocation;
		this.documentLocation = documentLocation;
		fireDocumentLocationChanged(oldDocumentLocation);
	}

	protected void setDocumentAndLocation(final D document, final L documentLocation) {
		setDocument(document);
		setDocumentLocation(documentLocation);
	}

	/**
	 * Returns the current document.
	 * @return the current document
	 */
	protected abstract D getDocument();

	/**
	 * Sets the current document
	 * @param document the new document
	 */
	protected abstract void setDocument(D document);

	//

	private final Collection<IDocumentStorageListener<L>> documentListeners = new ArrayList<IDocumentStorageListener<L>>();

	@Override
	public void addDocumentStorageListener(final IDocumentStorageListener<L> documentStorageListener) {
		documentListeners.add(documentStorageListener);
	}

	@Override
	public void removeDocumentStorageListener(final IDocumentStorageListener<L> documentStorageListener) {
		documentListeners.remove(documentStorageListener);
	}

	protected void fireDocumentLocationChanged(final L oldDocumentLocation) {
		for (final IDocumentStorageListener<L> documentStorageListener : documentListeners) {
			documentStorageListener.documentLocationChanged(documentLocation, oldDocumentLocation);
		}
	}

	protected void fireDocumentChanged(final D oldDocument) {
		for (final IDocumentStorageListener<L> documentListener : documentListeners) {
			if (documentListener instanceof IDocumentListener) {
				((IDocumentListener<D, L>) documentListener).documentChanged(getDocument(), oldDocument);
			}
		}
	}

	/**
	 * Creates a new and empty document.
	 * @return
	 */
	protected abstract D createDocument();

	@Override
	public void newDocument() {
		setDocumentAndLocation(createDocument(), null);
	}

	/**
	 * Creates an ImportStream from a location
	 * @param location
	 * @return
	 * @throws IOException
	 */
	protected abstract InputStream toInputStream(L location) throws IOException;

	@Override
	public void openDocument(final L storage) throws IOException {
		try (InputStream input = toInputStream(storage)){
			setDocumentAndLocation(loadDocument(input), storage);
		} catch (final Exception e) {
			throw new IOException(e);
		}
	}

	@Override
	public void saveDocument() throws IOException {
		try {
			saveDocument(getDocument(), getDocumentLocation());
		} catch (final Exception e) {
			throw new IOException(e);
		}
	}

	public void saveDocumentAs(final L documentLocation) throws IOException {
		final L oldDocumentLocation = getDocumentLocation();
		setDocumentLocation(documentLocation);
		try {
			saveDocument();
		} catch (final IOException e) {
			setDocumentLocation(oldDocumentLocation);
			throw e;
		}
	}

	public void saveCopyAs(final L documentLocation) throws Exception {
		saveDocument(getDocument(), documentLocation);
	}
}
