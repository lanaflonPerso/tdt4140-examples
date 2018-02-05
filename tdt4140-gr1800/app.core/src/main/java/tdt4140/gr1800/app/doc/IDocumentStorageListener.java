package tdt4140.gr1800.app.doc;

public interface IDocumentStorageListener<L> {
	public void documentLocationChanged(L documentLocation, L oldDocumentLocation);
}
