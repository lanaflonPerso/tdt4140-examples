package tdt4140.gr1800.app.doc;

import java.io.IOException;
import java.io.InputStream;

public interface IDocumentImporter {
	public void importDocument(InputStream inputStream) throws IOException;
}
