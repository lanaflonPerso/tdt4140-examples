package tdt4140.gr1800.app.doc;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;

public abstract class DocumentStorageImpl<D, L> implements IDocumentStorage<L>, IDocumentPersistence<D, L> {

	private L documentLocation;

	@Override
	public L getDocumentLocation() {
		return documentLocation;
	}

	@Override
	public void setDocumentLocation(L documentLocation) {
		L oldDocumentLocation = this.documentLocation;
		this.documentLocation = documentLocation;
		fireDocumentLocationChanged(oldDocumentLocation);
	}

	protected void setDocumentAndLocation(D document, L documentLocation) {
		setDocument(document);
		setDocumentLocation(documentLocation);
	}

	protected abstract D getDocument();
	protected abstract void setDocument(D document);

	//
	
	private Collection<IDocumentStorageListener<L>> documentListeners = new ArrayList<IDocumentStorageListener<L>>();
	
	public void addDocumentStorageListener(IDocumentStorageListener<L> documentStorageListener) {
		documentListeners.add(documentStorageListener);
	}

	public void removeDocumentStorageListener(IDocumentStorageListener<L> documentStorageListener) {
		documentListeners.remove(documentStorageListener);
	}
	
	protected void fireDocumentLocationChanged(L oldDocumentLocation) {
		for (IDocumentStorageListener<L> documentStorageListener : documentListeners) {
			documentStorageListener.documentLocationChanged(documentLocation, oldDocumentLocation);
		}
	}

	protected void fireDocumentChanged(D oldDocument) {
		for (IDocumentStorageListener<L> documentListener : documentListeners) {
			if (documentListener instanceof IDocumentListener) {
				((IDocumentListener<D, L>) documentListener).documentChanged(getDocument(), oldDocument);
			}
		}
	}
	
	protected abstract D createDocument();
	
	@Override
	public void newDocument() {
		setDocumentAndLocation(createDocument(), null);
	}

	@Override
	public void openDocument(L storage) throws IOException {
		try {
			setDocumentAndLocation(loadDocument(storage), storage);
		} catch (Exception e) {
			throw new IOException(e);
		}
	}

	@Override
	public void saveDocument() throws IOException {
		try {
			saveDocument(getDocument(), getDocumentLocation());
		} catch (Exception e) {
			throw new IOException(e);
		}
	}

	public void saveDocumentAs(L documentLocation) throws IOException {
		L oldDocumentLocation = getDocumentLocation();
		setDocumentLocation(documentLocation);
		try {
			saveDocument();
		} catch (IOException e) {
			setDocumentLocation(oldDocumentLocation);
			throw e;
		}
	}
	
	public void saveCopyAs(L documentLocation) throws Exception {
		saveDocument(getDocument(), documentLocation);
	}
}
