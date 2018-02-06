package tdt4140.gr1800.app.doc;

import java.io.IOException;
import java.util.Collection;

public interface IDocumentStorage<L> {
	public L getDocumentLocation();
	public void setDocumentLocation(L documentLocation);

	public void addDocumentStorageListener(IDocumentStorageListener<L> documentStorageListener);
	public void removeDocumentStorageListener(IDocumentStorageListener<L> documentStorageListener);

	public void newDocument();
	public void openDocument(L documentLocation) throws IOException;
	public void saveDocument() throws IOException;

	public Collection<IDocumentImporter> getDocumentImporters();
}
