package tdt4140.gr1800.app.ui;

import java.io.File;
import java.net.URI;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import com.lynden.gmapsfx.GoogleMapView;
import com.lynden.gmapsfx.MapComponentInitializedListener;
import com.lynden.gmapsfx.javascript.event.UIEventType;
import com.lynden.gmapsfx.javascript.object.GoogleMap;
import com.lynden.gmapsfx.javascript.object.MVCArray;
import com.lynden.gmapsfx.javascript.object.MapOptions;
import com.lynden.gmapsfx.javascript.object.MapShape;
import com.lynden.gmapsfx.javascript.object.MapTypeIdEnum;
import com.lynden.gmapsfx.javascript.object.Marker;
import com.lynden.gmapsfx.javascript.object.MarkerOptions;
import com.lynden.gmapsfx.shapes.Polyline;
import com.lynden.gmapsfx.shapes.PolylineOptions;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Slider;
import javafx.scene.control.TextField;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import tdt4140.gr1800.app.core.App;
import tdt4140.gr1800.app.core.GeoLocations;
import tdt4140.gr1800.app.core.LatLong;

public class FxAppController implements MapComponentInitializedListener {

	@FXML
	private TextField geolocationsFileText;

	@FXML
	private ComboBox<String> geoLocationsSelector;

	@FXML
	private GoogleMapView mapView;
	private GoogleMap map;
	
	@FXML
	private Slider zoomSlider;
	
	private App app;

	@FXML
	public void initialize() {
		mapView.addMapInializedListener(this);
		app = new App();
		geoLocationsSelector.getSelectionModel().selectedItemProperty().addListener((stringProperty, oldValue, newValue) -> updateGeoLocations());
		zoomSlider.valueProperty().addListener((doubleProperty, oldValue, newValue) -> map.setZoom((int) zoomSlider.getValue()));
	}

	private Object updateGeoLocations() {
		return null;
	}

	private Map<GeoLocations, MapShape> shapes = new HashMap<GeoLocations, MapShape>();
	private Map<GeoLocations, Collection<Marker>> markers = new HashMap<GeoLocations, Collection<Marker>>();
	
	private void initMapMarkers() {
		map.clearMarkers();
		for (String geoLocationName : app.getGeoLocationNames()) {
			GeoLocations geoLocations = app.getGeoLocations(geoLocationName);
			if (geoLocations.isPath()) {
				Collection<com.lynden.gmapsfx.javascript.object.LatLong> latLongs = new ArrayList<com.lynden.gmapsfx.javascript.object.LatLong>();
				for (LatLong latLong : geoLocations) {
					double lat = latLong.latitude, lon = latLong.longitude;
					latLongs.add(new com.lynden.gmapsfx.javascript.object.LatLong(lat, lon));
				}
				MVCArray mvc = new MVCArray(latLongs.toArray(new com.lynden.gmapsfx.javascript.object.LatLong[latLongs.size()]));
				PolylineOptions options = new PolylineOptions()
					.path(mvc)
					.strokeColor("red")
					.strokeWeight(2);
				Polyline polyline = new Polyline(options);
				map.addMapShape(polyline);
				this.shapes.put(geoLocations, polyline);
			} else {
				Collection<Marker> markers = new ArrayList<Marker>();
				for (LatLong latLong : geoLocations) {
					double lat = latLong.latitude, lon = latLong.longitude;
					MarkerOptions options = new MarkerOptions().label(geoLocationName)
							.position(new com.lynden.gmapsfx.javascript.object.LatLong(lat, lon));
					Marker marker = new Marker(options);
					map.addMarker(marker);
					markers.add(marker);
				}
				this.markers.put(geoLocations, markers);
			}
			geoLocationsSelector.getItems().add(geoLocationName);
		}
		map.setCenter(getCenter(null));
		map.addMouseEventHandler(UIEventType.click, (event) -> {
		   com.lynden.gmapsfx.javascript.object.LatLong latLong = event.getLatLong();
		   System.out.println("Latitude: " + latLong.getLatitude());
		   System.out.println("Longitude: " + latLong.getLongitude());
		});
		System.out.println("Map markers initialized");
	}

	private com.lynden.gmapsfx.javascript.object.LatLong getCenter(GeoLocations geoLocations) {
		double latSum = 0.0, lonSum = 0.0;
		int num = 0;
		if (geoLocations == null) {
			for (String geoLocationName : app.getGeoLocationNames()) {
				geoLocations = app.getGeoLocations(geoLocationName);
				for (LatLong latLong : geoLocations) {
					double lat = latLong.latitude, lon = latLong.longitude;
					latSum += lat;
					lonSum += lon;
					num++;
				}
			}
		} else {
			for (LatLong latLong : geoLocations) {
				double lat = latLong.latitude, lon = latLong.longitude;
				latSum += lat;
				lonSum += lon;
				num++;
			}
		}
		return new com.lynden.gmapsfx.javascript.object.LatLong(latSum / num, lonSum / num);
	}
	
	@Override
	public void mapInitialized() {
		// Set the initial properties of the map.
		MapOptions mapOptions = new MapOptions();

		mapOptions
			.mapType(MapTypeIdEnum.ROADMAP)
			.center(new com.lynden.gmapsfx.javascript.object.LatLong(63.0, 10.0))
			.overviewMapControl(false)
			.panControl(false)
			.rotateControl(false)
			.scaleControl(false)
			.streetViewControl(false)
			.zoomControl(false)
			.zoom(zoomSlider.getValue());
		map = mapView.createMap(mapOptions);
		System.out.println("Map initialized: " + map);
		if (app.getGeoLocationNames().iterator().hasNext()) {
			initMapMarkers();
		}
	}
	
	@FXML
	public void loadGeolocationsFile() {
		try {
			URI fileUri = new URI(geolocationsFileText.getText());
			app.loadGeoLocations(fileUri);
			System.out.println("GeoLocations initialized: " + app.getGeoLocationNames());
			if (map != null) {
				initMapMarkers();
			}
		} catch (Exception e) {
			Alert alert = new Alert(AlertType.ERROR);
			alert.setTitle("Exception when loading from " + geolocationsFileText.getText() + ": " + e.getMessage());
			alert.setContentText(e.getMessage());
			alert.show();
		}
	}

	@FXML
	public void handleBrowseGeolocationsFile() {
		FileChooser fileChooser = new FileChooser();
		fileChooser.setTitle("Select geo-locations file");

		File file = fileChooser.showOpenDialog(mapView.getScene().getWindow());
		if (file == null) {
			file = new File(".");
		}
		geolocationsFileText.setText(file.toURI().toString());
		geolocationsFileText.getOnAction().handle(null);
	}
}
