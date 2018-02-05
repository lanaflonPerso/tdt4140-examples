package tdt4140.gr1800.app.doc;

import java.io.IOException;

public interface IDocumentImporter<L> {
	public void importDocument(L documentLocation) throws IOException;
}
