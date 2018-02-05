package tdt4140.gr1800.app.core;

import java.io.IOException;

public abstract class DocumentStorageImpl<D, L> implements IDocumentStorage<L>, IDocumentPersistence<D, L> {

	private L documentLocation;

	@Override
	public L getDocumentLocation() {
		return documentLocation;
	}

	@Override
	public void setDocumentLocation(L documentLocation) {
		this.documentLocation = documentLocation;
	}

	protected void setDocumentAndLocation(D document, L documentLocation) {
		setDocument(document);
		setDocumentLocation(documentLocation);
	}
	
	protected abstract D getDocument();
	protected abstract void setDocument(D document);

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
