package tdt4140.gr1800.app.core;

import java.io.File;
import java.io.IOException;
import java.net.URL;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class AppTest {

	private App app;
	
	@Before
	public void setUp() {
		app = new App();
	}

	@Test
	public void testLoadDocument() {
		IDocumentStorage<File> documentStorage = app.getDocumentStorage();
		URL url = getClass().getResource("geoLocations.json");
		Assert.assertEquals("Not file URL", "file", url.getProtocol()); 
		File file = new File(url.getPath());
		try {
			documentStorage.openDocument(file);
		} catch (IOException e) {
			Assert.fail("Couldn't open " + file);
		}
		Assert.assertEquals(file, documentStorage.getDocumentLocation());
		GeoLocationsPersistenceTest.testGeoLocationsDotJson(app.getGeoLocations((String[]) null));
	}
}
