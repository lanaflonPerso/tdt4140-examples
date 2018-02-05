package tdt4140.gr1800.app.core;

public interface IDocumentSaver<D, L> {
	public void saveDocument(D document, L documentLocation) throws Exception;
}
