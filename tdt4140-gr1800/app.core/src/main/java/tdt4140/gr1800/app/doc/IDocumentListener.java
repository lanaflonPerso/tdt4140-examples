package tdt4140.gr1800.app.doc;

public interface IDocumentListener<D, L> extends IDocumentStorageListener<L>{
	public void documentChanged(D document, D oldDocument);
}
