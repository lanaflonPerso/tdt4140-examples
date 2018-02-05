package tdt4140.gr1800.app.doc;

public interface IDocumentSaver<D, L> {
	public void saveDocument(D document, L documentLocation) throws Exception;
}
