package tdt4140.gr1800.app.doc;

public interface IDocumentLoader<D, L> {
	public D loadDocument(L documentLocation) throws Exception;
}
