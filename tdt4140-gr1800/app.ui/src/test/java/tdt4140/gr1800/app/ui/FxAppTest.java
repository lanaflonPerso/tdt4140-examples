package tdt4140.gr1800.app.ui;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.testfx.framework.junit.ApplicationTest;
import org.testfx.util.WaitForAsyncUtils;

import fxmapcontrol.MapBase;
import fxmapcontrol.MapItemsControl;
import javafx.collections.ObservableList;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ComboBox;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyCombination;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import tdt4140.gr1800.app.core.GeoLocations;
import tdt4140.gr1800.app.doc.IDocumentStorageListener;

public class FxAppTest extends ApplicationTest {

    @BeforeClass
    public static void headless() {
    		if (Boolean.valueOf(System.getProperty("gitlab-ci", "false"))) {
    			GitlabCISupport.headless();
    		}
    }

    private FxAppController controller;
    
	@Override
    public void start(Stage stage) throws Exception {
		FXMLLoader loader = new FXMLLoader(getClass().getResource("FxApp.fxml"));
        Parent root = loader.load();
        this.controller = loader.getController();
        Scene scene = new Scene(root);
        
        stage.setTitle("JavaFX and Maven");
        stage.setScene(scene);
        stage.show();
    }
	
	private File geoLocationsDotJson = null;

	@Before
	public void ensureGeoLocationsDotJson() {
		openGeoLocationsDotJson();
		WaitForAsyncUtils.waitForFxEvents();
	}
	
	protected void openGeoLocationsDotJson() {
		Assert.assertNotNull(this.controller);
		InputStream inputStream = GeoLocations.class.getResourceAsStream("geoLocations.json");
		try {
			geoLocationsDotJson = File.createTempFile("geoLocations", ".json");
			geoLocationsDotJson.deleteOnExit();
			Files.copy(inputStream, geoLocationsDotJson.toPath(), StandardCopyOption.REPLACE_EXISTING);
		} catch (IOException e) {
			Assert.fail(e.getMessage());
		}
		try {
			controller.fileMenuController.documentStorage.openDocument(geoLocationsDotJson);
		} catch (IOException e) {
			Assert.fail(e.getMessage());			
		}
	}
	
	@Test
	public void testMapExists() {
		Node map = lookup("#mapView").query();
		Assert.assertTrue(map instanceof MapBase);
	}

	private <T> T getItem(Iterable<?> items, Class<T> clazz, int num) {
		for (Object child : items) {
			if (clazz.isInstance(child)) {
				num--;
				if (num <= 0) {
					return (T) child;					
				}
			}
		}
		return null;
	}
	
	private <T> Collection<T> getItems(Iterable<?> items, Class<T> clazz, int count) {
		Collection<T> result = new ArrayList<T>();
		for (Object child : items) {
			if (clazz.isInstance(child)) {
				result.add((T) child);
				count--;
				if (count <= 0) {
					return result;					
				}
			}
		}
		return null;
	}
	
    @Test
    public void testMapHasMarkers() {
    		Assert.assertNotNull(getItems(controller.markersParent.getItems(), MapMarker.class, 4));
    }

    @Test
    public void testGeoLocationsSelector() {
	    	Node combo = lookup("#geoLocationsSelector").query();
	    	Assert.assertTrue(combo instanceof ComboBox);
	    	ObservableList<?> items = ((ComboBox<?>) combo).getItems();
	    	Assert.assertEquals(2, items.size());
	    	Assert.assertEquals("1 (2)", items.get(0));
	    	Assert.assertEquals("2 (2)", items.get(1));	    	
    }
    
//    @Test
    public void testOpenTestDocument() {
    		try {
    			InputStream inputStream = GeoLocations.class.getResourceAsStream("geoLocations.json");
			File tempFile = File.createTempFile("geoLocations", ".json");
			tempFile.deleteOnExit();
    			Files.copy(inputStream, tempFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
    			FileChooser fileChooser = controller.fileMenuController.getFileChooser();
    			fileChooser.setInitialDirectory(tempFile.getParentFile());
//    			fileChooser.setInitialFileName(tempFile.getName());
    			KeyCodeCombination openKeys = new KeyCodeCombination(KeyCode.O, KeyCombination.META_DOWN);
    			push(openKeys);
    			sleep(1, TimeUnit.SECONDS);
    			String name = tempFile.getName().substring(0, 1);
    			for (int i = 0; i < name.length(); i++) {
    				push(KeyCode.getKeyCode(String.valueOf(name.charAt(i)).toUpperCase()));
    			}
    			final File[] documentLocations = new File[1];
    			controller.fileMenuController.documentStorage.addDocumentStorageListener(new IDocumentStorageListener<File>() {
    				@Override
    				public void documentLocationChanged(File documentLocation, File oldDocumentLocation) {
    					documentLocations[0] = documentLocation;
    				}
    			});
    			push(KeyCode.ENTER);
    			sleep(1, TimeUnit.SECONDS);
    			Assert.assertEquals(tempFile.getCanonicalPath(), String.valueOf(documentLocations[0]));
    		} catch (IOException e) {
    			System.out.println(e);
			Assert.fail(e.getMessage());
		}
    }
}
